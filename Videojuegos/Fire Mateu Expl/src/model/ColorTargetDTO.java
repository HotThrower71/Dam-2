package src.model;
public class ColorTargetDTO {
    private final int temperature;
    private final int red;
    private final int green;
    private final int blue;
    private final int alpha;

    public ColorTargetDTO(int temperature,int red,int green,int blue,int alpha){
        this.temperature=temperature;
        this.green=green;
        this.red=red;
        this.blue=blue;
        this.alpha=alpha;
    }
public int getTemperature() {return temperature;}
    public int getAlpha() {return alpha;}
    public int getBlue() {return blue;}
    public int getGreen() {return green;}
    public int getRed() {return red;}
}
