const express = require('express');
const router = express.Router();

// Import the protect middleware for authentication
const { protect } = require('../middleware/authMiddleware');

// Import the patient profile controller methods
const { savePatientProfile, getPatientProfile, getAllPatientNames } = require('../controller/patientProfileController');

// Route to update patient profile
router.put('/update', protect, savePatientProfile); // Use PUT for updating

// Route to get patient profile
router.get('/patient/:patientID', protect, getPatientProfile); // GET to fetch the profile

// Route to get all patient names
router.get('/patients/names', protect, getAllPatientNames); // GET to fetch all patient names


module.exports = router;
