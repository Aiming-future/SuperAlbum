package com.StrivingRookies.superalbum.ui_menu;

import android.app.usage.NetworkStats;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.common.utils.OSSUtils;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetBucketACLRequest;
import com.alibaba.sdk.android.oss.model.ObjectMetadata;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;


import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.StrivingRookies.superalbum.ui_menu.SampleActivity.userName;


public class loadHelper {

    //与个人的存储区域有关
    private static final String ENDPOINT = "https://oss-cn-shenzhen.aliyuncs.com";
    //上传仓库名
    private static final String BUCKET_NAME = "superalbum";
    private static final String ACCESS_ID =；
    private static final String ACCESS_KEY =；
    private OSSClient oss;

    private static Context cxt;
    public loadHelper(Context cxt)
    {
        this.cxt=cxt;

        
    }

    public static OSS getOSSClient(Context cxt) {

        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(ACCESS_ID, ACCESS_KEY);
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(30 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(30 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(10); // 最大并发请求数，默认5个
        conf.setMaxErrorRetry(100); // 失败后最大重试次数，默认2次
        // oss为全局变量，OSS_ENDPOINT是一个OSS区域地址
        OSSClient oss = new OSSClient(cxt, ENDPOINT, credentialProvider, conf);



//        OSSCredentialProvider credentialProvider =
//                new OSSPlainTextAKSKCredentialProvider("LTAI4GBbddyHN28T6xr8n3H8" ,
//                        "Vs4CUJFlWDQYHtJ9UXLnhBDFdbpZzT");
        return oss;
    }

    /**
     * 上传方法
     *
     * @param objectKey 标识
     * @param path      需上传文件的路径
     * @return 外网访问的路径
     */
    private static String upload(String objectKey, String path) {
        // 构造上传请求
        PutObjectRequest request =
                new PutObjectRequest(BUCKET_NAME,
                        objectKey, path);





        try{
//            Map<String, String> tags = new HashMap<String, String> ();
//            tags.put ("filepath", path);
//// 在http header中设置标签信息。
//            ObjectMetadata metadata = new ObjectMetadata();
//            metadata.setUserMetadata ( tags );
//            request.setMetadata ( metadata );
            OSSClient oss= (OSSClient) getOSSClient ( cxt );
            //oss.putObject ( request);


            OSSAsyncTask task = oss.asyncPutObject(request, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                @Override
                public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                    Log.d("PutObject", "UploadSuccess");
                    Log.d("ETag", result.getETag());
                    Log.d("RequestId", result.getRequestId());



                }

                @Override
                public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                    // 请求异常
                    if (clientExcepion != null) {
                        // 本地异常如网络异常等
                        clientExcepion.printStackTrace();
                    }
                    if (serviceException != null) {
                        // 服务异常
                        Log.e("ErrorCode", serviceException.getErrorCode());
                        Log.e("RequestId", serviceException.getRequestId());
                        Log.e("HostId", serviceException.getHostId());
                        Log.e("RawMessage", serviceException.getRawMessage());
                    }

                    return;
                }
            });

            String url = getOSSClient ( cxt ).presignPublicObjectURL(BUCKET_NAME, objectKey);
            //格式打印输出

            return url;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    /**
     * 上传普通图片
     *
     * @param path 本地地址
     * @return 服务器地址
     */
    public static String uploadImage(String path) {
        String key = getObjectImageKey(path);
        return upload(key, path);
    }

    public static String uploaduser(String user,String path) {
        String key = getObjectPassKey(user,path);
        return upload(key, path);
    }

    /**
     * 上传头像
     *
     * @param path 本地地址
     * @return 服务器地址
     */
    public static String uploadPortrait(String path) {
        String key = getObjectPortraitKey(path);
        return upload(key, path);
    }

    /**
     * 上传audio
     *
     * @param path 本地地址
     * @return 服务器地址
     */
    public static String uploadAudio(String path) {
        String key = getObjectAudioKey(path);
        return upload(key, path);
    }


    /**
     * 获取时间
     *
     * @return 时间戳 例如:201805
     */
    private static String getDateString() {
        return DateFormat.format("yyyyMM", new Date()).toString();
    }

    /**
     * 返回key
     *
     * @param path 本地路径
     * @return key
     */
    //格式: image/201805/sfdsgfsdvsdfdsfs.jpg
    private static String getObjectImageKey(String path) {
        String filename = (new File(path)).getName ();
        String dateString = filename.substring(filename.lastIndexOf("\\")+1);
        return String.format(userName+"/%s", filename);
    }

    private static String getObjectPassKey(String user,String path) {
        String filename = (new File(path)).getName ();
        String dateString = filename.substring(filename.lastIndexOf("\\")+1);
        return String.format("User/"+user+"/%s", filename);
    }

    //格式: portrait/201805/sfdsgfsdvsdfdsfs.jpg
    private static String getObjectPortraitKey(String path) {
        String fileMd5 = (new File(path)).getName ();
        String dateString = getDateString();
        return String.format("portrait/%s/%s.jpg", dateString, fileMd5);
    }

    //格式: audio/201805/sfdsgfsdvsdfdsfs.mp3
    private static String getObjectAudioKey(String path) {
        String filename = (new File(path)).getName ();
        String dateString = filename.substring(0,filename.lastIndexOf("."));
        return String.format("audio/%s/%s.mp3", dateString, filename);
    }

}

