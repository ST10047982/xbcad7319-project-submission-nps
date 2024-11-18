const User = require('../models/User');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');

const register = async (req, res) => {
    const { username, password, role, name, surname, email, phoneNumber, medicalAid, medicalAidNumber } = req.body;
  
    try {
      console.log("Register request received:", req.body);
  
      // Check if user already exists
      const userExists = await User.findOne({ username });
      if (userExists) {
        console.log("User already exists with username:", username);
        return res.status(400).json({ message: 'Username already taken' });
      }
  
      // Hash password
      console.log("Hashing password for:", username);
      const hashedPassword = await bcrypt.hash(password, 10);
  
      // Create new user
      const user = new User({
        username,
        password: hashedPassword,
        role,
        name,
        surname,
        email,
        phoneNumber,
        medicalAid,
        medicalAidNumber,
      });
  
      // Save user to database
      console.log("Saving new user to the database...");
      await user.save();
  
      console.log("User registered successfully:", username);
      res.status(201).json({ message: 'User registered successfully' });
    } catch (error) {
      console.error("Error during registration:", error);
      res.status(500).json({ message: 'Server error', error: error.message});
    }
  };

  const login = async (req, res) => {
    const { username, password } = req.body;
  
    try {
      console.log("Login request received for username:", username);
  
      // Find user by username
      const user = await User.findOne({ username });
      if (!user) {
        console.log("No user found with username:", username);
        return res.status(400).json({ message: 'Invalid username' });
      }
  
      // Check if password matches
      console.log("Checking password for user:", username);
      const isMatch = await user.matchPassword(password);
      if (!isMatch) {
        console.log("Invalid password for username:", username);
        return res.status(400).json({ message: 'Invalid password' });
      }
      
   
      // Generate token
      console.log("Generating token for user:", username);
      const token = jwt.sign({ id: user._id, role: user.role }, process.env.JWT_SECRET, {
        expiresIn: '1h',
      });
  
      console.log("Login successful for user:", username);
  
      // Differentiate response based on user role
      if (user.role === 'staff') {
        return res.json({
          token,
          userId: user._id,
          role: user.role,
          message: 'Welcome to the staff portal!',
        });
      } else if (user.role === 'patient') {
        return res.json({
          token,
          userId: user._id,
          role: user.role,
          message: 'Welcome to the patient portal!',
        });
      } else {
        return res.status(400).json({ message: 'Unknown user role' });
      }
    } catch (error) {
      console.error("Error during login:", error);
      res.status(500).json({ message: 'Server error' });
    }
  };
  
  
  const forgetPassword = async (req, res) => {
    const { username, newPassword } = req.body;
  
    try {
      console.log("Forget password request for username:", username);
  
      // Find user by username
      const user = await User.findOne({ username });
      if (!user) {
        console.log("No user found with username:", username);
        return res.status(404).json({ message: 'User not found' });
      }
  
      // Hash new password
      console.log("Hashing new password for user:", username);
      const hashedPassword = await bcrypt.hash(newPassword, 10);
      user.password = hashedPassword;
  
      // Save updated user
      console.log("Saving updated password for user:", username);
      await user.save();
  
      console.log("Password updated successfully for user:", username);
      res.json({ message: 'Password updated successfully' });
    } catch (error) {
      console.error("Error during forget password:", error);
      res.status(500).json({ message: 'Server error' });
    }
  };

  const updateProfile = async (req, res) => {
    const { userId } = req.params; // Patient's user ID from route parameters
    const { email, phoneNumber, medicalAid, medicalAidNumber } = req.body; // Fields to be updated

    // Check if the userId in the params matches the logged-in user's ID
    if (userId !== req.user.id) {
        return res.status(403).json({ message: 'You are not authorized to update this profile' });
    }

    try {
        console.log("Update profile request received for userId:", userId);

        // Find user by ID
        const user = await User.findById(userId);
        if (!user) {
            console.log("No user found with userId:", userId);
            return res.status(404).json({ message: 'User not found' });
        }

        // Update only the fields that are provided
        if (email) user.email = email;
        if (phoneNumber) user.phoneNumber = phoneNumber;
        if (medicalAid) user.medicalAid = medicalAid;
        if (medicalAidNumber) user.medicalAidNumber = medicalAidNumber;

        // Save updated user
        await user.save();

        console.log("Profile updated successfully for userId:", userId);
        res.status(200).json({ message: 'Profile updated successfully', user });
    } catch (error) {
        console.error("Error updating profile:", error);
        res.status(500).json({ message: 'Server error' });
    }
};

// View Patient Profile - Staff Member
const viewPatientProfile = async (req, res) => {
  const { patientId } = req.params; // Assuming patientId is passed as a URL parameter

  try {
      console.log("View patient profile request for patientId:", patientId);

      // Check if the logged-in user is a staff member
      if (req.user.role !== 'staff') {
          return res.status(403).json({ message: 'Access denied. Only staff members can view patient profiles.' });
      }

      // Find the patient by ID
      const patient = await User.findById(patientId);
      if (!patient) {
          console.log("No patient found with ID:", patientId);
          return res.status(404).json({ message: 'Patient not found' });
      }

      // Exclude the password field from the response
      const { password, ...patientDetails } = patient._doc;

      console.log("Patient profile retrieved successfully:", patientDetails);
      res.status(200).json(patientDetails);
  } catch (error) {
      console.error("Error retrieving patient profile:", error);
      res.status(500).json({ message: 'Server error' });
  }
};

// Logout function (handled mostly on the client-side)
const logout = (req, res) => {
  try {
    console.log("Logout request received.");

    // Clear the token (handled on the client-side in most implementations)
    res.status(200).json({ message: 'Logout successful' });

    // Alternatively, you can also instruct the client to remove the token from their storage
    // You can optionally send a response that instructs the client to clear the token.
  } catch (error) {
    console.error("Error during logout:", error);
    res.status(500).json({ message: 'Server error' });
  }
};


  // Export the functions
   module.exports = {
    register,
    login,
    forgetPassword,
    updateProfile,
    viewPatientProfile,
    logout,
  };