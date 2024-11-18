const User = require('../models/User'); // Adjust the path as necessary

// Save Patient Profile
const savePatientProfile = async (req, res) => {
    const { email, phoneNumber, medicalAid, medicalAidNumber } = req.body;

    try {
        // Find the user profile by user ID
        let userProfile = await User.findById(req.user._id);
        
        if (userProfile) {
            // Only update the specified fields
            userProfile.email = email || userProfile.email;
            userProfile.phoneNumber = phoneNumber || userProfile.phoneNumber;
            userProfile.medicalAid = medicalAid || userProfile.medicalAid;
            userProfile.medicalAidNumber = medicalAidNumber || userProfile.medicalAidNumber;

            // Save the updated profile
            await userProfile.save();

            // Return success response with updated profile
            res.status(200).json({
                message: 'Patient profile updated successfully',
                profile: {
                    email: userProfile.email,
                    phoneNumber: userProfile.phoneNumber,
                    medicalAid: userProfile.medicalAid,
                    medicalAidNumber: userProfile.medicalAidNumber,
                }
            });
        } else {
            // If user profile doesn't exist, return an error
            return res.status(404).json({ message: 'User not found' });
        }
    } catch (error) {
        console.error('Error saving patient profile:', error);
        res.status(500).json({ message: 'Server error' });
    }
};

// Get Patient Profile
const getPatientProfile = async (req, res) => {
    try {
        // Assuming req.user is set by authentication middleware with the logged-in user's data
        const patientID = req.user.id; // Get the logged-in user's ID

        // Fetch the logged-in patient's profile using the user ID
        const patient = await User.findById(patientID).select('-password'); // Exclude the password field

        if (!patient) {
            return res.status(404).json({ message: 'Patient not found' });
        }

        res.status(200).json(patient);
    } catch (error) {
        console.error('Error fetching patient profile:', error);
        res.status(500).json({ message: 'Server error' });
    }
};

// Get Patient Profile by ID from URL
const getPatientProfileById = async (req, res) => {
    try {
        // Get the patient ID from the URL parameters
        const patientID = req.params.id;

        // Fetch the patient's profile by ID, excluding the password field
        const patient = await User.findById(patientID).select('-password'); // Exclude password field

        if (!patient) {
            return res.status(404).json({ message: 'Patient not found' });
        }

        // Send back the patient's profile
        res.status(200).json(patient);
    } catch (error) {
        console.error('Error fetching patient profile:', error);
        res.status(500).json({ message: 'Server error' });
    }
};

// API to find a user ID by name and surname
const getUserIdByNameAndSurname = async (req, res) => {
    try {
        const { name, surname } = req.query; // Assuming name and surname are passed as query parameters

        if (!name || !surname) {
            return res.status(400).json({ message: 'Name and surname are required.' });
        }

        console.log(`Searching for user with name: ${name} and surname: ${surname}`);

        // Search for the user in the database
        const user = await User.findOne({ name, surname }).select('_id'); // Select only the ID field

        if (!user) {
            return res.status(404).json({ message: 'User not found.' });
        }

        // Respond with the user's ID
        res.status(200).json({ userId: user._id });
    } catch (error) {
        console.error('Error finding user ID:', error);
        res.status(500).json({ message: 'Server error' });
    }
};


// Get All Patient Names
const getAllPatientNames = async (req, res) => {
    try {
        console.log('Fetching all patients with role "patient"');

        // Fetch all patients with role 'patient' and select 'name' and 'surname' fields
        const patients = await User.find({ role: 'patient' }).select('name surname');

        console.log('Fetched patients:', patients);

        if (!patients || patients.length === 0) {
            return res.status(404).json({ message: 'No patients found' });
        }

        // Map to extract 'name' and 'surname' from each patient document
        const patientNames = patients.map(patient => `${patient.name} ${patient.surname}`);

        // Return the list of patient names
        res.status(200).json({ patientNames });
    } catch (error) {
        console.error('Error fetching patient names:', error);
        res.status(500).json({ message: 'Server error' });
    }
};


module.exports = {
    savePatientProfile,
    getPatientProfile,
    getPatientProfileById,
    getAllPatientNames, // Export the new function
    getUserIdByNameAndSurname,
};
