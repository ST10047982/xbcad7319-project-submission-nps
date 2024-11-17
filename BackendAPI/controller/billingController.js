const Billing = require('../models/Billing');

// Create a new billing entry
const createBilling = async (req, res) => {
    const { patientName, cost } = req.body;

    // Validate input
    if (!patientName || !cost) {
        return res.status(400).json({ error: 'Patient name and cost are required.' });
    }

    // Validate cost as a positive number
    if (cost < 0) {
        return res.status(400).json({ error: 'Cost must be a positive number.' });
    }

    // Create a new Billing instance
    const billingEntry = new Billing({
        patientName,
        cost,
    });

    try {
        await billingEntry.save(); // Save to the database
        return res.status(201).json({ message: 'Billing entry created successfully', billingEntry });
    } catch (error) {
        console.error('Error saving billing entry:', error);
        return res.status(500).json({ error: 'Internal server error' });
    }
};

// Retrieve all billing entries
const getAllBilling = async (req, res) => {
    try {
        const billingEntries = await Billing.find();
        return res.status(200).json(billingEntries);
    } catch (error) {
        console.error('Error retrieving billing entries:', error);
        return res.status(500).json({ error: 'Internal server error' });
    }
};

// Delete a billing entry by ID
const deleteBilling = async (req, res) => {
    const { id } = req.params;

    try {
        const result = await Billing.findByIdAndDelete(id);
        if (!result) {
            return res.status(404).json({ error: 'Billing entry not found.' });
        }
        return res.status(200).json({ message: 'Billing entry deleted successfully.' });
    } catch (error) {
        console.error('Error deleting billing entry:', error);
        return res.status(500).json({ error: 'Internal server error' });
    }
};

// Export the controller functions
module.exports = {
    createBilling,
    getAllBilling,
    deleteBilling,
};
