const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');

const UserSchema = new mongoose.Schema({
  username: { type: String, required: true, unique: true },
  password: { type: String, required: true },
  role: { type: String, enum: ['patient', 'staff']},
  // For patients only:
  name: { type: String, required: true },
  surname: { type: String, required: true },
  email: { type: String, required: true },
  phoneNumber: { type: String, required: true },
  medicalAid: { type: String, required: false },
  medicalAidNumber: { type: String, required: false },
});

UserSchema.methods.matchPassword = async function (enteredPassword) {
  return await bcrypt.compare(enteredPassword, this.password);
};

const User = mongoose.model('User', UserSchema);

module.exports = User;
