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
      const teslaMockUser = new user.User("Tesla", "Nikola", "Tesla", "tesla@mail.com", ["0x9BF4001d307dFd62B26A2F1307ee0C0307632d59"])
      const zephUser = new user.User("zeph", "Zeph", "Ko", "zeph@mail.com", ["0xDA9dfA130Df4dE4673b89022EE50ff26f6EA73Cf"])
      db.collection("users").insertOne(teslaMockUser)
      db.collection("users").insertOne(zephUser)
      db.collection("users").updateOne({"email": zephUser.email}, {$addToSet: {friends: teslaMockUser.email}})

      
    })

describe ("GET /user/displayotheruserbyusername", () => {
    describe("Get other user profile by username with day time scale", () => {
        test("should respond w 200 status code", async () => {
            const response = await request(app).get("/user/displayotheruserbyusername").set("otherUsername", "Tesla").set("time", "day")
            expect(response.statusCode).toBe(200)

        })
    })
    describe("Get other user profile by username with week time scale", () => {
        test("should respond w 200 status code", async () => {
            const response = await request(app).get("/user/displayotheruserbyusername").set("otherUsername", "Tesla").set("time", "week")
            expect(response.statusCode).toBe(200)

        })
    })
    describe("Get other user profile by username with month time scale", () => {
        test("should respond w 200 status code", async () => {
            const response = await request(app).get("/user/displayotheruserbyusername").set("otherUsername", "Tesla").set("time", "month")
            expect(response.statusCode).toBe(200)

        })
    })
    describe("Get other user profile by username with year time scale", () => {
        test("should respond w 200 status code", async () => {
            const response = await request(app).get("/user/displayotheruserbyusername").set("otherUsername", "Tesla").set("time", "year")
            expect(response.statusCode).toBe(200)

        })
    })
    describe("Get other user profile by wallet address (day)", () => {
        test("should respond w 200 status code", async () => {
            const response = await request(app).get("/user/displayotheruserbywalletaddress").set("otherWalletAddress", "0x9BF4001d307dFd62B26A2F1307ee0C0307632d59").set("time", "day")
            expect(response.statusCode).toBe(200)

        })
    })
    describe("Get other user profile by wallet address (week)", () => {
        test("should respond w 200 status code", async () => {
            const response = await request(app).get("/user/displayotheruserbywalletaddress").set("otherWalletAddress", "0x9BF4001d307dFd62B26A2F1307ee0C0307632d59").set("time", "week")
            expect(response.statusCode).toBe(200)

        })
    })
    describe("Get other user profile by wallet address (month)", () => {
        test("should respond w 200 status code", async () => {
            const response = await request(app).get("/user/displayotheruserbywalletaddress").set("otherWalletAddress", "0x9BF4001d307dFd62B26A2F1307ee0C0307632d59").set("time", "month")
            expect(response.statusCode).toBe(200)

        })
    })
    describe("Get other user profile by wallet address (year)", () => {
        test("should respond w 200 status code", async () => {
            const response = await request(app).get("/user/displayotheruserbywalletaddress").set("otherWalletAddress", "0x9BF4001d307dFd62B26A2F1307ee0C0307632d59").set("time", "year")
            expect(response.statusCode).toBe(200)

        })
    })
    describe("Get other user profile for a user not in DB using username", () => {
        test("should respond w 400 status code", async () => {
            const response = await request(app).get("/user/displayotheruserbyusername").set("otherUsername", "notauser")
            expect(response.statusCode).toBe(400)

        })
    })
    describe("Get other user profile for a user not in DB using walletaddress", () => {
        test("should respond w 400 status code", async () => {
            const response = await request(app).get("/user/displayotheruserbywalletaddress").set("otherWalletAddress", "notawalletaddress")
            expect(response.statusCode).toBe(400)

        })
    })
})

describe ("GET /user/displaycurruser", () => {
    describe("Get current user profile by username", () => {
        test("should respond w 200 status code", async () => {
            const response = await request(app).get("/user/displaycurruser").set("email", "tesla@mail.com").set("time", "day")
            expect(response.statusCode).toBe(200)

        })
        test("should respond w 200 status code", async () => {
            const response = await request(app).get("/user/displaycurruser").set("email", "tesla@mail.com").set("time", "week")
            expect(response.statusCode).toBe(200)

        })
        test("should respond w 200 status code", async () => {
            const response = await request(app).get("/user/displaycurruser").set("email", "tesla@mail.com").set("time", "month")
            expect(response.statusCode).toBe(200)

        })
        test("should respond w 200 status code", async () => {
            const response = await request(app).get("/user/displaycurruser").set("email", "tesla@mail.com").set("time", "year")
            expect(response.statusCode).toBe(200)

        })
    })
    describe("Get current user profile for a user not in DB", () => {
        test("should respond w 400 status code", async () => {
            const response = await request(app).get("/user/displaycurruser").set(
                "username", "notauser"
            )
            expect(response.statusCode).toBe(400)

        })
        })
    })
