const MedicalTest = require('../models/medicalTest');

// Get all medical tests for a patient
const getAllMedicalTests = async (req, res) => {
    try {
        const tests = await MedicalTest.find({ patientId: req.user.id }).sort({ testDate: -1 });
        res.json(tests);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
};

// Add a new medical test
const addMedicalTest = async (req, res) => {
    try {
        const { testName, testDate, testResults, imageUrl, notes } = req.body;

        if (!testResults) {
            return res.status(400).json({ message: 'Test results are required' });
        }

        const newTest = new MedicalTest({
            patientId: req.user.id,
            testName,
            testDate,
            testResults,
            imageUrl,
            notes
        });

        const savedTest = await newTest.save();
        res.status(201).json({ message: 'Medical test saved successfully', test: savedTest });
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
};

// Delete a medical test
const deleteMedicalTest = async (req, res) => {
    try {
        const test = await MedicalTest.findOne({
            _id: req.params.id,
            patientId: req.user.id
        });

        if (!test) {
            return res.status(404).json({ message: 'Medical test not found' });
        }

        await test.remove();
        res.json({ message: 'Medical test deleted successfully' });
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
};

module.exports = {
    getAllMedicalTests,
    addMedicalTest,
    deleteMedicalTest
};