package com.cschar.pmode3;

import javazoom.jl.player.Player;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import com.intellij.openapi.diagnostic.Logger;

public class Sound {
    private static final Logger LOGGER = Logger.getInstance( Sound.class.getName() );
    public static Queue<Player> playerBank = new ConcurrentLinkedQueue<Player>();

    Player player;
    private String path;
    public String getPath(){ return this.path; };


    public static void closeAllPlayers(){
        for(Player p : playerBank){
            p.close();
        }
        playerBank.clear();
    }


    public Sound(String filePath, boolean isResource){
        this.path = filePath;
        InputStream stream;
        if(isResource){
            stream = this.getClass().getResourceAsStream(filePath);
            initPlayer(stream);
        }else {
            try {
                stream = new FileInputStream(filePath);
                initPlayer(stream);
            }catch(FileNotFoundException ex){
                LOGGER.error("File not found for MP3 sound.. falling back to resource");
                LOGGER.error(ex.toString(), ex );
                stream = this.getClass().getResourceAsStream(filePath);
                initPlayer(stream);
            }

        }
    }

    private void initPlayer(InputStream stream){
        try {
            this.player = new Player(stream);
            playerBank.add(this.player);
        }catch( Exception ex){
            LOGGER.error("Error initializing MP3 sound player");
            LOGGER.error(ex.toString(), ex );
        }
    }

    public void play() {
        new Thread(() -> {
            try {
                player.play();
                player.close();
                playerBank.remove(player);
            } catch (Exception ex) {
                LOGGER.error( ex.toString(), ex );
            }
        }).start();
    }

    public void play(SoundPlayCallback callback) {
        new Thread(() -> {
            try {
                player.play();
                player.close();
                playerBank.remove(player);
                callback.call();
            } catch (Exception ex) {
                LOGGER.error(ex.toString(), ex );
            }
        }).start();


    }

    public void stop(){
        if(player != null){
            player.close();
            playerBank.remove(player);
        }
    }

    //https://stackoverflow.com/a/2187030/5198805
    //make a cleanup callback after a sound is done playing
    // e.g.  change a button UI
    public static interface SoundPlayCallback {
        public void call();

    }
}

