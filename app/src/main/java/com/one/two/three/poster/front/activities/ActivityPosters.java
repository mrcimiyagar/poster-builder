package com.one.two.three.poster.front.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class ActivityPosters extends BaseActivity {

    public static final int REQUEST_CODE_IAB = 801;

    private TextView[] moreBTNS;
    private RecyclerView[] recyclerViews;
    private LottieAnimationView[] animationsViews;

    private IabHelper mHelper;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posters);

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
                        Log.d("ActivityPosters", "Failed to query inventory: " + result);
                        return;
                    } else {
                        Core.getInstance().setInventory(inventory);
                    }
                }
            };

            if(Core.getInstance().getIabHelper() == null && BazaarHelper.isBazaarInstalled()){
                mHelper = new IabHelper(this, Core.getInstance().getBase64EncodedPublicKey());
                Core.getInstance().setIabHelper(mHelper);
                mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                    public void onIabSetupFinished(IabResult result) {
                        if (!result.isSuccess()) {
                            Log.d("ActivityPosters", "Problem ic_setting up In-app Billing: " + result);
                        }
                        mHelper.queryInventoryAsync(false, mGotInventoryListener);
                    }
                });
            }else{
                if(BazaarHelper.isBazaarInstalled()){
                    mHelper = Core.getInstance().getIabHelper();
                }else{
                    mHelper = null;
                }
            }

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.problem_iab), Toast.LENGTH_SHORT).show();
        }

    }

    private void initToolbar() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        View view = LayoutInflater.from(this).inflate(R.layout.custom_action_bar, null, false);
        toolbar.addView(view);
        Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(params);

        TextView txtDownloads = view.findViewById(R.id.txtDownloads);
        TextView txtHome = view.findViewById(R.id.txtHome);
        TextView txtActionbarTitle = view.findViewById(R.id.txtActionbarTitle);
        ImageView imgHome = view.findViewById(R.id.imgHome);

        txtActionbarTitle.setText(getResources().getString(R.string.posters_title));
        txtActionbarTitle.setTextSize(17);

        txtDownloads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityPosters.this, ActivityDownloads.class));
            }
        });
        View.OnClickListener homeListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityPosters.this, ActivityHome.class));
                ActivityPosters.this.finish();
            }
        };
        txtHome.setOnClickListener(homeListener);
        imgHome.setOnClickListener(homeListener);
    }

    private void initViews() {

        animationsViews = new LottieAnimationView[6];

        animationsViews[0] = findViewById(R.id.loading_0_frame);
        animationsViews[1] = findViewById(R.id.loading_1_frame);
        animationsViews[2] = findViewById(R.id.loading_2_frame);
        animationsViews[3] = findViewById(R.id.loading_3_frame);
        animationsViews[4] = findViewById(R.id.loading_4_frame);
        animationsViews[5] = findViewById(R.id.loading_5_frame);
        for(int i=0; i<6; i++)
            setLoadingAnimation(animationsViews[i]);

        recyclerViews = new RecyclerView[6];

        recyclerViews[0] = findViewById(R.id.activity_posters_0_frames_recycler_view);
        recyclerViews[1] = findViewById(R.id.activity_posters_1_frames_recycler_view);
        recyclerViews[2] = findViewById(R.id.activity_posters_2_frames_recycler_view);
        recyclerViews[3] = findViewById(R.id.activity_posters_3_frames_recycler_view);
        recyclerViews[4] = findViewById(R.id.activity_posters_4_frames_recycler_view);
        recyclerViews[5] = findViewById(R.id.activity_posters_5_frames_recycler_view);

        moreBTNS = new TextView[6];

        moreBTNS[0] = findViewById(R.id.activity_posters_more_text_view_0);
        moreBTNS[1] = findViewById(R.id.activity_posters_more_text_view_1);
        moreBTNS[2] = findViewById(R.id.activity_posters_more_text_view_2);
        moreBTNS[3] = findViewById(R.id.activity_posters_more_text_view_3);
        moreBTNS[4] = findViewById(R.id.activity_posters_more_text_view_4);
        moreBTNS[5] = findViewById(R.id.activity_posters_more_text_view_5);
    }

    public void setLoadingAnimation(LottieAnimationView animationView){
        LottieComposition lottieComposition = LottieComposition.Factory.fromFileSync(Core.getInstance().getContext(), "Animations/loading.json");
        animationView.setComposition(lottieComposition);
        animationView.loop(true);
        animationView.setScale(2.0f);
        animationView.playAnimation();
    }

    private void initDecorations() {

        for (RecyclerView recyclerView : recyclerViews) {
            recyclerView.setLayoutManager(new LinearLayoutManager(ActivityPosters.this, LinearLayoutManager.HORIZONTAL, true));
        }
    }

    private void initListeners() {

        for (int counter = 0; counter < moreBTNS.length; counter++) {
            final int btnIndex = counter;
            moreBTNS[counter].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ActivityPosters.this, ActivityFramePosters.class);
                    intent.putExtra("frame_count", btnIndex);
                    startActivity(intent);
                }
            });
        }
    }

    private void fetchPostersFromServer() {

        if (Core.getInstance().getNetworkHelper().isNetworkAvailable()) {
            for (int counter = 0; counter < 6; counter++) {
                final int index = counter;

                Core.getInstance().getNetworkHelper().fetchPostersData("NA", index + "", "NA", "NA", "[]", "0", "10", new OnPosterPreviewsFetchedListener() {
                    @Override
                    public void onPosterPreviewsFetched(ArrayList<PosterPreview> posterPreviews) {
                        AdapterPoster posterAdapter = new AdapterPoster(AdapterPoster.BehindActivityName.PostersActivity, posterPreviews, ActivityPosters.this, false);
                        animationsViews[index].setVisibility(View.INVISIBLE);
                        recyclerViews[index].setAdapter(posterAdapter);
                    }
                });
            }
        }
        else {
            DialogNoInternet dialogNoInternet = new DialogNoInternet(this);
            dialogNoInternet.show();
        }
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
    protected void onResume() {
        super.onResume();
        this.fetchPostersFromServer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (Core.getInstance().getIabHelper() != null)
                Core.getInstance().getIabHelper().dispose();
            Core.getInstance().setIabHelper(null);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, ActivityHome.class));
        this.finish();
    }

}