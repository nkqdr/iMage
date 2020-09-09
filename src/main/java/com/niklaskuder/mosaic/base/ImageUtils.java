package com.niklaskuder.mosaic.base;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public final class ImageUtils {
    private ImageUtils() {
        throw new IllegalAccessError();
    }

    public static BufferedImage scaleAndCrop(BufferedImage input, int width, int height) {
        float factorX = 1.0F * (float)width / (float)input.getWidth();
        float factorY = 1.0F * (float)height / (float)input.getHeight();
        BufferedImage scaled = factorX > factorY ? scaleWidth(input, width) : scaleHeight(input, height);
        int scaleW = scaled.getWidth();
        int scaleH = scaled.getHeight();
        BufferedImage cropped = scaled.getSubimage((scaleW - width) / 2, (scaleH - height) / 2, width, height);
        return cropped;
    }

    public static BufferedImage scaleWidth(BufferedImage input, int width) {
        if (width <= 0) {
            throw new IllegalArgumentException("width cannot be <= 0");
        } else {
            Image scaled = input.getScaledInstance(width, -1, 4);
            int height = scaled.getHeight((ImageObserver)null);
            if (height <= 0) {
                throw new IllegalArgumentException("height would be 0");
            } else {
                BufferedImage res = new BufferedImage(width, height, input.getType());
                Graphics2D g2d = res.createGraphics();
                g2d.drawImage(scaled, 0, 0, (ImageObserver)null);
                g2d.dispose();
                res.flush();
                return res;
            }
        }
    }

    public static BufferedImage scaleHeight(BufferedImage input, int height) {
        if (height <= 0) {
            throw new IllegalArgumentException("width cannot be <= 0");
        } else {
            Image scaled = input.getScaledInstance(-1, height, 4);
            int width = scaled.getWidth((ImageObserver)null);
            if (width <= 0) {
                throw new IllegalArgumentException("width would be 0");
            } else {
                BufferedImage res = new BufferedImage(width, height, input.getType());
                Graphics2D g2d = res.createGraphics();
                g2d.drawImage(scaled, 0, 0, (ImageObserver)null);
                g2d.dispose();
                res.flush();
                return res;
            }
        }
    }
}
