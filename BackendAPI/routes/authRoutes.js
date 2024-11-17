const express = require('express');
const { register, login, forgetPassword, logout, updateProfile, viewPatientProfile } = require('../controller/authController');
const router = express.Router();
const { protect } = require('../middleware/authMiddleware'); // Assuming you have a middleware to protect routes

router.post('/register', register);
router.post('/login', login);
router.post('/logout', logout);
router.post('/forget-password', forgetPassword);
router.put('/update-profile/:userId', protect, updateProfile);
// Route to view patient profile
router.get('/view-patient-profile/:patientId', protect, viewPatientProfile);


module.exports = router;