describe ("POST /user/addbyusername and /user/addbywalletaddress", () => {
    describe("add existing user by username", () => {
        test("should respond w 200 status code", async () => {
            const random = Math.random().toString(36).replace(/[^a-z]+/g, '').substr(0, 5)
            await request(app).post("/user/register").send({
                username: random,
                email: random + "@mail.com",
                firstname: "test",
                lastname: "user",
                walletaddress: ["0x9bf4001d307dfd62b26a2f1307ee0c0307632d59"]
            })
            const response = await request(app).post("/user/addbyusername").send({
                friendUsername: "Tesla",
                email: random + "@mail.com",
            })
            expect(response.statusCode).toBe(200)
        })
    })

    describe("add existing user by wallet address", () => {
        test("should respond w 200 status code", async () => {
            const random = Math.random().toString(36).replace(/[^a-z]+/g, '').substr(0, 5)
            await request(app).post("/user/register").send({
                username: random,
                email: random + "@mail.com",
                firstname: "test",
                lastname: "user",
                walletaddress: ["0x9bf4001d307dfd62b26a2f1307ee0c0307632d59"]
            })
            const response = await request(app).post("/user/addbywalletaddress").send({
                friendwalletaddress: "0x9bf4001d307dfd62b26a2f1307ee0c0307632d59",
                email: random + "@mail.com",
            })
            expect(response.statusCode).toBe(200)
            
        })
    })
    describe("add currently logged in user", () => {
        test("should respond w 400 status code", async () => {
            const response = await request(app).post("/user/addbyusername").send({
                friendusername: "Tesla",
                email: "tesla@mail.com",
            })
            expect(response.statusCode).toBe(400)
            
        })
    })
    describe("add a non-existing user by username", () => {
        test("should respond w 400 status code", async () => {
            const response = await request(app).post("/user/addbyusername").send({
                friendusername: "notauser",
                email: "tesla@mail.com",
            })
            expect(response.statusCode).toBe(400)
        })
    })
    describe("add a non-existing user by walletaddress", () => {
        test("should respond w 400 status code", async () => {
            const response = await request(app).post("/user/addbywalletaddress").send({
                friendWalletaAddress: "fakewalletaddress",
                email: "tesla@mail.com",
            })
            expect(response.statusCode).toBe(200)
        })
    })
})

describe ("POST /user/changename", () => {
    describe("change first name for user", () => {
        test("should respond w 200 status code", async () => {
            const response = await request(app).post("/user/changename").send({
                name: "first",
                email: "tesla@mail.com",
                newName: "Nikola"
            })
            expect(response.statusCode).toBe(200)
        })
    })
    describe("change last name for user", () => {
        test("should respond w 200 status code", async () => {
            const response = await request(app).post("/user/changename").send({
                name: "last",
                email: "tesla@mail.com",
                newName: "Tesla"
            })
            expect(response.statusCode).toBe(200)
        })
    })
    describe("change name for non-existing user", () => {
        test("should respond w 200 status code", async () => {
            const response = await request(app).post("/user/changename").send({
                name: "last",
                email: "notauser@mail.com",
                newName: "newname"
            })
            expect(response.statusCode).toBe(400)
        })
    })
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

describe ("GET /user/getprediction", () => {
    describe("test changing risk tolerance and risk agg", () => {
        test("should respond w 200 status code", async () => {
            const response = await (await request(app).get("/user/getprediction").set("email", "tesla@mail.com").set("riskAgg", 50).set("riskTolerance", 50))
            expect(response.statusCode).toBe(200)
        })
    })
})

describe ("DELETE /user/delete_friend", () => {
    describe("test deleting friend", () => {
        test("should respond w 200 status code", async () => {
            const response =  await (await request(app).delete("/user/delete_friend").set("email", "zeph@mail.com").set("friend", "Tesla"))
            expect(response.statusCode).toBe(200)
        })
    })
})