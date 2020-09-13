package com.niklaskuder.mosaic.rectangle;

import com.niklaskuder.mosaic.base.BufferedArtImage;
import com.niklaskuder.mosaic.base.MosaicArtist;
import com.niklaskuder.mosaic.base.MosaicShape;

public final class MosaicRunnable implements Runnable {
    private static int heightIndex = 0;
    private static int widthIndex = 0;

    private final int tileWidth;
    private final int tileHeight;
    private final BufferedArtImage image;
    private final MosaicArtist artist;
    private final BufferedArtImage result;

    public MosaicRunnable(BufferedArtImage image, MosaicArtist artist, BufferedArtImage result) {
        this.artist = artist;
        this.tileWidth = artist.getTileWidth();
        this.tileHeight = artist.getTileHeight();
        this.image = image;
        this.result = result;
    }

    @Override
    public void run() {
        while (heightIndex <= image.getHeight()) {
            int[] bounds = getBounds();
            int x = bounds[0];
            int y = bounds[1];
            System.out.println("Working in: " + x + ", " + y);
            int width = x + tileWidth < image.getWidth() ? tileWidth : image.getWidth() - x;
            int height = y + tileHeight < image.getHeight() ? tileHeight : image.getHeight() - y;
            if (width <= 0 || height <= 0) {
                break;
            }
            BufferedArtImage sub = image.getSubimage(x, y, width, height);
            BufferedArtImage tile = artist.getTileForRegion(sub);
            decreaseDisabledCounts();
            result.setSubimage(x, y, tile);
        }
    }

    private synchronized void decreaseDisabledCounts() {
        for (MosaicShape shape : artist.getShapes()) {
            if (shape.isDisabled()) {
                shape.decreaseDisabled();
            }
        }
    }

    public static void resetBounds() {
        heightIndex = 0;
        widthIndex = 0;
    }

    private synchronized int[] getBounds() {
        int[] bounds = new int[2];
        bounds[0] = widthIndex;
        bounds[1] = heightIndex;
        if (widthIndex >= image.getWidth() - tileWidth) {
            widthIndex = 0;
            heightIndex += tileHeight;
        } else {
            widthIndex += tileWidth;
        }
        return bounds;
    }
}
