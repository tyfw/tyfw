const request = require('supertest')
const server = require('../../src/server.js')

describe ("GET /user/getfirstname", () => {
    describe("get first name for user", () => {
        test("should respond w 200 status code", async () => {
            const response = await (await request(server).get("/user/getfirstname").set("email", "tesla@mail.com"))
            expect(response.statusCode).toBe(200)
        })
    })
    describe("get first name for non-existing user", () => {
        test("should respond w 400 status code", async () => {
            const response = await (await request(server).get("/user/getfirstname").set("email", "notauser@mail.com"))
            expect(response.statusCode).toBe(400)
        })
    })
})

describe ("GET /user/getlastname", () => {
    describe("get last name for user", () => {
        test("should respond w 200 status code", async () => {
            const response = await (await request(server).get("/user/getlastname").set("email", "tesla@mail.com"))
            expect(response.statusCode).toBe(200)
        })
    })
    describe("get last name for non-existing user", () => {
        test("should respond w 400 status code", async () => {
            const response = await (await request(server).get("/user/getlastname").set("email", "notauser@mail.com"))
            expect(response.statusCode).toBe(400)
        })
    })
})

describe ("GET /user/getwalletaddress", () => {
    describe("get wallet address for user", () => {
        test("should respond w 200 status code", async () => {
            const response = await (await request(server).get("/user/getwalletaddress").set("username", "Tesla"))
            expect(response.statusCode).toBe(200)
        })
    })
    describe("get last name for non-existing user", () => {
        test("should respond w 400 status code", async () => {
            const response = await (await request(server).get("/user/getwalletaddress").set("email", "notauser@mail.com"))
            expect(response.statusCode).toBe(400)
        })
    })
})

describe ("GET /user/getuser", () => {
    describe("get user", () => {
        test("should respond w 200 status code", async () => {
            const response = await (await request(server).get("/user/getuser").set("email", "tesla@mail.com"))
            expect(response.statusCode).toBe(200)
        })
    })
    describe("get non-existing user", () => {
        test("should respond w 400 status code", async () => {
            const response = await (await request(server).get("/user/getuser").set("email", "notauser@mail.com"))
            expect(response.statusCode).toBe(400)
        })
    })
})
