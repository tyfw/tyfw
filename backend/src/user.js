const { MongoClient } = require('mongodb')
const uri = "mongodb://localhost:27017"
// const uri = global.__MONGO_URI__
const mongo_client = new MongoClient(uri)

class User {
    constructor(username, firstname, lastname, email, addresses, risktolerance, riskagg) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.addresses = addresses; 
        this.friends = [];
        this.risktolerance = risktolerance;
        this.riskagg = riskagg;
    }
}


async function runMongo() {
    // try {
      // Connect the client to the server
      await mongo_client.connect();
      // Establish and verify connection
      await mongo_client.db("admin").command({ ping: 1 });
      console.log("Connected successfully to database");
    // } catch(err) {
    //     // console.log(err)
    //     // await mongo_client.close()
    // } 
  }

const getUserByEmail = async (email) => {
    const existingUser = await mongo_client.db("tyfw").collection("users").findOne({email})
    return existingUser
}

const getUserByUsername = async (username) => {
    const existingUser = await mongo_client.db("tyfw").collection("users").findOne({username})
    return existingUser
}

const getUserByWalletAddress = async (address) => {
    const user = await mongo_client.db("tyfw").collection("users").findOne({"addresses": address}) 
    return user
}

const registerUser = async (username, firstName, lastName, email, walletAddress, risktolerance, riskAgg) => {
    const user = new User(username, firstName, lastName, email, walletAddress, risktolerance, riskAgg)
    await mongo_client.db("tyfw").collection("users").insertOne(user)
    return true
}

const changeName = async (email, name, newName) => {
      if (name == "first") {
        await mongo_client.db("tyfw").collection("users").updateOne({email}, {$set: {firstname: newName}})
        return true
      }
      else if (name == "last") {
        await mongo_client.db("tyfw").collection("users").updateOne({email}, {$set: {lastname: newName}})
        return true
      }
      else {
        return false
      }
}

const changeRiskTolerance = async(email, riskTolerance) => {
  await mongo_client.db("tyfw").collection("users").updateOne({email}, {$set: {risktolerance: riskTolerance}})
}

const changeRiskAgg = async(email, riskAgg) => {
  await mongo_client.db("tyfw").collection("users").updateOne({email}, {$set: {riskagg: riskAgg}})
}

const search = async(email, queryString) => {
      var query = {
        $and : [
          {"email": {$not: {$regex: email}}},
          {$or:[
            {"username": {$regex: queryString, $options: "$i"}},
            {"addresses": {$regex: queryString, $options: "$i"}}
          ]}
        ]
      }
      const queryMatches = await mongo_client.db("tyfw").collection("users").find(query).project({username: 1, addresses: 1, _id: 0}).toArray()
      return queryMatches
}

const addFriend = async(email, newFriendEmail) => {
    await mongo_client.db("tyfw").collection("users").updateOne({email}, {$addToSet: {friends: newFriendEmail}})
    return true
}

const deleteFriend  = async(email, friend) => {
  friendUser = await getUserByUsername(friend)
  await mongo_client.db("tyfw").collection("users").updateOne({email}, {$pull: {friends: friendUser.email}})
}

module.exports = {
    runMongo,
    getUserByUsername, 
    getUserByEmail,
    registerUser,
    getUserByWalletAddress,
    changeName,
    changeRiskTolerance,
    changeRiskAgg,
    search,
    addFriend,
    deleteFriend,
    User
}