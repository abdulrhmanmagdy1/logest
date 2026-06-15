/**
 * ============================================
 * 🤝 CRM Controller - نظام إدهام الاحترافي
 * Edham Logistics - Customer Relationship Management
 * ============================================
 */

const { MESSAGES, HTTP_STATUS } = require('../config/constants');
const logger = require('../utils/logger');
const Client = require('../models/Client');
const Contact = require('../models/Contact');
const Interaction = require('../models/Interaction');
const Opportunity = require('../models/Opportunity');
const Contract = require('../models/Contract');
const Campaign = require('../models/Campaign');
const User = require('../models/User');

class CRMController {
  /**
   * Get CRM dashboard statistics
   */
  static async getDashboardStats(req, res) {
    try {
      const [
        totalClients,
        activeClients,
        newClientsThisMonth,
        totalOpportunities,
        wonOpportunities,
        totalRevenue,
        pipelineValue,
        clientSatisfaction,
        topPerformers
      ] = await Promise.all([
        Client.countDocuments({ isActive: true }),
        Client.countDocuments({ isActive: true, status: 'active' }),
        Client.countDocuments({ 
          isActive: true, 
          createdAt: { $gte: new Date(new Date().setDate(1)) }
        }),
        Opportunity.countDocuments(),
        Opportunity.countDocuments({ status: 'won' }),
        Opportunity.aggregate([
          { $match: { status: 'won' } },
          { $group: { _id: null, total: { $sum: '$value' } } }
        ]),
        Opportunity.aggregate([
          { $match: { status: { $in: ['open', 'negotiation'] } } },
          { $group: { _id: null, total: { $sum: '$value' } } }
        ]),
        Interaction.aggregate([
          { $match: { type: 'survey' } },
          { $group: { _id: null, avgRating: { $avg: '$rating' } } }
        ]),
        Client.aggregate([
          { $match: { isActive: true } },
          {
            $lookup: {
              from: 'opportunities',
              localField: '_id',
              foreignField: 'clientId',
              as: 'opportunities'
            }
          },
          {
            $addFields: {
              totalValue: { $sum: '$opportunities.value' },
              wonCount: {
                $size: {
                  $filter: {
                    input: '$opportunities',
                    cond: { $eq: ['$$this.status', 'won'] }
                  }
                }
              }
            }
          },
          { $sort: { totalValue: -1 } },
          { $limit: 10 }
        ])
      ]);

      res.json({
        success: true,
        statistics: {
          clients: {
            total: totalClients,
            active: activeClients,
            newThisMonth: newClientsThisMonth,
            growthRate: activeClients > 0 ? ((newClientsThisMonth / activeClients) * 100).toFixed(1) : 0
          },
          opportunities: {
            total: totalOpportunities,
            won: wonOpportunities,
            winRate: totalOpportunities > 0 ? ((wonOpportunities / totalOpportunities) * 100).toFixed(1) : 0,
            totalRevenue: totalRevenue[0]?.total || 0,
            pipelineValue: pipelineValue[0]?.total || 0
          },
          satisfaction: {
            averageRating: clientSatisfaction[0]?.avgRating || 0,
            totalSurveys: await Interaction.countDocuments({ type: 'survey' })
          },
          topClients: topPerformers
        }
      });
    } catch (error) {
      logger.error('Error getting CRM dashboard stats:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Get all clients with filtering and pagination
   */
  static async getClients(req, res) {
    try {
      const {
        page = 1,
        limit = 20,
        search,
        status,
        industry,
        size,
        sortBy = 'createdAt',
        sortOrder = 'desc'
      } = req.query;

      const query = { isActive: true };
      
      if (search) {
        query.$or = [
          { name: { $regex: search, $options: 'i' } },
          { email: { $regex: search, $options: 'i' } },
          { phone: { $regex: search, $options: 'i' } },
          { 'company.name': { $regex: search, $options: 'i' } }
        ];
      }
      
      if (status) query.status = status;
      if (industry) query['company.industry'] = industry;
      if (size) query['company.size'] = size;

      const skip = (page - 1) * limit;
      const sort = { [sortBy]: sortOrder === 'desc' ? -1 : 1 };

      const [clients, total] = await Promise.all([
        Client.find(query)
          .populate('primaryContactId', 'name email phone')
          .populate('assignedUserId', 'name email')
          .sort(sort)
          .skip(skip)
          .limit(parseInt(limit)),
        Client.countDocuments(query)
      ]);

      res.json({
        success: true,
        clients,
        pagination: {
          page: parseInt(page),
          limit: parseInt(limit),
          total,
          pages: Math.ceil(total / limit)
        }
      });
    } catch (error) {
      logger.error('Error getting clients:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Create new client
   */
  static async createClient(req, res) {
    try {
      const clientData = {
        ...req.body,
        createdBy: req.user.id,
        status: 'prospect'
      };

      // Check for duplicate email/phone
      const existingClient = await Client.findOne({
        $or: [
          { email: clientData.email },
          { phone: clientData.phone }
        ]
      });

      if (existingClient) {
        return res.status(HTTP_STATUS.CONFLICT).json({
          success: false,
          message: 'البريد الإلكتروني أو رقم الهاتف مسجل بالفعل'
        });
      }

      const client = new Client(clientData);
      await client.save();

      // Create initial interaction
      const interaction = new Interaction({
        clientId: client._id,
        type: 'contact',
        direction: 'inbound',
        subject: 'تسجيل عميل جديد',
        description: 'تم تسجيل العميل في النظام',
        userId: req.user.id,
        metadata: {
          source: 'crm_registration',
          clientId: client._id
        }
      });
      await interaction.save();

      logger.success('New client created', { clientId: client._id, name: client.name });

      res.status(HTTP_STATUS.CREATED).json({
        success: true,
        message: 'تم إنشاء العميل بنجاح',
        client: await client.populate('primaryContactId assignedUserId')
      });
    } catch (error) {
      logger.error('Error creating client:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Update client
   */
  static async updateClient(req, res) {
    try {
      const { id } = req.params;
      const updateData = {
        ...req.body,
        updatedBy: req.user.id,
        updatedAt: new Date()
      };

      const client = await Client.findByIdAndUpdate(
        id,
        updateData,
        { new: true, runValidators: true }
      ).populate('primaryContactId assignedUserId');

      if (!client) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: 'العميل غير موجود'
        });
      }

      // Create update interaction
      const interaction = new Interaction({
        clientId: client._id,
        type: 'update',
        subject: 'تحديث بيانات العميل',
        description: 'تم تحديث معلومات العميل',
        userId: req.user.id,
        metadata: {
          updatedFields: Object.keys(req.body)
        }
      });
      await interaction.save();

      logger.info('Client updated', { clientId: client._id });

      res.json({
        success: true,
        message: 'تم تحديث العميل بنجاح',
        client
      });
    } catch (error) {
      logger.error('Error updating client:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Get client details with full history
   */
  static async getClientDetails(req, res) {
    try {
      const { id } = req.params;

      const [client, contacts, interactions, opportunities, contracts] = await Promise.all([
        Client.findById(id)
          .populate('primaryContactId')
          .populate('assignedUserId'),
        Contact.find({ clientId: id }),
        Interaction.find({ clientId: id })
          .populate('userId', 'name email')
          .sort({ createdAt: -1 })
          .limit(50),
        Opportunity.find({ clientId: id })
          .sort({ createdAt: -1 }),
        Contract.find({ clientId: id })
          .sort({ createdAt: -1 })
      ]);

      if (!client) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: 'العميل غير موجود'
        });
      }

      // Calculate client metrics
      const metrics = {
        totalInteractions: interactions.length,
        totalOpportunities: opportunities.length,
        wonOpportunities: opportunities.filter(o => o.status === 'won').length,
        totalValue: opportunities.reduce((sum, o) => sum + (o.status === 'won' ? o.value : 0), 0),
        averageResponseTime: await this.calculateAverageResponseTime(id),
        satisfactionScore: await this.calculateClientSatisfaction(id)
      };

      res.json({
        success: true,
        client: {
          ...client.toObject(),
          contacts,
          interactions,
          opportunities,
          contracts,
          metrics
        }
      });
    } catch (error) {
      logger.error('Error getting client details:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Create opportunity
   */
  static async createOpportunity(req, res) {
    try {
      const opportunityData = {
        ...req.body,
        createdBy: req.user.id,
        status: 'open'
      };

      const opportunity = new Opportunity(opportunityData);
      await opportunity.save();

      // Create opportunity interaction
      const interaction = new Interaction({
        clientId: opportunity.clientId,
        type: 'opportunity',
        subject: 'فرصة عمل جديدة',
        description: `فرصة عمل: ${opportunity.name}`,
        value: opportunity.value,
        userId: req.user.id,
        metadata: {
          opportunityId: opportunity._id
        }
      });
      await interaction.save();

      logger.success('New opportunity created', { 
        opportunityId: opportunity._id,
        clientId: opportunity.clientId 
      });

      res.status(HTTP_STATUS.CREATED).json({
        success: true,
        message: 'تم إنشاء الفرصة بنجاح',
        opportunity
      });
    } catch (error) {
      logger.error('Error creating opportunity:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Get opportunities with pipeline view
   */
  static async getOpportunities(req, res) {
    try {
      const {
        stage,
        clientId,
        assignedUserId,
        valueRange,
        closingDate,
        page = 1,
        limit = 20
      } = req.query;

      const query = {};
      if (stage) query.stage = stage;
      if (clientId) query.clientId = clientId;
      if (assignedUserId) query.assignedUserId = assignedUserId;
      
      if (valueRange) {
        const [min, max] = valueRange.split('-').map(Number);
        query.value = { $gte: min, $lte: max };
      }
      
      if (closingDate) {
        const [start, end] = closingDate.split(',');
        query.closingDate = {};
        if (start) query.closingDate.$gte = new Date(start);
        if (end) query.closingDate.$lte = new Date(end);
      }

      const skip = (page - 1) * limit;

      const [opportunities, total] = await Promise.all([
        Opportunity.find(query)
          .populate('clientId', 'name company.name')
          .populate('assignedUserId', 'name email')
          .sort({ closingDate: 1 })
          .skip(skip)
          .limit(parseInt(limit)),
        Opportunity.countDocuments(query)
      ]);

      // Pipeline statistics
      const pipelineStats = await Opportunity.aggregate([
        {
          $group: {
            _id: '$stage',
            count: { $sum: 1 },
            totalValue: { $sum: '$value' },
            averageValue: { $avg: '$value' }
          }
        }
      ]);

      res.json({
        success: true,
        opportunities,
        pipelineStats,
        pagination: {
          page: parseInt(page),
          limit: parseInt(limit),
          total,
          pages: Math.ceil(total / limit)
        }
      });
    } catch (error) {
      logger.error('Error getting opportunities:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Create marketing campaign
   */
  static async createCampaign(req, res) {
    try {
      const campaignData = {
        ...req.body,
        createdBy: req.user.id,
        status: 'draft'
      };

      const campaign = new Campaign(campaignData);
      await campaign.save();

      logger.success('New campaign created', { campaignId: campaign._id });

      res.status(HTTP_STATUS.CREATED).json({
        success: true,
        message: 'تم إنشاء الحملة بنجاح',
        campaign
      });
    } catch (error) {
      logger.error('Error creating campaign:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Get campaigns with performance metrics
   */
  static async getCampaigns(req, res) {
    try {
      const { status, type, page = 1, limit = 20 } = req.query;

      const query = {};
      if (status) query.status = status;
      if (type) query.type = type;

      const skip = (page - 1) * limit;

      const [campaigns, total] = await Promise.all([
        Campaign.find(query)
          .populate('createdBy', 'name email')
          .sort({ createdAt: -1 })
          .skip(skip)
          .limit(parseInt(limit)),
        Campaign.countDocuments(query)
      ]);

      // Calculate performance metrics for each campaign
      const campaignsWithMetrics = await Promise.all(
        campaigns.map(async (campaign) => {
          const metrics = await this.calculateCampaignMetrics(campaign._id);
          return { ...campaign.toObject(), metrics };
        })
      );

      res.json({
        success: true,
        campaigns: campaignsWithMetrics,
        pagination: {
          page: parseInt(page),
          limit: parseInt(limit),
          total,
          pages: Math.ceil(total / limit)
        }
      });
    } catch (error) {
      logger.error('Error getting campaigns:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Record interaction
   */
  static async recordInteraction(req, res) {
    try {
      const interactionData = {
        ...req.body,
        userId: req.user.id
      };

      const interaction = new Interaction(interactionData);
      await interaction.save();

      // Update client last interaction
      await Client.findByIdAndUpdate(
        interactionData.clientId,
        { 
          lastInteractionAt: new Date(),
          lastInteractionType: interactionData.type
        }
      );

      logger.success('Interaction recorded', { 
        interactionId: interaction._id,
        clientId: interactionData.clientId 
      });

      res.status(HTTP_STATUS.CREATED).json({
        success: true,
        message: 'تم تسجيل التفاعل بنجاح',
        interaction
      });
    } catch (error) {
      logger.error('Error recording interaction:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Get client communication history
   */
  static async getCommunicationHistory(req, res) {
    try {
      const { clientId } = req.params;
      const { 
        type, 
        page = 1, 
        limit = 50,
        startDate,
        endDate 
      } = req.query;

      const query = { clientId };
      if (type) query.type = type;
      
      if (startDate || endDate) {
        query.createdAt = {};
        if (startDate) query.createdAt.$gte = new Date(startDate);
        if (endDate) query.createdAt.$lte = new Date(endDate);
      }

      const skip = (page - 1) * limit;

      const [interactions, total] = await Promise.all([
        Interaction.find(query)
          .populate('userId', 'name email')
          .sort({ createdAt: -1 })
          .skip(skip)
          .limit(parseInt(limit)),
        Interaction.countDocuments(query)
      ]);

      res.json({
        success: true,
        interactions,
        pagination: {
          page: parseInt(page),
          limit: parseInt(limit),
          total,
          pages: Math.ceil(total / limit)
        }
      });
    } catch (error) {
      logger.error('Error getting communication history:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Generate client reports
   */
  static async generateClientReports(req, res) {
    try {
      const { 
        reportType,
        clientId,
        startDate,
        endDate,
        format = 'json'
      } = req.query;

      let reportData;

      switch (reportType) {
        case 'client_summary':
          reportData = await this.generateClientSummaryReport(clientId, startDate, endDate);
          break;
        case 'sales_pipeline':
          reportData = await this.generateSalesPipelineReport(startDate, endDate);
          break;
        case 'campaign_performance':
          reportData = await this.generateCampaignPerformanceReport(startDate, endDate);
          break;
        case 'interaction_analytics':
          reportData = await this.generateInteractionAnalyticsReport(startDate, endDate);
          break;
        default:
          return res.status(HTTP_STATUS.BAD_REQUEST).json({
            success: false,
            message: 'نوع التقرير غير مدعوم'
          });
      }

      if (format === 'excel') {
        // Generate Excel file
        const workbook = await this.generateExcelReport(reportData, reportType);
        res.setHeader('Content-Type', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet');
        res.setHeader('Content-Disposition', `attachment; filename="${reportType}_${Date.now()}.xlsx"`);
        await workbook.xlsx.write(res);
      } else {
        res.json({
          success: true,
          reportData,
          generatedAt: new Date(),
          reportType
        });
      }
    } catch (error) {
      logger.error('Error generating client report:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Helper: Calculate average response time
   */
  static async calculateAverageResponseTime(clientId) {
    try {
      const interactions = await Interaction.find({
        clientId,
        direction: 'inbound'
      }).sort({ createdAt: 1 });

      if (interactions.length === 0) return 0;

      let totalResponseTime = 0;
      let responseCount = 0;

      for (let i = 0; i < interactions.length; i++) {
        const inboundInteraction = interactions[i];
        
        // Find next outbound interaction
        const outboundInteraction = await Interaction.findOne({
          clientId,
          direction: 'outbound',
          createdAt: { $gt: inboundInteraction.createdAt }
        }).sort({ createdAt: 1 });

        if (outboundInteraction) {
          const responseTime = outboundInteraction.createdAt - inboundInteraction.createdAt;
          totalResponseTime += responseTime;
          responseCount++;
        }
      }

      return responseCount > 0 ? totalResponseTime / responseCount : 0;
    } catch (error) {
      logger.error('Error calculating average response time:', error);
      return 0;
    }
  }

  /**
   * Helper: Calculate client satisfaction score
   */
  static async calculateClientSatisfaction(clientId) {
    try {
      const surveyInteractions = await Interaction.find({
        clientId,
        type: 'survey',
        rating: { $exists: true }
      });

      if (surveyInteractions.length === 0) return 0;

      const totalRating = surveyInteractions.reduce((sum, interaction) => sum + interaction.rating, 0);
      return totalRating / surveyInteractions.length;
    } catch (error) {
      logger.error('Error calculating client satisfaction:', error);
      return 0;
    }
  }

  /**
   * Helper: Calculate campaign metrics
   */
  static async calculateCampaignMetrics(campaignId) {
    try {
      const campaign = await Campaign.findById(campaignId);
      if (!campaign) return {};

      const sentCount = campaign.metrics?.sent || 0;
      const deliveredCount = campaign.metrics?.delivered || 0;
      const openedCount = campaign.metrics?.opened || 0;
      const clickedCount = campaign.metrics?.clicked || 0;
      const convertedCount = campaign.metrics?.converted || 0;

      return {
        sent: sentCount,
        delivered: deliveredCount,
        opened: openedCount,
        clicked: clickedCount,
        converted: convertedCount,
        deliveryRate: sentCount > 0 ? (deliveredCount / sentCount * 100).toFixed(2) : 0,
        openRate: deliveredCount > 0 ? (openedCount / deliveredCount * 100).toFixed(2) : 0,
        clickRate: openedCount > 0 ? (clickedCount / openedCount * 100).toFixed(2) : 0,
        conversionRate: clickedCount > 0 ? (convertedCount / clickedCount * 100).toFixed(2) : 0,
        roi: campaign.budget && convertedCount > 0 ? 
          ((campaign.metrics.revenue - campaign.budget) / campaign.budget * 100).toFixed(2) : 0
      };
    } catch (error) {
      logger.error('Error calculating campaign metrics:', error);
      return {};
    }
  }

  /**
   * Helper: Generate client summary report
   */
  static async generateClientSummaryReport(clientId, startDate, endDate) {
    try {
      const client = await Client.findById(clientId)
        .populate('primaryContactId assignedUserId');

      const [opportunities, interactions] = await Promise.all([
        Opportunity.find({ clientId }),
        Interaction.find({ clientId })
      ]);

      return {
        client,
        opportunities: {
          total: opportunities.length,
          won: opportunities.filter(o => o.status === 'won').length,
          totalValue: opportunities.reduce((sum, o) => sum + o.value, 0),
          wonValue: opportunities.filter(o => o.status === 'won').reduce((sum, o) => sum + o.value, 0)
        },
        interactions: {
          total: interactions.length,
          byType: this.groupBy(interactions, 'type'),
          lastInteraction: interactions[0]?.createdAt
        }
      };
    } catch (error) {
      logger.error('Error generating client summary report:', error);
      return {};
    }
  }

  /**
   * Helper: Group array by key
   */
  static groupBy(array, key) {
    return array.reduce((groups, item) => {
      const group = item[key];
      groups[group] = groups[group] || [];
      groups[group].push(item);
      return groups;
    }, {});
  }
}

module.exports = CRMController;
