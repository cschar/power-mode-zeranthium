package com.cschar.pmode3;

import javazoom.jl.player.Player;

import java.io.FileInputStream;
import java.io.InputStream;

public class Sound {

    Player player;


//    public Sound(String resourcePath){
//        initResource(resourcePath);
//    }



    public Sound(String filePath, boolean isResource){
        if(isResource){
            initResource(filePath);
        }else {
            try {
                FileInputStream stream = new FileInputStream(filePath);
                this.player = new Player(stream);
            } catch (Exception e) {
                System.out.println("Error initializing MP3 sound");
            }
        }
    }

    private void initResource(String resourcePath){
        try {
            InputStream stream = this.getClass().getResourceAsStream(resourcePath);
            this.player = new Player(stream);
        }catch( Exception e){
            System.out.println("Error initializing MP3 sound");
        }
    }

    public void play() {
        new Thread(() -> {
            try {
                player.play();
                player.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }).start();
    }

    public void stop(){
        if(player != null){
            player.close();
        }
    }

}
