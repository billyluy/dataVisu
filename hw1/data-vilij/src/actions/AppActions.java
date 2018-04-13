package actions;

import dataprocessors.AppData;
import dataprocessors.TSDProcessor;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import ui.AppUI;
import vilij.components.ActionComponent;
import vilij.components.ConfirmationDialog;
import vilij.components.ConfirmationDialog.Option;
import vilij.components.Dialog;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static settings.AppPropertyTypes.*;

/**
 * This is the concrete implementation of the action handlers required by the application.
 *
 * @author Ritwik Banerjee
 */
public final class AppActions implements ActionComponent {

    /** The application to which this class of actions belongs. */
    private ApplicationTemplate applicationTemplate;

    /** Path to the data file currently active. */
    Path dataFilePath;

    public AppActions(ApplicationTemplate applicationTemplate) {
        this.applicationTemplate = applicationTemplate;
        dataFilePath = null;
    }

    @Override
    public void handleNewRequest() {
        // TODO for homework 1
        PropertyManager manager = applicationTemplate.manager;
        /*
        try {
            if(promptToSave()){
                ((AppUI)applicationTemplate.getUIComponent()).getChart().getData().clear();
                applicationTemplate.getUIComponent().clear();
                ((AppUI) applicationTemplate.getUIComponent()).getTextArea2().clear();
                ((AppUI) applicationTemplate.getUIComponent()).getTextArea().clear();
                ((AppUI)applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(true);
                dataFilePath = null;
            }
        } catch (IOException e) {
            (applicationTemplate.getDialog(Dialog.DialogType.ERROR)).show(manager.getPropertyValue(ERROR_TITLE.name()), manager.getPropertyValue(RESOURCE_SUBDIR_NOT_FOUND.name()));
        }
        */
        ((AppUI)applicationTemplate.getUIComponent()).getChart().getData().clear();
        applicationTemplate.getUIComponent().clear();
        ((AppData)applicationTemplate.getDataComponent()).getProcessor().clear();
        ((AppUI) applicationTemplate.getUIComponent()).getTextArea2().clear();
        ((AppUI) applicationTemplate.getUIComponent()).getTextArea().clear();
        ((AppUI) applicationTemplate.getUIComponent()).getInputDataText().setText("");
        ((AppUI)applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(true);
        ((AppUI) applicationTemplate.getUIComponent()).getTextArea().setDisable(false);
        ((AppUI) applicationTemplate.getUIComponent()).getVPane().setVisible(true);
        ((AppUI) applicationTemplate.getUIComponent()).getEditToggle().setVisible(true);
        ((AppUI) applicationTemplate.getUIComponent()).getAlgorithimTitle().setVisible(false);
        ((AppUI) applicationTemplate.getUIComponent()).getTb1().setVisible(false);
        ((AppUI) applicationTemplate.getUIComponent()).getTb2().setVisible(false);
        ((AppUI) applicationTemplate.getUIComponent()).getTb2().setSelected(false);
        ((AppUI) applicationTemplate.getUIComponent()).getTb1().setSelected(false);
        ((AppUI) applicationTemplate.getUIComponent()).getTb2().setSelected(false);


        dataFilePath = null;

    }

    @Override
    public void handleSaveRequest() {
        // TODO: NOT A PART OF HW 1
        TSDProcessor processor = new TSDProcessor();
        PropertyManager manager = applicationTemplate.manager;
        String text = ((AppUI) applicationTemplate.getUIComponent()).getTextArea().getText() + ((AppUI) applicationTemplate.getUIComponent()).getTextArea2().getText();
        try {
            processor.processString(text);
            if (dataFilePath == null) {
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(manager.getPropertyValue(DATA_FILE_EXT_DESC.name()), manager.getPropertyValue(DATA_FILE_EXT.name()));
                FileChooser fc = new FileChooser();
                fc.getExtensionFilters().add(extFilter);
                fc.setInitialDirectory(new File(manager.getPropertyValue(DATA_RESOURCE_PATH.name())));
                fc.getInitialDirectory();
                fc.setTitle(manager.getPropertyValue(SAVE_TITLE.name()));

                File file = fc.showSaveDialog((applicationTemplate).getUIComponent().getPrimaryWindow());
                dataFilePath = file.toPath();

                ((AppData) applicationTemplate.getDataComponent()).saveData(dataFilePath);
                ((AppUI) applicationTemplate.getUIComponent()).getSaveButton().setDisable(true);
            } else {
                ((AppData) applicationTemplate.getDataComponent()).saveData(dataFilePath);
                ((AppUI) applicationTemplate.getUIComponent()).getSaveButton().setDisable(true);
            }
        } catch (Exception e) {
            if (processor.getLineOfDupe() != -1) {
                (applicationTemplate.getDialog(Dialog.DialogType.ERROR)).show(manager.getPropertyValue(ERROR_TITLE.name()), manager.getPropertyValue(DUPE_LINE.name())+processor.getLineOfDupe()+processor.getDupeName());
            }else if(!processor.getErrorArray().isEmpty()){
                (applicationTemplate.getDialog(Dialog.DialogType.ERROR)).show(manager.getPropertyValue(ERROR_TITLE.name()), manager.getPropertyValue(ERROR_LINE.name()) + Integer.toString(processor.getErrorArray().get(0)));
            }else{
                (applicationTemplate.getDialog(Dialog.DialogType.ERROR)).show(manager.getPropertyValue(ERROR_TITLE.name()), manager.getPropertyValue(RESOURCE_SUBDIR_NOT_FOUND.name()));
            }
        }
    }

    @Override
    public void handleLoadRequest() {
        // TODO: NOT A PART OF HW 1
        PropertyManager manager = applicationTemplate.manager;
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(manager.getPropertyValue(DATA_FILE_EXT_DESC.name()), manager.getPropertyValue(DATA_FILE_EXT.name()));
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.getInitialDirectory();
        File file = fileChooser.showOpenDialog((applicationTemplate).getUIComponent().getPrimaryWindow());
        try{
            dataFilePath = file.toPath();
            applicationTemplate.getDataComponent().loadData(file.toPath());
            ((AppUI)applicationTemplate.getUIComponent()).getEditToggle().setVisible(false);
            ((AppUI)applicationTemplate.getUIComponent()).getSaveButton().setDisable(true);
        } catch (NullPointerException e){
            (applicationTemplate.getDialog(Dialog.DialogType.ERROR)).show(manager.getPropertyValue(SPECIFIED_FILE.name()), manager.getPropertyValue(RESOURCE_SUBDIR_NOT_FOUND.name()));
        }

    }

    @Override
    public void handleExitRequest() {
        // TODO for homework 1
        Platform.exit();
    }

    @Override
    public void handlePrintRequest() {
        // TODO: NOT A PART OF HW 1
    }

    public void handleScreenshotRequest() throws IOException {
        // TODO: NOT A PART OF HW 1
        PropertyManager manager = applicationTemplate.manager;
        WritableImage image = ((AppUI)applicationTemplate.getUIComponent()).getChart().snapshot(new SnapshotParameters(), null);
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(manager.getPropertyValue(PNG_EXT_DESC.name()), manager.getPropertyValue(PNG_EXT.name()));
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog((applicationTemplate).getUIComponent().getPrimaryWindow());
        try {
            if(file != null){
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            }
        } catch (IOException e) {
            (applicationTemplate.getDialog(Dialog.DialogType.ERROR)).show(manager.getPropertyValue(SPECIFIED_FILE.name()), manager.getPropertyValue(RESOURCE_SUBDIR_NOT_FOUND.name()));
        }
    }

    /**
     * This helper method verifies that the user really wants to save their unsaved work, which they might not want to
     * do. The user will be presented with three options:
     * <ol>
     * <li><code>yes</code>, indicating that the user wants to save the work and continue with the action,</li>
     * <li><code>no</code>, indicating that the user wants to continue with the action without saving the work, and</li>
     * <li><code>cancel</code>, to indicate that the user does not want to continue with the action, but also does not
     * want to save the work at this point.</li>
     * </ol>
     *
     * @return <code>false</code> if the user presses the <i>cancel</i>, and <code>true</code> otherwise.
     */
    private boolean promptToSave() throws IOException {
        // TODO for homework 1
        // TODO remove the placeholder line below after you have implemented this method
        PropertyManager manager = applicationTemplate.manager;
        TSDProcessor processor = new TSDProcessor();
        String text = ((AppUI) applicationTemplate.getUIComponent()).getTextArea().getText() + ((AppUI) applicationTemplate.getUIComponent()).getTextArea2().getText();
        applicationTemplate.getDialog(Dialog.DialogType.CONFIRMATION).show(manager.getPropertyValue(SAVE_UNSAVED_WORK_TITLE.name()), manager.getPropertyValue(SAVE_UNSAVED_WORK.name()));
        if(((ConfirmationDialog)applicationTemplate.getDialog(Dialog.DialogType.CONFIRMATION)).getSelectedOption().equals(Option.YES)){
            try {
                processor.processString(text);
                if (dataFilePath == null) {
                    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(manager.getPropertyValue(DATA_FILE_EXT_DESC.name()), manager.getPropertyValue(DATA_FILE_EXT.name()));
                    FileChooser fc = new FileChooser();
                    fc.getExtensionFilters().add(extFilter);
                    fc.setInitialDirectory(new File(manager.getPropertyValue(DATA_RESOURCE_PATH.name())));
                    fc.getInitialDirectory();
                    fc.setTitle(manager.getPropertyValue(SAVE_TITLE.name()));

                    File file = fc.showSaveDialog((applicationTemplate).getUIComponent().getPrimaryWindow());
                    dataFilePath = file.toPath();

                    ((AppData) applicationTemplate.getDataComponent()).saveData(dataFilePath);
                    ((AppUI) applicationTemplate.getUIComponent()).getSaveButton().setDisable(true);
                } else {
                    ((AppData) applicationTemplate.getDataComponent()).saveData(dataFilePath);
                    ((AppUI) applicationTemplate.getUIComponent()).getSaveButton().setDisable(true);
                }
                return true;
            } catch (Exception e) {
                if (processor.getLineOfDupe() != -1) {
                    (applicationTemplate.getDialog(Dialog.DialogType.ERROR)).show(manager.getPropertyValue(ERROR_TITLE.name()), manager.getPropertyValue(DUPE_LINE.name())+processor.getLineOfDupe() + processor.getDupeName());
                }else if(!processor.getErrorArray().isEmpty()){
                    (applicationTemplate.getDialog(Dialog.DialogType.ERROR)).show(manager.getPropertyValue(ERROR_TITLE.name()), manager.getPropertyValue(ERROR_LINE.name()) + Integer.toString(processor.getErrorArray().get(0)));
                }else{
                    (applicationTemplate.getDialog(Dialog.DialogType.ERROR)).show(manager.getPropertyValue(ERROR_TITLE.name()), manager.getPropertyValue(RESOURCE_SUBDIR_NOT_FOUND.name()));
                }
                return false;
            }
        }
        if(((ConfirmationDialog)applicationTemplate.getDialog(Dialog.DialogType.CONFIRMATION)).getSelectedOption().equals(Option.NO)){
            return true;
        }else{
            return false;
        }
    }
}
