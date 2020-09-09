package com.niklaskuder.mosaic.base;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RasterFormatException;

public class BufferedArtImage {
    private final BufferedImage image;

    public BufferedArtImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, 2);
        int[] transparent = new int[width * height * 4];
        image.setRGB(0, 0, width, height, transparent, 0, 4);
        this.image = image;
    }

    public BufferedArtImage(BufferedImage image) {
        if (image.getType() == 2) {
            this.image = image;
        } else {
            BufferedImage argb = new BufferedImage(image.getWidth(), image.getHeight(), 2);

            for(int x = 0; x < image.getWidth(); ++x) {
                for(int y = 0; y < image.getHeight(); ++y) {
                    argb.setRGB(x, y, image.getRGB(x, y));
                }
            }

            this.image = argb;
        }

    }

    public BufferedArtImage createBlankImage() {
        return new BufferedArtImage(this.getWidth(), this.getHeight());
    }

    public BufferedImage toBufferedImage() {
        return this.image;
    }

    public BufferedArtImage getSubimage(int x, int y, int width, int height) {
        try {
            BufferedImage rect = this.image.getSubimage(x, y, width, height);
            return new BufferedArtImage(rect);
        } catch (RasterFormatException var6) {
            throw new IllegalArgumentException(var6);
        }
    }

    public void setSubimage(int x, int y, BufferedArtImage rectangle) {
        this.image.getGraphics().drawImage(rectangle.image, x, y, (ImageObserver)null);
    }

    public int getRGB(int x, int y) {
        return this.image.getRGB(x, y);
    }

    public void setRGB(int x, int y, int rgb) {
        this.image.setRGB(x, y, rgb);
    }

    public int getWidth() {
        return this.image.getWidth();
    }

    public int getHeight() {
        return this.image.getHeight();
    }
}
