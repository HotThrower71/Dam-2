package Model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Palette {
    private int[] colors = new int[256];

    public void createPalette(List<ColorTargetDTO> targets) {
        List<ColorTargetDTO> sorted = new ArrayList<>(targets);
        sorted.sort(Comparator.comparingInt(t -> t.temperature));

        // Antes del primer objetivo: su color
        ColorTargetDTO first = sorted.get(0);
        for (int t = 0; t <= first.temperature && t < 256; t++)
            colors[t] = first.color.getRGB();

        // Entre objetivos: siempre se parte del color inicial + paso * i
        for (int i = 0; i < sorted.size() - 1; i++) {
            ColorTargetDTO a = sorted.get(i);
            ColorTargetDTO b = sorted.get(i + 1);
            int steps = b.temperature - a.temperature;
            for (int s = 0; s <= steps; s++) {
                int temp = a.temperature + s;
                if (temp > 255)
                    break;
                colors[temp] = new Color(
                        channel(a.color.getRed(), b.color.getRed(), s, steps),
                        channel(a.color.getGreen(), b.color.getGreen(), s, steps),
                        channel(a.color.getBlue(), b.color.getBlue(), s, steps),
                        channel(a.color.getAlpha(), b.color.getAlpha(), s, steps)).getRGB();
            }
        }

        // Despues del ultimo objetivo: su color
        ColorTargetDTO last = sorted.get(sorted.size() - 1);
        for (int t = last.temperature; t < 256; t++)
            colors[t] = last.color.getRGB();
    }

    private int channel(int from, int to, int step, int steps) {
        double inc = (double) (to - from) / steps;
        return (int) Math.round(from + inc * step);
    }

    public int getColor(int temp) {
        return colors[Math.max(0, Math.min(255, temp))];
    }
}
