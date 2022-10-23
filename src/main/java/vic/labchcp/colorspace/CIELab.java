package vic.labchcp.colorspace;

import static vic.labchcp.colorspace.RGB.hexToRgb;
import static vic.labchcp.colorspace.RGB.rgbToHex;
import static vic.labchcp.colorspace.XYZ.rgbToXyz;
import static vic.labchcp.colorspace.XYZ.xyzToRgb;

/*
 * Basic color space conversion utilities, assuming two degree observer and
 * illuminant D65.
 *
 * @author Jarek Sacha
 * @version $Revision: 1.7 $
 */
public final class CIELab {

	private static final double X_D65 = 0.950467;
	private static final double Y_D65 = 1.0000;
	private static final double Z_D65 = 1.088969;
	private static final double CIE_EPSILON = 0.008856;
	private static final double CIE_KAPPA = 903.3;
	private static final double CIE_KAPPA_EPSILON = CIE_EPSILON * CIE_KAPPA;

	/**
	 * Conversion from CIE L*a*b* to CIE XYZ assuming Observer. = 2, Illuminant =
	 * D65. Conversion based on formulas provided at
	 * http://www.brucelindbloom.com/index.html?Eqn_RGB_XYZ_Matrix.html
	 *
	 * @param lab input CIE L*a*b* values.
	 * @output xyz output CIE XYZ values.
	 */
	public static double[] LabToXyz(final double[] lab) {

		final double l = lab[0];
		final double a = lab[1];
		final double b = lab[2];

		final double yr = l > CIE_KAPPA_EPSILON ? Math.pow((l + 16) / 116, 3) : l / CIE_KAPPA;

		final double fy = yr > CIE_EPSILON ? (l + 16) / 116.0 : (CIE_KAPPA * yr + 16) / 116.0;

		final double fx = a / 500 + fy;
		final double fx3 = fx * fx * fx;

		final double xr = fx3 > CIE_EPSILON ? fx3 : (116 * fx - 16) / CIE_KAPPA;

		final double fz = fy - b / 200.0;
		final double fz3 = fz * fz * fz;
		final double zr = fz3 > CIE_EPSILON ? fz3 : (116 * fz - 16) / CIE_KAPPA;

		double[] xyz = new double[3];
		xyz[0] = (xr * X_D65);
		xyz[1] = (yr * Y_D65);
		xyz[2] = (zr * Z_D65);

		return xyz;
	}

	/**
	 * /** Conversion from CIE XYZ to CIE L*a*b* assuming Observer. = 2, Illuminant
	 * = D65. Conversion based on formulas provided at
	 * http://www.brucelindbloom.com/index.html?Eqn_RGB_XYZ_Matrix.html
	 *
	 * @param xyz source CIE XYZ values. Size of array <code>xyz</code> must be at
	 *            least 3. If size of array <code>xyz</code> larger than three then
	 *            only first 3 values are used.
	 * @output lab destinaltion CIE L*a*b* values. Size of array <code>lab</code>
	 *            must be at least 3. If size of array <code>lab</code> larger than
	 *            three then only first 3 values are used.
	 */
	public static double[] xyzToLab(final double[] xyz) {
		final double xr = xyz[0] / X_D65;
		final double yr = xyz[1] / Y_D65;
		final double zr = xyz[2] / Z_D65;

		final double fx = xr > CIE_EPSILON ? Math.pow(xr, 1.0 / 3.0) : (CIE_KAPPA * xr + 16) / 116.0;

		final double fy = yr > CIE_EPSILON ? Math.pow(yr, 1.0 / 3.0) : (CIE_KAPPA * yr + 16) / 116.0;

		final double fz = zr > CIE_EPSILON ? Math.pow(zr, 1.0 / 3.0) : (CIE_KAPPA * zr + 16) / 116.0;

		double[] lab = new double[3];
		lab[0] = (float) (116 * fy - 16);
		lab[1] = (float) (500 * (fx - fy));
		lab[2] = (float) (200 * (fy - fz));

		return lab;
	}

	// CIE-L*ab �> CIE-L*CH�
	public static double[] LabToLch(final double[] lab) {
		double var_H = Math.atan2(lab[2], lab[1]); // Quadrant by signs
		if (var_H > 0) {
			var_H = (var_H / Math.PI) * 180;
		} else {
			var_H = 360 - (Math.abs(var_H) / Math.PI) * 180;
		}
		double[] lch = new double[3];
		lch[0] = lab[0];
		lch[1] = (float) Math.sqrt(Math.pow(lab[1], 2) + Math.pow(lab[2], 2));
		lch[2] = (float) var_H;
		return lch;
	}

	// CIE-L*CH� �>CIE-L*ab//CIE-H� from 0 to 360�
	public static double[] LchToLab(final double[] lch) {
		double[] lab = new double[3];
		lab[0] = lch[0];
		lab[1] = Math.cos((lch[2] * Math.PI / 180.0)) * lch[1];
		lab[2] = Math.sin((lch[2] * Math.PI / 180.0)) * lch[1];
		return lab;
	}

	public static double[] rgbToLch(int[] rgb) {// rgb 0..255
		double[] Lch = new double[3];
		Lch = LabToLch(xyzToLab(rgbToXyz(rgb)));
		return Lch;
	}

	public static double[] rgbToLab(int[] rgb) {// rgb 0..255
		double[] Lab = new double[3];
		Lab = xyzToLab(rgbToXyz(rgb));
		return Lab;
	}

	public static int[] LabToRgb(double[] Lab) {
		int[] rgb = new int[3];
		rgb = xyzToRgb(LabToXyz(Lab));


		return rgb ;
	}

	public static int[] LchToRgb(double[] Lch) {
		int[] rgb = new int[3];
		rgb = (xyzToRgb(LabToXyz(LchToLab(Lch))));

		return rgb ;
	}

	public static double[] stringToLch(String hex) {
		return rgbToLch(hexToRgb(hex));
	}

	public static double[] stringToLab(String hex) {
		return rgbToLab(hexToRgb(hex));
	}

	public static String LchToString(double[] Lch) {
		return rgbToHex(LchToRgb(Lch));
	}

	public static String LabToString(double[] Lab) {
		return rgbToHex(LabToRgb(Lab));
	}
}
