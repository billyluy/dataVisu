package ui;

import actions.AppActions;
import algorithms.Classifier;
import classification.Algorithm;
import data.DataSet;
import dataprocessors.AppData;
import dataprocessors.TSDProcessor;
import dataprocessors.configurationClassificationWindow;
import dataprocessors.configurationClusteringWindow;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import vilij.components.Dialog;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;

import static settings.AppPropertyTypes.*;
import static vilij.settings.PropertyTypes.*;

/**
 * This is the application's user interface implementation.
 *
 * @author Ritwik Banerjee
 */
public final class AppUI extends UITemplate {

    /** The application to which this class of actions belongs. */
    ApplicationTemplate applicationTemplate;

    @SuppressWarnings("FieldCanBeLocal")
    private Button                       scrnshotButton; // toolbar button to take a screenshot of the data
    private Button                       editToggle;
    private LineChart<Number, Number>    chart;          // the chart where data will be displayed
    private Button                       displayButton;  // workspace button to display data on the chart
    private TextArea                     textArea;       // text area for new data input
    private TextArea                     textArea2;       // text area for new data input
    private boolean                      hasNewText;     // whether or not the text area has any new data since last display
    protected String                     scrnshotPath;   // path to the 'screenshot' icon
    private CheckBox                     checkBox;
    private Text                         inputDataText;
    private VBox                         vPane;
    private HBox                         hPane;
    private HBox                         algorithmListPaneH;
    private VBox                         clusteringPane;
    private VBox                         classificationPane;
    private VBox                         algorithimPaneV;
    private VBox                         algorithmListPaneV2;
    private Text                         algorithmTitle;
    private ToggleButton                 tb1;
    private ToggleButton                 tb2;
    private RadioButton                  selectedButtton;
    private Button                       cb1;
    private Button                       cb2;
    private Button                       runButton;
    private ToggleButton                   algorType;
    private configurationClassificationWindow[] classificationList;
    private boolean[]                      isClassificationConfigedList;
    private configurationClusteringWindow[] clusteringList;
    private boolean[]                      isClusteringConfigedList;
    private ToggleGroup                  algorList;
    private DataSet                      dataSet;
    private Text                         algorCount;
    private Thread                       thread2;
    private Boolean                      runStarted;
    private Boolean                      isAlgorithmRun;
    private Boolean                      isLoad;
    private ArrayList<String>            listOfAlgors;

    public LineChart<Number, Number> getChart() { return chart; }

    public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
        super(primaryStage, applicationTemplate);
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    protected void setResourcePaths(ApplicationTemplate applicationTemplate) {
        super.setResourcePaths(applicationTemplate);
        PropertyManager manager = applicationTemplate.manager;
        String SEPARATOR = "/";
        String iconsPath = SEPARATOR + String.join(SEPARATOR,
                manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
                manager.getPropertyValue(ICONS_RESOURCE_PATH.name()));
        scrnshotPath = String.join(SEPARATOR, iconsPath, manager.getPropertyValue(SCREENSHOT_ICON.name()));
    }

    @Override
    protected void setToolBar(ApplicationTemplate applicationTemplate) {
        // TODO for homework 1
        PropertyManager manager = applicationTemplate.manager;
        newButton = super.setToolbarButton(newiconPath, manager.getPropertyValue(NEW_TOOLTIP.name()), false);
        saveButton = super.setToolbarButton(saveiconPath, manager.getPropertyValue(SAVE_TOOLTIP.name()), true);
        loadButton = super.setToolbarButton(loadiconPath, manager.getPropertyValue(LOAD_TOOLTIP.name()), false);
        exitButton = super.setToolbarButton(exiticonPath, manager.getPropertyValue(EXIT_TOOLTIP.name()), false);
        scrnshotButton = super.setToolbarButton(scrnshotPath, manager.getPropertyValue(SCREENSHOT_TOOLTIP.name()), true);
        toolBar = new ToolBar(newButton, saveButton, loadButton, exitButton, scrnshotButton);
    }

