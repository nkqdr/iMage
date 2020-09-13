package com.niklaskuder.mosaic.rectangle;

import com.niklaskuder.mosaic.base.BufferedArtImage;
import com.niklaskuder.mosaic.base.ImageUtils;
import com.niklaskuder.mosaic.base.MosaicShape;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class RectangleMosaicShape implements MosaicShape {
    private static final int CORRECTION_INTENSITY = 15;

    private final BufferedImage image;
    private final int width;
    private final int height;
    private final int average;
    private int disabledCounter;

    public RectangleMosaicShape(BufferedImage image, int w, int h) {
        this.image = ImageUtils.scaleAndCrop(image, w, h);
        this.width = w;
        this.height = h;
        this.average = calcAverage(this.image, w, h);
        this.disabledCounter = 0;
    }

    @Override
    public synchronized void decreaseDisabled() {
        if (this.disabledCounter > 0) {
            this.disabledCounter--;
        }
    }

    @Override
    public synchronized void setDisabled(int amount) {
        this.disabledCounter = amount;
    }

    @Override
    public synchronized boolean isDisabled() {
        return this.disabledCounter > 0;
    }

    private int calcAverage(BufferedImage region, int w, int h) {
        long r = 0;
        long g = 0;
        long b = 0;
        long a = 0;
        int ctr = 0;

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int col = region.getRGB(x, y);

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

    @Override
    public BufferedImage getThumbnail() {
        return this.image;
    }

    @Override
    public int getAverageColor() {
        return this.average;
    }

    @Override
    public void drawMe(BufferedArtImage image) {
        if (image.getWidth() > this.getWidth() || image.getHeight() > this.getHeight()) {
            throw new IllegalArgumentException("dimensions of target are too big for this tile");
        }
        int w = Math.min(this.getWidth(), image.getWidth());
        int h = Math.min(this.getHeight(), image.getHeight());

        BufferedArtImage correctedImage = getColorCorrectedMe(image);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                image.setRGB(x, y, correctedImage.getRGB(x, y));
            }
        }
    }

    private int getOptimalCorrectionIntensity(int goal, int current) {
        if (current + CORRECTION_INTENSITY < goal
                || current - CORRECTION_INTENSITY > goal) {
            return CORRECTION_INTENSITY;
        } else if (current + CORRECTION_INTENSITY > goal) {
            return goal - current;
        } else if (current - CORRECTION_INTENSITY < goal) {
            return current - goal;
        }
        return CORRECTION_INTENSITY;
    }

    private BufferedArtImage getColorCorrectedMe(BufferedArtImage input) {
        BufferedArtImage correctedMe = new BufferedArtImage(this.image);
        for (int x = 0; x < input.getWidth(); x++) {
            for (int y = 0; y < input.getHeight(); y++) {
                int inputRGB = input.getRGB(x, y);
                Color inputPixelColor = new Color(inputRGB);
                int tileRGB = this.image.getRGB(x, y);
                Color tilePixelColor = new Color(tileRGB);
                int correctionIntensity = getOptimalCorrectionIntensity(inputRGB, tileRGB);
                int newRed = tilePixelColor.getRed() < inputPixelColor.getRed()
                        ? tilePixelColor.getRed() + correctionIntensity
                        : tilePixelColor.getRed() - correctionIntensity;
                int newGreen = tilePixelColor.getGreen() < inputPixelColor.getGreen()
                        ? tilePixelColor.getGreen() + correctionIntensity
                        : tilePixelColor.getGreen() - correctionIntensity;
                int newBlue = tilePixelColor.getBlue() < inputPixelColor.getBlue()
                        ? tilePixelColor.getBlue() + correctionIntensity
                        : tilePixelColor.getBlue() - correctionIntensity;
                int newAlpha = tilePixelColor.getAlpha() < inputPixelColor.getAlpha()
                        ? tilePixelColor.getAlpha() + correctionIntensity
                        : tilePixelColor.getAlpha() - correctionIntensity;

                newRed = Math.min(newRed, 255);
                newRed = Math.max(newRed, 0);
                newGreen = Math.min(newGreen, 255);
                newGreen = Math.max(newGreen, 0);
                newBlue = Math.min(newBlue, 255);
                newBlue = Math.max(newBlue, 0);
                newAlpha = Math.min(newAlpha, 255);
                newAlpha = Math.max(newAlpha, 0);

                Color correctedColor = new Color(newRed, newGreen, newBlue, newAlpha);
                correctedMe.setRGB(x, y, correctedColor.getRGB());
            }
        }
        return correctedMe;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }
}
