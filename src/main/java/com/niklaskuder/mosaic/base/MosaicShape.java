package com.niklaskuder.mosaic.base;

import java.awt.image.BufferedImage;

public interface MosaicShape {
    void decreaseDisabled();
    boolean isDisabled();
    void setDisabled(int amount);
    BufferedImage getThumbnail();
    int getAverageColor();
    void drawMe(BufferedArtImage image);
    int getWidth();
    int getHeight();

}