    @Override
    protected void setToolbarHandlers(ApplicationTemplate applicationTemplate) {
        applicationTemplate.setActionComponent(new AppActions(applicationTemplate));
        PropertyManager manager = applicationTemplate.manager;
        newButton.setOnAction(e -> applicationTemplate.getActionComponent().handleNewRequest());
        saveButton.setOnAction(e -> applicationTemplate.getActionComponent().handleSaveRequest());
        loadButton.setOnAction(e -> applicationTemplate.getActionComponent().handleLoadRequest());
        exitButton.setOnAction(e -> applicationTemplate.getActionComponent().handleExitRequest());
        scrnshotButton.setOnAction(e -> {
            try {
                ((AppActions)applicationTemplate.getActionComponent()).handleScreenshotRequest();
            } catch (IOException e1) {
                (applicationTemplate.getDialog(Dialog.DialogType.ERROR)).show(manager.getPropertyValue(ERROR_TITLE.name()), manager.getPropertyValue(INVALID_ERROR.name()));
            }
        });
    }

    @Override
    public void initialize() {
        layout();
        setWorkspaceActions();
    }

    @Override
    public void clear() {
        // TODO for homework 1
        textArea.clear();
    }

    public void displayAlgorithmButtons(){
        for(int i =0; i < listOfAlgors.size();i++){
            try {
                if(Class.forName(listOfAlgors.get(i)).getSuperclass().equals(Classifier.class)){
                    RadioButton rb1= new RadioButton(listOfAlgors.get(i).replaceAll("algorithmTypes.",""));
                    rb1.setToggleGroup(algorList);
                    rb1.setOnAction(Event->{
                        selectedButtton = rb1;
                        if(isClusteringConfigedList[0]){
                            runButton.setDisable(false);
                        }
                    });
                    classificationPane.getChildren().add(rb1);
                }else{
                    RadioButton rb1 = new RadioButton(listOfAlgors.get(i).replaceAll("algorithmTypes.",""));
                    rb1.setToggleGroup(algorList);
                    rb1.setOnAction(Event->{
                        selectedButtton = rb1;
                        if(rb1.getText().equals("KMeansClusterer")){
                            if(isClusteringConfigedList[0]){
                                runButton.setDisable(false);
                            }
                        }else{
                            if(isClusteringConfigedList[1]){
                                runButton.setDisable(false);
                            }
                        }
                    });
                    clusteringPane.getChildren().add(rb1);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void layout() {
        // TODO for homework 1
        listOfAlgors= new ArrayList<String>();
        findFolder();
        runStarted = false;
        isAlgorithmRun = false;
        dataSet = new DataSet();
        classificationList = new configurationClassificationWindow[3];
        isClassificationConfigedList = new boolean[3];
        clusteringList = new configurationClusteringWindow[3];
        isClusteringConfigedList = new boolean[3];
        PropertyManager manager = applicationTemplate.manager;
        appPane.getStylesheets().add(manager.getPropertyValue(CSS_PATH.name()));
        chart = new LineChart<>(new NumberAxis(),new NumberAxis());
        chart.setHorizontalGridLinesVisible(false);
        chart.setVerticalGridLinesVisible(false);
        chart.setHorizontalZeroLineVisible(false);
        chart.setVerticalZeroLineVisible(false);
        chart.setTitle(manager.getPropertyValue(CHART_TITLE.name()));
        chart.setMaxHeight(primaryStage.getHeight()/2);

        Text title = new Text();
        title.setText(manager.getPropertyValue(TEXT_AREA.name()));
        title.setFont(new Font(20));

        inputDataText = new Text();
        algorCount = new Text();
        textArea = new TextArea();
        textArea2 = new TextArea();
        displayButton = new Button();
        editToggle = new Button();
        editToggle.setText(manager.getPropertyValue(DONE.name()));
        displayButton.setText(manager.getPropertyValue(DISPLAY_BUTTON.name()));
        checkBox = new CheckBox(manager.getPropertyValue(CHECK_TITLE.name()));
        algorithmTitle = new Text();
        algorithmTitle.setText(manager.getPropertyValue(APPLICATION_TITLE.name()));
        algorithmTitle.setVisible(false);
        final ToggleGroup algoTypes = new ToggleGroup();

        tb1 = new ToggleButton(manager.getPropertyValue(ALGOR1.name()));
        tb1.setToggleGroup(algoTypes);
        tb1.setVisible(false);
        tb1.setDisable(true);

        tb2 = new ToggleButton(manager.getPropertyValue(ALGOR2.name()));
        tb2.setToggleGroup(algoTypes);
        tb2.setVisible(false);

        algorList = new ToggleGroup();

//        rb1 = new RadioButton();
//        rb1.setToggleGroup(algorList);
//        rb2 = new RadioButton();
//        rb2.setToggleGroup(algorList);

        Image settingImage = new Image(manager.getPropertyValue(SETTING_PATH.name()),20,20,true,false);
        Image runImage = new Image(manager.getPropertyValue(RUN_PATH.name()),20,20,true,false);

        runButton = new Button();
        runButton.setGraphic(new ImageView(runImage));
        runButton.setVisible(false);
        runButton.setDisable(true);

        cb1 = new Button();
        cb1.setGraphic(new ImageView(settingImage));
        cb2 = new Button();
        cb2.setGraphic(new ImageView(settingImage));

        clusteringPane = new VBox();
        clusteringPane.setSpacing(15);

        classificationPane = new VBox();
        classificationPane.setSpacing(15);

        displayAlgorithmButtons();

        algorithmListPaneV2 = new VBox();
        algorithmListPaneV2.getChildren().add(cb1);
        algorithmListPaneV2.getChildren().add(cb2);

        algorithmListPaneH = new HBox();
        algorithimPaneV = new VBox();
        algorithimPaneV.getChildren().add(clusteringPane);
        algorithimPaneV.getChildren().add(classificationPane);
        algorithmListPaneH.getChildren().add(algorithimPaneV);
        algorithmListPaneH.getChildren().add(algorithmListPaneV2);
        algorithmListPaneH.setVisible(false);
        algorithmListPaneH.setSpacing(20);

        vPane = new VBox(10);
        vPane.getChildren().add(title);
        vPane.getChildren().add(textArea);
        vPane.getChildren().add(editToggle);
        vPane.getChildren().add(inputDataText);
        vPane.getChildren().add(algorithmTitle);
        vPane.getChildren().add(algorCount);
        vPane.getChildren().add(tb1);
        vPane.getChildren().add(tb2);
        vPane.getChildren().add(algorithmListPaneH);
        vPane.getChildren().add(runButton);
        vPane.setVisible(false);

        hPane = new HBox();
        hPane.getChildren().add(vPane);
        hPane.getChildren().add(chart);

        appPane.getChildren().add(hPane);
    }

    private void setWorkspaceActions() {
        // TODO for homework 1
        TSDProcessor processor = ((AppData)applicationTemplate.getDataComponent()).getProcessor();
        PropertyManager manager = applicationTemplate.manager;
        editToggle.setOnAction(e -> {
            ((AppData)applicationTemplate.getDataComponent()).getProcessor().clear();
            if(editToggle.getText().equals(manager.getPropertyValue(DONE.name()))){
                try{
                    inputDataText.setText("");
                    processor.processString(textArea.getText());
                    String s = "";
                    s += processor.getInstanceSize() + manager.getPropertyValue(METADATA1.name()) + processor.getUniqueLabels().size() + manager.getPropertyValue(METADATA3.name()) +"\n";
                    for(Object labels : processor.getUniqueLabels()){
                        s += "\t -" + labels.toString() + "\n";
                    }
                    inputDataText.setText(s);
                    editToggle.setText(manager.getPropertyValue(EDIT.name()));
                    tb1.setSelected(false);
                    tb2.setSelected(false);
                    tb1.setVisible(true);
                    tb2.setVisible(true);
                    runButton.setVisible(false);
                    getAlgorithmListPaneH().setVisible(false);
                    algorithmTitle.setVisible(true);
                    textArea.setDisable(true);
                    if(((AppData)applicationTemplate.getDataComponent()).getProcessor().twoNonNulls()){
                        tb1.setDisable(false);
                    }else{
                        tb1.setDisable(true);
                    }
                    ((AppData)applicationTemplate.getDataComponent()).getProcessor().clear();
                }catch (Exception el) {
                if ((processor).getLineOfDupe() != -1) {
                    (applicationTemplate.getDialog(Dialog.DialogType.ERROR)).show(manager.getPropertyValue(ERROR_TITLE.name()), manager.getPropertyValue(DUPE_LINE.name())+processor.getLineOfDupe() +processor.getDupeName());
                }else if(!processor.getErrorArray().isEmpty()){
                    (applicationTemplate.getDialog(Dialog.DialogType.ERROR)).show(manager.getPropertyValue(ERROR_TITLE.name()), manager.getPropertyValue(ERROR_LINE.name()) + Integer.toString(processor.getErrorArray().get(0)));
                }else{
                    (applicationTemplate.getDialog(Dialog.DialogType.ERROR)).show(manager.getPropertyValue(ERROR_TITLE.name()), manager.getPropertyValue(RESOURCE_SUBDIR_NOT_FOUND.name()));
                }
            }
            }else if(editToggle.getText().equals(manager.getPropertyValue(EDIT.name()))){
                ((AppData)applicationTemplate.getDataComponent()).getProcessor().clear();
                inputDataText.setText("");
                editToggle.setText(manager.getPropertyValue(DONE.name()));
//                rb1.setVisible(true);
                clusteringPane.setVisible(true);
                tb1.setVisible(false);
                tb2.setVisible(false);
                algorithmTitle.setVisible(false);
                algorithmListPaneH.setVisible(false);
                runButton.setVisible(false);
//                rb1.setSelected(false);
                textArea.setDisable(false);
            }
        });
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            StringBuilder s = new StringBuilder();
            int amountOfMove = 10 - newValue.split("\n").length;
            ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(textArea2.getText().split("\n")));
            if(amountOfMove > 0 && (arrayList.size() > 0) && !textArea2.getText().isEmpty()){
                for(int i =0; i < amountOfMove; i++){
                    if(arrayList.size() > 0) {
                        s.append(arrayList.remove(0)).append("\n");
                    }
                }
                String p = "";
                while(arrayList.size() > 0){
                    p += arrayList.remove(0)+"\n";
                    System.out.println(p);
                }
                textArea2.setText(p);
            }
            textArea.setText(textArea.getText()+s);
            if(textArea.getText().isEmpty()){
                saveButton.setDisable(true);
            }else {
                saveButton.setDisable(false);
            }
        });
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            hasNewText = newValue;
            textArea.setDisable(hasNewText);
        });
        tb1.selectedProperty().addListener((observable, oldValue, newValue) -> {
            runButton.setVisible(true);
            runButton.setDisable(true);
            algorithmListPaneH.setVisible(true);
            if(algorithimPaneV.getChildren().contains(clusteringPane)){
                algorithimPaneV.getChildren().remove(clusteringPane);
            }
            if(!algorithimPaneV.getChildren().contains(classificationPane)){
                algorithimPaneV.getChildren().add(classificationPane);
            }
            algorType = tb1;
            cb2.setVisible(false);
            ((AppUI)applicationTemplate.getUIComponent()).getAlgorCount().setText("");
        });
        tb2.selectedProperty().addListener((observable, oldValue, newValue) -> {
            runButton.setVisible(true);
            runButton.setDisable(true);
            algorithmListPaneH.setVisible(true);
            if(!algorithimPaneV.getChildren().contains(clusteringPane)){
                algorithimPaneV.getChildren().add(clusteringPane);
            }
            if(algorithimPaneV.getChildren().contains(classificationPane)){
                algorithimPaneV.getChildren().remove(classificationPane);
            }
            algorType = tb2;
            cb2.setVisible(true);
            ((AppUI)applicationTemplate.getUIComponent()).getAlgorCount().setText("");
        });
        cb1.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if(algorType.equals(tb1)){
                configurationClassificationWindow configWindow = new configurationClassificationWindow();
                if(!isClassificationConfigedList[0]){
                    configWindow.showConfig(cb1,1,1,false);
                    isClassificationConfigedList[0] = true;
                }else{
                    configWindow.showConfig(cb1, classificationList[0].getMaxIterations(),  classificationList[0].getUpdateInterval(),  classificationList[0].getContinuous());
                }
                if(configWindow != (null)){
                    classificationList[0] = configWindow;
                }
                if(selectedButtton.getText().equals("RandomClassifier")){
                    runButton.setDisable(false);
                }
            }else if(algorType.equals(tb2)){
                configurationClusteringWindow configWindow = new configurationClusteringWindow();
                if(!isClusteringConfigedList[0]){
                    configWindow.showConfig(cb1,1,1,1,false);
                    isClusteringConfigedList[0] = true;
                }else{
                    configWindow.showConfig(cb1, clusteringList[0].getMaxIterations(),  clusteringList[0].getUpdateInterval(), clusteringList[0].getNumLabel(),  clusteringList[0].getContinuous());
                }
                if(configWindow != (null)){
                    clusteringList[0] = configWindow;
                }
            }
        });
        cb2.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if(algorType.equals(tb1)){
                configurationClassificationWindow configWindow = new configurationClassificationWindow();
                if(!isClassificationConfigedList[1]){
                    configWindow.showConfig(cb1,1,1,false);
                    isClassificationConfigedList[1] = true;
                }else{
                    configWindow.showConfig(cb1, classificationList[1].getMaxIterations(),  classificationList[1].getUpdateInterval(),  classificationList[1].getContinuous());
                }
                if(configWindow != (null)){
                    classificationList[1] = configWindow;
                }
            }else if(algorType.equals(tb2)){
                configurationClusteringWindow configWindow = new configurationClusteringWindow();
                if(!isClusteringConfigedList[1]){
                    configWindow.showConfig(cb1,1,1,1,false);
                    isClusteringConfigedList[1] = true;
                }else{
                    configWindow.showConfig(cb1, clusteringList[1].getMaxIterations(),  clusteringList[1].getUpdateInterval(), clusteringList[1].getNumLabel(),  clusteringList[1].getContinuous());
                }
                if(configWindow != (null)){
                    clusteringList[1] = configWindow;
                }
            }
        });
        algorList.selectedToggleProperty().addListener((ov, toggle, new_toggle) -> {
            try{
                if(algorType.equals(tb1)){
                    if(new_toggle.equals(selectedButtton)&&classificationList[0]!=null){
                        runButton.setDisable(false);
                    }
                    else{
                        runButton.setDisable(true);
                    }
                }
                if(algorType.equals(tb2)){
                    if(new_toggle.equals(selectedButtton)&&clusteringList[0]!=null){
                        runButton.setDisable(false);
                    }
                    else{
                        runButton.setDisable(true);
                    }
                }
            }catch(Exception el){
                runButton.setDisable(true);
            }

        });
        runButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            algorCount.setText("algorithm is running");
            DataSet dataSet = new DataSet();
            if(algorType.equals(tb1)){
                try{
                    if(!runStarted){
                        runStarted = true;
                        isAlgorithmRun = true;
                        runButton.setDisable(true);
                        Class randomClassifier= Class.forName(listOfAlgors.get(1));
                        Constructor randomClassifierConstructor = randomClassifier.getConstructors()[0];
                        Algorithm RandomClassifierAlgorithm = (Algorithm)(randomClassifierConstructor.newInstance(dataSet, classificationList[0].getMaxIterations(), classificationList[0].getUpdateInterval(), classificationList[0].getContinuous(),applicationTemplate));
                        thread2 = new Thread(RandomClassifierAlgorithm);
                        ((AppData)applicationTemplate.getDataComponent()).clear();
                        String s = ((AppUI)applicationTemplate.getUIComponent()).getTextArea().getText() + ((AppUI)applicationTemplate.getUIComponent()).getTextArea2().getText();
                        ((AppData) applicationTemplate.getDataComponent()).loadData(s);
                        thread2.start();
                    }else{
                        synchronized (applicationTemplate.manager){
                            applicationTemplate.manager.notify();
                        }
                    }
                }catch(Exception el){
                    el.printStackTrace();
                }
            }else if(algorType.equals(tb2)){
                if(selectedButtton.getText().equals("KMeansClusterer")){
                    try{
                        dataSet = dataSet.fromString(textArea.getText() + textArea2.getText());
                        if (!runStarted) {
                            runStarted = true;
                            isAlgorithmRun = true;
                            runButton.setDisable(true);
                            Class kMeanClass= Class.forName(listOfAlgors.get(0));
                            Constructor kMeanConstructor = kMeanClass.getConstructors()[0];
                            Algorithm kMeanAlgorithm = (Algorithm)(kMeanConstructor.newInstance(dataSet, clusteringList[0].getMaxIterations(), clusteringList[0].getUpdateInterval(), clusteringList[0].getNumLabel(), clusteringList[0].getContinuous(),applicationTemplate));
                            ((AppData) applicationTemplate.getDataComponent()).clear();
                            String s = ((AppUI) applicationTemplate.getUIComponent()).getTextArea().getText() + ((AppUI) applicationTemplate.getUIComponent()).getTextArea2().getText();
                            ((AppData) applicationTemplate.getDataComponent()).loadData(s);
                            new Thread(kMeanAlgorithm).start();
                        }else{
                            synchronized (applicationTemplate.manager){
                                applicationTemplate.manager.notify();
                            }
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }else{
                    try{
                        dataSet = dataSet.fromString(textArea.getText() + textArea2.getText());
                        if (!runStarted) {
                            runStarted = true;
                            isAlgorithmRun = true;
                            runButton.setDisable(true);
                            Class randomClusterer= Class.forName(listOfAlgors.get(2));
                            Constructor randomClustererConstructor = randomClusterer.getConstructors()[0];
                            Algorithm RandomClustererAlgorithm = (Algorithm)(randomClustererConstructor.newInstance(dataSet, clusteringList[1].getMaxIterations(), clusteringList[1].getUpdateInterval(), clusteringList[1].getNumLabel(), clusteringList[1].getContinuous(),applicationTemplate));
                            thread2 = new Thread(RandomClustererAlgorithm);
                            ((AppData) applicationTemplate.getDataComponent()).clear();
                            String s = ((AppUI) applicationTemplate.getUIComponent()).getTextArea().getText() + ((AppUI) applicationTemplate.getUIComponent()).getTextArea2().getText();
                            ((AppData) applicationTemplate.getDataComponent()).loadData(s);
                            thread2.start();
                        }else{
                            synchronized (applicationTemplate.manager){
                                applicationTemplate.manager.notify();
                            }
                        }
                    }catch(Exception el){
                        el.printStackTrace();
                    }
                }
            }
        });
    }

    public void findFolder(){
        File folder = new File("hw1\\data-vilij\\src\\algorithmTypes");
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                listOfAlgors.add("algorithmTypes."+listOfFiles[i].getName().replaceAll(".java",""));
            } else if (listOfFiles[i].isDirectory()) {
            }
        }
        System.out.println(listOfAlgors.toString());
    }

    public VBox getVPane() { return vPane;}

    public TextArea getTextArea(){
        return textArea;
    }

    public Button getNewButton(){
        return newButton;
    }

    public Boolean getRunStarted(){
        return runStarted;
    }

    public void setRunStarted(Boolean bol){
        runStarted = bol;
    }

    public void setIsAlgorithmRun(Boolean bol){
        isAlgorithmRun = bol;
    }

    public Boolean getIsAlgorithmRun(){
        return isAlgorithmRun;
    }

