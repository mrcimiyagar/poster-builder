package com.one.two.three.poster.back.callbacks;

import com.one.two.three.poster.back.models.PosterPreview;

import java.util.ArrayList;

public interface OnPosterPreviewsFetchedListener {

    void onPosterPreviewsFetched(ArrayList<PosterPreview> posterPreviews);
}