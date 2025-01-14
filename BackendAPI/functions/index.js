const express = require('express');
const cors = require('cors');
const functions = require('firebase-functions');
const admin = require('firebase-admin');
const mongoose = require('mongoose');
require('dotenv').config();  // Load environment variables for local development

// Initialize Firebase Admin SDK (only once)
const serviceAccount = require('./Keys/serviceKey.json');

if (!admin.apps.length) {
  admin.initializeApp({
    credential: admin.credential.cert(serviceAccount)
  });
  console.log('Firebase Admin SDK initialized successfully.');
} else {
  console.log('Firebase Admin SDK was already initialized.');
}

// Initialize Express app
const app = express();

// Middleware
app.use(cors({ origin: true }));
app.use(express.json());

// Access MongoDB URI from Firebase Config or local environment
const mongoURI =  process.env.MONGO_URI;
const jwtSecret = process.env.JWT_SECRET || 'default_secret'; // Fallback to a default secret if not set

// MongoDB connection (ensure the MongoDB URI is correctly set)
mongoose.connect(mongoURI, { useNewUrlParser: true })
  .then(() => console.log('MongoDB connected'))
  .catch((error) => console.error('MongoDB connection error:', error));

// Register routes
const authRoutes = require('./routes/authRoutes');
const appointmentRoutes = require('./routes/appointmentRoutes');
const medicalHistoryRoutes = require('./routes/medicalHistoryRoutes');
const form2Routes = require('./routes/formRoutes'); // Import the formRoutes
const patientProfileRoutes = require('./routes/patientProfileRoutes');
const medicalTestRoutes = require('./routes/medicalTestRoutes');
const billingRoutes = require('./routes/billingRoute');

app.use('/api/auth', authRoutes);
app.use('/api/appointments', appointmentRoutes);
app.use('/api/medicalHistory', medicalHistoryRoutes);
app.use('/api/form2', form2Routes); // Ensure this is set correctly
app.use('/api/patient/profile', patientProfileRoutes);
app.use('/api/medical-tests', medicalTestRoutes);
app.use('/api', billingRoutes);

// Basic error handling middleware
app.use((err, req, res, next) => {
  console.error("Error encountered:", err);
  res.status(500).json({ error: 'Something went wrong with error handling!' });
});

// Export the app as a Firebase Cloud Function
exports.api = functions.https.onRequest(app);