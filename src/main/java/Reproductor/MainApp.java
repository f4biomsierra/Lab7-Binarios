/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Reproductor;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

/**
 * @author Fabio Sierra
 */
public class MainApp extends JFrame {
    private ListaCanciones listaCanciones;
    private DefaultListModel<Nodo> modeloLista;
    private JList<Nodo> jlist;
    private JLabel imagenLabel;
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
        setSize(1000, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));

        // --- OESTE: LISTA ---
        modeloLista = new DefaultListModel<>();
        jlist = new JList<>(modeloLista);
        jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jlist.setBackground(new Color(245, 245, 245));
        
        for (int contador = 0; contador < listaCanciones.size(); contador++) {
            modeloLista.addElement(listaCanciones.getSong(contador));
        }

        JScrollPane scrollLista = new JScrollPane(jlist);
        scrollLista.setPreferredSize(new Dimension(250, 0));
        scrollLista.setBorder(BorderFactory.createTitledBorder("Tu Playlist"));
        add(scrollLista, BorderLayout.WEST);

        // --- CENTRO: IMAGEN ---
        imagenLabel = new JLabel("Seleccione una canción", SwingConstants.CENTER);
        imagenLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        imagenLabel.setOpaque(true);
        imagenLabel.setBackground(Color.DARK_GRAY);
        add(imagenLabel, BorderLayout.CENTER);

        // --- SUR: BOTONES ---
        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelControles.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JButton play = new JButton("▶ Play");
        JButton pause = new JButton("⏸ Pause");
        JButton stop = new JButton("⏹ Stop");
        JButton remove = new JButton("🗑 Remove");
        JButton addBtn = new JButton("➕ Add"); // Cambié el nombre para evitar conflictos

        panelControles.add(play);
        panelControles.add(pause);
        panelControles.add(stop);
        panelControles.add(remove);
        panelControles.add(addBtn); // ¡IMPORTANTE! Agregarlo al panel
        
        add(panelControles, BorderLayout.SOUTH);

        // --- EVENTOS ---
        play.addActionListener(evento -> {
            Nodo nodoSeleccionado = jlist.getSelectedValue();
            if (nodoSeleccionado != null) {
                reproductor.play(nodoSeleccionado.rutaAudio);
                mostrarImagen();
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona una canción de la lista primero.");
            }
        });

        pause.addActionListener(evento -> reproductor.pause());
        stop.addActionListener(evento -> reproductor.stop());
        remove.addActionListener(evento -> eliminarCancion());
        
        // Aquí conectamos el botón con la ventana emergente
        addBtn.addActionListener(evento -> mostrarVentanaAgregar());
        
        jlist.addListSelectionListener(evento -> {
            if (!evento.getValueIsAdjusting()) mostrarImagen();
        });

        setVisible(true);
    }

    private void mostrarVentanaAgregar() {
        JDialog ventanaEmergente = new JDialog(this, "Agregar Nueva Canción", true);
        ventanaEmergente.setLayout(new BorderLayout());

        // Importante: Pasar null o un Runnable vacío si no lo usas en el constructor
        PanelAgregar panel = new PanelAgregar(() -> {
        });

        // Le damos un tamaño preferido para que el diálogo sepa cuánto espacio ocupar
        panel.setPreferredSize(new Dimension(350, 400));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Configuramos el botón de agregar que está DENTRO del panel
        panel.agregarButton.addActionListener(e -> {
            agregarCancion(panel);
            ventanaEmergente.dispose(); // Cerrar al terminar
        });

        ventanaEmergente.add(panel, BorderLayout.CENTER);

        ventanaEmergente.pack(); // Ajusta al tamaño del panel
        ventanaEmergente.setResizable(false); // Evita que el usuario la deforme
        ventanaEmergente.setLocationRelativeTo(this); // Centrar sobre la app principal
        ventanaEmergente.setVisible(true);
    }

    private void agregarCancion(PanelAgregar panel) {
        try {
            int codigo = listaCanciones.size() + 1;
            String nombre = panel.getNombre();
            String artista = panel.getArtista();
            String genero = panel.getGenero();
            String rutaAudio = panel.getRutaAudio();
            String rutaImagen = panel.getRutaImagen();

            if (nombre.isEmpty() || artista.isEmpty() || genero.isEmpty() || rutaAudio == null || rutaImagen == null) {
                JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos.");
                return;
            }

            Nodo nuevoNodo = new Nodo(codigo, nombre, artista, genero, rutaAudio, rutaImagen);
            listaCanciones.addSong(nuevoNodo);
            modeloLista.addElement(nuevoNodo);
            archivo.guardar(listaCanciones);

            panel.limpiarCampos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void eliminarCancion() {
        Nodo nodoSeleccionado = jlist.getSelectedValue();
        if (nodoSeleccionado != null) {
            reproductor.stop();
            listaCanciones.removeSong(nodoSeleccionado.codigo);
            modeloLista.removeElement(nodoSeleccionado);
            archivo.guardar(listaCanciones);
            imagenLabel.setIcon(null);
            imagenLabel.setText("Seleccione una canción");
        }
    }

    private void mostrarImagen() {
        Nodo nodoSeleccionado = jlist.getSelectedValue();
        if (nodoSeleccionado != null && nodoSeleccionado.rutaImagen != null) {
            ImageIcon iconoOriginal = new ImageIcon(nodoSeleccionado.rutaImagen);
            Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(350, 350, Image.SCALE_SMOOTH);
            imagenLabel.setIcon(new ImageIcon(imagenEscalada));
            imagenLabel.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApp());
    }
}
