package src.model;
import java.util.Random;
public class Fire {
    private final int sparksPercentage =40;
    private final int CoolPointsPercentage =30;
    private final int randomizer = 1;

    private final int ancho;
    private final int altura;
    private final int [][]temperatures;

    private final Random random = new Random();



    public Fire(int ancho,int altura){
        this.ancho=ancho;
        this.altura=altura;
        this.temperatures=new int[altura][ancho];

    }
    public void sparksGeneration(){
        int base = altura -1;
        for (int x = 0; x < ancho; x++) {
            if (random.nextInt(100)<sparksPercentage){
                temperatures[base][x]=255;
            }
        }
    }

    public void coolPointsGeneration(){
        int base = altura -1;
        for (int x = 0; x < ancho; x++) {
            if (random.nextInt(100)<CoolPointsPercentage){
                temperatures[base][x]=0;
            }
        }

    }

    public  void updateFlames(){
        for(int y = altura -2; y >=0;y--){
            for (int x = 0; x < ancho; x++) {
                

                int actual = temperatures [y][x];
                int left = temperatures [y-1][x-1];
                int center = temperatures [y-1][x];
                int right = temperatures [y][x+1];
            
                int t = (actual + left +right + center)/4;

            temperatures[y][x] = Math.max(t, 0);
            
            }
        }
    }

    public int [][] getTemperature(){return temperatures;}
    public int getAncho() {return ancho;}
    public int getAltura() {return altura;}
}
