const express = require('express');
const router = express.Router();
const { getAllMedicalTests, addMedicalTest, deleteMedicalTest } = require('../controller/medicalTestController');
const { protect } = require('../middleware/authMiddleware');

// Get all medical tests for a patient
router.get('/', protect, getAllMedicalTests);

// Add a new medical test
router.post('/', protect, addMedicalTest);

// Delete a medical test
router.delete('/:id', protect, deleteMedicalTest);

module.exports = router;