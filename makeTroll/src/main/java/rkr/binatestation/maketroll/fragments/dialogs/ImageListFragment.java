package rkr.binatestation.maketroll.fragments.dialogs;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.esafirm.imagepicker.features.ImagePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import rkr.binatestation.maketroll.BuildConfig;
import rkr.binatestation.maketroll.R;
import rkr.binatestation.maketroll.adapters.ImageListRecyclerViewAdapter;
import rkr.binatestation.maketroll.database.DbActionsIntentService;
import rkr.binatestation.maketroll.database.TrollMakerContract;
import rkr.binatestation.maketroll.interfaces.ImageSelectedListener;
import rkr.binatestation.maketroll.models.DataModel;
import rkr.binatestation.maketroll.web.VolleySingleton;

import static android.support.v7.widget.StaggeredGridLayoutManager.VERTICAL;
import static rkr.binatestation.maketroll.utils.Constants.KEY_DATA;
import static rkr.binatestation.maketroll.utils.Constants.KEY_SEARCH;
import static rkr.binatestation.maketroll.utils.Constants.KEY_STATUS;
import static rkr.binatestation.maketroll.utils.Constants.URL_SEARCH;

/**
 * Bottom sheet dialog fragment to show the image list
 */
public class ImageListFragment extends BottomSheetDialogFragment implements SearchView.OnQueryTextListener,
        View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>,
        Response.Listener<String>, Response.ErrorListener, SwipeRefreshLayout.OnRefreshListener {

    public static final int REQUEST_CODE_PICKER = 100;
    private static final String TAG = "ImageListFragment";
    private static final String KEY_QUERY = "QUERY";
    private ImageListRecyclerViewAdapter mImageListRecyclerViewAdapter;
    private View.OnClickListener mOnClickListener;
    private ImageSelectedListener mImageSelectedListener;
    private StaggeredGridLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

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
        SearchView searchView = view.findViewById(R.id.FIL_search);
        searchView.setOnQueryTextListener(this);

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorMaskWhite, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        RecyclerView recyclerView = view.findViewById(R.id.FIL_image_list_recycler_view);
        recyclerView.setLayoutManager(mLayoutManager = new StaggeredGridLayoutManager(2, VERTICAL));
        recyclerView.setAdapter(mImageListRecyclerViewAdapter = new ImageListRecyclerViewAdapter(getShowsDialog(), this));
        getImageList();
    }

    private void getImageListFromLocalDB(String query) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_QUERY, query);
        if (getActivity() != null) {
            LoaderManager loaderManager = getActivity().getSupportLoaderManager();
            if (loaderManager != null) {
                if (loaderManager.getLoader(1) == null) {
                    loaderManager.initLoader(1, bundle, this);
                } else {
                    loaderManager.restartLoader(1, bundle, this);
                }
            }
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        getImageListFromLocalDB(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        getImageListFromLocalDB(newText);
        return true;
    }

    private void getImageList() {
        try {
            StringRequest stringRequest = new StringRequest(
                    Request.Method.POST,
                    URL_SEARCH,
                    this,
                    this
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new LinkedHashMap<>();
                    params.put(KEY_SEARCH, "");
                    return params;
                }
            };
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "---------------------------------Request---------------------------------------");
                Log.d(TAG, "create: Url :- " + stringRequest.getUrl());
                Log.d(TAG, "---------------------------------Request End---------------------------------------");
            }
            VolleySingleton.getInstance(getContext()).addToRequestQueue(getContext(), stringRequest);
            showProgress();
        } catch (Exception e) {
            Log.e(TAG, "getImageList: ", e);
        }
    }

    private void showProgress() {
        if (mSwipeRefreshLayout != null && !mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });
        }
    }

    private void parseServerResponse(JSONObject jsonObject) {
        if (jsonObject != null) {
            int status = jsonObject.optInt(KEY_STATUS);
            if (200 == status) {
                JSONArray jsonArray = jsonObject.optJSONArray(KEY_DATA);
                if (jsonArray != null && mImageListRecyclerViewAdapter != null) {
                    ArrayList<DataModel> stringList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject dataJsonObject = jsonArray.optJSONObject(i);
                        if (dataJsonObject != null) {
                            DataModel dataModel = new DataModel(dataJsonObject);
                            stringList.add(dataModel);
                        }
                    }
                    DbActionsIntentService.startActionSaveFiles(getContext(), stringList);
                }
            }
        }
        getImageListFromLocalDB("");
        hideProgress();
    }

    private void hideProgress() {
        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String query = args.getString(KEY_QUERY);
        return new CursorLoader(
                getContext(),
                TrollMakerContract.FilePaths.CONTENT_URI,
                null,
                TextUtils.isEmpty(query) ? null : TrollMakerContract.FilePaths.COLUMN_DESCRIPTION + " LIKE ?",
                TextUtils.isEmpty(query) ? null : new String[]{"%" + query + "%"},
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        loadDataFromCursor(data);
    }

    private void loadDataFromCursor(Cursor data) {
        ArrayList<Object> filePaths = new ArrayList<>();
        if (data != null) {
            if (data.moveToFirst()) {
                do {
                    filePaths.add(data.getString(data.getColumnIndex(TrollMakerContract.FilePaths.COLUMN_FILE_PATH)));
                } while (data.moveToNext());
            }
        }
        if (mImageListRecyclerViewAdapter != null) {
            if (mLayoutManager != null) {
                if (filePaths.size() > 0) {
                    mLayoutManager.setSpanCount(2);
                } else {
                    mLayoutManager.setSpanCount(1);
                }
            }
            mImageListRecyclerViewAdapter.setImageEndUrls(filePaths);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            parseServerResponse(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        getImageList();
    }
}
