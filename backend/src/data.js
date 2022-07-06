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
const etherscanApiKey = process.env.ETHERSCAN_API_KEY

// get balance of ether by querying wallet address on blockchain
const getEthBalance = async (wallet_address) => {
  const balance = await web3.eth.getBalance(wallet_address);
  return web3.utils.fromWei(balance, "ether");
};

// return current value of eth at wallet address
const getBalance = async (address) => {
  const etheriumBalance = await getEthBalance(address);
  const conversion = await getEthPrice();
  return etheriumBalance * conversion
}

// get balance of erc 20 token by querying token contract
const getERC20Balance = async (token_address, wallet_address) => {
  const contract = new web3.eth.Contract(erc20abi, token_address);
  const res = await contract.methods.balanceOf(wallet_address).call();
  const format = web3.utils.fromWei(res);
  console.log(format);
};

// get transaction history of wallet address
const getTransactionHistory = async (wallet_address) => {
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
    etherscanApiKey;
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
// uses candlestick data, price taken as avg of open, close, high, and low
// Max 1000 points, default 500
const getPriceHistory = async (price_abv, interval, options) => {
  const res = await client.klines(price_abv, interval, options)
  return res["data"].map( (item) => {
    return {
      "Start_time": item[0],
      "avgPrice": (item.slice(1,5).map(parseFloat).reduce((a,b)=> a+b)) / 4 ,
      "Close_time": item[6]
    }
  });
};


// Get current price of etherium relative to USDC
const getEthPrice = async () => {
  const point = await getPriceHistory("ETHUSDC", "1m", {limit: 1})
  return point[0]["avgPrice"];
}



// Get account ballance history
const getAccountHistory = async (address, startTime, endTime) => {
  
}

module.exports = {
  getEthBalance: getEthBalance,
  getBalance: getBalance,
  getTransactionHistory: getTransactionHistory,
};
