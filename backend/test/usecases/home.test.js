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

describe ("GET /user/getfirstname", () => {
    describe("get first name for user", () => {
        test("should respond w 200 status code", async () => {
            const response = await (await request(app).get("/user/getfirstname").set("email", "tesla@mail.com"))
            expect(response.statusCode).toBe(200)
        })
    })
    describe("get first name for non-existing user", () => {
        test("should respond w 400 status code", async () => {
            const response = await (await request(app).get("/user/getfirstname").set("email", "notauser@mail.com"))
            expect(response.statusCode).toBe(400)
        })
    })
})

describe ("GET /user/getlastname", () => {
    describe("get last name for user", () => {
        test("should respond w 200 status code", async () => {
            const response = await (await request(app).get("/user/getlastname").set("email", "tesla@mail.com"))
            expect(response.statusCode).toBe(200)
        })
    })
    describe("get last name for non-existing user", () => {
        test("should respond w 400 status code", async () => {
            const response = await (await request(app).get("/user/getlastname").set("email", "notauser@mail.com"))
            expect(response.statusCode).toBe(400)
        })
    })
})

describe ("GET /user/getwalletaddress", () => {
    describe("get wallet address for user", () => {
        test("should respond w 200 status code", async () => {
            const response = await (await request(app).get("/user/getwalletaddress").set("username", "Tesla"))
            expect(response.statusCode).toBe(200)
        })
    })
    describe("get last name for non-existing user", () => {
        test("should respond w 400 status code", async () => {
            const response = await (await request(app).get("/user/getwalletaddress").set("email", "notauser@mail.com"))
            expect(response.statusCode).toBe(400)
        })
    })
})

describe ("GET /user/getuser", () => {
    describe("get user", () => {
        test("should respond w 200 status code", async () => {
            const response = await (await request(app).get("/user/getuser").set("email", "tesla@mail.com"))
            expect(response.statusCode).toBe(200)
        })
    })
    describe("get non-existing user", () => {
        test("should respond w 400 status code", async () => {
            const response = await (await request(app).get("/user/getuser").set("email", "notauser@mail.com"))
            expect(response.statusCode).toBe(400)
        })
    })
})
