package Model;

import java.util.Random;

public class Fire {
    private int[][] temperatures; // [fila][columna], valores 0..255
    private double coolPointsPercentage;
    private double sparkPercentage;
    private final Random random = new Random();

    public Fire(int width, int height) {
        temperatures = new int[height][width];
    }

    public void setSparks(double percentage) {
        this.sparkPercentage = percentage;
    }

    public void setCoolPoints(double percentage) {
        this.coolPointsPercentage = percentage;
    }

    public void evolveTemperatures() {
        int rows = temperatures.length;
        int cols = temperatures[0].length;

        // Fila de abajo: solo puntos calientes y puntos frios
        int last = rows - 1;
        for (int c = 0; c < cols; c++) {
            if (random.nextDouble() < sparkPercentage)
                temperatures[last][c] = 255;
            if (random.nextDouble() < coolPointsPercentage)
                temperatures[last][c] = 0;
        }

        // Resto: media de las vecinas de abajo (frame anterior), de arriba hacia abajo
        for (int r = 0; r < last; r++) {
            for (int c = 0; c < cols; c++) {
                int left = temperatures[r + 1][Math.max(c - 1, 0)];
                int center = temperatures[r + 1][c];
                int right = temperatures[r + 1][Math.min(c + 1, cols - 1)];
                int below2 = temperatures[Math.min(r + 2, last)][c];
                temperatures[r][c] = Math.max(0, (left + center + right + below2) / 4 - 1);
            }
        }
    }

    public int[][] getTemperatures() {
        return temperatures;
    }
}
