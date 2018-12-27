/*
 *  A JavaFx application designed to take user input in the form of a drawn digit 0-9, and ask an MLP to display the value of digit drawn.
 *  Copyright (C) 2018  Kobe Weinstein
 *
 *  This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
