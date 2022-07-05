// Web3 for Eth and ERC 20 chain data
const Web3 = require("web3");
const erc20abi = require("../erc20abi.json");

// eth node hosted on infura
const provider =
  "https://mainnet.infura.io/v3/218ce01889ea405a93e2b842b6ef64b9";
const web3 = new Web3(provider);

// Binance API Client
const { Spot } = require("@binance/connector");
const client = new Spot(
  process.env.BINANCE_API_KEY,
  process.env.BINANCE_SECRET
);

// load api keys from .env file
require("dotenv").config();

// axios for fetching from etherscan
const axios = require("axios");
const etherscan_api_key = process.env.ETHERSCAN_API_KEY;

// get balance of ether by querying wallet address on blockchain
const get_eth_ballance = async (wallet_address) => {
  const ballance = await web3.eth.getBalance(wallet_address);
  return web3.utils.fromWei(ballance, "ether");
};

// get balance of erc 20 token by querying token contract
const get_erc_20_ballance = async (token_address, wallet_address) => {
  const contract = new web3.eth.Contract(erc20abi, token_address);
  const res = await contract.methods.balanceOf(wallet_address).call();
  const format = web3.utils.fromWei(res);
  console.log(format);
};

// get transaction history of wallet address
const get_transaction_history = async (wallet_address) => {
  const url =
    "https://api.etherscan.io/api" +
    "?module=account" +
    "&action=txlist" +
    "&address=" +
    wallet_address +
    "&startblock=0" +
    "&endblock=99999999" +
    "&page=1" +
    "&offset=10" +
    "&sort=asc" +
    "&apikey=" +
    etherscan_api_key;
  let res = await axios.get(url);
  return res.data["result"].map( (item) => {
    return {
      "time": item["timeStamp"],
      "from": item["from"],
      "to": item["to"],
      "value": web3.utils.fromWei(item["value"], "ether"),
    };
  });
};


// get price history of token from binance
// uses candlestick data, price taken as open price
const get_price_history = async (price_abv, interval, options) => {
  const res = await client.klines(price_abv, interval, options)
  return res["data"].map( (item) => {
    return {
      "time": item[0],
      "price": item[1],
    }
  });
};
// ex:
// get_price_history("ETHUSDC", "1M", {
  //limit: 1,
  //startTime: Date.UTC(2020, 1, 1),
//});


// Get account ballance history
const get_account_ballance_history = async (address) => {
    
}

let addr1 = "0xDA9dfA130Df4dE4673b89022EE50ff26f6EA73Cf";
let addr2 = "0xa5cD18A9c0028853Cac10c778B03001e2c18aFF4"

// used while developing
// TODO: Delete this
const test_func = async (funk, address) => {
  const data = await funk(address);
  console.log(data);
};

const test_async = async () => {
  const a = await get_price_history("ETHUSDC", "1M", {
    limit: 1,
    startTime: Date.UTC(2020, 1, 1),
  });
  console.log(a);
};

test_async();
test_func(get_eth_ballance, addr2);
test_func(get_transaction_history, addr2);


module.exports = {
    get_eth_balance: get_eth_ballance,
    get_erc_20_ballance: get_erc_20_ballance,
};