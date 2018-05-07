package dataprocessors;

import org.junit.Test;

import static org.junit.Assert.*;

public class configurationClusteringWindowTest {

    @Test
    /*
    Tested the largest possible Integer value, test passed
     */
    public void boundaryTestMaxIterations() {
        configurationClusteringWindow clusteringWindow = new configurationClusteringWindow(1,-1,2,false);
        clusteringWindow.correctInput();
        assertEquals(1,clusteringWindow.getMaxIterations());
    }

    @Test
    public void ValidTestMaxIterations() {
        configurationClusteringWindow clusteringWindow = new configurationClusteringWindow(1,1,3,false);
        clusteringWindow.correctInput();
        assertEquals(1,clusteringWindow.getMaxIterations());
    }

    @Test
    public void InvalidTestMaxIterations() {
        configurationClusteringWindow clusteringWindow = new configurationClusteringWindow(-1,-1,2,false);
        clusteringWindow.correctInput();
        assertEquals(1,clusteringWindow.getMaxIterations());
    }

    @Test
    /*
    Tested the largest possible Integer value, test passed
     */
    public void boundaryTestUpdateInterval() {
        configurationClusteringWindow clusteringWindow = new configurationClusteringWindow(1,1,2,false);
        clusteringWindow.correctInput();
        assertEquals(1,clusteringWindow.getUpdateInterval());
    }

    @Test
    public void ValidTestUpdateInterval() {
        configurationClusteringWindow clusteringWindow = new configurationClusteringWindow(1,1,3,false);
        clusteringWindow.correctInput();
        assertEquals(1,clusteringWindow.getUpdateInterval());
    }

    @Test
    public void InvalidTestUpdateInterval() {
        configurationClusteringWindow clusteringWindow = new configurationClusteringWindow(1,-1,2,false);
        clusteringWindow.correctInput();
        assertEquals(1,clusteringWindow.getUpdateInterval());
    }

    @Test
    /*
    Tested the largest possible Integer value, test passed
     */
    public void boundaryTestNumLabels() {
        configurationClusteringWindow clusteringWindow = new configurationClusteringWindow(1,1,4,false);
        clusteringWindow.correctInput();
        assertEquals(4,clusteringWindow.getNumLabel());
    }

    @Test
    public void ValidTestNumLabels() {
        configurationClusteringWindow clusteringWindow = new configurationClusteringWindow(1,1,2,false);
        clusteringWindow.correctInput();
        assertEquals(2,clusteringWindow.getNumLabel());
    }

    @Test
    public void InvalidTestNumLabels() {
        configurationClusteringWindow clusteringWindow = new configurationClusteringWindow(1,-1,10,false);
        clusteringWindow.correctInput();
        assertEquals(4,clusteringWindow.getNumLabel());
    }

}