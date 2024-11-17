const MedicalHistory = require('../models/medicalHistory');

// Add or Update Medical History
const saveMedicalHistory = async (req, res) => {
    const { allergies, injuries, procedures, medications, familyHistory } = req.body;

    try {
        // Find if the user already has a medical history
        let medicalHistory = await MedicalHistory.findOne({ user: req.user._id });

        if (medicalHistory) {
            // If it exists, update it
            medicalHistory.allergies = allergies;
            medicalHistory.injuries = injuries;
            medicalHistory.procedures = procedures;
            medicalHistory.medications = medications;
            medicalHistory.familyHistory = familyHistory;

            await medicalHistory.save();
            res.status(200).json({ message: 'Medical history updated successfully', medicalHistory });
        } else {
            // If not, create a new entry
            medicalHistory = new MedicalHistory({
                user: req.user._id,
                allergies,
                injuries,
                procedures,
                medications,
                familyHistory
            });

            await medicalHistory.save();
            res.status(201).json({ message: 'Medical history saved successfully', medicalHistory });
        }
    } catch (error) {
        console.error('Error saving medical history:', error);
        res.status(500).json({ message: 'Server error' });
    }
};

// Get Medical History
const getMedicalHistory = async (req, res) => {
    try {
        const medicalHistory = await MedicalHistory.findOne({ user: req.user._id });

        if (!medicalHistory) {
            return res.status(404).json({ message: 'No medical history found for this user' });
        }

        res.status(200).json(medicalHistory);
    } catch (error) {
        console.error('Error fetching medical history:', error);
        res.status(500).json({ message: 'Server error' });
    }
};

module.exports = {
    saveMedicalHistory,
    getMedicalHistory,
};
