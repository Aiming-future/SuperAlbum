package com.StrivingRookies.superalbum.ui_menu;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.StrivingRookies.superalbum.SuperAlbum;
import com.StrivingRookies.superalbum.R;
import com.StrivingRookies.superalbum.constant.Code;
import com.StrivingRookies.superalbum.constant.Key;
import com.StrivingRookies.superalbum.models.ad.AdListener;
import com.StrivingRookies.superalbum.models.album.AlbumModel;
import com.StrivingRookies.superalbum.models.album.entity.Photo;
import com.StrivingRookies.superalbum.result.Result;
import com.StrivingRookies.superalbum.setting.Setting;
import com.StrivingRookies.superalbum.ui.PreviewActivity;
import com.StrivingRookies.superalbum.ui.PuzzleSelectorActivity;
import com.StrivingRookies.superalbum.ui.adapter.AlbumItemsAdapter;
import com.StrivingRookies.superalbum.ui.adapter.PhotosAdapter;
import com.StrivingRookies.superalbum.ui.widget.PressedTextView;
import com.StrivingRookies.superalbum.utils.Color.ColorUtils;
import com.StrivingRookies.superalbum.utils.DateUtil;
import com.StrivingRookies.superalbum.utils.String.StringUtils;
import com.StrivingRookies.superalbum.utils.media.DurationUtils;
import com.StrivingRookies.superalbum.utils.media.MediaScannerConnectionUtils;
import com.StrivingRookies.superalbum.utils.permission.PermissionUtil;
import com.StrivingRookies.superalbum.utils.settings.SettingsUtils;
import com.StrivingRookies.superalbum.utils.system.SystemUtils;
import com.StrivingRookies.superalbum.utils.uri.UriUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.StrivingRookies.superalbum.ui_menu.SampleActivity.timeflag;


public class Test_ui extends Fragment implements AlbumItemsAdapter.OnClickListener, PhotosAdapter.OnClickListener, AdListener, View.OnClickListener {

    private File mTempImageFile;


    public static AlbumModel albumModel;
    private ArrayList<Object> photoList = new ArrayList<> ();
    private ArrayList<Object> albumItemList = new ArrayList<> ();

    private ArrayList<Photo> resultList = new ArrayList<> ();

    private RecyclerView rvPhotos;
    public static PhotosAdapter photosAdapter;
    private GridLayoutManager gridLayoutManager;

    private RecyclerView rvAlbumItems;
    private AlbumItemsAdapter albumItemsAdapter;
    private RelativeLayout rootViewAlbumItems;

    private PressedTextView tvAlbumItems, tvDone, tvPreview;
    private TextView tvOriginal;
    private AnimatorSet setHide;
    private AnimatorSet setShow;

    private int currAlbumItemIndex = 0;

    private ImageView ivCamera;
    private TextView tvTitle;

    private LinearLayout mSecondMenus;

    private RelativeLayout permissionView;
    private TextView tvPermission;
    private View mBottomBar;
    public View rootview;
    private ActionBar supportActionBar;
    private Activity mCtx;
    public static Test_ui instance;
    private ArrayList<Integer> span=new ArrayList<> (  );
    private ArrayList<Integer> spanIndex=new ArrayList<> (  );

    public static Test_ui getInstance()
    {
        if (null == instance) {

            synchronized (Test_ui.class) {
                int a = 2;
                if(null==instance)
                {instance = new Test_ui ();}

            }


        }
        return instance;
    }

    private TimeDecoration decoration=new TimeDecoration(this,3, new TimeDecoration.DecorationCallback() {
        @Override
        public String getGroupId(int position) {
            return DateUtil.stampToDate ( ((Photo) photoList.get ( position )).time );
        }

        @Override
        public String getGroupFirstLine(int position) {

            return DateUtil.stampToDate ( ((Photo) photoList.get ( position )).time );
        }
    });
    public void initSpan()
    {
        String s1,s2;
        int l=photoList.size ();
        span.add ( 1 );//表示有头
        spanIndex.add ( 0 );
        for(int i=0;i<l-1;i++)
        {
            s1=DateUtil.stampToDate ( ((Photo) photoList.get ( i )).time );
            s2=DateUtil.stampToDate ( ((Photo) photoList.get ( i+1 )).time );
            if(s1.equals ( s2 )){
                span.add ( 0 );
            }else{
                span.add ( 1 );
                spanIndex.add ( i+1 );
            }
        }

    }
    public void setView1()
    {
        initSpan ();

        GridLayoutManager g=new GridLayoutManager ( getActivity (),3 );

        g.setSpanSizeLookup ( new GridLayoutManager.SpanSizeLookup () {
            boolean f=false;
            int len=spanIndex.size ();

            @Override
            public int getSpanSize(int position) {

                for(int i=0;i<len;i++)
                {
                    if(position==spanIndex.get ( i )){
                        return 3;
                    }
                }
                return 1;
            }
        } );
        rvPhotos.setLayoutManager ( g );



    }
    public void setView2()
    {
        GridLayoutManager g=new GridLayoutManager ( getActivity (),3 );
        rvPhotos.setLayoutManager ( g );

    }



