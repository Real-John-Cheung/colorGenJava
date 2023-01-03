/**
 * some color generation functions
 * @author John C
 * @version 1.0
 * @since 2023-1-3
 */

package colorgen;

public class ColorGen {
    /**
     * get a random RGB color
     * 
     * @return an array of int [R, G, B] in range of 0 - 255
     */
    public static int[] randomRGB() {
        return new int[] { (int) Math.floor(Math.random() * 256), (int) Math.floor(Math.random() * 256),
                (int) Math.floor(Math.random() * 256) };
    }

    /**
     * caculate the weighted Euclidean distance of two RGB colors
     * 
     * @param color1 int array representing the first color, [R, G, B] range from 0
     *               - 255;
     * @param color2 int array representing the second color, [R, G, B] range from 0
     *               - 255;
     * @return float, the distance between two color
     */
    public static float colorDistance(int[] color1, int[] color2) {
        float rmean = ((float) color1[0] + (float) color2[0]) / 2;
        float dr = (float) color1[0] - (float) color2[0];
        float dg = (float) color1[1] - (float) color2[1];
        float db = (float) color1[2] - (float) color2[2];
        return (float) Math
                .sqrt((double) (((512 + rmean) * dr * dr) / 256) + 4 * dg * dg + (((767 - rmean) * db * db) / 256));
    }

    /**
     * generate random offset color based on a chosen color
     * 
     * @param baseColor int array representing the chosen color, [R, G, B] range
     *                  from 0 - 255
     * @param offset    float the max offset
     * @return int array representing the generated color, [R, G, B] range from 0 -
     *         255
     */
    public static int[] randomOffsetRGB(int[] baseColor, float offset) {
        float mean = ((float) baseColor[0] + (float) baseColor[1] + (float) baseColor[2]) / 3;
        float newMean = mean + 2 * (float) Math.random() * offset - offset;
        float offsetRatio = newMean / mean;
        return new int[] { (int) Math.floor(baseColor[0] * offsetRatio), (int) Math.floor(baseColor[0] * offsetRatio),
                (int) Math.floor(baseColor[0] * offsetRatio) };
    }

    /**
     * Cover HSB color (0-1 range) to RGB color (0-255), this is based on the
     * function in java.awt.Color
     * 
     * @param hue float, 0-1
     * @param sat float, 0-1
     * @param bri float, 0-1
     * @return int array representing the corresponding RGB color
     */
    public static int[] HSBtoRGB(float hue, float sat, float bri) {
        if (sat == 0)
            return new int[] { (int) Math.round(bri * 255), (int) Math.round(bri * 255), (int) Math.round(bri * 255) };
        if (sat < 0 || sat > 1 || bri < 0 || bri > 1)
            throw new ColorException("Bad HSB Value");
        hue = hue - (float) Math.floor(hue); // hue to 0-1 if not
        int i = (int) (6 * hue);
        float f = 6 * hue - i;
        float p = bri * (1 - sat);
        float q = bri * (1 - sat * f);
        float t = bri * (1 - sat * (1 - f));
        switch (i) {
            case 0:
                return floatToIntRGB(bri, t, p);
            case 1:
                return floatToIntRGB(q, bri, p);
            case 2:
                return floatToIntRGB(p, bri, t);
            case 3:
                return floatToIntRGB(p, q, bri);
            case 4:
                return floatToIntRGB(t, p, bri);
            case 5:
                return floatToIntRGB(bri, p, q);
            default:
                throw new ColorException("impossible to convert hsb to rgb");
        }
    }

    /**
     * convert RGB (0-255) to HSB (0-1), base on the function in java.awt.Color
     * 
     * @param red   int, between 0 - 255
     * @param green int, between 0 - 255
     * @param blue  int, between 0 - 255
     * @return a float array representing the HSB color
     */
    public static float[] RGBtoHSB(int red, int green, int blue) {
        float[] array = new float[3];
        int min;
        int max;
        if (red < green) {
            min = red;
            max = green;
        } else {
            min = green;
            max = red;
        }
        if (blue > max) {
            max = blue;
        } else if (blue < min) {
            min = blue;
        }
        array[2] = max / 255f;
        if (max == 0) {
            array[1] = 0;
        } else {
            array[1] = ((float) (max - min)) / ((float) max);
        }
        if (array[1] == 0) {
            array[0] = 0;
        } else {
            float delta = (max - min) * 6;
            if (red == max) {
                array[0] = (green - blue) / delta;
            } else if (green == max) {
                array[0] = 1f / 3 + (blue - red) / delta;
            } else {
                array[0] = 2f / 3 + (red - green) / delta;
            }
            if (array[0] < 0)
                array[0]++;
        }
        return array;
    }

