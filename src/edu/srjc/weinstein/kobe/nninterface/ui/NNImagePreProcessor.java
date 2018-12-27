//Kobe Weinstein
//WeinstienKobe2137@gmail.com
//12/17/2018
//NeuralNetInterface
//Cs17.11
//This object stores a buffered image and allows it to be proceeded similar to the MNIST data set.
//The MNIST set is processed by scaling digits preserving aspect ratio to 20x20 and then placing them by center of mass into a 28x28 image.

package edu.srjc.weinstein.kobe.nninterface.ui;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class NNImagePreProcessor
{
    private BufferedImage image;

    public NNImagePreProcessor (Image input)
    {
        image = SwingFXUtils.fromFXImage(input, null);
    }


    //finds the bounds of the digit and crops it.
    public void crop ()
    {
        int xMin = image.getWidth();
        int xMax = 0;
        int yMin = image.getHeight();
        int yMax = 0;
        for (int y = 0; y < image.getHeight(); y++)
        {
            for (int x = 0; x < image.getWidth(); x++)
            {

                //RGB value from unsigned int.
                //Author: AKX https://stackoverflow.com/users/51685/akx
                //https://stackoverflow.com/a/2534131
                int currentPxValue = 255 - (image.getRGB(x, y) & 0xff);


                if (currentPxValue > 0)
                {
                    if (x < xMin)
                    {
                        xMin = x;
                    }
                    if (x > xMax)
                    {
                        xMax = x;
                    }
                    if (y < yMin)
                    {
                        yMin = y;
                    }
                    if (y > yMax)
                    {
                        yMax = y;
                    }
                }
            }
        }
        image = image.getSubimage(xMin, yMin, xMax - xMin, yMax - yMin);
    }

    //Scales the image preserving aspect ratio, so that non-square digits aren't distorted.
    public void scaleWithAspectRatio (int dimensionOfLargestSide)
    {

        double yScale;
        double xScale;
        if (image.getWidth() > image.getHeight())
        {
            yScale = (double) dimensionOfLargestSide / image.getWidth();
            xScale = (double) dimensionOfLargestSide / image.getWidth();

        }
        else
        {
            yScale = (double) dimensionOfLargestSide / image.getHeight();
            xScale = (double) dimensionOfLargestSide / image.getHeight();
        }
        AffineTransform scale = new AffineTransform();
        scale.scale(xScale, yScale);
        AffineTransformOp scaleOp = new AffineTransformOp(scale, AffineTransformOp.TYPE_BICUBIC);

        int newWidth = ((int) Math.ceil(xScale * image.getWidth()));
        int newHeight = ((int) Math.ceil(yScale * image.getHeight()));

        //resize the image to 20x20, storing the resized image in a new bufferedImage.
        BufferedImage resizedImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = resizedImg.createGraphics();
        g.setBackground(Color.WHITE);
        g.clearRect(0, 0, newWidth, newHeight);
        g.drawImage(image, scaleOp, 0, 0);
        g.dispose();
        image = resizedImg;
    }

    //Find the Center of Mass of the image and place it in a new image matching the center of mass to the center point
    // of the new image.
    public void placeByCenterOfMass (int widthOfNewImage, int heightOfNewImage)
    {
        int height = image.getHeight();
        int width = image.getWidth();

        //find the center of Mass of the current image.
        //Credit: DanielHsH (https://stackoverflow.com/users/757345/danielhsh)
        //https://stackoverflow.com/questions/22615005/how-to-find-center-of-mass-for-my-entire-binary-image/22620206#22620206
        int totalX = 0;
        int totalY = 0;
        int totalWeight = 0;

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                int value = 255 - (image.getRGB(x, y) & 0xff);

                totalX += x * value;
                totalY += y * value;
                totalWeight += value;
            }
        }
        int centerMassX = totalX / totalWeight;
        int centerMassY = totalY / totalWeight;

        //find the center of the new image
        int centerX = (widthOfNewImage / 2) - 1;
        int centery = (heightOfNewImage / 2) - 1;

        //find the x,y location where the the top-left corner of the original image should be placed.
        int x = centerX - centerMassX;
        int y = centery - centerMassY;

        BufferedImage newImg = new BufferedImage(widthOfNewImage, heightOfNewImage, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = newImg.createGraphics();
        g.setBackground(Color.WHITE);
        g.clearRect(0, 0, widthOfNewImage, heightOfNewImage);
        g.drawImage(image, x, y, Color.white, null);
        g.dispose();
        image = newImg;
    }


    public int[] getImgSet ()
    {
        int height = image.getHeight();
        int width = image.getWidth();

        int[] pixels = new int[height * width];
        int pxIDX = 0;
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                int value = 255 - (image.getRGB(x, y) & 0xff);
                pixels[pxIDX] = value;
                pxIDX++;
            }
        }
        return pixels;
    }

}
