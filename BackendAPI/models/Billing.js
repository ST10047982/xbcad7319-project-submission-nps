const mongoose = require('mongoose');

// Define the Billing schema
const BillingSchema = new mongoose.Schema({
    patientName: {
        type: String,
        required: true,
        trim: true
    },
    cost: {
        type: Number,
        required: true,
        validate: {
            validator: (value) => value >= 0,
            message: 'Cost must be a positive number'
        }
    },
    date: {
        type: Date,
        default: Date.now
    }
});

// Create the Billing model
const Billing = mongoose.model('Billing', BillingSchema);

module.exports = Billing;
