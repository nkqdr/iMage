package com.niklaskuder.mosaic.base;

import java.awt.image.BufferedImage;

public interface MosaicEasel {
    BufferedImage createMosaic(BufferedImage input, MosaicArtist artist);
}
