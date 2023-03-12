package com.cschar.pmode3;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.cschar.pmode3.config.common.SpriteDataAnimated;
import com.intellij.configurationStore.XmlSerializer;
import com.intellij.openapi.application.ApplicationManager;

import com.intellij.testFramework.LightPlatform4TestCase;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
//import org.junit.jupiter.api.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.intellij.util.SmartList;
import com.intellij.util.xml.dom.XmlDomReader;
import com.intellij.util.xml.dom.XmlElement;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.hamcrest.CoreMatchers;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;




//The BasePlatformTestcase seems to be from Junit3... so gradle's test task, using Junit4 Vintage enigne can't seem to run it...
//public class PowerMode3Test extends BasePlatformTestCase {
import com.intellij.openapi.diagnostic.Logger;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

//import org.slf4j.
//for now... replace with LightPlatform4TestCase

public class PowerMode3Test extends LightPlatform4TestCase {  //This boots up the Service/Components defined in plugin.xml
//public class PowerMode3Test {
//    private static final Logger LOG = Logger.getInstance(PowerMode3Test.class);
//    private static final Logger LOG = LoggerFactory.getLogger(PowerMode3Test.class);
    private static final Logger LOG = Logger.getInstance(PowerMode3Test.class);
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
        // XmlElement el = XmlDomReader.readXmlAsModel(Files.newInputStream(inputFile.toPath()))
        File inputFile = new File("build/resources/test/save_config.xml");
        Document document = saxBuilder.build(inputFile);
        Element zeranthiumComponentEl = document.getRootElement().getChildren().get(0);

        System.out.println("Checking initial serialized state values......==================================");
        //check loaded values
        List<Element> options = zeranthiumComponentEl.getChildren("option");
        assertEquals(options.get(0).getAttribute("name").getValue(), "particleRGB");
        assertEquals(options.get(0).getAttribute("value").getValue(), "-12566465");

        assertEquals(options.get(1).getAttribute("name").getValue(), "pathDataMap");
        Element map = options.get(1).getChild("map");
        assertEquals("LIZARD", map.getChildren().get(0).getAttribute("key").getValue());
        assertEquals("MULTI_LAYER", map.getChildren().get(1).getAttribute("key").getValue());
        assertEquals("LINKER", map.getChildren().get(2).getAttribute("key").getValue());

        List<Element> lizardPathOpts = map.getChildren().get(0).getChild("value").getChild("list").getChildren("option");
        assertEquals(3, lizardPathOpts.size());


        Element lizardPathoOpt = lizardPathOpts.get(0);
        SpriteDataAnimated spa = SpriteDataAnimated.fromJsonObjectString(lizardPathoOpt.getAttributeValue("value"));
        assertEquals(0.45f, spa.scale);

        System.out.println("Checking initial pmode3 default values......==================================");
        PowerMode3 pmode3Service = ApplicationManager.getApplication().getService(PowerMode3.class);
        assertEquals(-12566464, pmode3Service.getParticleRGB());

        //check default config values are loaded
        List<String> deserializedLizardOpts = pmode3Service.pathDataMap.get(PowerMode3.ConfigType.LIZARD);
        assertEquals(3, deserializedLizardOpts.size());
        spa = SpriteDataAnimated.fromJsonObjectString(deserializedLizardOpts.get(0));
        assertEquals(0.4f, spa.scale);

        System.out.println("deserializing, force re-loading state......==================================");
        pmode3Service.isConfigLoaded = false;
        XmlSerializer.deserializeAndLoadState(pmode3Service, zeranthiumComponentEl, PowerMode3.class);

        System.out.println("verifying state was loaded in......==================================");
        assertEquals(-12566465, pmode3Service.getParticleRGB());

        deserializedLizardOpts = pmode3Service.pathDataMap.get(PowerMode3.ConfigType.LIZARD);
        assertEquals(3, deserializedLizardOpts.size());
        spa = SpriteDataAnimated.fromJsonObjectString(deserializedLizardOpts.get(0));
        assertEquals(0.45f, spa.scale);

    }


    @Test
    public void testPowerMode3_serialization_handlesMissingSetting() throws IOException, JDOMException {
        PowerMode3 pmode3Service = ApplicationManager.getApplication().getService(PowerMode3.class);
        LOG.info("info log");
        LOG.warn("warn log");

        SAXBuilder saxBuilder = new SAXBuilder();
        File inputFile = new File("build/resources/test/save_config_missingConfigType.xml");
        Document document = saxBuilder.build(inputFile);
        Element zeranthiumComponentEl = document.getRootElement().getChildren().get(0);

        Element map  = zeranthiumComponentEl.getChildren("option").get(1).getChild("map");;

        List<String> enumNames = Stream.of(PowerMode3.ConfigType.values())
                .map(Enum::name)
                .collect(Collectors.toList());

        for (Element e : map.getChildren()){
            Assert.assertThat(enumNames, CoreMatchers.hasItem(e.getAttributeValue("key")));
            assertNotEquals("LIZARD", e.getAttributeValue("key"));
        }


        System.out.println("deserializing, force re-loading state......==================================");
        XmlSerializer.deserializeAndLoadState(pmode3Service, zeranthiumComponentEl, PowerMode3.class);


        System.out.println("verifying default CONFIG.JSON state was loaded in......==================================");

        //This fails due to bug that we load ALL defaultJSONConfigs when missing configs are found
        SmartList<String> deserializedLizardOpts = pmode3Service.pathDataMap.get(PowerMode3.ConfigType.LIZARD);

        assertEquals(3, deserializedLizardOpts.size());
        SpriteDataAnimated spa = SpriteDataAnimated.fromJsonObjectString(deserializedLizardOpts.get(0));
        assertEquals(0.4f, spa.scale);
    }

    @Test
    public void testCopy(){
        PowerMode3 pmode3Service = ApplicationManager.getApplication().getService(PowerMode3.class);
        PowerMode3 p1 = new PowerMode3();

        assertEquals(12, pmode3Service.pathDataMap.size());
        assertEquals(0, p1.pathDataMap.size());
        XmlSerializerUtil.copyBean(pmode3Service, p1);

        assertEquals(pmode3Service.getParticleRGB(), p1.getParticleRGB());
        assertEquals(12, p1.pathDataMap.size());

    }
}
