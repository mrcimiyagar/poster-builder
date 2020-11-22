package com.one.two.three.poster.back.helpers;

import android.app.Activity;
import android.graphics.Point;

import com.one.two.three.poster.back.core.Core;

/**
 * Created by Pouyan-PC on 12/28/2017.
 */

public class GraphicHelper {

    private static final GraphicHelper ourInstance = new GraphicHelper();

    public static GraphicHelper getInstance() {
        return ourInstance;
    }

    private GraphicHelper() {}

    public int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }

    public int getScreenHeight(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.y;
    }

    public int dpToPixels(float dpNum) {
        return (int) (Core.getInstance().getResources().getDisplayMetrics().density * dpNum);
    }

}
