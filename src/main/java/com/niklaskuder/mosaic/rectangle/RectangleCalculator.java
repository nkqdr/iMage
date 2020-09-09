package com.niklaskuder.mosaic.rectangle;

import com.niklaskuder.mosaic.base.AbstractCalculator;

import java.awt.image.BufferedImage;

public final class RectangleCalculator extends AbstractCalculator {

  private static RectangleCalculator instance;

  private RectangleCalculator() {

  }

  public static RectangleCalculator getInstance() {
    if (instance == null) {
      instance = new RectangleCalculator();
    }
    return instance;
  }

  @Override
  public int xStart(BufferedImage region) {
    return 0;
  }

  @Override
  public int yStart(BufferedImage region, int x) {
    return 0;
  }

  @Override
  public int getCorrectRow(BufferedImage region, int y) {
    return y;
  }

  @Override
  public int getXBound(BufferedImage region) {
    return region.getWidth();
  }

  @Override
  public float getYBound(BufferedImage region, int x) {
    return region.getHeight();
  }

}
