//
/**
 * ============================================
 * 📋 Form Builder Routes - منشئ النماذج
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const { Form, FormSubmission, FormAnalytics } = require('../models/FormBuilder');
const logger = require('../utils/logger');

// Forms
// @route   GET /api/v1/forms
// @desc    Get forms
// @access  Private
router.get('/', protect, async (req, res) => {
  try {
    const { status, category, page = 1, limit = 20 } = req.query;

    let query = { company: req.user.company };
    if (status) query.status = status;
    if (category) query.category = category;

    const forms = await Form.find(query)
      .populate('createdBy', 'firstName lastName')
      .sort({ updatedAt: -1 })
      .limit(limit * 1)
      .skip((page - 1) * limit);

    const count = await Form.countDocuments(query);

    res.json({
      success: true,
      count,
      pagination: {
        page: parseInt(page),
        pages: Math.ceil(count / limit),
        total: count
      },
      data: forms
    });

  } catch (error) {
    logger.error('Get forms error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/forms
// @desc    Create form
// @access  Private
router.post('/', protect, async (req, res) => {
  try {
    const formId = `FORM-${Date.now().toString().slice(-6)}`;

    const form = await Form.create({
      ...req.body,
      formId,
      company: req.user.company,
      createdBy: req.user.id
    });

    res.status(201).json({
      success: true,
      data: form
    });

  } catch (error) {
    logger.error('Create form error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/forms/:id
// @desc    Get form details
// @access  Private
router.get('/:id', protect, async (req, res) => {
  try {
    const form = await Form.findById(req.params.id)
      .populate('createdBy', 'firstName lastName');

    if (!form) {
      return res.status(404).json({ success: false, message: 'Form not found' });
    }

    res.json({
      success: true,
      data: form
    });

  } catch (error) {
    logger.error('Get form error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/forms/:id
// @desc    Update form
// @access  Private
router.put('/:id', protect, async (req, res) => {
  try {
    const form = await Form.findOneAndUpdate(
      { _id: req.params.id, company: req.user.company },
      {
        ...req.body,
        updatedAt: new Date()
      },
      { new: true }
    );

    if (!form) {
      return res.status(404).json({ success: false, message: 'Form not found' });
    }

    res.json({
      success: true,
      data: form
    });

  } catch (error) {
    logger.error('Update form error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/forms/:id/publish
// @desc    Publish form
// @access  Private
router.put('/:id/publish', protect, async (req, res) => {
  try {
    const form = await Form.findOneAndUpdate(
      { _id: req.params.id, company: req.user.company },
      { status: 'published', updatedAt: new Date() },
      { new: true }
    );

    if (!form) {
      return res.status(404).json({ success: false, message: 'Form not found' });
    }

    res.json({
      success: true,
      message: 'Form published',
      data: form
    });

  } catch (error) {
    logger.error('Publish form error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Public form endpoint
// @route   GET /api/v1/forms/public/:formId
// @desc    Get public form
// @access  Public
router.get('/public/:formId', async (req, res) => {
  try {
    const form = await Form.findOne({
      formId: req.params.formId,
      status: 'published',
      type: { $in: ['public', 'restricted'] }
    }).select('-integrations -settings.webhooks');

    if (!form) {
      return res.status(404).json({ success: false, message: 'Form not found' });
    }

    res.json({
      success: true,
      data: form
    });

  } catch (error) {
    logger.error('Get public form error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Submit form
// @route   POST /api/v1/forms/:id/submit
// @desc    Submit form
// @access  Public/Private based on form settings
router.post('/:id/submit', async (req, res) => {
  try {
    const form = await Form.findById(req.params.id);

    if (!form || form.status !== 'published') {
      return res.status(404).json({ success: false, message: 'Form not found or not published' });
    }

    const submissionId = `SUB-${Date.now()}`;

    const submission = await FormSubmission.create({
      submissionId,
      form: form._id,
      company: form.company,
      submittedBy: {
        email: req.body._meta?.email,
        name: req.body._meta?.name,
        ip: req.ip,
        userAgent: req.headers['user-agent']
      },
      data: req.body,
      metadata: {
        submittedAt: new Date(),
        referrer: req.body._meta?.referrer,
        utm: req.body._meta?.utm,
        timeSpent: req.body._meta?.timeSpent
      }
    });

    // Update analytics
    await FormAnalytics.findOneAndUpdate(
      { form: form._id, date: new Date().setHours(0, 0, 0, 0) },
      { $inc: { submissions: 1 } },
      { upsert: true }
    );

    // TODO: Trigger integrations, webhooks, notifications

    res.status(201).json({
      success: true,
      message: 'Form submitted successfully',
      data: {
        submissionId: submission.submissionId,
        redirect: form.settings.redirectAfterSubmit.enabled 
          ? form.settings.redirectAfterSubmit.url 
          : null,
        message: form.settings.redirectAfterSubmit.message
      }
    });

  } catch (error) {
    logger.error('Submit form error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Submissions
// @route   GET /api/v1/forms/:id/submissions
// @desc    Get form submissions
// @access  Private
router.get('/:id/submissions', protect, async (req, res) => {
  try {
    const { status, page = 1, limit = 20 } = req.query;

    let query = { form: req.params.id, company: req.user.company };
    if (status) query.status = status;

    const submissions = await FormSubmission.find(query)
      .sort({ submittedAt: -1 })
      .limit(limit * 1)
      .skip((page - 1) * limit);

    const count = await FormSubmission.countDocuments(query);

    res.json({
      success: true,
      count,
      pagination: {
        page: parseInt(page),
        pages: Math.ceil(count / limit),
        total: count
      },
      data: submissions
    });

  } catch (error) {
    logger.error('Get submissions error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/forms/submissions/:submissionId
// @desc    Get submission details
// @access  Private
router.get('/submissions/:submissionId', protect, async (req, res) => {
  try {
    const submission = await FormSubmission.findOne({
      submissionId: req.params.submissionId,
      company: req.user.company
    })
      .populate('form', 'name fields')
      .populate('submittedBy.user', 'firstName lastName email')
      .populate('review.reviewedBy', 'firstName lastName');

    if (!submission) {
      return res.status(404).json({ success: false, message: 'Submission not found' });
    }

    res.json({
      success: true,
      data: submission
    });

  } catch (error) {
    logger.error('Get submission error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/forms/submissions/:submissionId/review
// @desc    Review submission
// @access  Private
router.put('/submissions/:submissionId/review', protect, async (req, res) => {
  try {
    const { status, notes, rating } = req.body;

    const submission = await FormSubmission.findOneAndUpdate(
      {
        submissionId: req.params.submissionId,
        company: req.user.company
      },
      {
        status,
        review: {
          reviewedBy: req.user.id,
          reviewedAt: new Date(),
          notes,
          rating
        }
      },
      { new: true }
    );

    if (!submission) {
      return res.status(404).json({ success: false, message: 'Submission not found' });
    }

    res.json({
      success: true,
      data: submission
    });

  } catch (error) {
    logger.error('Review submission error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Analytics
// @route   GET /api/v1/forms/:id/analytics
// @desc    Get form analytics
// @access  Private
router.get('/:id/analytics', protect, async (req, res) => {
  try {
    const { from, to } = req.query;

    let query = { form: req.params.id, company: req.user.company };
    if (from && to) {
      query.date = {
        $gte: new Date(from),
        $lte: new Date(to)
      };
    }

    const analytics = await FormAnalytics.find(query).sort({ date: -1 });

    // Aggregate stats
    const stats = analytics.reduce((acc, day) => {
      acc.totalViews += day.views;
      acc.totalStarts += day.starts;
      acc.totalSubmissions += day.submissions;
      acc.totalAbandonment += day.abandonment;
      return acc;
    }, { totalViews: 0, totalStarts: 0, totalSubmissions: 0, totalAbandonment: 0 });

    stats.conversionRate = stats.totalViews > 0 
      ? ((stats.totalSubmissions / stats.totalViews) * 100).toFixed(2)
      : 0;

    res.json({
      success: true,
      data: {
        daily: analytics,
        summary: stats
      }
    });

  } catch (error) {
    logger.error('Get analytics error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Form templates
// @route   GET /api/v1/forms/templates
// @desc    Get form templates
// @access  Private
router.get('/templates/list', protect, async (req, res) => {
  try {
    const templates = [
      {
        id: 'shipment_order',
        name: 'Shipment Order Form',
        category: 'shipment',
        description: 'Standard shipment booking form',
        preview: '/templates/shipment_order.png'
      },
      {
        id: 'delivery_feedback',
        name: 'Delivery Feedback',
        category: 'feedback',
        description: 'Customer satisfaction survey',
        preview: '/templates/feedback.png'
      },
      {
        id: 'customer_survey',
        name: 'Customer Survey',
        category: 'survey',
        description: 'General customer feedback survey',
        preview: '/templates/survey.png'
      },
      {
        id: 'driver_application',
        name: 'Driver Application',
        category: 'application',
        description: 'Driver registration form',
        preview: '/templates/driver_app.png'
      },
      {
        id: 'contact_us',
        name: 'Contact Us',
        category: 'custom',
        description: 'General contact form',
        preview: '/templates/contact.png'
      }
    ];

    res.json({
      success: true,
      data: templates
    });

  } catch (error) {
    logger.error('Get templates error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
