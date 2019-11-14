package com.w3engineers.unicef.telemesh.ui.importprofile;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityImportProfileBinding;

public class ImportProfileActivity extends BaseActivity {

    private ActivityImportProfileBinding mBinding;
    private ImportProfileViewModel mViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_import_profile;
    }


    @Override
    protected void startUI() {
        mBinding = (ActivityImportProfileBinding) getViewDataBinding();
        mViewModel = getViewModel();

        initView();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.image_view_back:
                finish();
                break;
            case R.id.button_import_profile:
                // Todo import file
                break;
            case R.id.button_scan_profile:
                // Scan from qr.
                // Now it is off
                break;
        }
    }

    private void initView() {
        setClickListener(mBinding.imageViewBack, mBinding.buttonImportProfile, mBinding.buttonScanProfile);
    }

    private ImportProfileViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) ServiceLocator.getInstance().getImportProfileViewModel(getApplication());
            }
        }).get(ImportProfileViewModel.class);
    }
}
