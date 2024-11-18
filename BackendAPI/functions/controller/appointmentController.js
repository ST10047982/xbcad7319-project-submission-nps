const Appointment = require('../models/Appointment');

// Book an appointment
const bookAppointment = async (req, res) => {
    const { date, time, description } = req.body;
    const patientId = req.user.id; 
  
    try {
      const appointment = new Appointment({
        patient: patientId,
        date,
        time,
        description,
        status: 'pending' // Status is set to pending when an appointment is booked
      });
  
      await appointment.save();
      res.status(201).json({
        message: 'Appointment booked successfully',
        appointment,
      });
    } catch (error) {
      console.error("Error booking appointment:", error);
      res.status(500).json({ message: 'Server error' });
    }
  };
  

// Reschedule an appointment
const rescheduleAppointment = async (req, res) => {
    const appointmentId = req.params.appointmentId.trim();
    const { date, time, description } = req.body;

    try {
        const appointment = await Appointment.findById(appointmentId);

        if (!appointment) {
            return res.status(404).json({ message: 'Appointment not found' });
        }

        // Check if the appointment status is 'approved'
        if (appointment.status !== 'approved') {
            return res.status(400).json({ message: 'Only approved appointments can be rescheduled' });
        }

        // Check if the logged-in user is allowed to reschedule this appointment
        if (appointment.patient.toString() !== req.user._id.toString()) {
            return res.status(403).json({ message: 'You do not have permission to reschedule this appointment' });
        }

        // Update the appointment details and mark it as rescheduled
        appointment.date = date || appointment.date;
        appointment.time = time || appointment.time;
        appointment.description = description || appointment.description;
        appointment.status = 'rescheduled'; // Change status to rescheduled

        const updatedAppointment = await appointment.save();

        res.status(200).json({
            message: 'Appointment rescheduled successfully',
            appointment: updatedAppointment
        });
    } catch (error) {
        console.error('Error rescheduling appointment:', error);
        res.status(500).json({ message: 'Server error' });
    }
};


// Cancel an appointment
const cancelAppointment = async (req, res) => {
    const appointmentId = req.params.appointmentId.trim();
    const { date, time, description } = req.body;

    try {
        const appointment = await Appointment.findById(appointmentId);

        if (!appointment) {
            return res.status(404).json({ message: 'Appointment not found' });
        }

        //Check if the appointment is already canceled
        if (appointment.status === 'canceled') {
            console.log('Appointment is already canceled');
            return res.status(400).json({ message: 'This appointment has already been canceled' });
        }

        // Check if the appointment status is 'approved'
        if (appointment.status !== 'approved') {
            return res.status(400).json({ message: 'Only approved appointments can be canceled' });
        }

        // Check if the logged-in user is allowed to reschedule this appointment
        if (appointment.patient.toString() !== req.user._id.toString()) {
            return res.status(403).json({ message: 'You do not have permission to canceled this appointment' });
        }

        // Update the appointment details and mark it as rescheduled
        appointment.date = date || appointment.date;
        appointment.time = time || appointment.time;
        appointment.description = description || appointment.description;
        appointment.status = 'canceled'; // Change status to rescheduled

        const updatedAppointment = await appointment.save();

        res.status(200).json({
            message: 'Appointment rescheduled successfully',
            appointment: updatedAppointment
        });
    } catch (error) {
        console.error('Error rescheduling appointment:', error);
        res.status(500).json({ message: 'Server error' });
    }
};

// Approve an appointment
const approveAppointment = async (req, res) => {
    const appointmentId = req.params.appointmentId.trim();
    console.log(`Attempting to approve appointment with ID: ${appointmentId}`);

    try {
        const appointment = await Appointment.findById(appointmentId).populate('patient', 'name email');
        console.log('Fetched appointment:', appointment);

        if (!appointment) {
            console.log('Appointment not found');
            return res.status(404).json({ message: 'Appointment not found' });
        }

        // Check if the appointment is already confirmed
        if (appointment.status === 'approved') {
            console.log('Appointment is already approved');
            return res.status(400).json({ message: 'This appointment is already confirmed' });
        }

        // Check if the appointment is canceled
        if (appointment.status === 'canceled') {
            console.log('Cannot approve a canceled appointment');
            return res.status(400).json({ message: 'This appointment has been canceled and cannot be approved' });
        }

        // Verify if the logged-in user is a staff member
        if (req.user.role !== 'staff') {
            console.log('Permission denied: user is not authorized to approve this appointment');
            return res.status(403).json({ message: 'You do not have permission to approve this appointment' });
        }

        // Update the appointment status to 'approved'
        appointment.status = 'approved';
        await appointment.save();

        console.log('Appointment approved successfully');
        res.status(200).json({
            message: 'Appointment approved successfully',
            appointment,
        });
    } catch (error) {
        console.error('Error approving appointment:', error);
        res.status(500).json({ message: 'Server error' });
    }
};


