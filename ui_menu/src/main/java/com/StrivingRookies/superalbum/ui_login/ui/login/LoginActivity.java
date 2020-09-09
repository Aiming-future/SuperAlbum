package com.StrivingRookies.superalbum.ui_login.ui.login;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.StrivingRookies.superalbum.ui.PreviewActivity;
import com.StrivingRookies.superalbum.ui_menu.R;
import com.StrivingRookies.superalbum.ui_menu.SampleActivity;
import com.StrivingRookies.superalbum.ui_menu.loadHelper;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.ListObjectsRequest;
import com.alibaba.sdk.android.oss.model.ListObjectsResult;
import com.alibaba.sdk.android.oss.model.OSSObjectSummary;

import java.io.File;
import java.io.IOException;


public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ToggleButton pv;
    private EditText etPassword;
    public  EditText userName;
    public static String name1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_login );
        loginViewModel = ViewModelProviders.of ( this, new LoginViewModelFactory () )
                .get ( LoginViewModel.class );

        final EditText usernameEditText = findViewById ( R.id.username );
        final EditText passwordEditText = findViewById ( R.id.password );
        final Button loginButton = findViewById ( R.id.login );
        final ProgressBar loadingProgressBar = findViewById ( R.id.loading );
        this.etPassword = (EditText) findViewById(R.id.password);
        this.userName = (EditText) findViewById(R.id.username);
        this.pv=(ToggleButton)findViewById ( R.id.password_visibility );
        this.pv.setOnCheckedChangeListener ( new ToggleButtonClick(  ) );
        loginViewModel.getLoginFormState ().observe ( this, new Observer<LoginFormState> () {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled ( loginFormState.isDataValid () );
                if (loginFormState.getUsernameError () != null) {
                    usernameEditText.setError ( getString ( loginFormState.getUsernameError () ) );
                }
                if (loginFormState.getPasswordError () != null) {
                    passwordEditText.setError ( getString ( loginFormState.getPasswordError () ) );
                }
            }
        } );

        loginViewModel.getLoginResult ().observe ( this, new Observer<LoginResult> () {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility ( View.GONE );
                if (loginResult.getError () != null) {
                    showLoginFailed ( loginResult.getError () );
                }
                if (loginResult.getSuccess () != null) {
                    updateUiWithUser ( loginResult.getSuccess () );
                }
                setResult ( Activity.RESULT_OK );

                //Complete and destroy login activity once successful
                finish ();
            }
        } );

        TextWatcher afterTextChangedListener = new TextWatcher () {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged ( usernameEditText.getText ().toString (),
                        passwordEditText.getText ().toString () );
            }
        };
        usernameEditText.addTextChangedListener ( afterTextChangedListener );
        passwordEditText.addTextChangedListener ( afterTextChangedListener );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            passwordEditText.setOnEditorActionListener ( new TextView.OnEditorActionListener () {

                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        loginViewModel.login ( usernameEditText.getText ().toString (),
                                passwordEditText.getText ().toString () );
                    }
                    return false;
                }
            } );
        }

        loginButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Boolean loginResult=false;
                loadHelper load=new loadHelper ( getApplication () );
                OSS ossClient = load.getOSSClient ( getApplication () );

                // 构造ListObjectsRequest请求
                ListObjectsRequest listObjectsRequest = new ListObjectsRequest("superalbum");
                //Delimiter 设置为 “/” 时，罗列该文件夹下的文件
                listObjectsRequest.setDelimiter("/");
                //Prefix 设为某个文件夹名，罗列以此 Prefix 开头的文件
                listObjectsRequest.setPrefix("User/"+userName.getText ().toString ()+"/");

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
                    String pass = url.substring(url.lastIndexOf('/')+1);
                    if(pass.equals ( etPassword.getText ().toString () )){
                        loginResult=true;
                        break;
                    }
                }
                if(loginResult) {
                    loadingProgressBar.setVisibility ( View.VISIBLE );
                    loginViewModel.login ( usernameEditText.getText ().toString (),
                            passwordEditText.getText ().toString () );
                    Intent intent = new Intent ();
                    intent.putExtra ( "uname", userName.getText ().toString () );
                    intent.setClass ( LoginActivity.this, SampleActivity.class );
                    startActivity ( intent );
                }else{
                    Toast.makeText ( getApplicationContext (), "登录失败!", Toast.LENGTH_LONG ).show ();
                    AlertDialog.Builder builder = new AlertDialog.Builder( LoginActivity.this);
                    builder.setTitle("注册");
                    builder.setMessage("账号或密码错误，是否进行注册后登录？");
                    builder.setPositiveButton ( "是",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String parentPath= Environment.getExternalStorageDirectory().getAbsolutePath();
                            File newPass=new File(parentPath+File.separator+etPassword.getText ().toString ());
                            try {
                                newPass.createNewFile ();
                            } catch (IOException e) {
                                e.printStackTrace ();
                            }
                            load.uploaduser ( userName.getText ().toString (),newPass.getAbsolutePath () );
                            if(newPass.exists ()){
                                Toast.makeText(LoginActivity.this, "注册成功，自动登录跳转！",Toast.LENGTH_SHORT).show();
                                loadingProgressBar.setVisibility ( View.VISIBLE );
                                loginViewModel.login ( usernameEditText.getText ().toString (),
                                        passwordEditText.getText ().toString () );
                                Intent intent = new Intent ();
                                intent.putExtra ( "uname", userName.getText ().toString () );
                                intent.setClass ( LoginActivity.this, SampleActivity.class );
                                startActivity ( intent );
                            }else{
                                Toast.makeText(LoginActivity.this, "注册失败，请再次尝试！",Toast.LENGTH_SHORT).show();

                            }


                        }
                    });
                    builder.setNeutralButton ( "否",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Toast.makeText(LoginActivity.this, "请输入正确的账号密码！",Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setCancelable ( false );
                    builder.show();
                }
            }
        } );
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString ( R.string.welcome ) +"  "+ userName.getText ().toString ();
        // TODO : initiate successful logged in experience
        Toast.makeText ( getApplicationContext (), welcome, Toast.LENGTH_LONG ).show ();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText ( getApplicationContext (), errorString, Toast.LENGTH_SHORT ).show ();
    }
    private class ToggleButtonClick implements CompoundButton.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            //5、判断事件源的选中状态
            if (isChecked){

                //显示密码
                //etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                etPassword.setTransformationMethod( HideReturnsTransformationMethod.getInstance());
            }else {
                // 隐藏密码
                //etPassword.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                etPassword.setTransformationMethod( PasswordTransformationMethod.getInstance());

            }
            //6、每次显示或者关闭时，密码显示编辑的线不统一在最后，下面是为了统一
            etPassword.setSelection(etPassword.length());
        }
    }
}
