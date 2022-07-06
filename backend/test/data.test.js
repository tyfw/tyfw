const data = require('../src/data.js');

const testWallet = "0xa5cd18a9c0028853cac10c778b03001e2c18aff4";

test("Test get_eth_ballance", async () => {
    const dat = await data.getEthBalance(testWallet);
    expect(dat).toBe("0.010188593518029595");
});

test("Test getBalance returns a number", async () => {
    const balance = await data.getBalance(testWallet);
    expect(typeof balance).toBe("number");
})

test("Test getTransactionHistory", async () => {
    const history = await data.getTransactionHistory(testWallet);
    console.log(history)
    expect(history).toStrictEqual([
      {
        time: '1616805252',
        from: '0xea674fdde714fd979de3edf0f56aa9716b898ec8',
        to: '0xa5cd18a9c0028853cac10c778b03001e2c18aff4',
        value: '0.010188593518029595'
      }
    ]);
})
