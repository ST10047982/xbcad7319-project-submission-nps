const express = require('express');
const router = express.Router();

// Import the protect middleware for authentication
const { protect } = require('../middleware/authMiddleware');

// Import the patient profile controller methods
const { savePatientProfile, getPatientProfile, getPatientProfileById, getAllPatientNames, getUserIdByNameAndSurname} = require('../controller/patientProfileController');

// Route to update patient profile
router.put('/update', protect, savePatientProfile); // Use PUT for updating

// Route to get patient profile (using logged-in user's ID)
router.get('/patient/profile', protect, getPatientProfile); // GET to fetch the profile

// Example route for fetching patient profile by ID
router.get('/patient/:id', getPatientProfileById);

// Route to get all patient names
router.get('/patients/names', protect, getAllPatientNames); // GET to fetch all patient names

router.get('/patients/namestoID', getUserIdByNameAndSurname);


module.exports = router;
