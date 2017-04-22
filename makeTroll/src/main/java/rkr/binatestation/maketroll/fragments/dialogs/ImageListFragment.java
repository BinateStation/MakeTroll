package rkr.binatestation.maketroll.fragments.dialogs;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.esafirm.imagepicker.features.ImagePicker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rkr.binatestation.maketroll.R;
import rkr.binatestation.maketroll.adapters.ImageListRecyclerViewAdapter;
import rkr.binatestation.maketroll.interfaces.ImageSelectedListener;
import rkr.binatestation.maketroll.web.ServerResponseReceiver;
import rkr.binatestation.maketroll.web.WebServiceUtils;

import static android.support.v7.widget.StaggeredGridLayoutManager.VERTICAL;
import static rkr.binatestation.maketroll.web.WebServiceConstants.KEY_DATA;
import static rkr.binatestation.maketroll.web.WebServiceConstants.KEY_STATUS;

/**
 * Bottom sheet dialog fragment to show the image list
 */
public class ImageListFragment extends BottomSheetDialogFragment implements SearchView.OnQueryTextListener, View.OnClickListener {

    public static final int REQUEST_CODE_PICKER = 100;
    private static final String TAG = "ImageListFragment";
    private ImageListRecyclerViewAdapter mImageListRecyclerViewAdapter;
    private View.OnClickListener mOnClickListener;
    private ImageSelectedListener mImageSelectedListener;

    public ImageListFragment() {
        // Required empty public constructor
    }

    public static ImageListFragment newInstance() {
        Log.d(TAG, "newInstance() called");
        Bundle args = new Bundle();

        ImageListFragment fragment = new ImageListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static ImageListFragment newInstance(View.OnClickListener onClickListener, ImageSelectedListener imageSelectedListener) {
        Log.d(TAG, "newInstance() called with: onClickListener = [" + onClickListener + "]");
        Bundle args = new Bundle();

        ImageListFragment fragment = new ImageListFragment();
        fragment.mOnClickListener = onClickListener;
        fragment.mImageSelectedListener = imageSelectedListener;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View toolbarView = view.findViewById(R.id.FIL_toolbar_layout);
        if (toolbarView != null && !getShowsDialog()) {
            toolbarView.setVisibility(View.GONE);
        }
        if (getShowsDialog()) {
            View addTextView = view.findViewById(R.id.FIL_frame_label);
            View doneView = view.findViewById(R.id.FIL_action_done);
            addTextView.setOnClickListener(this);
            doneView.setOnClickListener(this);
        }
        SearchView searchView = (SearchView) view.findViewById(R.id.FIL_search);
        searchView.setOnQueryTextListener(this);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.FIL_image_list_recycler_view);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, VERTICAL));
        recyclerView.setAdapter(mImageListRecyclerViewAdapter = new ImageListRecyclerViewAdapter(getShowsDialog(), this));
        getImageList("");
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        getImageList(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        getImageList(newText);
        return true;
    }

    private void getImageList(String query) {
        try {
            WebServiceUtils.search(getContext(), query, new ServerResponseReceiver(new Handler(), new ServerResponseReceiver.Receiver() {
                @Override
                public void onServerResponse(int resultCode, JSONObject resultData, String message) {
                    if (resultCode == 200) {
                        parseServerResponse(resultData);
                    }
                }

                @Override
                public void onUpdateProgress(int progress) {

                }
            }));
        } catch (Exception e) {
            Log.e(TAG, "getImageList: ", e);
        }
    }

    private void parseServerResponse(JSONObject jsonObject) {
        if (jsonObject != null) {
            int status = jsonObject.optInt(KEY_STATUS);
            if (200 == status) {
                JSONArray jsonArray = jsonObject.optJSONArray(KEY_DATA);
                if (jsonArray != null && mImageListRecyclerViewAdapter != null) {
                    List<String> stringList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        stringList.add(jsonArray.optString(i));
                    }
                    mImageListRecyclerViewAdapter.setImageEndUrls(stringList);
                }
            }
        }
    }

    public ArrayList<String> getItemModels() {
        if (mImageListRecyclerViewAdapter != null) {
            return mImageListRecyclerViewAdapter.getSelectedItemModel();
        }
        return null;
    }

    public void clearSelection() {
        if (mImageListRecyclerViewAdapter != null) {
            mImageListRecyclerViewAdapter.clearSelection();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.FIL_action_done) {
            if (mImageSelectedListener != null && mImageListRecyclerViewAdapter != null) {
                mImageSelectedListener.onDone(mImageListRecyclerViewAdapter.getSelectedItemModel(), false);
            }
        } else if (id == R.id.FPFT_frame_square) {
            showImagePicker();
        } else {
            if (mOnClickListener != null) {
                mOnClickListener.onClick(v);
            }
        }
        dismiss();
    }


    private void showImagePicker() {
        Log.d(TAG, "showImagePicker() called");
        ImagePicker.create(getActivity())
                .multi()
                .start(REQUEST_CODE_PICKER);
    }
}
