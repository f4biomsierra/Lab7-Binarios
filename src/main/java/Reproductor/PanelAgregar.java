/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Reproductor;

import javax.swing.*;
import java.awt.*;

/**
 * @author Fabio Sierra
 */

public class PanelAgregar extends JPanel {
    JTextField nombreField;
    JTextField artistaField;
    JTextField generoField;
    JTextField duracionField;
    JButton audioButton;
    JButton imagenButton;
    JButton agregarButton;
    String rutaAudio;
    String rutaImagen;

    public PanelAgregar(Runnable onAgregar) {
        setLayout(new GridLayout(7, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(new JLabel("Nombre:"));
        nombreField = new JTextField();
        add(nombreField);

        add(new JLabel("Artista:"));
        artistaField = new JTextField();
        add(artistaField);

        add(new JLabel("Género:"));
        generoField = new JTextField();
        add(generoField);

        add(new JLabel("Duración (Auto):"));
        duracionField = new JTextField("00:00");
        duracionField.setEditable(false);
        duracionField.setBackground(new Color(230, 230, 230)); 
        add(duracionField);

        add(new JLabel("Archivo MP3:"));
        audioButton = new JButton("Seleccionar Audio");
        add(audioButton);

        add(new JLabel("Carátula:"));
        imagenButton = new JButton("Seleccionar Imagen");
        add(imagenButton);
        imagenButton.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                rutaImagen = fc.getSelectedFile().getAbsolutePath();
                imagenButton.setText(fc.getSelectedFile().getName());
            }
        });

        add(new JLabel(""));
        agregarButton = new JButton("Guardar Canción");
        add(agregarButton);
    }

    private void crearCampoTexto(String titulo, JTextField campo) {
        JLabel label = new JLabel(titulo);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(label);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        add(campo);
        add(Box.createVerticalStrut(5));
    }

    private void configurarBotonArchivo(JButton boton, boolean esAudio) {
        boton.addActionListener(e -> {
            JFileChooser buscador = new JFileChooser();
            if (buscador.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                String ruta = buscador.getSelectedFile().getAbsolutePath();
                if (esAudio) {
                    rutaAudio = ruta;
                } else {
                    rutaImagen = ruta;
                }
                boton.setText(buscador.getSelectedFile().getName());
            }
        });
    }

    public void limpiarCampos() {
        nombreField.setText("");
        artistaField.setText("");
        generoField.setText("");
        duracionField.setText("00:00");
        audioButton.setText("Seleccionar Audio");
        imagenButton.setText("Seleccionar Imagen");
        rutaAudio = null;
        rutaImagen = null;
    }
    
    public void setRutaAudio(String ruta) { this.rutaAudio = ruta; }

    public String getNombre(){
        return nombreField.getText();
    }
    
    public String getArtista(){
        return artistaField.getText();
    }
    
    public String getGenero(){
        return generoField.getText();
    }
    
    public String getRutaAudio(){
        return rutaAudio;
    }
    
    public String getRutaImagen(){
        return rutaImagen;
    }
    
    
}
