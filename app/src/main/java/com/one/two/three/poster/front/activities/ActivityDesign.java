package com.one.two.three.poster.front.activities;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.one.two.three.poster.R;
import com.one.two.three.poster.back.callbacks.OnPermissionGrantListener;
import com.one.two.three.poster.back.callbacks.OnVolumeProgressListener;
import com.one.two.three.poster.back.core.Core;
import com.one.two.three.poster.back.helpers.GraphicHelper;
import com.one.two.three.poster.back.models.BackFrame;
import com.one.two.three.poster.back.models.BaseFrame;
import com.one.two.three.poster.back.models.Poster;
import com.one.two.three.poster.back.models.ImgFrame;
import com.one.two.three.poster.back.models.TextStyle;
import com.one.two.three.poster.back.utils.DataDecoder;
import com.one.two.three.poster.back.utils.PosterExporter;
import com.one.two.three.poster.front.behaviours.ControllerView;
import com.one.two.three.poster.front.components.CollageImageView;
import com.one.two.three.poster.front.components.DialogAddLogo;
import com.one.two.three.poster.front.components.DialogAddText;
import com.one.two.three.poster.front.components.DialogExitDesign;
import com.one.two.three.poster.front.components.DialogSavePoster;
import com.one.two.three.poster.front.components.DialogSelectFont;
import com.one.two.three.poster.front.components.DialogTextStyle;
import com.one.two.three.poster.front.components.FloatingLogoView;
import com.one.two.three.poster.front.components.FloatingMenu;
import com.one.two.three.poster.front.components.FloatingProgressView;
import com.one.two.three.poster.front.components.FloatingTextView;
import com.one.two.three.poster.front.components.MoveTextView;
import com.one.two.three.poster.front.components.ResizeClipView;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;


public class ActivityDesign extends BaseActivity {

    private final String TAG = "ActivityDesign";
    private final int FLOATING_TEXT_INITIAL_SIZE = 16;

    private RelativeLayout posterContainer;
    private ImageView backgroundIV;
    private RelativeLayout frameContainer;
    private RelativeLayout imagesBTN;
    private RelativeLayout textsBTN;
    private RelativeLayout logoBTN;
    private RelativeLayout loadingLayout;

    private int framesCount;
    private ArrayList<FloatingTextView> textFrames;
    private ArrayList<CollageImageView> imageFrames;
    private ArrayList<RelativeLayout> posterIVCovers;
    private ArrayList<FloatingLogoView> logoFrames;

    private LinearLayout imgControlPanel;
    private ImageButton galleryBTN;
    private ImageButton rotateBTN;
    private ImageButton flipBTN;

    private String posterId;

    private ImageView textFrameDeleteBtn;
    private ImageView textFrameLockBtn;

    private ImageView logoFrameDeleteBtn;
    private ResizeClipView logoFrameResizeBtn;

    private enum DesignModes { IMAGE, TEXT, LOGO }

    private DesignModes designMode;

    private boolean anyWorks = false;

    private FloatingMenu visibleFloatingMenu;
    private ControllerView visibleControllerView;

    private FloatingTextView focusedTextFrame;
    private CollageImageView focusedImgFrame;
    private FloatingLogoView focusedLogoFrame;

    private BackFrame backFrame;

