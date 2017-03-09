package rkr.binatestation.maketroll.fragments;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import rkr.binatestation.maketroll.R;
import rkr.binatestation.maketroll.adapters.MyCreationsRecyclerViewAdapter;
import rkr.binatestation.maketroll.interfaces.FbShareListener;

import static android.support.v7.widget.StaggeredGridLayoutManager.VERTICAL;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyCreationsFragment extends Fragment implements FbShareListener {

    private static final String TAG = "MyCreationsFragment";

    private MyCreationsRecyclerViewAdapter mMyCreationsRecyclerViewAdapter;

    public MyCreationsFragment() {
        // Required empty public constructor
    }

    public static MyCreationsFragment newInstance() {
        Log.d(TAG, "newInstance() called");
        Bundle args = new Bundle();

        MyCreationsFragment fragment = new MyCreationsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_creations, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.FMC_recycler_view);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, VERTICAL));
        recyclerView.setAdapter(mMyCreationsRecyclerViewAdapter = new MyCreationsRecyclerViewAdapter(getChildFragmentManager(), this));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMyCreationsRecyclerViewAdapter != null) {
            mMyCreationsRecyclerViewAdapter.setFileList(getContext());
        }
    }

    @Override
    public void shareToFacebook(Uri uri) {
        if (ShareDialog.canShow(SharePhotoContent.class)) {
            SharePhoto photo = new SharePhoto.Builder()
                    .setImageUrl(uri)
                    .build();
            SharePhotoContent content = new SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build();
            ShareDialog.show(this, content);
        }
    }

}
