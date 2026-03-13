/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Reproductor;

import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import java.io.*;

/**
 * @author Fabio Sierra
 */

public class Reproductor {
    private AdvancedPlayer player;
    private String rutaActual;
    private int pausaMilisegundo = 0;
    private boolean estaPausado = false;
    private Thread hiloReproduccion;

    public void play(String ruta) {
        stop();
        this.rutaActual = ruta;
        this.pausaMilisegundo = 0;
        this.estaPausado = false;
        comenzar(0);
    }

    public void pause() {
        if (player != null && !estaPausado) {
            stop();
            estaPausado = true;
        }
    }

    public void resume() {
        if (estaPausado && rutaActual != null) {
            estaPausado = false;
            comenzar(pausaMilisegundo);
        }
    }

    public void stop() {
        if (player != null) {
            player.close();
            player = null;
        }
        if (hiloReproduccion != null) {
            hiloReproduccion.interrupt();
        }
    }

    private void comenzar(int desdeMilisegundo) {
        hiloReproduccion = new Thread(() -> {
            try {
                FileInputStream fis = new FileInputStream(rutaActual);
                BufferedInputStream bis = new BufferedInputStream(fis);
                player = new AdvancedPlayer(bis);

                player.setPlayBackListener(new PlaybackListener() {
                    @Override
                    public void playbackFinished(PlaybackEvent evt) {
                        pausaMilisegundo += evt.getFrame();
                    }
                });

                player.play(desdeMilisegundo, Integer.MAX_VALUE);
            } catch (Exception e) {
                System.out.println("Error al reproducir: " + e.getMessage());
            }
        });
        hiloReproduccion.start();
    }
}
