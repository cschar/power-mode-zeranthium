package com.cschar.pmode3.config.common.ui;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SoundFileChooserDescriptor extends FileChooserDescriptor {

    String myTitle = "Pick a MP3 File";
    private SoundFileChooserDescriptor(@NotNull FileChooserDescriptor d) {
        super(d);
        this.setTitle(myTitle);
        this.withFileFilter(file -> "mp3".equals(file.getExtension()));
        //MACOS doesnt respect overloads in native file browser
        this.setForcedToUseIdeaFileChooser(true);
    }

    public static class Builder {
        public Builder() {}
        public SoundFileChooserDescriptor build() {
            FileChooserDescriptor fd = new FileChooserDescriptor(
                    true, false, false,
                    false, false, false);
//            fd.withExtensionFilter(".mp3", "*.mp3");
            return new SoundFileChooserDescriptor(fd);
        }
    }

}
