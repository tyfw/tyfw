// Web3 for Eth and ERC 20 chain data
const Web3 = require("web3");
//const erc20abi = require("../erc20abi.json");

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
const etherscanApiKey = process.env.ETHERSCAN_API_KEY;

// load filesystem for writing to file
const fs = require("fs");

// get balance of ether by querying wallet address on blockchain
const getEthBalance = async (wallet_address) => {
  if (web3.utils.isAddress(wallet_address)) {
    const balance = await web3.eth.getBalance(wallet_address);
    return web3.utils.fromWei(balance, "ether");
  } else {
    throw new Error("Invalid address");
  }
};

const priceIntervals = [
  "1m",
  "3m",
  "5m",
  "15m",
  "30m",
  "1h",
  "2h",
  "4h",
  "6h",
  "8h",
  "12h",
  "1d",
  "3d",
  "1w",
  "1M",
];

// return current value of eth at wallet address
const getBalanceUSD = async (address) => {
  const ethBalance = await getEthBalance(address);
  const ethPrice = await getEthPrice();
  return ethBalance * ethPrice;
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
  // check if address is valid
  if (!web3.utils.isAddress(wallet_address)) {
    throw new Error("Invalid address");
  }

  // check if result is valid
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
  if(!options) options = {};
  // convert unix time interval to binance format precision
  if(options.startTime != undefined){
    options.startTime = options.startTime * 1000;
  }
  if(options.endTime != undefined) {
    options.endTime = options.endTime * 1000;
  }
  if(!priceIntervals.includes(interval)) {
    throw new Error("Invalid interval");
  }
  if (price_abv != "ETHUSDC") {
    throw new Error("Invalid Price ABV");
  }
  return client.klines(price_abv, interval, options)
  .then((res)=>{
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
  });
};

// Get current price of etherium relative to USDC
const getEthPrice = async () => {
  const point = await getPriceHistory("ETHUSDC", "1m", { limit: 1 });
  return point[0]["avgPrice"];
};

// Get account ballance history
const getAccountHistory = async (
  address,
  interval,
  numPoints,
  startTime,
  endTime
) => {
  let ethBalance = await getEthBalance(address);
  if (typeof numPoints !== "number" || numPoints < 1) {
    throw new Error("Invalid numPoints");
  }
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
  return (
    (accountHistory[accountHistory.length - 1] - accountHistory[0]) /
    accountHistory[0]
  );
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
  getEthPrice,
  getBalance: getBalanceUSD,
  getPriceHistory,
  getTransactionHistory,
  getAccountHistory,
  getYearPercentReturn,
  generateTrainingData,
};
