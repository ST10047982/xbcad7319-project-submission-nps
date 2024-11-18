const mongoose = require('mongoose');

let isConnected = false; // Flag to track whether the connection is established

const connectDB = async () => {
  if (isConnected) {
    console.log('MongoDB is already connected');
    return; // Skip connecting if already connected
  }

  try {
    // Get Mongo URI from Firebase environment configuration
    const mongoURI = process.env.MONGO_URI || functions.config().mongo.uri;

    if (!mongoURI) {
      throw new Error('Mongo URI is missing!');
    }

    await mongoose.connect(mongoURI, {
      useNewUrlParser: true,
      useUnifiedTopology: true,
    });

    isConnected = true; // Mark as connected
    console.log('MongoDB connected');
  } catch (err) {
    console.error('Error connecting to MongoDB:', err.message);
    process.exit(1); // Exit the process if the database connection fails
  }
};

module.exports = connectDB;

