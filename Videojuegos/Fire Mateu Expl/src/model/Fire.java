package src.model;
import java.util.Random;

// Guarda la temperatura de cada punto y la hace evolucionar
public class Fire {
    private final int sparksPercentage =40;
    private final int CoolPointsPercentage =30;
    private final int randomizer = 1;

    private final int ancho;
    private final int altura;

    // temperatures[fila][columna], de 0 (frio) a 255. La ultima fila es la base del fuego
    
    private final int [][]temperatures;
    private final Random random = new Random();



    public Fire(int ancho,int altura){
        this.ancho=ancho;
        this.altura=altura;
        this.temperatures=new int[altura][ancho];

    }
    // Enciende (255) un 40% de los puntos de la base
    public void sparksGeneration(){
        int base = altura -1;
        for (int x = 0; x < ancho; x++) {
            if (random.nextInt(100)<sparksPercentage){
                temperatures[base][x]=255;
            }
        }
    }

    // Apaga (0) un 30% de la base para que las llamas sean irregulares
    public void coolPointsGeneration(){
        int base = altura -1;
        for (int x = 0; x < ancho; x++) {
            if (random.nextInt(100)<CoolPointsPercentage){
                temperatures[base][x]=0;
            }
        }

    }

    // Sube el calor: cada punto = media de si mismo y los 3 de la fila de abajo, menos un enfriamiento al azar
    public  void updateFlames(){
        for(int y = altura -2; y >=0;y--){
            for (int x = 0; x < ancho; x++) {


                int actual = temperatures [y][x];
                // max/min evitan salirnos del array en los bordes
                int left = temperatures [ y + 1][Math.max(x-1,0)];
                int center = temperatures [y + 1][x];
                int right = temperatures [y + 1][Math.min (x+1,ancho-1)];

                int t = (actual + left +right + center)/4 - random.nextInt(2+1);

            temperatures[y][x] = Math.max(t, 0); // nunca negativo

            }
        }
    }

    public int [][] getTemperature(){return temperatures;}
    public int getAncho() {return ancho;}
    public int getAltura() {return altura;}
}
