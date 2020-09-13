package com.niklaskuder.mosaic.rectangle;

import com.niklaskuder.mosaic.base.BufferedArtImage;
import com.niklaskuder.mosaic.base.MosaicArtist;
import com.niklaskuder.mosaic.base.MosaicShape;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RectangleMosaicArtist implements MosaicArtist {

    private static final double EPS = 1E-5;
    private static final double TOLERANCE = 10;
    private static final int MIN_PICTURES = 2;
    private static final int LIST_SIZE = 20;
    private static final int TILE_SPACING = 5;

    private List<MosaicShape> shapes;
    private int tileWidth;
    private int tileHeight;

    public RectangleMosaicArtist(List<BufferedImage> images, int tileWidth, int tileHeight) {
        if (images.isEmpty()) {
            throw new IllegalArgumentException("no tiles provided");
        }
        int numThreads = Runtime.getRuntime().availableProcessors();
        initialize(images, tileWidth, tileHeight, numThreads);
    }

    /*public RectangleMosaicArtist(List<BufferedImage> images, int tileWidth, int tileHeight, int numThreads) {
        if (images.isEmpty()) {
            throw new IllegalArgumentException("no tiles provided");
        }
        initialize(images, tileWidth, tileHeight, numThreads);
    }*/

    public List<MosaicShape> getShapes() {
        return shapes;
    }

    private void initialize(List<BufferedImage> images, int tileWidth, int tileHeight, int numThreads) {
        this.shapes = new ArrayList<>();
        this.tileHeight = tileHeight;
        this.tileWidth = tileWidth;
        int totalLength = images.size();
        int divider = totalLength / numThreads;
        int remainder = totalLength % numThreads;

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);
        executor.execute(new RectangleArtistRunnable(shapes, images, 0, divider, tileWidth, tileHeight));
        for (int i = 2; i <= numThreads; i++) {
            executor.execute(new RectangleArtistRunnable(shapes, images, (i - 1) * divider + 1, i * divider,
                    tileWidth, tileHeight));
        }
        if (remainder != 0) {
            executor.execute(new RectangleArtistRunnable(shapes, images, totalLength - remainder, totalLength,
                    tileWidth, tileHeight));
        }
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getTileWidth() {
        return tileWidth;
    }

    @Override
    public int getTileHeight() {
        return tileHeight;
    }

    @Override
    public List<BufferedImage> getThumbnails() {
        List<BufferedImage> thumbnails = new ArrayList<>();
        for (MosaicShape shape : shapes) {
            thumbnails.add(shape.getThumbnail());
        }
        return thumbnails;
    }

    @Override
    public BufferedArtImage getTileForRegion(BufferedArtImage region) {
        if (region.getWidth() > this.tileWidth || region.getHeight() > this.tileHeight) {
            throw new IllegalArgumentException("requested tiling is greater than tileWidth or tileHeight");
        }
        int average = RectangleCalculator.getInstance().averageColor(region.toBufferedImage());
        MosaicShape tile = findNearest(average, shapes);
        tile.drawMe(region);
        return region;
    }

    /**
     * Find the shape with the best matching color from a list of shapes.
     *
     * @param target the target color as argb.
     * @param shapes the available shapes (cannot be empty)
     * @return the best matching shape
     */
    protected final MosaicShape findNearest(int target, Collection<MosaicShape> shapes) {
        List<MosaicShape> nearest = new ArrayList<>();
        int additionalListSize = 0;

        Iterator<MosaicShape> iter = shapes.iterator();
        MosaicShape first = iter.next();
        while (first.isDisabled()) {
            //System.out.println("First is disabled.");
            if (!iter.hasNext()) {
                throw new IllegalStateException("All tiles disabled.");
            }
            first = iter.next();
        }
        nearest.add(first);

        double closestDist = colorError(target, first.getAverageColor());

        while (iter.hasNext()) {
            MosaicShape next = iter.next();

            while (next.isDisabled()) {
                //System.out.println("Next is disabled.");
                if (!iter.hasNext()) {
                    return nearest.get(0);
                }
                //Skip disabled Tile
                next = iter.next();
            }

            double nextDist = colorError(target, next.getAverageColor());
            if (Math.abs(closestDist - nextDist) < EPS) {
                //Distances equal
                additionalListSize++;
                nearest.add(next);
            } else if (nextDist < closestDist) {
                //New smallest
                if (nearest.size() >= LIST_SIZE + additionalListSize) {
                    nearest.remove(0);
                }
                if (nextDist + TOLERANCE < closestDist) {
                    nearest.remove(nearest.size() - 1);
                }
                nearest.add(next);
                closestDist = nextDist;
            }
        }
        System.out.println("Size: " + nearest.size());

        List<MosaicShape> toRemove = new ArrayList<>();
        for (MosaicShape shape : nearest) {
            if (colorError(target, shape.getAverageColor()) > TOLERANCE && nearest.size() - toRemove.size() > MIN_PICTURES) {
                toRemove.add(shape);
            }
        }
        nearest.removeAll(toRemove);
        MosaicShape returnShape = nearest.get((int) (Math.random() * nearest.size()));
        returnShape.setDisabled(TILE_SPACING);
        return returnShape;
    }

    /**
     * Calculate the difference between two argb colors as euclidean distance.<br> Range: [0, sqrt(4 *
     * pow(255, 2))]
     *
     * @param colorA the first color
     * @param colorB the second color
     * @return the difference of the colors
     */
    private static double colorError(int colorA, int colorB) {
        Color a = new Color(colorA, true);
        Color b = new Color(colorB, true);
        int colorError = (int) Math.pow(a.getRed() - b.getRed(), 2);
        colorError += (int) Math.pow(a.getGreen() - b.getGreen(), 2);
        colorError += (int) Math.pow(a.getBlue() - b.getBlue(), 2);
        colorError += (int) Math.pow(a.getAlpha() - b.getAlpha(), 2);
        return Math.sqrt(colorError);
    }
}
