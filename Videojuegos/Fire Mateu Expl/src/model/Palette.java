package src.model;

import java.util.ArrayList;
import java.util.List;
import java.awt.Color;

// Traduce una temperatura (0-255) a un color, mezclando entre unos pocos colores clave
public class Palette {
    private final int[] colors = new int[256]; // colors[temperatura] = color

public Palette (List<ColorTargetDTO> targets){
    //Recorrer Paleta
    // Cada vuelta es un tramo from -> to (con 4 targets hay 3 tramos, por eso el -1)
    for (int i = 0; i < targets.size()-1; i++) {
        ColorTargetDTO from =targets.get(i);
        ColorTargetDTO to =targets.get(i+1);

        int range = to.getTemperature() - from.getTemperature();
        for (int t = from.getTemperature(); t<= to.getTemperature();t++){
            // 0 = estamos en from, 1 = estamos en to, 0.5 = a la mitad
            double ratio = (double)(t - from.getTemperature())/range;//valor entre 0 y  1
        // Ejemplos (tramo from=0 -> to=20, range=20, canal rojo de 0 a 224):
        // 1) t=0  -> ratio = (0-0)/20  = 0.0  -> red = 0 + (224-0)*0.0  = 0    (negro, inicio del tramo)
        // 2) t=10 -> ratio = (10-0)/20 = 0.5  -> red = 0 + (224-0)*0.5  = 112  (mitad del camino)
        // 3) t=20 -> ratio = (20-0)/20 = 1.0  -> red = 0 + (224-0)*1.0  = 224  (rojo completo, final del tramo)

            int alpha = mix(from.getAlpha(),to.getAlpha(),ratio);
            int green = mix(from.getGreen(),to.getGreen(),ratio);
            int blue = mix(from.getBlue(),to.getBlue(),ratio);
            int red = mix(from.getRed(),to.getRed(),ratio);

        colors[t] = new Color(red,green,blue,alpha).getRGB();
        }
    }
}
public int color (int temperatura){
    return colors[temperatura];
}

// Valor entre v1 y v2 segun el ratio (0 -> v1, 1 -> v2, 0.5 -> el medio)
private int mix (int v1,int v2,double ratio){
    return (int) Math.round(v1 + (v2-v1)*ratio);
}

    // Orden de ColorTargetDTO: (temperatura, rojo, verde, azul, alpha)
    public static Palette createPalette(){
        List<ColorTargetDTO> targets = new ArrayList<>();
        targets.add(new ColorTargetDTO(0, 0, 0, 0, 0));//negro
        targets.add(new ColorTargetDTO(20, 224, 58, 0, 255));
        targets.add(new ColorTargetDTO(80, 255, 208, 32, 255));
        targets.add(new ColorTargetDTO(255 ,  255,255,255, 255));//Blanco

        return new Palette(targets);
    }
}
