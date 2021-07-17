package com.cschar.pmode3;

import static org.junit.Assert.assertEquals;

import com.intellij.configurationStore.XmlSerializer;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

//Dummy test for pipeline
public class SmokeJUnit4Test {
    @Test
    public void testAdd() {
        assertEquals(42, Integer.sum(19, 23));
    }


    @Test
    public void deserializeXml() throws IOException, JDOMException {

        SAXBuilder saxBuilder = new SAXBuilder();
        String url = this.getClass().getClassLoader().getResource("").getPath();
        File inputFile = new File("build/resources/test/save_config.xml");
        Document document = saxBuilder.build(inputFile);
        Element classElement = document.getRootElement();


        List<Element> options = classElement.getChild("component").getChildren("option");
        assertEquals(options.get(0).getAttribute("name").getValue(), "particleRGB");
        assertEquals(options.get(0).getAttribute("value").getValue(), "-12566465");


//        PowerMode3 pmode3Service = new PowerMode3();
//        assertEquals(pmode3Service.getParticleRGB(), 0);
//
//        XmlSerializer.deserializeAndLoadState(pmode3Service, classElement);
//
//        assertEquals(-12566465, pmode3Service.getParticleRGB());

        assertEquals(true,true);
    }
}