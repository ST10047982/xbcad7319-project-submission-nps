const express = require('express');
const http = require('http');
const cors = require('cors');
const dotenv = require('dotenv');
const connectDB = require('./config/db');
const authRoutes = require('./routes/authRoutes');
const appointmentRoutes = require('./routes/appointmentRoutes');
const medicalHistoryRoutes = require('./routes/medicalHistoryRoutes');
const form2Routes = require('./routes/formRoutes'); // Import the formRoutes
const patientProfileRoutes = require('./routes/patientProfileRoutes');
const medicalTestRoutes = require('./routes/medicalTestRoutes');
const billingRoutes = require('./routes/billingRoute');

// Load environment variables
dotenv.config();

// Initialize Express app
const app = express();

// Middleware
app.use(cors());
app.use(express.json());

// Database Connection
connectDB();

// Register your routes
app.use('/api/auth', authRoutes);
app.use('/api/appointments', appointmentRoutes);
app.use('/api/medicalHistory', medicalHistoryRoutes);
app.use('/api/form2', form2Routes); // Ensure this is set correctly
app.use('/api/patient/profile', patientProfileRoutes);
app.use('/api/medical-tests', medicalTestRoutes);
app.use('/api', billingRoutes);

// Basic error handling middleware
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).json({ error: 'Something went wrong!' });
});

// Create the HTTP server
const port = process.env.PORT || 3000; // Use environment variable or default to 3000
http.createServer(app).listen(port, () => {
  console.log(`Server is running on http://localhost:${port}`);
});
