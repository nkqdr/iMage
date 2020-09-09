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

    private static final double EPS = 1;
    private static final double TOLERANCE = 10;

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

    public RectangleMosaicArtist(List<BufferedImage> images, int tileWidth, int tileHeight, int numThreads) {
        if (images.isEmpty()) {
            throw new IllegalArgumentException("no tiles provided");
        }
        initialize(images, tileWidth, tileHeight, numThreads);
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
        BufferedArtImage result = region.createBlankImage();
        int average = RectangleCalculator.getInstance().averageColor(region.toBufferedImage());
        MosaicShape tile = findNearest(average, shapes);
        tile.drawMe(result);
        return result;
    }

    /**
     * Find the shape with the best matching color from a list of shapes.
     *
     * @param target
     *     the target color as argb.
     * @param shapes
     *     the available shapes (cannot be empty)
     * @return the best matching shape
     */
    protected final MosaicShape findNearest(int target,
                                            Collection<MosaicShape> shapes) {
        List<MosaicShape> nearest = new ArrayList<>();

        Iterator<MosaicShape> iter = shapes.iterator();
        MosaicShape first = iter.next();
        nearest.add(first);

        double closestDist = colorError(target, first.getAverageColor());

        while (iter.hasNext()) {
            MosaicShape next = iter.next();
            double nextDist = colorError(target, next.getAverageColor());

            if (nextDist < closestDist) {
                nearest.add(next);
                closestDist = nextDist;
                nearest.remove(first);
                break;
            }

            /*if (Math.abs(dist - nextDist) < EPS) {
                // Distances equal
                nearest.add(next);
            } else if (nextDist < dist) {
                // New smallest
                nearest.clear();
                nearest.add(next);
                dist = nextDist;
            }*/
        }

        while (iter.hasNext()) {
            MosaicShape nextE = iter.next();
            double nextDistE = colorError(target, nextE.getAverageColor());
            if (nextDistE <= TOLERANCE) {
                nearest.add(nextE);
            } else if (nextDistE <= closestDist) {
                nearest.add(nextE);
                closestDist = nextDistE;
            }
        }
        System.out.println("Size: " + nearest.size());
        return nearest.get((int) (Math.random() * nearest.size()));
    }

    /**
     * Calculate the difference between two argb colors as euclidean distance.<br> Range: [0, sqrt(4 *
     * pow(255, 2))]
     *
     * @param colorA
     *     the first color
     * @param colorB
     *     the second color
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