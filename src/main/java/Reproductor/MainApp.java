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
/**
 * @author Fabio Sierra
 */


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
        } catch (Exception e) {
            e.printStackTrace();
        }

        listaCanciones = new ListaCanciones();
        archivo = new PlaylistArchivo();
        listaCanciones = archivo.cargar();
        reproductor = new Reproductor();

        setTitle("Reproductor de Música");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));

        // --- OESTE: LISTA ---
        modeloLista = new DefaultListModel<>();
        jlist = new JList<>(modeloLista);
        for (int contador = 0; contador < listaCanciones.size(); contador++) {
            modeloLista.addElement(listaCanciones.getSong(contador));
        }

        JScrollPane scrollLista = new JScrollPane(jlist);
        scrollLista.setPreferredSize(new Dimension(280, 0));
        scrollLista.setBorder(BorderFactory.createTitledBorder("Tu Playlist"));
        add(scrollLista, BorderLayout.WEST);

        // --- CENTRO: VISOR ---
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

        // --- SUR: BOTONES ---
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
        stopBtn.addActionListener(e -> {
            reproductor.stop();
            duracionLabel.setText("Duración: " + (jlist.getSelectedValue() != null ? jlist.getSelectedValue().duracion : "--:--"));
        });
        
        removeBtn.addActionListener(e -> eliminarCancion());
        addBtn.addActionListener(e -> mostrarVentanaAgregar());

        jlist.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) actualizarInterfaz();
        });

        setVisible(true);
    }

    // MÉTODO CLAVE: Calcula la duración usando Bitstream de JLayer
    public String calcularDuracion(String ruta) {
        try (FileInputStream fis = new FileInputStream(new File(ruta))) {
            Bitstream bitstream = new Bitstream(fis);
            Header encabezado = bitstream.readFrame();
            long milisegundos = (long) encabezado.total_ms((int) new File(ruta).length());
            
            long segundosTotales = milisegundos / 1000;
            long min = segundosTotales / 60;
            long seg = segundosTotales % 60;
            
            bitstream.close();
            return String.format("%02d:%02d", min, seg);
        } catch (Exception e) {
            return "00:00";
        }
    }

    private void mostrarVentanaAgregar() {
        JDialog ventana = new JDialog(this, "Nueva Canción", true);
        PanelAgregar panel = new PanelAgregar(null); 
        
        // Modificamos el comportamiento del botón de audio del panel desde aquí
        panel.audioButton.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(panel) == JFileChooser.APPROVE_OPTION) {
                String ruta = fc.getSelectedFile().getAbsolutePath();
                panel.setRutaAudio(ruta);
                panel.audioButton.setText(fc.getSelectedFile().getName());
                
                // Cálculo automático al seleccionar
                String dur = calcularDuracion(ruta);
                panel.duracionField.setText(dur);
            }
        });

        panel.agregarButton.addActionListener(e -> {
            agregarCancion(panel);
            ventana.dispose();
        });

        ventana.add(panel);
        ventana.pack();
        ventana.setLocationRelativeTo(this);
        ventana.setVisible(true);
    }

    private void agregarCancion(PanelAgregar panel) {
        int codigo = listaCanciones.size() + 1;
        String dur = panel.duracionField.getText(); // Viene calculado automáticamente
        
        Nodo nuevo = new Nodo(codigo, panel.getNombre(), panel.getArtista(), 
                              panel.getGenero(), panel.getRutaAudio(), 
                              panel.getRutaImagen(), dur);
        
        listaCanciones.addSong(nuevo);
        modeloLista.addElement(nuevo);
        archivo.guardar(listaCanciones);
    }

    private void actualizarInterfaz() {
        Nodo n = jlist.getSelectedValue();
        if (n != null) {
            if (n.rutaImagen != null) {
                ImageIcon icon = new ImageIcon(n.rutaImagen);
                Image img = icon.getImage().getScaledInstance(350, 350, Image.SCALE_SMOOTH);
                imagenLabel.setIcon(new ImageIcon(img));
                imagenLabel.setText("");
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
