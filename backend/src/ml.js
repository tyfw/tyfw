const tf = require('@tensorflow/tfjs');
require("@tensorflow/tfjs-node");
const fs = require('fs');

const main = async () => {

    const model = await tf.loadLayersModel('file://data/model.json');
    let a = tf.tensor([0.1, 0.2, 0.3, 0.4, 0.5, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 0.1, 0.2, 0.3, 0.4, 0.5, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9]);
    a = a.expandDims(1);
    a = a.reshape([-1,24,1]);   

    model.predict(a).print();
    //console.log(predict.dataSync());
}
main().then(() => {}).catch((err) => {console.log(err)});