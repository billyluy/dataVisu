package ui;

import actions.AppActions;
import dataprocessors.AppData;
import dataprocessors.TSDProcessor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
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
        /*
        super.setToolBar(applicationTemplate);
        PropertyManager manager = applicationTemplate.manager;
        scrnshotButton = super.setToolbarButton(scrnshotPath, manager.getPropertyValue(SCREENSHOT_TOOLTIP.name()), true);
        toolBar.getItems().add(scrnshotButton);
        */
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

        vPane = new VBox();
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
        vPane.getChildren().add(title);
        vPane.getChildren().add(textArea);
        vPane.getChildren().add(editToggle);
        vPane.getChildren().add(inputDataText);
//        vPane.getChildren().add(displayButton);
//        vPane.getChildren().add(checkBox);
        vPane.setVisible(false);

        hPane = new HBox();
        hPane.getChildren().add(vPane);
        hPane.getChildren().add(chart);

        appPane.getChildren().add(hPane);
    }

    private void setWorkspaceActions() {
        // TODO for homework 1
        TSDProcessor processor = new TSDProcessor();
        PropertyManager manager = applicationTemplate.manager;
        displayButton.setOnAction(e -> {
            String s = textArea.getText()+ textArea2.getText();
            ((AppData) applicationTemplate.getDataComponent()).loadData(s);
        });
        editToggle.setOnAction(e -> {
            if(editToggle.getText().equals("Done")){
                try{
                    processor.processString(textArea.getText());
                    editToggle.setText("Edit");
                    textArea.setDisable(true);
                }catch (Exception el) {
                if (processor.getLineOfDupe() != -1) {
                    (applicationTemplate.getDialog(Dialog.DialogType.ERROR)).show(manager.getPropertyValue(ERROR_TITLE.name()), manager.getPropertyValue(DUPE_LINE.name())+processor.getLineOfDupe() +processor.getDupeName());
                }else if(!processor.getErrorArray().isEmpty()){
                    (applicationTemplate.getDialog(Dialog.DialogType.ERROR)).show(manager.getPropertyValue(ERROR_TITLE.name()), manager.getPropertyValue(ERROR_LINE.name()) + Integer.toString(processor.getErrorArray().get(0)));
                }else{
                    (applicationTemplate.getDialog(Dialog.DialogType.ERROR)).show(manager.getPropertyValue(ERROR_TITLE.name()), manager.getPropertyValue(RESOURCE_SUBDIR_NOT_FOUND.name()));
                }
            }
            }else if(editToggle.getText().equals("Edit")){
                editToggle.setText("Done");
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
//                newButton.setDisable(true);
                saveButton.setDisable(true);
            }else {
//                newButton.setDisable(false);
                saveButton.setDisable(false);
            }
        });
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            hasNewText = newValue;
            textArea.setDisable(hasNewText);
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
}
