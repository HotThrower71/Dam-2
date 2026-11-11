package src.view;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

import src.presenter.IPresenter;

// La ventana: pide la imagen al Presenter y se la da al Viewer para dibujarla
public class View implements IView, Runnable {
    private IPresenter presenter;

    private final JFrame frame;
    private final Viewer viewer;

    private final int FRAME_MS = 16;

    public View() {
        viewer = new Viewer();
        frame = new JFrame("Fire Animation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(viewer);
    }

    @Override
    public void setPresenter(IPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void start(){
        //El tamaño de la ventana se define gracias a lo siguiente:
        BufferedImage first = presenter.getImage();

        //usamos first para settear la imagen
        viewer.setPreferredSize(new Dimension(first.getWidth(),first.getHeight()));
        viewer. setImage (first);
        // Frame -ventana principal
        frame.pack(); // ajusta la ventana al tamaño del lienzo
        frame.setVisible(true);

        //Hilos
        Thread thread = new Thread(this,"fire-view");
        thread.setDaemon(true);
        thread.start();
    }
    // Lo que hace el hilo: pedir imagen, pintarla y esperar, para siempre
    @Override
    public void run() {
        while (true) {
            viewer.setImage(presenter.getImage());
            try {
                Thread.sleep(FRAME_MS);
            } catch (Exception e) {
                // TODO: handle exception
                return;
            }
        }
    }

}
