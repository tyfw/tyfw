const data = require('../src/data.js');

test("Test get_eth_ballance", async () => {
    const dat = await data.get_balance("0xa5cD18A9c0028853Cac10c778B03001e2c18aFF4")
    expect(dat).toBe("0.010188593518029595");
});
