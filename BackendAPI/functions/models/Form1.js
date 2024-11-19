const mongoose = require('mongoose');

const form1Schema = new mongoose.Schema({
    firstNameP: {
        type: String,
        required: true, // Field is required
        trim: true      // Trims whitespace from the input
    },
    surnameP: {
        type: String,
        required: true,
        trim: true
    },
    titleP: {
        type: String,
        required: true,
        trim: true
    },
    idP: {
        type: String,
        required: true,
        trim: true
    },
    ageP: {
        type: String,
        required: true,
        trim: true
    },
    addressP: {
        type: String,
        required: true,
        trim: true
    },
    codeP: {
        type: String,
        required: true,
        trim: true
    },
    cellNumberP: {
        type: String,
        required: true,
        trim: true
    },
    workNumberP: {
        type: String,
        required: true,
        trim: true
    },
    homeNumberP: {
        type: String,
        required: true,
        trim: true
    },
    emailP: {
        type: String,
        required: true,
        trim: true
    },
    medicalAidNameP: {
        type: String,
        required: false,
        trim: true
    },
    medicalAidNumberP: {
        type: String,
        required: false,
        trim: true
    },
    firstNameR: {
        type: String,
        required: false,
        trim: true
    },
    surnameR: {
        type: String,
        required: false,
        trim: true
    },
    titleR: {
        type: String,
        required: false,
        trim: true
    },
    idR: {
        type: String,
        required: false,
        trim: true
    },
    ageR: {
        type: String,
        required: false,
        trim: true
    },
    addressR: {
        type: String,
        required: false,
        trim: true
    },
    codeR: {
        type: String,
        required: false,
        trim: true
    },
    cellNumberR: {
        type: String,
        required: false,
        trim: true
    },
    workNumberR: {
        type: String,
        required: false,
        trim: true
    },
    homeNumberR: {
        type: String,
        required: false,
        trim: true
    },
    emailR: {
        type: String,
        required: false,
        trim: true
    },
    firstNameK: {
        type: String,
        required: true,
        trim: true
    },
    addressK: {
        type: String,
        required: true,
        trim: true
    },
    codeK: {
        type: String,
        required: true,
        trim: true
    },
    nameS: {
        type: String,
        required: true,
        trim: true
    },
    typeS: {
        type: String,
        required: true,
        trim: true
    },
    signature: {
        type: String,
        required: true // Ensure the signature is provided
    },
    placeS: {
        type: String,
        required: true,
        trim: true
    },
    date: {
        type: Date,
        required: true // Ensure the date is provided
    },
    form1Filled: { type: Boolean, default: false },
});

// Create a model from the schema
const Form1 = mongoose.model('Form1', form1Schema);

// Export the model
module.exports = Form1;
