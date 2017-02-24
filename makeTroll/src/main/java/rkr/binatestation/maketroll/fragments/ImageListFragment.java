package rkr.binatestation.maketroll.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import rkr.binatestation.maketroll.R;
import rkr.binatestation.maketroll.adapters.ImageListRecyclerViewAdapter;
import rkr.binatestation.maketroll.web.ServerResponseReceiver;
import rkr.binatestation.maketroll.web.WebServiceUtils;

import static android.support.v7.widget.StaggeredGridLayoutManager.VERTICAL;
import static rkr.binatestation.maketroll.web.WebServiceConstants.KEY_DATA;
import static rkr.binatestation.maketroll.web.WebServiceConstants.KEY_STATUS;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageListFragment extends Fragment implements SearchView.OnQueryTextListener {

    private static final String TAG = "ImageListFragment";

    private ImageListRecyclerViewAdapter mImageListRecyclerViewAdapter;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SearchView searchView = (SearchView) view.findViewById(R.id.FIL_search);
        searchView.setOnQueryTextListener(this);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.FIL_image_list_recycler_view);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, VERTICAL));
        recyclerView.setAdapter(mImageListRecyclerViewAdapter = new ImageListRecyclerViewAdapter());
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
    }

    private void parseServerResponse(JSONObject jsonObject) {
        if (jsonObject != null) {
            int status = jsonObject.optInt(KEY_STATUS);
            if (200 == status) {
                JSONArray jsonArray = jsonObject.optJSONArray(KEY_DATA);
                if (jsonArray != null && mImageListRecyclerViewAdapter != null) {
                    mImageListRecyclerViewAdapter.setJsonArray(jsonArray);
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

}
