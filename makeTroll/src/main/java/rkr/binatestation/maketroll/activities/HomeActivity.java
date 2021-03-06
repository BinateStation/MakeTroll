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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import rkr.binatestation.maketroll.R;
import rkr.binatestation.maketroll.adapters.ViewPagerAdapter;
import rkr.binatestation.maketroll.fragments.MyCreationsFragment;
import rkr.binatestation.maketroll.fragments.dialogs.ImageListFragment;

import static rkr.binatestation.maketroll.utils.Constants.KEY_ITEM_MODELS;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private ImageListFragment mImageListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.AH_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton createFrameFab = findViewById(R.id.AH_add_frame);
        createFrameFab.setColorFilter(Color.WHITE);
        createFrameFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToMakeTrollFrame();
            }
        });

        ViewPager viewPager = findViewById(R.id.AH_view_pager);
        TabLayout tabLayout = findViewById(R.id.AH_tab_layout);
        assert tabLayout != null;
        tabLayout.setupWithViewPager(viewPager, true);
        setupViewPager(viewPager);

        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.MH_action_help) {
            startActivity(new Intent(this, HelpActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        Log.d(TAG, "setupViewPager() called with: viewPager = [" + viewPager + "]");
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        mImageListFragment = ImageListFragment.newInstance();
        mImageListFragment.setShowsDialog(false);
        adapter.addFrag(mImageListFragment, getString(R.string.images));
        adapter.addFrag(MyCreationsFragment.newInstance(), getString(R.string.my_creations));
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
