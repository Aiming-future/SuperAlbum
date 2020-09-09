package com.StrivingRookies.superalbum.ui_menu;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.StrivingRookies.superalbum.SuperAlbum;
import com.StrivingRookies.superalbum.ui.adapter.PhotosAdapter;
import com.StrivingRookies.superalbum.ui.widget.PressedTextView;

public class AboutApp extends AppCompatActivity {
    private TextView back;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView(R.layout.about_app);
        back=(TextView)findViewById ( R.id.textView6 );
        back.setOnClickListener ( new View.OnClickListener (){
            @Override
            public void onClick(View v) {
                onBackPressed ();
            }
        } );

    }
}
