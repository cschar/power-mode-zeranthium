package com.cschar.pmode3;


import com.intellij.configurationStore.XmlSerializer;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.junit.jupiter.api.Test;

public class PowerMode3Test extends BasePlatformTestCase {

    @Test
    public void testPowerMode3_initializes() {
        PowerMode3 pmode3Service = ApplicationManager.getApplication().getService(PowerMode3.class);
        assertInstanceOf(pmode3Service, PowerMode3.class);
    }
}
