package com.StrivingRookies.superalbum.ui_menu;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.StrivingRookies.superalbum.SuperAlbum;
import com.StrivingRookies.superalbum.constant.Code;
import com.StrivingRookies.superalbum.models.album.AlbumModel;
import com.StrivingRookies.superalbum.models.album.entity.Photo;
import com.StrivingRookies.superalbum.setting.Setting;
import com.StrivingRookies.superalbum.utils.media.MediaScannerConnectionUtils;

import java.util.ArrayList;

import at.technikum.mti.fancycoverflow.FancyCoverFlow;
import at.technikum.mti.fancycoverflow.FancyCoverFlowAdapter;

public class Fragment_CoverFlow extends Fragment {
    private Activity mCtx;
    private FancyCoverFlow fancycoverflow;
    public ArrayList<Photo> photoList = new ArrayList<>();

    private AlbumModel albumModel;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate ( R.layout.fragment_3dgallery,container,false );



        AlbumModel.CallBack albumModelCallBack = new AlbumModel.CallBack() {
            @Override
            public void onAlbumWorkedCallBack() {
                getActivity ().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(albumModel.album.isEmpty ()){
                            System.out.println ( "fancy:album为空" );
                        }
                    }
                });
            }
        };
        albumModel = AlbumModel.getInstance();
        albumModel.query(getActivity (), albumModelCallBack);
        photoList.clear ();
        photoList.addAll(albumModel.getCurrAlbumItemPhotos(0));






        fancycoverflow=(FancyCoverFlow)v.findViewById ( R.id.fancy_coverflow );
        FancyCoverFlowSampleAdapter adapter=new FancyCoverFlowSampleAdapter ();
        fancycoverflow.setAdapter ( adapter );

        fancycoverflow.setUnselectedAlpha(1.0f);//未选择的Item透明度
        fancycoverflow.setUnselectedSaturation(0.0f);//未选中的饱和度
        fancycoverflow.setUnselectedScale(0.5f);//未选中的缩放
        fancycoverflow.setSpacing(1);//设置Item之间间隙
        //fancycoverflow.setBackgroundColor ( Color.BLACK );
        fancycoverflow.setMaxRotation(20);//设置最大的旋转角
        fancycoverflow.setScaleDownGravity(0.3f);//从哪个位置进行缩放
        fancycoverflow.setReflectionEnabled(true);
        fancycoverflow.setReflectionRatio(0.3f);//倒影的比例
        fancycoverflow.setReflectionGap(40);//倒影和原图的间距
        fancycoverflow.setActionDistance(FancyCoverFlow.ACTION_DISTANCE_AUTO);




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
    public void destroy(){
        super.onDestroy ();
        if(albumModel!=null) albumModel.stopQuery ();
    }


    public  class FancyCoverFlowSampleAdapter extends FancyCoverFlowAdapter {


        private int[] images = {R.drawable.face2, R.drawable.face1, R.drawable.app_face};



        @Override
        public int getCount() {
            return photoList.size ();
        }

        @Override
        public Integer getItem(int i) {
            return images[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getCoverFlowItem(int i, View reuseableView, ViewGroup viewGroup) {
            ImageView imageView = null;

            if (reuseableView != null) {
                imageView = (ImageView) reuseableView;
            } else {
                imageView = new ImageView ( viewGroup.getContext () );
                imageView.setLayoutParams ( new FancyCoverFlow.LayoutParams ( 800, 1200 ) );
                imageView.setScaleType ( ImageView.ScaleType.FIT_XY);




            }

            imageView.setImageURI ( photoList.get ( i ).uri );
            return imageView;
        }
    }
}



