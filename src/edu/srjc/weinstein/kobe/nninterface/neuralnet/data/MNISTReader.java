//Kobe Weinstein
//WeinstienKobe2137@gmail.com
//12/17/2018
//Neural Net Interface
// Cs17.11
// A class for reading binary files in the .IDX format as listed at http://yann.lecun.com/exdb/mnist/

package edu.srjc.weinstein.kobe.nninterface.neuralnet.data;


import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

public class MNISTReader
{
    private File img;
    private File label;


    public MNISTReader (File img, File label)
    {
        if (img != null && label != null)
        {
            this.img = img;
            this.label = label;
        }
        else
        {
            throw new NullPointerException();
        }
    }

    //Reads the file according to the specifications and produces a data set to be used by a NeuralNet Object
    public NNDataSet getNNistSet () throws Exception
    {

        DataInputStream imagedatain = new DataInputStream(new BufferedInputStream(new FileInputStream(img)));

        int imageMagicNumber = imagedatain.readInt();
        int imageNumberOfItems = imagedatain.readInt();
        int rows = imagedatain.readInt();
        int cols = imagedatain.readInt();

        DataInputStream labeldatain = new DataInputStream(new BufferedInputStream(new FileInputStream(label)));

        int labelMagicNumber = labeldatain.readInt();
        int labelNumberOfItems = labeldatain.readInt();

        if (labelNumberOfItems != imageNumberOfItems)
        {
            throw new IllegalArgumentException("Dataset Lengths Do not match.");
        }


        NNDataSet dataSet = new NNDataSet();
        int label;

        for (int idxLabels = 0; idxLabels < labelNumberOfItems; idxLabels++)
        {
            int image[] = new int[(rows * cols)];
            label = Byte.toUnsignedInt(labeldatain.readByte());
            for (int pixelCount = 0; pixelCount < (rows * cols); pixelCount++)
            {
                image[pixelCount] = Byte.toUnsignedInt(imagedatain.readByte());
            }
            dataSet.addSet(label, image);
        }
        imagedatain.close();
        labeldatain.close();
        return dataSet;
    }
}
