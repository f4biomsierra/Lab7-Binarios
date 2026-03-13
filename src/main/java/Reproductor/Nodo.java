/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Reproductor;

/**
 *
 * @author Fabio Sierra
 */
public class Nodo {
    int codigo;
    String nombre;
    String artista;
    String genero;
    String rutaAudio;
    String rutaImagen;
    Nodo next;
    
    public Nodo(int codigo, String nombre, String artista, String genero, String rutaAudio, String rutaImagen){
        this.codigo = codigo;
        this.nombre = nombre;
        this.artista = artista;
        this.genero = genero;
        this.rutaAudio = rutaAudio;
        this.rutaImagen = rutaImagen;
        next = null;
    }
    
    @Override
    public String toString(){
        return nombre + " - " + artista;
    }
}
