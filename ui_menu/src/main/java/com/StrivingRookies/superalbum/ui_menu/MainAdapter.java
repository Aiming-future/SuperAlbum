package com.StrivingRookies.superalbum.ui_menu;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.StrivingRookies.superalbum.utils.DateUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.StrivingRookies.superalbum.models.album.entity.Photo;

import java.util.ArrayList;

/**
 * 返回图片的列表适配器
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainVH> {
    private ArrayList<Photo> list;
    private LayoutInflater mInflater;
    private RequestManager mGlide;

    MainAdapter(Context cxt, ArrayList<Photo> list) {
        this.list = list;
        mInflater = LayoutInflater.from(cxt);
        mGlide = Glide.with(cxt);
    }

    @Override
    public MainVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainVH(mInflater.inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(MainVH holder, int position) {
        Photo photo = list.get(position);
        mGlide.load(photo.uri).into(holder.ivPhoto);
        String time= DateUtil.stampToDate ( photo.time );
        holder.tvMessage.setText("[图片名称]： "+photo.name+"\n[宽]："+photo.width+"\n[高]："+photo.height+"\n[文件大小,单位bytes]："+photo.size+"\n[日期，时间戳，毫秒]："+photo.time+"\n[日期，年月日]："+time+"\n[日期，年-月-日]："+time+"\n[图片地址]："+photo.path+"\n[图片类型]："+photo.type+"\n[是否选择原图]："+photo.selectedOriginal);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MainVH extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
        TextView tvMessage;
        MainVH(View itemView) {
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(R.id.iv_photo);
            tvMessage = (TextView) itemView.findViewById(R.id.tv_message);
        }
    }
}
