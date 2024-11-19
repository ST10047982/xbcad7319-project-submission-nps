const mongoose = require('mongoose');

const form2Schema = new mongoose.Schema({
    name: {
        type: String,
        required: true, // Field is required
        trim: true      // Trims whitespace from the input
    },
    areasConcernedForNeedling: {
        type: String,
        required: true,
        trim: true
    },
    date: {
        type: Date,
        required: true // Ensure the date is provided
    },
    signature: {
        type: String,
        required: true, // Ensure the signature is provided
    },
    form2Filled: { type: Boolean, default: false },
});


// Create a model from the schema
const Form2 = mongoose.model('Form2', form2Schema);

// Export the model
module.exports = Form2;