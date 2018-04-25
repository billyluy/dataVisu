package classification;

import algorithms.Classifier;
import data.DataSet;
import dataprocessors.AppData;
import ui.AppUI;
import vilij.templates.ApplicationTemplate;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Ritwik Banerjee
 */
public class RandomClassifier extends Classifier {

    private static final Random RAND = new Random();

    @SuppressWarnings("FieldCanBeLocal")
    // this mock classifier doesn't actually use the data, but a real classifier will
    private DataSet dataset;

    private final int maxIterations;
    private final int updateInterval;
    private ApplicationTemplate applicationTemplate;
    private Double y1;
    private Double y2;

    // currently, this value does not change after instantiation
    private final AtomicBoolean tocontinue;

    @Override
    public int getMaxIterations() {
        return maxIterations;
    }

    @Override
    public int getUpdateInterval() {
        return updateInterval;
    }

    @Override
    public boolean tocontinue() {
        return tocontinue.get();
    }

    public RandomClassifier(DataSet dataset,
                            int maxIterations,
                            int updateInterval,
                            boolean tocontinue) {
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(tocontinue);
//        this.applicationTemplate = applicationTemaple;
    }

    public void setApplicationTemplate(ApplicationTemplate applicationTemplate){
        this.applicationTemplate = applicationTemplate;
        System.out.println(((AppUI)applicationTemplate.getUIComponent()).getAlgorithimTitle().getText());
    }

    @Override
    public void run() {
        /*
        Most code should be done here
        (Ax+By=C)
         */
        for (int i = 1; i <= maxIterations && tocontinue(); i++) {
            System.out.println("run");
            int xCoefficient =  new Long(-1 * Math.round((2 * RAND.nextDouble() - 1) * 10)).intValue();
            int yCoefficient = 10;
            int constant     = RAND.nextInt(11);

            y1 = ((constant-(xCoefficient*((AppData)applicationTemplate.getDataComponent()).getMinX()))/yCoefficient);
            y2 = ((constant-(xCoefficient*((AppData)applicationTemplate.getDataComponent()).getMaxX()))/yCoefficient);

            System.out.println("min: "+((AppData)applicationTemplate.getDataComponent()).getMinX() + "," + y1);
            System.out.println("max: "+((AppData)applicationTemplate.getDataComponent()).getMaxX() + "," + y2);

            // this is the real output of the classifier
            output = Arrays.asList(xCoefficient, yCoefficient, constant);

            // everything below is just for internal viewing of how the output is changing
            // in the final project, such changes will be dynamically visible in the UI
            if (i % updateInterval == 0) {
                System.out.printf("Iteration number %d: ", i); //
                flush();
            }
            if (i > maxIterations * .6 && RAND.nextDouble() < 0.05) {
                System.out.printf("Iteration number %d: ", i);
                flush();
                break;
            }
        }

        Thread thread1 = new Thread();
        /*
        the threading should be in appui, thread acalls run on randomclassifier. pausing and waiting etc.
        try {
            System.out.println("sleep");
            thread1.sleep(500);
            System.out.println("wake");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */

    }

    // for internal viewing only
    protected void flush() {
        System.out.printf("%d\t%d\t%d%n", output.get(0), output.get(1), output.get(2));
    }

    /** A placeholder main method to just make sure this code runs smoothly */
    public static void main(String... args) throws IOException {

        DataSet          dataset    = DataSet.fromTSDFile(Paths.get("/path/to/some-data.tsd"));
        RandomClassifier classifier = new RandomClassifier(dataset, 100, 5, true);
        classifier.run(); // no multithreading yet

    }
}