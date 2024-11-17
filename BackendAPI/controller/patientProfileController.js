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
        // Fetch the user's profile using the logged-in user's ID
        const user = await User.findById(req.user._id).select('-password'); // Exclude the password field

        if (!user) {
            return res.status(404).json({ message: 'User not found' });
        }

        res.status(200).json(user);
    } catch (error) {
        console.error('Error fetching patient profile:', error);
        res.status(500).json({ message: 'Server error' });
    }
};

// Get All Patient Names
const getAllPatientNames = async (req, res) => {
    try {
        // Fetch all patient names where the role is 'patient'
        const patients = await User.find({ role: 'patient' }).select('name'); // Select only the 'name' field

        if (!patients || patients.length === 0) {
            return res.status(404).json({ message: 'No patients found' });
        }

        // Map the results to a list of patient names
        const patientNames = patients.map(patient => patient.name);

        res.status(200).json(patientNames);
    } catch (error) {
        console.error('Error fetching patient names:', error);
        res.status(500).json({ message: 'Server error' });
    }
};






module.exports = {
    savePatientProfile,
    getPatientProfile,
    getAllPatientNames, // Export the new function
};
