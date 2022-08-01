const user = require('../src/user.js')

test("Test opening mongo connection", async () => {
    user.runMongo()
})

test("Test registering a new unique user", async () => {
    const random = Math.random().toString(36).replace(/[^a-z]+/g, '').substr(0, 5)
    var success = await user.registerUser(random, "Test", "User", random + "mail.com", ["0x9BF4001d307dFd62B26A2F1307ee0C0307632d59"])
    expect(success).toBe(true)
})

test("Test registering a user with a non-unique username", async () => {
    await expect(async () => {
        await user.registerUser("Tesla", "Nikola", "Tesla", "unique@mail.com", ["0xa5cD18A9c0028853Cac10c778B03001e2c18aFF4"])
    }).rejects
})

test("Test registering a user with a non-unique username", async () => {
    await expect(async () => {
        await user.registerUser("testUser", "Test", "User", "tesla@mail.com", ["0xa5cD18A9c0028853Cac10c778B03001e2c18aFF4"])
    }).rejects
})

test("Test getting user from DB by email", async () => {
    var foundUser = await user.getUserByEmail("tesla@mail.com")
    expect(foundUser.username).toBe("Tesla")
})

test("Test getting non-existing user from DB by email throws not found error", async () => {
    await expect(async () => {
        await user.getUserByEmail("notauser@mail.com")
    }).rejects
})

test("Test getting user from DB by username", async () => {
    var foundUser = await user.getUserByUsername("Tesla")
    expect(foundUser.email).toBe("tesla@mail.com")
})

test("Test getting non-existing user from DB by username throws not found error", async () => {
    await expect(async () => {
        await user.getUserByUsername("notauser")
    }).rejects
})

test("Test getting user from DB by wallet address", async () => {
    var foundUser = await user.getUserByWalletAddress("0xa5cD18A9c0028853Cac10c778B03001e2c18aFF4")
    expect(foundUser.email).toBe("tesla@mail.com")
})

test("Test getting non-existing user from DB by wallet address throws not found error", async () => {
    await expect(async () => {
        await user.getUserByWalletAddress("fakeWalletAddress")
    }).rejects
})

test("Test search when query string fully matches a username", async () => {
    var results = await user.search("email@mail.com", "Tesla")
    expect(results.length).toBe(1)
    expect(results[0].username).toBe("Tesla")
})

test("Test search when query string fully matches a wallet address", async () => {
    var results = await user.search("email@mail.com", "0xa5cD18A9c0028853Cac10c778B03001e2c18aFF4")
    expect(results.length).toBe(1)
    expect(results[0].username).toBe("Tesla")
})

test("Test search when query string is a substring of a username", async () => {
    var results = await user.search("email@mail.com", "Tes")
    expect(results.length).toBe(1)
    expect(results[0].username).toBe("Tesla")
})

test("Test search when query string is a substring of a username", async () => {
    var results = await user.search("email@mail.com", "e")
    expect(results.length).toBeGreaterThan(1)
})

test("Test search when query string is a substring of a wallet address", async () => {
    var results = await user.search("email@mail.com", "a5cD18A")
    expect(results.length).toBe(1)
    expect(results[0].username).toBe("Tesla")
})

test("Test search when query string is a substring of multiple wallet addresses", async () => {
    var results = await user.search("email@mail.com", "0x")
    expect(results.length).toBeGreaterThan(1)
    var usernames = results.map(function (result) {return result.username})
    expect(usernames).toContain("Tesla")
})

test("Test search when query string is not a substring of a username or wallet address", async () => {
    var results = await user.search("email@mail.com", "notausernameorwalletaddress")
    expect(results.length).toBe(0)
})

test("Test adding a friend by username is successful", async () => {
    const random = Math.random().toString(36).replace(/[^a-z]+/g, '').substr(0, 5)
    var success = await user.registerUser(random, "Test", "User", random + "mail.com", ["0x9BF4001d307dFd62B26A2F1307ee0C0307632d59"])
    var result = await user.addFriend("tesla@mail.com", random + "mail.com")
    expect(result).toBe(true)
})

test("Test adding a friend fails when user email does not match any user", async () => {
    await expect(async () => {
    var result = await user.addFriend("nouser@mail.com", "tesla@mail.com")
    }).rejects
})

test("Test adding a friend by username when user already friended", async () => {
    var result = await user.addFriend("tesla@mail.com", "mike@gmail.com")
    expect(result).toBe(true)
})

test("Test adding a friend fails when friend email does not match any user", async () => {
    await expect(async () => {
    var result = await user.addFriend("tesla@mail.com", "nouser@mail.com")
    }).rejects
})

test("Test changing first name", async () => {
    var result = await user.changeName("tesla@mail.com", "first", "Elon")
    expect(result).toBe(true)
})

test("Test changing last name", async () => {
    var result = await user.changeName("tesla@mail.com", "last", "Musk")
    expect(result).toBe(true)
})

test("Test changing name with an invalid name field", async () => {
    var result = await user.changeName("tesla@mail.com", "middle", "Musk")
    expect(result).toBe(false)
})

test("Test changing name fails when user email does not match any user", async () => {
    await expect(async () => {
    var result = await user.changeName("nouser@mail.com", "last", "newName")
    }).rejects
})