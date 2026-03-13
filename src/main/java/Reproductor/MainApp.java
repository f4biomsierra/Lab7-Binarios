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
import java.util.*;
/**
 * @author Fabio Sierra
 */
public class MainApp extends JFrame {
    private ArrayList<Cancion> listaCanciones;
    private DefaultListModel<Cancion> modeloLista;
    private JList<Cancion> jlist;

    private JLabel imagenLabel;
    private JLabel duracionLabel;

    private Reproductor reproductor;
    private PlaylistArchivo archivo;

    private CardLayout cardLayout;
    private JPanel panelPrincipal;
    private JPanel panelBiblioteca;
    private JPanel panelAgregar;

    private Cancion cancionSeleccionada;
    private String rutaAudioTemp;
    private String rutaImagenTemp;

    private JTextField txtNombre, txtArtista, txtGenero;
    private JLabel lblPreview, lblInfo;

    public MainApp() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        archivo = new PlaylistArchivo();
        listaCanciones = archivo.cargar();
        reproductor = new Reproductor();

        setTitle("Reproductor de Música");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        panelPrincipal = new JPanel(cardLayout);

        construirPanelBiblioteca();
        panelAgregar = crearPanelAgregar();

        panelPrincipal.add(panelBiblioteca, "biblioteca");
        panelPrincipal.add(panelAgregar, "agregar");

