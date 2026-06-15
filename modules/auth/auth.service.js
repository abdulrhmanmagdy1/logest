const jwt = require("jsonwebtoken");
const config = require("../../config/environment");
const { HTTP_STATUS, MESSAGES } = require("../../config/constants");
const AuthRepository = require("./auth.repository");

class AuthService {
  static generateToken(user) {
    return jwt.sign({ id: user._id, role: user.role }, config.JWT_SECRET, {
      expiresIn: config.JWT_EXPIRE,
    });
  }

  static async register(payload) {
    const { name, email, password, phone, role, department } = payload;
    const existingUser = await AuthRepository.findByEmail(email);
    if (existingUser) {
      return {
        ok: false,
        statusCode: HTTP_STATUS.BAD_REQUEST,
        message: MESSAGES.ALREADY_EXISTS,
      };
    }

    const user = await AuthRepository.createUser({
      name,
      email,
      password,
      phone,
      role: role || "client",
      department: department || "logistics",
      isActive: true,
    });

    return {
      ok: true,
      statusCode: HTTP_STATUS.CREATED,
      message: MESSAGES.CREATED,
      token: this.generateToken(user),
      user: {
        id: user._id,
        name: user.name,
        email: user.email,
        role: user.role,
        phone: user.phone,
      },
    };
  }

  static async login(payload) {
    const { email, password } = payload;
    const user = await AuthRepository.findByEmailWithPassword(email);
    if (!user) {
      return { ok: false, statusCode: HTTP_STATUS.UNAUTHORIZED, message: MESSAGES.INVALID_CREDENTIALS };
    }

    if (user.isLocked()) {
      return { ok: false, statusCode: HTTP_STATUS.UNAUTHORIZED, message: MESSAGES.ACCOUNT_LOCKED };
    }

    if (!user.isActive) {
      return { ok: false, statusCode: HTTP_STATUS.UNAUTHORIZED, message: "Account is inactive" };
    }

    const isPasswordValid = await user.comparePassword(password);
    if (!isPasswordValid) {
      await user.incLoginAttempts();
      return { ok: false, statusCode: HTTP_STATUS.UNAUTHORIZED, message: MESSAGES.INVALID_CREDENTIALS };
    }

    if (user.loginAttempts > 0) {
      await user.resetLoginAttempts();
    }

    user.lastLogin = new Date();
    await user.save();

    return {
      ok: true,
      statusCode: HTTP_STATUS.OK,
      message: MESSAGES.LOGGED_IN,
      token: this.generateToken(user),
      user: {
        id: user._id,
        name: user.name,
        email: user.email,
        role: user.role,
        phone: user.phone,
        department: user.department,
        lastLogin: user.lastLogin,
      },
    };
  }
}

module.exports = AuthService;
