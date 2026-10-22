package Model;

import java.awt.image.BufferedImage;

public class Render {
    private BufferedImage image;

    public BufferedImage getImage(Palette palette, int[][] temps) {
        int height = temps.length;
        int width = temps[0].length;
        if (image == null || image.getWidth() != width || image.getHeight() != height)
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        render(palette, temps);
        return image;
    }

    private void render(Palette palette, int[][] temps) {
        for (int y = 0; y < temps.length; y++)
            for (int x = 0; x < temps[y].length; x++)
                image.setRGB(x, y, palette.getColor(temps[y][x]));
    }
}
