package src.view;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

// Lienzo que dibuja la imagen que le den
public class Viewer extends Canvas {
    private BufferedImage image;

    public void setImage (BufferedImage image){
        this.image = image;//Guarda imagen

        repaint(); // avisa de que hay que redibujar
    }

    //Funciones provenientes del Canvas
    @Override
    public void paint(Graphics g){
        g.drawImage(image,0,0,getWidth(),getHeight(),null);
    }

    // Sin esto Canvas borra antes de dibujar y la animacion parpadea
    @Override
    public void update (Graphics g){
        paint(g);
    }

}
