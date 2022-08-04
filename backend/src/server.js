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


//debug printout
console.isDebugMode = true;


// mongo-db
const { MongoClient } = require('mongodb');
const uri = "mongodb://localhost:27017"
const mongo_client = new MongoClient(uri)

// Google User Auth
const {OAuth2Client} = require('google-auth-library');
const { getBalance, getAccountHistory, getYearPercentReturn} = require('./data.js');
const { Message, initConversation, getChat, addMessageToChat, getConversationID} = require('./chat.js')
const {runMongo, deleteFriend, getUserByUsername, getUserByEmail, getUserByWalletAddress, registerUser, changeName, search, addFriend, changeRiskTolerance, changeRiskAgg} = require('./user.js')
const CLIENT_ID = process.env.CLIENT_ID;
const client = new OAuth2Client(CLIENT_ID);

const SocketServer = require('websocket').server
const http = require('http');
const { json } = require('express');
const socket_server = http.createServer((req, res) => {})

socket_server.listen(3000, ()=>{
  console.log("Listening on port 3000...")
})

wsServer = new SocketServer({httpServer:socket_server})

const connections = []

wsServer.on('request', (req) => {
  var conversationID = req.resourceURL.query.ConversationID
  const connection = req.accept()
  connection.conversationID = conversationID
  console.log('new connection with conversation ID: ' + conversationID)
  connections.push(connection)

  connection.on('message', mes => {
    connections.forEach(async (element) => {
      if (element != connection && element.conversationID ==  connection.conversationID)
        element.sendUTF(mes.utf8Data)
        var receivedJSON = JSON.parse(mes.utf8Data)
        var message = new Message(receivedJSON.message, receivedJSON.fromUser, receivedJSON.toUser)
        var existingChat = await getChat(receivedJSON.fromUser, receivedJSON.toUser)
        // if (existingChat == null) {
        //   var chat = new Chat(receivedJSON.fromUser, receivedJSON.toUser)
        //   chat.messages.push(message)
        //   await mongo_client.db("tyfw").collection("chat").insertOne(chat)
        //   console.log("added new chat to db")
        // }
        // else {
        addMessageToChat(existingChat.user1, existingChat.user2, message)
          // await mongo_client.db("tyfw").collection("chat").updateOne({$and: [{"user1": existingChat.user1}, {"user2": existingChat.user2}]}, {$addToSet: {"messages": message}})
        // }
        
    })

  connection.on('close', (resCode, des) => {
      console.log('connection closed')
      connections.splice(connections.indexOf(connection), 1)
  })
  })
})


const ml = require('./ml.js');



// verifacation of token provided by frontend
async function googleAuthVerify(token) {
  try {
    const ticket = await client.verifyIdToken({
        idToken: token,
        audience: CLIENT_ID,
    });
    // TODO: Check if more info from the ticket needs to be validated
    const payload = ticket.getPayload();
    const userid = payload['sub']
    console.log("User ID: %s", userid)
    return true

  } catch (err) {
    console.log(err)
    logger.log(String(err))
    return false
  }
  //const payload = ticket.getPayload();
  //const userid = payload['sub'];
}

