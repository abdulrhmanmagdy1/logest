const User = require("../../models/User");

class AuthRepository {
  static findByEmail(email) {
    return User.findOne({ email });
  }

  static findByEmailWithPassword(email) {
    return User.findOne({ email }).select("+password");
  }

  static createUser(payload) {
    return User.create(payload);
  }

  static findById(id) {
    return User.findById(id).select("-password");
  }

  static findByIdWithPassword(id) {
    return User.findById(id).select("+password");
  }

  static updateProfileById(id, payload) {
    return User.findByIdAndUpdate(id, payload, { new: true, runValidators: true }).select("-password");
  }
}

module.exports = AuthRepository;
