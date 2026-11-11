package src.model;

import java.awt.image.BufferedImage;

// Junta Fire + Palette + Render y hace avanzar el fuego en su propio hilo
public class Model implements IModel, Runnable {
    private final int ancho = 320;
    private final int altura = 200;
    private final int frameMS = 33;

    private final Fire fire;
    private final Render render;

    public Model() {
        fire = new Fire(ancho, altura);
        render = new Render(fire, Palette.createPalette());
    }

    @Override
    public void start() {
        Thread thread = new Thread(this,"Model-Fire");// Modelo inciia el hilo
        thread.setDaemon(true);// El hilo se cierra al cerrar el program
        thread.start();// El hilo ejecuta

    }

    // Lo que hace el hilo: actualizar y esperar, para siempre
    @Override
    public void run() {
        while (true) {
            update();
            try {
                Thread.sleep(frameMS);

            } catch (InterruptedException e) {
                return;
            }
        }
    }

    @Override
    public void update() {
        fire.sparksGeneration();
        fire.coolPointsGeneration();
        fire.updateFlames();

    }

    @Override
    public BufferedImage getImage() {
        return render.getImage();
    }

}
