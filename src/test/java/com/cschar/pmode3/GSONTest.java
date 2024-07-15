package com.cschar.pmode3;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.assertEquals;

//Dummy test for pipeline
@EnabledIfEnvironmentVariable(named = "TEST_TYPE", matches = "NORMAL")
public class GSONTest {
    @Test
    public void testSerializeColor() {
        ZStateBasicParticle z = new ZStateBasicParticle();
        ZStateBasicParticleConverter c = new ZStateBasicParticleConverter();

        ZStateBasicParticle zDeserialized = c.fromString(c.toString(z));

        assertEquals(z.basicColor.getRGB(), zDeserialized.basicColor.getRGB());
    }

}