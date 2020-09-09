package com.StrivingRookies.superalbum.ui_menu;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.media.MediaPlayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.stx.xhb.xbanner.XBanner;
import com.stx.xhb.xbanner.transformers.Transformer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Fragment_player extends Fragment {
    private static List<String> imagUrl= new ArrayList<>();
    private Activity mCtx;
    private XBanner.XBannerAdapter xBannerAdapter;
    private static XBanner xBanner;
    private FloatingActionButton playButton;
    private MediaPlayer mediaPlayer;
    private int playFlag=1;


//    private void play() {
//        try {
//            mediaPlayer.reset ();
//            mediaPlayer.setDataSource ( String.valueOf ( R.raw.softmusic ) );//重新设置要播放的音频
//            mediaPlayer.prepare ();//预加载音频
//            mediaPlayer.start ();//开始播放
//
//        } catch (Exception e) {
//            e.printStackTrace ();
//        }
//    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate ( R.layout.fragment_player,container,false );

         xBanner=v.findViewById ( R.id.xbanner );
         playButton=v.findViewById ( R.id.playButton );
         playButton.setImageResource ( R.drawable.play0 );
         playButton.setVisibility ( View.GONE );
         mediaPlayer=MediaPlayer.create ( getActivity (),R.raw.softmusic );

        mediaPlayer.setLooping ( true );
//            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener () {
//                @Override
//                public void onCompletion(MediaPlayer mp) {
//                    play();//重新开始播放
//                }
//            });

         mediaPlayer.start ();
        for (int i=0;i<SampleActivity.selectedPhotoList.size ();i++)
        {
            imagUrl.add( SampleActivity.selectedPhotoList.get ( i ).uri.toString () );
        }


        xBanner.setData (imagUrl ,null);
        xBanner.setOnItemClickListener ( new XBanner.OnItemClickListener () {
            @Override
            public void onItemClick(XBanner banner, Object model, View view, int position) {
                if(playFlag==1)
                {
                    xBanner.stopAutoPlay ();
                    mediaPlayer.pause ();
                    playFlag=0;
                    playButton.setVisibility ( View.VISIBLE );
                }else if(playFlag==0){
                    xBanner.startAutoPlay ();
                    mediaPlayer.start ();
                    playFlag=1;
                    playButton.setVisibility ( View.GONE );
                }
            }
        } );
        //加载图片

        xBannerAdapter=new XBanner.XBannerAdapter() {




            @Override
            public void loadBanner(XBanner banner, Object model, View view, int position) {

                //1、此处使用的Glide加载图片，可自行替换自己项目中的图片加载框架
                //2、返回的图片路径为Object类型，你只需要强转成你传输的类型就行，切记不要胡乱强转！
                view.setTag ( null );
                Glide.with(getActivity ()).load(Uri.parse ( (imagUrl.get ( position )))).into((ImageView)view);
                view.setTag ( imagUrl.get ( position ) );
            }


        };
        xBanner.loadImage(xBannerAdapter);

        xBanner.setPageTransformer ( Transformer.Accordion );
        Spinner spinner=v.findViewById ( R.id.spinner2 );
        Spinner spinner1=v.findViewById ( R.id.spinner );
        String[] arr1={"轻快","浪漫","安静"};
        String[] arr={"横向移动","渐变","单页旋转","立体旋转","反转","三角换页","缩放1","缩放2","缩放3","左移1","左移2","左移3","左移4"};
        final ArrayAdapter<String> adapter=new ArrayAdapter<>( getActivity (),android.R.layout.simple_spinner_item,arr);
        final ArrayAdapter<String> adapter1=new ArrayAdapter<>( getActivity (),android.R.layout.simple_spinner_item,arr1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter ( adapter1 );
        spinner.setAdapter ( adapter );
        spinner1.setOnItemSelectedListener ( new AdapterView.OnItemSelectedListener () {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selected=parent.getSelectedItemPosition ();
                switch (selected)
                {
                    case 0:
                        mediaPlayer.release ();
                        mediaPlayer=MediaPlayer.create ( getActivity (),R.raw.softmusic );
                        mediaPlayer.start ();
                        break;
                    case 1:
                        mediaPlayer.release ();
                        mediaPlayer=MediaPlayer.create ( getActivity (),R.raw.romanmusic );
                        mediaPlayer.start ();
                        break;
                    case 2:
                        mediaPlayer.release ();
                        mediaPlayer=MediaPlayer.create ( getActivity (),R.raw.quietmusic );
                        mediaPlayer.start ();

                    default:return;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        } );
        spinner.setOnItemSelectedListener ( new AdapterView.OnItemSelectedListener () {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selected=parent.getSelectedItemPosition ();
                switch (selected)
                {
                    case 1-1:
                        xBanner.setData ( imagUrl,null );
                        xBanner.setPageTransformer(Transformer.Default);//横向移动
                        break;
                    case 2-1:
                        xBanner.setData ( imagUrl,null );
                        xBanner.setPageTransformer(Transformer.Alpha); //渐变，效果不明显
                        break;
                    case 3-1:
                        xBanner.setData ( imagUrl,null );
                        xBanner.setPageTransformer(Transformer.Rotate);  //单页旋转
                        break;
                    case 4-1:
                        xBanner.setData ( imagUrl,null );

                        xBanner.setPageTransformer(Transformer.Cube);    //立体旋转
                        break;
                    case 5-1:
                        xBanner.setData ( imagUrl,null );

                        xBanner.setPageTransformer(Transformer.Flip);  // 反转效果
                        break;
                    case 6-1:
                        xBanner.setData ( imagUrl,null );

                        xBanner.setPageTransformer(Transformer.Accordion); //三角换页
                        break;
                    case 7-1:
                        xBanner.setData ( imagUrl,null );

                        xBanner.setPageTransformer(Transformer.ZoomFade); // 缩小本页，同时放大另一页
                        break;
                    case 8-1:
                        xBanner.setData ( imagUrl,null );

                        xBanner.setPageTransformer(Transformer.ZoomCenter); //本页缩小一点，另一页就放大
                        break;
                    case 9-1:
                        xBanner.setData ( imagUrl,null );

                        xBanner.setPageTransformer(Transformer.ZoomStack); // 本页和下页同事缩小和放大
                        break;
                    case 10-1:
                        xBanner.setData ( imagUrl,null );

                        xBanner.setPageTransformer(Transformer.Stack);  //本页和下页同时左移
                        break;
                    case 11-1:
                        xBanner.setData ( imagUrl,null );

                        xBanner.setPageTransformer(Transformer.Depth);  //本页左移，下页从后面出来
                        break;
                    case 12-1:
                        xBanner.setData ( imagUrl,null );

                        xBanner.setPageTransformer(Transformer.Zoom);  //本页刚左移，下页就在后面
                    default:
                        return;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


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




    public static void notifydatasetchanged()
    {
        imagUrl.clear ();
        for (int i=0;i<SampleActivity.selectedPhotoList.size ();i++)
        {
            imagUrl.add( SampleActivity.selectedPhotoList.get ( i ).uri.toString () );
        }
        xBanner.setData ( imagUrl,null );
        xBanner.setAutoPlayAble ( true );
    }
    @Override
    public void onResume() {
        super.onResume();
        xBanner.startAutoPlay();


    }

    @Override
    public void onStop() {
        super.onStop();
        xBanner.stopAutoPlay();

    }

    public void onDestroy() {

        super.onDestroy ();
        mediaPlayer.stop ();
        mediaPlayer.release ();
    }



}
