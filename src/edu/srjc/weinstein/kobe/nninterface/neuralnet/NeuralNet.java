//Kobe Weinstein
//WeinstienKobe2137@gmail.com
//12/17/2018
//NeuralNetInterface
//Cs17.11
//Creates a Artificial Neural network, with an output layer of 10.
//This network runs on the standard backpropagation algorithm, and least Squared error Objective function.
//The propagation method used takes a weighted sum of the output of the previous nodes, rescaled by the sigmoid function,
//to determine the activation of the current node.
package edu.srjc.weinstein.kobe.nninterface.neuralnet;

import edu.srjc.weinstein.kobe.nninterface.neuralnet.data.NNDataSet;
import edu.srjc.weinstein.kobe.nninterface.neuralnet.util.MatrixMath;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class NeuralNet implements Cloneable
{
    private ArrayList<double[][]> weights = new ArrayList<>();
    private ArrayList<double[]> bias = new ArrayList<>();

    //The first array will be set to inputs.
    private ArrayList<double[]> activiation = new ArrayList<>();


    //numOfInputs is the number of desired nodes in the input layer.
    //hidden layers is an array containing the values for each hidden layer.
    // EX: {20,12,8} would represent 3 hidden layers, the first one with 20 nodes,
    //the second with 12, and the third with 8.
    //numOfOutputs should be the number of nodes required to represent the output.
    public NeuralNet (int numOfInputs, int[] hiddenlayers, int numOfOutputs)
    {
        //Input Nodes initialized
        activiation.add(new double[numOfInputs]);

        //Hidden Nodes initialized
        for (int i = 0; i < hiddenlayers.length; i++)
        {
            activiation.add(new double[hiddenlayers[i]]);
            weights.add(new double[hiddenlayers[i]][activiation.get(i).length]);
            bias.add(new double[hiddenlayers[i]]);

        }

        //Output Nodes initialized
        weights.add(new double[numOfOutputs][hiddenlayers[hiddenlayers.length - 1]]);
        bias.add(new double[numOfOutputs]);


        activiation.add(new double[numOfOutputs]);

        initializeWeights();
    }

    //Deep copy.
    //https://www.geeksforgeeks.org/clone-method-in-java-2/ (Description of Clone use for shallow and deep copies)
    public Object clone () throws CloneNotSupportedException
    {
        NeuralNet output = (NeuralNet) super.clone();
        output.activiation = new ArrayList<>();
        output.weights = new ArrayList<>();
        output.bias = new ArrayList<>();

        for (double[] layer : activiation)
        {
            output.activiation.add(layer.clone());
        }

        for (double[][] layer : weights)
        {
            double[][] newLayer = new double[layer.length][layer[0].length];
            for (int rows = 0; rows < layer.length; rows++)
            {
                newLayer[rows] = layer[rows].clone();
            }
            output.weights.add(newLayer);
        }

        for (double[] layer : bias)
        {
            output.bias.add(layer.clone());
        }
        return output;
    }

    //Trains the network using backpropagation. This is where the network "learns", it is "taught" the right values for
    //the weights and bias that result in correct guesses for the trueValue of the input arrays.
    public void train (int epochs, NNDataSet trainingData, double learningRate)
    {
        for (int e = 0; e <= epochs; e++)
        {
            for (int i = 0; i < trainingData.getNumOfDataSets(); i++)
            {
                activiation.set(0, trainingData.getImage(i));
                propagate();
                backpropagate(trainingData.getLabel(i), learningRate);
            }
            //randomize order of data before going through the whole set again.
            trainingData.randomizer();
        }
    }

    //initializes the weights, using Xavier intialization.
    //The paper that describes and test these starting weights is listed below.
    //Understanding the difficulty of training deep feedforward neural networks(Glorot and Bengio)
    //http://proceedings.mlr.press/v9/glorot10a/glorot10a.pdf
    public void initializeWeights ()
    {
        for (int idx = 0; idx < weights.size(); idx++)
        {
            for (int rows = 0; rows < weights.get(idx).length; rows++)
            {
                for (int cols = 0; cols < weights.get(idx)[rows].length; cols++)
                {
                    Random random = new Random();
                    weights.get(idx)[rows][cols] = (random.nextGaussian())
                                                   / Math.sqrt(weights.get(idx).length);
                }
            }
        }
    }

    //Calculates the values of activation for all of the nodes.
    //The basic equation for forward propagation can be found at wikipedia.
    //https://en.wikipedia.org/wiki/Artificial_neural_network#Components_of_an_artificial_neural_network
    private void propagate ()
    {
        for (int layer = 0; layer < activiation.size() - 1; layer++)
        {
            try
            {
                activiation.set(layer + 1, sigmoid(
                        MatrixMath.elementWiseAdd(
                                MatrixMath.multiply(weights.get(layer), activiation.get(layer)), bias.get(layer))));
            }
            catch (IllegalArgumentException e)
            {
                System.err.println(String.format("Something went wrong with the matrix sizes, %s", e.getMessage()));
                return;
            }
        }
    }

    //rescales value of the input to a node to between 0 and 1 using the logistic function.
    private double[] sigmoid (double[] input)
    {
        double[] result = new double[input.length];
        for (int i = 0; i < input.length; i++)
        {
            result[i] = 1 / (1 + Math.exp(- input[i]));
        }
        return result;
    }

    //Uses the Gradient of the cost function in terms of the weights, to find the direction that decreases the
    //Objective or Cost function. The weights are then changed to move in this direction.
    // This minimizes the cost(Error), increasing the accuracy of the network.
    private void backpropagate (int expectedValue, double learningRate)
    {
        int outputLayerIdx = activiation.size() - 1;
        double[] expectedOutputActivation = new double[activiation.get(outputLayerIdx).length];
        expectedOutputActivation[expectedValue] = 1;

        ArrayList<double[]> delta = new ArrayList<>();

        //do the output layer first as the backPropagation equation defines the change in weights in terms of the next layers deltaWeight.
        delta.add(deltaOutPutLayer(expectedOutputActivation, activiation.get(outputLayerIdx)));
        for (int row = 0; row < weights.get(outputLayerIdx - 1).length; row++)
        {
            for (int col = 0; col < weights.get(outputLayerIdx - 1)[0].length; col++)
            {
                weights.get(outputLayerIdx - 1)[row][col] -= delta.get(0)[row] * activiation.get(outputLayerIdx - 1)[col] * learningRate;
            }
        }
        for (int i = 0; i < bias.get(outputLayerIdx - 1).length; i++)
        {
            bias.get(outputLayerIdx - 1)[i] -= delta.get(0)[i] * learningRate;
        }
        //Start with the first layer that comes before the output layer, and stop when we reach the input.
        for (int layer = outputLayerIdx - 1; layer > 0; layer--)
        {
            delta.add(0, deltaInnerLayers(delta.get(0), activiation.get(layer), weights.get(layer)));

            for (int rows = 0; rows < delta.get(0).length; rows++)
            {
                for (int cols = 0; cols < activiation.get(layer - 1).length; cols++)
                {
                    weights.get(layer - 1)[rows][cols] -= delta.get(0)[rows] * activiation.get(layer - 1)[cols] * learningRate;
                }
            }
            for (int i = 0; i < bias.get(outputLayerIdx - 1).length; i++)
            {
                bias.get(outputLayerIdx - 1)[i] -= delta.get(0)[i];
            }
        }
    }

    //delta is the greek symbol used to denote the derivative of the cost function in terms of the final of the node.
    //This means that inner nodes have a different equation for delta, as they affect the final output indirectly.

    private double[] deltaOutPutLayer (double[] expectedOutput, double[] actualOutput)
    {
        double[] delta = new double[actualOutput.length];

        for (int i = 0; i < actualOutput.length; i++)
        {
            delta[i] = (actualOutput[i] - expectedOutput[i]) * actualOutput[i] * (1 - actualOutput[i]);
        }
        return delta;
    }

    private double[] deltaInnerLayers (double[] previouslValues, double[] outputPrevious,
                                       double[][] weightsOfNextLayer)
    {
        double[] result = new double[weightsOfNextLayer[0].length];

        for (int col = 0; col < weightsOfNextLayer[0].length; col++)
        {
            for (int row = 0; row < weightsOfNextLayer.length; row++)
            {
                result[col] += weightsOfNextLayer[row][col] * previouslValues[row];
            }
            result[col] = result[col] * outputPrevious[col] * (1 - outputPrevious[col]);
        }
        return result;
    }

    //returns the guess of the network on a NNDataset containing a single input,trueValue pair
    public int test (NNDataSet testSet)
    {

        activiation.set(0, testSet.getImage(0));
        propagate();
        return findMax(activiation.get(activiation.size() - 1));
    }

    //takes a full set of input,trueValue pairs in a NNDataSet and returns the percent the network guessed right
    public double testSet (NNDataSet testSet)
    {
        testSet.randomizer();
        int totaldata = testSet.getNumOfDataSets();
        int totalCorrect = 0;
        for (int i = 0; i < totaldata; i++)
        {
            activiation.set(0, testSet.getImage(i));
            propagate();

            if (isCorrectResult(testSet.getLabel(i), activiation.get(activiation.size() - 1)))
            {
                totalCorrect++;
            }
        }
        return ((double) totalCorrect / (double) totaldata) * 100;
    }

    private boolean isCorrectResult (int trueValue, double[] nnOutput)
    {
        int nnGuess = findMax(nnOutput);
        return (trueValue == nnGuess);

    }

    //get index of the node with the highest activation value in the output layer. This is the Neural nets guess.
    private int findMax (double[] nnOutput)
    {
        int maxIdx = 0;
        double max = nnOutput[0];
        for (int i = 0; i < nnOutput.length; i++)
        {
            if (max < nnOutput[i])
            {
                maxIdx = i;
                max = nnOutput[i];
            }
        }
        return maxIdx;
    }

    //Save the weights to a file, so that a trained network can be loaded, and retraining is not required.
    public void saveweights (File saveLocation) throws IOException
    {
        try
        {
            OutputStreamWriter weights = new OutputStreamWriter(new FileOutputStream(saveLocation));

            weights.write("#Weights: \n");
            for (double[][] weight : this.weights)
            {
                for (double[] row : weight)
                {
                    for (double col : row)
                    {
                        weights.write(String.format("%1$.12f|", col));
                    }
                    weights.write("\n");
                }
                weights.write("\n");
            }

            weights.write("#Bias: \n");
            for (double[] bias : bias)
            {
                for (double row : bias)
                {
                    weights.write(String.format("%1$.12f|", row));
                }
                weights.write("\n");

            }
            weights.close();

        }
        catch (IOException e)
        {
            throw e;
        }
    }

    public void loadweights (File file) throws Exception
    {
        Scanner scannerFile = new Scanner(file);

        int numOfWeights = this.weights.size();
        ArrayList<double[][]> tempWeights = new ArrayList<>();
        ArrayList<double[]> tempBias = new ArrayList<>();
        for (double[][] layer : weights)
        {
            double[][] newLayer = new double[layer.length][layer[0].length];
            for (int rows = 0; rows < layer.length; rows++)
            {
                newLayer[rows] = layer[rows].clone();
            }
            tempWeights.add(newLayer);
        }
        for (double[] layer : bias)
        {
            tempBias.add(layer.clone());
        }


        for (int layer = 0; layer < numOfWeights; layer++)
        {
            for (int row = 0; row < tempWeights.get(layer).length; row++)
            {
                String[] line = scannerFile.nextLine().trim().split("\\|");

                while (line[0].isEmpty() || line[0].startsWith("#"))
                {
                    line = scannerFile.nextLine().trim().split("\\|");
                }

                for (int col = 0; col < tempWeights.get(layer)[row].length; col++)
                {
                    if (line.length != tempWeights.get(layer)[row].length)
                    {
                        throw new Exception(String.format("Invalid weight file; Error on line %s.", layer));
                    }
                    else
                    {
                        tempWeights.get(layer)[row][col] = Double.parseDouble(line[col]);
                    }
                }
            }
        }
        int numOfBias = tempBias.size();
        for (int layer = 0; layer < numOfBias; layer++)
        {
            String[] line = scannerFile.nextLine().trim().split("\\|");
            while (line[0].isEmpty() || line[0].startsWith("#"))
            {
                line = scannerFile.nextLine().trim().split("\\|");
            }

            for (int row = 0; row < tempBias.get(layer).length; row++)
            {
                if (line.length != tempBias.get(layer).length)
                {
                    throw new Exception(String.format("Invalid weight file; Error on line %s.", layer));
                }
                else
                {
                    tempBias.get(layer)[row] = Double.parseDouble(line[row]);
                }
            }
        }

        //the rest of the file should be empty.
        while (scannerFile.hasNext())
        {
            if (! scannerFile.nextLine().isEmpty())
            {
                throw new Exception(String.format("Invalid weight file. The file is longer than expected."));
            }
        }
        //The file held valid weights save them to the network.
        weights = tempWeights;
        bias = tempBias;
    }
}