//    public RandomClassifier getRandomClassifier(){
//        return randomClassifier;
//    }

    public Thread getThread2(){
        return thread2;
    }

    public TextArea getTextArea2(){
        return textArea2;
    }

    public Text getInputDataText() { return inputDataText; }

    public Button getSaveButton(){
        return saveButton;
    }

    public Button getScrnshotButton(){
        return scrnshotButton;
    }

    public Button getLoadButton(){
        return loadButton;
    }

    public Button getEditToggle(){
        return editToggle;
    }

    public ToggleButton getTb1(){
        return tb1;
    }

    public ToggleButton getTb2() {
        return tb2;
    }

    public Text getAlgorithimTitle(){
        return algorithmTitle;
    }

    public VBox getClusteringPane() {
        return clusteringPane;
    }

    public VBox getClassificationPane() {
        return classificationPane;
    }

    //    public RadioButton getRb1(){
//        return rb1;
//    }

//    public RadioButton getRb2(){
//        return rb2;
//    }
//
//    public RadioButton getRb3(){
//        return rb3;
//    }

    public Button getRunButton(){
        return runButton;
    }

    public HBox getAlgorithmListPaneH(){
        return algorithmListPaneH;
    }

    public Text getAlgorCount(){
        return algorCount;
    }

    public void setIsLoad(Boolean bol){
        isLoad = bol;
    }
}
