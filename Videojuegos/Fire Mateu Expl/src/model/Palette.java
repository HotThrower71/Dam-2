package src.model;

import java.util.ArrayList;
import java.util.List;
import java.awt.Color;
public class Palette {
    private final int[] colors = new int[256];

public Palette (List<ColorTargetDTO> targets){
    //Recorrer Paleta
    for (int i = 0; i < targets.size(); i++) {
        ColorTargetDTO from =targets.get(i);
        ColorTargetDTO to =targets.get(i+1);

        int range = to.getTemperature() - from.getTemperature();
        for (int t = from.getTemperature(); t<= to.getTemperature();t++){
            double ratio = (double)(t - from.getTemperature())/range;//valor entre 0 y  1 

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

private int mix (int v1,int v2,double ratio){
    return (int) Math.round(v1 + (v2-v1)*ratio);
}

    public static Palette createPalette(){
        List<ColorTargetDTO> targets = new ArrayList<>();
        targets.add(new ColorTargetDTO(0, 0, 0, 0, 0));
        targets.add(new ColorTargetDTO(20, 224, 58, 0, 255));
        targets.add(new ColorTargetDTO(80, 255, 208, 32, 255));
        targets.add(new ColorTargetDTO(255 ,  255,255,255, 255));

        return new Palette(targets);
    }
}
