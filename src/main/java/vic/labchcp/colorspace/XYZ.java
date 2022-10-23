package vic.labchcp.colorspace;


public class XYZ {
	/**
	 * Conversion from CIE XYZ to sRGB as defined in the IEC 619602-1 standard
	 * (http://www.colour.org/tc8-05/Docs/colorspace/61966-2-1.pdf)
	 *
	 * @param xyz input CIE XYZ values.
	 * @ouput  rgb output sRGB values in [0, 255] range.
	 */
	public static int[] xyzToRgb(double[] xyz) {
		double x = xyz[0];
		double y = xyz[1];
		double z = xyz[2];

		double r_linear = 3.2404542 * x - 1.5371385 * y - 0.4985314 * z;
		double g_linear = -0.9692660 * x + 1.8760108 * y + 0.0415560 * z;
		double b_linear = +0.0556434 * x - 0.2040259 * y + 1.0572252 * z;

		double r = r_linear > 0.0031308 ? 1.055 * Math.pow(r_linear, (1 / 2.4)) - 0.055 : 12.92 * r_linear;

		double g = g_linear > 0.0031308 ? 1.055 * Math.pow(g_linear, (1 / 2.4)) - 0.055 : 12.92 * g_linear;

		double b = b_linear > 0.0031308 ? 1.055 * Math.pow(b_linear, (1 / 2.4)) - 0.055 : 12.92 * b_linear;

		int[] rgb = new int[3];
		if ((r >= 0 && r <= 1) && (g >= 0 && g <= 1) && (b >= 0 && b <= 1)) {
			rgb[0] = (int) Math.round(r * 255);
			rgb[1] = (int) Math.round(g * 255);
			rgb[2] = (int) Math.round(b * 255);
		} else{
			rgb=new int[]{-1, -1, -1};
		}
		return rgb;
	}

	/**
	 * Conversion from sRGB to CIE XYZ as defined in the IEC 619602-1 standard
	 * (http://www.colour.org/tc8-05/Docs/colorspace/61966-2-1.pdf)
	 *
	 * @param rgb source sRGB values. Size of array <code>rgb</code> must be at
	 *            least 3. If size of array <code>rgb</code> larger than three then
	 *            only first 3 values are used.
	 * @output xyz destinaltion CIE XYZ values. Size of array <code>xyz</code> must
	 *            be at least 3. If size of array <code>xyz</code> larger than three
	 *            then only first 3 values are used.
	 */
	public static double[] rgbToXyz(int[] rgb) {
		double r = rgb[0] / 255.0;
		double g = rgb[1] / 255.0;
		double b = rgb[2] / 255.0;

		double r_linear = r > 0.04045 ? Math.pow((r + 0.055) / 1.055, 2.4) : r / 12.92;

		double g_linear = g > 0.04045 ? Math.pow((g + 0.055) / 1.055, 2.4) : g / 12.92;

		double b_linear = b > 0.04045 ? Math.pow((b + 0.055) / 1.055, 2.4) : b / 12.92;

		double x = 0.4124564 * r_linear + 0.3575761 * g_linear + 0.1804375 * b_linear;
		double y = 0.2126729 * r_linear + 0.7151522 * g_linear + 0.0721750 * b_linear;
		double z = 0.0193339 * r_linear + 0.1191920 * g_linear + 0.9503041 * b_linear;
		;

		double[] xyz = new double[3];
		xyz[0] = (double) x;
		xyz[1] = (double) y;
		xyz[2] = (double) z;

		return xyz;
	}

 	
}