package ui;

import actions.AppActions;
import dataprocessors.AppData;
import javafx.stage.Stage;
import vilij.templates.ApplicationTemplate;

import static vilij.settings.InitializationParams.*;


/**
 * The main class from which the application is run. The various components used here must be concrete implementations
 * of types defined in {@link vilij.components}.
 *
 * @author Ritwik Banerjee
 */
public final class DataVisualizer extends ApplicationTemplate {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.show();
        dialogsAudit(primaryStage); //sets up error messages for the stage
        if (propertyAudit()) {// propertyAudit() returns true explore method why does this return true
            System.out.println("ran");
            userInterfaceAudit(primaryStage);//main error comes from here NEXT STEP
        }
    }

    @Override
    protected boolean propertyAudit() {
        boolean failed = (manager == null) || (!(loadProperties(PROPERTIES_XML) && loadProperties(WORKSPACE_PROPERTIES_XML)));
        if (failed)
            errorDialog.show(LOAD_ERROR_TITLE.getParameterName(), PROPERTIES_LOAD_ERROR_MESSAGE.getParameterName());
        return !failed;
    }

    @Override
    protected void userInterfaceAudit(Stage primaryStage) {
        setUIComponent(new AppUI(primaryStage, this));
        setActionComponent(new AppActions(this));
        setDataComponent(new AppData(this));
        //is there a missing componet?
        uiComponent.initialize(); //what does this method do?
    }

}
