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

describe ("GET /user/leaderboard", () => {
    describe("Get leaderboard for a user with no friends added", () => {
        test("should respond w 200 status code", async () => {
            const response = await request(app).get("/user/leaderboard").set(
                "email", "tesla@mail.com"
            )
            expect(response.statusCode).toBe(200)

        })
    })
    describe("Get leaderboard for a user with 2 friends added", () => {
        test("should respond w 200 status code", async () => {
            const response = await request(app).get("/user/leaderboard").set(
                "email", "zeph@mail.com"
            )
            expect(response.statusCode).toBe(200)

        })
    })
    describe("Get leaderboard for a user not in DB", () => {
        test("should respond w 404 status code", async () => {
            const response = await request(app).get("/user/leaderboard").set(
                "email", "notauser@mail.com"
            )
            expect(response.statusCode).toBe(400)

        })
    })
})