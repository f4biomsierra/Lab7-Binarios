/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Reproductor;

import javazoom.jl.player.Player;
import java.io.*;
/**
 *
 * @author Fabio Sierra
 */
public class Reproductor {
    private Player reproductorMp3;
    private FileInputStream entrada;
    private Thread hiloMusica;
    
    private String rutaActual;
    private long totalBytes;
    private long bytesRestantes;
    private long posicionPausa;
    private boolean estaPausado = false;

    public void play(String ruta) {
        if (!ruta.equals(rutaActual)) {
            stop();
            rutaActual = ruta;
            posicionPausa = 0;
        }
        
        if (reproductorMp3 != null && !estaPausado) return;

        hiloMusica = new Thread(() -> {
            try {
                entrada = new FileInputStream(rutaActual);
                totalBytes = entrada.available();
                
                if (estaPausado && posicionPausa > 0) {
                    entrada.skip(totalBytes - posicionPausa);
                }

                reproductorMp3 = new Player(entrada);
                estaPausado = false;
                reproductorMp3.play();
                
                if (reproductorMp3.isComplete()) {
                    posicionPausa = 0;
                    estaPausado = false;
                }
                
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        });
        hiloMusica.start();
    }

    public void pause() {
        if (reproductorMp3 != null) {
            try {
                posicionPausa = entrada.available();
                estaPausado = true;
                reproductorMp3.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        if (reproductorMp3 != null) {
            reproductorMp3.close();
            reproductorMp3 = null;
        }
        posicionPausa = 0;
        estaPausado = false;
    }
}
