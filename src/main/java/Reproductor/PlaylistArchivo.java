/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Reproductor;

import java.io.*;
import java.util.*;
/**
 *
 * @author Fabio Sierra
 */
public class PlaylistArchivo {

    private String archivo = "playlist.dat";

    public void guardar(ArrayList<Cancion> lista) {
        try (RandomAccessFile rafObj = new RandomAccessFile(archivo, "rw")) {
            rafObj.setLength(0);
            for (Cancion can : lista) {
                rafObj.writeInt(can.codigo);
                rafObj.writeUTF(can.nombre);
                rafObj.writeUTF(can.artista);
                rafObj.writeUTF(can.genero);
                rafObj.writeUTF(can.rutaAudio);
                rafObj.writeUTF(can.rutaImagen == null ? "" : can.rutaImagen);
                rafObj.writeUTF(can.duracion);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Cancion> cargar() {
        ArrayList<Cancion> lista = new ArrayList<>();
        try (RandomAccessFile rafObj = new RandomAccessFile(archivo, "r")) {
            while (rafObj.getFilePointer() < rafObj.length()) {
                int codigo = rafObj.readInt();
                String nombre = rafObj.readUTF();
                String artista = rafObj.readUTF();
                String genero = rafObj.readUTF();
                String rutaAudio = rafObj.readUTF();
                String rutaImagen = rafObj.readUTF();
                String duracion = rafObj.readUTF();
                lista.add(new Cancion(codigo, nombre, artista, genero, rutaAudio, rutaImagen, duracion));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
