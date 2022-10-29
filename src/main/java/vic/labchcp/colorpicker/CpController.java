package vic.labchcp.colorpicker;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import vic.labchcp.colorspace.CIELab;
import vic.labchcp.colorspace.RGB;
import vic.labchcp.colorspace.XYZ;
import vic.labchcp.model.ColorState;
import vic.labchcp.model.Palette;
import vic.labchcp.model.Palettes;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * la gestion et l'édition des items (couleur ou palette) se fait au niveau des containers graphiques qui servent de stokage pertinent.
 * Les objets Palette et Palettes
 * ne sont mis à jour qu'au moment de la sauvegarde et du chargement des palettes dans un fichier JSON
 */


public class CpController implements Initializable {


    public Label labL;
    public Label labA;
    public Label labB;
    public Label labC;
    public Label labH;
    public Canvas canvasL;
    public Slider sliderL;
    public Canvas canvasA;
    public Slider sliderA;
    public Canvas canvasB;
    public Slider sliderB;
    public Canvas canvasC;
    public Slider sliderC;
    public Canvas canvasH;
    public Slider sliderH;
    public HBox hboxColors;
    public Rectangle rectangleCurrentColor;
    public Button btnAddColor;
    public Button btnSavePalette;
    public VBox vboxPalettes;
    public MenuItem miClose;

    double minL = 0.0;
    double maxL = 100.0;
    double minA = -86.18425672918573;
    double maxA = 98.2546681694157;
    double minB = -107.85554792486691;
    double maxB = 94.48676524244688;
    double minC = 0;
    double maxC = 133.809;
    double minH = 0;
    double maxH = 360;

    Palettes palettes;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        sliderL.setMin(minL);
        sliderL.setMax(maxL);
        sliderL.setValue((minL + maxL) / 1.5);

        sliderA.setMin(minA);
        sliderA.setMax(maxA);
        sliderA.setValue(0);

        sliderB.setMin(minB);
        sliderB.setMax(maxB);
        sliderB.setValue(0);

        sliderC.setMin(minC);
        sliderC.setMax(maxC);
        sliderC.setValue(0);

        sliderH.setMin(minH);
        sliderH.setMax(maxH);
        sliderH.setValue(0);


