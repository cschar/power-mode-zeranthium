package com.cschar.pmode3.config.common.ui;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SoundFileChooserDescriptor extends FileChooserDescriptor {

    String myTitle = "Pick a MP3 File";
    public SoundFileChooserDescriptor(boolean chooseFiles, boolean chooseFolders, boolean chooseJars, boolean chooseJarsAsFiles, boolean chooseJarContents, boolean chooseMultiple) {
        super(chooseFiles, chooseFolders, chooseJars, chooseJarsAsFiles, chooseJarContents, chooseMultiple);
        this.setTitle(myTitle);

        //MACOS doesnt respect overloads in native file browser
        this.setForcedToUseIdeaFileChooser(true);

    }

    public SoundFileChooserDescriptor(@NotNull FileChooserDescriptor d) {
        super(d);
        this.setTitle(myTitle);
        //MACOS doesnt respect overloads in native file browser
        this.setForcedToUseIdeaFileChooser(true);
    }

    @Override
    public boolean isFileSelectable(VirtualFile file) {
       boolean isSuperSelectable = super.isFileSelectable(file);
       boolean isSound = Objects.equals(file.getExtension(), "mp3");

       return isSuperSelectable && isSound;

    }

}
