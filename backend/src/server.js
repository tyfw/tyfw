// express server
const express = require('express');
const app = express();
app.use(express.json());


// mongo-db
const { MongoClient } = require('mongodb');

// test function for mongo-db
async function run() {
    try {
      // Connect the client to the server
      await mongo_client.connect();
      // Establish and verify connection
      await mongo_client.db("admin").command({ ping: 1 });
      console.log("Connected successfully to server");
    } finally {
      // Ensures that the client will close when you finish/error
      await mongo_client.close();
    }
  }

// Google User Auth
const {OAuth2Client} = require('google-auth-library');
const CLIENT_ID = process.env.CLIENT_ID;
const client = new OAuth2Client(CLIENT_ID);


// verifacation of token provided by frontend
async function verify() {
  const ticket = await client.verifyIdToken({
      idToken: token,
      audience: CLIENT_ID,  // Specify the CLIENT_ID of the app that accesses the backend
  });
  const payload = ticket.getPayload();
  const userid = payload['sub'];
}

//verify().catch(console.error);
