package com.example.imagemagic;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.example.imagemagic.databinding.ActivityEditBinding;

import listener.AlertDialogListener;
import util.AlertUtil;
import util.AppUtil;
import util.BitmapUtil;

public class EditActivity extends AppCompatActivity implements AlertDialogListener {

    private ActivityEditBinding binding;
    public static Bitmap editBitmap;
    public static Uri editBitmapUri;
    public Bitmap updatedBitmap;
    public Bitmap visibleBitmap;
    public SeekBar borderSeekBar;
    Handler handler = new Handler();
    boolean canGoBack=false;
    int backgroundColor = Color.WHITE;
    int borderRadius = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (editBitmap==null) {
            finish();
            return;
        }

        binding = ActivityEditBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        updatedBitmap = editBitmap;
        visibleBitmap = BitmapUtil.addBackgroundToBitmap(editBitmap, backgroundColor);

        borderSeekBar = binding.borderSeekBar;

        binding.editImage.setImageBitmap(visibleBitmap);
        binding.back.setOnClickListener(v -> handleBack());
        binding.save.setOnClickListener(v -> handleSave());
        binding.crop.setOnClickListener(v -> AppUtil.showToastMessage(this, "Crop"));
        binding.rotate.setOnClickListener(v -> handleRotate());
        binding.border.setOnClickListener(v -> handleBorder());

        binding.background.setOnClickListener(v -> handleBackground());
            binding.blackBackground.setOnClickListener(v -> handleBlackBackground());
            binding.whiteBackground.setOnClickListener(v -> handleWhiteBackground());

        binding.filter.setOnClickListener(v -> AppUtil.showToastMessage(this, "Filter"));
        binding.adjust.setOnClickListener(v -> AppUtil.showToastMessage(this, "Adjust"));
        binding.draw.setOnClickListener(v -> AppUtil.showToastMessage(this, "Draw"));

        view.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            // Setting the height for the ImageView (half of the screen height)
            int desiredHeight = getResources().getDisplayMetrics().heightPixels / 2;

            ViewGroup.LayoutParams layoutParams = binding.editImage.getLayoutParams();
            layoutParams.height = desiredHeight;
            binding.editImage.setLayoutParams(layoutParams);
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding=null;
    }

    @Override
    public void onBackPressed() {
        if (canGoBack){
            super.onBackPressed();
        } else {
            if (binding.backgroundBar.getVisibility() == View.VISIBLE) {
                binding.backgroundBar.setVisibility(View.GONE);
                binding.toolBarContainer.setVisibility(View.VISIBLE);
            } else {
                handleBack();
            }
        }
    }

    @Override
    public void onPositiveButtonClick() {
        canGoBack=true;
        onBackPressed();
    }

    public void handleBack() {
        AlertUtil.showAlertDialog(this, "Confirm Exit", "Are you sure you want to exit?", "Yes", "No", this);
    }

    public void handleSave() {
        binding.progressBarLayout.setVisibility(View.VISIBLE);
        new Thread(() -> {
            final boolean isImageSaved = BitmapUtil.saveImageToDevice(this, BitmapUtil.drawableToBitmap(binding.editImage.getDrawable()));
            handler.post(() -> {
                binding.progressBarLayout.setVisibility(View.GONE);
                if (isImageSaved) {
                    AppUtil.showToastMessage(this, "Image saved to device.");
                } else {
                    AppUtil.showToastMessage(this, "Image failed to save.");
                }
            });
        }).start();
    }

    public void handleRotate() {
        if (updatedBitmap!=null) {
            borderSeekBar.setVisibility(View.GONE);
            updatedBitmap = BitmapUtil.rotateBitmap(updatedBitmap);
            visibleBitmap = BitmapUtil.addBackgroundToBitmap(updatedBitmap, backgroundColor);
            visibleBitmap = BitmapUtil.createRoundedBitmap(visibleBitmap, borderRadius);
            binding.editImage.setImageBitmap(visibleBitmap);
        } else {
            AppUtil.showToastMessage(this, "No image to rotate.");
        }
    }

    public void handleBorder() {
        if (borderSeekBar.getVisibility() == View.VISIBLE) {
            borderSeekBar.setVisibility(View.GONE);
        } else {
            borderSeekBar.setVisibility(View.VISIBLE);
            changeBitmapBorder();
        }
    }

    public void handleBackground() {
        if (borderSeekBar.getVisibility() == View.VISIBLE) {
            borderSeekBar.setVisibility(View.GONE);
        }
        binding.toolBarContainer.setVisibility(View.GONE);
        binding.backgroundBar.setVisibility(View.VISIBLE);
    }

    public void handleBlackBackground() {
        backgroundColor = Color.BLACK;
        visibleBitmap = BitmapUtil.addBackgroundToBitmap(updatedBitmap, backgroundColor);
        visibleBitmap = BitmapUtil.createRoundedBitmap(visibleBitmap, borderRadius);
        binding.editImage.setImageBitmap(visibleBitmap);
    }

    public void handleWhiteBackground() {
        backgroundColor = Color.WHITE;
        visibleBitmap = BitmapUtil.addBackgroundToBitmap(updatedBitmap, backgroundColor);
        visibleBitmap = BitmapUtil.createRoundedBitmap(visibleBitmap, borderRadius);
        binding.editImage.setImageBitmap(visibleBitmap);
    }

    public void changeBitmapBorder() {
        binding.borderSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                visibleBitmap = BitmapUtil.addBackgroundToBitmap(updatedBitmap, backgroundColor);
                visibleBitmap = BitmapUtil.createRoundedBitmap(visibleBitmap, progress);
                binding.editImage.setImageBitmap(visibleBitmap);
                borderRadius = progress;
                binding.borderSeekBar.setProgress(borderRadius);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}

        });
    }

}