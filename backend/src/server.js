// express server
const express = require('express');
const app = express();
app.use(express.json());

class User {
    constructor(username, firstname, lastname, email, addresses) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        // this.role = role;
        this.addresses = [];
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
    } catch(err) {
        console.log(err)
        await mongo_client.close()
    }
  }

// Google User Auth
const {OAuth2Client, UserRefreshClient} = require('google-auth-library');
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

app.get("/", (req, res) => {
  res.send("Hello world!")
})

var server = app.listen(8081, (req, res) => {
  var host = server.address().address
  var port = server.address().port
  console.log("TYFW backend server running at http://%s:%s", host, port)
})

app.get("/user/authenticate", async (req, res) => {
  try {
      verify()
      const existingUser = await mongo_client.db("tyfw").collection("users").findOne({"email": req.body.email})
      if (existingUser == null) {
        throw new Error('No User with this email')
      }
  }
  catch (err) {
      console.log(err)
      res.sendStatus(401)
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
      res.sendStatus(401)
  }
})

app.get("/user/leaderboard", async (req, res) => {
  try {
      res.status(200).json([{user: "BarackObama", value: 4.20}, {user: "CaleMakar", value: 8.0} ])
  }
  catch (err) {
      console.log(err)
      res.send(400).send(err)
  }
})
  
app.get("/user/displaycurruser", async (req, res) => {
  try {
      res.status(200).json({startTime: 10, endTime: 12, timescale: "month", value: [5, 6, 7, 8]}) 
  }
  catch (err) {
      console.log(err)
      res.send(400).send(err)
  }
})

app.get("/user/displayotheruserbyusername", async (req, res) => {
  try {
      res.status(200).json({startTime: 10, endTime: 12, timescale: "hour", value: [1, 2, 3, 4]})
  }
  catch (err) {
      console.log(err)
      res.send(400).send(err)
  }
})

app.get("/user/displayotheruserbywalletaddress", async (req, res) => {
  try {
      res.status(200).json({startTime: 10, endTime: 12, timescale: "hour", value: [1, 2, 3, 4]})
  }
  catch (err) {
      console.log(err)
      res.send(400).send(err)
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
      res.send(400).send(err)
  }
})

app.get("/user/search", async (req, res) => {
  try {
      res.status(200).send("Success")

  }
  catch (err) {
      console.log(err)
      res.send(400).send(err)
  }

})

run()