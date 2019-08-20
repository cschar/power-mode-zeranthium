package com.cschar.pmode3;

import java.awt.*;

public class Anchor {
    Point p;
    int anchorOffset;
    int cursorOffset;

    public Anchor(Point p, int anchorOffset, int cursorOffset) {
        this.p = p;
        this.anchorOffset = anchorOffset;
        this.cursorOffset = cursorOffset;
    }
}
