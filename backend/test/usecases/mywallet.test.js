const request = require('supertest')
const server = require('../../src/server.js')

describe ("GET /user/displaycurruser", () => {
    describe("Get current user profile by username", () => {
        test("should respond w 200 status code", async () => {
            const response = await request(server).get("/user/displaycurruser").set("email", "tesla@mail.com").set("time", "day")
            expect(response.statusCode).toBe(200)

        })
        test("should respond w 200 status code", async () => {
            const response = await request(server).get("/user/displaycurruser").set("email", "tesla@mail.com").set("time", "week")
            expect(response.statusCode).toBe(200)

        })
        test("should respond w 200 status code", async () => {
            const response = await request(server).get("/user/displaycurruser").set("email", "tesla@mail.com").set("time", "month")
            expect(response.statusCode).toBe(200)

        })
        test("should respond w 200 status code", async () => {
            const response = await request(server).get("/user/displaycurruser").set("email", "tesla@mail.com").set("time", "year")
            expect(response.statusCode).toBe(200)

        })
    })
    describe("Get current user profile for a user not in DB", () => {
        test("should respond w 400 status code", async () => {
            const response = await request(server).get("/user/displaycurruser").set(
                "username", "notauser"
            )
            expect(response.statusCode).toBe(400)

        })
        })
    })
describe ("GET /user/getbalance", () => {
    describe("get balance for user", () => {
        test("should respond w 200 status code", async () => {
            const response = await (await request(server).get("/user/getbalance").set("email", "tesla@mail.com"))
            expect(response.statusCode).toBe(200)
        })
    })
    describe("get balance for non-existing user", () => {
        test("should respond w 400 status code", async () => {
            const response = await (await request(server).get("/user/getbalance").set("email", "notauser@mail.com"))
            expect(response.statusCode).toBe(400)
        })
    })
})