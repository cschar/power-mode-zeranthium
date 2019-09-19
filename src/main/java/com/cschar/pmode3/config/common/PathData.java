package com.cschar.pmode3.config.common;

import com.cschar.pmode3.ParticleSpriteLightning;
import com.cschar.pmode3.ParticleUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public abstract class PathData {

    public String defaultPath;
    public String customPath;
    public boolean customPathValid = false;



    public PathData(String defaultPath, String customPath) {
        this.customPath = customPath;
        this.defaultPath = defaultPath;
        this.customPathValid = false;
    }

}
