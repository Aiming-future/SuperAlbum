package com.StrivingRookies.superalbum.ui_menu;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.StrivingRookies.superalbum.SuperAlbum;
import com.StrivingRookies.superalbum.ui.adapter.PhotosAdapter;
import com.StrivingRookies.superalbum.ui.widget.PressedTextView;
import com.StrivingRookies.superalbum.utils.bitmap.BitmapUtils;
import com.StrivingRookies.superalbum.utils.bitmap.SaveBitmapCallBack;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.ListObjectsRequest;
import com.alibaba.sdk.android.oss.model.ListObjectsResult;
import com.alibaba.sdk.android.oss.model.OSSObjectSummary;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.stx.xhb.xbanner.XBanner;
import com.stx.xhb.xbanner.transformers.Transformer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.StrivingRookies.superalbum.ui_menu.SampleActivity.userName;

public class CloudAlbum extends AppCompatActivity {
    private XBanner.XBannerAdapter xBannerAdapter;
    private  XBanner xBanner;
    private  List<String> imagUrl= new ArrayList<> ();
    private ImageView back;
    private ImageView synToLocal;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView(R.layout.cloudalbum);
        xBanner=findViewById ( R.id.cloudxban );
        back=findViewById ( R.id.cloudback);
        back.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                xBanner.removeAllViews ();
                setResult ( RESULT_CANCELED);
                onBackPressed ();
                //startActivity ( new Intent ( CloudAlbum.this,SampleActivity.class ) );
            }
        } );
        synToLocal=findViewById ( R.id.syntolocal );
        synToLocal.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                int tag=xBanner.getBannerCurrentItem ();

                    final String url = imagUrl.get ( tag );
                    final String path= Environment.getExternalStorageDirectory().getAbsolutePath()+ "/" + getString( R.string.app_name);


                    new Thread ( new Runnable () {
                        @Override
                        public void run() {
                            try {
                                InputStream inputStream = new URL (url).openStream();
                                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                SaveBitmapCallBack callBack= new SaveBitmapCallBack () {
                                    @Override
                                    public void onSuccess(File file) {
                                        System.out.println ( "save success" );
                                    }

                                    @Override
                                    public void onIOFailed(IOException exception) {
                                        System.out.println ( "save IOfail" );
                                    }

                                    @Override
                                    public void onCreateDirFailed() {
                                        System.out.println ( "save failed" );
                                    }
                                };
                                BitmapUtils.saveBitmapToDir ( CloudAlbum.this,path,"IMG",bitmap,true,callBack );


                            } catch (MalformedURLException e) {
                                e.printStackTrace ();
                            } catch (IOException e) {
                                e.printStackTrace ();
                            }
                        } }).start ();




                Toast.makeText(CloudAlbum.this,"同步完毕", Toast.LENGTH_SHORT).show();
            }
        } );
        loadHelper load=new loadHelper ( getApplication () );
        OSS ossClient = load.getOSSClient ( getApplication () );

        // 构造ListObjectsRequest请求
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest("superalbum");
        //Delimiter 设置为 “/” 时，罗列该文件夹下的文件
        listObjectsRequest.setDelimiter("/");
        //Prefix 设为某个文件夹名，罗列以此 Prefix 开头的文件
        listObjectsRequest.setPrefix(userName+"/");

        ListObjectsResult listing = null;
        try {
            listing = ossClient.listObjects(listObjectsRequest);
        } catch (ClientException e) {
            e.printStackTrace ();
        } catch (ServiceException e) {
            e.printStackTrace ();
        }
        imagUrl.clear ();
        // 遍历所有Object:目录下的文件
        for (OSSObjectSummary objectSummary : listing.getObjectSummaries()) {
            //key：fun/like/001.avi等，即：Bucket中存储文件的路径
            String key = objectSummary.getKey();
            String url = ossClient.presignPublicObjectURL("superalbum", key);
            imagUrl.add ( url );
            //判断文件所在本地路径是否存在，若无，新建目录
//            File file = new File(localPath + key);
//            File fileParent = file.getParentFile();
//            if (!fileParent.exists()) {
//                fileParent.mkdirs();
//            }
//            //下载object到文件
//            try {
//                ossClient.getObject(new GetObjectRequest ("superalbum", key));
//            } catch (ClientException e) {
//                e.printStackTrace ();
//            } catch (ServiceException e) {
//                e.printStackTrace ();
//            }
        }
        System.out.println("下载完成");
        // 关闭client




        xBanner.setData (imagUrl ,null);
        xBannerAdapter=new XBanner.XBannerAdapter() {




            @Override
            public void loadBanner(XBanner banner, Object model, View view, int position) {

                //1、此处使用的Glide加载图片，可自行替换自己项目中的图片加载框架
                //2、返回的图片路径为Object类型，你只需要强转成你传输的类型就行，切记不要胡乱强转！
                //view.setTag ( null );

                Picasso.with(CloudAlbum.this).load( Uri.parse ( (imagUrl.get ( position )))).into((ImageView)view);

                //view.setTag ( imagUrl.get ( position ) );
            }


        };
        xBanner.loadImage(xBannerAdapter);



    }
    @Override
    public void onStop() {
        super.onStop();


    }


}

