const mongoose = require('mongoose');

const AppointmentSchema = new mongoose.Schema({
    patient: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
    date: { type: Date, required: true },
    time: { type: String, required: true },
    description: { type: String, required: false },
    status: { 
        type: String, 
        enum: ['pending', 'approved', 'canceled', 'rescheduled'],
        default: 'pending' 
    },
    notes: { type: String },
});

const Appointment = mongoose.model('Appointment', AppointmentSchema);

module.exports = Appointment;

