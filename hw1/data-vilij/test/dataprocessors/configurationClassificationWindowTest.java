package dataprocessors;

import org.junit.Test;

import static org.junit.Assert.*;

public class configurationClassificationWindowTest {

    @Test
    /*
    Tested the largest possible Integer value, test passed
     */
    public void boundaryTestMaxIterations() {
        configurationClassificationWindow classificationWindow = new configurationClassificationWindow(1,-1,false);
        classificationWindow.correctInput();
        assertEquals(1,classificationWindow.getMaxIterations());
    }

    @Test
    public void ValidTestMaxIterations() {
        configurationClassificationWindow classificationWindow = new configurationClassificationWindow(11,11,false);
        classificationWindow.correctInput();
        assertEquals(11,classificationWindow.getMaxIterations());
    }

    @Test
    public void InvalidTestMaxIterations() {
        configurationClassificationWindow classificationWindow = new configurationClassificationWindow(-1,-1,false);
        classificationWindow.correctInput();
        assertEquals(1,classificationWindow.getMaxIterations());
    }

    @Test
    /*
    Tested the largest possible Integer value, test passed
     */
    public void boundaryTestUpdateInterval() {
        configurationClassificationWindow classificationWindow = new configurationClassificationWindow(1,1,false);
        classificationWindow.correctInput();
        assertEquals(1,classificationWindow.getUpdateInterval());
    }

    @Test
    public void ValidTestUpdateInerval() {
        configurationClassificationWindow classificationWindow = new configurationClassificationWindow(11,11,false);
        classificationWindow.correctInput();
        assertEquals(11,classificationWindow.getUpdateInterval());
    }

    @Test
    public void InvalidTestUpdateInterval() {
        configurationClassificationWindow classificationWindow = new configurationClassificationWindow(-1,-1,false);
        classificationWindow.correctInput();
        assertEquals(1,classificationWindow.getUpdateInterval());
    }
}