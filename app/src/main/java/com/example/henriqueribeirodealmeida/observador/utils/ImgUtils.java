package com.example.henriqueribeirodealmeida.observador.utils;

import android.graphics.Bitmap;

public class ImgUtils {

    //Return a Bitmap scaled to fit a max width and a max height, does nothing if image is already smaller
    //The result image keeps the proportion of the original image
    //maxW is the maximum width the result image should have after scaled
    //maxH is the maximum height the result image should have after scaled
    //btm is the Bitmap object that the result image will be scaled from
    public static Bitmap scaleBitmap(int maxW, int maxH, Bitmap btm) {
        int imgW = btm.getWidth();
        int imgH = btm.getHeight();
        float horizontalProportion = (float) maxW / imgW;
        float verticalProportion = (float) maxH / imgH;
        float finalProportion = 1;
        if (horizontalProportion < verticalProportion) {
            if (horizontalProportion < 1) finalProportion = horizontalProportion;
        } else {
            if (verticalProportion < 1) finalProportion = verticalProportion;
        }

        return Bitmap.createScaledBitmap(btm, (int) (imgW * finalProportion), (int) (imgH * finalProportion), false);
    }

}
