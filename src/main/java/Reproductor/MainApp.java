/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Reproductor;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.io.*;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Header;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * @author Fabio Sierra
 */

public class MainApp extends JFrame {
    private ListaCanciones listaCanciones;
    private DefaultListModel<Nodo> modeloLista;
    private JList<Nodo> jlist;
    private JLabel imagenLabel;
    private JLabel duracionLabel; 
    private Reproductor reproductor;
    private PlaylistArchivo archivo;

    public MainApp() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { e.printStackTrace(); }

        listaCanciones = new ListaCanciones();
        archivo = new PlaylistArchivo();
        listaCanciones = archivo.cargar();
        reproductor = new Reproductor();

        setTitle("Reproductor de Música");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));

        modeloLista = new DefaultListModel<>();
        jlist = new JList<>(modeloLista);
        for (int contador = 0; contador < listaCanciones.size(); contador++) {
            modeloLista.addElement(listaCanciones.getSong(contador));
        }

        JScrollPane scrollLista = new JScrollPane(jlist);
        scrollLista.setPreferredSize(new Dimension(280, 0));
        scrollLista.setBorder(BorderFactory.createTitledBorder("Tu Playlist"));
        add(scrollLista, BorderLayout.WEST);

        JPanel panelVisualizador = new JPanel(new BorderLayout());
        panelVisualizador.setBackground(Color.DARK_GRAY);
        
        imagenLabel = new JLabel("Seleccione una canción", SwingConstants.CENTER);
        imagenLabel.setForeground(Color.LIGHT_GRAY);
        
        duracionLabel = new JLabel("Duración: --:--", SwingConstants.CENTER);
        duracionLabel.setForeground(Color.WHITE);
        duracionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        duracionLabel.setBorder(new EmptyBorder(10, 0, 10, 0));

        panelVisualizador.add(imagenLabel, BorderLayout.CENTER);
        panelVisualizador.add(duracionLabel, BorderLayout.SOUTH);
        add(panelVisualizador, BorderLayout.CENTER);

        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 15));
        JButton playBtn = new JButton("▶ Play");
        JButton pauseBtn = new JButton("⏸ Pause");
        JButton stopBtn = new JButton("⏹ Stop");
        JButton removeBtn = new JButton("Remove");
        JButton addBtn = new JButton("Add");

        panelControles.add(playBtn);
        panelControles.add(pauseBtn);
        panelControles.add(stopBtn);
        panelControles.add(removeBtn);
        panelControles.add(addBtn);
        add(panelControles, BorderLayout.SOUTH);

        // --- EVENTOS ---
        playBtn.addActionListener(e -> {
            Nodo seleccionado = jlist.getSelectedValue();
            if (seleccionado != null) {
                reproductor.play(seleccionado.rutaAudio);
                actualizarInterfaz();
            }
        });

        pauseBtn.addActionListener(e -> reproductor.pause());
        stopBtn.addActionListener(e -> reproductor.stop());
        removeBtn.addActionListener(e -> eliminarCancion());
        addBtn.addActionListener(e -> mostrarVentanaAgregar());

        jlist.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) actualizarInterfaz();
        });

        setVisible(true);
    }

    public String calcularDuracion(String ruta) {
        try (FileInputStream fis = new FileInputStream(new File(ruta))) {
            Bitstream bitstream = new Bitstream(fis);
            Header encabezado = bitstream.readFrame();
            long milisegundos = (long) encabezado.total_ms((int) new File(ruta).length());
            long segTotal = milisegundos / 1000;
            bitstream.close();
            return String.format("%02d:%02d", segTotal / 60, segTotal % 60);
        } catch (Exception e) { return "00:00"; }
    }

    private void mostrarVentanaAgregar() {
        JDialog ventana = new JDialog(this, "Nueva Canción", true);
        PanelAgregar panel = new PanelAgregar(null);

        panel.audioButton.addActionListener(e -> {
            JFileChooser selectorAudio = new JFileChooser();

            FileNameExtensionFilter filtroMp3 = new FileNameExtensionFilter("Solo archivos MP3", "mp3");
            selectorAudio.setFileFilter(filtroMp3);

            selectorAudio.setAcceptAllFileFilterUsed(false);

            if (selectorAudio.showOpenDialog(panel) == JFileChooser.APPROVE_OPTION) {
                File archivoSeleccionado = selectorAudio.getSelectedFile();

                if (archivoSeleccionado.getName().toLowerCase().endsWith(".mp3")) {
                    String ruta = archivoSeleccionado.getAbsolutePath();
                    panel.setRutaAudio(ruta);
                    panel.audioButton.setText(archivoSeleccionado.getName());
                    panel.duracionField.setText(calcularDuracion(ruta));
                } else {
                    JOptionPane.showMessageDialog(ventana, "Por favor, seleccione un archivo con extensión .mp3");
                }
            }
        });

        panel.agregarButton.addActionListener(e -> {
            if (panel.getNombre().isEmpty() || panel.getArtista().isEmpty() || panel.getRutaAudio() == null) {
                JOptionPane.showMessageDialog(ventana, "Error: Nombre, Artista y Audio son obligatorios.", "Campos vacíos", JOptionPane.ERROR_MESSAGE);
            } else {
                int codigo = listaCanciones.size() + 1;
                Nodo nuevo = new Nodo(codigo, panel.getNombre(), panel.getArtista(),
                        panel.getGenero(), panel.getRutaAudio(),
                        panel.getRutaImagen(), panel.duracionField.getText());

                listaCanciones.addSong(nuevo);
                modeloLista.addElement(nuevo);
                archivo.guardar(listaCanciones);

                ventana.dispose();
                JOptionPane.showMessageDialog(this, "Canción guardada correctamente.");
            }
        });

        ventana.add(panel);
        ventana.pack();
        ventana.setLocationRelativeTo(this);
        ventana.setVisible(true);
    }

    private void actualizarInterfaz() {
        Nodo n = jlist.getSelectedValue();
        if (n != null) {
            if (n.rutaImagen != null && !n.rutaImagen.isEmpty()) {
                ImageIcon icon = new ImageIcon(n.rutaImagen);
                Image img = icon.getImage().getScaledInstance(350, 350, Image.SCALE_SMOOTH);
                imagenLabel.setIcon(new ImageIcon(img));
                imagenLabel.setText("");
            } else {
                imagenLabel.setIcon(null);
                imagenLabel.setText("Sin carátula");
            }
            duracionLabel.setText("Duración: " + n.duracion);
        }
    }

    private void eliminarCancion() {
        Nodo n = jlist.getSelectedValue();
        if (n != null) {
            reproductor.stop();
            listaCanciones.removeSong(n.codigo);
            modeloLista.removeElement(n);
            archivo.guardar(listaCanciones);
            imagenLabel.setIcon(null);
            duracionLabel.setText("Duración: --:--");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainApp::new);
    }
}
