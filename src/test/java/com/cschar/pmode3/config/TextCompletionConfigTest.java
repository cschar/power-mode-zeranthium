package com.cschar.pmode3.config;

import com.cschar.pmode3.config.common.SoundData;
//import org.junit.Test;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TextCompletionConfigTest {

    @Test
    public void testLadder() {

        int[] ladder = new int[10];
        int[] results = null;

        ArrayList<SoundData> sounds = new ArrayList<>();
        var sd1 = new SoundData(false, 1, "", "", "word1");
        var sd2 = new SoundData(false, 1, "", "", "word");
        var sd3 = new SoundData(false, 1, "", "", "hmm");
        var sd4 = new SoundData(false, 1, "", "", "wow");


        sounds.add(sd1);
        sounds.add(sd2);
        sounds.add(sd3);
        sounds.add(sd4);

        TextCompletionSoundConfig.incrementLadder(ladder, 'w', sounds);
        assertEquals(ladder[0],1);
        assertEquals(ladder[1],1);
        assertEquals(ladder[2],0);

        TextCompletionSoundConfig.incrementLadder(ladder, 'o', sounds);
        assertEquals(ladder[0],2);
        assertEquals(ladder[1],2);
        assertEquals(ladder[2],0);
        assertEquals(ladder[3],2);

        TextCompletionSoundConfig.incrementLadder(ladder, 'r', sounds);
        assertEquals(ladder[0],3);
        assertEquals(ladder[1],3);
        assertEquals(ladder[2],0);
        assertEquals(ladder[3],0);

        results =  TextCompletionSoundConfig.incrementLadder(ladder, 'd', sounds);
        assertEquals(ladder[0],4);
        assertEquals(ladder[1],0);
        assertEquals(results[1],1);


        //resets but also increments if last char is first
        TextCompletionSoundConfig.incrementLadder(ladder, 'w', sounds);
        assertEquals(ladder[3],1);
        assertEquals(results[3],0);
        TextCompletionSoundConfig.incrementLadder(ladder, 'o', sounds);
        assertEquals(ladder[3],2);
        assertEquals(results[3],0);
        results =  TextCompletionSoundConfig.incrementLadder(ladder, 'w', sounds);
        assertEquals(ladder[3],1);
        assertEquals(results[3],1);

    }
}
