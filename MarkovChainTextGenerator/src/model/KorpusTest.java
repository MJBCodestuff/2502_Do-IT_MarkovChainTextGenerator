package model;

import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class KorpusTest {

    private Korpus c;

    @BeforeEach
    void setup(){
        c = new Korpus();
    }

    @org.junit.jupiter.api.Test
    void cleanupText() {
        String results = c.cleanupText("Dies     ist\nein\n, Test!");
        assertEquals("Dies ist ein, Test!", results);
    }
}