    public static int[][] gradientRGB(int n, float from, float to, String type) {
        return gradientRGB(n, from, to, type, 1, 1);
    }

    /**
     * generate a bunch of color in gradient, take different type
     * 
     * @param n    the number of color return
     * @param from the starting point of the gradient (0 - 1)
     * @param to   the end point of the gradient (0 - 1)
     * @param type how the colors are picked. "UR": Uniform Random, randomly select
     *             values between 0 and 1, and map this to the gradient to select
     *             colours. "G": Grid, uniform sections, no two colours will be
     *             closer to each other(along the gradient) than 1/n. "JG": Jittered
     *             Grid, grid with random offset. "GR" Golden Ratio, using golden
     *             ratio.
     * @param sat  optional, satuation value between 0 - 1, defalut to 1
     * @param bri  optional, brightness value between 0 - 1, defalut to 1
     * @return an array of rbg colors
     */
    public static int[][] gradientRGB(int n, float from, float to, String type, float sat, float bri) {
        int[][] array = new int[n][3];
        if (from < 0 || from > 1 || to < 0 || to > 1)
            throw new ColorException("bad gradient range");
        if (from > to) {
            float tem = from;
            from = to;
            to = tem;
        }
        float del = to - from;
        switch (type) {
            default:
                throw new ColorException("unknown gradiendRGB type");
            case "UR":
                for (int i = 0; i < array.length; i++) {
                    float h = from + (float) Math.random() * del;
                    array[i] = HSBtoRGB(h, sat, bri);
                }
                return array;
            case "G":
                for (int i = 0; i < array.length; i++) {
                    array[i] = HSBtoRGB(from + i * (del / n), sat, bri);
                }
                return array;
            case "JG":
                for (int i = 0; i < array.length; i++) {
                    float maxJitter = 0.5f;
                    float h = from + i * ((2 * (float) Math.random() - 1) * maxJitter) * del / n;
                    array[i] = HSBtoRGB(h, sat, bri);
                }
                return array;
            case "GR":
                float offset = (float) Math.random();
                for (int i = 0; i < array.length; i++) {
                    float h = from + (offset + (0.618033988749895f * i) % 1) * del / n;
                    array[i] = HSBtoRGB(h, sat, bri);
                }
                return array;
        }
    }

    public static int[][] standerHarmonyColorRGB(int n, float range1, float range2, float range3) {
        return standerHarmonyColorRGB(n, range1, range2, range3, (float) Math.random(), 1, 1, 0, 0);
    }

    /**
     * Generate a bunch of color according to the stander harmony color scheme, see
     * http://doi.acm.org/10.1145/1179352.1141933
     * 
     * @param n         number of the colors
     * @param range1    float, range angle 1 in the hue ring
     * @param range2    float, range angle 2 in the hue ring
     * @param range3    float, range angle 3 in the hue ring
     * @param reference optional, float, as the reference hue for generation,
     *                  default is random
     * @param sat       optional, float (0 - 1), sat value, default to 1
     * @param bri       optional, float (0 - 1), bri value, default to 1
     * @param offset1   optional, float, offset amount 1, default 0, must between 0
     *                  - 1
     * @param offset2   optional, float, offset amount 2, default 0, must between 0
     *                  - 1
     * @return an array of color in RGB format
     */
    public static int[][] standerHarmonyColorRGB(int n, float range1, float range2, float range3, float reference,
            float sat, float bri, float offset1, float offset2) {
        int[][] array = new int[n][3];
        for (int i = 0; i < array.length; i++) {
            float randA = (float) Math.random() * (range1 + range2 + range3);
            if (randA < range1) {
                randA -= range1 / 2;
            } else if (randA >= range1 && randA < range1 + range2) {
                randA += offset1 - range2;
            } else {
                randA += offset2 - range3;
            }
            float h = randA + reference;
            while (h < 0)
                h++;
            h = h % 1f;
            array[i] = HSBtoRGB(h, sat, bri);
        }
        return array;
    }

    public static int[][] analogousColorRGB(int n, float range) {
        return analogousColorRGB(n, range, (float) Math.random(), 1, 1, 0, 0);
    }

    /**
     * Generate a bunch of color according to the analogous color scheme
     * 
     * @param n       number of colors
     * @param range   range of color
     * @param ref     optional, reference angle, default is random
     * @param sat     optional, float, 0 - 1, default to 1
     * @param bri     optional, float, 0 - 1, default to 1
     * @param offset1 optional, float, offset amount 1, default 0, must between 0 -
     *                1
     * @param offset2 optional, float, offset amount 2, default 0, must between 0 -
     *                1
     * @return an array of colors in RGB format
     */
    public static int[][] analogousColorRGB(int n, float range, float ref, float sat, float bri, float offset1,
            float offset2) {
        return standerHarmonyColorRGB(n, range, 0, 0, ref, sat, bri, offset1, offset2);
    }

