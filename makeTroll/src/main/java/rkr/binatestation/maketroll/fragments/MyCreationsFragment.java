package rkr.binatestation.maketroll.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rkr.binatestation.maketroll.R;
import rkr.binatestation.maketroll.adapters.MyCreationsRecyclerViewAdapter;

import static android.support.v7.widget.StaggeredGridLayoutManager.VERTICAL;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyCreationsFragment extends Fragment {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_creations, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.FMC_recycler_view);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, VERTICAL));
        recyclerView.setAdapter(mMyCreationsRecyclerViewAdapter = new MyCreationsRecyclerViewAdapter());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMyCreationsRecyclerViewAdapter != null) {
            mMyCreationsRecyclerViewAdapter.setFileList(getContext());
        }
    }
}
