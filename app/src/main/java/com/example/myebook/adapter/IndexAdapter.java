package com.example.myebook.adapter;


import android.content.Context;
import android.support.annotation.IntDef;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.myebook.R;
import com.example.myebook.bean.PageIndex;
import com.example.myebook.util.VirtureUtil.onClickItemListener;
import com.example.myebook.util.VirtureUtil.onLongClickItemListener;

import java.util.ArrayList;

public class IndexAdapter extends BaseAdapter implements OnClickListener, OnLongClickListener {

    private static final String TAG = "IndexAdapter";
    private ArrayList<PageIndex> mData;
    private LayoutInflater mInflater;
    private int mCount = 0;
    private int checkid = 0;

    private Boolean deleteMode = false;

    public IndexAdapter(Context context, ArrayList<PageIndex> indexInfo){
        mData = indexInfo;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        holder = new ViewHolder();
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.item_index, null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_index);
            holder.cb_check = (CheckBox) convertView.findViewById(R.id.checkBox);
            holder.tv_name.setOnClickListener(this);
            holder.tv_name.setOnLongClickListener(this);
            holder.tv_name.setId(mCount++);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        if(holder.tv_name.getId() == checkid){
            holder.tv_name.setBackgroundColor(0XAA92fd92);
        }else{
            holder.tv_name.setBackgroundColor(0X33aaffff);
        }


        holder.tv_name.setText(mData.get(position).title);

        holder.cb_check.setChecked(mData.get(position).isCheck);
        if(deleteMode){
            holder.cb_check.setVisibility(View.VISIBLE);
        }else{
            holder.cb_check.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public  Object getItem(int i){
        return mData.get(i);
    }

    @Override
    public long getItemId(int i){
        return i;
    }

    @Override
    public int getCount(){
        return mData.size();
    }


    class ViewHolder{
        TextView tv_name;
        CheckBox cb_check;
    }

    public void freshListIndex(ArrayList<PageIndex> content){
        mData = content;
    }

    @Override
    public void onClick(View v){
        mClickListener.onItemClick(v, v.getId());
        checkid = v.getId();
    }
    @Override
    public boolean onLongClick(View v) {

        deleteMode = !deleteMode;
        mLongClickListener.onLongClickItem(v, v.getId());
        return true;
    }

    public void setCheckId(int num){
        checkid = num;
    }

    private onClickItemListener mClickListener;
    private onLongClickItemListener mLongClickListener;
    public void setOnClickItemListener(onClickItemListener listener){
        mClickListener = listener;
    }
    public void setOnLongClickItemListener(onLongClickItemListener listener){
        mLongClickListener = listener;
    }
}