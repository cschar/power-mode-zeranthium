package dk.lost_world;

import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.editor.actionSystem.TypedAction;

import javax.swing.*;
import java.net.URL;
import java.util.ArrayList;

public class SpriteHolder {
    public static ArrayList<ImageIcon> sprites = new ArrayList<ImageIcon>();


    public SpriteHolder(){
        if (this.sprites.size() == 0){
            for(int i = 1; i <=25; i++) {
//            sprites.add(new ImageIcon(getClass().getResource(String.format("/blender/000%d.png", i))));
                URL resource = this.getClass().getResource("/blender/0001.png");
                sprites.add(new ImageIcon(resource));
            }
        }
    }

}
