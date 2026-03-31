package ca.ucalgary.seng300;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MainAppTest {

    @Test
    void testMainAppInstantiates() {
        MainApp app = new MainApp();
        assertNotNull(app);
    }

    @Test
    void testTwoPlusTwoEqualsFour() {
        assertEquals(4, 2 + 2);
    }
}