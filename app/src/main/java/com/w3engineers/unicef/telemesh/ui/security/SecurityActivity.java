package com.w3engineers.unicef.telemesh.ui.security;

import android.Manifest;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivitySecurityBinding;
import com.w3engineers.unicef.telemesh.ui.createuser.CreateUserActivity;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;

import java.util.List;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

public class SecurityActivity extends BaseActivity {

    private ActivitySecurityBinding mBinding;
    private SecurityViewModel mViewModel;
    private String mUserName;
    private int mAvatarIndex;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_security;
    }

    @Override
    protected int getToolbarId() {
        return R.id.toolbar;
    }


    @Override
    protected void startUI() {
        mBinding = (ActivitySecurityBinding) getViewDataBinding();
        mViewModel = getViewModel();

        parseIntent();
        initView();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.button_next:
                requestMultiplePermissions();
                break;
            case R.id.button_skip:
                // Todo goto home page with other password
                break;
        }
    }

    private void initView() {
        setClickListener(mBinding.buttonNext, mBinding.buttonSkip);
        mViewModel.textChangeLiveData.observe(this, this::nextButtonControl);
        mViewModel.textEditControl(mBinding.editTextBoxPassword);
    }

    private void nextButtonControl(String nameText) {
        if (!TextUtils.isEmpty(nameText) &&
                nameText.length() >= Constants.DefaultValue.MINIMUM_PASSWORD_LIMIT) {

            mBinding.buttonNext.setBackgroundResource(R.drawable.ractangular_gradient);
            mBinding.buttonNext.setTextColor(getResources().getColor(R.color.white));
            mBinding.buttonNext.setClickable(true);
        } else {
            mBinding.buttonNext.setBackgroundResource(R.drawable.ractangular_white);
            mBinding.buttonNext.setTextColor(getResources().getColor(R.color.new_user_button_color));
            mBinding.buttonNext.setClickable(false);
        }
    }

    protected void requestMultiplePermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {
                            goNext();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            requestMultiplePermissions();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(
                            List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).withErrorListener(error -> requestMultiplePermissions()).onSameThread().check();
    }

    protected void goNext() {
        String password = mBinding.editTextBoxPassword.getText() + "";

        if (mViewModel.storeData(mUserName, mAvatarIndex,password)) {

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

            if (CreateUserActivity.sInstance != null) {
                CreateUserActivity.sInstance.finish();
            }
        }
    }

    private void parseIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(Constants.IntentKeys.USER_NAME)) {
            mUserName = intent.getStringExtra(Constants.IntentKeys.USER_NAME);
            mAvatarIndex = intent.getIntExtra(Constants.IntentKeys.AVATAR_INDEX, 0);
        }
    }


    private SecurityViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) ServiceLocator.getInstance().getSecurityViewModel(getApplication());
            }
        }).get(SecurityViewModel.class);
    }
}
