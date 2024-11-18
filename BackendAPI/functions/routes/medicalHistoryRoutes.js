const express = require('express');
const router = express.Router();
const { protect } = require('../middleware/authMiddleware'); // Ensure your protect middleware is correctly set up
const { saveMedicalHistory, getMedicalHistory } = require('../controller/medicalHistoryController');

// Route to add medical history
router.post('/', protect, saveMedicalHistory); 
// Route to get medical history
router.get('/', protect, getMedicalHistory);

module.exports = router;