    public int hasDecoration()
    {
        return rvPhotos.getItemDecorationCount ();
    }

    public void setIvCameraYes(){

        ivCamera.setVisibility ( View.VISIBLE );
    }
    public void setIvCameraNo(){

        ivCamera.setVisibility ( View.INVISIBLE );
    }
    public void clearTimeView(){

        rvPhotos.removeItemDecoration (decoration );
    }
    public void initTimeView(){

        rvPhotos.addItemDecoration(decoration);

    }
    public void refreshdata()
    {
        photoList.clear ();
        hasPermissions();
        currAlbumItemIndex=0;
        rvAlbumItems.scrollToPosition ( 0 );

    }

    public static void start(Activity activity, int requestCode) {
        Intent intent = new Intent ( activity, Test_ui.class );
        activity.startActivityForResult ( intent, requestCode );
    }

    public static void start(Fragment fragment, int requestCode) {
        Intent intent = new Intent ( fragment.getActivity (), Test_ui.class );
        fragment.startActivityForResult ( intent, requestCode );
    }

    public static void start(androidx.fragment.app.Fragment fragment, int requestCode) {
        Intent intent = new Intent ( fragment.getContext (), Test_ui.class );
        fragment.startActivityForResult ( intent, requestCode );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootview = inflater.inflate ( R.layout.fragments_testui, container, false );


        hideActionBar ();
        adaptationStatusBar ();
        if (!Setting.onlyStartCamera && null == Setting.imageEngine) {
            getActivity ().finish ();

        }
        initSomeViews ();
        if (PermissionUtil.checkAndRequestPermissionsInActivity ( getActivity (), getNeedPermissions () )) {
            hasPermissions ();
        } else {
            permissionView.setVisibility ( View.VISIBLE );
        }


        return rootview;
    }

