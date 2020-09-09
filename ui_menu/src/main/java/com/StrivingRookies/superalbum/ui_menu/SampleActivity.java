package com.StrivingRookies.superalbum.ui_menu;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.StrivingRookies.superalbum.SuperAlbum;
import com.StrivingRookies.superalbum.constant.Code;
import com.StrivingRookies.superalbum.models.album.AlbumModel;
import com.StrivingRookies.superalbum.models.album.entity.Album;
import com.StrivingRookies.superalbum.ui.EasyPhotosActivity;
import com.StrivingRookies.superalbum.ui.PreviewActivity;
import com.StrivingRookies.superalbum.utils.bitmap.BitmapUtils;
import com.StrivingRookies.superalbum.utils.bitmap.SaveBitmapCallBack;
import com.StrivingRookies.superalbum.utils.file.FileUtils;
import com.StrivingRookies.superalbum.utils.media.MediaScannerConnectionUtils;
import com.StrivingRookies.superalbum.utils.uri.UriUtils;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.ListObjectsRequest;
import com.alibaba.sdk.android.oss.model.ListObjectsResult;
import com.alibaba.sdk.android.oss.model.OSSObjectSummary;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.StrivingRookies.superalbum.callback.PuzzleCallback;
import com.StrivingRookies.superalbum.callback.SelectCallback;
import com.StrivingRookies.superalbum.constant.Type;
import com.StrivingRookies.superalbum.models.album.entity.Photo;
import com.StrivingRookies.superalbum.setting.Setting;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import cn.hzw.doodle.DoodleActivity;
import cn.hzw.doodle.DoodleParams;

import static com.StrivingRookies.superalbum.ui.EasyPhotosActivity.istrashEmpty;


public class SampleActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * 选择的图片集
     */
    public static ArrayList<Photo> selectedPhotoList = new ArrayList<>();
    public static String userName;
    private MainAdapter adapter;
    private RecyclerView rvImage;

    /**
     * 图片列表和专辑项目列表的广告view
     */
    private RelativeLayout photosAdView, albumItemsAdView;

    /**
     * 广告是否加载完成
     */
    private boolean photosAdLoaded = false, albumItemsAdLoaded = false;

    /**
     * 展示bitmap功能的
     */
    private Bitmap bitmap = null;
    private ImageView bitmapView = null;
    private DrawerLayout drawer;
    public static int n=0;
    public static Fragment fa;
    private FragmentManager ft=getFragmentManager ();
    public static int timeflag=0;
    public AlbumModel albumModel;
    public Test_ui test_ui;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        initView();
        SuperAlbum.createAlbum(this, true, GlideEngine.getInstance())
                .setFileProviderAuthority("com.StrivingRookies.superalbum.ui_menu.fileprovider")
                .setCount(9)
                .setVideo(true)
                .setGif(true);
        fa=new Test_ui ();
        FragmentTransaction ff=ft.beginTransaction ();
        ff.add (R.id.contain_test,fa,"Test_ui");
        ff.commit ();







//
//        rvImage.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//
//                if (recyclerView != null && recyclerView.getChildCount() > 0) {
//                    try {
//                        n = ((RecyclerView.LayoutParams) recyclerView.getChildAt(0).getLayoutParams()).getViewAdapterPosition();
//                        Toast.makeText(SampleActivity.this,""+n, Toast.LENGTH_SHORT).show();
//
//
//                    } catch (Exception e) {
//                    }
//                }
//
//            }
//        });
    }




    private void initView() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar ().setDisplayShowTitleEnabled (false );
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //drawer.openDrawer(GravityCompat.START);
        drawer.clearAnimation();
        drawer.setAnimation(null);
        drawer.setLayoutAnimation(null);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView=navigationView.getHeaderView ( 0 );
        TextView t=(TextView)headerView.findViewById ( R.id.head_user );
        t.setText("用户："+getIntent ().getStringExtra ( "uname" ));
        userName=getIntent ().getStringExtra ( "uname" );
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.clearAnimation();
        navigationView.setAnimation(null);
        navigationView.setLayoutAnimation(null);





