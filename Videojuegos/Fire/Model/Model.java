package Model;

import java.awt.image.BufferedImage;
import java.util.List;

public class Model implements IModel {
    private Render render = new Render();
    private Fire fire;
    private Palette palette;
    private volatile boolean running;

    @Override
    public void setDimension(int width, int height) {
        fire = new Fire(width, height);
    }

    @Override
    public BufferedImage getImage() {
        if (fire == null || palette == null)
            return null;
        return render.getImage(palette, fire.getTemperatures());
    }

    @Override
    public void setPalette(List<ColorTargetDTO> targets) {
        Palette p = new Palette();
        p.createPalette(targets);
        palette = p;
    }

    @Override
    public void setSparks(double percentage) {
        if (fire != null)
            fire.setSparks(percentage);
    }

    @Override
    public void setCoolPoints(double percentage) {
        if (fire != null)
            fire.setCoolPoints(percentage);
    }

    @Override
    public void play() {
        if (running || fire == null)
            return;
        running = true;
        Thread thread = new Thread(() -> {
            while (running) {
                fire.evolveTemperatures();
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void stop() {
        running = false;
    }
}
