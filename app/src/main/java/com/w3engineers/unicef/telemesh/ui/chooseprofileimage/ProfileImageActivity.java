package com.w3engineers.unicef.telemesh.ui.chooseprofileimage;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.ext.strom.application.ui.base.ItemClickListener;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.databinding.ActivityProfileImageBinding;
import com.w3engineers.unicef.telemesh.ui.createuser.CreateUserActivity;
import com.w3engineers.unicef.util.helper.ImageUtil;

public class ProfileImageActivity extends BaseActivity implements ItemClickListener<Integer> {

    private ActivityProfileImageBinding mProfileImageBinding;
    private int selectedItem = -1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_profile_image;
    }

    @Override
    protected int getToolbarId() {
        return R.id.toolbar;
    }

    @Override
    protected int statusBarColor() {
        return R.color.colorPrimaryDark;
    }


    @Override
    protected void startUI() {

        mProfileImageBinding = (ActivityProfileImageBinding) getViewDataBinding();
        setTitle(getString(R.string.select_photo));
        selectedItem = getIntent().getIntExtra(CreateUserActivity.IMAGE_POSITION, CreateUserActivity.INITIAL_IMAGE_INDEX);
        initRecyclerView();
    }

    private void initRecyclerView() {
        ProfileImageAdapter mAdapter = new ProfileImageAdapter(selectedItem);
        mAdapter.setItemClickListener(this);
        mProfileImageBinding.recyclerView.setAdapter(mAdapter);
        int ITEM_IN_ROW = 3;
        mProfileImageBinding.recyclerView.setLayoutManager(new GridLayoutManager(this, ITEM_IN_ROW));
        mAdapter.addItem(ImageUtil.getAllImages());
    }


    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_done:
                Intent intent = getIntent();
                intent.putExtra(CreateUserActivity.IMAGE_POSITION, selectedItem);
                setResult(RESULT_OK, intent);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(@NonNull View view, @NonNull Integer item) {
        selectedItem = item;
    }
}
