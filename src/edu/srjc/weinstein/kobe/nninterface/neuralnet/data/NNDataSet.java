//Kobe Weinstein
//WeinstienKobe2137@gmail.com
//12/17/2018
//NeuralNetInterface
//Cs17.11
// A class to generate objects containing images in array format.
// The images location in the array list is the index of the corresponding trueValue.

package edu.srjc.weinstein.kobe.nninterface.neuralnet.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class NNDataSet
{
    //The expected value from evaluating input, using a Neural Net object.
    //In the case of the MNIST this is the digit that the image represents, referred to as the label.
    private ArrayList<Integer> trueValue = new ArrayList<>();

    //The input for a neural net object.
    // In the Case of the MNIST data this is an array of pixels representing an image.
    private ArrayList<double[]> input = new ArrayList<>();


    public NNDataSet ()
    {
    }

    public int getNumOfDataSets ()
    {
        return trueValue.size();
    }


    public int getLabel (int idx)
    {
        return trueValue.get(idx);
    }

    public void setLabel (int idx, int label)
    {
        this.trueValue.set(idx, label);
    }

    public double[] getImage (int idx)
    {
        return input.get(idx);
    }

    public void setImage (int idx, int[] inputImage)
    {
        double[] image = Arrays.stream(inputImage).asDoubleStream().toArray();
        this.input.set(idx, image);
    }

    public void addSet (int label, int[] inputImage)
    {
        double[] image = Arrays.stream(inputImage).asDoubleStream().parallel().toArray();
        this.trueValue.add(label);
        this.input.add(image);
    }


    //Prints a representation of the input to the console for testing.
    //For use with input in the form of an image.
    public void printImage (int idx, int rows, int cols)
    {
        System.out.println(String.format("The input is of a %s", trueValue.get(idx)));

        int imageIdx = 0;
        for (int row = 0; row < rows; row++)
        {
            for (int col = 0; col < cols; col++)
            {
                if (input.get(idx)[imageIdx] > 0)
                {
                    System.out.print("1 ");
                }
                else
                {
                    System.out.print("0 ");
                }
                imageIdx++;
            }
            System.out.println();
        }
    }

    //randomize the lists while preserving trueValue input pairs.
    public void randomizer ()
    {
        ArrayList<Integer> idxs = new ArrayList<>();
        for (int i = 0; i < trueValue.size(); i++)
        {
            idxs.add(i);
        }
        Collections.shuffle(idxs);
        for (int i = 0; i < idxs.size(); i++)
        {

            Collections.swap(trueValue, i, idxs.get(i));
            Collections.swap(input, i, idxs.get(i));

        }

    }
}
