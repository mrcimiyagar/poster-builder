package com.one.two.three.poster.back.utils;

import android.content.pm.PackageManager;

import com.one.two.three.poster.back.core.Core;

/**
 * Created by pouyan on 2/7/18.
 */

public class BazaarHelper {

    public static boolean isBazaarInstalled() {
        try {
            Core.getInstance().getPackageManager().getPackageInfo("com.farsitel.bazaar", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}
