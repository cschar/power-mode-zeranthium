package com.cschar.pmode3;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

//Dummy test for pipeline
public class GSONJUnit4Test {
    @Test
    public void testSerializeColor() {
        ZStateBasicParticle z = new ZStateBasicParticle();
        ZStateBasicParticleConverter c = new ZStateBasicParticleConverter();

        ZStateBasicParticle zDeserialized = c.fromString(c.toString(z));

        assertEquals(z.basicColor.getRGB(), zDeserialized.basicColor.getRGB());
    }

}