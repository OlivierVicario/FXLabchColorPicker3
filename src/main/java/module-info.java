module vic.labchcp.fxlabchcolorpicker {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;

    requires com.fasterxml.jackson.databind;


    opens vic.labchcp.colorpicker to javafx.fxml;
    exports vic.labchcp.colorpicker;
    exports vic.labchcp.model;
}