const tf = require('@tensorflow/tfjs');
const fs = require('fs');

const model = tf.layers.loadLayersModel('../data/model.json');

const predict = model.predict(tf.tensor([0.5, 0.5]));
console.log(predict.dataSync());