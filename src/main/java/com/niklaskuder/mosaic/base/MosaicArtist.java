package com.niklaskuder.mosaic.base;

import java.awt.image.BufferedImage;
import java.util.List;

public interface MosaicArtist {
    int getTileWidth();

    int getTileHeight();

    List<MosaicShape> getShapes();

    List<BufferedImage> getThumbnails();

    BufferedArtImage getTileForRegion(BufferedArtImage var1);
}
