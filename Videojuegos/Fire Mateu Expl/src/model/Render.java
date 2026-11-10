package src.model;

import java.awt.image.BufferedImage;

// Convierte las temperaturas del fuego en una imagen usando la paleta
public class Render {
    private final Fire fire;
    private final Palette palette;
    private final BufferedImage image;

    public Render (Fire fire,Palette palette){
        this.fire=fire;
        this.palette=palette;
        this.image= new BufferedImage(fire.getAncho(),fire.getAltura(),BufferedImage.TYPE_INT_ARGB);

    }

    public BufferedImage getImage(){
        int[][] temperatures = fire.getTemperature();

        for (int y = 0; y < fire.getAltura(); y++) {
            for (int x = 0; x < fire.getAncho(); x++) {
                // OJO: setRGB recibe primero x (columna) y luego y (fila)
                image.setRGB(x, y, palette.color(temperatures[y][x]));
            }
        }
        return image;
    }
}
