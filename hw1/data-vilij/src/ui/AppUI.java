package ui;

import actions.AppActions;
import dataprocessors.AppData;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;

import static settings.AppPropertyTypes.DISPLAY_BUTTON;
import static settings.AppPropertyTypes.DATA_TITLE;
import static settings.AppPropertyTypes.CHART_TITLE;
import static settings.AppPropertyTypes.SCREENSHOT_TOOLTIP;
import static vilij.settings.PropertyTypes.GUI_RESOURCE_PATH;
import static vilij.settings.PropertyTypes.ICONS_RESOURCE_PATH;
import static settings.AppPropertyTypes.SCREENSHOT_ICON;

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
    private ScatterChart<Number, Number> chart;          // the chart where data will be displayed
    private Button                       displayButton;  // workspace button to display data on the chart
    private TextArea                     textArea;       // text area for new data input
    private boolean                      hasNewText;     // whether or not the text area has any new data since last display
    protected String                     scrnshotPath;   // path to the 'screenshot' icon

    public ScatterChart<Number, Number> getChart() { return chart; }

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
        super.setToolBar(applicationTemplate);
        PropertyManager manager = applicationTemplate.manager;
        scrnshotButton = super.setToolbarButton(scrnshotPath, manager.getPropertyValue(SCREENSHOT_TOOLTIP.name()), true);
        toolBar.getItems().add(scrnshotButton);
    }

    @Override
    protected void setToolbarHandlers(ApplicationTemplate applicationTemplate) {
        applicationTemplate.setActionComponent(new AppActions(applicationTemplate));
        newButton.setOnAction(e -> applicationTemplate.getActionComponent().handleNewRequest());
        saveButton.setOnAction(e -> applicationTemplate.getActionComponent().handleSaveRequest());
        loadButton.setOnAction(e -> applicationTemplate.getActionComponent().handleLoadRequest());
        exitButton.setOnAction(e -> applicationTemplate.getActionComponent().handleExitRequest());
        printButton.setOnAction(e -> applicationTemplate.getActionComponent().handlePrintRequest());
        scrnshotButton.setOnAction(e -> ((AppActions)applicationTemplate.getActionComponent()).handleScreenshotRequest());  //TODO change to screenshot action
    }

    @Override
    public void initialize() {
        layout();
        setWorkspaceActions();
    }

    @Override
    public void clear() {
        // TODO for homework
    }

    private void layout() {
        // TODO for homework 1
        PropertyManager manager = applicationTemplate.manager;
        chart = new ScatterChart<>(new NumberAxis(),new NumberAxis());
        chart.setTitle(manager.getPropertyValue(CHART_TITLE.name()));

        VBox vPane = new VBox();
        Text title = new Text();
        title.setText(manager.getPropertyValue(DATA_TITLE.name()));
        title.setFont(new Font(20));

        textArea = new TextArea();
        displayButton = new Button();
        displayButton.setText(manager.getPropertyValue(DISPLAY_BUTTON.name()));
        vPane.getChildren().add(title);
        vPane.getChildren().add(textArea);
        vPane.getChildren().add(displayButton);
        vPane.setMaxWidth(this.windowWidth/2);

        HBox hPane = new HBox();
        hPane.getChildren().add(vPane);
        hPane.getChildren().add(chart);

        appPane.getChildren().add(hPane);

    }

    private void setWorkspaceActions() {
        // TODO for homework 1
        displayButton.setOnAction(e ->{
            ((AppData) applicationTemplate.getDataComponent()).loadData(textArea.getText());
        });
        textArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
                if(oldValue.isEmpty()){
                    newButton.setDisable(true);
                }else {
                    newButton.setDisable(false);
                }
            }
        });
    }
}
