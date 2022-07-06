const Web3 = require("web3");
const erc20abi = require("../erc20abi.json");

// eth node hosted on infura
const provider =
  "https://mainnet.infura.io/v3/218ce01889ea405a93e2b842b6ef64b9";
const web3 = new Web3(provider);


const get_eth_ballance = async (wallet_address) => {
  web3.eth.getBalance(wallet_address, (err, res) => {
    if (err) {
      console.log(err);
    } else {
      console.log(web3.utils.fromWei(res, "ether") + " ETH");
    }
  });
};

const get_erc_20_ballance = async (token_address, wallet_address) => {
  const contract = new web3.eth.Contract(erc20abi, token_address);
  const res = await contract.methods.balanceOf(wallet_address).call();
  const format = web3.utils.fromWei(res);
  console.log(format);
};

export default {
  get_eth_ballance,
  get_erc_20_ballance,
};