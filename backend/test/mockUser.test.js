class User {
    constructor(username, firstname, lastname, email, addresses) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.addresses = addresses; 
        this.friends = [];
    }
}

const mockUser = new User("mockUser", "mock", "user", "mock@mail.com", ["0x12345"])

const mockUserModule = {
  getUserByEmail: jest.fn((email) => {
    return Promise.resolve(mockUser);
    }),
  getUserByUsername: jest.fn((username) => {
        return Promise.resolve(mockUser);
    }),
  getUserByWalletAddress: jest.fn((wallet_address) => {
        return Promise.resolve(mockUser);
    }),
  search: jest.fn((queryString) => {
    return Promise.resolve({"username": "mockuser", "addresses": ["0x12345"]})
  })
}

test("test mockGetUserByEmail", async () => {
  const user = await mockUserModule.getUserByEmail("mock@mail.com");
  expect(user).toBe(mockUser);
});

test("test mockGetUserByUsername", async () => {
  const user = await mockUserModule.getUserByUsername("mockUser");
  expect(user).toBe(mockUser);
});

test("test mockGetUserByWalletAddress", async () => {
  const user = await mockUserModule.getUserByWalletAddress("0x12345");
  expect(user).toBe(mockUser);
});

test("test mockSearch", async () => {
    const result = await mockUserModule.search("mock")
    expect(result).toStrictEqual({"username": "mockuser", "addresses": ["0x12345"]});
    
})