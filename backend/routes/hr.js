//
/**
 * ============================================
 * 👔 HR Routes - الموارد البشرية
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const { Employee, Payroll, LeaveRequest } = require('../models/HR');
const logger = require('../utils/logger');

// @route   GET /api/v1/hr/employees
// @desc    Get all employees
// @access  Private (HR, Admin)
router.get('/employees', protect, authorize(['hr', 'admin']), async (req, res) => {
  try {
    const { department, status, employmentType } = req.query;
    
    let query = {};
    if (department) query.department = department;
    if (status) query.status = status;
    if (employmentType) query.employmentType = employmentType;

    const employees = await Employee.find(query)
      .populate('user', 'firstName lastName email phone avatar')
      .populate('reportingTo', 'employeeId position')
      .sort({ createdAt: -1 });

    res.json({
      success: true,
      count: employees.length,
      data: employees
    });

  } catch (error) {
    logger.error('Get employees error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/hr/employees
// @desc    Create employee
// @access  Private (HR, Admin)
router.post('/employees', protect, authorize(['hr', 'admin']), async (req, res) => {
  try {
    const employee = await Employee.create(req.body);

    res.status(201).json({
      success: true,
      data: employee
    });

  } catch (error) {
    logger.error('Create employee error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/hr/employees/:id
// @desc    Get employee details
// @access  Private
router.get('/employees/:id', protect, async (req, res) => {
  try {
    const employee = await Employee.findById(req.params.id)
      .populate('user', '-password')
      .populate('reportingTo', 'employeeId position user')
      .populate('team.members.user', 'firstName lastName');

    if (!employee) {
      return res.status(404).json({ success: false, message: 'Employee not found' });
    }

    res.json({
      success: true,
      data: employee
    });

  } catch (error) {
    logger.error('Get employee error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/hr/attendance
// @desc    Record attendance
// @access  Private
router.post('/attendance', protect, async (req, res) => {
  try {
    const { employeeId, type } = req.body;
    const now = new Date();

    const employee = await Employee.findById(employeeId);
    if (!employee) {
      return res.status(404).json({ success: false, message: 'Employee not found' });
    }

    const today = now.toISOString().split('T')[0];
    const existingEntry = employee.attendance.find(a => 
      a.date.toISOString().split('T')[0] === today
    );

    if (type === 'checkIn') {
      if (existingEntry) {
        return res.status(400).json({ success: false, message: 'Already checked in today' });
      }

      employee.attendance.push({
        date: now,
        checkIn: now,
        status: now.getHours() > 9 ? 'late' : 'present'
      });
    } else if (type === 'checkOut') {
      if (!existingEntry) {
        return res.status(400).json({ success: false, message: 'Not checked in today' });
      }

      existingEntry.checkOut = now;
      
      // Calculate hours
      const hours = (now - existingEntry.checkIn) / (1000 * 60 * 60);
      if (hours > 9) {
        existingEntry.overtime = hours - 9;
      }
    }

    await employee.save();

    res.json({
      success: true,
      message: `${type === 'checkIn' ? 'Check-in' : 'Check-out'} recorded`,
      data: employee.attendance[employee.attendance.length - 1]
    });

  } catch (error) {
    logger.error('Attendance error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/hr/leave-requests
// @desc    Submit leave request
// @access  Private
router.post('/leave-requests', protect, async (req, res) => {
  try {
    const employee = await Employee.findOne({ user: req.user.id });
    if (!employee) {
      return res.status(404).json({ success: false, message: 'Employee record not found' });
    }

    const { type, startDate, endDate, days, reason } = req.body;

    // Check leave balance
    const balance = employee.leaveBalance[type];
    const used = employee.leaveBalance.used[type];
    
    if (balance - used < days) {
      return res.status(400).json({
        success: false,
        message: `Insufficient leave balance. Available: ${balance - used}, Requested: ${days}`
      });
    }

    const leaveRequest = await LeaveRequest.create({
      employee: employee._id,
      type,
      startDate,
      endDate,
      days,
      reason,
      status: 'pending'
    });

    res.status(201).json({
      success: true,
      message: 'Leave request submitted',
      data: leaveRequest
    });

  } catch (error) {
    logger.error('Leave request error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/hr/leave-requests
// @desc    Get leave requests (for approval)
// @access  Private (HR, Manager)
router.get('/leave-requests', protect, authorize(['hr', 'admin', 'supervisor']), async (req, res) => {
  try {
    const { status, employeeId } = req.query;
    
    let query = {};
    if (status) query.status = status;
    if (employeeId) query.employee = employeeId;

    const requests = await LeaveRequest.find(query)
      .populate({
        path: 'employee',
        populate: { path: 'user', select: 'firstName lastName email' }
      })
      .sort({ createdAt: -1 });

    res.json({
      success: true,
      count: requests.length,
      data: requests
    });

  } catch (error) {
    logger.error('Get leave requests error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/hr/leave-requests/:id/approve
// @desc    Approve/Reject leave request
// @access  Private (HR, Manager)
router.put('/leave-requests/:id/approve', protect, authorize(['hr', 'admin', 'supervisor']), async (req, res) => {
  try {
    const { status, rejectionReason } = req.body;

    const leaveRequest = await LeaveRequest.findByIdAndUpdate(
      req.params.id,
      {
        status,
        approvedBy: req.user.id,
        approvedAt: new Date(),
        rejectionReason
      },
      { new: true }
    );

    if (!leaveRequest) {
      return res.status(404).json({ success: false, message: 'Leave request not found' });
    }

    // Update employee leave balance if approved
    if (status === 'approved') {
      await Employee.findByIdAndUpdate(
        leaveRequest.employee,
        { $inc: { [`leaveBalance.used.${leaveRequest.type}`]: leaveRequest.days } }
      );
    }

    res.json({
      success: true,
      message: `Leave request ${status}`,
      data: leaveRequest
    });

  } catch (error) {
    logger.error('Approve leave request error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/hr/payroll
// @desc    Get payroll records
// @access  Private (HR, Admin, Accountant)
router.get('/payroll', protect, authorize(['hr', 'admin', 'accountant']), async (req, res) => {
  try {
    const { month, year, employee, status } = req.query;
    
    let query = {};
    if (month && year) {
      query['period.month'] = parseInt(month);
      query['period.year'] = parseInt(year);
    }
    if (employee) query.employee = employee;
    if (status) query.status = status;

    const payrolls = await Payroll.find(query)
      .populate({
        path: 'employee',
        populate: { path: 'user', select: 'firstName lastName email' }
      })
      .sort({ 'period.year': -1, 'period.month': -1 });

    res.json({
      success: true,
      count: payrolls.length,
      data: payrolls
    });

  } catch (error) {
    logger.error('Get payroll error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/hr/payroll/calculate
// @desc    Calculate payroll
// @access  Private (HR, Admin)
router.post('/payroll/calculate', protect, authorize(['hr', 'admin']), async (req, res) => {
  try {
    const { month, year } = req.body;

    const employees = await Employee.find({ status: 'active' });
    const calculated = [];

    for (const emp of employees) {
      // Get attendance for the month
      const startOfMonth = new Date(year, month - 1, 1);
      const endOfMonth = new Date(year, month, 0);

      const attendance = emp.attendance.filter(a => 
        a.date >= startOfMonth && a.date <= endOfMonth
      );

      const presentDays = attendance.filter(a => a.status === 'present').length;
      const absentDays = attendance.filter(a => a.status === 'absent').length;
      const overtimeHours = attendance.reduce((sum, a) => sum + (a.overtime || 0), 0);

      // Calculate salary components
      const dailyRate = emp.salary.basic / 30;
      const basicPay = dailyRate * presentDays;
      const overtimePay = overtimeHours * (dailyRate / 8 * 1.5);
      
      // Saudi GOSI calculation (9% for Saudi employees)
      const gosi = basicPay * 0.09;

      const totalEarnings = basicPay + emp.salary.housing + emp.salary.transportation + overtimePay;
      const totalDeductions = gosi;
      const netSalary = totalEarnings - totalDeductions;

      const payroll = await Payroll.create({
        employee: emp._id,
        period: { month, year },
        earnings: {
          basic: basicPay,
          housing: emp.salary.housing,
          transportation: emp.salary.transportation,
          overtime: overtimePay,
          totalEarnings
        },
        deductions: {
          gosi,
          totalDeductions
        },
        netSalary,
        attendance: {
          workingDays: 30,
          presentDays,
          absentDays,
          overtimeHours
        },
        status: 'calculated',
        createdBy: req.user.id
      });

      calculated.push(payroll);
    }

    res.json({
      success: true,
      message: `Calculated payroll for ${calculated.length} employees`,
      data: calculated
    });

  } catch (error) {
    logger.error('Calculate payroll error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/hr/dashboard
// @desc    HR dashboard
// @access  Private (HR, Admin)
router.get('/dashboard', protect, authorize(['hr', 'admin']), async (req, res) => {
  try {
    const today = new Date();
    const startOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);

    const stats = await Promise.all([
      Employee.countDocuments({ status: 'active' }),
      Employee.countDocuments({ status: 'on_leave' }),
      LeaveRequest.countDocuments({ status: 'pending' }),
      Employee.aggregate([
        { $match: { department: { $exists: true } } },
        { $group: { _id: '$department', count: { $sum: 1 } } }
      ]),
      Employee.aggregate([
        { $match: { 'attendance.date': { $gte: startOfMonth } } },
        { $unwind: '$attendance' },
        { $match: { 'attendance.status': 'present' } },
        { $count: 'presentCount' }
      ])
    ]);

    res.json({
      success: true,
      data: {
        activeEmployees: stats[0],
        onLeave: stats[1],
        pendingLeaveRequests: stats[2],
        byDepartment: stats[3],
        presentThisMonth: stats[4][0]?.presentCount || 0
      }
    });

  } catch (error) {
    logger.error('HR dashboard error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
