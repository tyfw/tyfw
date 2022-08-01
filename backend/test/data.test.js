const data = require("../src/data.js");

const testWallet = "0xa5cd18a9c0028853cac10c778b03001e2c18aff4";
const testWallet2 = "0xAf1931c20ee0c11BEA17A41BfBbAd299B2763bc0"

test("Test get_eth_ballance", async () => {
  const dat = await data.getEthBalance(testWallet);
  expect(dat).toBe("0.010188593518029595");
});

test("get_eth_ballance fails with bad address", async () => {
  await expect(async () => {
    await data.getEthBalance("0x$@dasdasd");
  }).rejects.toThrow("Invalid address");
});


test("Test getBalance returns a number", async () => {
  const balance = await data.getBalance(testWallet);
  expect(typeof balance).toBe("number");
});

test("Test getBalance fails with bad address", async () => {
  await expect(async () => {
    await data.getBalance("0x$@dasdasd");
  }).rejects.toThrow("Invalid address");
});

test("Test getTransactionHistory", async () => {
  const history = await data.getTransactionHistory(testWallet);
  expect(history).toStrictEqual([
    {
      time: "1616805252",
      value: 0.010188593518029594,
    },
  ])
});

test("Test getTransactionHistory fails with bad address", async () => {
  await expect(async () => {
    await data.getTransactionHistory("$@sdasd");
  }).rejects.toThrow("Invalid address");
});


test("Test getPriceHistory", async () => {
  const history = await data.getPriceHistory("ETHUSDC", "1m", {startTime:"1659146280", limit:1});
  expect(history.length).toBe(1);
  expect(history[0].time).toBe(1659146280);
  expect(history[0].avgPrice).toBe(1721.1975);
});

test("Test getPriceHistory fails with bad interval", async () => {
  await expect(async () => {
    await data.getPriceHistory("ETHUSDC", "asd");
  }).rejects.toThrow("Invalid interval");
});

test("Test getPriceHistory fails with bad Price Abv", async () => {
  await expect(async () => {
    await data.getPriceHistory("a", "3m", {});
  }).rejects.toEqual(Error("Invalid Price ABV"));
});

test("Test getEthPrice", async () => {
  const price = await data.getEthPrice();
  expect(typeof price).toBe("number");
});

test("Test getAccountHistory", async () => {
  const history = await data.getAccountHistory(testWallet, "1m", 1, "1659146280", "1659146280");
  expect(history).toStrictEqual([17.536581691748744]);
  const fullHist = await data.getAccountHistory(testWallet2, "1M", 36);
  expect(fullHist.length).toBe(36);
});

test("Test getAccountHistory fails with bad address", async () => {
  await expect(async () => {
    await data.getAccountHistory("$@sdasd", "1m", 1, "1659146280", "1659146280");
  }).rejects.toThrow("Invalid address");
});

test("Test getAccountHistory fails with bad interval", async () => {
  await expect(async () => {
    await data.getAccountHistory(testWallet, "asd", 1, "1659146280", "1659146280");
  }).rejects.toThrow("Invalid interval");
});

test("Test getAccountHistory fails with bad numPoints", async () => {
  await expect(async () => {
    await data.getAccountHistory(testWallet, "1m", "asd", "1659146280", "1659146280");
  }).rejects.toThrow("Invalid numPoints");

  await expect(async () => {
    await data.getAccountHistory(testWallet, "1m", -2, "1659146280", "1659146280");
  }).rejects.toThrow("Invalid numPoints");
});

test("Test getYearPercentReturn", async () => {
  const returns = await data.getYearPercentReturn(testWallet);
  expect(typeof returns).toBe("number");
});

test("Test getYearPercentReturn fails with bad address", async () => {
  await expect(async () => {
    await data.getYearPercentReturn("$@sdasd");
  }).rejects.toThrow("Invalid address");
});