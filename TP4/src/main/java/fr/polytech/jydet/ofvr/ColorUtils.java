package fr.polytech.jydet.ofvr;

import static java.lang.Math.pow;

public class ColorUtils {
    public static int interpolation(float fraction, int startValue, int endValue) {
        float startA = ((startValue >> 24) & 0xff) / 255.0f;
        float startR = ((startValue >> 16) & 0xff) / 255.0f;
        float startG = ((startValue >> 8) & 0xff) / 255.0f;
        float startB = (startValue & 0xff) / 255.0f;

        float endA = ((endValue >> 24) & 0xff) / 255.0f;
        float endR = ((endValue >> 16) & 0xff) / 255.0f;
        float endG = ((endValue >> 8) & 0xff) / 255.0f;
        float endB = (endValue & 0xff) / 255.0f;

        // convert from sRGB to linear
        startR = EOCF_sRGB(startR);
        startG = EOCF_sRGB(startG);
        startB = EOCF_sRGB(startB);

        endR = EOCF_sRGB(endR);
        endG = EOCF_sRGB(endG);
        endB = EOCF_sRGB(endB);

        // compute the interpolated color in linear space
        float a = startA + fraction * (endA - startA);
        float r = startR + fraction * (endR - startR);
        float g = startG + fraction * (endG - startG);
        float b = startB + fraction * (endB - startB);

        // convert back to sRGB in the [0..255] range
        a = a * 255.0f;
        r = OECF_sRGB(r) * 255.0f;
        g = OECF_sRGB(g) * 255.0f;
        b = OECF_sRGB(b) * 255.0f;

        return Math.round(a) << 24 | Math.round(r) << 16 | Math.round(g) << 8 | Math.round(b);
    }

    /**
     * Opto-electronic conversion function for the sRGB color space
     * Takes a gamma-encoded sRGB value and converts it to a linear sRGB value
     */
    static float OECF_sRGB(float linear) {
        // IEC 61966-2-1:1999
        return linear <= 0.0031308f ?
            linear * 12.92f : (float) ((pow(linear, 1.0f / 2.4f) * 1.055f) - 0.055f);
    }

    /**
     * Electro-optical conversion function for the sRGB color space
     * Takes a linear sRGB value and converts it to a gamma-encoded sRGB value
     */
    static float EOCF_sRGB(float srgb) {
        // IEC 61966-2-1:1999
        return srgb <= 0.04045f ? srgb / 12.92f : (float) pow((srgb + 0.055f) / 1.055f, 2.4f);
    }
}
