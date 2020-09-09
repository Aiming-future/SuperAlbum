package com.StrivingRookies.superalbum.ui_menu;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.StrivingRookies.superalbum.models.album.entity.Photo;

import java.util.ArrayList;

import static com.StrivingRookies.superalbum.ui_menu.SampleActivity.n;
import static com.StrivingRookies.superalbum.ui_menu.SampleActivity.selectedPhotoList;

public  class MessgFragment extends Fragment {
public static RecyclerView rvImage;

    public static MainAdapter adapter;
    public static ImageView bitmapView;
    public static ImageView del;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.content_sample, container, false);
        del=rootView.findViewById ( R.id.del_view );
        rvImage=(RecyclerView)rootView.findViewById ( R.id.rv_image );
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity (), LinearLayoutManager.HORIZONTAL, false);
        adapter = new MainAdapter(getActivity (), selectedPhotoList);
        rvImage.setLayoutManager(linearLayoutManager);
        rvImage.setAdapter(adapter);
        SnapHelper snapHelper = new PagerSnapHelper ();
        snapHelper.attachToRecyclerView(rvImage);
//        if(selectedPhotoList.size ()>0){
//            MessgFragment.del.setVisibility ( View.VISIBLE );
//        }else{
//            MessgFragment.del.setVisibility ( View.INVISIBLE );
//        }
        MessgFragment.del.setVisibility ( View.VISIBLE );
        MessgFragment.del.setOnClickListener (  new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                if(selectedPhotoList.size ()>0) {
                    selectedPhotoList.remove ( n );
                    MessgFragment.adapter.notifyDataSetChanged ();
                    if (n < selectedPhotoList.size ()) {
                        MessgFragment.rvImage.smoothScrollToPosition ( n );
                    } else if (n > 0) {
                        MessgFragment.rvImage.smoothScrollToPosition ( n - 1 );
                    } else {
                        MessgFragment.del.setVisibility ( View.INVISIBLE );
                    }
                }else{
                    del.setVisibility ( View.INVISIBLE );
                }
            }
        } );


                bitmapView = (ImageView)rootView.findViewById(R.id.iv_image);
        bitmapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bitmapView.setVisibility(View.GONE);
            }
        });


        rvImage.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (recyclerView != null && recyclerView.getChildCount() > 0) {
                    try {
                        n = ((RecyclerView.LayoutParams) recyclerView.getChildAt(0).getLayoutParams()).getViewAdapterPosition();
                        Toast.makeText(getActivity (),""+ n, Toast.LENGTH_SHORT).show();


                    } catch (Exception e) {
                    }
                }

            }
        });
        return rootView;
    }
}
