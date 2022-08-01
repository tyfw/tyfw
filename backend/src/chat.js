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

const getChatHistory = async (fromUser, toUser) => {
    const existingChat = await mongo_client.db("tyfw").collection("chat").findOne({$or: [{"user1": fromUser, "user2": toUser}, {"user1": toUser, "user2": fromUser}]})
    return existingChat
}

const initConversation  = async (fromUser, toUser) => {
    await mongo_client.db("tyfw").collection("chat").insertOne(new Chat)
}
