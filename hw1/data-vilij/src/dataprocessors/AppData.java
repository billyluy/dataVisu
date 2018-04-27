package dataprocessors;

import javafx.scene.chart.XYChart;
import ui.AppUI;
import vilij.components.DataComponent;
import vilij.components.Dialog;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;

import javafx.geometry.Point2D;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static settings.AppPropertyTypes.*;

/**
 * This is the concrete application-specific implementation of the data component defined by the Vilij framework.
 *
 * @author Ritwik Banerjee
 * @see DataComponent
 */
public class AppData implements DataComponent {

    private TSDProcessor        processor;
    private ApplicationTemplate applicationTemplate;
    private StringBuilder       inputData;
    private Double              maxX;
    private Double              minX;
    private XYChart.Series<Number,Number> series2;

    public AppData(ApplicationTemplate applicationTemplate) {
        this.processor = new TSDProcessor();
        this.applicationTemplate = applicationTemplate;
        minX = Double.MAX_VALUE;
        maxX = Double.MIN_VALUE;
    }

    @Override
    public void loadData(Path dataFilePath) {
        // TODO: NOT A PART OF HW 1
        PropertyManager manager = applicationTemplate.manager;
        StringBuilder first = new StringBuilder();
        StringBuilder rest = new StringBuilder();
        inputData = new StringBuilder();
        int lineCount = 0;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(dataFilePath.toString()));
            String s;
            while((s= bufferedReader.readLine()) != null){
                if(lineCount < 10){
                    first.append(s).append("\n");
                }else{
                    rest.append(s).append("\n");
                }
                lineCount++;
            }
            try {
                processor.clear();
                processor.processString(first + rest.toString());
                ((AppUI) applicationTemplate.getUIComponent()).getTextArea().setText(first.toString());
                ((AppUI) applicationTemplate.getUIComponent()).getTextArea2().setText(rest.toString());
                loadMetaData(dataFilePath.toString());
                ((AppUI) applicationTemplate.getUIComponent()).getVPane().setVisible(true);
                ((AppUI) applicationTemplate.getUIComponent()).getTextArea().setDisable(true);
                ((AppUI) applicationTemplate.getUIComponent()).getAlgorithimTitle().setVisible(true);
                ((AppUI)applicationTemplate.getUIComponent()).getTb1().setVisible(true);
                ((AppUI)applicationTemplate.getUIComponent()).getTb2().setVisible(true);
                ((AppUI)applicationTemplate.getUIComponent()).getTb1().setDisable(false);
                ((AppUI) applicationTemplate.getUIComponent()).getTb1().setSelected(false);
                ((AppUI) applicationTemplate.getUIComponent()).getTb2().setSelected(false);
                ((AppUI) applicationTemplate.getUIComponent()).getAlgorithimTitle().setVisible(true);
                ((AppUI) applicationTemplate.getUIComponent()).getAlgorithmListPaneH().setVisible(false);
                ((AppUI) applicationTemplate.getUIComponent()).getRunButton().setVisible(false);
                ((AppUI) applicationTemplate.getUIComponent()).getTb1().setVisible(true);
                ((AppUI) applicationTemplate.getUIComponent()).getTb2().setVisible(true);
                if(((AppData)applicationTemplate.getDataComponent()).getProcessor().twoNonNulls()){
                    ((AppUI) applicationTemplate.getUIComponent()).getTb1().setDisable(false);
                }else{
                    ((AppUI) applicationTemplate.getUIComponent()).getTb1().setDisable(true);
                }
                (applicationTemplate.getDialog(Dialog.DialogType.ERROR)).show(manager.getPropertyValue(ERROR_TITLE.name()), manager.getPropertyValue(LENGTH1.name()) + Integer.toString(lineCount) +manager.getPropertyValue(LENGTH2.name()));
            } catch (Exception e) {
                if (processor.getLineOfDupe() != -1) {
                    (applicationTemplate.getDialog(Dialog.DialogType.ERROR)).show(manager.getPropertyValue(ERROR_TITLE.name()), manager.getPropertyValue(DUPE_LINE.name())+processor.getLineOfDupe() +processor.getDupeName());
                }else if(!processor.getErrorArray().isEmpty()){
                    (applicationTemplate.getDialog(Dialog.DialogType.ERROR)).show(manager.getPropertyValue(ERROR_TITLE.name()), manager.getPropertyValue(ERROR_LINE.name()) + Integer.toString(processor.getErrorArray().get(0)));
                }else{
                    (applicationTemplate.getDialog(Dialog.DialogType.ERROR)).show(manager.getPropertyValue(ERROR_TITLE.name()), manager.getPropertyValue(RESOURCE_SUBDIR_NOT_FOUND.name()));
                }
            }
        } catch (IOException e) {
            (applicationTemplate.getDialog(Dialog.DialogType.ERROR)).show(manager.getPropertyValue(ERROR_TITLE.name()), manager.getPropertyValue(INVALID_ERROR.name()));
        }
    }

    public void loadData(String dataString) {
        // TODO for homework 1
        PropertyManager manager = applicationTemplate.manager;
        try {
            ((AppUI) applicationTemplate.getUIComponent()).getChart().getData().clear();
            clear();
            processor.processString(dataString);
            displayData();
        } catch (Exception e) {
            (applicationTemplate.getDialog(Dialog.DialogType.ERROR)).show(manager.getPropertyValue(ERROR_TITLE.name()), manager.getPropertyValue(INVALID_ERROR.name()));
            ((AppUI) applicationTemplate.getUIComponent()).getChart().getData().clear();
            ((AppUI) applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(true);
        }
    }

    @Override
    public void saveData(Path dataFilePath) {
        // TODO: NOT A PART OF HW 1
        PropertyManager manager = applicationTemplate.manager;
        String text = ((AppUI)applicationTemplate.getUIComponent()).getTextArea().getText() + ((AppUI)applicationTemplate.getUIComponent()).getTextArea2().getText();
        try (PrintWriter writer = new PrintWriter(Files.newOutputStream(dataFilePath))){
            writer.write(text);
        } catch (Exception e) {
            (applicationTemplate.getDialog(Dialog.DialogType.ERROR)).show(manager.getPropertyValue(ERROR_TITLE.name()), manager.getPropertyValue(INVALID_ERROR.name()));
        }
    }

    public void loadMetaData(String location){
        PropertyManager manager = applicationTemplate.manager;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(processor.getInstanceSize() + manager.getPropertyValue(METADATA1.name()) + processor.getUniqueLabels().size() + manager.getPropertyValue(METADATA2.name()) + "\n"
                + location + "\n");
        for(Object labels : processor.getUniqueLabels()){
            stringBuilder.append("\t -" + labels.toString() + "\n");
        }
        ((AppUI) applicationTemplate.getUIComponent()).getInputDataText().setText(stringBuilder.toString());
    }

    @Override
    public void clear() {
        processor.clear();
    }

    public void displayData() {
        processor.toChartData(((AppUI) applicationTemplate.getUIComponent()).getChart());
        if(!((AppUI) applicationTemplate.getUIComponent()).getChart().getData().isEmpty()){
            for(int i =0; i <processor.getDataPoints().values().size(); i++){
                if(minX > ((Point2D)processor.getDataPoints().values().toArray()[i]).getX()){
                    minX = ((Point2D)processor.getDataPoints().values().toArray()[i]).getX();
                }
                if(maxX < ((Point2D)processor.getDataPoints().values().toArray()[i]).getX()){
                    maxX = ((Point2D)processor.getDataPoints().values().toArray()[i]).getX();
                }
            }
            ((AppUI) applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(false);
        }
    }

    public void addTwoPointLine(Double y1, Double y2){
        series2 = new XYChart.Series<>();
        series2.getData().add(new XYChart.Data<>(minX, y1));
        series2.getData().add(new XYChart.Data<>(maxX, y2));
        ((AppUI) applicationTemplate.getUIComponent()).getChart().getData().add(series2);
        for (Object data: series2.getData()) {
            ((XYChart.Data)data).getNode().setVisible(false);
            series2.getNode().setId("AverageId");
        }
    }

    public TSDProcessor getProcessor() {
        return processor;
    }

    public Double getMaxX(){
        return maxX;
    }

    public Double getMinX(){
        return minX;
    }

    public XYChart.Series<Number, Number> getseries2(){
        return series2;
    }
}
