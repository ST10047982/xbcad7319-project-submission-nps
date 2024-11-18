const express = require('express');
const router = express.Router();

// Import the protect middleware for authentication
const { protect } = require('../middleware/authMiddleware');

// Import the patient profile controller methods
const { savePatientProfile, getPatientProfile, getAllPatientNames } = require('../controller/patientProfileController');

// Route to update patient profile
router.put('/update', protect, savePatientProfile); // Use PUT for updating

// Route to get patient profile
router.get('/:patientID', protect, getPatientProfile); // GET to fetch the profile

router.get('/names', getAllPatientNames);

module.exports = router;
