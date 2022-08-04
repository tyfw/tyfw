const request = require('supertest')
const app = require('../../src/server.js')

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
                "email", "zeph@gmail.com"
            )
            expect(response.statusCode).toBe(200)

        })
    })
    describe("Get leaderboard for a user not in DB", () => {
        test("should respond w 404 status code", async () => {
            const response = await request(app).get("/user/leaderboard").set(
                "email", "notauser@gmail.com"
            )
            expect(response.statusCode).toBe(400)

        })
    })
})