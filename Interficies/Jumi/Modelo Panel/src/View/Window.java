package View;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane; // Importante para que la tabla tenga cabeceras y scroll
import javax.swing.JTable;      // Importante para la tabla
import java.awt.*;// Esta importa toda la libreria de golpe(incluido canvas)

public class Window {
    public void createWindow(){
        JFrame frame = new JFrame();// Instanciamos el Jframe
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//Esto es para que se pueda cerrar el Jframe
        JPanel contentPanel = new JPanel(new GridBagLayout());//Intanciamos el Jpanel y le ponemos la organizacion GridBagLayout
        frame.setContentPane(contentPanel);// Seteamos cual va a ser el contenedor principal donde va a estar los elementos

        // Le ponemos GridBagLayout al buttonPanel para que sus elementos se centren automáticamente
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        
        // Le ponemos BorderLayout al controlPanel para que la tabla se expanda por todo el panel
        JPanel controlPanel = new JPanel(new BorderLayout());
        Canvas Lienzo = new Canvas();
        
        // Les ponemos el texto "Start" y "Stop"
        JButton boton1 = new JButton("Start");
        JButton boton2 = new JButton("Stop");
        
        // Hacemos los botones más grandes
        boton1.setPreferredSize(new Dimension(150, 50));
        boton2.setPreferredSize(new Dimension(150, 50));
        
        // Aquí añadimos los bordes a los paneles usando BorderFactory
        buttonPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        controlPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        // Creamos unas constraints simples solo para darles margen entre ellos dentro del buttonPanel
        GridBagConstraints cBotones = new GridBagConstraints();
        cBotones.insets = new Insets(10, 10, 10, 10);
        
        buttonPanel.add(boton1, cBotones);
        buttonPanel.add(boton2, cBotones);
        
        // --- CREACIÓN DE LA TABLA PARA EL CONTROLPANEL ---
        // 1. Definimos los nombres de las columnas
        String[] columnNames = {"ID", "Nombre", "Estado"};
        
        // 2. Definimos los datos de prueba (matriz de Objetos)
        Object[][] data = {
            {"1", "Proceso A", "Activo"},
            {"2", "Proceso B", "Detenido"},
            {"3", "Proceso C", "En espera"}
        };
        
        // 3. Instanciamos la tabla con sus datos y columnas
        JTable tabla = new JTable(data, columnNames);
        
        // 4. Metemos la tabla en un JScrollPane
        JScrollPane scrollPane = new JScrollPane(tabla);
        
        // 5. Añadimos el scrollPane al controlPanel en el centro para que ocupe todo
        controlPanel.add(scrollPane, BorderLayout.CENTER);
        // --------------------------------------------------

        contentPanel.add(buttonPanel,constraints(0, 0, 1, 1, 0.3, 0.5));//Añadimos a el control panel sus hijos button y Lienzo 
        contentPanel.add(controlPanel,constraints(0, 1, 1, 1, 0.3, 0.5));
        contentPanel.add(Lienzo,constraints(1, 0, 1, 2, 0.7, 1));
        
        frame.setSize(1200,700);
        frame.setMinimumSize(new Dimension(800,500));//Ponemos un minimo de tamaño
        frame.setLocationRelativeTo(null);//Al ponerlo null esta funcion lo pone en el centro de la pantalla
        frame.setVisible(true);//Hace que la pantalla sea visible
        
        

    }

    private GridBagConstraints constraints(// Funcion creada por Mateu para poenr los valores alaas consttaint 
        int x, int y, int width, int height, double weightX, double weightY){

            GridBagConstraints c = new GridBagConstraints();

            c.gridx = x;
            c.gridy = y;
            c.gridwidth = width;
            c.gridheight = height;
            c.weightx = weightX;
            c.weighty = weightY;

            c.fill = GridBagConstraints.BOTH; // el componente se estira para llenar su celda (ancho y alto)
            
            c.insets = new Insets(5,5,5,5); // margen de 5 px alrededor (arriba, izquierda, abajo, derecha)

            return c;
    }
}