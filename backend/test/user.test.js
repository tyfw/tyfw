const user = require('../../src/user.js')

describe ("POST /user/register", () => {
    describe("unique username and email", () => {
        //should succesfully register user
        //should respond w 200 status code
        test("should respond w 200 status code", async () => {
            const random = Math.random().toString(36).replace(/[^a-z]+/g, '').substr(0, 5)
            const response = await request(server).post("/user/register").send({
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
            const response = await request(server).post("/user/register").send({
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
            const response = await request(server).post("/user/authenticate").send({
                googleIdToken : "fakeToken",
                email: "tesla@mail.com"
        })
            expect(response.statusCode).toBe(401)
        })
    })
    describe("failed authentication due to user not found", () => {
        test("should respond w 400 status code", async () => {
            const response = await request(server).post("/user/authenticate").send({
                googleIdToken : "fakeToken",
                email: "notauser@mail.com"
        })
            expect(response.statusCode).toBe(400)
        })
    })
})