const tf = require("@tensorflow/tfjs");
require("@tensorflow/tfjs-node");

const data = require("./data.js");

//normalization constants
const min = 83.5925;
const max = 4773.12;

const predict = async (riskTolerance) => {
  const model = await tf.loadLayersModel("file://data/model.json");
  const priceHistInput = await data.getPriceHistory("ETHUSDC", "1d", {
    limit: 30,
  });
  let inputTensor = tf.tensor(
    priceHistInput.map((point) => {
      return (point.avgPrice - min) / (max - min);
    })
  );

  inputTensor = inputTensor.expandDims(1);
  inputTensor = inputTensor.reshape([-1, 30, 1]);

  const predictions = model.predict(inputTensor).dataSync();


  const todayPrice = priceHistInput[priceHistInput.length - 1].avgPrice
  const tomorrowPrice = predictions[predictions.length-1] * (max - min) + min;

  console.log("Today's price: " + todayPrice);
  console.log("Model prediction: " + tomorrowPrice);

  return (tomorrowPrice - todayPrice) / todayPrice < riskTolerance;

};

module.exports = {
    predict
}