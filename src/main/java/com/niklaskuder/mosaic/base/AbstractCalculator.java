package com.niklaskuder.mosaic.base;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Abstract calculator class used for calculating the average color of a given region.
 *
 * @author Niklas Kuder
 * @version 1.0
 */
public abstract class AbstractCalculator {

    /**
     * calculates the average color for a given region.
     * @param region the given region.
     * @return returns the average color as an integer.
     */
    public int averageColor(BufferedImage region) {
        long r = 0;
        long g = 0;
        long b = 0;
        long a = 0;
        int ctr = 0;

        for (int x = xStart(region); x < getXBound(region); x++) {
            float yBound = getYBound(region, x);
            for (int y = yStart(region, x); y < yBound; y++) {
                int correct = getCorrectRow(region, y);
                int col = region.getRGB(x, correct);

                Color c = new Color(col, true);
                r += c.getRed();
                g += c.getGreen();
                b += c.getBlue();
                a += c.getAlpha();
                ctr++;
            }
        }
        return new Color((int) (r / ctr), (int) (g / ctr), (int) (b / ctr), (int) (a / ctr)).getRGB();
    }

    public abstract int xStart(BufferedImage region);

    public abstract int yStart(BufferedImage region, int x);

    /**
     * This abstract method returns the correct y-value that will be used for iterating through the region.
     * Different triangles need different y-values.
     * @param region the given region.
     * @param y the current y-value
     * @return returns the correct y-value
     */
    public abstract int getCorrectRow(BufferedImage region, int y);

    public abstract int getXBound(BufferedImage region);

    /**
     * This abstract method lets the individual calculators return the right yBounds for their purpose.
     * @param region the given region
     * @param x the current row index
     * @return returns the yBound for the given row
     */
    public abstract float getYBound(BufferedImage region, int x);

}
