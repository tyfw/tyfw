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
require("dotenv").config({path: '../.env'});

// axios for fetching from etherscan
const axios = require("axios");
const etherscanApiKey = process.env.ETHERSCAN_API_KEY;

// load filesystem for writing to file
const fs = require("fs");

// get balance of ether by querying wallet address on blockchain
const getEthBalance = async (wallet_address) => {
  const balance = await web3.eth.getBalance(wallet_address);
  return web3.utils.fromWei(balance, "ether");
};

// return current value of eth at wallet address
const getBalance = async (address) => {
  const etheriumBalance = await getEthBalance(address);
  const conversion = await getEthPrice();
  return etheriumBalance * conversion;
};

// get balance of erc 20 token by querying token contract
const getERC20Balance = async (token_address, wallet_address) => {
  const contract = new web3.eth.Contract(erc20abi, token_address);
  const res = await contract.methods.balanceOf(wallet_address).call();
  const format = web3.utils.fromWei(res);
  return format;
};

// get transaction history of wallet address
const getTransactionHistory = async (wallet_address) => {
  const url =
    "https://api.etherscan.io/api" +
    "?module=account" +
    "&action=txlist" +
    "&address=" +
    wallet_address +
    "&startblock=9193266" + //jan 1st 2020
    "&endblock=99999999" +
    //"&page=1" +
    //"&offset=10" +
    "&sort=desc" +
    "&apikey=" +
    etherscanApiKey;
  let res = await axios.get(url);
  return res.data["result"].map((item) => {
    const sign = item.to == wallet_address.toLowerCase() ? 1 : -1;
    return {
      time: item["timeStamp"],
      value: web3.utils.fromWei(item["value"], "ether") * sign,
    };
  });
};

// get price history of token from binance
// uses candlestick data, price taken as avg of open, close, high, and low
// Max 1000 points, default 500
const getPriceHistory = async (price_abv, interval, options) => {
  const res = await client.klines(price_abv, interval, options);
  return res["data"].map((item) => {
    return {
      time: item[0] / 1000,
      avgPrice:
        item
          .slice(1, 5)
          .map(parseFloat)
          .reduce((a, b) => a + b) / 4,
    };
  });
};

// Get current price of etherium relative to USDC
const getEthPrice = async () => {
  const point = await getPriceHistory("ETHUSDC", "1m", { limit: 1 });
  return point[0]["avgPrice"];
};

// get current price of ERC20 token
const getERC20Price = async (token_address) => {
  const url =
    "https://api.coingecko.com/api/v3/simple/token_price/ethereum?contract_addresses=" +
    token_address +
    "&vs_currencies=USD";
  let res = await axios.get(url);
  return res.data[token_address.toLowerCase()]["usd"];
};

// Get account ballance history
const getAccountHistory = async (address, interval, numPoints, startTime, endTime) => {
  if (!interval) interval = "1d";
  let ethBalance = await getEthBalance(address);
  let priceHistory = await getPriceHistory("ETHUSDC", interval, {
    startTime,
    endTime,
    limit: numPoints,
  });
  priceHistory = priceHistory.reverse();
  const transactionHistory = await getTransactionHistory(address);
  let balanceHistory = [];
  let transactionHistoryIndex = 0;

  // Iterate over price history
  for (let i = 0; i < priceHistory.length; i++) {
    let price = priceHistory[i]["avgPrice"];
    let pointTime = priceHistory[i]["time"];

    // if next point is after a transaction, update balance with the changes from transaction
    if (pointTime <= transactionHistory[transactionHistoryIndex]["time"]) {
      while (pointTime <= transactionHistory[transactionHistoryIndex]["time"]) {
        if (transactionHistoryIndex < transactionHistory.length - 1) {
          ethBalance -= transactionHistory[transactionHistoryIndex]["value"];
          transactionHistoryIndex++;
        } else {
          break;
        }
      }
    }
    balanceHistory.push(ethBalance * price);
  }
  return balanceHistory.reverse();
};

// Returns the current preformance of a given address
const getYearPercentReturn = async (address) => {
  const accountHistory = await getAccountHistory(address, "1w", 52);
  return (accountHistory[accountHistory.length -1] - accountHistory[0])/accountHistory[0];
};

// Returns the prior 10 days of etherium price and output to csv
const generateTrainingData = async () => {

  const priceHistoryA = await getPriceHistory("ETHUSDT", "1d", {
    limit: 1000,
  });

  const midTime = priceHistoryA[0]["time"]

  const priceHistoryB = await getPriceHistory("ETHUSDT", "1d", {
    //startTime: (midTime - 1000 * 24 * 60 * 60)*1000,
    endTime: (midTime - 24*60*60) * 1000,
    limit: 1000,
  });
  const priceHistory = priceHistoryB.concat(priceHistoryA);

  const csv = priceHistory.map((item) => {
    return item["time"] + "," + item["avgPrice"];
  }).join("\n");
  // check if data directory exists, if not create it
  if (!fs.existsSync("./data")) {
    fs.mkdirSync("./data");
  }
  // write to csv file
  fs.writeFile("data/eth_price.csv", csv, (err) => {
    if (err) throw err;
    console.log("Saved!");
  });
}

module.exports = {
  getEthBalance,
  getBalance,
  getTransactionHistory,
  getERC20Price,
  getERC20Balance,
  getAccountHistory,
  getYearPercentReturn,
  generateTrainingData,
};
