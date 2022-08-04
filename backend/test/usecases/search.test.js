const request = require('supertest')
const server = require('../../src/server.js')
const app = request(server)

describe ("GET /user/search", () => {
    describe("search for users with query matching 1 or more users", () => {
        test("should respond w 200 status code", async () => {
            const response = await (await app.get("/user/search").set("queryString", "Tesla").set("email", "charles@mail.com"))
            expect(response.statusCode).toBe(200)

        })
    })
    describe("search for users with query matching no users", () => {
        test("should respond w 404 status code", async () => {
            const response = await (await app.get("/user/search").set("queryString", "notinthedatabase").set("email", "tesla@mail.com"))
            expect(response.statusCode).toBe(400)

        })
    })
})