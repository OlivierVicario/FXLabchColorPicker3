package vic.labchcp.colorspace;

import static vic.labchcp.colorspace.RGB.hexToRgb;
import static vic.labchcp.colorspace.RGB.rgbToHex;
import static vic.labchcp.colorspace.XYZ.rgbToXyz;
import static vic.labchcp.colorspace.XYZ.xyzToRgb;

public class JzAzBz {

	public static final double b = 1.15;
	public static final double g = 0.66;
	public static final double c1 = 3424 / Math.pow(2, 12);
	public static final double c2 = 2413 / Math.pow(2, 7);
	public static final double c3 = 2392 / Math.pow(2, 7);
	public static final double n = 2610 / Math.pow(2, 14);
	public static final double p = 1.7 * 2523 / Math.pow(2, 5);
	public static final double d = -0.56;
	public static final double d0 = 1.6295499532821566 * Math.pow(10, -11);

	
	public static double[] jabToJch(double[] jab) {
		double var_H = Math.atan2(jab[2], jab[1]); // Quadrant by signs
		if (var_H > 0) {
			var_H = (var_H / Math.PI) * 180;
		} else {
			var_H = 360 - (Math.abs(var_H) / Math.PI) * 180;
		}

		double[] jch = new double[3];
		jch[0] = jab[0];
		jch[1] = (double) Math.sqrt(Math.pow(jab[1], 2) + Math.pow(jab[2], 2));
		jch[2] = (double) var_H;
		return jch;
	}

	public static double[] jchToJab(double[] jch) {
		double[] jab = new double[3];
		jab[0] = jch[0];
		jab[1] = (double) Math.cos((jch[2] * Math.PI / 180.0)) * jch[1];
		jab[2] = (double) Math.sin((jch[2] * Math.PI / 180.0)) * jch[1];
		return jab;
	}

	public static double[] xyzToJab(double[] xyz) {
		double[] XYZp = new double[3];
		XYZp[0] = b * xyz[0] - ((b - 1) * xyz[2]);
		XYZp[1] = g * xyz[1] - ((g - 1) * xyz[0]);
		XYZp[2] = xyz[2];

		double[] LMS = new double[3];
		LMS[0] = 0.41478972 * XYZp[0] + 0.579999 * XYZp[1] + 0.0146480 * XYZp[2];
		LMS[1] = -0.2015100 * XYZp[0] + 1.120649 * XYZp[1] + 0.0531008 * XYZp[2];
		LMS[2] = -0.0166008 * XYZp[0] + 0.264800 * XYZp[1] + 0.6684799 * XYZp[2];

		double[] LMSp = new double[3];
		for (int i = 0; i < 3; i++) {
			LMSp[i] = Math.pow((c1 + c2 * Math.pow((LMS[i] / 10000.0), n)) / (1 + c3 * Math.pow((LMS[i] / 10000.0), n)),
					p);
		}

		double[] Iab = new double[3];
		Iab[0] = 0.5 * LMSp[0] + 0.5 * LMSp[1];
		Iab[1] = 3.524000 * LMSp[0] - 4.066708 * LMSp[1] + 0.542708 * LMSp[2];
		Iab[2] = 0.199076 * LMSp[0] + 1.096799 * LMSp[1] - 1.295875 * LMSp[2];

		double[] jab = new double[3];
		jab[0] = (((1 + d) * Iab[0]) / (1 + d * Iab[0])) - d0;
		jab[1] = Iab[1];
		jab[2] = Iab[2];

		return jab;
	}

	public static double[] jabToXyz(double[] jab) {

		double[] Iab = new double[3];
		Iab[0] = (jab[0] + d0) / (1 + d - d * (jab[0] + d0));
		Iab[1] = jab[1];
		Iab[2] = jab[2];

		double[] LMSp = new double[3];
		LMSp[0] = 1.0 * Iab[0] + 0.138605043271539 * Iab[1] + 0.058047316156119 * Iab[2];
		LMSp[1] = 1.0 * Iab[0] - 0.138605043271539 * Iab[1] - 0.058047316156119 * Iab[2];
		LMSp[2] = 1.0 * Iab[0] - 0.096019242026319 * Iab[1] - 0.811891896056039 * Iab[2];
		double[] LMS = new double[3];
		for (int i = 0; i < 3; i++) {
			LMS[i] = 10000 * Math.pow((c1 - Math.pow(LMSp[i], 1 / p)) / ((c3 * Math.pow(LMSp[i], 1 / p)) - c2), 1 / n);
		}
		double[] XYZp = new double[3];
		XYZp[0] = 1.924226435787607 * LMS[0] - 1.004792312595365 * LMS[1] + 0.037651404030618 * LMS[2];
		XYZp[1] = 0.350316762094999 * LMS[0] + 0.726481193931655 * LMS[1] - 0.065384422948085 * LMS[2];
		XYZp[2] = -0.090982810982848 * LMS[0] - 0.312728290523074 * LMS[1] + 1.522766561305260 * LMS[2];

		double[] xyz = new double[3];
		xyz[0] = (XYZp[0] + (b - 1) * XYZp[2]) / b;
		xyz[1] = (XYZp[1] + (g - 1) * xyz[0]) / g;// corrigé
		xyz[2] = XYZp[2];
		return xyz;
	}

	public static double[] rgbToJch(int[] rgb) {// rgb 0..255
		double[] jch = new double[3];
		jch = jabToJch(xyzToJab(rgbToXyz(rgb)));
		return jch;
	}

	public static double[] rgbToJab(int[] rgb) {// rgb 0..255
		double[] jab = new double[3];
		jab = xyzToJab(rgbToXyz(rgb));
		return jab;
	}

	public static int[] jabToRgb(double[] jab) {
		int[] rgb = new int[3];
		rgb = xyzToRgb(jabToXyz(jab));

		if (rgb[0] >= 0 && rgb[0] <= 255 && rgb[1] >= 0 && rgb[1] <= 255 && rgb[2] >= 0 && rgb[2] <= 255) {
			return rgb;
		} else {
			return null;
		}
	}

	public static int[] jchToRgb(double[] jch) {
		int[] rgb = new int[3];
		rgb = (xyzToRgb(jabToXyz(jchToJab(jch))));
		if (rgb[0] >= 0 && rgb[0] <= 255 && rgb[1] >= 0 && rgb[1] <= 255 && rgb[2] >= 0 && rgb[2] <= 255) {
			return rgb;
		} else {
			return null;
		}
	}

	public static double[] stringToJch(String hex) {
		return rgbToJch(hexToRgb(hex));
	}

	public static double[] stringToJab(String hex) {
		return rgbToJab(hexToRgb(hex));
	}

	public static String jchToString(double[] jch) {
		return rgbToHex(jchToRgb(jch));
	}

	public static String jabToString(double[] jab) {
		return rgbToHex(jabToRgb(jab));
	}
	

}
