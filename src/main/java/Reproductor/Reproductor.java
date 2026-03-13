/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Reproductor;

import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import java.io.*;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Header;

public class Reproductor {
    private AdvancedPlayer player;
    private String rutaActual;
    private volatile boolean estaPausado = false;
    private Thread hilo;

    private long msTranscurridos = 0;
    private long msInicio = 0;

    public void play(String ruta) {
        if (rutaActual == null || !rutaActual.equals(ruta)) {
            stop();
            rutaActual = ruta;
        }
        if (player != null && !estaPausado) {
            return;
        }
        estaPausado = false;
        reproducir(msTranscurridos);
    }

    public void pause() {
        if (player != null && !estaPausado) {
            msTranscurridos += System.currentTimeMillis() - msInicio;
            estaPausado = true;
            player.close();
        }
    }

    public void stop() {
        estaPausado = false;
        msTranscurridos = 0;
        msInicio = 0;
        rutaActual = null;
        if (player != null) {
            player.close();
            player = null;
        }
    }

    private void reproducir(long startMs) {
        hilo = new Thread(() -> {
            try {
                FileInputStream fis = new FileInputStream(rutaActual);
                BufferedInputStream bis = new BufferedInputStream(fis);

                if (startMs > 0) {
                    Bitstream bitstream = new Bitstream(new FileInputStream(rutaActual));
                    Header header = bitstream.readFrame();
                    int bitrateKbps = header.bitrate() / 1000;
                    bitstream.close();

                    long bytesPerMs = (bitrateKbps * 1000) / 8 / 1000;
                    long bytesToSkip = startMs * bytesPerMs;
                    bis.skip(bytesToSkip);
                }

                player = new AdvancedPlayer(bis);
                msInicio = System.currentTimeMillis();
                player.play();

                if (!estaPausado) {
                    msTranscurridos = 0;
                }

            } catch (Exception e) {
                System.err.println("Error en la reproducción: " + e.getMessage());
            }
        });
        hilo.setPriority(Thread.MAX_PRIORITY);
        hilo.start();
    }
}
