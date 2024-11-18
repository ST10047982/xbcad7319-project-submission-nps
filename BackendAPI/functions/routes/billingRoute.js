const express = require('express');
const router = express.Router();
const { createBilling, getAllBilling, deleteBilling } = require('../controller/billingController');

// Route to create a new billing entry
router.post('/billing', createBilling);

// Route to retrieve all billing entries
router.get('/billing', getAllBilling);

// Route to delete a billing entry by ID
router.delete('/billing/:id', deleteBilling);

module.exports = router;
