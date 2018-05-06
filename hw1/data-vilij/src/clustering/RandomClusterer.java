package clustering;

import data.DataSet;
import dataprocessors.AppData;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import ui.AppUI;
import vilij.templates.ApplicationTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class RandomClusterer extends Clusterer{
    private DataSet dataset;
    private List<Point2D> centroids;

    private final int           maxIterations;
    private final int           updateInterval;
    private final AtomicBoolean tocontinue;
    private ApplicationTemplate applicationTemplate;
    private int                 count;

    public RandomClusterer(DataSet dataset, int maxIterations, int updateInterval, int numberOfClusters, boolean toContinue) {
        super(numberOfClusters);
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(toContinue);
        applicationTemplate = new ApplicationTemplate();
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

    public synchronized void run() {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ((AppUI)applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(true);
                ((AppUI)applicationTemplate.getUIComponent()).getSaveButton().setDisable(true);
                ((AppUI)applicationTemplate.getUIComponent()).getLoadButton().setDisable(true);
                ((AppUI)applicationTemplate.getUIComponent()).getNewButton().setDisable(true);
            }
        });
        for (int i = 1; i <= maxIterations && tocontinue(); i++) {
            assignLabels();
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
        while(count< maxIterations && !tocontinue()) {
            count++;
            assignLabels();
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
                ((AppUI)applicationTemplate.getUIComponent()).getCb1().setDisable(false);
                ((AppUI)applicationTemplate.getUIComponent()).getCb2().setDisable(false);
                ((AppUI)applicationTemplate.getUIComponent()).getTb1().setVisible(true);
                ((AppUI)applicationTemplate.getUIComponent()).getTb2().setVisible(true);
                ((AppUI)applicationTemplate.getUIComponent()).getRb1().setDisable(false);
                ((AppUI)applicationTemplate.getUIComponent()).getRb2().setDisable(false);
                ((AppUI)applicationTemplate.getUIComponent()).getAlgorCount().setText("Algorithm is done");
            }
        });
    }

    private void assignLabels() {
        dataset.getLocations().forEach((instanceName, location) -> {
            Random RAND = new Random();
            int randomNum = RAND.nextInt((numberOfClusters));
            dataset.getLabels().put(instanceName, Integer.toString(randomNum));
        });
    }

}
