const mongoose = require('mongoose');

const medicalTestSchema = new mongoose.Schema({
    patientId: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User',
        required: true
    },
    testName: {
        type: String,
        required: true
    },
    testDate: {
        type: Date,
        required: true
    },
    testResults: {
        type: String
    },
    imageUrl: {
        type: String
    },
    notes: {
        type: String
    }
}, {
    timestamps: true
});

module.exports = mongoose.model('MedicalTest', medicalTestSchema);