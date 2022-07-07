// dotenv for environment variables
require('dotenv').config();

// express server
const express = require('express');
const app = express();
app.use(express.json());

// Graylog logging
const graylog2 = require('graylog2');

const logger = new graylog2.graylog({
  servers: [{ host: '4m1pqj.stackhero-network.com', port: 12201 }] // Replace the "host" per your Graylog domain
});

// Send a simple message to Graylog
logger.log('Hello from tyfw server');

// Crypto data functions
const crypto = require("./data.js")

class User {
    constructor(username, firstname, lastname, email, addresses) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.addresses = addresses; 
        this.friends = [];
    }
}

// mongo-db
const { MongoClient } = require('mongodb');
const uri = "mongodb://localhost:27017"
const mongo_client = new MongoClient(uri)

// test function for mongo-db
async function run() {
    try {
      // Connect the client to the server
      await mongo_client.connect();
      // Establish and verify connection
      await mongo_client.db("admin").command({ ping: 1 });
      console.log("Connected successfully to database");
      logger.log("Connected successfully to database")
    } catch(err) {
        console.log(err)
        await mongo_client.close()
    }
  }

// Google User Auth
const {OAuth2Client, UserRefreshClient} = require('google-auth-library');
const { getBalance, getEthBalance, getAccountHistory} = require('./data.js');
const CLIENT_ID = process.env.CLIENT_ID;
const client = new OAuth2Client(CLIENT_ID);


// verifacation of token provided by frontend
async function googleAuthVerify(token) {
  try {
    const ticket = await client.verifyIdToken({
        idToken: token,
        audience: CLIENT_ID,
    });
    // TODO: Check if more info from the ticket needs to be validated
    return true

  } catch (err) {
    console.log(err)
    logger.log(String(err))
    return false
  }
  //const payload = ticket.getPayload();
  //const userid = payload['sub'];
}

app.get("/", (req, res) => {
  res.send("Hello world!")
})

var server = app.listen(8081, (req, res) => {
  var host = server.address().address
  var port = server.address().port
  console.log("TYFW backend server running at http://%s:%s", host, port)
  logger.log("TYFW backend server running at http://%s:%s", host, port)
})

app.post("/user/authenticate", async (req, res) => {
  try {
    const verifyied = await googleAuthVerify(req.body.googleIdToken)
    if (!verifyied) {
      res.sendStatus(401)
      return;
    }

    const existingUser = await mongo_client.db("tyfw").collection("users").findOne({"email": req.body.email})
    
    if (existingUser == null) {
      console.log("User not found")
      res.sendStatus(201)
      return;
    }
    res.sendStatus(200)
  }
  catch (err) {
      console.log(err)
      logger.log(String(err))
      res.sendStatus(400)
  }
})

app.post("/user/register", async (req, res) => {
  try {
      // check if there is another user with the same username
      const existingUser = await mongo_client.db("tyfw").collection("users").findOne({"username": req.body.username})
      if (existingUser != null) {
        throw new Error('Username Exists')
      }
      else {
        //create user object
        const user = new User(req.body.username, req.body.firstName, req.body.lastName, req.body.email, req.body.walletAddress)
        await mongo_client.db("tyfw").collection("users").insertOne(user)
        res.status(200).send("Success") 
      }
  }
  catch (err) {
      console.log(err)
      logger.log(String(err))
      res.sendStatus(401)
  }
})

app.get("/user/leaderboard", async (req, res) => {
  try {
      var leaderboard = []
      const user = await mongo_client.db("tyfw").collection("users").findOne({"email": req.header("email")})
      for (let index in user.friends) {
        const friend = await mongo_client.db("tyfw").collection("users").findOne({"email": user.friends[index]})
        var total_balance = 0
        for (let j = 0; j < friend.addresses.length; j = j+1) {
                var balance = await getBalance(friend.addresses[j])
          total_balance += balance
        }
        leaderboard.push({"user": friend.username, "value": total_balance})
              leaderboard.sort((a, b) => {
                if (a.value > b.value) return -1
                else return 1
              })
      }
      res.status(200).send(leaderboard)
    }
  catch (err) {
      console.log(err)
      logger.log(String(err))
      res.sendStatus(400)
  }
})
  