const getPatientNotifications = async (req, res) => {
    try {
        const now = new Date();
        const oneDayLater = new Date(now);
        oneDayLater.setDate(now.getDate() + 1);

        // Fetch all appointments for the logged-in patient within the next 24 hours
        const upcomingAppointments = await Appointment.find({
            patient: req.user._id,
            date: { $gte: now, $lt: oneDayLater },
            status: 'confirmed'
        }).populate('patient', 'name email');

        // Fetch all appointments with status changes (rescheduled, canceled, confirmed)
        const statusChangedAppointments = await Appointment.find({
            patient: req.user._id,
            status: { $in: ['rescheduled', 'canceled', 'confirmed'] }
        }).populate('patient', 'name email');

        // Prepare notifications for upcoming appointments
        const upcomingNotifications = upcomingAppointments.map(appointment => ({
            appointmentId: appointment._id,
            message: `Reminder: You have a confirmed appointment tomorrow at ${appointment.time}.`,
            date: appointment.date,
            time: appointment.time,
            description: appointment.description,
            status: appointment.status
        }));

        // Prepare notifications for status changes (rescheduled, canceled, confirmed)
        const statusChangeNotifications = statusChangedAppointments.map(appointment => {
            let message;
            if (appointment.status === 'rescheduled') {
                message = `Your appointment has been rescheduled.`;
            } else if (appointment.status === 'canceled') {
                message = `Your appointment has been canceled.`;
            } else if (appointment.status === 'confirmed') {
                message = `Your appointment has been confirmed.`;
            }

            return {
                appointmentId: appointment._id,
                message,
                date: appointment.date,
                time: appointment.time,
                description: appointment.description,
                status: appointment.status
            };
        });

        // Combine both notifications
        const notifications = [...upcomingNotifications, ...statusChangeNotifications];

        // Count the number of notifications
        const notificationCount = notifications.length;

        if (!notifications.length) {
            return res.status(404).json({ message: 'No notifications found for this patient' });
        }

        res.status(200).json({ count: notificationCount, notifications });
    } catch (error) {
        console.error('Error fetching patient notifications:', error);
        res.status(500).json({ message: 'Server error' });
    }
};


const getStaffNotifications = async (req, res) => {
    try {
        const now = new Date();
        const oneDayLater = new Date(now);
        oneDayLater.setDate(now.getDate() + 1);

        // Fetch all upcoming appointments within the next 24 hours with confirmed status
        const upcomingAppointments = await Appointment.find({
            status: 'confirmed',
            date: { $gte: now, $lt: oneDayLater }
        }).populate('patient', 'name email'); // Populate patient details

        // Fetch appointments with status changes (pending, rescheduled, canceled)
        const statusChangedAppointments = await Appointment.find({
            status: { $in: ['pending', 'rescheduled', 'canceled'] }
        }).populate('patient', 'name email'); // Populate patient details

        // Prepare notifications for upcoming confirmed appointments
        const upcomingNotifications = upcomingAppointments.map(appointment => ({
            appointmentId: appointment._id,
            patientId: appointment.patient._id,
            message: `Reminder: ${appointment.patient.name} has a confirmed appointment tomorrow at ${appointment.time}.`,
            date: appointment.date,
            time: appointment.time,
            description: appointment.description,
            status: appointment.status
        }));

        // Prepare notifications for status changes (pending, rescheduled, canceled)
        const statusChangeNotifications = statusChangedAppointments.map(appointment => {
            let message;
            if (appointment.status === 'pending') {
                message = `New appointment for ${appointment.patient.name} is pending confirmation.`;
            } else if (appointment.status === 'rescheduled') {
                message = `Appointment for ${appointment.patient.name} has been rescheduled.`;
            } else if (appointment.status === 'canceled') {
                message = `Appointment for ${appointment.patient.name} has been canceled.`;
            }

            return {
                appointmentId: appointment._id,
                patientId: appointment.patient._id,
                message,
                date: appointment.date,
                time: appointment.time,
                description: appointment.description,
                status: appointment.status
            };
        });

        // Combine both types of notifications
        const notifications = [...upcomingNotifications, ...statusChangeNotifications];

        // Count the number of notifications
        const notificationCount = notifications.length;

        if (!notificationCount) {
            return res.status(404).json({ message: 'No notifications found for staff' });
        }

        res.status(200).json({ count: notificationCount, notifications });
    } catch (error) {
        console.error('Error fetching staff notifications:', error);
        res.status(500).json({ message: 'Server error' });
    }
};


