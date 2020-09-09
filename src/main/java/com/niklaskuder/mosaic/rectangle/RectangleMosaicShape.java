package com.niklaskuder.mosaic.rectangle;

import com.niklaskuder.mosaic.base.BufferedArtImage;
import com.niklaskuder.mosaic.base.ImageUtils;
import com.niklaskuder.mosaic.base.MosaicShape;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class RectangleMosaicShape implements MosaicShape {
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
    public void decreaseDisabled() {
        if (this.disabledCounter > 0) {
            this.disabledCounter--;
        }
    }

    @Override
    public boolean isDisabled() {
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

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                image.setRGB(x, y, this.image.getRGB(x, y));
            }
        }
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
