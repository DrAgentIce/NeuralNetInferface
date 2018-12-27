//Kobe Weinstein
//WeinstienKobe2137@gmail.com
//12/17/2018
//NeuralNetInterface
//Cs17.11
//A class to handle commonly occurring matrix operations, in NeuralNet
package edu.srjc.weinstein.kobe.nninterface.neuralnet.util;

public class MatrixMath
{
    public static double[] multiply (double[][] m1, double[] m2)
    {
        double[] result = new double[m1.length];

        if (m1[0].length == m2.length)
        {
            for (int rowm1 = 0; rowm1 < m1.length; rowm1++)
            {
                for (int i = 0; i < m1[0].length; i++)
                {
                    result[rowm1] += m1[rowm1][i] * m2[i];
                }
            }
            return result;
        }
        else
        {
            throw new IllegalArgumentException("The matrices must be mxn and nx1");
        }
    }

    public static double[] elementWiseAdd (double[] m1, double[] m2)
    {
        double[] result = new double[m1.length];

        if (m1.length == m2.length)
        {
            for (int row = 0; row < m1.length; row++)
            {

                result[row] = m1[row] + m2[row];
            }
            return result;
        }
        else
        {
            throw new IllegalArgumentException("The matrices must both be mxn");
        }
    }

}