    private void adaptationStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int statusColor = getActivity ().getWindow ().getStatusBarColor ();
            if (statusColor == Color.TRANSPARENT) {
                statusColor = ContextCompat.getColor ( getActivity (), R.color.colorPrimaryDark );
            }
            if (ColorUtils.isWhiteColor ( statusColor )) {
                SystemUtils.getInstance ().setStatusDark ( getActivity (), true );
            }
        }
    }

    private void initSomeViews() {
        mBottomBar = rootview.findViewById ( R.id.m_bottom_bar );
        permissionView = rootview.findViewById ( R.id.rl_permissions_view );
        tvPermission = rootview.findViewById ( R.id.tv_permission );
        rootViewAlbumItems = rootview.findViewById ( R.id.root_view_album_items );
        tvTitle = rootview.findViewById ( R.id.tv_title );
        tvPermission.setText ( R.string.permissions_again_easy_photos );
        permissionView.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                if (PermissionUtil.checkAndRequestPermissionsInActivity ( getActivity (), getNeedPermissions () )) {
                    hasPermissions ();
                }
            }
        } );

        if (Setting.isOnlyVideo ()) {
            tvTitle.setText ( R.string.video_selection_easy_photos );
        }
        rootview.findViewById ( R.id.iv_second_menu ).setVisibility ( Setting.showPuzzleMenu || Setting.showCleanMenu || Setting.showOriginalMenu ? View.GONE : View.GONE );
        setClick ( R.id.iv_back );
    }

    public void hasPermissions() {
        permissionView.setVisibility ( View.GONE );
        if (Setting.onlyStartCamera) {
            launchCamera ( Code.REQUEST_CAMERA );
            return;
        }
        AlbumModel.CallBack albumModelCallBack = new AlbumModel.CallBack () {
            @Override
            public void onAlbumWorkedCallBack() {
                getActivity ().runOnUiThread ( new Runnable () {
                    @Override
                    public void run() {
                        onAlbumWorkedDo ();
                    }
                } );
            }
        };
        albumModel = AlbumModel.getInstance ();
        albumModel.query ( getActivity (), albumModelCallBack );
    }

    protected String[] getNeedPermissions() {
        if (Setting.isShowCamera) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                return new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE};
            }
            return new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                return new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE};
            }
            return new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult ( requestCode, permissions, grantResults );

        PermissionUtil.onPermissionResult ( getActivity (), permissions, grantResults,
                new PermissionUtil.PermissionCallBack () {
                    @Override
                    public void onSuccess() {
                        hasPermissions ();
                    }

                    @Override
                    public void onShouldShow() {
                        tvPermission.setText ( R.string.permissions_again_easy_photos );
                        permissionView.setOnClickListener ( new View.OnClickListener () {
                            @Override
                            public void onClick(View view) {
                                if (PermissionUtil.checkAndRequestPermissionsInActivity ( getActivity (), getNeedPermissions () )) {
                                    hasPermissions ();
                                }
                            }
                        } );

                    }

                    @Override
                    public void onFailed() {
                        tvPermission.setText ( R.string.permissions_die_easy_photos );
                        permissionView.setOnClickListener ( new View.OnClickListener () {
                            @Override
                            public void onClick(View view) {
                                SettingsUtils.startMyApplicationDetailsForResult ( getActivity (),
                                        getActivity ().getPackageName () );
                            }
                        } );

                    }
                } );
    }


    /**
     * 启动相机
     *
     * @param requestCode startActivityForResult的请求码
     */
    private void launchCamera(int requestCode) {
        if (TextUtils.isEmpty ( Setting.fileProviderAuthority ))
            throw new RuntimeException ( "AlbumBuilder" + " : 请执行 setFileProviderAuthority()方法" );
        if (!cameraIsCanUse ()) {
            permissionView.setVisibility ( View.VISIBLE );
            tvPermission.setText ( R.string.permissions_die_easy_photos );
            permissionView.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View view) {
                    SettingsUtils.startMyApplicationDetailsForResult ( getActivity (),
                            getActivity ().getPackageName () );
                }
            } );
            return;
        }
        toAndroidCamera ( requestCode );
    }

    /**
     * 启动系统相机
     *
     * @param requestCode 请求相机的请求码
     */
    private Uri photoUri = null;

    private void toAndroidCamera(int requestCode) {
        Intent cameraIntent = new Intent ( MediaStore.ACTION_IMAGE_CAPTURE );
        if (cameraIntent.resolveActivity ( getActivity ().getPackageManager () ) != null) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                photoUri = createImageUri ();
                cameraIntent.putExtra ( MediaStore.EXTRA_OUTPUT, photoUri );
                cameraIntent.addFlags ( Intent.FLAG_GRANT_WRITE_URI_PERMISSION );
                startActivityForResult ( cameraIntent, requestCode );
                return;
            }

            createCameraTempImageFile ();
            if (mTempImageFile != null && mTempImageFile.exists ()) {

                Uri imageUri = UriUtils.getUri ( getActivity (), mTempImageFile );

                cameraIntent.addFlags ( Intent.FLAG_GRANT_READ_URI_PERMISSION ); //对目标应用临时授权该Uri所代表的文件

                cameraIntent.putExtra ( MediaStore.EXTRA_OUTPUT, imageUri );//将拍取的照片保存到指定URI
                startActivityForResult ( cameraIntent, requestCode );
            } else {
                Toast.makeText ( getActivity (), R.string.camera_temp_file_error_easy_photos,
                        Toast.LENGTH_SHORT ).show ();
            }
        } else {
            Toast.makeText ( getActivity (), R.string.msg_no_camera_easy_photos, Toast.LENGTH_SHORT ).show ();
        }
    }


    /**
     * 创建图片地址uri,用于保存拍照后的照片 Android 10以后使用这种方法
     */
    private Uri createImageUri() {
        String status = Environment.getExternalStorageState ();
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals ( Environment.MEDIA_MOUNTED )) {
            return getActivity ().getContentResolver ().insert ( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new ContentValues () );
        } else {
            return getActivity ().getContentResolver ().insert ( MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                    new ContentValues () );
        }
    }


    private void createCameraTempImageFile() {
        File dir = Environment.getExternalStoragePublicDirectory ( Environment.DIRECTORY_PICTURES );
        if (null == dir) {
            dir = new File ( Environment.getExternalStorageDirectory (),
                    File.separator + "DCIM" + File.separator + "Camera" + File.separator );
        }
        if (!dir.exists () || !dir.isDirectory ()) {
            if (!dir.mkdirs ()) {
                dir = getActivity ().getExternalFilesDir ( null );
                if (null == dir || !dir.exists ()) {
                    dir = getActivity ().getFilesDir ();
                    if (null == dir || !dir.exists ()) {
                        String cacheDirPath =
                                File.separator + "data" + File.separator + "data" + File.separator + getActivity ().getPackageName () + File.separator + "cache" + File.separator;
                        dir = new File ( cacheDirPath );
                        if (!dir.exists ()) {
                            dir.mkdirs ();
                        }
                    }
                }
            }
        }

        try {
            mTempImageFile = File.createTempFile ( "IMG", ".jpg", dir );
        } catch (IOException e) {
            e.printStackTrace ();
            mTempImageFile = null;
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult ( requestCode, resultCode, data );
        if (requestCode == Code.REQUEST_SETTING_APP_DETAILS) {
            if (PermissionUtil.checkAndRequestPermissionsInActivity ( getActivity (), getNeedPermissions () )) {
                hasPermissions ();
            } else {
                permissionView.setVisibility ( View.VISIBLE );
            }
            return;
        }
        switch (resultCode) {
            case RESULT_OK:
                if (Code.REQUEST_CAMERA == requestCode) {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                        onCameraResultForQ ();
                        tvPreview.setVisibility ( View.INVISIBLE );
                        tvDone.setVisibility ( View.INVISIBLE );

                        return;
                    }

                    if (mTempImageFile == null || !mTempImageFile.exists ()) {
                        throw new RuntimeException ( "EasyPhotos拍照保存的图片不存在" );
                    }
                    onCameraResult ();
                    tvPreview.setVisibility ( View.INVISIBLE );
                    tvDone.setVisibility ( View.INVISIBLE );
                    return;
                }

                if (Code.REQUEST_PREVIEW_ACTIVITY == requestCode) {
                    if (data.getBooleanExtra ( Key.PREVIEW_CLICK_DONE, false )) {
                        done ();
                        return;
                    }
                    photosAdapter.change ();
                    processOriginalMenu ();
                    shouldShowMenuDone ();
                    return;
                }

                if (Code.REQUEST_PUZZLE_SELECTOR == requestCode) {
                    Photo puzzlePhoto = data.getParcelableExtra ( SuperAlbum.RESULT_PHOTOS );
                    addNewPhoto ( puzzlePhoto );
                    return;
                }

                break;
            case RESULT_CANCELED:

                if (Code.REQUEST_CAMERA == requestCode) {
                    // 删除临时文件
                    if (mTempImageFile != null && mTempImageFile.exists ()) {
                        mTempImageFile.delete ();
                        mTempImageFile = null;
                    }
                    if (Setting.onlyStartCamera) {
                        getActivity ().finish ();
                    }
                    return;
                }

                if (Code.REQUEST_PREVIEW_ACTIVITY == requestCode) {
                    hasPermissions();
                    currAlbumItemIndex=0;
                    rvAlbumItems.scrollToPosition ( 0 );
                    processOriginalMenu ();
                    return;
                }
                break;
            default:
                break;
        }
    }

    private void addNewPhoto(Photo photo) {
        MediaScannerConnectionUtils.refresh ( getActivity (), photo.path );
        photo.selectedOriginal = Setting.selectedOriginal;

        String albumItem_all_name = albumModel.getAllAlbumName ( getActivity () );
        albumModel.album.getAlbumItem ( albumItem_all_name ).addImageItem ( 0, photo );
        String folderPath = new File ( photo.path ).getParentFile ().getAbsolutePath ();
        String albumName = StringUtils.getLastPathSegment ( folderPath );
        albumModel.album.addAlbumItem ( albumName, folderPath, photo.path, photo.uri );
        albumModel.album.getAlbumItem ( albumName ).addImageItem ( 0, photo );

        albumItemList.clear ();
        albumItemList.addAll ( albumModel.getAlbumItems () );
        if (Setting.hasAlbumItemsAd ()) {
            int albumItemsAdIndex = 2;
            if (albumItemList.size () < albumItemsAdIndex + 1) {
                albumItemsAdIndex = albumItemList.size () - 1;
            }
            albumItemList.add ( albumItemsAdIndex, Setting.albumItemsAdView );
        }
        albumItemsAdapter.notifyDataSetChanged ();

        if (Setting.count == 1) {
            Result.clear ();
            int res = Result.addPhoto ( photo );
            onSelectorOutOfMax ( res );
        } else {
            if (Result.count () >= Setting.count) {
                onSelectorOutOfMax ( null );
            } else {
                int res = Result.addPhoto ( photo );
                onSelectorOutOfMax ( res );
            }
        }
        rvAlbumItems.scrollToPosition ( 0 );
        albumItemsAdapter.setSelectedPosition ( 0 );
        shouldShowMenuDone ();
    }

    private Photo getPhoto(Uri uri) {
        Photo p = null;
        String path;
        String name;
        long dateTime;
        String type;
        long size;
        int width = 0;
        int height = 0;
        Cursor cursor = getActivity ().getContentResolver ().query ( uri, null, null, null, null );
        if (cursor == null) {
            return null;
        }
        if (cursor.moveToFirst ()) {
            path = cursor.getString ( cursor.getColumnIndex ( MediaStore.MediaColumns.DATA ) );
            name = cursor.getString ( cursor.getColumnIndex ( MediaStore.MediaColumns.DISPLAY_NAME ) );
            dateTime = cursor.getLong ( cursor.getColumnIndex ( MediaStore.MediaColumns.DATE_MODIFIED ) );
            type = cursor.getString ( cursor.getColumnIndex ( MediaStore.MediaColumns.MIME_TYPE ) );
            size = cursor.getLong ( cursor.getColumnIndex ( MediaStore.MediaColumns.SIZE ) );
            width = cursor.getInt ( cursor.getColumnIndex ( MediaStore.MediaColumns.WIDTH ) );
            height = cursor.getInt ( cursor.getColumnIndex ( MediaStore.MediaColumns.HEIGHT ) );
            p = new Photo ( name, uri, path, dateTime, width, height, size, 0, type );
        }
        cursor.close ();

        return p;
    }

    private void onCameraResultForQ() {
        Photo photo = getPhoto ( photoUri );
        if (photo == null) {
            Log.e ( "easyPhotos", "onCameraResultForQ() -》photo = null" );
            return;
        }

        MediaScannerConnectionUtils.refresh ( getActivity (), new File ( photo.path ) );// 更新媒体库

        if (Setting.onlyStartCamera || albumModel.getAlbumItems ().isEmpty ()) {

            Intent data = new Intent ();
            photo.selectedOriginal = Setting.selectedOriginal;
            resultList.add ( photo );

            data.putParcelableArrayListExtra ( SuperAlbum.RESULT_PHOTOS, resultList );
            data.putExtra ( SuperAlbum.RESULT_SELECTED_ORIGINAL, Setting.selectedOriginal );
            getActivity ().setResult ( RESULT_OK, data );
            getActivity ().finish ();
            return;
        }

        addNewPhoto ( photo );

    }

    private void onCameraResult() {
        SimpleDateFormat dateFormat = new SimpleDateFormat ( "yyyyMMdd_HH_mm_ss",
                Locale.getDefault () );
        String imageName = "IMG_%s.jpg";
        String filename = String.format ( imageName, dateFormat.format ( new Date () ) );
        File reNameFile = new File ( mTempImageFile.getParentFile (), filename );
        if (!reNameFile.exists ()) {
            if (mTempImageFile.renameTo ( reNameFile )) {
                mTempImageFile = reNameFile;
            }
        }
        BitmapFactory.Options options = new BitmapFactory.Options ();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile ( mTempImageFile.getAbsolutePath (), options );
        MediaScannerConnectionUtils.refresh ( getActivity (), mTempImageFile );// 更新媒体库
        if (Setting.onlyStartCamera || albumModel.getAlbumItems ().isEmpty ()) {
            Intent data = new Intent ();
            Uri uri = UriUtils.getUri ( getActivity (), mTempImageFile );
            Photo photo = new Photo ( mTempImageFile.getName (), uri,
                    mTempImageFile.getAbsolutePath (), mTempImageFile.lastModified () / 1000,
                    options.outWidth, options.outHeight, mTempImageFile.length (),
                    DurationUtils.getDuration ( mTempImageFile.getAbsolutePath () ),
                    options.outMimeType );
            photo.selectedOriginal = Setting.selectedOriginal;
            resultList.add ( photo );

            data.putParcelableArrayListExtra ( SuperAlbum.RESULT_PHOTOS, resultList );

            data.putExtra ( SuperAlbum.RESULT_SELECTED_ORIGINAL, Setting.selectedOriginal );


            getActivity ().setResult ( RESULT_OK, data );
            getActivity ().finish ();
            return;
        }

        Uri uri = UriUtils.getUri ( getActivity (), mTempImageFile );

        Photo photo = new Photo ( mTempImageFile.getName (), uri, mTempImageFile.getAbsolutePath (),
                mTempImageFile.lastModified () / 1000, options.outWidth, options.outHeight,
                mTempImageFile.length (),
                DurationUtils.getDuration ( mTempImageFile.getAbsolutePath () ), options.outMimeType );
        addNewPhoto ( photo );

    }


    private void onAlbumWorkedDo() {
        initView ();
    }

    private void initView() {

        if (albumModel.getAlbumItems ().isEmpty ()) {
            Toast.makeText ( getActivity (), R.string.no_photos_easy_photos, Toast.LENGTH_LONG ).show ();
            if (Setting.isShowCamera) launchCamera ( Code.REQUEST_CAMERA );
            else getActivity ().finish ();
            return;
        }

        SuperAlbum.setAdListener ( this );
        if (Setting.hasPhotosAd ()) {
            rootview.findViewById ( R.id.m_tool_bar_bottom_line ).setVisibility ( View.GONE );
        }
        ivCamera = rootview.findViewById ( R.id.fab_camera );
        ivCamera.setVisibility ( View.VISIBLE ); //&& Setting.isBottomRightCamera ()
        if(timeflag==1){
            ivCamera.setVisibility ( View.INVISIBLE );
        }
        if (Setting.isShowCamera ) {
            ivCamera.setVisibility ( View.VISIBLE );
        }
        if (!Setting.showPuzzleMenu) {
            rootview.findViewById ( R.id.tv_puzzle ).setVisibility ( View.GONE );
        }
        mSecondMenus = rootview.findViewById ( R.id.m_second_level_menu );
        int columns = getResources ().getInteger ( R.integer.photos_columns_easy_photos );
        tvAlbumItems = rootview.findViewById ( R.id.tv_album_items );
        tvAlbumItems.setText ( albumModel.getAlbumItems ().get ( 0 ).name );
        tvDone = rootview.findViewById ( R.id.tv_done );
        rvPhotos = rootview.findViewById ( R.id.rv_photos );

        ((SimpleItemAnimator) rvPhotos.getItemAnimator ()).setSupportsChangeAnimations ( false );
        //去除item更新的闪光
        photoList.clear ();
        photoList.addAll ( albumModel.getCurrAlbumItemPhotos ( 0 ) );


        int index = 0;
        if (Setting.hasPhotosAd ()) {
            photoList.add ( index, Setting.photosAdView );
        }
        if (Setting.isShowCamera && !Setting.isBottomRightCamera ()) {
            if (Setting.hasPhotosAd ()) index = 1;
            photoList.add ( index, null );
        }
        ItemTouchHelper mItemHelper = new ItemTouchHelper( new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                Log.e("hsjkkk", "getMovementFlags()");
                if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                    final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                    final int swipeFlags = 0;
                    return makeMovementFlags(dragFlags, swipeFlags);
                } else {
                    final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                    final int swipeFlags = 0;
                    return makeMovementFlags(dragFlags, swipeFlags);
                }
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Log.e("hsjkkk", "onMove()");
                //得到当拖拽的viewHolder的Position
                int fromPosition = viewHolder.getAdapterPosition();
                //拿到当前拖拽到的item的viewHolder
                int toPosition = target.getAdapterPosition();

                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(photoList, i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(photoList, i, i - 1);
                    }
                }
                photosAdapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//                Toast.makeText(MainActivity.this, "拖拽完成 方向" + direction, Toast.LENGTH_SHORT).show();
                Log.e("hsjkkk", "拖拽完成 方向" + direction);

            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
                Log.e("hsjkkk", "onSelectedChanged()");
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE)
                    viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                Log.e("hsjkkk", "clearView()");
                viewHolder.itemView.setBackgroundColor(0);

            }

            //重写拖拽不可用
            @Override
            public boolean isLongPressDragEnabled() {
                Log.e("hsjkkk", "isLongPressDragEnabled()");
                return false;
            }


        });
        photosAdapter = new PhotosAdapter ( mCtx, photoList, this ,1,mItemHelper);


        gridLayoutManager = new GridLayoutManager ( getActivity (), columns ) {

            public boolean canScrollvertically() {
                return false;
            }
        };

        if (Setting.hasPhotosAd ()) {
            gridLayoutManager.setSpanSizeLookup ( new GridLayoutManager.SpanSizeLookup () {
                @Override
                public int getSpanSize(int position) {
                    if (position == 0) {
                        return gridLayoutManager.getSpanCount ();//独占一行
                    } else {
                        return 1;//只占一行中的一列
                    }
                }
            } );
        }

        photosAdapter.notifyDataSetChanged ();

        rvPhotos.setLayoutManager ( gridLayoutManager );
        rvPhotos.setAdapter ( photosAdapter );
        rvPhotos.setNestedScrollingEnabled ( false );

        mItemHelper.attachToRecyclerView(rvPhotos);


        tvOriginal = rootview.findViewById ( R.id.tv_original );
        if (Setting.showOriginalMenu) {
            processOriginalMenu ();
        } else {
            tvOriginal.setVisibility ( View.GONE );
        }
        tvPreview = rootview.findViewById ( R.id.tv_preview );
        if (timeflag==1)
        {
            setView1 ();
            clearTimeView ();
            initTimeView ();


        }


        initAlbumItems ();
        shouldShowMenuDone ();
        tvPreview.setVisibility ( View.INVISIBLE );
        tvDone.setVisibility ( View.INVISIBLE );
        setClick ( R.id.iv_album_items, R.id.tv_clear, R.id.iv_second_menu, R.id.tv_puzzle );
        setClick ( tvAlbumItems, rootViewAlbumItems, tvDone, tvOriginal, tvPreview, ivCamera );

    }

    public void hideActionBar() {
        ActionBar actionBar = getSupportActionBar ();
        if (actionBar != null) {
            actionBar.hide ();
        }
    }

    private void initAlbumItems() {

        rvAlbumItems = rootview.findViewById ( R.id.rv_album_items );
        albumItemList.clear ();
        albumItemList.addAll ( albumModel.getAlbumItems () );

        if (Setting.hasAlbumItemsAd ()) {
            int albumItemsAdIndex = 2;
            if (albumItemList.size () < albumItemsAdIndex + 1) {
                albumItemsAdIndex = albumItemList.size () - 1;
            }
            albumItemList.add ( albumItemsAdIndex, Setting.albumItemsAdView );
        }
        albumItemsAdapter = new AlbumItemsAdapter ( getActivity (), albumItemList, 0, this );
        rvAlbumItems.setLayoutManager ( new LinearLayoutManager ( getActivity () ) );
        rvAlbumItems.setAdapter ( albumItemsAdapter );
    }

    @Override
    public void onClick(View v) {
        int id = v.getId ();
        if (R.id.tv_album_items == id || R.id.iv_album_items == id) {
            showAlbumItems ( View.GONE == rootViewAlbumItems.getVisibility () );
        } else if (R.id.root_view_album_items == id) {
            showAlbumItems ( false );
        } else if (R.id.iv_back == id) {
            onBackPressed ();
        } else if (R.id.tv_done == id) {
            done ();
        } else if (R.id.tv_clear == id) {
            if (Result.isEmpty ()) {
                processSecondMenu ();
                return;
            }
            Result.removeAll ();
            photosAdapter.change ();
            shouldShowMenuDone ();
            processSecondMenu ();
        } else if (R.id.tv_original == id) {
            if (!Setting.originalMenuUsable) {
                Toast.makeText ( getActivity (), Setting.originalMenuUnusableHint, Toast.LENGTH_SHORT ).show ();
                return;
            }
            Setting.selectedOriginal = !Setting.selectedOriginal;
            processOriginalMenu ();
            processSecondMenu ();
        } else if (R.id.tv_preview == id) {
            PreviewActivity.start ( getActivity (), -1, 0 );
        } else if (R.id.fab_camera == id) {
            launchCamera ( Code.REQUEST_CAMERA );
        } else if (R.id.iv_second_menu == id) {
            processSecondMenu ();
        } else if (R.id.tv_puzzle == id) {
            processSecondMenu ();
            PuzzleSelectorActivity.start ( getActivity () );
        }
    }

    public void processSecondMenu() {
        if (mSecondMenus == null) {
            return;
        }
        if (View.VISIBLE == mSecondMenus.getVisibility ()) {
            mSecondMenus.setVisibility ( View.INVISIBLE );
            if (Setting.isShowCamera && Setting.isBottomRightCamera ()) {
                ivCamera.setVisibility ( View.VISIBLE );
            }
        } else {
            mSecondMenus.setVisibility ( View.VISIBLE );
            if (Setting.isShowCamera && Setting.isBottomRightCamera ()) {
                ivCamera.setVisibility ( View.INVISIBLE );
            }
        }
    }

    private void done() {
        Intent intent = new Intent ();
        Result.processOriginal ();
        resultList.addAll ( Result.photos );
        intent.putParcelableArrayListExtra ( SuperAlbum.RESULT_PHOTOS, resultList );
        intent.putExtra ( SuperAlbum.RESULT_SELECTED_ORIGINAL, Setting.selectedOriginal );
        getActivity ().setResult ( RESULT_OK, intent );
        getActivity ().finish ();
    }

    private void processOriginalMenu() {
        if (!Setting.showOriginalMenu) return;
        if (Setting.selectedOriginal) {
            tvOriginal.setTextColor ( ContextCompat.getColor ( getActivity (), R.color.easy_photos_fg_accent ) );
        } else {
            if (Setting.originalMenuUsable) {
                tvOriginal.setTextColor ( ContextCompat.getColor ( getActivity (),
                        R.color.easy_photos_fg_primary ) );
            } else {
                tvOriginal.setTextColor ( ContextCompat.getColor ( getActivity (),
                        R.color.easy_photos_fg_primary_dark ) );
            }
        }
    }

    private void showAlbumItems(boolean isShow) {
        if (null == setShow) {
            newAnimators ();
        }
        if (isShow) {
            rootViewAlbumItems.setVisibility ( View.VISIBLE );
            setShow.start ();
        } else {
            setHide.start ();
        }

    }

    private void newAnimators() {
        newHideAnim ();
        newShowAnim ();
    }

    private void newShowAnim() {
        ObjectAnimator translationShow = ObjectAnimator.ofFloat ( rvAlbumItems, "translationY",
                mBottomBar.getTop (), 0 );
        ObjectAnimator alphaShow = ObjectAnimator.ofFloat ( rootViewAlbumItems, "alpha", 0.0f, 1.0f );
        translationShow.setDuration ( 300 );
        setShow = new AnimatorSet ();
        setShow.setInterpolator ( new AccelerateDecelerateInterpolator () );
        setShow.play ( translationShow ).with ( alphaShow );
    }

    private void newHideAnim() {
        ObjectAnimator translationHide = ObjectAnimator.ofFloat ( rvAlbumItems, "translationY", 0,
                mBottomBar.getTop () );
        ObjectAnimator alphaHide = ObjectAnimator.ofFloat ( rootViewAlbumItems, "alpha", 1.0f, 0.0f );
        translationHide.setDuration ( 200 );
        setHide = new AnimatorSet ();
        setHide.addListener ( new AnimatorListenerAdapter () {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd ( animation );
                rootViewAlbumItems.setVisibility ( View.GONE );
            }
        } );
        setHide.setInterpolator ( new AccelerateInterpolator () );
        setHide.play ( translationHide ).with ( alphaHide );
    }

    @Override
    public void onAlbumItemClick(int position, int realPosition) {
        updatePhotos ( realPosition );
        showAlbumItems ( false );
        tvAlbumItems.setText ( albumModel.getAlbumItems ().get ( realPosition ).name );
    }

    private void updatePhotos(int currAlbumItemIndex) {
        this.currAlbumItemIndex = currAlbumItemIndex;
        photoList.clear ();
        photoList.addAll ( albumModel.getCurrAlbumItemPhotos ( currAlbumItemIndex ) );
        int index = 0;
        if (Setting.hasPhotosAd ()) {
            photoList.add ( index, Setting.photosAdView );
        }
        if (Setting.isShowCamera && !Setting.isBottomRightCamera ()) {
            if (Setting.hasPhotosAd ()) index = 1;
            photoList.add ( index, null );
        }
        photosAdapter.change ();
        rvPhotos.scrollToPosition ( 0 );
    }

    private void shouldShowMenuDone() {
        if (Result.isEmpty ()) {
            if (View.VISIBLE == tvDone.getVisibility ()) {
                ScaleAnimation scaleHide = new ScaleAnimation ( 1f, 0f, 1f, 0f );
                scaleHide.setDuration ( 200 );
                tvDone.startAnimation ( scaleHide );
            }
            tvDone.setVisibility ( View.INVISIBLE );
            tvPreview.setVisibility ( View.INVISIBLE );
        } else {
            if (View.INVISIBLE == tvDone.getVisibility ()) {
                ScaleAnimation scaleShow = new ScaleAnimation ( 0f, 1f, 0f, 1f );
                scaleShow.setDuration ( 200 );
                tvDone.startAnimation ( scaleShow );
            }
            tvDone.setVisibility ( View.VISIBLE );
            tvPreview.setVisibility ( View.VISIBLE );
        }
        tvDone.setText ( getString ( R.string.selector_action_done_easy_photos, Result.count (),
                Setting.count ) );
    }

    @Override
    public void onCameraClick() {
        launchCamera ( Code.REQUEST_CAMERA );
    }

    @Override
    public void onPhotoClick(int position, int realPosition) {
        PreviewActivity.start ( this, currAlbumItemIndex, realPosition ,1);
    }

    @Override
    public void onSelectorOutOfMax(@Nullable Integer result) {
        if (result == null) {
            Toast.makeText ( getActivity (), getString ( R.string.selector_reach_max_hint_easy_photos,
                    Setting.count ), Toast.LENGTH_SHORT ).show ();
            return;
        }
        switch (result) {
            case -1:
                Toast.makeText ( getActivity (), getString ( R.string.selector_reach_max_image_hint_easy_photos
                        , Setting.pictureCount ), Toast.LENGTH_SHORT ).show ();
                break;
            case -2:
                Toast.makeText ( getActivity (), getString ( R.string.selector_reach_max_video_hint_easy_photos
                        , Setting.videoCount ), Toast.LENGTH_SHORT ).show ();
                break;
        }
    }

    @Override
    public void onSelectorChanged() {
        shouldShowMenuDone ();
    }


    public void onBackPressed() {

        if (null != rootViewAlbumItems && rootViewAlbumItems.getVisibility () == View.VISIBLE) {
            showAlbumItems ( false );
            return;
        }

        if (null != mSecondMenus && View.VISIBLE == mSecondMenus.getVisibility ()) {
            processSecondMenu ();
            return;
        }
        //if (albumModel != null) albumModel.stopQuery ();
        if (Setting.hasPhotosAd ()) {
            photosAdapter.clearAd ();
        }
        if (Setting.hasAlbumItemsAd ()) {
            albumItemsAdapter.clearAd ();
        }
        getActivity ().setResult ( RESULT_CANCELED );
        getActivity ().finish ();
    }

    @Override
    public void onDestroy() {
        //if (albumModel != null) albumModel.stopQuery ();
        super.onDestroy ();
    }

    @Override
    public void onPhotosAdLoaded() {
        getActivity ().runOnUiThread ( new Runnable () {
            @Override
            public void run() {
                photosAdapter.change ();
            }
        } );
    }

    @Override
    public void onAlbumItemsAdLoaded() {
        getActivity ().runOnUiThread ( new Runnable () {
            @Override
            public void run() {
                albumItemsAdapter.notifyDataSetChanged ();
            }
        } );
    }


    private void setClick(@IdRes int... ids) {
        for (int id : ids) {
            rootview.findViewById ( id ).setOnClickListener ( this );
        }
    }

    private void setClick(View... views) {
        for (View v : views) {
            v.setOnClickListener ( this );
        }
    }

    /**
     * 返回true 表示可以使用  返回false表示不可以使用
     */
    public boolean cameraIsCanUse() {
        boolean isCanUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open ();
            Camera.Parameters mParameters = mCamera.getParameters (); //针对魅族手机
            mCamera.setParameters ( mParameters );
        } catch (Exception e) {
            isCanUse = false;
        }

        if (mCamera != null) {
            try {
                mCamera.release ();
            } catch (Exception e) {
                e.printStackTrace ();
                return isCanUse;
            }
        }
        return isCanUse;
    }


    public ActionBar getSupportActionBar() {
        return supportActionBar;
    }

    public void setSupportActionBar(ActionBar supportActionBar) {
        this.supportActionBar = supportActionBar;
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
