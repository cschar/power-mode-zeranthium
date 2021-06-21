package com.cschar.pmode3;


import com.intellij.configurationStore.XmlSerializer;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.testFramework.LightPlatform4TestCase;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
//import org.junit.jupiter.api.Test;
import static org.junit.Assert.assertEquals;

import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Test;

import java.io.*;
import java.net.URL;
import java.util.*;
//import org.jdom2.*;

//The BasePlatformTestcase seems to be from Junit3... so gradle's test task, using Junit4 Vintage enigne can't seem to run it...
//public class PowerMode3Test extends BasePlatformTestCase {

//for now... replace with LightPlatform4TestCase
public class PowerMode3Test extends LightPlatform4TestCase {  //This boots up the Service/Components defined in plugin.xml
//public class PowerMode3Test {

    //can use Tags to separate UiTests from unit tests

//
//    @Test
//    public void testPowerMode3_initializes() {
//        PowerMode3 pmode3Service = ApplicationManager.getApplication().getService(PowerMode3.class);
//        assertInstanceOf(pmode3Service, PowerMode3.class);
//    }

    @Test
    public void testPowerMode3_serialization() throws IOException, JDOMException {
        SAXBuilder saxBuilder = new SAXBuilder();
        File inputFile = new File("build/resources/test/save_config.xml");
        Document document = saxBuilder.build(inputFile);
        Element zeranthiumComponentEl = document.getRootElement().getChildren().get(0);


        //check loaded values
        List<Element> options = zeranthiumComponentEl.getChildren("option");
        assertEquals(options.get(0).getAttribute("name").getValue(), "particleRGB");
        assertEquals(options.get(0).getAttribute("value").getValue(), "-12566465");

        assertEquals(options.get(1).getAttribute("name").getValue(), "pathDataMap");
        Element map = options.get(1).getChild("map");
        assertEquals("LIZARD", map.getChildren().get(0).getAttribute("key").getValue());
        assertEquals("MULTI_LAYER", map.getChildren().get(1).getAttribute("key").getValue());
        assertEquals("LINKER", map.getChildren().get(2).getAttribute("key").getValue());


//        PowerMode3 pmode3Service = new PowerMode3();
        PowerMode3 pmode3Service = ApplicationManager.getApplication().getService(PowerMode3.class);
        assertEquals(-12566464, pmode3Service.getParticleRGB());

        System.out.println("deserializing......==================================");


        XmlSerializer.deserializeAndLoadState(pmode3Service, zeranthiumComponentEl, PowerMode3.class);

        assertEquals(-12566465, pmode3Service.getParticleRGB());



        assertEquals(true,true);
    }


    @Test
    public void testCopy(){
        PowerMode3 pmode3Service = ApplicationManager.getApplication().getService(PowerMode3.class);

        PowerMode3 p1 = new PowerMode3();


        assertEquals(12, pmode3Service.pathDataMap.size());
        assertEquals(0, p1.pathDataMap.size());
        XmlSerializerUtil.copyBean(pmode3Service, p1);

        assertEquals(-12566464, pmode3Service.getParticleRGB());
        assertEquals(pmode3Service.getParticleRGB(), p1.getParticleRGB());
        assertEquals(12, p1.pathDataMap.size());

    }
}
