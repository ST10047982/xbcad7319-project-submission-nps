const express = require('express');
const { createForm2 } = require('../controller/formController');
const { createForm1 } = require('../controller/formController');
const router = express.Router();

// Make sure this matches the route you want to hit
router.post('/createForm2', createForm2); // Attach the controller function
router.post('/createForm1', createForm1);

module.exports = router;
