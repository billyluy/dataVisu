package ui;

import actions.AppActions;
import dataprocessors.AppData;
import dataprocessors.TSDProcessor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    private RadioButton                  rb2;
    private RadioButton                  rb3;
    private Button                       cb1;
    private Button                       cb2;
    private Button                       cb3;
    private Button                       runButton;

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

        textArea = new TextArea();
        textArea2 = new TextArea();
        displayButton = new Button();
        editToggle = new Button();
        editToggle.setText("Done");
        displayButton.setText(manager.getPropertyValue(DISPLAY_BUTTON.name()));
        checkBox = new CheckBox("Read-Only");
        algorithmTitle = new Text();
        algorithmTitle.setText("Algorithm Type");
        algorithmTitle.setVisible(false);
        /*
        listView = new ListView();
        ObservableList<String> types = FXCollections.observableArrayList(
                "Classification","Clustering");
        listView = new ListView(types);
        listView.setEditable(true);
        listView.setCellFactory(TextFieldListCell.forListView());
        listView.getSelectionModel().selectFirst();

        listView.setMaxHeight(60);
        listView.setVisible(false);
        */
        final ToggleGroup algoTypes = new ToggleGroup();

        tb1 = new ToggleButton("Classification");
        tb1.setToggleGroup(algoTypes);
        tb1.setVisible(false);
        tb1.setDisable(true);

        tb2 = new ToggleButton("Clustering");
        tb2.setToggleGroup(algoTypes);
        tb2.setVisible(false);

        final ToggleGroup algorList = new ToggleGroup();

        rb1 = new RadioButton("A");
        rb1.setToggleGroup(algorList);
//        rb1.setVisible(false);

        rb2 = new RadioButton("B");
        rb2.setToggleGroup(algorList);
//        rb2.setVisible(false);

        rb3 = new RadioButton("C");
        rb3.setToggleGroup(algorList);
//        rb3.setVisible(false);

        Image settingImage = new Image("/gui/icons/settingIcon.png",20,20,true,false);
        Image runImage = new Image("/gui/icons/runIcon.png",20,20,true,false);

        runButton = new Button();
        runButton.setGraphic(new ImageView(runImage));
        runButton.setVisible(false);

        cb1 = new Button();
        cb1.setGraphic(new ImageView(settingImage));
//        cb1.setVisible(false);

        cb2 = new Button();
        cb2.setGraphic(new ImageView(settingImage));
//        cb2.setVisible(false);

        cb3 = new Button();
        cb3.setGraphic(new ImageView(settingImage));
//        cb3.setVisible(false);

        algorithmListPaneV1 = new VBox();
        algorithmListPaneV1.getChildren().add(rb1);
        algorithmListPaneV1.getChildren().add(rb2);
        algorithmListPaneV1.getChildren().add(rb3);
        algorithmListPaneV1.setSpacing(15);

        algorithmListPaneV2 = new VBox();
        algorithmListPaneV2.getChildren().add(cb1);
        algorithmListPaneV2.getChildren().add(cb2);
        algorithmListPaneV2.getChildren().add(cb3);

        algorithmListPaneH = new HBox();
        algorithmListPaneH.getChildren().add(algorithmListPaneV1);
        algorithmListPaneH.getChildren().add(algorithmListPaneV2);
        algorithmListPaneH.setVisible(false);
        algorithmListPaneH.setSpacing(20);

        vPane = new VBox();
        vPane.getChildren().add(title);
        vPane.getChildren().add(textArea);
        vPane.getChildren().add(editToggle);
        vPane.getChildren().add(inputDataText);
        vPane.getChildren().add(algorithmTitle);
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
        displayButton.setOnAction(e -> {
            String s = textArea.getText()+ textArea2.getText();
            ((AppData) applicationTemplate.getDataComponent()).loadData(s);
        });
        editToggle.setOnAction(e -> {
            if(editToggle.getText().equals("Done")){
                try{
                    inputDataText.setText("");
                    processor.processString(textArea.getText());
                    String s = "";
                    s += processor.getInstanceSize() + " instances with " + processor.getUniqueLabels().size() + " labels:" +"\n";
                    for(Object labels : processor.getUniqueLabels()){
                        s += "\t -" + labels.toString() + "\n";
                    }
                    inputDataText.setText(s);
                    editToggle.setText("Edit");
                    tb1.setVisible(true);
                    tb2.setVisible(true);
                    algorithmTitle.setVisible(true);
                    textArea.setDisable(true);
                    System.out.println(((AppData)applicationTemplate.getDataComponent()).getProcessor().twoNonNulls());
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
            }else if(editToggle.getText().equals("Edit")){
                ((AppData)applicationTemplate.getDataComponent()).getProcessor().clear();
                inputDataText.setText("");
                editToggle.setText("Done");
                rb1.setVisible(false);
                rb2.setVisible(false);
                algorithmTitle.setVisible(false);
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
            tb1.setVisible(false);
            tb2.setVisible(false);
            algorithmListPaneH.setVisible(true);
        });
    }

    public VBox getVPane() { return vPane;}

    public TextArea getTextArea(){
        return textArea;
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
}
