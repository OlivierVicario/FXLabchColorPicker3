package vic.labchcp.model;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import vic.labchcp.colorspace.RGB;

import java.util.ArrayList;

public class Palette {
   //public ArrayList<Color> colors;
    public ArrayList<int[]> rgbs;

    public ArrayList<int[]> getRgbs() {
        return rgbs;
    }

    public void setRgbs(ArrayList<int[]> rgbs) {
        this.rgbs = rgbs;
    }

    public Palette(){
        //this.colors = new ArrayList<Color>();
        this.rgbs = new ArrayList<int[]>();
    }

    public Palette(HBox hbox){
        //this.colors = new ArrayList<Color>();
        this.rgbs = new ArrayList<int[]>();
        for(int i=0;i<hbox.getChildren().size();i++){
            VBox vbox =(VBox) hbox.getChildren().get(i);
            Rectangle rectangle = (Rectangle)vbox.getChildren().get(0);
            Color color = (Color) rectangle.getFill();
            //this.colors.add(color);
            this.rgbs.add(RGB.fxColorToRgb(color));
        }
    }
}
