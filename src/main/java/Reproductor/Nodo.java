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
    Cancion cancion;
    Nodo next;
    
    public Nodo(Cancion can){
        this.cancion = can;
        this.next = null;
    }
    
    @Override
    public String toString(){
        return cancion.toString();
    }
}