app.get("/user/displaycurruser", async (req, res) => {
  try {
      const user = await mongo_client.db("tyfw").collection("users").findOne({"email": req.header("email")})
      if (user == null) {
        throw new Error('No users found')
      }
      else {
        const interval = "1d"
        const secondsInMonth = 2.628E6
        const startTime = Date.now() - secondsInMonth
        const endTime = Date.now()
        const accountHistory = await getAccountHistory(user.addresses[0], interval, numpoints=req.header("numPoints"), startTime, endTime) 
        res.status(200).json({"startTime": startTime, "endTime": endTime, "timescale": "day", "data": accountHistory})
      }
  }
  catch (err) {
      console.log(err)
      logger.log(String(err))
      res.sendStatus(400)
  }
})

app.get("/user/displayotheruserbyusername", async (req, res) => {
  try {
      const user = await mongo_client.db("tyfw").collection("users").findOne({"username": req.header("otherUsername")})
      if (user == null) {
        throw new Error('No users found')
      }
      else {
        const interval = "1d"
        const secondsInMonth = 2.628E6
        const startTime = Date.UTC - secondsInMonth
        const endTime = Date.UTC
        const accountHistory = await getAccountHistory(user.addresses[0], interval, numpoints=req.header("numPoints"), startTime, endTime) 
        res.status(200).json({"startTime": startTime, "endTime": endTime, "timescale": "day", "data": accountHistory})
      }
    }
  catch (err) {
      console.log(err)
      logger.log(String(err))
      res.sendStatus(400)
  }
})

app.get("/user/displayotheruserbywalletaddress", async (req, res) => {
  try {
      const user = await mongo_client.db("tyfw").collection("users").findOne({"addresses": req.header("otherWalletAddress")})
      if (user == null) {
        throw new Error('No users found')
      }
      else {
        res.status(200).json(user.wallet)
      }
    }
  catch (err) {
      console.log(err)
      logger.log(String(err))
      res.sendStatus(400)
  }
})


app.post("/user/changename", async (req, res) => {
  try {
      if (req.body.name == "first") {
        await mongo_client.db("tyfw").collection("users").updateOne({"email": req.body.email}, {$set: {firstname: req.body.newName}})
      }
      else if (req.body.name == "last") {
        await mongo_client.db("tyfw").collection("users").updateOne({"email": req.body.email}, {$set: {lastname: req.body.newName}})
      }
      res.status(200).send("Success")

  }
  catch (err) {
      console.log(err)
      logger.log(String(err))
      res.sendStatus(400)
  }
})

app.get("/user/search", async (req, res) => {
  try {
      const queryMatches = await mongo_client.db("tyfw").collection("users").find({$or: [{"username": {$regex: req.header("queryString"), $options: "$i"}}, {"addresses": {$regex: req.header("queryString"), $options: "$i"}}]}).project({username: 1, addresses: 1, _id: 0}).toArray()
      if (queryMatches.length == 0) {
        throw new Error('No users found')
      }
      res.status(200).json({"queryMatches": queryMatches})
  }
  catch (err) {
      console.log(err)
      logger.log(String(err))
      res.sendStatus(400)
  }

})

app.post("/user/addbyusername", async (req, res) => {
  try {
    //check that username exists
      const newFriend = await mongo_client.db("tyfw").collection("users").findOne({"username": req.body.friendUsername})
      if (newFriend == null) {
        throw new Error('No User with this username')
      }
      await mongo_client.db("tyfw").collection("users").updateOne({"email": req.body.email}, {$addToSet: {friends: newFriend.email}})
      res.status(200).send("Success")
  }
  catch (err) {
      console.log(err)
      logger.log(String(err))
      res.sendStatus(400)
  }

})

app.post("/user/addbywalletaddress", async (req, res) => {
  try {
    //check that there is a user with the specified wallet address 
      const newFriend = await mongo_client.db("tyfw").collection("users").findOne({"addresses": req.body.friendWalletAddress})
      if (newFriend == null) {
        throw new Error('No User with this wallet address')
      }
      await mongo_client.db("tyfw").collection("users").updateOne({"email": req.body.email}, {$addToSet: {friends: newFriend.email}})
      res.status(200).send("Success")
  }
  catch (err) {
      console.log(err)
      logger.log(String(err))
      res.sendStatus(400)
  }
})

run()
