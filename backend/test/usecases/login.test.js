const request = require('supertest')
const app = require('../../src/server.js')
const user = require('../../src/user.js')
const {MongoClient} = require('mongodb')
let connection;
let db;

beforeAll(async () => {
    console.log(MongoClient)
    console.log(global.__MONGO_URI__)
    console.log(process.env.MONGO_URL)
    // connection = await MongoClient.connect(global.__MONGO_URI__, {
    connection = await MongoClient.connect("mongodb://localhost:27017", {
        useNewUrlParser: true,
        useUnifiedTopology: true,
      });
      db = await connection.db("tyfw");
      await db.collection("users").deleteMany({});
      const teslaMockUser = new user.User("Tesla", "Nikola", "Tesla", "tesla@mail.com", ["0xa5cD18A9c0028853Cac10c778B03001e2c18aFF4"])
      const zephUser = new user.User("zeph", "Zeph", "Ko", "zeph@mail.com", ["0xDA9dfA130Df4dE4673b89022EE50ff26f6EA73Cf"])
      db.collection("users").insertOne(teslaMockUser)
      db.collection("users").insertOne(zephUser)
      
    })

describe ("POST /user/register", () => {
    describe("unique username and email", () => {
        //should succesfully register user
        //should respond w 200 status code
        test("should respond w 200 status code", async () => {
            const random = Math.random().toString(36).replace(/[^a-z]+/g, '').substr(0, 5)
            const response = await request(app).post("/user/register").send({
                username: random,
                email: random + "@mail.com",
                firstName: "test",
                lastName: "user",
                walletAddress: ["0x9BF4001d307dFd62B26A2F1307ee0C0307632d59"]
            })
            expect(response.statusCode).toBe(200)
        })
    })

    describe("non-unique username and non-unique email", () => {
        test("should respond w 401 status code", async () => {
            const response = await request(app).post("/user/register").send({
                username: "Tesla",
                email: "tesla@mail.com",
                firstName: "Nikola",
                lastName: "Tesla",
                walletAddress: ["0x9BF4001d307dFd62B26A2F1307ee0C0307632d59"]
            })
            expect(response.statusCode).toBe(401)
            
        })
        
    })
    
})

describe ("POST /user/authenticate", () => {
    describe("failed authentication due to invalid token", () => {
        test("should respond w 401 status code", async () => {
            const response = await request(app).post("/user/authenticate").send({
                googleIdToken : "fakeToken",
                email: "tesla@mail.com"
        })
            expect(response.statusCode).toBe(401)
        })
    })
    describe("failed authentication due to user not found", () => {
        test("should respond w 201 status code", async () => {
            const response = await request(app).post("/user/authenticate").send({
                googleIdToken : "fakeToken",
                email: "notauser@mail.com"
        })
            expect(response.statusCode).toBe(201)
        })
    })
})