        const Form2 = require('../models/Form2'); 
        const Form1 = require('../models/Form1');

        // Create a new form entry
        const createForm2 = async (req, res) => {
            const { name, areasConcernedForNeedling, date, signature } = req.body;

            // Validate input
            if (!name || !areasConcernedForNeedling || !date || !signature) {
                return res.status(400).json({ error: 'All fields are required.' });
            }

            // Create a new Form2 instance
            const formEntry = new Form2({
                name,
                areasConcernedForNeedling,
                date,
                signature,
                form2Filled: true,
            });

            try {
                await formEntry.save(); // Save to the database
                return res.status(201).json({ message: 'Form submitted successfully', formEntry });
            } catch (error) {
                console.error('Error saving form data:', error);
                return res.status(500).json({ error: 'Internal server error' });
            }
        };




const createForm1 = async (req, res) => {
    const {
        firstNameP,
        surnameP,
        titleP,
        idP,
        ageP,
        addressP,
        codeP,
        cellNumberP,
        workNumberP,
        homeNumberP,
        emailP,
        medicalAidNameP,
        medicalAidNumberP,
        firstNameR,
        surnameR,
        titleR,
        idR,
        ageR,
        addressR,
        codeR,
        cellNumberR,
        workNumberR,
        homeNumberR,
        emailR,
        firstNameK,
        addressK,
        codeK,
        nameS,
        typeS,
        signature,
        placeS,
        date,
        
    } = req.body;

    // Validate required fields only
    if (
        !firstNameP || !surnameP || !titleP || !idP || !ageP || !addressP ||
        !codeP || !cellNumberP || !emailP || 
        !firstNameK || !addressK || !codeK || 
        !nameS || !typeS || !signature || !placeS || !date
    ) {
        return res.status(400).json({ error: 'Some required fields are missing.' });
    }

    // Create a new Form1 instance with both required and optional fields
    const formEntry = new Form1({
        firstNameP,
        surnameP,
        titleP,
        idP,
        ageP,
        addressP,
        codeP,
        cellNumberP,
        workNumberP,
        homeNumberP,
        emailP,
        medicalAidNameP,      // Optional field
        medicalAidNumberP,    // Optional field
        firstNameR,           // Optional field
        surnameR,             // Optional field
        titleR,               // Optional field
        idR,                  // Optional field
        ageR,                 // Optional field
        addressR,             // Optional field
        codeR,                // Optional field
        cellNumberR,          // Optional field
        workNumberR,          // Optional field
        homeNumberR,          // Optional field
        emailR,               // Optional field
        firstNameK,
        addressK,
        codeK,
        nameS,
        typeS,
        signature,
        placeS,
        date,
        form1Filled: true
    });

    try {
        // Save the form entry to the database
        await formEntry.save();
        return res.status(201).json({ message: 'Form submitted successfully', formEntry });
    } catch (error) {
        console.error('Error saving form data:', error);
        return res.status(500).json({ error: 'Internal server error' });
    }
};


        // Export the controller functions
        module.exports = {
        createForm2,
        createForm1,
        };
