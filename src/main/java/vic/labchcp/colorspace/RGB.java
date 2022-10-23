package vic.labchcp.colorspace;

import javafx.scene.paint.Color;

public class RGB {

    public static String rgbToHex(int[] rgb) {
        return String.format("#%02X%02X%02X", rgb[0], rgb[1], rgb[2]);
    }

    public static int[] hexToRgb(String hex) {
        int[] rgb = new int[3];
        rgb[0] = Integer.valueOf(hex.substring(1, 3), 16);
        rgb[1] = Integer.valueOf(hex.substring(3, 5), 16);
        rgb[2] = Integer.valueOf(hex.substring(5, 7), 16);
        return rgb;
    }

    public static int[] fxColorToRgb(Color c) {
        int[] rgb = new int[3];
        rgb[0] = (int) Math.round(c.getRed() * 255);
        rgb[1] = (int) Math.round(c.getGreen() * 255);
        rgb[2] = (int) Math.round(c.getBlue() * 255);
        return rgb;
    }

    public static Color rgbToFxcolor(int[] rgb) {
        return Color.rgb(rgb[0],rgb[1],rgb[2]);
    }
}
