package com.StrivingRookies.superalbum.ui_menu;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.StrivingRookies.superalbum.SuperAlbum;
import com.StrivingRookies.superalbum.ui.EasyPhotosActivity;

public class Fragment_test extends Fragment {
    private Activity mCtx;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate ( R.layout.fragment_test,container,false );
        final ImageView image=v.findViewById ( R.id.imageView2 );
        image.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                SuperAlbum.createAlbum(mCtx, true, GlideEngine.getInstance())
                        .setFileProviderAuthority("com.StrivingRookies.superalbum.ui_menu.fileprovider")
                        .start(101);
            }
        } );


        return v;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCtx = activity;//mCtx 是成员变量，上下文引用
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCtx = null;
    }
}
