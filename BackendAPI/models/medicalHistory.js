const mongoose = require('mongoose');

const medicalHistorySchema = mongoose.Schema({
    user: {
        type: mongoose.Schema.Types.ObjectId,
        required: true,
        ref: 'User', // Reference to the User model
    },
    allergies: {
        type: String,
        required: false,
    },
    injuries: {
        type: String,
        required: false,
    },
    procedures: {
        type: String,
        required: false,
    },
    medications: {
        type: String,
        required: false,
    },
    familyHistory: {
        type: String,
        required: false,
    },
});

module.exports = mongoose.model('MedicalHistory', medicalHistorySchema);
