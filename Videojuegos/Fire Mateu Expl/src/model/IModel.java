package src.model;

import java.awt.image.BufferedImage;

public interface IModel {
    void update();
    void start();
    BufferedImage getImage();
}
