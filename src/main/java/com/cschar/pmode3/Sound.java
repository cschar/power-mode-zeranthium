package com.cschar.pmode3;

import javazoom.jl.player.Player;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Sound {

    public static Queue<Player> playerBank = new ConcurrentLinkedQueue<Player>();

    Player player;



    public static void closeAllPlayers(){
        for(Player p : playerBank){
            p.close();
        }
        playerBank.clear();
    }


    public Sound(String filePath, boolean isResource){
        InputStream stream;
        if(isResource){
            stream = this.getClass().getResourceAsStream(filePath);
            initPlayer(stream);
        }else {
            try {
                stream = new FileInputStream(filePath);
                initPlayer(stream);
            }catch(FileNotFoundException e){
                System.out.println("File not found for MP3 sound.. falling back to resource");
                stream = this.getClass().getResourceAsStream(filePath);
                initPlayer(stream);
            }

        }
    }

    private void initPlayer(InputStream stream){
        try {
            this.player = new Player(stream);
            playerBank.add(this.player);
        }catch( Exception e){
            System.out.println("Error initializing MP3 sound player");
        }
    }

    public void play() {
        new Thread(() -> {
            try {
                player.play();
                player.close();
                playerBank.remove(player);
            } catch (Exception e) {
                System.out.println(e);
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
            } catch (Exception e) {
                System.out.println(e);
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

