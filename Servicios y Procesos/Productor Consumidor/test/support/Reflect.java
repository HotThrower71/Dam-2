package test.support;

import java.lang.reflect.Field;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

// Las pruebas de los paneles necesitan escribir en sus campos privados, que no tienen setters: se hace por reflexion
public final class Reflect {
    private Reflect() {
    }

    private static Object field(Object owner, String name) {
        try {
            Field f = owner.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return f.get(owner);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("No existe el campo " + name, e);
        }
    }

    public static void setText(Object panel, String name, String text) {
        ((JTextField) field(panel, name)).setText(text);
    }

    public static void setSelected(Object panel, String name, boolean selected) {
        ((JCheckBox) field(panel, name)).setSelected(selected);
    }

    public static void selectItem(Object panel, String name, String item) {
        ((JComboBox<?>) field(panel, name)).setSelectedItem(item);
    }

    public static String message(Object panel) {
        return ((JLabel) field(panel, "message")).getText();
    }

    public static String areaText(Object panel) {
        return ((JTextArea) field(panel, "text")).getText();
    }

    public static javax.swing.AbstractButton button(Object panel, String name) {
        return (javax.swing.AbstractButton) field(panel, name);
    }
}
