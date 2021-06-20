package com.cschar.pmode3;


import com.intellij.configurationStore.XmlSerializer;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.testFramework.LightPlatform4TestCase;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
//import org.junit.jupiter.api.Test;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

//The BasePlatformTestcase seems to be from Junit3... so gradle's test task, using Junit4 Vintage enigne can't pick it up...
//public class PowerMode3Test extends BasePlatformTestCase {

//for now... replace with LightPlatform4TestCase
public class PowerMode3Test extends LightPlatform4TestCase {

    @Test
    public void testPowerMode3_initializes() {
        PowerMode3 pmode3Service = ApplicationManager.getApplication().getService(PowerMode3.class);
        assertInstanceOf(pmode3Service, PowerMode3.class);
    }
}