    private float logoFrameTouchX;
    private float logoFrameTouchY;
    private float logoFrameStartWidth;
    private float logoFrameStartHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_design);

        initToolbar();
        initView();
        initListeners();

        this.textFrames = new ArrayList<>();
        this.imageFrames = new ArrayList<>();
        this.posterIVCovers = new ArrayList<>();
        this.logoFrames = new ArrayList<>();

        posterId = getIntent().getExtras().getString("poster_id");

        startLoadingPoster();

        designMode = DesignModes.IMAGE;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == 1) {
                if (designMode == DesignModes.IMAGE) {
                    Bitmap image = getBitmapFromUri(data.getData());
                    focusedImgFrame.setImage(image, image.getWidth(), image.getHeight());
                    openImageFrameControlPanel(focusedImgFrame);
                    anyWorks = true;
                } else if (designMode == DesignModes.LOGO) {
                    Bitmap image = getBitmapFromUri(data.getData());
                    final FloatingLogoView floatingLogoView = new FloatingLogoView(
                            ActivityDesign.this,
                            frameContainer.getMeasuredWidth(),
                            frameContainer.getMeasuredHeight());
                    floatingLogoView.setLayoutParams(new ViewGroup.LayoutParams(
                            (int)dpToPixels(100),
                            (int)dpToPixels(100)));
                    floatingLogoView.attachControlBtns(logoFrameDeleteBtn, logoFrameResizeBtn);
                    floatingLogoView.setOnSelectedListener(new FloatingLogoView.OnSelectedListener() {
                        @Override
                        public void selected() {
                            initLogoFrameBtns(floatingLogoView);
                        }
                    });
                    floatingLogoView.setImageBitmap(image);
                    frameContainer.addView(floatingLogoView);
                    floatingLogoView.setX(frameContainer.getMeasuredWidth() / 2);
                    floatingLogoView.setY(frameContainer.getMeasuredHeight() / 2);
                    floatingLogoView.setBackgroundResource(R.drawable.rect_dashed_white);
                    int flvPadding = (int)dpToPixels(2);
                    floatingLogoView.setPadding(flvPadding, flvPadding, flvPadding, flvPadding);
                    logoFrames.add(floatingLogoView);
                    focusedLogoFrame = floatingLogoView;
                    anyWorks = true;
                }
            }
        }
        catch (Exception ignored) {
            if (designMode == DesignModes.IMAGE) {
                focusedImgFrame.setSelected(false);
                posterIVCovers.get(imageFrames.indexOf(focusedImgFrame))
                        .setBackgroundResource(R.drawable.highlight_rect);
                imgControlPanel.setVisibility(View.GONE);
                focusedImgFrame = null;
            }
        }
    }

    @Override
    public void onBackPressed() {

        if (this.anyWorks) {
            DialogExitDesign dialogExitDesign = new DialogExitDesign(ActivityDesign.this);
            dialogExitDesign.setOnExitListener(new DialogExitDesign.OnPosterExitListener() {
                @Override
                public void onExit() {
                    exitActivity();
                }
            });
            dialogExitDesign.show();
        }
        else {
            exitActivity();
        }
    }

    private void startLoadingPoster() {

        loadingLayout.setVisibility(View.VISIBLE);

        this.askPermission(new OnPermissionGrantListener() {
            @Override
            public void onPermissionGranted() {

                final Poster poster = new DataDecoder(new File(Core.getInstance().DOWNLOAD_DIR_PATH + File
                        .separator + posterId + File.separator + "DataFile.pki"), posterId).deocode();

                Core.getInstance().checkAppFolders();

                loadingLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        if (poster != null) {
                            loadPosterViews(poster);
                        }
                    }
                });
            }

            @Override
            public void onPermissionFailure() {
                Toast.makeText(ActivityDesign.this, getString(R.string.grant_permission), Toast.LENGTH_SHORT).show();
            }
        }, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE });
    }

    private void exitActivity() {

        finish();

        for (File file : new File(Core.getInstance().TEMP_DIR_PATH).listFiles()) {
            file.delete();
        }

        ActivityDesign.this.finish();
    }

    private void initToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        View view = LayoutInflater.from(this).inflate(R.layout.custom_design_action_bar, null, false);
        toolbar.addView(view);
        Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(params);

        ImageView imgBack = view.findViewById(R.id.imgBack);
        TextView txtBack = view.findViewById(R.id.txtBack);
        TextView txtActionbarTitle = view.findViewById(R.id.txtActionbarTitle);
        ImageView imgSave = view.findViewById(R.id.imgSave);
        TextView txtSave = view.findViewById(R.id.txtSave);

        txtActionbarTitle.setText("پوستر جدید");
        txtActionbarTitle.setTextSize(17);

        if (getIntent().getExtras().getString("behind_activity_name").equals("DownloadsActivity")) {
            txtBack.setText(getResources().getString(R.string.downloads));
        }
        else {
            txtBack.setText(getResources().getString(R.string.posters));
        }

        View.OnClickListener saveListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (loadingLayout.getVisibility() == View.GONE) {

                    DialogSavePoster dialogSavePoster = new DialogSavePoster(ActivityDesign.this);
                    dialogSavePoster.setPosterId(posterId);

                    dialogSavePoster.setOnSaveListener(new DialogSavePoster.OnPosterSaveListener() {
                        @Override
                        public void onSave(final int frame, int rate) {

                            Toast.makeText(ActivityDesign.this, "در حال ساخت پوستر...", Toast.LENGTH_SHORT).show();

                            for (RelativeLayout ivCover : posterIVCovers) {
                                ivCover.setBackgroundResource(0);
                            }

                            clearFloatingTextsComponents();

                            imgControlPanel.setVisibility(View.GONE);

                            if(rate != 0)
                                Core.getInstance().getNetworkHelper().ratePoster(posterId, rate);

                            PosterExporter.exportPoster(posterId, frame, posterContainer, ActivityDesign.this);
                        }
                    });
                    dialogSavePoster.show();
                }
            }
        };

        txtSave.setOnClickListener(saveListener);
        imgSave.setOnClickListener(saveListener);

        View.OnClickListener backListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        };

        txtBack.setOnClickListener(backListener);
        imgBack.setOnClickListener(backListener);
    }

    private void clearFloatingTextsComponents() {
        textFrameLockBtn.setVisibility(View.GONE);
        textFrameDeleteBtn.setVisibility(View.GONE);
        destroyVisibleFloatingMenu();
        destroyVisibleControllerView();
        if (focusedTextFrame != null) {
            focusedTextFrame.setBackgroundResource(0);
            focusedTextFrame = null;
        }
    }

    private void initView() {
        posterContainer = (RelativeLayout) findViewById(R.id.activity_design_poster_container_layout);
        backgroundIV = (ImageView) findViewById(R.id.activity_poster_background_image_view);
        frameContainer = (RelativeLayout) findViewById(R.id.activity_poster_frame_container_layout);
        logoBTN = (RelativeLayout) findViewById(R.id.activity_design_logo_button_layout);
        imagesBTN = (RelativeLayout) findViewById(R.id.activity_design_images_button_layout);
        textsBTN = (RelativeLayout) findViewById(R.id.activity_design_texts_button_layout);
        loadingLayout = (RelativeLayout) findViewById(R.id.activity_poster_loading_layout);
        imgControlPanel = (LinearLayout) findViewById(R.id.activity_design_img_frame_control_panel);
        galleryBTN = (ImageButton) findViewById(R.id.activity_design_gallery_button);
        rotateBTN = (ImageButton) findViewById(R.id.activity_design_rotate_button);
        flipBTN = (ImageButton) findViewById(R.id.activity_design_flip_button);
        textFrameDeleteBtn = (ImageView) findViewById(R.id.activity_design_text_frame_close_image_button);
        textFrameLockBtn = (ImageView) findViewById(R.id.activity_design_text_frame_lock_image_button);
        logoFrameDeleteBtn = (ImageView) findViewById(R.id.activity_design_logo_frame_close_image_button);
        logoFrameResizeBtn = (ResizeClipView) findViewById(R.id.activity_design_logo_frame_resize_image_button);
    }

    private void initListeners() {

        galleryBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (focusedImgFrame != null) {
                    openGallery();
                }
            }
        });

        rotateBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (focusedImgFrame != null) {
                    focusedImgFrame.rotate90();
                }
            }
        });

        flipBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (focusedImgFrame != null) {
                    focusedImgFrame.mirror();
                }
            }
        });

        imagesBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                designMode = DesignModes.IMAGE;

                clearLogoFrameComponents();
                clearImageFrameComponents();
                clearFloatingTextsComponents();
                setTextsLockedFull(true);
                setLogosLockedFull(true);

                imagesBTN.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                textsBTN.setBackgroundColor(Color.TRANSPARENT);
                logoBTN.setBackgroundColor(Color.TRANSPARENT);

                for (FloatingLogoView flv : logoFrames) {
                    flv.setBackgroundResource(0);
                }

                int imgViewCounter = 0;
                for (final CollageImageView imageView : imageFrames) {
                    animateFrame(posterIVCovers.get(imgViewCounter));
                    imageView.setTouchable(true);
                    imgViewCounter++;
                }
            }
        });

        textsBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                designMode = DesignModes.TEXT;

                clearLogoFrameComponents();
                clearImageFrameComponents();
                clearFloatingTextsComponents();
                setTextsLockedFull(false);
                setLogosLockedFull(true);

                if (framesCount == 0) {
                    imagesBTN.setBackgroundColor(ContextCompat.getColor(ActivityDesign.this, R.color.colorHomeGray));
                } else {
                    imagesBTN.setBackgroundColor(Color.TRANSPARENT);
                }
                logoBTN.setBackgroundColor(Color.TRANSPARENT);
                textsBTN.setBackgroundColor(getResources().getColor(R.color.colorAccent));

                for (final CollageImageView imageView : imageFrames) {
                    imageView.setTouchable(false);
                }

                for (FloatingLogoView flv : logoFrames) {
                    flv.setBackgroundResource(0);
                }

                DialogAddText dialogAddText = new DialogAddText(ActivityDesign.this
                        , new DialogAddText.OnAddListener() {
                    @Override
                    public void onAdd(String text, DialogAddText.TEXT_ALIGN align, Typeface typeface) {

                        anyWorks = true;
                        textsBTN.setBackgroundResource(R.drawable.add_text_top_stroke);
                        final FloatingTextView fTV = new FloatingTextView(ActivityDesign.this
                                , frameContainer.getMeasuredWidth()
                                , frameContainer.getMeasuredHeight());
                        fTV.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                                .WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                        fTV.setText(text);
                        fTV.setTypeface(typeface);
                        fTV.setTextColor(Color.BLACK);
                        fTV.setGravity(Gravity.CENTER_VERTICAL|(align == DialogAddText.TEXT_ALIGN.CENTER
                                ? Gravity.CENTER_HORIZONTAL : align == DialogAddText.TEXT_ALIGN.LEFT ?
                                Gravity.LEFT : Gravity.RIGHT));
                        fTV.setTextSize(FLOATING_TEXT_INITIAL_SIZE);

                        fTV.setOnSelectedListener(new FloatingTextView.OnSelectedListener() {
                            @Override
                            public void selected() {
                                clearFloatingTextsComponents();
                                focusedTextFrame = fTV;
                                focusedTextFrame.setBackgroundResource(R.drawable.background_text_frame);
                                initFloatingMenu(fTV);
                            }
                        });
                        frameContainer.addView(fTV);

                        final ViewTreeObserver observer = fTV.getViewTreeObserver();                           // Centering added text
                        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                fTV.moveHorizontally(frameContainer.getMeasuredWidth() / 2 - fTV.getMeasuredWidth() / 2);
                                fTV.moveVertically(frameContainer.getMeasuredHeight() / 2 - fTV.getMeasuredHeight() / 2);
                                observer.removeOnGlobalLayoutListener(this);
                            }
                        });
                        textFrames.add(fTV);
                        fTV.select();
                    }

                    @Override
                    public void onCancel() {
                        textsBTN.setBackgroundResource(R.drawable.add_text_top_stroke);
                    }
                });
                dialogAddText.show();

            }
        });

        logoBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                designMode = DesignModes.LOGO;

                clearLogoFrameComponents();
                clearImageFrameComponents();
                clearFloatingTextsComponents();
                setTextsLockedFull(true);
                setLogosLockedFull(false);

                if (framesCount == 0) {
                    imagesBTN.setBackgroundColor(ContextCompat.getColor(ActivityDesign.this, R.color.colorHomeGray));
                } else {
                    imagesBTN.setBackgroundColor(Color.TRANSPARENT);
                }
                textsBTN.setBackgroundColor(Color.TRANSPARENT);
                logoBTN.setBackgroundColor(getResources().getColor(R.color.colorAccent));

                for (FloatingLogoView flv : logoFrames) {
                    flv.setBackgroundResource(R.drawable.rect_dashed_white);
                }

                DialogAddLogo dialogAddLogo = new DialogAddLogo(ActivityDesign.this
                        , new DialogAddLogo.OnAddListener() {
                    @Override
                    public void onAdd() {
                        logoBTN.setBackgroundResource(R.drawable.add_text_top_stroke);
                        openGallery();
                    }

                    @Override
                    public void onCancel() {
                        logoBTN.setBackgroundResource(R.drawable.add_text_top_stroke);
                    }
                });
                dialogAddLogo.show();
            }
        });

        frameContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                clearFloatingTextsComponents();
                clearImageFrameComponents();
                clearLogoFrameComponents();
            }
        });
    }

    private void clearImageFrameComponents() {
        if (focusedImgFrame != null) {
            focusedImgFrame.setSelected(false);
        }
        imgControlPanel.setVisibility(View.GONE);
        focusedImgFrame = null;
        if (designMode == DesignModes.IMAGE) {
            for (RelativeLayout ivCover : posterIVCovers) {
                ivCover.setBackgroundResource(R.drawable.highlight_rect);
            }
        } else {
            for (RelativeLayout ivCover : posterIVCovers) {
                ivCover.setBackgroundResource(0);
            }
        }
    }

    private void destroyVisibleFloatingMenu() {
        if (visibleFloatingMenu != null) {
            if (focusedTextFrame != null) {
                focusedTextFrame.detachMenu();
            }
            frameContainer.removeView(visibleFloatingMenu);
        }
    }

    private void destroyVisibleControllerView() {
        if (visibleControllerView != null) {
            if (focusedTextFrame != null) {
                focusedTextFrame.detachMoveControlView();
            }
            frameContainer.removeView((View) visibleControllerView);
        }
    }

    private void initFloatingMenu(final FloatingTextView fTV) {

        textFrameLockBtn.setX(fTV.getX() - textFrameLockBtn.getMeasuredWidth() * 2 / 3);
        textFrameLockBtn.setY(fTV.getY() - textFrameLockBtn.getMeasuredHeight() * 2 / 3);
        textFrameLockBtn.setVisibility(View.VISIBLE);
        int padding = (int) dpToPixels(4);
        textFrameLockBtn.setPadding(padding, padding, padding, padding);
        textFrameLockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fTV.setLockedMini(!fTV.isLockedMini());
                if (fTV.isLockedMini()) {
                    textFrameLockBtn.setImageResource(R.drawable.ic_locked);
                } else {
                    textFrameLockBtn.setImageResource(R.drawable.ic_unlocked);
                }
            }
        });
        if (fTV.isLockedMini()) {
            textFrameLockBtn.setImageResource(R.drawable.ic_locked);
        } else {
            textFrameLockBtn.setImageResource(R.drawable.ic_unlocked);
        }

        textFrameDeleteBtn.setX(fTV.getX() + fTV.getMeasuredWidth() - textFrameDeleteBtn.getMeasuredWidth() / 3);
        textFrameDeleteBtn.setY(fTV.getY() - textFrameDeleteBtn.getMeasuredHeight() * 2 / 3);
        textFrameDeleteBtn.setVisibility(View.VISIBLE);
        textFrameDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFloatingTextsComponents();
                textFrames.remove(fTV);
                frameContainer.removeView(fTV);
            }
        });

        FloatingMenu floatingMenu = new FloatingMenu(ActivityDesign.this);
        floatingMenu.setButtonsClickListener(new FloatingMenu.MenuButtonsClickListener() {
            @Override
            public void textColorBtnClicked() {
                DialogTextStyle dialogTextStyle = new DialogTextStyle(ActivityDesign.this, fTV.getTextStyle());
                dialogTextStyle.setBackgroundSample(getBackgroundSample());
                dialogTextStyle.setStyleSaveListener(new DialogTextStyle.OnStyleSaveListener() {
                    @Override
                    public void onStyleSave(TextStyle style) {

                        fTV.setTextStyle(style);
                        updateStyles(fTV);
                    }
                });
                dialogTextStyle.show();
            }

            @Override
            public void pickFontBtnClicked() {

                DialogSelectFont dialogSelectFont = new DialogSelectFont(ActivityDesign.this);
                dialogSelectFont.setListener(new DialogSelectFont.OnFontSelectListener() {
                    @Override
                    public void onFontSelect(Typeface font) {
                        fTV.setTypeface(font);
                        updateStyles(fTV);
                    }
                });
                dialogSelectFont.show();
            }

            @Override
            public void editTextBtnClicked() {
                DialogAddText dialogAddText = new DialogAddText(ActivityDesign.this, new DialogAddText.OnAddListener() {
                    @Override
                    public void onAdd(String text, DialogAddText.TEXT_ALIGN align, Typeface typeface) {
                        fTV.setText(text);
                        fTV.setGravity(Gravity.CENTER_VERTICAL|(align == DialogAddText.TEXT_ALIGN.CENTER
                                ? Gravity.CENTER_HORIZONTAL : align == DialogAddText.TEXT_ALIGN.LEFT ?
                                Gravity.LEFT : Gravity.RIGHT));
                        fTV.setTypeface(typeface);
                        updateStyles(fTV);

                        final ViewTreeObserver observer = fTV.getViewTreeObserver();                        // Control text not to got out of screen after editing
                        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                if((fTV.getX() + fTV.getWidth()) > frameContainer.getWidth()){
                                    fTV.setX(frameContainer.getWidth() - fTV.getWidth());
                                }
                                if((fTV.getY() + fTV.getHeight()) > frameContainer.getHeight()){
                                    fTV.setY(frameContainer.getHeight() - fTV.getHeight());
                                }

                                observer.removeOnGlobalLayoutListener(this);                        // Do it once! ;)
                            }
                        });

                    }

                    @Override
                    public void onCancel() {}
                }, fTV.getText().toString(), fTV.getTypeface());
                dialogAddText.show();
            }

            @Override
            public void moveBtnClicked() {
                destroyVisibleControllerView();
                initMoveControlView(fTV);
                fTV.moveVertically(0);
            }

            @Override
            public void rotateBtnClicked() {
                destroyVisibleControllerView();
                initRotateView(fTV);
                fTV.moveVertically(0);
            }

            @Override
            public void textSizeBtnClicked() {
                destroyVisibleControllerView();
                initResizeView(fTV);
                fTV.moveVertically(0);
            }
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.leftMargin = GraphicHelper.getInstance().dpToPixels(4);
        params.rightMargin = GraphicHelper.getInstance().dpToPixels(4);
        floatingMenu.setLayoutParams(params);
        frameContainer.addView(floatingMenu);
        floatingMenu.setX(0);
        floatingMenu.setY(frameContainer.getMeasuredHeight());
        visibleFloatingMenu = floatingMenu;
        focusedTextFrame.attachMenu(floatingMenu, textFrameLockBtn, textFrameDeleteBtn);
    }

    private void initMoveControlView(final FloatingTextView fTV) {
        MoveTextView moveTextView = new MoveTextView(ActivityDesign.this);
        moveTextView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT));
        moveTextView.setMoveListener(new MoveTextView.OnMoveClickListener() {
            @Override
            public void onMovedClicked(int move) {

                if (move == MoveTextView.MOVE_LEFT) {
                    fTV.moveHorizontally(dpToPixels(-1f));
                }
                else if (move == MoveTextView.MOVE_UP) {
                    fTV.moveVertically(dpToPixels(-1f));
                }
                else if (move == MoveTextView.MOVE_RIGHT) {
                    fTV.moveHorizontally(dpToPixels(+1f));
                }
                else if (move == MoveTextView.MOVE_DOWN) {
                    fTV.moveVertically(dpToPixels(+1f));
                }
            }

            @Override
            public void onAlignClicked(int align) {

                if (align == MoveTextView.ALIGN_HORIZONTAL) {
                    fTV.setX(frameContainer.getWidth() / 2 - fTV.getWidth() / 2);
                }
                else if (align == MoveTextView.ALIGN_VERTICAL) {
                    fTV.setY(frameContainer.getHeight() / 2 - fTV.getHeight() / 2);
                }
            }
        });
        frameContainer.addView(moveTextView);
        moveTextView.setX(0);
        if (fTV.getY() - moveTextView.getControllerHeight() >= 0) {
            moveTextView.setY(fTV.getY() - moveTextView.getControllerHeight());
        }
        else {
            moveTextView.setY(fTV.getY() + fTV.getMeasuredHeight());
            visibleFloatingMenu.setY(fTV.getY() + fTV.getMeasuredHeight() + moveTextView.getControllerHeight());
        }
        visibleControllerView = moveTextView;
        fTV.attachMoveControlView(moveTextView);
    }

    private void initResizeView(final FloatingTextView fTV) {
        FloatingProgressView volumeView = new FloatingProgressView(ActivityDesign.this);
        volumeView.setMin(-10);
        volumeView.setMax(25);
        volumeView.setProgress(0);
        volumeView.setX(0);
        volumeView.setLabel("اندازه");
        volumeView.setProgress((int)(fTV.getTextSize() / getResources().getDisplayMetrics().density - 16));
        frameContainer.addView(volumeView);
        visibleControllerView = volumeView;
        volumeView.setY(fTV.getY() + fTV.getMeasuredHeight());
        volumeView.setProgressListener(new OnVolumeProgressListener() {
            @Override
            public void onProgressChanged(int progress) {
                fTV.setTextSize(FLOATING_TEXT_INITIAL_SIZE + progress);
                fTV.moveVertically(0);
                fTV.moveHorizontally(0);
                while (fTV.getX() + fTV.getMeasuredWidth() > frameContainer.getMeasuredWidth()) {
                    fTV.setX(fTV.getX() - 1);
                }
                fTV.post(new Runnable() {
                    @Override
                    public void run() {
                        fTV.init();
                    }
                });
            }
        });
        visibleControllerView = volumeView;
        fTV.attachMoveControlView(volumeView);
    }

    private void initRotateView(final FloatingTextView fTV) {
        FloatingProgressView volumeView = new FloatingProgressView(ActivityDesign.this);
        volumeView.setMin(-180);
        volumeView.setMax(+180);
        volumeView.setX(0);
        volumeView.setLabel("چرخش");
        volumeView.setProgress((int)fTV.getRotation());
        frameContainer.addView(volumeView);
        visibleControllerView = volumeView;
        volumeView.setY(fTV.getY() + fTV.getMeasuredHeight());
        volumeView.setProgressListener(new OnVolumeProgressListener() {
            @Override
            public void onProgressChanged(int progress) {
                fTV.setRotation(progress);
                fTV.moveVertically(0);

                double a = Math.abs(Math.sin(Math.toRadians(progress)));
                double b = Math.abs(Math.cos(Math.toRadians(progress)));

                float pivotX = fTV.getPivotX() + fTV.getMeasuredWidth()/2;
                float pivotY = fTV.getPivotY() + fTV.getMeasuredHeight()/2;

//                Log.i("CALCP", fTV.getPivotX() + " , " + fTV.getPivotY());
                Log.i("CALCP", progress + "");
                Log.i("CALCP", fTV.getMeasuredWidth() + " , " + fTV.getMeasuredHeight());

                int boxHeight = (int)((b * fTV.getMeasuredHeight()) + (2 * a * fTV.getMeasuredWidth() / 2));
                int boxWidth = (int)((a * fTV.getMeasuredHeight()) + (2 * b * fTV.getMeasuredWidth() / 2));

//                Log.i("CALCP", "width: " + boxWidth + " , height: " + boxHeight);
//                Log.i("CALCP", "Sin : " + a);
//                Log.i("CALCP", "Cos : " + b);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(boxWidth, boxHeight);
//                fTV.setLayoutParams(params);

//                fTV.getParentLayout().setX(frameContainer.getX() + pivotX - boxWidth/2);
//                fTV.getParentLayout().setY(frameContainer.getY() + pivotY - boxWidth/2);

//                fTV.getParentLayout().invalidate();
//                fTV.getParentLayout().requestLayout();

                /*int angle = progress;
                int parentWidth = (int)(Math.sin(angle) * fTV.getMeasuredWidth());
                int parentHeight = (int)(Math.cos(angle) * fTV.getMeasuredWidth());

                fTV.getParentLayout().getLayoutParams().width = parentWidth;
                fTV.getParentLayout().getLayoutParams().height = parentHeight;

                fTV.getParentLayout().invalidate();*/
            }
        });
        visibleControllerView = volumeView;
        fTV.attachMoveControlView(volumeView);
    }

    private void initLogoFrameBtns(final FloatingLogoView logoView) {

        logoFrameDeleteBtn.setX(logoView.getX() + logoView.getMeasuredWidth() - logoFrameDeleteBtn.getMeasuredWidth() / 2);
        logoFrameDeleteBtn.setY(logoView.getY() - logoFrameDeleteBtn.getMeasuredHeight() / 2);

        logoFrameDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoFrames.remove(logoView);
                frameContainer.removeView(logoView);
                clearLogoFrameComponents();
            }
        });

        logoFrameResizeBtn.setX(logoView.getX() + logoView.getMeasuredWidth() - logoFrameResizeBtn.getMeasuredWidth() / 2);
        logoFrameResizeBtn.setY(logoView.getY() + logoView.getMeasuredHeight() - logoFrameResizeBtn.getMeasuredHeight() / 2);

        logoFrameResizeBtn.setOnDragListener(new ResizeClipView.OnDragListener() {
            @Override
            public void dragged(float x, float y) {
                logoView.getLayoutParams().width = (int)(x - logoView.getX());
                logoView.getLayoutParams().height = (int)((y - logoView.getY()));
                logoView.requestLayout();
                logoFrameDeleteBtn.setX(logoView.getX() + logoView.getMeasuredWidth() - logoFrameDeleteBtn.getMeasuredWidth() / 2);
                logoFrameDeleteBtn.setY(logoView.getY() - logoFrameDeleteBtn.getMeasuredHeight() / 2);
            }
        });

        logoFrameDeleteBtn.setVisibility(View.VISIBLE);
        logoFrameResizeBtn.setVisibility(View.VISIBLE);
    }

    private void clearLogoFrameComponents() {
        logoFrameDeleteBtn.setVisibility(View.GONE);
        logoFrameResizeBtn.setVisibility(View.GONE);
    }

    private void loadPosterViews(final Poster poster) {

        final ArrayList<String> imgResPaths = new ArrayList<>();

        backFrame = poster.getBackFrame();

        float screenSizeHeight = posterContainer.getMeasuredHeight();
        final float screenSizeWidth = screenSizeHeight * ((float) backFrame.getWidth() / (float) backFrame.getHeight());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int)screenSizeWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(0, (int)(72 * getResources().getDisplayMetrics().density), 0, (int)(72 * getResources().getDisplayMetrics().density));
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        posterContainer.setLayoutParams(params);
        posterContainer.invalidate();

        ArrayList<BaseFrame> frames = poster.getFrames();

        for (BaseFrame frame : frames) {

            if (frame instanceof ImgFrame) {
                framesCount++;

                final CollageImageView imageView = new CollageImageView(ActivityDesign.this);
                float width = ((float)frame.getWidth()) * (screenSizeWidth / ((float) backFrame.getWidth())) + 2;// + imgFramePadding * 2;
                float height = ((float)frame.getHeight()) * (screenSizeHeight / ((float) backFrame.getHeight())) + 2;// + imgFramePadding * 2;
                RelativeLayout.LayoutParams imgFrameParams = new RelativeLayout.LayoutParams((int)(width), (int)(height));
                float x = ((float)frame.getX() * (screenSizeWidth / (float) backFrame.getWidth()));// - imgFramePadding);
                float y = ((float)frame.getY() * (screenSizeHeight / (float) backFrame.getHeight()));// - imgFramePadding);

                imgFrameParams.setMargins((int)x, (int)y, (int)dpToPixels(-2), (int)dpToPixels(-2));
                imageView.setLayoutParams(imgFrameParams);
                imageView.setTouchable(true);
                imageView.setFrameDimensions((int)width, (int)height);
                imgResPaths.add(((ImgFrame) frame).getMaskPath());
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        clearFloatingTextsComponents();
                        clearLogoFrameComponents();

                        if (designMode == DesignModes.IMAGE) {

                            imgControlPanel.setVisibility(View.GONE);

                            for (CollageImageView otherIV : imageFrames) {
                                otherIV.setSelected(false);
                            }

                            if (focusedImgFrame != null) {
                                posterIVCovers.get(imageFrames.indexOf(focusedImgFrame))
                                        .setBackgroundResource(R.drawable.highlight_rect);
                            }

                            focusedImgFrame = imageView;

                            posterIVCovers.get(imageFrames.indexOf(focusedImgFrame))
                                    .setBackgroundResource(R.drawable.select_rect);

                            imageView.setSelected(true);

                            if (imageView.getImage() == null) {
                                openGallery();
                            } else {
                                openImageFrameControlPanel(imageView);
                            }
                        }
                    }
                });
                ActivityDesign.this.imageFrames.add(imageView);
            }/*
            else {
                final CollageTextView txtView = (CollageTextView) LayoutInflater.from(this).inflate(R.layout.auto_size_text_view_sample, null, false);
                txtView.setBackgroundColor(Color.TRANSPARENT);
                txtView.setTypeface(Core.getInstance().getFont());
                txtView.setTextColor(((TxtFrame)frame).getTextColor());
                int width = (int)( ((float)frame.getWidth()) * ( screenSizeWidth / ((float)backFrame.getWidth()) ) );
                int height = (int)( ((float)frame.getHeight()) * ( screenSizeHeight / ((float)backFrame.getHeight()) ) );
                txtView.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
                txtView.setTouchable(false);
                txtView.setX((int)( ((float)frame.getX()) * ( screenSizeWidth / ((float)backFrame.getWidth()) ) ));
                txtView.setY((int)( ((float)frame.getY()) * ( screenSizeHeight / ((float)backFrame.getHeight()) ) ));
                txtView.setSingleLine(false);
                txtView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!imageMode) {

                            if (currentTextView != null) {
                                currentTextView.setBackgroundResource(R.drawable.highlight_rect);
                            }

                            currentTextView = txtView;
                            showTxtFrameSettings(txtView.getText().toString());
                            txtView.setBackgroundResource(R.drawable.select_rect);
                        }
                        else {
                            imgControlPanel.setVisibility(View.GONE);
                        }
                    }
                });
                ActivityDesign.this.posterTVs.add(txtView);
            }*/
        }

        if(framesCount == 0){
            imagesBTN.setClickable(false);
            imagesBTN.setBackgroundColor(ContextCompat.getColor(this, R.color.colorHomeGray));
        }

        final Bitmap[] images = new Bitmap[ActivityDesign.this.imageFrames.size()];

        new Thread(new Runnable() {
            @Override
            public void run() {

                final Bitmap bmp = BitmapFactory.decodeFile(backFrame.getBackgroundPath());

                int counter = 0;

                for (String imgResPath : imgResPaths) {
                    images[counter] = BitmapFactory.decodeFile(imgResPath);
                    counter++;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        backgroundIV.setImageBitmap(bmp);

                        int counter = images.length - 1;

                        Collections.reverse(ActivityDesign.this.imageFrames);

                        for (CollageImageView imgView : ActivityDesign.this.imageFrames) {
                            try {

                                imgView.setMask(images[counter]);
                                counter--;
                                frameContainer.addView(imgView);

                                RelativeLayout rectView = new RelativeLayout(ActivityDesign.this);
                                rectView.setLayoutParams(imgView.getLayoutParams());
                                rectView.setBackgroundResource(R.drawable.highlight_rect);
                                rectView.setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View view, MotionEvent motionEvent) {
                                        return false;
                                    }
                                });
                                posterIVCovers.add(rectView);
                                frameContainer.addView(rectView);
                            }
                            catch (Exception ignored) {
                                ignored.printStackTrace();
                            }
                        }

                        loadingLayout.setVisibility(View.GONE);

                        /*for (TextView txtView : posterTVs) {
                            frameContainer.addView(txtView);
                        }*/
                    }
                });
            }
        }).start();

        imgControlPanel.bringToFront();
    }

    private void animateFrame(RelativeLayout img){
        ShapeDrawable stroke = new ShapeDrawable(new RectShape());
        stroke.getPaint().setStrokeWidth(dpToPixels(2));
        stroke.getPaint().setStyle(Paint.Style.STROKE);
        stroke.getPaint().setColor(ContextCompat.getColor(this, R.color.colorHomePink));
        img.setBackground(stroke);

        ObjectAnimator colorAnimator = ObjectAnimator.ofInt(stroke, "alpha", 255, 0);
        ArgbEvaluator colorEvaluator = new ArgbEvaluator();
        colorAnimator.setEvaluator(colorEvaluator);
        colorAnimator.setRepeatCount(3);
        colorAnimator.setRepeatMode(ValueAnimator.REVERSE);
        colorAnimator.setDuration(350);
        colorAnimator.start();
    }

    private void openImageFrameControlPanel(CollageImageView imageView) {

        final int imgControlPanelWidth = (int)(161 * getResources().getDisplayMetrics().density);
        final int imgControlPanelHeight = (int)(48 * getResources().getDisplayMetrics().density);

        imgControlPanel.setVisibility(View.VISIBLE);

        imgControlPanel.setX(imageView.getX() + imageView.getMeasuredWidth() / 2 - imgControlPanelWidth / 2);

        if (imageView.getMeasuredHeight() > 0.9f * frameContainer.getMeasuredHeight()) {

            imgControlPanel.setY(imageView.getY() + imageView.getMeasuredHeight() - imgControlPanelHeight);
        }
        else {

            if (backgroundIV.getMeasuredHeight() - (imageView.getY() + imageView.getMeasuredHeight())  < imgControlPanelHeight) {

                imgControlPanel.setY(imageView.getY() - imgControlPanelHeight);
            }
            else {

                imgControlPanel.setY(imageView.getY() + imageView.getMeasuredHeight());
            }
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        InputStream is = getContentResolver().openInputStream(uri);
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, bitmapOptions);
        int imageWidth = bitmapOptions.outWidth;
        int imageHeight = bitmapOptions.outHeight;
        is.close();

        int w = (int)(imageWidth / (300 * getResources().getDisplayMetrics().density));
        int h = (int)(imageHeight / (300 * getResources().getDisplayMetrics().density));

        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Rect padding = new Rect(0, 0, 0, 0);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = Math.max(w, h);
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor, padding, options);
        parcelFileDescriptor.close();
        return image;
    }

    private void openGallery() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
        startActivityForResult(chooserIntent, 1);
    }

    private void setTextsLockedFull(boolean setLocked){
        for(FloatingTextView fTV : textFrames){
            fTV.setLockedFull(setLocked);
        }
    }

    private void setLogosLockedFull(boolean setLocked) {
        for (FloatingLogoView logoView : this.logoFrames) {
            logoView.setLockedFull(setLocked);
        }
    }

    private Bitmap getBackgroundSample(){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inSampleSize = 6;
        return BitmapFactory.decodeFile(backFrame.getBackgroundPath(), options);
    }

    private void updateStyles(final FloatingTextView fTV){
        TextStyle style = fTV.getTextStyle();

        if(style == null)
            return;

        if (style.getTextColor() != 0) {
            fTV.setTextColor(style.getTextColor());
        }

        if (style.getTextBackground() != null) {
            fTV.setTextBackground(style.getTextBackground());
        }

        if (style.getTextShadow() != null && style.getTextShadow().getColor() != 0) {
            fTV.setTextShadow(style.getTextShadow());
        }

        if (style.getTextStroke() != null) {
            fTV.setTextStroke(style.getTextStroke());
        }

        fTV.post(new Runnable() {
            @Override
            public void run() {
                fTV.init();
            }
        });

    }
}