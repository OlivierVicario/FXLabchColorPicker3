package vic.labchcp.model;

import javafx.scene.paint.Color;
import vic.labchcp.colorspace.CIELab;
import vic.labchcp.colorspace.RGB;

public class ColorState {
    public double L;
    public double A;
    public double B;
    public double C;
    public double H;

    public ColorState() {

    }

    public ColorState(double L, double A, double B, double C, double H) {
        this.L = L;
        this.A = A;
        this.B = B;
        this.C = C;
        this.H = H;
    }

    public ColorState(Color c){
        int[] rgb = RGB.fxColorToRgb(c);
        double[] lab = CIELab.rgbToLab(rgb);
        double[] lch = CIELab.LabToLch(lab);
        this.L = lab[0];
        this.A = lab[1];
        this.B = lab[2];
        this.C = lch[1];
        this.H = lch[2];
    }
}
