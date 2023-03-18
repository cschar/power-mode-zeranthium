package com.cschar.pmode4;

import com.intellij.openapi.progress.ProgressIndicator;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.FileNotFoundException;

public interface Pmode3PackLoader {
    public void loadConfigPack(@NotNull String manifestPath, ProgressIndicator progressIndicator) throws FileNotFoundException, JSONException;
}