function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
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
    console.debug("/user/authenticate \n    Time: ", Date.now(), "\n    req.body: ", req.body)

    const existingUser = await getUserByEmail(req.body.email)
    console.debug("existingUser: ", existingUser)

    if (existingUser == null) {
      console.log("User not found")
      res.sendStatus(201)
      return;
    }
    
    const verifyied = await googleAuthVerify(req.body.googleIdToken)
    if (!verifyied) {
      res.sendStatus(401)
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
  console.debug("/user/register \n  Time: ", Date.now(), "\n  req.body: ", req.body)
  try {
      // check if there is another user with the same username
      const existingUser = await getUserByUsername(req.body.username)
      if (existingUser != null) {
        if (req.body.username == "testuser") {
          res.status(200).send("Success")
        } else {
          throw new Error('Username Exists')
        }
      }
      else {
        //create user object
        // const user = new User(req.body.username, req.body.firstName, req.body.lastName, req.body.email, req.body.walletAddress)
        await registerUser(req.body.username, req.body.firstName, req.body.lastName, req.body.email, req.body.walletAddress, req.body.riskTolerance, req.body.riskAgg)
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
  console.debug("/user/leaderboard\n  Time: ", Date.now(), "\n  req.headers: ", req.headers)
  try {
    var leaderboard = []
    const user = await getUserByEmail(req.header("email"))
    console.log(user)
    const user_year_return = await getYearPercentReturn(user.addresses[0])
    leaderboard.push({"user": user.username, "address": user.addresses[0], "value": user_year_return})
    for (let index in user.friends) {
      const friend = await getUserByEmail(user.friends[index])
      var year_return = await getYearPercentReturn(friend.addresses[0])
      leaderboard.push({"user": friend.username, "address":friend.addresses[0], "value": year_return})
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
  console.debug("/user/displaycurruser\n  Time: ", Date.now(), "\n  req.body: ", req.headers)
    try {
        const user = await getUserByEmail(req.header("email"))
        if (user == null) {
          throw new Error('No users found')
        }
        var interval, numPoints;
        if (req.header("time") == "day") {
          interval = "1h"
          numPoints = 24
        } else if (req.header("time") == "week"){
          interval = "1d"
          numPoints = 7
        } else if (req.header("time") == "month") {
          interval = "1d"
          numPoints = 30
        } else if (req.header("time") == "year") {
          interval = "1w"
          numPoints = 52
        }
        const accountHistory = await getAccountHistory(user.addresses[0], interval, numPoints)
        res.status(200).json({"timescale": interval, "data": accountHistory})
        return
    }
    catch (err) {
        console.log(err)
        logger.log(String(err))
        res.sendStatus(400)
        return
    }
})

app.get("/user/displayotheruserbyusername", async (req, res) => {
  console.debug("/user/displayotheruserbyusername\n  Time: ", Date.now(), "\n  req.headers: ", req.headers)
  try {
    const user = await getUserByUsername(req.header("otherUsername"))
      if (user == null) {
        throw new Error('No users found')
      }
      else {
        var interval, numPoints;
        if (req.header("time") == "day") {
          interval = "1h"
          numPoints = 24
        } else if (req.header("time") == "week"){
          interval = "1d"
          numPoints = 7
        } else if (req.header("time") == "month") {
          interval = "1d"
          numPoints = 30
        } else if (req.header("time") == "year") {
          interval = "1w"
          numPoints = 52
        }
        const accountHistory = await getAccountHistory(user.addresses[0], interval, numPoints)
        res.status(200).json({"timescale": interval, "data": accountHistory})
        return
      }
    }
  catch (err) {
    console.log(err)
    logger.log(String(err))
    res.sendStatus(400)
    return
  }
})

app.get("/user/displayotheruserbywalletaddress", async (req, res) => {
  console.debug("/user/displayotheruserbywalletaddress\n  Time: ", Date.now(), "\n  req.headers: ", req.headers)
  try {
    const user = await getUserByWalletAddress(req.header("otherWalletAddress"))
    if (user == null) {
      throw new Error('No users found')
    }
    else {
      var interval, numPoints;
      if (req.header("time") == "day") {
        interval = "1h"
        numPoints = 24
      } else if (req.header("time") == "week"){
        interval = "1d"
        numPoints = 7
      } else if (req.header("time") == "month") {
        interval = "1d"
        numPoints = 30
      } else if (req.header("time") == "year") {
        interval = "1w"
        numPoints = 52
      }
      const accountHistory = await getAccountHistory(user.addresses[0], interval, numPoints)
      res.status(200).json({"timescale": interval, "data": accountHistory})
      return
    }
  }
  catch (err) {
    console.log(err)
    logger.log(String(err))
    res.sendStatus(400)
    return
    }
})


app.post("/user/changename", async (req, res) => {
  console.debug("/user/changename\n  Time: ", Date.now(), "\n  req.body: ", req.body)
  try {
      const user = await getUserByEmail(req.body.email)
      if (user == null) {
      throw new Error('No users found')
    }
      await changeName(req.body.name, req.body.email, req.body.newName)
      res.status(200).send("Success")

  }
  catch (err) {
      console.log(err)
      logger.log(String(err))
      res.sendStatus(400)
  }
})

app.get("/user/search", async (req, res) => {
  console.debug("/user/search\n  Time: ", Date.now(), "\n  req.headers: ", req.headers)
  try {
      const queryMatches = await search(req.header("email"), req.header("queryString"))

      if (queryMatches.length == 0) {
        throw new Error('No users found')
      }
      res.status(200).json({queryMatches})
  }
  catch (err) {
      console.log(err)
      logger.log(String(err))
      res.sendStatus(400)
  }

})

app.post("/user/addbyusername", async (req, res) => {
  console.debug("/user/addbyusername\n  Time: ", Date.now(), "\n  req.body: ", req.body)
  try {
    //check that username exists
      const newFriend = await getUserByUsername(req.body.friendUsername)
      if (newFriend == null) {
        throw new Error('No User with this username')
      }
      else if (newFriend.email == req.body.email) {
        throw new Error('User cannot add themself')
      }
      await addFriend(req.body.email, newFriend.email)
      res.status(200).send("Success")
  }
  catch (err) {
      console.log(err)
      logger.log(String(err))
      res.sendStatus(400)
  }

})

app.post("/user/addbywalletaddress", async (req, res) => {
  console.debug("/user/addbywalletaddress\n Time: ", Date.now(), "\n  req.body: ", req.body)
  try {
    //check that there is a user with the specified wallet address 
      const newFriend = await getUserByWalletAddress(req.body.friendWalletAddress)
      if (newFriend == null) {
        throw new Error('No User with this wallet address')
      }
      else if (newFriend.email == req.body.email) {
        throw new Error('User cannot add themself')
      }
      await addFriend(req.body.email, req.body.newFriendEmail)
      res.status(200).send("Success")
  }
  catch (err) {
      console.log(err)
      logger.log(String(err))
      res.sendStatus(400)
  }
})


app.get("/user/getfirstname", async (req, res) => {
  try {
      const user = await getUserByEmail(req.header("email"))
      if (user == null) {
        throw new Error('No users found')
      }
      res.status(200).send(user.firstname)

  }
  catch (err) {
      console.log(err)
      res.sendStatus(400)
  }
})

app.get("/user/getlastname", async (req, res) => {
  try {
      const user = await getUserByEmail(req.header("email"))
      if (user == null) {
        throw new Error('No users found')
      }
      res.status(200).send(user.lastname)

  }
  catch (err) {
      console.log(err)
      res.sendStatus(400)
  }
})

app.get("/user/getwalletaddress", async (req, res) => {
  try {
      const user = await getUserByUsername(req.header("username"))
      if (user == null) {
        throw new Error('No users found')
      }
      else {
        res.status(200).json({"addresses": user.addresses})
      }
  }
  catch (err) {
      console.log(err)
      logger.log(String(err))
      res.sendStatus(400)
  }
});
      
app.get("/user/getbalance", async (req, res) => {
  console.debug("/user/getbalance\n  Time: ", Date.now(), "\n  req.headers: ", req.headers)
  try {
      const user = await getUserByEmail(req.header("email"))
      var user_balance = 0
      for (let i = 0; i < user.addresses.length; i = i+1) {
              var balance = await getBalance(user.addresses[i])
        user_balance += balance
      }
      res.status(200).json({"balance": user_balance})
      return
      }
  catch (err) {
      console.log(err)
      logger.log(String(err))
      res.sendStatus(400)
      return
  }
});

app.get("/user/getuser", async (req, res) => {
  console.debug("/user/getuser\n  Time: ", Date.now(), "\n  req.headers: ", req.headers)

  try {
    const user = await getUserByEmail(req.header("email"))
    if (user == null) {
      throw new Error('No users found')
    }
    res.status(200).json({"data": user})
  } catch (err) {
    console.log(err)
    logger.log(String(err))
    res.sendStatus(400)
  }
});

app.get("/user/getprediction", async (req, res) => {
  console.debug("/user/getprediction\n  Time: ", Date.now(), "\n  req.headers: ", req.headers)
  const riskTolerance = req.header("riskTolerance")
  const riskAgg = req.header("riskAgg")
  const user = await getUserByEmail(req.header("email"))
  if (user.riskTolerance != riskTolerance) {
    await changeRiskTolerance(req.header("email"), riskTolerance)
  }
  if (user.riskAgg != riskAgg) {
    await changeRiskAgg(req.header("email"), riskAgg)
  }
  const predict = await ml.predict(riskTolerance);
  res.status(200).json(predict)
});

app.get("/user/getfriends", async (req, res) => {
  console.debug("/user/getfriends\n  Time: ", Date.now(), "\n  req.headers: ", req.headers)
  const user = await getUserByEmail(req.header("email"))
  try {
    var usernames = [];
    for (let i = 0; i < user.friends.length; i++) {
      var foundUser = await getUserByEmail(user.friends[i])
      usernames.push(foundUser.username)
    }
    res.status(200).json({"friends": usernames})
  } catch (err) {
    console.log(err)
    logger.log(String(err))
    res.sendStatus(400)
  }
});


app.get("/user/chathistory", async (req, res) => {
  console.debug("/user/chathistory\n\
  Time: ", Date.now(), "\n\
  req.headers: ", req.headers)

  try {
    const existingChat = await getChat(req.header("fromUser"), req.header("toUser"))
    if (existingChat == null) {
      res.status(404)
    }
    res.status(200).json(existingChat.messages)
  } catch (err) {
    console.log(err)
    logger.log(String(err))
    res.sendStatus(400)
  }
});

app.get("/user/conversation_id", async (req, res) => {
  console.debug("/user/conversation_id\n\
  Time: ", Date.now(), "\n\
  req.headers: ", req.headers)

  try {
    const conversation_id = await getConversationID(req.header("fromUser"), req.header("toUser")) 
    console.log(conversation_id)
    if (conversation_id == null) {
      res.status(404)
    }
    res.status(200).json(conversation_id)
  } catch (err) {
    console.log(err)
    logger.log(String(err))
    res.sendStatus(400)
  }
});

app.post("/user/init_conversation", async (req, res) => {
  console.debug("/user/init_conversation\n\
  Time: ", Date.now(), "\n\
  req.headers: ", req.headers)

  try {
    await initConversation(req.body.fromUser, req.body.toUser)
    res.sendStatus(200)
  } catch (err) {
    console.log(err)
    logger.log(String(err))
    res.sendStatus(400)
  }
});

app.delete("/user/delete_friend", async (req, res) => {
  console.debug("/user/delete_friend\n\
  Time: ", Date.now(), "\n\
  req.headers: ", req.headers)

  try {
    await deleteFriend(req.header("email"), req.header("friend"))
    res.sendStatus(200)
  } catch (err) {
    console.log(err)
    logger.log(String(err))
    res.sendStatus(400)
  }
  
  
})

module.exports = server

runMongo()