    public static int[][] complementaryColorRGB(int n, float range1, float range2) {
        return complementaryColorRGB(n, range1, range2, (float) Math.random(), 1, 1, 0);
    }

    /**
     * Generate a bunch of color according to the complementary color scheme
     * 
     * @param n       number of colors
     * @param range1  range1
     * @param range2  range2
     * @param ref     optional, reference angle, default is random
     * @param sat     optional, float, 0 - 1, default to 1
     * @param bri     optional, float, 0 - 1, default to 1
     * @param offset2 optional, float, offset amount 2, default 0, must between 0 -
     *                1
     * @return an array of colors in RGB format
     */
    public static int[][] complementaryColorRGB(int n, float range1, float range2, float ref, float sat, float bri,
            float offset2) {
        return standerHarmonyColorRGB(n, range1, range2, 0, ref, sat, bri, 0.5f, offset2);
    }

    public static int[][] splitcomplementaryColorRGB(int n, float range1, float range2, float range3, float var) {
        return splitcomplementaryColorRGB(n, range1, range2, range3, var, (float) Math.random(), 1, 1);
    }

    /**
     * Generate a bunch of color according to the split complementary color scheme
     * 
     * @param n      number of colors
     * @param range1 range1
     * @param range2 range2, must smaller than 2*var
     * @param range3 range3, must smaller than 2*var
     * @param var    variation from 180 for the offset values
     * @param ref    optional, reference angle, default is random
     * @param sat    optional, float, 0 - 1, default to 1
     * @param bri    optional, float, 0 - 1, default to 1
     * @return an array of colors in RGB format
     */
    public static int[][] splitcomplementaryColorRGB(int n, float range1, float range2, float range3, float var,
            float ref, float sat, float bri) {
        if (range2 >= 2 * var || range3 >= 2 * var)
            throw new ColorException("bad ranges for split complementary scheme");
        return standerHarmonyColorRGB(n, range1, range2, range3, ref, sat, bri, 180f - var, 180f + var);
    }

    public static int[][] triadColorRGB(int n, float range1, float range2, float range3) {
        return triadColorRGB(n, range1, range2, range3, (float) Math.random(), 1, 1);
    }

    /**
     * Generate a bunch of color according to the traid color scheme
     * 
     * @param n      number of colors
     * @param range1 float, range1
     * @param range2 float, range2
     * @param range3 float, range3
     * @param ref    optional, reference angle, default is random
     * @param sat    optional, float, 0 - 1, default to 1
     * @param bri    optional, float, 0 - 1, default to 1
     * @return an array of colors in RGB format
     */
    public static int[][] triadColorRGB(int n, float range1, float range2, float range3, float ref, float sat,
            float bri) {
        return standerHarmonyColorRGB(n, range1, range2, range3, ref, sat, bri, 0.33333f, 0.66667f);
    }

    /**
     * mix 3 color to get a new one, use the greyControl to control the grey value (0:low - 1:high)
     * 
     * @param color1 first color to mix
     * @param color2 second color to mix
     * @param color3 third color to mix
     * @param greyControl 0-1, how much grey get mix in
     * @return a color in RGB format
     */
    public static int[] triadMixingRGB(int[] color1, int[] color2, int[] color3, float greyControl) {
        int randomIdx = (int) Math.floor(Math.random() * 3);
        float mixRatio1 = randomIdx == 0 ? (float) Math.random() * greyControl : (float) Math.random();
        float mixRatio2 = randomIdx == 1 ? (float) Math.random() * greyControl : (float) Math.random();
        float mixRatio3 = randomIdx == 2 ? (float) Math.random() * greyControl : (float) Math.random();
        float sum = mixRatio1 + mixRatio2 + mixRatio3;
        mixRatio1 /= sum;
        mixRatio2 /= sum;
        mixRatio3 /= sum;
        return new int[] { Math.round(mixRatio1 * color1[0] + mixRatio2 * color2[0] + mixRatio3 * color3[0]),
                Math.round(mixRatio1 * color1[1] + mixRatio2 * color2[1] + mixRatio3 * color3[1]),
                Math.round(mixRatio1 * color1[2] + mixRatio2 * color2[2] + mixRatio3 * color3[2]) };
    }

    private static int[] floatToIntRGB(float r, float g, float b) {
        return new int[] { Math.round(r), Math.round(g), Math.round(b) };
    }

}

class ColorException extends RuntimeException {
    public ColorException(String message) {
        super(message);
    }
}
