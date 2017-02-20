package rkr.bharathi.maketroll.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import rkr.bharathi.maketroll.R;
import rkr.bharathi.maketroll.fragments.CellFragment;
import rkr.bharathi.maketroll.fragments.dialogs.PickFrameTypeFragment;
import rkr.bharathi.maketroll.models.ViewType;
import rkr.bharathi.maketroll.utils.Utils;

public class FrameLayoutActivity extends AppCompatActivity implements View.OnClickListener, CellFragment.CellFragmentListener {

    private static final String TAG = "FrameLayoutActivity";
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private FrameLayout mImageFrame;
    private List<CellFragment> mCellFragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame_layout);

        mImageFrame = (FrameLayout) findViewById(R.id.activity_frame_layout_image_frame);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.global_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.GM_switch_activity:
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(android.R.anim.slide_out_right, android.R.anim.slide_in_left);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_frame_layout_action_add_frame:
                showFramePickerDialog();
                break;
            case R.id.activity_frame_layout_action_save:
                if (mImageFrame != null) {
                    hideUnwantedViews();
                }
                break;
            case R.id.FPFT_frame_square:
                addCell(ViewType.SQUARE);
                break;
            case R.id.FPFT_frame_rectangle:
                addCell(ViewType.RECTANGLE);
                break;
            case R.id.FPFT_frame_label:
                addCell(ViewType.TEXT);
                break;
        }

    }

    private void hideUnwantedViews() {
        for (CellFragment cellFragment : mCellFragments) {
            cellFragment.setHideUnwantedViews(true);
        }
        mImageFrame.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        checkPermissionBeforeSaveImage();
    }

    private void showAlertForPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("To save this created image, we need permission to access your external storage..!");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(FrameLayoutActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void checkPermissionBeforeSaveImage() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showAlertForPermission();
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            saveFrame(mImageFrame);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (PackageManager.PERMISSION_GRANTED == grantResults[0]) {
                saveFrame(findViewById(R.id.activity_main_recycler_view));
            } else {
                showAlertForPermission();
            }
        }
    }

    private void saveFrame(final View view) {
        view.post(new Runnable() {
            @Override
            public void run() {
                try {
                    //Create a Bitmap with the same dimensions
                    Bitmap image = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.RGB_565);
                    //Draw the view inside the Bitmap
                    view.draw(new Canvas(image));

                    //Store to sdcard
                    File myFile = Utils.createImageFile(view.getContext());
                    FileOutputStream out = new FileOutputStream(myFile);

                    image.compress(Bitmap.CompressFormat.PNG, 90, out); //Output

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void addCell(ViewType viewType) {
        FrameLayout.LayoutParams layoutParams;
        int width = mImageFrame.getWidth();
        int height = mImageFrame.getHeight();
        if (viewType == ViewType.RECTANGLE) {
            layoutParams = new FrameLayout.LayoutParams(width, height / 3);
        } else if (viewType == ViewType.TEXT) {
            layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            layoutParams = new FrameLayout.LayoutParams(width / 2, height / 3);
        }
        layoutParams.setMargins(50, 50, 0, 0);
        CellFragment cellFragment = CellFragment.newInstance(layoutParams, width, height, viewType, mCellFragments.size());
        getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_frame_layout_image_frame, cellFragment, cellFragment.getTag())
                .commit();
        mCellFragments.add(cellFragment);
    }

    private void showFramePickerDialog() {
        Log.d(TAG, "showFragmentPickerDialog() called");
        PickFrameTypeFragment pickFrameTypeFragment = PickFrameTypeFragment.newInstance(this);
        pickFrameTypeFragment.show(getSupportFragmentManager(), pickFrameTypeFragment.getTag());
    }

    @Override
    public void invalidate() {
        mImageFrame.invalidate();
    }

    @Override
    public void remove(int position) {
        CellFragment cellFragment = mCellFragments.get(position);
        getSupportFragmentManager().beginTransaction()
                .remove(cellFragment)
                .commit();
        mCellFragments.remove(position);
        mImageFrame.invalidate();
    }
}