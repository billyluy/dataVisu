package dataprocessors;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ui.AppUI;
import vilij.templates.ApplicationTemplate;

public class configurationClassificationWindow {

    ApplicationTemplate applicationTemplate;

    private  Stage configStage;
    private  Button doneButton;
    private int maxIterations;
    private int updateInterval;
    private boolean isContinuous;
    private TextArea maxIterationsTextArea;
    private TextArea updateIntervalTextArea;
    private CheckBox continuousRunCheckBox;
    private Button buttonPressed;

    public  void showConfig(Button bp, int maxInt, int upInt, boolean continuous){
        buttonPressed = bp;
        configStage = new Stage();
        VBox vBox1 = new VBox();
        VBox vBox2 = new VBox();
        HBox hBox = new HBox();

        Text title = new Text("Algorithm Run Configuration");
        Text info = new Text();
        info.setText("Max Iterations :" +"\n\n" + "Update Intervals :" + "\n\n\n" + "Continuous Run? : " + "\n");
        doneButton = new Button("Done");
        vBox1.getChildren().add(info);
        vBox1.getChildren().add(doneButton);

        maxIterationsTextArea = new TextArea();
        maxIterationsTextArea.setMaxSize(100,5);
        maxIterationsTextArea.setText(Integer.toString(maxInt));
        updateIntervalTextArea = new TextArea();
        updateIntervalTextArea.setMaxSize(100,5);
        updateIntervalTextArea.setText(Integer.toString(upInt));
        continuousRunCheckBox = new CheckBox();
        continuousRunCheckBox.selectedProperty().setValue(continuous);
        vBox2.getChildren().addAll(maxIterationsTextArea, updateIntervalTextArea, continuousRunCheckBox);

//        hBox.getChildren().add(title);
        hBox.getChildren().add(vBox1);
        hBox.getChildren().add(vBox2);

        Scene stageScene = new Scene(hBox, 500, 300);
        configStage.setScene(stageScene);
        setActions();
        configStage.show();
    }

    public void setActions(){
        configStage.addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String s = maxIterationsTextArea.getText().trim();
                if((!s.equals(""))){
                    if(Integer.parseInt(maxIterationsTextArea.getText())<1){
                        maxIterationsTextArea.setText("1");
                    }
                }
                String l = updateIntervalTextArea.getText().trim();
                if((!l.equals(""))){
                    if(Integer.parseInt(updateIntervalTextArea.getText())<1){
                        updateIntervalTextArea.setText("1");
                    }
                }
            }
        });
        maxIterationsTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                maxIterationsTextArea.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        updateIntervalTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                updateIntervalTextArea.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        doneButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                try{
                    maxIterations = Integer.parseInt(maxIterationsTextArea.getText());
                    updateInterval = Integer.parseInt(updateIntervalTextArea.getText());
                    isContinuous = continuousRunCheckBox.isSelected();
                    configStage.close();
                }catch (Exception el){
                    maxIterations = 1;
                    updateInterval = 1;
                    isContinuous = continuousRunCheckBox.isSelected();
                    configStage.close();
                }
            }
        });
    }

    public int getMaxIterations(){
        return maxIterations;
    }

    public int getUpdateInterval(){
        return updateInterval;
    }

    public boolean getContinuous(){
        return isContinuous;
    }
}
