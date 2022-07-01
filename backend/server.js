// express server
const express = require('express');
const app = express();
app.use(express.json());

const uri = ""

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
        // authenticate user
        // check for valid googleIdToken
        
        res.status(200).send("Success") 
    }
    catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})

app.post("/user/register", async (req, res) => {
    try {
        res.status(200).send("Success") 
    }
    catch (err) {
        console.log(err)
        res.send(400).send(err)
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
        res.status(200).json({startTime: 10, endTime: 12, timescale: {type: "timescaleType", hour: 12, month: 10, day: 31}, value: [1, 2, 3, 4]})
    }
    catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})

app.get("/user/displayotheruser", async (req, res) => {
    try {
        res.status(200).json({startTime: 10, endTime: 12, timescale: {type: "timescaleType", hour: 12, month: 10, day: 31}, value: [1, 2, 3, 4]})
    }
    catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})

app.post("/user/changename", async (req, res) => {
    try {
        res.status(200).send("Success")

    }
    catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})
