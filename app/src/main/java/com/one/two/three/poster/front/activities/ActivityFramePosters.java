package com.one.two.three.poster.front.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.one.two.three.poster.R;
import com.one.two.three.poster.back.callbacks.OnPosterPreviewsFetchedListener;
import com.one.two.three.poster.back.core.Core;
import com.one.two.three.poster.back.models.PosterPreview;
import com.one.two.three.poster.back.utils.BazaarHelper;
import com.one.two.three.poster.front.adapters.AdapterPoster;
import com.one.two.three.poster.front.components.DialogNoInternet;
import com.one.two.three.poster.util.IabHelper;
import com.one.two.three.poster.util.IabResult;
import com.one.two.three.poster.util.Inventory;

import java.util.ArrayList;

public class ActivityFramePosters extends BaseActivity {

    private final String TAG = "POUYANNN";

    public static final int REQUEST_CODE_IAB = 801;

    private int frameCount;

    private boolean isLoading;
    private boolean isAllLoaded;
    private int offset = 0;
    private int limit = 24;

    private RecyclerView recyclerView;
    private IabHelper mHelper;

    private AdapterPoster posterAdapter;
    private StaggeredGridLayoutManager gridLayoutManager;
    private LottieAnimationView loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame_posters);

        this.frameCount = getIntent().getExtras().getInt("frame_count");

        this.initToolbar();
        this.initViews();
        this.initDecorations();
        this.initListeners();

        setupIab();

    }

    private void setupIab() {

        try{
            final IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
                public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                    if (result.isFailure()) {
                        Log.d("ActivityFramePosters", "Failed to query inventory: " + result);
                        return;
                    } else {
                        Core.getInstance().setInventory(inventory);
                    }
                }
            };

            if(Core.getInstance().getIabHelper() == null && BazaarHelper.isBazaarInstalled()){
                mHelper = new IabHelper(this, Core.getInstance().getBase64EncodedPublicKey());
                Core.getInstance().setIabHelper(mHelper);
                mHelper = Core.getInstance().getIabHelper();
                mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                    public void onIabSetupFinished(IabResult result) {

                        if (!result.isSuccess()) {
                            Log.d("PouyanNN", "Problem ic_setting up In-app Billing: " + result);
                        }
                        mHelper.queryInventoryAsync(mGotInventoryListener);
                    }
                });
            }else{
                mHelper = Core.getInstance().getIabHelper();
            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.problem_iab), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.fetchDataFromServer();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        View view = LayoutInflater.from(this).inflate(R.layout.custom_action_bar, null, false);
        toolbar.addView(view);
        Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(params);

        TextView txtDownloads = (TextView) view.findViewById(R.id.txtDownloads);
        TextView txtActionbarTitle = (TextView) view.findViewById(R.id.txtActionbarTitle);
        TextView txtBack = (TextView) view.findViewById(R.id.txtHome);
        ImageView imgBack = (ImageView) view.findViewById(R.id.imgHome);

        switch (frameCount) {
            case 0: {
                txtActionbarTitle.setText(getResources().getString(R.string.no_frame_group_title));
                break;
            }
            case 1: {
                txtActionbarTitle.setText(getResources().getString(R.string.one_frame_group_title));
                break;
            }
            case 2: {
                txtActionbarTitle.setText(getResources().getString(R.string.two_frame_group_title));
                break;
            }
            case 3: {
                txtActionbarTitle.setText(getResources().getString(R.string.three_frame_group_title));
                break;
            }
            case 4: {
                txtActionbarTitle.setText(getResources().getString(R.string.four_frame_group_title));
                break;
            }
            case 5: {
                txtActionbarTitle.setText(getResources().getString(R.string.five_frame_group_title));
                break;
            }
        }

        txtActionbarTitle.setTextSize(17);

        txtBack.setText("برگشت");
        imgBack.setImageResource(R.drawable.back);

        txtDownloads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityFramePosters.this, ActivityDownloads.class));
            }
        });
        View.OnClickListener backListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        };
        txtBack.setOnClickListener(backListener);
        imgBack.setOnClickListener(backListener);
    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.activity_frame_posters_recycler_view);
        loadingView = findViewById(R.id.loading_frame);
        setLoadingAnimation(loadingView);
    }

    private void initDecorations() {
        gridLayoutManager = new StaggeredGridLayoutManager(3, GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    private void initListeners() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = gridLayoutManager.getChildCount();
                int totalItemCount = gridLayoutManager.getItemCount();
                int[] pastVisiblesItems = gridLayoutManager.findFirstVisibleItemPositions(null);

                if ((visibleItemCount + pastVisiblesItems[0]) >= totalItemCount * 0.5) {    // fetch on half of list
                    if(isLoading || isAllLoaded)
                        return;
                    isLoading = true;
                    fetchDataFromServer();
                    offset += 24;
                }
            }
        });
    }

    private void fetchDataFromServer() {
        if (Core.getInstance().getNetworkHelper().isNetworkAvailable()) {
            Core.getInstance().getNetworkHelper().fetchPostersData("NA", frameCount + "", "NA", "NA", "[]"
                    , offset + "", limit + "", new OnPosterPreviewsFetchedListener() {
                        @Override
                        public void onPosterPreviewsFetched(ArrayList<PosterPreview> posterPreviews) {
                            if(posterPreviews.size() < 24 || posterPreviews.size() == 0)
                                isAllLoaded = true;
                            if (posterAdapter == null) {
                                posterAdapter = new AdapterPoster(AdapterPoster.BehindActivityName.PosterFramesActivity, posterPreviews,
                                        ActivityFramePosters.this, true);
                                recyclerView.setAdapter(posterAdapter);
                                loadingView.setVisibility(View.INVISIBLE);
                            }
                            else {
                                posterAdapter.addPosters(posterPreviews);
                            }
                            isLoading = false;
                        }
                    });
        } else {
            DialogNoInternet dialogNoInternet = new DialogNoInternet(this);
            dialogNoInternet.show();
        }

    }

    public void setLoadingAnimation(LottieAnimationView animationView){
        LottieComposition lottieComposition = LottieComposition.Factory.fromFileSync(Core.getInstance().getContext(), "Animations/loading.json");
        animationView.setComposition(lottieComposition);
        animationView.loop(true);
        animationView.setScale(2.0f);
        animationView.playAnimation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Core.getInstance().getIabHelper() != null){
            Core.getInstance().getIabHelper().dispose();
            Core.getInstance().setIabHelper(null);
        }
    }

}