/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Reproductor;

import java.io.*;
/**
 *
 * @author Fabio Sierra
 */
public class PlaylistArchivo {
    private String archivo = "playlist.dat";
    
    public void guardar(ListaCanciones lista){
        try(RandomAccessFile rafObj = new RandomAccessFile(archivo, "rw")){
            rafObj.setLength(0);
            Nodo tmp = lista.getSong(0);
            int indice=0;
            while(tmp!=null){
                rafObj.writeInt(tmp.codigo);
                rafObj.writeUTF(tmp.nombre);
                rafObj.writeUTF(tmp.artista);
                rafObj.writeUTF(tmp.genero);
                rafObj.writeUTF(tmp.rutaAudio);
                rafObj.writeUTF(tmp.rutaImagen);
                
                tmp=tmp.next;
                indice++;
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public ListaCanciones cargar(){
        ListaCanciones lista = new ListaCanciones();
        try(RandomAccessFile rafObj = new RandomAccessFile(archivo, "r")){
            while(rafObj.getFilePointer() < rafObj.length()){
                int codigo = rafObj.readInt();
                String nombre = rafObj.readUTF();
                String artista = rafObj.readUTF();
                String genero = rafObj.readUTF();
                String rutaAudio = rafObj.readUTF();
                String rutaImagen = rafObj.readUTF();
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        return lista;
    }
}
