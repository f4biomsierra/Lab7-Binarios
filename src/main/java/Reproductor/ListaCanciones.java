/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Reproductor;

/**
 *
 * @author Fabio Sierra
 */
public class ListaCanciones {
    private Nodo inicio = null;
    private int size = 0;
    
    public boolean isEmpty(){
        return inicio==null;
    }
    
    public void addSong(Nodo data){
        if(data == null) return;
        
        if(isEmpty()){
            inicio=data;
        } else {
            Nodo tmp = inicio;
            while(tmp.next != null){
                tmp = tmp.next;
            }
            tmp.next = data;
        }
        size++;
    }
    
    public void removeSong(int codigo){
        if(isEmpty()) return;
        
        if(inicio.codigo == codigo){
            inicio=inicio.next;
            size--;
            return;
        }
        
        Nodo tmp = inicio;
        while(tmp.next != null && tmp.next.codigo != codigo)
            tmp=tmp.next;
        
        if(tmp.next != null){
            tmp.next = tmp.next.next;
            size--;
        }
    }
    
    public Nodo getSong(int indice){
        if(indice<0 || indice >= size) return null;
        
        Nodo tmp = inicio;
        for(int contador=0; contador<indice; contador++)
            tmp = tmp.next;
        return tmp;
    }
    
    public int size(){
        return size;
    }
    
}
