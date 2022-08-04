const request = require('supertest')
const server = require('../../src/server.js')

const app = request(server)

describe ("GET /user/displayotheruserbyusername", () => {
    describe("Get other user profile by username with day time scale", () => {
        test("should respond w 200 status code", async () => {
            const response = await app.get("/user/displayotheruserbyusername").set("otherUsername", "Tesla").set("time", "day")
            expect(response.statusCode).toBe(200)

        })
    })
    describe("Get other user profile by username with week time scale", () => {
        test("should respond w 200 status code", async () => {
            const response = await app.get("/user/displayotheruserbyusername").set("otherUsername", "Tesla").set("time", "week")
            expect(response.statusCode).toBe(200)

        })
    })
    describe("Get other user profile by username with month time scale", () => {
        test("should respond w 200 status code", async () => {
            const response = await app.get("/user/displayotheruserbyusername").set("otherUsername", "Tesla").set("time", "month")
            expect(response.statusCode).toBe(200)

        })
    })
    describe("Get other user profile by username with year time scale", () => {
        test("should respond w 200 status code", async () => {
            const response = await app.get("/user/displayotheruserbyusername").set("otherUsername", "Tesla").set("time", "year")
            expect(response.statusCode).toBe(200)

        })
    })
    describe("Get other user profile by wallet address (day)", () => {
        test("should respond w 200 status code", async () => {
            const response = await app.get("/user/displayotheruserbywalletaddress").set("otherWalletAddress", "0x9BF4001d307dFd62B26A2F1307ee0C0307632d59").set("time", "day")
            expect(response.statusCode).toBe(200)

        })
    })
    describe("Get other user profile by wallet address (week)", () => {
        test("should respond w 200 status code", async () => {
            const response = await app.get("/user/displayotheruserbywalletaddress").set("otherWalletAddress", "0x9BF4001d307dFd62B26A2F1307ee0C0307632d59").set("time", "week")
            expect(response.statusCode).toBe(200)

        })
    })
    describe("Get other user profile by wallet address (month)", () => {
        test("should respond w 200 status code", async () => {
            const response = await app.get("/user/displayotheruserbywalletaddress").set("otherWalletAddress", "0x9BF4001d307dFd62B26A2F1307ee0C0307632d59").set("time", "month")
            expect(response.statusCode).toBe(200)

        })
    })
    describe("Get other user profile by wallet address (year)", () => {
        test("should respond w 200 status code", async () => {
            const response = await app.get("/user/displayotheruserbywalletaddress").set("otherWalletAddress", "0x9BF4001d307dFd62B26A2F1307ee0C0307632d59").set("time", "year")
            expect(response.statusCode).toBe(200)

        })
    })
    describe("Get other user profile for a user not in DB using username", () => {
        test("should respond w 400 status code", async () => {
            const response = await app.get("/user/displayotheruserbyusername").set("otherUsername", "notauser")
            expect(response.statusCode).toBe(400)

        })
    })
    describe("Get other user profile for a user not in DB using walletaddress", () => {
        test("should respond w 400 status code", async () => {
            const response = await app.get("/user/displayotheruserbywalletaddress").set("otherWalletAddress", "notawalletaddress")
            expect(response.statusCode).toBe(400)

        })
    })
})

describe ("GET /user/displaycurruser", () => {
    describe("Get current user profile by username", () => {
        test("should respond w 200 status code", async () => {
            const response = await app.get("/user/displaycurruser").set("email", "tesla@mail.com").set("time", "day")
            expect(response.statusCode).toBe(200)

        })
        test("should respond w 200 status code", async () => {
            const response = await app.get("/user/displaycurruser").set("email", "tesla@mail.com").set("time", "week")
            expect(response.statusCode).toBe(200)

        })
        test("should respond w 200 status code", async () => {
            const response = await app.get("/user/displaycurruser").set("email", "tesla@mail.com").set("time", "month")
            expect(response.statusCode).toBe(200)

        })
        test("should respond w 200 status code", async () => {
            const response = await app.get("/user/displaycurruser").set("email", "tesla@mail.com").set("time", "year")
            expect(response.statusCode).toBe(200)

        })
    })
    describe("Get current user profile for a user not in DB", () => {
        test("should respond w 400 status code", async () => {
            const response = await app.get("/user/displaycurruser").set(
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
            await app.post("/user/register").send({
                username: random,
                email: random + "@mail.com",
                firstname: "test",
                lastname: "user",
                walletaddress: ["0x9bf4001d307dfd62b26a2f1307ee0c0307632d59"]
            })
            const response = await app.post("/user/addbyusername").send({
                friendUsername: "Tesla",
                email: random + "@mail.com",
            })
            expect(response.statusCode).toBe(200)
        })
    })

    describe("add existing user by wallet address", () => {
        test("should respond w 200 status code", async () => {
            const random = Math.random().toString(36).replace(/[^a-z]+/g, '').substr(0, 5)
            await app.post("/user/register").send({
                username: random,
                email: random + "@mail.com",
                firstname: "test",
                lastname: "user",
                walletaddress: ["0x9bf4001d307dfd62b26a2f1307ee0c0307632d59"]
            })
            const response = await app.post("/user/addbywalletaddress").send({
                friendwalletaddress: "0x9bf4001d307dfd62b26a2f1307ee0c0307632d59",
                email: random + "@mail.com",
            })
            expect(response.statusCode).toBe(200)
            
        })
    })
    describe("add currently logged in user", () => {
        test("should respond w 400 status code", async () => {
            const response = await app.post("/user/addbyusername").send({
                friendusername: "Tesla",
                email: "tesla@mail.com",
            })
            expect(response.statusCode).toBe(400)
            
        })
    })
    describe("add a non-existing user by username", () => {
        test("should respond w 400 status code", async () => {
            const response = await app.post("/user/addbyusername").send({
                friendusername: "notauser",
                email: "tesla@mail.com",
            })
            expect(response.statusCode).toBe(400)
        })
    })
    describe("add a non-existing user by walletaddress", () => {
        test("should respond w 400 status code", async () => {
            const response = await app.post("/user/addbywalletaddress").send({
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
            const response = await app.post("/user/changename").send({
                name: "first",
                email: "tesla@mail.com",
                newName: "Nikola"
            })
            expect(response.statusCode).toBe(200)
        })
    })
    describe("change last name for user", () => {
        test("should respond w 200 status code", async () => {
            const response = await app.post("/user/changename").send({
                name: "last",
                email: "tesla@mail.com",
                newName: "Tesla"
            })
            expect(response.statusCode).toBe(200)
        })
    })
    describe("change name for non-existing user", () => {
        test("should respond w 200 status code", async () => {
            const response = await app.post("/user/changename").send({
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
            const response = await (await app.get("/user/getfirstname").set("email", "tesla@mail.com"))
            expect(response.statusCode).toBe(200)
        })
    })
    describe("get first name for non-existing user", () => {
        test("should respond w 400 status code", async () => {
            const response = await (await app.get("/user/getfirstname").set("email", "notauser@mail.com"))
            expect(response.statusCode).toBe(400)
        })
    })
})

describe ("GET /user/getlastname", () => {
    describe("get last name for user", () => {
        test("should respond w 200 status code", async () => {
            const response = await (await app.get("/user/getlastname").set("email", "tesla@mail.com"))
            expect(response.statusCode).toBe(200)
        })
    })
    describe("get last name for non-existing user", () => {
        test("should respond w 400 status code", async () => {
            const response = await (await app.get("/user/getlastname").set("email", "notauser@mail.com"))
            expect(response.statusCode).toBe(400)
        })
    })
})