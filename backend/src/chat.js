// Citation for chat functionality:
// This was the template for the chat code
// https://github.com/heyletscode/Chat-App-In-Android-And-NodeJS-Using-WebSockets
// https://www.youtube.com/watch?v=7ZbqvnbrFjQ&list=PLsOU6EOcj51ehxchZXdlL06wkATIh-6z3

const { MongoClient } = require('mongodb')
const uri = "mongodb://localhost:27017"
const mongo_client = new MongoClient(uri)

class Chat {
    constructor(user1, user2) {
      this.user1 = user1
      this.user2 = user2
      this.messages = [];
    }
}

class Message {
    constructor(message, fromUser, toUser) {
      this.message = message;
      this.fromUser = fromUser;
      this.toUser = toUser;
    }
}

const getChat = async (fromUser, toUser) => {
    const existingChat = await mongo_client.db("tyfw").collection("chat").findOne({$or: [{"user1": fromUser, "user2": toUser}, {"user1": toUser, "user2": fromUser}]})
    return existingChat
}

const initConversation  = async (fromUser, toUser) => {
    await mongo_client.db("tyfw").collection("chat").insertOne(new Chat(fromUser, toUser))
}

const getConversationID = async (fromUser, toUser) => {
    var existingChat = await mongo_client.db("tyfw").collection("chat").findOne({$or: [{"user1": fromUser, "user2": toUser}, {"user1": toUser, "user2": fromUser}]})
    if (existingChat == null) {
        await initConversation(fromUser, toUser)
        existingChat = await getChat(fromUser, toUser)
    }
    console.log(existingChat._id.toString())
    return existingChat._id.toString()
}

const addMessageToChat  = async (user1, user2, message) => {
    await mongo_client.db("tyfw").collection("chat").updateOne({$and: [{user1}, {user2}]}, {$addToSet: {"messages": message}})
}

module.exports = {
    Message,
    Chat,
    getChat,
    addMessageToChat,
    getConversationID,
    initConversation
}
