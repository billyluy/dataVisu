package dataprocessors;

import javafx.geometry.Point2D;
import org.junit.Test;

import static org.junit.Assert.*;

public class TSDProcessorTest {

    TSDProcessor processor = new TSDProcessor();

    @Test
    public void ParsSingleLineValid() throws Exception {
        String valid = "@a\tlabel1\t3,3";
        processor.processString(valid);
        assertEquals("label1", processor.getDataLabels().get("@a"));
        assertEquals(new Point2D(3,3), processor.getDataPoints().get("@a"));
    }

    @Test(expected = Exception.class)
    public void ParsSingleLineInvalid() throws Exception {
        String invalid = "";
        processor.processString(invalid);

    }

    @Test
    /*
    The maximum and minimum values for doubles are tested and pass the test case
     */
    public void ParsSingleLineBoundary() throws Exception {
        String s = "@a\tlabel1\t"+ Double.MAX_VALUE+","+Double.MIN_VALUE;
        processor.processString(s);
        assertEquals("label1", processor.getDataLabels().get("@a"));
        assertEquals(new Point2D(Double.MAX_VALUE,Double.MIN_VALUE), processor.getDataPoints().get("@a"));
    }
}