package clustering;

import data.DataSet;
import dataprocessors.AppData;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import ui.AppUI;
import vilij.templates.ApplicationTemplate;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Ritwik Banerjee
 */
public class KMeansClusterer extends Clusterer {

    private DataSet dataset;
    private List<Point2D> centroids;

    private final int           maxIterations;
    private final int           updateInterval;
    private final AtomicBoolean tocontinue;
    private boolean             continuousRun;
    private ApplicationTemplate applicationTemplate;
    private int                 count;


    public KMeansClusterer(DataSet dataset, int maxIterations, int updateInterval, int numberOfClusters, boolean continuousRun) {
        super(numberOfClusters);
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(false);
        this.continuousRun = continuousRun;
        this.applicationTemplate = new ApplicationTemplate();
        count = 1;
    }

    public void setApplicationTemplate(ApplicationTemplate applicationTemplate){
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    public int getMaxIterations() { return maxIterations; }

    @Override
    public int getUpdateInterval() { return updateInterval; }

    @Override
    public boolean tocontinue() { return tocontinue.get(); }

    @Override
    public void run() {
        //to continue is finish
        initializeCentroids();
        int iteration = 0;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ((AppUI)applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(true);
                ((AppUI)applicationTemplate.getUIComponent()).getSaveButton().setDisable(true);
                ((AppUI)applicationTemplate.getUIComponent()).getLoadButton().setDisable(true);
                ((AppUI)applicationTemplate.getUIComponent()).getNewButton().setDisable(true);
            }
        });
        for (int i = 1; i <= maxIterations && tocontinue() && continuousRun; i++) {
            assignLabels();
            recomputeCentroids();
            if(i % updateInterval == 0) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        ((AppData)applicationTemplate.getDataComponent()).updateLabels(dataset.getLabels());
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        while(count< maxIterations && tocontinue() && !continuousRun) {
            count++;
            assignLabels();
            recomputeCentroids();
            if (count % updateInterval == 0) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        ((AppUI) applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(true);
                    }
                });
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        ((AppData)applicationTemplate.getDataComponent()).updateLabels(dataset.getLabels());
                        ((AppUI) applicationTemplate.getUIComponent()).getRunButton().setDisable(true);
                        ((AppUI) applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(false);
                    }
                });
                if (count >= maxIterations) {
                    break;
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        ((AppUI) applicationTemplate.getUIComponent()).getRunButton().setDisable(false);
                    }
                });
                try {
                    synchronized (this) {
                        this.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        ((AppUI) applicationTemplate.getUIComponent()).getRunButton().setDisable(false);
                    }
                });
            }
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ((AppUI)applicationTemplate.getUIComponent()).getRunButton().setDisable(false);
                ((AppUI)applicationTemplate.getUIComponent()).setRunStarted(false);
                ((AppUI)applicationTemplate.getUIComponent()).setIsAlgorithmRun(false);
                ((AppUI)applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(false);
                ((AppUI)applicationTemplate.getUIComponent()).getSaveButton().setDisable(false);
                ((AppUI)applicationTemplate.getUIComponent()).getLoadButton().setDisable(false);
                ((AppUI)applicationTemplate.getUIComponent()).getAlgorCount().setText("Algorithm is done");
            }
        });
    }

    private void initializeCentroids() {
        Set<String>  chosen        = new HashSet<>();
        List<String> instanceNames = new ArrayList<>(dataset.getLabels().keySet());
        Random       r             = new Random();
        while (chosen.size() < numberOfClusters) {
            int i = r.nextInt(instanceNames.size());
            while (chosen.contains(instanceNames.get(i)))
                i = (++i % instanceNames.size());
            chosen.add(instanceNames.get(i));
        }
        centroids = chosen.stream().map(name -> dataset.getLocations().get(name)).collect(Collectors.toList());
        tocontinue.set(true);
    }

    private void assignLabels() {
        dataset.getLocations().forEach((instanceName, location) -> {
            double minDistance      = Double.MAX_VALUE;
            int    minDistanceIndex = -1;
            for (int i = 0; i < centroids.size(); i++) {
                double distance = computeDistance(centroids.get(i), location);
                if (distance < minDistance) {
                    minDistance = distance;
                    minDistanceIndex = i;
                }
            }
            dataset.getLabels().put(instanceName, Integer.toString(minDistanceIndex));
        });
    }

    private void recomputeCentroids() {
        tocontinue.set(false);
        IntStream.range(0, numberOfClusters).forEach(i -> {
            AtomicInteger clusterSize = new AtomicInteger();
            Point2D sum = dataset.getLabels()
                    .entrySet()
                    .stream()
                    .filter(entry -> i == Integer.parseInt(entry.getValue()))
                    .map(entry -> dataset.getLocations().get(entry.getKey()))
                    .reduce(new Point2D(0, 0), (p, q) -> {
                        clusterSize.incrementAndGet();
                        return new Point2D(p.getX() + q.getX(), p.getY() + q.getY());
                    });
            Point2D newCentroid = new Point2D(sum.getX() / clusterSize.get(), sum.getY() / clusterSize.get());
            if (!newCentroid.equals(centroids.get(i))) {
                centroids.set(i, newCentroid);
                tocontinue.set(true);
            }
        });
    }

    private static double computeDistance(Point2D p, Point2D q) {
        return Math.sqrt(Math.pow(p.getX() - q.getX(), 2) + Math.pow(p.getY() - q.getY(), 2));
    }

}
