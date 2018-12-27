# NeuralNetInferface

A JavaFx application designed to take user input in the form of a drawn digit 0-9, and ask an MLP to display the value of digit drawn.

#Neural Net
The underlying network, is a 3 layer neural net that take images in the form of a 784 entry length integer array, containing grayscale values between 0-255. The network then propagates, using the sigmoid function to resize the weighted sum of the previous layers activation values. The network uses both weights and biases.

Backpropagation is done using standard backpropagation. The equation for this comes from the total derivative of the cost function (least squared error) in terms of the weights and biases of each layer. The weights and biases are then ajusted using stochastic gradient descent. Stochastic gradient descent and batch stochastic gradient descent where both tested, and Stochastic gradient descent was more effecient at training the network.

#MNIST data
The project contains a program to read the MNIST data set. The MNIST Dataset is a freely avalible set of images of handwritten digits saved into file in the .idx format. These Files are both in the reasources folder, and avalible at http://yann.lecun.com/exdb/mnist/.