        sliderL.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                updateColorPickerLayout();
            }
        });
        sliderA.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (sliderA.isFocused()) changeSlidersCH();
                updateColorPickerLayout();
            }
        });
        sliderB.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (sliderB.isFocused()) changeSlidersCH();
                updateColorPickerLayout();
            }
        });
        sliderC.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (sliderC.isFocused()) changeSlidersAB();
                updateColorPickerLayout();
            }
        });
        sliderH.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (sliderH.isFocused()) changeSlidersAB();
                updateColorPickerLayout();
            }
        });

        updateColorPickerLayout();

        palettes = new Palettes();

       /* try {
            loadJSONFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
    }

    //********************************** ColorPicker ********************************************
    public void btnAddCurrentColorActionHandler(ActionEvent actionEvent) {
        addColorToHboxColors((Color) rectangleCurrentColor.getFill());
    }

    public void addColorToHboxColors(Color c) {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        Label label = new Label();
        Rectangle rectangle = new Rectangle(50, 50, c);
        rectangle.setStroke(Color.TRANSPARENT);
        rectangle.setStrokeWidth(10.0);

        final ContextMenu cm = new ContextMenu();
        MenuItem cmItem1 = new MenuItem("Copy CSS Color");
        cmItem1.setOnAction((ActionEvent e) -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            //content.putImage(pic.getImage());
            content.putString(((Color) rectangle.getFill()).toString());
            clipboard.setContent(content);
        });

        MenuItem cmItem2 = new MenuItem("Delete");
        cmItem2.setOnAction((ActionEvent e) -> {
            hboxColors.getChildren().remove(vBox);
        });

        MenuItem cmItem3 = new MenuItem("Update");
        cmItem3.setOnAction((ActionEvent e) -> {
            rectangle.setFill(rectangleCurrentColor.getFill());
            ColorState state = new ColorState((Color) rectangleCurrentColor.getFill());
            String s = " " + String.format("%.0f", state.L) + ",\n"
                    + String.format("%.0f", state.A) + ", "
                    + String.format("%.0f", state.B) + ",\n"
                    + String.format("%.0f", state.C) + ","
                    + String.format("%.0f", state.H);
            //label.setText(rectangleCurrentColor.getFill().toString().substring(2, 8));
            label.setText(s);
        });

        cm.getItems().add(cmItem1);
        cm.getItems().add(cmItem2);
        cm.getItems().add(cmItem3);

        rectangle.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
            if (e.getButton() == MouseButton.SECONDARY)
                cm.show(rectangle, e.getScreenX(), e.getScreenY());
            if (e.getButton() == MouseButton.PRIMARY) {
                Color color = (Color) rectangle.getFill();
                updateColorState(color);
            }
        });
        ColorState state = new ColorState((Color) rectangleCurrentColor.getFill());
        label.setWrapText(true);
        label.setAlignment(Pos.CENTER);
        String s = " " + String.format("%.0f", state.L) + ",\n "
                + String.format("%.0f", state.A) + ", "
                + String.format("%.0f", state.B) + ",\n "
                + String.format("%.0f", state.C) + ", "
                + String.format("%.0f", state.H);
        //label.setText(rectangleCurrentColor.getFill().toString().substring(2, 8));
        label.setText(s);
        label.setMaxSize(50, 120);

        vBox.getChildren().addAll(rectangle, label);
        hboxColors.getChildren().add(vBox);
    }

    public void changeSlidersCH() {
        double l = sliderL.getValue();
        double a = sliderA.getValue();
        double bLab = sliderB.getValue();
        double[] lab = {l, a, bLab};
        double[] lch = CIELab.LabToLch(lab);
        if (Math.round(sliderC.getValue()) != Math.round(lch[1])) {
            sliderC.setValue(lch[1]);
        }
        if (Math.round(sliderH.getValue()) != Math.round(lch[2])) {
            sliderH.setValue(lch[2]);
        }
    }

    public void changeSlidersAB() {
        double l = sliderL.getValue();
        double c = sliderC.getValue();
        double h = sliderH.getValue();
        double[] lch = {l, c, h};
        double[] lab = CIELab.LchToLab(lch);
        if (Math.round(sliderA.getValue()) != Math.round(lab[1])) {
            sliderA.setValue(lab[1]);
        }
        if (Math.round(sliderB.getValue()) != Math.round(lab[2])) {
            sliderB.setValue(lab[2]);
        }
    }

    public void updateColorState(Color c) {
        ColorState colorState = new ColorState(c);
        sliderL.setValue(colorState.L);
        sliderA.setValue(colorState.A);
        sliderB.setValue(colorState.B);
        sliderC.setValue(colorState.C);
        sliderH.setValue(colorState.H);
        updateColorPickerLayout();
    }

    private void updateColorPickerLayout() {
        Color bgColor = Color.grayRgb(244);
        GraphicsContext gc = canvasL.getGraphicsContext2D();
        gc.setFill(bgColor);
        gc.fillRect(0, 0, 400, 20);
        gc = canvasA.getGraphicsContext2D();
        gc.setFill(bgColor);
        gc.fillRect(0, 0, 400, 20);
        gc = canvasB.getGraphicsContext2D();
        gc.setFill(bgColor);
        gc.fillRect(0, 0, 400, 20);
        gc = canvasC.getGraphicsContext2D();
        gc.setFill(bgColor);
        gc.fillRect(0, 0, 400, 20);
        gc = canvasH.getGraphicsContext2D();
        gc.setFill(bgColor);
        gc.fillRect(0, 0, 400, 20);

        double l = sliderL.getValue();
        double a = sliderA.getValue();
        double bLab = sliderB.getValue();
        double c = sliderC.getValue();
        double h = sliderH.getValue();
        labL.setText(String.format("%.0f", l));
        labA.setText(String.format("%.0f", a));
        labB.setText(String.format("%.0f", bLab));
        labC.setText(String.format("%.0f", c));
        labH.setText(String.format("%.0f", h));
        for (double trackL = minL; trackL < maxL; trackL += 5) {
            double[] lab = {trackL, a, bLab};
            int[] rgb = XYZ.xyzToRgb(CIELab.LabToXyz(lab));
            Color color;
            if (rgb[0] != -1) {
                int lum = (int) Math.round(rgb[0] * 0.2126 + rgb[1] * 0.7152 + rgb[2] * 0.0722);
                color = Color.rgb(lum, lum, lum);
            } else {
                color = Color.TRANSPARENT;
            }
            gc = canvasL.getGraphicsContext2D();
            gc.setFill(color);
            double x = map(trackL, minL, maxL, 0, 400);
            gc.fillRect(x - 10, 0, 20, 20);
        }
        for (double trackA = minA; trackA < maxA; trackA += 5) {
            double[] lab = {l, trackA, bLab};
            int[] rgb = XYZ.xyzToRgb(CIELab.LabToXyz(lab));
            Color color;
            if (rgb[0] != -1) {
                color = Color.rgb(rgb[0], rgb[1], rgb[2]);
            } else {
                color = Color.TRANSPARENT;
            }
            gc = canvasA.getGraphicsContext2D();
            gc.setFill(color);
            double x = map(trackA, minA, maxA, 0, 400);
            gc.fillRect(x - 10, 0, 20, 20);
        }
        for (double trackB = minB; trackB < maxB; trackB += 5) {
            double[] lab = {l, a, trackB};
            int[] rgb = XYZ.xyzToRgb(CIELab.LabToXyz(lab));
            Color color;
            if (rgb[0] != -1) {
                color = Color.rgb(rgb[0], rgb[1], rgb[2]);
            } else {
                color = Color.TRANSPARENT;
            }
            gc = canvasB.getGraphicsContext2D();
            gc.setFill(color);
            double x = map(trackB, minB, maxB, 0, 400);
            gc.fillRect(x - 10, 0, 20, 20);
        }

        for (double trackC = minC; trackC < maxC; trackC += 5) {
            double[] lch = {l, trackC, h};
            int[] rgb = XYZ.xyzToRgb(CIELab.LabToXyz(CIELab.LchToLab(lch)));
            Color color;
            if (rgb[0] != -1) {
                color = Color.rgb(rgb[0], rgb[1], rgb[2]);
            } else {
                color = Color.TRANSPARENT;
            }
            gc = canvasC.getGraphicsContext2D();
            gc.setFill(color);
            double x = map(trackC, minC, maxC, 0, 400);
            gc.fillRect(x - 10, 0, 20, 20);
        }

        for (double trackH = minH; trackH < maxH; trackH += 5) {
            double[] lch = {l, c, trackH};
            int[] rgb = XYZ.xyzToRgb(CIELab.LabToXyz(CIELab.LchToLab(lch)));
            Color color;
            if (rgb[0] != -1) {
                color = Color.rgb(rgb[0], rgb[1], rgb[2]);
            } else {
                color = Color.TRANSPARENT;
            }
            gc = canvasH.getGraphicsContext2D();
            gc.setFill(color);
            double x = map(trackH, minH, maxH, 0, 400);
            gc.fillRect(x, 0, 20, 20);
        }
        double[] lab = {l, a, bLab};
        int[] rgb = XYZ.xyzToRgb(CIELab.LabToXyz(lab));
        Color color;
        if (rgb[0] != -1) {
            color = Color.rgb(rgb[0], rgb[1], rgb[2]);
        } else {
            color = Color.TRANSPARENT;
        }
        rectangleCurrentColor.setFill(color);
    }

    private double map(double x0, double start0, double end0, double start1, double end1) {
        double k = (x0 - start0) / (end0 - start0);
        double x1 = start1 + k * (end1 - start1);
        return x1;
    }

    //********************************** Palette **********************************************
    public void btnSavePaletteActionHandler(ActionEvent actionEvent) {
        Palette palette = new Palette(hboxColors);
        displayPaletteInVboxPalettes(palette);
    }

    public void displayPaletteInVboxPalettes(Palette palette) {
        HBox hboxPalette = new HBox();
        hboxPalette.setStyle("-fx-border-color: #bbb");

        for (int i = 0; i < palette.rgbs.size(); i++) {
            Rectangle rectangle = new Rectangle(50, 50, RGB.rgbToFxcolor(palette.rgbs.get(i)));
            rectangle.setStroke(Color.TRANSPARENT);
            rectangle.setStrokeWidth(10.0);
            hboxPalette.getChildren().add(rectangle);
        }

        final ContextMenu cm = new ContextMenu();
        MenuItem cmItem1 = new MenuItem("Copy CSS Color");
        cmItem1.setOnAction((ActionEvent e) -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            //content.putImage(pic.getImage());
            //content.putString(((Color) rectangle.getFill()).toString());
            clipboard.setContent(content);
        });

        MenuItem cmItem2 = new MenuItem("Delete");
        cmItem2.setOnAction((ActionEvent e) -> {
            vboxPalettes.getChildren().remove(hboxPalette);
        });

        MenuItem cmItem3 = new MenuItem("Update");
        cmItem3.setOnAction((ActionEvent e) -> {

        });

        cm.getItems().add(cmItem1);
        cm.getItems().add(cmItem2);
        cm.getItems().add(cmItem3);

        hboxPalette.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
            if (e.getButton() == MouseButton.SECONDARY)
                cm.show(hboxPalette, e.getScreenX(), e.getScreenY());
            if (e.getButton() == MouseButton.PRIMARY) {
                hboxColors.getChildren().clear();
                for (int i = 0; i < hboxPalette.getChildren().size(); i++) {
                    Rectangle rectangle = (Rectangle) hboxPalette.getChildren().get(i);
                    Color color = (Color) rectangle.getFill();
                    addColorToHboxColors(color);
                }
            }
        });

        vboxPalettes.getChildren().add(hboxPalette);

    }

    //********************************** Fichier **********************************************

    private void saveJSONFile() throws IOException {
        palettes.items.clear();
        for (int i = 0; i < vboxPalettes.getChildren().size(); i++) {
            HBox hbox = (HBox) vboxPalettes.getChildren().get(i);
            Palette palette = new Palette();
            for (int j = 0; j < hbox.getChildren().size(); j++) {
                Rectangle rectangle = (Rectangle) hbox.getChildren().get(j);
                Color color = (Color) rectangle.getFill();
                palette.rgbs.add(RGB.fxColorToRgb(color));
            }
            palettes.items.add(palette);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(new File("palettes.json"), palettes);
    }

    private void loadJSONFile() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        palettes = objectMapper.readValue(new File("palettes.json"), Palettes.class);
        for (Palette p : palettes.items) {
            displayPaletteInVboxPalettes(p);
        }
    }

    public void miCloseActionHandler(ActionEvent actionEvent) throws IOException {
        saveJSONFile();
        Platform.exit();
    }
}