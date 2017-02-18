package rkr.bharathi.maketroll.activities;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipDescription;
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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import rkr.bharathi.maketroll.R;
import rkr.bharathi.maketroll.adapters.RecyclerViewAdapter;
import rkr.bharathi.maketroll.fragments.dialogs.PickFrameTypeFragment;
import rkr.bharathi.maketroll.models.ItemModel;
import rkr.bharathi.maketroll.models.ViewType;
import rkr.bharathi.maketroll.utils.OnStartDragListener;
import rkr.bharathi.maketroll.utils.SimpleItemTouchHelperCallback;
import rkr.bharathi.maketroll.utils.Utils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        OnStartDragListener, View.OnTouchListener, View.OnDragListener {

    private static final String TAG = "MainActivity";
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private GridLayoutManager mGridLayoutManager;
    private ItemTouchHelper mItemTouchHelper;
    private RelativeLayout mImageFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageFrame = (RelativeLayout) findViewById(R.id.activity_main_image_frame);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.activity_main_recycler_view);
        mGridLayoutManager = new GridLayoutManager(this, 2);
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mRecyclerViewAdapter != null) {
                    return mRecyclerViewAdapter.getItemViewType(position);
                } else {
                    return 2;
                }
            }
        });
        recyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerViewAdapter = new RecyclerViewAdapter(getSupportFragmentManager(), this);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mRecyclerViewAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
        mRecyclerViewAdapter.setItemModelList(ItemModel.getDummyList(1));
        recyclerView.setAdapter(mRecyclerViewAdapter);
        setMeasures();
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
                startActivity(new Intent(this, FrameLayoutActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setMeasures() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        if (mRecyclerViewAdapter != null) {
            mRecyclerViewAdapter.setMeasures(height - 100, width - 50);
        }
    }

    private void addFrame(ViewType viewType) {
        Log.d(TAG, "addFrame() called");
        if (mRecyclerViewAdapter != null) {
            mRecyclerViewAdapter.addItem(viewType);
        }
    }

    private void addFrame() {
        if (mImageFrame != null) {
            View newButton = new View(this);
            newButton.setTag("New button");
            newButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(100, 100);
            layoutParams.setMargins(50, 50, 0, 0);
            mImageFrame.addView(newButton, layoutParams);

            newButton.setOnTouchListener(new MyTouchListener());
            mImageFrame.setOnDragListener(new MyDragListener());
        }
    }

    private void showFramePickerDialog() {
        Log.d(TAG, "showFragmentPickerDialog() called");
        PickFrameTypeFragment pickFrameTypeFragment = PickFrameTypeFragment.newInstance(this);
        pickFrameTypeFragment.show(getSupportFragmentManager(), pickFrameTypeFragment.getTag());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_main_action_add_frame:
                showFramePickerDialog();
                break;
            case R.id.activity_main_action_save:
                if (mRecyclerViewAdapter != null) {
                    mRecyclerViewAdapter.setHideUnwantedViews(true);
                    checkView();
                }
                break;
            case R.id.FPFT_frame_square:
                addFrame(ViewType.SQUARE);
                break;
            case R.id.FPFT_frame_rectangle:
                addFrame(ViewType.RECTANGLE);
                break;
            case R.id.FPFT_frame_label:
                addFrame();
                break;
        }
    }

    private void showAlertForPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("To save this created image, we need permission to access your external storage..!");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(MainActivity.this,
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

    private void checkView() {
        if (mGridLayoutManager != null && mRecyclerViewAdapter != null) {
            int lastFullyVisibleViewPosition = mGridLayoutManager.findLastCompletelyVisibleItemPosition();
            int lastItemPosition = mRecyclerViewAdapter.getItemCount() - 1;
            if (lastFullyVisibleViewPosition < lastItemPosition) {
                Toast.makeText(this, "lastFullyVisibleViewPosition :- " + lastFullyVisibleViewPosition + " - lastItemPosition :- " + lastItemPosition, Toast.LENGTH_SHORT).show();
                mRecyclerViewAdapter.setHideUnwantedViews(false);
            } else {
                checkPermissionBeforeSaveImage();
            }
        }
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

    /**
     * Called when a view is requesting a start of a drag.
     *
     * @param viewHolder The holder of the view to drag.
     */
    @Override
    public void onStartDrag(RecyclerViewAdapter.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            ClipData.Item item = new ClipData.Item((CharSequence) view.getTag());
            String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};

            ClipData dragData = new ClipData("Test", mimeTypes, item);
            View.DragShadowBuilder myShadow = new View.DragShadowBuilder(view);

            view.startDrag(dragData, myShadow, null, 0);

            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);

            view.startDrag(data, shadowBuilder, view, 0);
//            view.setVisibility(View.INVISIBLE);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                layoutParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
                Log.d(TAG, "Action is DragEvent.ACTION_DRAG_STARTED");

                // Do nothing
                break;

            case DragEvent.ACTION_DRAG_ENTERED:
                Log.d(TAG, "Action is DragEvent.ACTION_DRAG_ENTERED");
                int x_cord = (int) event.getX();
                int y_cord = (int) event.getY();
                break;

            case DragEvent.ACTION_DRAG_EXITED:
                Log.d(TAG, "Action is DragEvent.ACTION_DRAG_EXITED");
                x_cord = (int) event.getX();
                y_cord = (int) event.getY();
                layoutParams.leftMargin = x_cord;
                layoutParams.topMargin = y_cord;
                v.setLayoutParams(layoutParams);
                break;

            case DragEvent.ACTION_DRAG_LOCATION:
                Log.d(TAG, "Action is DragEvent.ACTION_DRAG_LOCATION");
                x_cord = (int) event.getX();
                y_cord = (int) event.getY();
                break;

            case DragEvent.ACTION_DRAG_ENDED:
                Log.d(TAG, "Action is DragEvent.ACTION_DRAG_ENDED");

                // Do nothing
                break;

            case DragEvent.ACTION_DROP:
                Log.d(TAG, "ACTION_DROP event");

                // Do nothing
                break;
            default:
                break;
        }
        return true;
    }

    private final class MyDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            // Store the action type for the incoming event
            final int action = event.getAction();

            // Handles each of the expected events
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate();

                    // Returns true to indicate that the View can accept the
                    // dragged data.
                    return true;

                case DragEvent.ACTION_DRAG_ENTERED:
                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate();

                    return true;

                case DragEvent.ACTION_DRAG_LOCATION:
                    // Ignore the event
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate();

                    return true;

                case DragEvent.ACTION_DROP:
                    // Gets the item containing the dragged data
                    ClipData dragData = event.getClipData();
                    ViewGroup viewGroup = (ViewGroup) v;
                    for (int i = 0; i < viewGroup.getChildCount(); i++) {
                        View child = viewGroup.getChildAt(i);
                        if (child.getTag() != null && child.getTag().equals("New button")) {

                            viewGroup.removeView(child);

                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(100, 100);
                            layoutParams.setMargins((int) event.getX(), (int) event.getY(), 0, 0);

                            viewGroup.addView(child, layoutParams);
                            child.setVisibility(View.VISIBLE);
                            break;
                        }
                    }

                    // Gets the text data from the item.
                    final String tag = dragData.getItemAt(0).getText().toString();

                    // Displays a message containing the dragged data.
                    Toast.makeText(MainActivity.this, "The dragged image is " + tag, Toast.LENGTH_SHORT).show();

                    // Invalidates the view to force a redraw
                    v.invalidate();

                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    // Invalidates the view to force a redraw
                    v.invalidate();
                    mImageFrame.invalidate();
                    return true;

                default:
                    break;
            }

            return false;
        }
    }


    private final class MyTouchListener implements View.OnTouchListener {
        public boolean onTouch(View v, MotionEvent motionEvent) {
            String tag = v.getTag().toString();

            // Instantiates the drag shadow builder
            View.DragShadowBuilder mShadow;

            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    ClipData data = ClipData.newPlainText("some label", tag);
                    mShadow = new View.DragShadowBuilder(v);
                    v.startDrag(data, mShadow, null, 0);
                    v.setVisibility(View.GONE);
                    break;

                case MotionEvent.ACTION_UP:
                    v.performClick();
                    break;

                default:
                    break;
            }
            return false;
        }
    }
}
