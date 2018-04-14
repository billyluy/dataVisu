package settings;

/**
 * This enumerable type lists the various application-specific property types listed in the initial set of properties to
 * be loaded from the workspace properties <code>xml</code> file specified by the initialization parameters.
 *
 * @author Ritwik Banerjee
 * @see vilij.settings.InitializationParams
 */
public enum AppPropertyTypes {

    /* resource files and folders */
    DATA_RESOURCE_PATH,
    CSS_PATH,
    RUN_PATH,
    SETTING_PATH,
    APPLICATION_TITLE,
    CHECK_TITLE,
    ALGOR1,
    ALGOR2,
    DONE,
    EDIT,
    METADATA1,
    METADATA2,
    METADATA3,
    ALGORTITLE,
    INFO1,
    INFO2,
    INFO3,
    INFO4,

    /* user interface icon file names */
    SCREENSHOT_ICON,

    /* tooltips for user interface buttons */
    SCREENSHOT_TOOLTIP,
    DISPLAY_BUTTON,
    CHART_TITLE,
    SAVE_TITLE,
    PNG_EXT_DESC,
    PNG_EXT,

    /* error messages */
    RESOURCE_SUBDIR_NOT_FOUND,
    ERROR_TITLE,
    INVALID_ERROR,
    ERROR_LINE,
    DUPE_LINE,
    LENGTH1,
    LENGTH2,

    /* application-specific message titles */
    SAVE_UNSAVED_WORK_TITLE,

    /* application-specific messages */
    SAVE_UNSAVED_WORK,

    /* application-specific parameters */
    DATA_FILE_EXT,
    DATA_FILE_EXT_DESC,
    TEXT_AREA,
    SPECIFIED_FILE
}
