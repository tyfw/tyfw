const data = require("../src/data.js");

const testWallet = "0xa5cd18a9c0028853cac10c778b03001e2c18aff4";
const testToken = "0x0D8775F648430679A709E98d2b0Cb6250d2887EF";

test("Test get_eth_ballance", async () => {
  const dat = await data.getEthBalance(testWallet);
  expect(dat).toBe("0.010188593518029595");
});

test("Test getBalance returns a number", async () => {
  const balance = await data.getBalance(testWallet);
  expect(typeof balance).toBe("number");
});

test("Test getTransactionHistory", async () => {
  const history = await data.getTransactionHistory(testWallet);
  expect(history).toStrictEqual([
    {
      time: "1616805252",
      value: 0.010188593518029595,
    },
  ]);
});

test("Test getERC20Price", async () => {
  const price = await data.getERC20Price(testToken);
  console.log(price);
  expect(typeof price).toBe("number");
});
