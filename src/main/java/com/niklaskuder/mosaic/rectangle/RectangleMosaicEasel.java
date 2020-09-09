package com.niklaskuder.mosaic.rectangle;

import com.niklaskuder.mosaic.base.BufferedArtImage;
import com.niklaskuder.mosaic.base.MosaicArtist;
import com.niklaskuder.mosaic.base.MosaicEasel;

import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RectangleMosaicEasel implements MosaicEasel {
    private final int numThreads;

    public RectangleMosaicEasel(int numThreads) {
        this.numThreads = numThreads;
        //System.out.println("Available Threads: " + numThreads);
    }

    public RectangleMosaicEasel() {
        this.numThreads = Runtime.getRuntime().availableProcessors();
        System.out.println("Available Threads: " + numThreads);
    }

    @Override
    public BufferedImage createMosaic(BufferedImage input, MosaicArtist artist) {
        BufferedArtImage image = new BufferedArtImage(input);
        System.out.println("Creating blank image...");
        //BufferedArtImage result = image.createBlankImage();

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);
        for (int i = 0; i < numThreads; i++) {
            System.out.println("Creating Thread " + i);
            executor.execute(new MosaicRunnable(image, artist, image));
        }
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MosaicRunnable.resetBounds();
        return image.toBufferedImage();
    }
}
