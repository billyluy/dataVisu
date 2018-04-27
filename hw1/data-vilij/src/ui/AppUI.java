package ui;

import actions.AppActions;
import classification.RandomClassifier;
import data.DataSet;
import dataprocessors.AppData;
import dataprocessors.TSDProcessor;
import dataprocessors.configurationClassificationWindow;
import dataprocessors.configurationClusteringWindow;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
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

import java.io.IOException;
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
    private VBox                         algorithmListPaneV1;
    private VBox                         algorithmListPaneV2;
    private Text                         algorithmTitle;
    private ToggleButton                 tb1;
    private ToggleButton                 tb2;
    private RadioButton                  rb1;
    private Button                       cb1;
    private Button                       runButton;
    private ToggleButton                   algorType;
    private configurationClassificationWindow[] classificationList;
    private boolean[]                      isClassificationConfigedList;
    private configurationClusteringWindow[] clusteringList;
    private boolean[]                      isClusteringConfigedList;
    private ToggleGroup                  algorList;
    private RandomClassifier             randomClassifier;
    private DataSet                      dataSet;
    private Text                         algorCount;
    private Thread                       thread2;
    private Boolean                      runStarted;
    private Boolean                      isAlgorithmRun;
    private Boolean                      isLoad;

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

    private void layout() {
        // TODO for homework 1
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

        rb1 = new RadioButton();
        rb1.setToggleGroup(algorList);

        Image settingImage = new Image(manager.getPropertyValue(SETTING_PATH.name()),20,20,true,false);
        Image runImage = new Image(manager.getPropertyValue(RUN_PATH.name()),20,20,true,false);

        runButton = new Button();
        runButton.setGraphic(new ImageView(runImage));
        runButton.setVisible(false);
        runButton.setDisable(true);

        cb1 = new Button();
        cb1.setGraphic(new ImageView(settingImage));

        algorithmListPaneV1 = new VBox();
        algorithmListPaneV1.getChildren().add(rb1);
        algorithmListPaneV1.setSpacing(15);

        algorithmListPaneV2 = new VBox();
        algorithmListPaneV2.getChildren().add(cb1);

        algorithmListPaneH = new HBox();
        algorithmListPaneH.getChildren().add(algorithmListPaneV1);
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
                rb1.setVisible(true);
                tb1.setVisible(false);
                tb2.setVisible(false);
                algorithmTitle.setVisible(false);
                algorithmListPaneH.setVisible(false);
                runButton.setVisible(false);
                rb1.setSelected(false);
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
            algorithmListPaneH.setVisible(true);
            algorType = tb1;
            rb1.setText("Random"+algorType.getText());
            rb1.setSelected(false);
            ((AppUI)applicationTemplate.getUIComponent()).getAlgorCount().setText("");
        });
        tb2.selectedProperty().addListener((observable, oldValue, newValue) -> {
            runButton.setVisible(true);
            algorithmListPaneH.setVisible(true);
            algorType = tb2;
            rb1.setText("Random"+algorType.getText());
            rb1.setSelected(false);
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
                if(rb1.isSelected()){
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
                if(rb1.isSelected()){
                    runButton.setDisable(false);
                }
            }
        });
//        cb2.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
//            @Override public void handle(MouseEvent e) {
//                if(algorType.equals(tb1)){
//                    configurationClassificationWindow configWindow = new configurationClassificationWindow();
//                    if(!isClassificationConfigedList[1]){
//                        configWindow.showConfig(cb1,1,1,false);
//                        isClassificationConfigedList[1] = true;
//                    }else{
//                        configWindow.showConfig(cb1, classificationList[1].getMaxIterations(),  classificationList[1].getUpdateInterval(),  classificationList[1].getContinuous());
//                    }
//                    if(configWindow != (null)){
//                        classificationList[1] = configWindow;
//                    }
//                }else if(algorType.equals(tb2)){
//                    configurationClusteringWindow configWindow = new configurationClusteringWindow();
//                    if(!isClusteringConfigedList[1]){
//                        configWindow.showConfig(cb1,1,1,1,false);
//                        isClusteringConfigedList[1] = true;
//                    }else{
//                        configWindow.showConfig(cb1, clusteringList[1].getMaxIterations(),  clusteringList[1].getUpdateInterval(), clusteringList[1].getNumLabel(),  clusteringList[1].getContinuous());
//                    }
//                    if(configWindow != (null)){
//                        clusteringList[1] = configWindow;
//                    }
//                }
//            }
//        });
//        cb3.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
//            @Override public void handle(MouseEvent e) {
//                if(algorType.equals(tb1)){
//                    configurationClassificationWindow configWindow = new configurationClassificationWindow();
//                    if(!isClassificationConfigedList[2]){
//                        configWindow.showConfig(cb1,1,1,false);
//                        isClassificationConfigedList[2] = true;
//                    }else{
//                        configWindow.showConfig(cb1, classificationList[2].getMaxIterations(),  classificationList[2].getUpdateInterval(),  classificationList[2].getContinuous());
//                    }
//                    if(configWindow != (null)){
//                        classificationList[2] = configWindow;
//                    }
//                }else if(algorType.equals(tb2)){
//                    configurationClusteringWindow configWindow = new configurationClusteringWindow();
//                    if(!isClusteringConfigedList[2]){
//                        configWindow.showConfig(cb1,1,1,1,false);
//                        isClusteringConfigedList[2] = true;
//                    }else{
//                        configWindow.showConfig(cb1, clusteringList[2].getMaxIterations(),  clusteringList[2].getUpdateInterval(), clusteringList[2].getNumLabel(),  clusteringList[2].getContinuous());
//                    }
//                    if(configWindow != (null)){
//                        clusteringList[2] = configWindow;
//                    }
//                }
//            }
//        });
        algorList.selectedToggleProperty().addListener((ov, toggle, new_toggle) -> {
            try{
                if(algorType.equals(tb1)){
                    if(new_toggle.equals(rb1)&&classificationList[0]!=null){
                        runButton.setDisable(false);
                    }
                    else{
                        runButton.setDisable(true);
                    }
                }
                if(algorType.equals(tb2)){
                    if(new_toggle.equals(rb1)&&clusteringList[0]!=null){
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
            if(!runStarted){
                runStarted = true;
                isAlgorithmRun = true;
                runButton.setDisable(true);
                randomClassifier = new RandomClassifier(dataSet,classificationList[0].getMaxIterations(),classificationList[0].getUpdateInterval(),classificationList[0].getContinuous());
                thread2 = new Thread(randomClassifier);
                randomClassifier.setApplicationTemplate(applicationTemplate);
                ((AppData)applicationTemplate.getDataComponent()).clear();
                String s = ((AppUI)applicationTemplate.getUIComponent()).getTextArea().getText() + ((AppUI)applicationTemplate.getUIComponent()).getTextArea2().getText();
                ((AppData) applicationTemplate.getDataComponent()).loadData(s);
                thread2.start();
            }else{
                synchronized (randomClassifier){
                    randomClassifier.notify();
                }
            }
        });
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

    public RandomClassifier getRandomClassifier(){
        return randomClassifier;
    }

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

    public RadioButton getRb1(){
        return rb1;
    }

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
