const mockDataModule = {
  getBalance: jest.fn((address) => {
    return Promise.resolve(12.34);
    }),
  getEthBalance: jest.fn((address) => {
        return Promise.resolve(0.001234);
    }),
    getERC20Balance: jest.fn((token_address, wallet_address) => {
        return Promise.resolve(0.001234);
    }),
  getTransactionHistory: jest.fn((wallet_address) => {
        return Promise.resolve([
            {time: "1616805252", value: 0.010188593518029595},
            {time: "1616805253", value: 0.010188593518029597},
    ]);
    }),
    getPriceHistory: jest.fn((price_abv, interval, options) => {
        return Promise.resolve([
      { time: "1616805252", value: 1195.26 },
            {time: "1616805253", value: 1113.07},
        ]);
    }),
  getEthPrice: jest.fn(() => {
    return Promise.resolve(1195.26);
    }),
    getERC20Price: jest.fn((token_address) => {
        return Promise.resolve(1195.26);
    }),
  getAccountHistory: jest.fn(
        return Promise.resolve([
            0.010188593518029595,
            0.010188593518029597,
        ]);
    }),
    getYearPercentReturn: jest.fn((address) => {
    return Promise.resolve(1.23);
    }),
};

test("test mockGetBalance", async () => {
  const balance = await mockDataModule.getBalance("0x12345");
    expect(balance).toBe(12.34);
});

test("test mockGetEthBalance", async () => {
    const balance = await mockDataModule.getEthBalance("0x12345");
  expect(balance).toBe(0.001234);
});

test("test mockGetERC20Balance", async () => {
    const balance = await mockDataModule.getERC20Balance("0x12345", "0x12345");
    expect(balance).toBe(0.001234);
});

test("test mockGetTransactionHistory", async () => {
    const history = await mockDataModule.getTransactionHistory("0x12345");
    expect(history).toStrictEqual([
        {time: "1616805252", value: 0.010188593518029595},
        {time: "1616805253", value: 0.010188593518029597},
    ]);
});

test("test mockGetPriceHistory", async () => {
    const history = await mockDataModule.getPriceHistory("ABC", "1h", {});
    expect(history).toStrictEqual([
        {time: "1616805252", value: 1195.26},
        {time: "1616805253", value: 1113.07},
    ]);
});

test("test mockGetEthPrice", async () => {
    const history = await mockDataModule.getEthPrice();
    expect(history).toStrictEqual(1195.26);
});

test("test mockGetERC20Price", async () => {
    const history = await mockDataModule.getERC20Price("0x12345");
    expect(history).toStrictEqual(1195.26);
});

test("test mockGetAccountHistory", async () => {
    const history = await mockDataModule.getAccountHistory("0x12345", "1h", 10, 0, 0);
    expect(history).toStrictEqual([
        0.010188593518029595,
        0.010188593518029597,
    ]);
});

test("test mockGetYearPercentReturn", async () => {
    const history = await mockDataModule.getYearPercentReturn("0x12345");
    expect(history).toBe(1.23);
});