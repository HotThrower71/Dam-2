package Model;

import java.awt.image.BufferedImage;
import java.util.List;

public interface IModel {
    BufferedImage getImage();
    void setPalette(List<ColorTargetDTO> targets);
    void setDimension(int width, int height);
    void setSparks(double percentage);
    void setCoolPoints(double percentage);
    void play();
    void stop();
}
