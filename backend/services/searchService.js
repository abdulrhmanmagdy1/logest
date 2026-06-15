//
/**
 * ============================================
 * 🔍 Advanced Search Service - خدمة البحث المتقدم
 * ============================================
 */

const { Client } = require('@elastic/elasticsearch');
const logger = require('../utils/logger');

class SearchService {
  constructor() {
    this.client = new Client({
      node: process.env.ELASTICSEARCH_URL || 'http://localhost:9200'
    });
    this.indexPrefix = 'edham';
  }

  /**
   * Index a shipment for search
   */
  async indexShipment(shipment) {
    try {
      await this.client.index({
        index: `${this.indexPrefix}_shipments`,
        id: shipment._id.toString(),
        document: {
          trackingNumber: shipment.trackingNumber,
          cargo: shipment.cargo,
          pickup: shipment.pickup,
          delivery: shipment.delivery,
          status: shipment.status,
          driver: shipment.driver,
          client: shipment.createdBy,
          createdAt: shipment.createdAt,
          updatedAt: shipment.updatedAt
        }
      });
    } catch (error) {
      logger.error('Index shipment error:', error);
    }
  }

  /**
   * Search shipments with filters
   */
  async searchShipments(query, filters = {}, options = {}) {
    try {
      const { page = 1, limit = 20, sort = 'createdAt:desc' } = options;

      const searchQuery = {
        index: `${this.indexPrefix}_shipments`,
        body: {
          from: (page - 1) * limit,
          size: limit,
          sort: [this.parseSort(sort)],
          query: {
            bool: {
              must: [
                {
                  multi_match: {
                    query: query,
                    fields: [
                      'trackingNumber^3',
                      'cargo.description^2',
                      'pickup.address.city',
                      'delivery.address.city',
                      'client.name'
                    ],
                    type: 'best_fields',
                    fuzziness: 'AUTO'
                  }
                }
              ],
              filter: this.buildFilters(filters)
            }
          },
          highlight: {
            fields: {
              trackingNumber: {},
              'cargo.description': {}
            }
          }
        }
      };

      const result = await this.client.search(searchQuery);

      return {
        success: true,
        hits: result.hits.hits.map(hit => ({
          id: hit._id,
          score: hit._score,
          source: hit._source,
          highlights: hit.highlight
        })),
        total: result.hits.total.value,
        page: parseInt(page),
        pages: Math.ceil(result.hits.total.value / limit)
      };
    } catch (error) {
      logger.error('Search shipments error:', error);
      throw error;
    }
  }

  /**
   * Full-text search across all entities
   */
  async globalSearch(query, options = {}) {
    try {
      const indices = [
        `${this.indexPrefix}_shipments`,
        `${this.indexPrefix}_users`,
        `${this.indexPrefix}_drivers`,
        `${this.indexPrefix}_invoices`
      ];

      const result = await this.client.search({
        index: indices.join(','),
        body: {
          query: {
            multi_match: {
              query: query,
              fields: ['*'],
              type: 'cross_fields'
            }
          },
          aggs: {
            by_type: {
              terms: {
                field: '_index'
              }
            }
          }
        }
      });

      return {
        success: true,
        results: result.hits.hits,
        aggregations: result.aggregations
      };
    } catch (error) {
      logger.error('Global search error:', error);
      throw error;
    }
  }

  /**
   * Auto-suggest for search
   */
  async suggest(query, field = 'trackingNumber') {
    try {
      const result = await this.client.search({
        index: `${this.indexPrefix}_shipments`,
        body: {
          suggest: {
            suggestions: {
              prefix: query,
              completion: {
                field: `${field}.suggest`,
                fuzzy: true
              }
            }
          }
        }
      });

      return {
        success: true,
        suggestions: result.suggest.suggestions[0].options.map(opt => opt.text)
      };
    } catch (error) {
      logger.error('Suggest error:', error);
      return { success: true, suggestions: [] };
    }
  }

  /**
   * Analytics - Search trends
   */
  async getSearchAnalytics(startDate, endDate) {
    try {
      const result = await this.client.search({
        index: `${this.indexPrefix}_search_logs`,
        body: {
          query: {
            range: {
              timestamp: {
                gte: startDate,
                lte: endDate
              }
            }
          },
          aggs: {
            popular_queries: {
              terms: {
                field: 'query.keyword',
                size: 10
              }
            },
            search_over_time: {
              date_histogram: {
                field: 'timestamp',
                calendar_interval: 'day'
              }
            }
          }
        }
      });

      return {
        success: true,
        popularQueries: result.aggregations.popular_queries.buckets,
        searchTrends: result.aggregations.search_over_time.buckets
      };
    } catch (error) {
      logger.error('Search analytics error:', error);
      throw error;
    }
  }

  buildFilters(filters) {
    const filterArray = [];

    if (filters.status) {
      filterArray.push({ term: { status: filters.status } });
    }

    if (filters.dateRange) {
      filterArray.push({
        range: {
          createdAt: {
            gte: filters.dateRange.start,
            lte: filters.dateRange.end
          }
        }
      });
    }

    if (filters.city) {
      filterArray.push({
        multi_match: {
          query: filters.city,
          fields: ['pickup.address.city', 'delivery.address.city']
        }
      });
    }

    return filterArray;
  }

  parseSort(sort) {
    const [field, order] = sort.split(':');
    return { [field]: order };
  }

  /**
   * Reindex all data
   */
  async reindexAll() {
    try {
      const Shipment = require('../models/Shipment');
      const shipments = await Shipment.find();

      for (const shipment of shipments) {
        await this.indexShipment(shipment);
      }

      logger.info(`Reindexed ${shipments.length} shipments`);
      return { success: true, count: shipments.length };
    } catch (error) {
      logger.error('Reindex error:', error);
      throw error;
    }
  }
}

module.exports = new SearchService();