//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        //adapter = new MainAdapter(this, selectedPhotoList);
//        rvImage.setLayoutManager(linearLayoutManager);
        //rvImage.setAdapter(adapter);
//        SnapHelper snapHelper = new PagerSnapHelper();
//        snapHelper.attachToRecyclerView(rvImage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sample, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SampleFragments.class);
            startActivity(intent);
        }else if(id==R.id.aboutapp){
            Intent intent = new Intent(this, AboutApp.class);
            startActivity(intent);
        }else if(id==R.id.smart_photo){
            if(fa.getClass ()==Fragment_smartphoto.class){;}


            else{selectedPhotoList.clear ();


                SuperAlbum.createAlbum ( this, false, GlideEngine.getInstance () )
                        .setFileProviderAuthority ( "com.StrivingRookies.superalbum.ui_menu.fileprovider" )
                        .setCount ( 9 )
                        .setVideo ( false )
                        .setGif ( false );

                fa = Fragment_smartphoto.getInstance ();

                FragmentTransaction ff12=ft.beginTransaction ();

                ff12.replace ( R.id.contain_test, fa, "Fragment_smartphoto" );
                ff12.commit ();
                selectedPhotoList.clear ();



            }
        }
        else if(id==R.id.phone_photo)
        {
            timeflag=0;
            selectedPhotoList.clear ();
            //手机相册
            if(fa.getClass ()==Test_ui.class){if(((Test_ui) fa).hasDecoration ()>0){((Test_ui) fa).clearTimeView ();((Test_ui) fa).setView2 ();((Test_ui) fa).setIvCameraYes ();}}

            else{

            SuperAlbum.createAlbum ( this, true, GlideEngine.getInstance () )
                    .setFileProviderAuthority ( "com.StrivingRookies.superalbum.ui_menu.fileprovider" )
                    .setCount ( 9 )
                    .setVideo ( true )
                    .setGif ( true );

             fa = Test_ui.getInstance ();


             FragmentTransaction ff5=ft.beginTransaction ();

            ff5.replace ( R.id.contain_test, fa, "Test_ui2" );
            ff5.commit ();




        }
        }else if(id==R.id.gallery){//画廊
            if(fa.getClass ()==Fragment_CoverFlow.class){;}
            else{
                fa = new Fragment_CoverFlow ();
                FragmentTransaction ff5=ft.beginTransaction ();
                ff5.replace ( R.id.contain_test, fa, "coverflow" );
                ff5.commit ();
            }
        }else if(id==R.id.time_photo) {//时光
            timeflag=1;

            if(fa.getClass ()==Test_ui.class){
                fa=(Test_ui)fa;
                ((Test_ui) fa).setIvCameraNo ();
                ((Test_ui) fa).clearTimeView ();
                ((Test_ui) fa).initTimeView ();
                ((Test_ui) fa).setView1();
                //((Test_ui) fa).setView2 ();
                }
                else {

                    SuperAlbum.createAlbum ( this, false, GlideEngine.getInstance () )
                            .setFileProviderAuthority ( "com.StrivingRookies.superalbum.ui_menu.fileprovider" )
                            .setCount ( 9 )
                            .setVideo ( true )
                            .setGif ( true );

                    fa = new Test_ui ();
                    FragmentTransaction ff5=ft.beginTransaction ();
                    ff5.replace ( R.id.contain_test, fa, "TimeAlbum" );
                    ff5.commit ();


                }
        }
        return id==R.id.time_photo||id==R.id.gallery||id==R.id.phone_photo||id == R.id.aboutapp||id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //MessgFragment.bitmapView.setVisibility(View.GONE);

        int id = item.getItemId();
        switch (id) {

            case R.id.upLoadpic:

                SuperAlbum.createAlbum(this, false, GlideEngine.getInstance())
                        .setFileProviderAuthority("com.StrivingRookies.superalbum.ui_menu.fileprovider")
                        .setCount(10)
                        .setVideo(false)
                        .setGif(false)
                        .setCleanMenu ( false )
                        .start((new SelectCallback() {
                            @Override
                            public void onResult(ArrayList<Photo> photos, boolean isOriginal) {
                                selectedPhotoList.clear();
                                selectedPhotoList.addAll(photos);



                                    loadHelper load=new loadHelper ( getApplicationContext () );
                                    int msize=selectedPhotoList.size ();

                                        for(int i=0;i<msize;i++) {
                                            load.uploadImage ( selectedPhotoList.get ( i ).path );
                                        }


                                selectedPhotoList.clear ();

                                Toast.makeText(SampleActivity.this,"上传成功", Toast.LENGTH_SHORT).show();


                            }
                        }));
                break;
            case R.id.LoadtoLoacl:
                loadHelper load=new loadHelper ( getApplication () );
                OSS ossClient = load.getOSSClient ( getApplication () );

                // 构造ListObjectsRequest请求
                ListObjectsRequest listObjectsRequest = new ListObjectsRequest("superalbum");
                //Delimiter 设置为 “/” 时，罗列该文件夹下的文件
                listObjectsRequest.setDelimiter("/");
                //Prefix 设为某个文件夹名，罗列以此 Prefix 开头的文件
                listObjectsRequest.setPrefix("image/");

                ListObjectsResult listing = null;
                try {
                    listing = ossClient.listObjects(listObjectsRequest);
                } catch (ClientException e) {
                    e.printStackTrace ();
                } catch (ServiceException e) {
                    e.printStackTrace ();
                }

                // 遍历所有Object:目录下的文件
                for (OSSObjectSummary objectSummary : listing.getObjectSummaries()) {
                    //key：fun/like/001.avi等，即：Bucket中存储文件的路径
                    String key = objectSummary.getKey ();
                    final String url = ossClient.presignPublicObjectURL ( "superalbum", key );
                    final String path=Environment.getExternalStorageDirectory().getAbsolutePath()+ "/" + getString( R.string.app_name);
                    try {
                        GetObjectRequest req=new GetObjectRequest ( "superalbum",key);
                        req.setxOssProcess ( "imm/tagimage" );
                        GetObjectResult result=ossClient.getObject (req );
                        InputStream s=result.getObjectContent ();
                        InputStreamReader in=new InputStreamReader ( s );
                        BufferedReader inn=new BufferedReader ( in );
                        String line,res="";
                        while ((line = inn.readLine ()) != null) {
                            res += line;
                        }
                        System.out.println (res);

                    } catch (ClientException e) {
                        e.printStackTrace ();
                    } catch (ServiceException e) {
                        e.printStackTrace ();
                    } catch (IOException e) {
                        e.printStackTrace ();
                    }

//                    try {
//                        path = ossClient.getObject ( new GetObjectRequest ("superalbum",key) ).getMetadata ().getUserMetadata ().get ( "filepath" );
//                    } catch (ClientException e) {
//                        e.printStackTrace ();
//                    } catch (ServiceException e) {
//                        e.printStackTrace ();
//                    }
//                    System.out.println ( path );

//                    new Thread ( new Runnable () {
//                        @Override
//                        public void run() {
//                            try {
//                                InputStream inputStream = new URL(url).openStream();
//                                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                                SaveBitmapCallBack callBack= new SaveBitmapCallBack () {
//                                    @Override
//                                    public void onSuccess(File file) {
//                                        System.out.println ( "save success" );
//                                    }
//
//                                    @Override
//                                    public void onIOFailed(IOException exception) {
//                                        System.out.println ( "save IOfail" );
//                                    }
//
//                                    @Override
//                                    public void onCreateDirFailed() {
//                                        System.out.println ( "save failed" );
//                                    }
//                                };
//                                BitmapUtils.saveBitmapToDir ( SampleActivity.this,path,"IMG",bitmap,true,callBack );
//
//
//                        } catch (MalformedURLException e) {
//                                e.printStackTrace ();
//                            } catch (IOException e) {
//                                e.printStackTrace ();
//                            }
//                        } }).start ();



                }
                Toast.makeText(SampleActivity.this,"同步完毕", Toast.LENGTH_SHORT).show();



                break;
            case R.id.nav_picCloud://云相册
               startActivityForResult ( new Intent ( this,CloudAlbum.class ),1111 );


                break;
            case R.id.nav_settings://设置
                startActivity ( new Intent ( this,AboutApp.class ) );
                break;
            case  R.id.nav_picvideo://幻灯片
                selectedPhotoList.clear ();
                fa = new Fragment_player ();
                FragmentTransaction ff5=ft.beginTransaction ();
                ff5.replace ( R.id.contain_test, fa, "pic_player" );
                ff5.commit ();


                SuperAlbum.createAlbum(this, true, GlideEngine.getInstance())
                        .setFileProviderAuthority("com.StrivingRookies.superalbum.ui_menu.fileprovider")
                        .setCount(50)
                        .setVideo(true)
                        .setGif(true)
                        .start((new SelectCallback() {
                            @Override
                            public void onResult(ArrayList<Photo> photos, boolean isOriginal) {
                                selectedPhotoList.clear();
                                selectedPhotoList.addAll(photos);
                                Fragment_player.notifydatasetchanged ();
                                if(AlbumModel.instance.album.isEmpty ())
                                {
                                    System.out.println ( "相册为快快快快快" );
                                }



                            }
                        }));



                break;
            case R.id.delete://删除当前图片

                if(selectedPhotoList.size ()==0||fa.getClass ()!=MessgFragment.class){Toast.makeText(SampleActivity.this,"请先选择图片", Toast.LENGTH_SHORT).show();}
                else if(selectedPhotoList.get ( n).type=="video/mp4"){Toast.makeText(SampleActivity.this,"不能对视频进行删除操作", Toast.LENGTH_SHORT).show();}
                else {
                    if(MessgFragment.bitmapView.getVisibility ()==View.VISIBLE){MessgFragment.bitmapView.setVisibility ( View.GONE );}
                    FileUtils.deleteUri ( SampleActivity.this, selectedPhotoList.get ( n ).uri );
                    selectedPhotoList.remove ( n );
                    MessgFragment.adapter.notifyDataSetChanged ();
                    if (n < selectedPhotoList.size ()) {
                        MessgFragment.rvImage.smoothScrollToPosition ( n );
                    } else if (n > 0) {
                        MessgFragment.rvImage.smoothScrollToPosition ( n - 1 );
                    } //else {
//                        selectedPhotoList.clear ();
//                        MessgFragment.adapter.notifyDataSetChanged ();
//                    }
                }

                break;

            case R.id.nav_trash://回收站
                if(istrashEmpty)
                {
                    Toast.makeText(this,"回收站为空",Toast.LENGTH_SHORT).show();;
                }

                else {
                    selectedPhotoList.clear ();

                    SuperAlbum.createAlbum ( this, false, GlideEngine.getInstance () )
                            .setFileProviderAuthority ( "com.StrivingRookies.superalbum.ui_menu.fileprovider" )
                            .setCount ( 50 )
                            .setVideo ( true )
                            .setGif ( true )
                            .setCount ( 15 )
                            .start ( 1110 );
                }
                break;

            case R.id.camera://单独使用相机

                fa=new MessgFragment ();
                FragmentTransaction ff4=ft.beginTransaction ();

                ff4.replace ( R.id.contain_test,fa ,"messg");
                ff4.commit ();

                SuperAlbum.createCamera(this)
                        .setFileProviderAuthority("com.StrivingRookies.superalbum.ui_menu.fileprovider")
                        .start((new SelectCallback() {
                            @Override
                            public void onResult(ArrayList<Photo> photos, boolean isOriginal) {
                                selectedPhotoList.clear();
                                selectedPhotoList.addAll(photos);
                                MessgFragment.adapter.notifyDataSetChanged ();

                            }
                        }));

                n=0;

                break;



            case R.id.album_has_video_gif://相册中显示视频和gif图 图片信息
                selectedPhotoList.clear ();
                fa=new MessgFragment ();
                FragmentTransaction ff=ft.beginTransaction ();
                ff.replace ( R.id.contain_test,fa ,"messg1");
                ff.commit ();

                SuperAlbum.createAlbum(this, true, GlideEngine.getInstance())
                        .setFileProviderAuthority("com.StrivingRookies.superalbum.ui_menu.fileprovider")
                        .setCount(9)
                        .setVideo(true)
                        .setGif(true)
                        .start(101);
                n=0;
                break;





            case R.id.addWatermark: //给图片添加水印
                if(fa.getClass ()!=MessgFragment.class)
                {
                    Toast.makeText(this, "此处不能添加水印", Toast.LENGTH_SHORT).show();

                }else if(selectedPhotoList.size ()==0){Toast.makeText(SampleActivity.this,"请先选择图片", Toast.LENGTH_SHORT).show();}
                else if(selectedPhotoList.get ( n).type.contains ( Type.VIDEO )){Toast.makeText(SampleActivity.this,"不能对视频进行操作", Toast.LENGTH_SHORT).show();

                }
                else if (selectedPhotoList.isEmpty()) {
                    Toast.makeText(this, "没选图片", Toast.LENGTH_SHORT).show();

                }else {

                    //这一步如果图大的话会耗时，但耗时不长，你可以在异步操作。另外copy出来的bitmap在确定不用的时候记得回收，如果你用Glide操作过copy出来的bitmap那就不要回收了，否则Glide会报错。
                    Bitmap watermark = BitmapFactory.decodeResource ( getResources (), R.drawable.watermark ).copy ( Bitmap.Config.RGB_565, true );
                    try {
                        bitmap = BitmapFactory.decodeStream ( getContentResolver ().openInputStream ( selectedPhotoList.get ( n ).uri ) ).copy ( Bitmap.Config.ARGB_8888, true );
                    } catch (FileNotFoundException e) {
                        e.printStackTrace ();
                    }
                    //给图片添加水印的api
                    SuperAlbum.addWatermark ( this, watermark, bitmap, 1080, 20, 20, true );

                    MessgFragment.bitmapView.setVisibility ( View.VISIBLE );
                    MessgFragment.bitmapView.setImageBitmap ( bitmap );
                    Toast.makeText ( SampleActivity.this, "水印在左下角", Toast.LENGTH_SHORT ).show ();
                }

                break;

            case R.id.puzzle://拼图
                 fa=new MessgFragment ();
                FragmentTransaction ff1=ft.beginTransaction ();

                ff1.replace ( R.id.contain_test,fa ,"messg3");
                ff1.commit ();
                SuperAlbum.createAlbum(this, false, GlideEngine.getInstance())
                        .setCount(9)
                        .setPuzzleMenu(false)
                        .setFileProviderAuthority("com.StrivingRookies.superalbum.ui_menu.fileprovider")
                        .start(new SelectCallback() {
                            @Override
                            public void onResult(ArrayList<Photo> photos, boolean isOriginal) {
                                SuperAlbum.startPuzzleWithPhotos(SampleActivity.this, photos, Environment.getExternalStorageDirectory().getAbsolutePath()+ "/" + getString( R.string.app_name), "IMG", false, GlideEngine.getInstance(), new PuzzleCallback() {
                                    @Override
                                    public void onResult(Photo photo) {
                                        selectedPhotoList.clear();
                                        selectedPhotoList.add(photo);
                                        MessgFragment.adapter.notifyDataSetChanged();
                                        MessgFragment.rvImage.smoothScrollToPosition(0);
                                    }
                                });
                            }
                        });
                break;
            case R.id.doodle://涂鸦
                selectedPhotoList.clear ();
                fa=new MessgFragment ();
                FragmentTransaction fe=ft.beginTransaction ();
                fe.replace ( R.id.contain_test,fa ,"messg1");
                fe.commit ();

                SuperAlbum.createAlbum(this, true, GlideEngine.getInstance())
                        .setFileProviderAuthority("com.StrivingRookies.superalbum.ui_menu.fileprovider")
                        .setCount(1)
                        .setVideo(false)
                        .setGif(false)
                        .start(new SelectCallback() {
                            @Override
                            public void onResult(ArrayList<Photo> photos, boolean isOriginal) {
                                DoodleParams params = new DoodleParams(); // 涂鸦参数
//                                params.mSavePath=Environment.getExternalStorageDirectory().getAbsolutePath()+ "/" + getString( R.string.app_name);
                                params.mImagePath = photos.get ( 0 ).path; // the file path of image

                                DoodleActivity.startActivityForResult(SampleActivity.this, params, 120);


                            }


                        });




            default:
                break;

        }

        return true;
    }

    /**
     * 需要在启动前创建广告view
     * 广告view不能有父布局
     * 广告view可以包含子布局
     * 为了确保广告view地址不变，设置final会更安全
     */

    private void initAdViews() {

        //模拟启动EasyPhotos前广告已经装载完毕
        initPhotosAd();

        //模拟不确定启动EasyPhotos前广告是否装载完毕
        initAlbumItemsAd();

    }

    /**
     * 模拟启动EasyPhotos前广告已经装载完毕
     */
    private void initPhotosAd() {
        photosAdView = (RelativeLayout) getLayoutInflater().inflate(R.layout.ad_photos, null, false);//不可以有父布局，所以inflate第二个参数必须为null，并且布局文件必须独立
        ((TextView) photosAdView.findViewById(R.id.tv_title)).setText("photosAd广告");
        ((TextView) photosAdView.findViewById(R.id.tv_content)).setText("超级相册，你值得拥有！");
        photosAdLoaded = true;
    }

    /**
     * 模拟不确定启动EasyPhotos前广告是否装载完毕
     * 模拟5秒后网络回调
     */
    private void initAlbumItemsAd() {
        albumItemsAdView = (RelativeLayout) getLayoutInflater().inflate(R.layout.ad_album_items, null, false);//不可以有父布局，所以inflate第二个参数必须为null，并且布局文件必须独立

        //模拟5秒后网络回调
        rvImage.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((ImageView) albumItemsAdView.findViewById(R.id.iv_image)).setImageResource(R.mipmap.ad);
                ((TextView) albumItemsAdView.findViewById(R.id.tv_title)).setText("albumItemsAd广告");
                photosAdLoaded = true;//正常情况可能不知道是先启动EasyPhotos还是数据先回来，所以这里加个标识，如果是后启动EasyPhotos，那么EasyPhotos会直接加载广告
                SuperAlbum.notifyAlbumItemsAdLoaded();
            }
        }, 5000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (RESULT_OK == resultCode) {
            //相机或相册回调
            if (requestCode == 101) {
                //返回对象集合：如果你需要了解图片的宽、高、大小、用户是否选中原图选项等信息，可以用这个
                ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra( SuperAlbum.RESULT_PHOTOS);

                //返回图片地址集合时如果你需要知道用户选择图片时是否选择了原图选项，用如下方法获取
                boolean selectedOriginal = data.getBooleanExtra( SuperAlbum.RESULT_SELECTED_ORIGINAL, false);


                selectedPhotoList.clear();
                selectedPhotoList.addAll(resultPhotos);
                MessgFragment.adapter.notifyDataSetChanged();
                MessgFragment.rvImage.smoothScrollToPosition(0);

                return;
            }

            //为拼图选择照片的回调
            else if (requestCode == 102) {

                ArrayList<Photo> resultPhotos =
                        data.getParcelableArrayListExtra( SuperAlbum.RESULT_PHOTOS);
                if (resultPhotos.size() == 1) {
                    resultPhotos.add(resultPhotos.get(0));
                }
                selectedPhotoList.clear();
                selectedPhotoList.addAll(resultPhotos);

                SuperAlbum.startPuzzleWithPhotos(this, selectedPhotoList, Environment.getExternalStorageDirectory().getAbsolutePath(), "AlbumBuilder", 103, false, GlideEngine.getInstance());
                return;
            }

            //拼图回调
            else if (requestCode == 103) {
                Photo puzzlePhoto = data.getParcelableExtra( SuperAlbum.RESULT_PHOTOS);
                selectedPhotoList.clear();
                selectedPhotoList.add(puzzlePhoto);
                adapter.notifyDataSetChanged();
                rvImage.smoothScrollToPosition(0);
            }
            //涂鸦回调
            else if(requestCode==120)
            {

                String path=data.getStringExtra ( "key_image_path" );
                System.out.println ( path );
                MediaScannerConnectionUtils.refresh ( this,path );
                Photo photo=getPhoto ( UriUtils.getUriByPath ( this,path ) );
                selectedPhotoList.clear();
                selectedPhotoList.add(photo);
                MessgFragment.adapter.notifyDataSetChanged();
                MessgFragment.rvImage.smoothScrollToPosition(0);
            }
            else if (requestCode == 1110) {

                    System.out.println ( "回收站回调" );
                    initView();
                    SuperAlbum.createAlbum(SampleActivity.this, true, GlideEngine.getInstance())
                            .setFileProviderAuthority("com.StrivingRookies.superalbum.ui_menu.fileprovider")
                            .setCount(9)
                            .setVideo(true)
                            .setGif(true);
                    fa=new Test_ui ();
                    FragmentTransaction ff=ft.beginTransaction ();
                    ff.replace (R.id.contain_test,fa,"Test_ui");
                    ff.commit ();


                if (AlbumModel.instance.album.isEmpty ())
                {
                    System.out.println ( "相册为空" );
                }






            }



        } else if (RESULT_CANCELED == resultCode) {
            //回收站回调
            if (requestCode == 1110) {
                System.out.println ( "回收站回调" );


                SuperAlbum.createAlbum(SampleActivity.this, true, GlideEngine.getInstance())
                        .setFileProviderAuthority("com.StrivingRookies.superalbum.ui_menu.fileprovider")
                        .setCount(9)
                        .setVideo(true)
                        .setGif(true);
                fa=new Test_ui ();
                FragmentTransaction ff=ft.beginTransaction ();
                ff.replace (R.id.contain_test,fa,"Test_ui");
                ff.commit ();

            }else if (requestCode == 1111) {
                System.out.println ( "云相册回调" );

                SuperAlbum.createAlbum(SampleActivity.this, true, GlideEngine.getInstance())
                        .setFileProviderAuthority("com.StrivingRookies.superalbum.ui_menu.fileprovider")
                        .setCount(9)
                        .setVideo(true)
                        .setGif(true);
                fa=new Test_ui ();
                FragmentTransaction ff=ft.beginTransaction ();
                ff.replace (R.id.contain_test,fa,"Test_ui");
                ff.commit ();

            }
            //Toast.makeText(this, "cancel", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();

    }
    public void resetAlbum()
    {
        AlbumModel.CallBack albumModelCallBack = new AlbumModel.CallBack() {
            @Override
            public void onAlbumWorkedCallBack() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (albumModel.album.isEmpty ()) {

                            System.out.println ( "album空" );
                        }
                    }
                });
            }
        };
        albumModel = AlbumModel.getInstance();
        albumModel.query(this, albumModelCallBack);
    }
    private  Photo getPhoto(Uri uri) {
        Photo p = null;
        String path;
        String name;
        long dateTime;
        String type;
        long size;
        int width = 0;
        int height = 0;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            return null;
        }
        if (cursor.moveToFirst()) {
            path = cursor.getString(cursor.getColumnIndex( MediaStore.MediaColumns.DATA));
            name = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME));
            dateTime = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED));
            type = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE));
            size = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE));
            width = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns.WIDTH));
            height = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns.HEIGHT));
            p = new Photo(name, uri, path, dateTime, width, height, size, 0, type);
        }
        cursor.close();

        return p;
    }

}
