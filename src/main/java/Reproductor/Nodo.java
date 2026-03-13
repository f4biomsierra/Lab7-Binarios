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
    String duracion;
    Nodo next;
    
    public Nodo(int codigo, String nombre, String artista, String genero, String rutaAudio, String rutaImagen, String duracion){
        this.codigo = codigo;
        this.nombre = nombre;
        this.artista = artista;
        this.genero = genero;
        this.rutaAudio = rutaAudio;
        this.rutaImagen = rutaImagen;
        this.duracion = duracion;
        next = null;
    }
    
    @Override
    public String toString(){
        return nombre + " - " + artista + "(" + duracion + ")";
    }
}
