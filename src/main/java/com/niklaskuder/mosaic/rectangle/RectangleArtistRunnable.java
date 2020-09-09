package com.niklaskuder.mosaic.rectangle;

import com.niklaskuder.mosaic.base.MosaicShape;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class RectangleArtistRunnable implements Runnable {
    private final int startIndex;
    private final int endIndex;
    private final int width;
    private final int height;
    private final List<MosaicShape> shapes;
    private final List<MosaicShape> newShapes;
    private final List<BufferedImage> images;

    public RectangleArtistRunnable(List<MosaicShape> shapes, List<BufferedImage> images, int start, int end,
                                   int w, int h) {
        this.newShapes = new ArrayList<>();
        this.startIndex = start;
        this.endIndex = end;
        this.shapes = shapes;
        this.images = images;
        this.width = w;
        this.height = h;
    }

    public void run() {
        for (int i = startIndex; i <= endIndex; i++) {
            if (i == images.size()) {
                break;
            }
            this.newShapes.add(new RectangleMosaicShape(images.get(i), width, height));
        }
        addAll();
    }

    private synchronized void addAll() {
        this.shapes.addAll(this.newShapes);
    }
}