const getConfirmedAppointmentsForPatient = async (req, res) => {
    try {
        // Find all appointments for the logged-in patient with status "pending" or "confirmed"
        const appointments = await Appointment.find({
            patient: req.user._id, // Logged-in patient's ID
            status: { $in: ['pending', 'confirmed'] } // Only pending and confirmed appointments
        }).select('_id patient date time description status') // Explicitly select fields to include appointment ID
        .populate('patient', 'name email'); // Populate patient details

        if (!appointments.length) {
            return res.status(404).json({ message: 'No confirmed appointments found for this patient' });
        }

        res.status(200).json(appointments); // Send the response including the appointment ID
    } catch (error) {
        console.error('Error fetching confirmed appointments for patient:', error);
        res.status(500).json({ message: 'Server error' });
    }
};

const getAllAppointmentsForPatient = async (req, res) => {
    try {
        // Find all appointments for the logged-in patient with status "pending" or "confirmed"
        const appointments = await Appointment.find({
            patient: req.user._id, // Logged-in patient's ID
        }).select('_id patient date time description status') // Explicitly select fields to include appointment ID
        .populate('patient', 'name email'); // Populate patient details

        if (!appointments.length) {
            return res.status(404).json({ message: 'No confirmed appointments found for this patient' });
        }

        res.status(200).json(appointments); // Send the response including the appointment ID
    } catch (error) {
        console.error('Error fetching confirmed appointments for patient:', error);
        res.status(500).json({ message: 'Server error' });
    }
};

// View all appointments for all patients regardless of status
const getAllAppointments = async (req, res) => {
    try {
        // Find all appointments regardless of their status
        const appointments = await Appointment.find({})
            .populate('patient', 'name email'); 

        if (!appointments.length) {
            return res.status(404).json({ message: 'No appointments found' });
        }

        res.status(200).json(appointments);
    } catch (error) {
        console.error('Error fetching all appointments:', error);
        res.status(500).json({ message: 'Server error' });
    }
};

const getConfirmedAppointments = async (req, res) => {
    try {
        // Find all confirmed appointments 
        const appointments = await Appointment.find({
          status: { $in: ['approved'] } // Only  confirmed appointments
        }).select('_id patient date time description status') // Explicitly select fields to include appointment ID
        .populate('patient', 'name email'); // Populate patient details

        if (!appointments.length) {
            return res.status(404).json({ message: 'No confirmed appointments found for this patient' });
        }

        res.status(200).json(appointments); // Send the response including the appointment ID
    } catch (error) {
        console.error('Error fetching confirmed appointments for patient:', error);
        res.status(500).json({ message: 'Server error' });
    }
};
const addAppointmentNotes = async (req, res) => {
    const appointmentId = req.params.appointmentId.trim();  // Appointment ID from the route parameter
    const { notes } = req.body; // Notes from the request body

    console.log('Received request to add appointment notes:', { appointmentId, notes });

    try {
        console.log('Searching for appointment with ID:', appointmentId);
        const appointment = await Appointment.findById(appointmentId);

        if (!appointment) {
            console.log('Appointment not found:', appointmentId);
            return res.status(404).json({ message: 'Appointment not found' });
        }

 
        // Add or update the notes for the appointment
        console.log('Adding notes to appointment:', notes);
        appointment.notes = notes  || appointment.notes;

        const updatedAppointmentNotes = await appointment.save();
       
        console.log('Notes added successfully for appointment:', appointmentId);

        res.status(200).json({
            message: 'Appointment notes added successfully',
            appointment: updatedAppointmentNotes,
        });
    } catch (error) {
        console.error('Error adding appointment notes:', error);
        res.status(500).json({ message: 'Server error' });
    }
};

// Get Appointment Notes
const getAppointmentNotes = async (req, res) => {
    const { appointmentId } = req.params;

    try {
        const appointment = await Appointment.findById(appointmentId).populate('patient', 'name email');

        if (!appointment) {
            return res.status(404).json({ message: 'Appointment not found' });
        }

        // Verify if the logged-in user is a staff member
        if (!req.user.isStaff) {
            return res.status(403).json({ message: 'You do not have permission to view notes for this appointment' });
        }

        res.status(200).json({
            message: 'Appointment notes retrieved successfully',
            notes: appointment.notes,
        });
    } catch (error) {
        console.error('Error fetching appointment notes:', error);
        res.status(500).json({ message: 'Server error' });
    }
};
module.exports = {
    bookAppointment,
    rescheduleAppointment,
    cancelAppointment,
    approveAppointment,
    getPatientNotifications,
    getStaffNotifications,
    getConfirmedAppointmentsForPatient, 
    getAllAppointments,
    getAllAppointmentsForPatient,
    getConfirmedAppointments,
    addAppointmentNotes,
    getAppointmentNotes,
};