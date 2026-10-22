package Model;

import java.awt.Color;

/**
 * DTO: solo transporta datos. "A esta temperatura le corresponde este color".
 * La paleta usa varios de estos como puntos de referencia y rellena los huecos entre ellos.
 */
public class ColorTargetDTO {
    public int temperature; // 0..255
    public Color color;

    public ColorTargetDTO(int temperature, Color color) {
        this.temperature = temperature;
        this.color = color;
    }
}
