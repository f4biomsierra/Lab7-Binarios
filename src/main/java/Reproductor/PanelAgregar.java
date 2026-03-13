/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Reproductor;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
/**
 *
 * @author Fabio Sierra
 */
public class PanelAgregar extends JPanel {
    JTextField nombreField;
    JTextField artistaField;
    JTextField generoField;
    JButton audioButton;
    JButton imagenButton;
    JButton agregarButton;
    String rutaAudio;
    String rutaImagen;

    public PanelAgregar(Runnable onAgregar) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setPreferredSize(new Dimension(250, 0));

        // Método auxiliar para añadir campos con etiqueta arriba
        crearCampoTexto("Nombre de Canción:", nombreField = new JTextField());
        crearCampoTexto("Artista:", artistaField = new JTextField());
        crearCampoTexto("Género:", generoField = new JTextField());

        add(Box.createVerticalStrut(10));
        
        audioButton = new JButton("📁 Seleccionar Audio");
        audioButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        configurarBotonArchivo(audioButton, true);
        add(audioButton);

        add(Box.createVerticalStrut(5));

        imagenButton = new JButton("🖼 Seleccionar Imagen");
        imagenButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        configurarBotonArchivo(imagenButton, false);
        add(imagenButton);

        add(Box.createVerticalStrut(20));

        agregarButton = new JButton("✨ Agregar a la Lista");
        agregarButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        agregarButton.setBackground(new Color(70, 130, 180));
        agregarButton.setForeground(Color.BLACK);
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
        audioButton.setText("📁 Seleccionar Audio");
        imagenButton.setText("🖼 Seleccionar Imagen");
        rutaAudio = null;
        rutaImagen = null;
    }

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
