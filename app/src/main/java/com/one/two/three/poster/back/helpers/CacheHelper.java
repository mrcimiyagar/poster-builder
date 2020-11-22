package com.one.two.three.poster.back.helpers;

import com.one.two.three.poster.back.core.Core;

import java.io.File;
import java.util.HashSet;

public class CacheHelper {

    private final HashSet<String> cachedBluePrints = new HashSet<>();
    public HashSet<String> getCachedBluePrints() {
        if (this.cachedBluePrints.size() == 0) {
            loadBluePrints();
        }
        return this.cachedBluePrints;
    }

    public CacheHelper() {

    }

    public void cacheBluePrint(String posterId) {
        synchronized (this.cachedBluePrints) {
            if (!this.cachedBluePrints.contains(posterId)) {
                this.cachedBluePrints.add(posterId);
            }
        }
    }

    private void loadBluePrints() {
        File bpStorageDir = new File(Core.getInstance().DOWNLOAD_DIR_PATH);
        bpStorageDir.mkdirs();
        File[] blueprintsDirs = bpStorageDir.listFiles();
        for (File blueprintDir : blueprintsDirs) {
            if (blueprintDir.exists() && blueprintDir.isDirectory()) {
                if (new File(blueprintDir.getPath() + File.separator + "DataFile.pki").exists()) {
                    String posterId = blueprintDir.getName();
                    cachedBluePrints.add(posterId);
                }
            }
        }
    }
}