        add(panelPrincipal);
        cardLayout.show(panelPrincipal, "biblioteca");
        setVisible(true);
    }

    private void construirPanelBiblioteca() {
        panelBiblioteca = new JPanel(new BorderLayout(20, 20));
        panelBiblioteca.setBorder(new EmptyBorder(20, 20, 20, 20));
        panelBiblioteca.setBackground(new Color(245, 245, 245));

        modeloLista = new DefaultListModel<>();
        jlist = new JList<>(modeloLista);
        jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jlist.setFixedCellHeight(40);
        jlist.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        jlist.setSelectionBackground(new Color(52, 152, 219));
        jlist.setSelectionForeground(Color.WHITE);

        for (Cancion c : listaCanciones) {
            modeloLista.addElement(c);
        }

        JScrollPane scrollLista = new JScrollPane(jlist);
        scrollLista.setPreferredSize(new Dimension(300, 0));
        scrollLista.setBorder(BorderFactory.createTitledBorder("Lista de Reproducción"));

        panelBiblioteca.add(scrollLista, BorderLayout.WEST);

        JPanel panelVisualizador = new JPanel(new BorderLayout());
        panelVisualizador.setBackground(Color.WHITE);
        panelVisualizador.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 2));

        imagenLabel = new JLabel("Seleccione una canción", SwingConstants.CENTER);
        imagenLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));

        duracionLabel = new JLabel("Duración: --:--", SwingConstants.CENTER);
        duracionLabel.setOpaque(true);
        duracionLabel.setBackground(new Color(44, 62, 80));
        duracionLabel.setForeground(Color.WHITE);
        duracionLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        duracionLabel.setBorder(new EmptyBorder(15, 0, 15, 0));

        panelVisualizador.add(imagenLabel, BorderLayout.CENTER);
        panelVisualizador.add(duracionLabel, BorderLayout.SOUTH);
        panelBiblioteca.add(panelVisualizador, BorderLayout.CENTER);

        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelControles.setBackground(new Color(245, 245, 245));

        JButton selectBtn = crearBotonModerno("Select", new Color(149, 165, 166));
        JButton playBtn = crearBotonModerno("Play", new Color(46, 204, 113));
        JButton pauseBtn = crearBotonModerno("Pause", new Color(241, 196, 15));
        JButton stopBtn = crearBotonModerno("Stop", new Color(231, 76, 60));
        JButton removeBtn = crearBotonModerno("Remove", Color.LIGHT_GRAY);
        JButton addBtn = crearBotonModerno("+ Add", new Color(52, 152, 219));

        panelControles.add(selectBtn);
        panelControles.add(playBtn);
        panelControles.add(pauseBtn);
        panelControles.add(stopBtn);
        panelControles.add(new JSeparator(SwingConstants.VERTICAL));
        panelControles.add(removeBtn);
        panelControles.add(addBtn);

        panelBiblioteca.add(panelControles, BorderLayout.SOUTH);

        selectBtn.addActionListener(e -> {
            Cancion nueva = jlist.getSelectedValue();
            if (nueva != null) {
                if (cancionSeleccionada == null || !cancionSeleccionada.equals(nueva)) {
                    reproductor.stop();
                }
                cancionSeleccionada = nueva;
                actualizarInterfaz();
            }
        });

        playBtn.addActionListener(e -> {
            if (cancionSeleccionada != null) {
                reproductor.play(cancionSeleccionada.rutaAudio);
            } else {
                JOptionPane.showMessageDialog(this, "Debe pulsar 'Select' primero.");
            }
        });

        pauseBtn.addActionListener(e -> reproductor.pause());
        stopBtn.addActionListener(e -> reproductor.stop());
        removeBtn.addActionListener(e -> eliminarCancion());
        addBtn.addActionListener(e -> {
            limpiarFormulario();
            cardLayout.show(panelPrincipal, "agregar");
        });
    }

    private JButton crearBotonModerno(String texto, Color colorFondo) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(colorFondo);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel crearPanelAgregar() {
        JPanel panelContenedor = new JPanel(new GridBagLayout());
        panelContenedor.setBackground(new Color(242, 244, 246));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("Nueva Canción", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        panelContenedor.add(titulo, gbc);

        gbc.gridwidth = 1;
        txtNombre = new JTextField(15);
        txtArtista = new JTextField(15);
        txtGenero = new JTextField(15);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panelContenedor.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        panelContenedor.add(txtNombre, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panelContenedor.add(new JLabel("Artista:"), gbc);
        gbc.gridx = 1;
        panelContenedor.add(txtArtista, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panelContenedor.add(new JLabel("Género:"), gbc);
        gbc.gridx = 1;
        panelContenedor.add(txtGenero, gbc);

        lblPreview = new JLabel("Sin Imagen", SwingConstants.CENTER);
        lblPreview.setPreferredSize(new Dimension(120, 120));
        lblPreview.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridheight = 3;
        panelContenedor.add(lblPreview, gbc);

        gbc.gridheight = 1;
        gbc.gridx = 0;
        gbc.gridy = 4;
        JButton btnAudio = new JButton("Seleccionar MP3");
        panelContenedor.add(btnAudio, gbc);

        gbc.gridx = 1;
        JButton btnImagen = new JButton("Seleccionar Imagen");
        panelContenedor.add(btnImagen, gbc);

        lblInfo = new JLabel("Duración: 00:00", SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        panelContenedor.add(lblInfo, gbc);

        JButton btnGuardar = new JButton("Guardar en Lista de Reproducción");
        btnGuardar.setBackground(new Color(46, 204, 113));
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 3;
        panelContenedor.add(btnGuardar, gbc);

        btnAudio.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("Archivos MP3 (*.mp3)", "mp3"));
            fc.setAcceptAllFileFilterUsed(false);
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                rutaAudioTemp = fc.getSelectedFile().getAbsolutePath();
                lblInfo.setText("Duración: " + calcularDuracion(rutaAudioTemp));
            }
        });

        btnImagen.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("Imágenes", "jpg", "png", "jpeg"));
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                rutaImagenTemp = fc.getSelectedFile().getAbsolutePath();
                ImageIcon icon = new ImageIcon(rutaImagenTemp);
                Image img = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                lblPreview.setIcon(new ImageIcon(img));
                lblPreview.setText("");
            }
        });

        btnGuardar.addActionListener(e -> {
            if (txtNombre.getText().trim().isEmpty() || rutaAudioTemp == null) {
                JOptionPane.showMessageDialog(this, "Nombre y Audio son obligatorios.");
                return;
            }
            Cancion nueva = new Cancion(listaCanciones.size() + 1, txtNombre.getText(), txtArtista.getText(),
                    txtGenero.getText(), rutaAudioTemp, rutaImagenTemp, calcularDuracion(rutaAudioTemp));
            listaCanciones.add(nueva);
            modeloLista.addElement(nueva);
            archivo.guardar(listaCanciones);

            limpiarFormulario();
            cardLayout.show(panelPrincipal, "biblioteca");
        });

        JPanel pFinal = new JPanel(new BorderLayout());
        JButton v = new JButton("← Volver");
        v.addActionListener(e -> {
            limpiarFormulario();
            cardLayout.show(panelPrincipal, "biblioteca");
        });
        pFinal.add(panelContenedor, BorderLayout.CENTER);
        pFinal.add(v, BorderLayout.SOUTH);
        return pFinal;
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtArtista.setText("");
        txtGenero.setText("");
        lblInfo.setText("Duración: 00:00");
        lblPreview.setIcon(null);
        lblPreview.setText("Sin Imagen");
        rutaAudioTemp = null;
        rutaImagenTemp = null;
    }

    public String calcularDuracion(String ruta) {
        try (FileInputStream fis = new FileInputStream(new File(ruta))) {
            Bitstream bitstream = new Bitstream(fis);
            Header h = bitstream.readFrame();
            long s = (long) h.total_ms((int) new File(ruta).length()) / 1000;
            bitstream.close();
            return String.format("%02d:%02d", s / 60, s % 60);
        } catch (Exception e) {
            return "00:00";
        }
    }

    private void actualizarInterfaz() {
        if (cancionSeleccionada != null) {
            if (cancionSeleccionada.rutaImagen != null && !cancionSeleccionada.rutaImagen.isEmpty()) {
                ImageIcon icon = new ImageIcon(cancionSeleccionada.rutaImagen);
                Image img = icon.getImage().getScaledInstance(350, 350, Image.SCALE_SMOOTH);
                imagenLabel.setIcon(new ImageIcon(img));
                imagenLabel.setText("");
            } else {
                imagenLabel.setIcon(null);
                imagenLabel.setText("Sin Imagen");
            }
            duracionLabel.setText("Duración: " + cancionSeleccionada.duracion);
        }
    }

    private void eliminarCancion() {
        Cancion c = jlist.getSelectedValue();
        if (c != null) {
            reproductor.stop();
            listaCanciones.remove(c);
            modeloLista.removeElement(c);
            archivo.guardar(listaCanciones);

            cancionSeleccionada = null;
            imagenLabel.setIcon(null);
            imagenLabel.setText("Seleccione una canción");
            duracionLabel.setText("Duración: --:--");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainApp::new);
    }
}
