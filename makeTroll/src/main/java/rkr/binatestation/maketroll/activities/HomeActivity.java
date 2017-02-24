package rkr.binatestation.maketroll.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import rkr.binatestation.maketroll.R;
import rkr.binatestation.maketroll.adapters.ViewPagerAdapter;
import rkr.binatestation.maketroll.fragments.ImageListFragment;
import rkr.binatestation.maketroll.fragments.MyCreationsFragment;

import static rkr.binatestation.maketroll.web.WebServiceConstants.KEY_ITEM_MODELS;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private ImageListFragment mImageListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.AH_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.AH_add_frame);
        fab.setColorFilter(Color.WHITE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToMakeTrollFrame();
            }
        });

        ViewPager viewPager = (ViewPager) findViewById(R.id.AH_view_pager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.AH_tab_layout);
        assert tabLayout != null;
        tabLayout.setupWithViewPager(viewPager, true);
        setupViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        Log.d(TAG, "setupViewPager() called with: viewPager = [" + viewPager + "]");
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(MyCreationsFragment.newInstance(), getString(R.string.my_creations));
        adapter.addFrag(mImageListFragment = ImageListFragment.newInstance(), getString(R.string.images));
        viewPager.setAdapter(adapter);
    }

    private void navigateToMakeTrollFrame() {
        Log.d(TAG, "navigateToMakeTrollFrame() called");
        Intent intent = new Intent(this, FrameLayoutActivity.class);
        if (mImageListFragment != null) {
            ArrayList<String> itemModels = mImageListFragment.getItemModels();
            if (itemModels != null && itemModels.size() > 0) {
                intent.putStringArrayListExtra(KEY_ITEM_MODELS, itemModels);
            }
            mImageListFragment.clearSelection();
        }
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
