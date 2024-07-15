package com.cschar.pmode3.uitest;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "TEST_TYPE", matches = "UI")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SoundTest {

    @Test
    @Order(1)
    @Tag("Sound")
    public void test_sound_test_1() {
        assert 1 == 1;
    }
    @Test
    @Order(4)
    public void test_sound_test_final_4() {
        assert 1 == 1;
    }


    @Test
    @Order(2)
    void test_sound_test_another_2() {
        assert 1 == 1;
    }

    @Test
    @Order(3)
    public void test_sound_test_another_one_3() {
        assert 1 == 1;
    }


}
