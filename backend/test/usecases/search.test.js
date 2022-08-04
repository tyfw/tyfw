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


describe ("GET /user/search", () => {
    describe("search for users with query matching 1 or more users", () => {
        test("should respond w 200 status code", async () => {
            const response = await (await request(app).get("/user/search").set("queryString", "Tesla").set("email", "charles@mail.com"))
            expect(response.statusCode).toBe(200)

        })
    })
    describe("search for users with query matching no users", () => {
        test("should respond w 404 status code", async () => {
            const response = await (await request(app).get("/user/search").set("queryString", "notinthedatabase").set("email", "tesla@mail.com"))
            expect(response.statusCode).toBe(400)

        })
    })
})