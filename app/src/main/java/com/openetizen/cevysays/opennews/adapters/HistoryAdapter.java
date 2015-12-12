package com.openetizen.cevysays.opennews.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.openetizen.cevysays.opennews.R;
import com.openetizen.cevysays.opennews.fragments.CategoryOneFragment;
import com.openetizen.cevysays.opennews.models.CategoryOneItem;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;

/**
 * Created by Cevy Yufindra on 14/06/2015.
 */
public class HistoryAdapter extends BaseAdapter {
    private ArrayList<CategoryOneItem> posts;
    private Context context;

    public HistoryAdapter(ArrayList<CategoryOneItem> posts, Context context) {
        this.posts = posts;
        this.context = context;
    }

    //mbuh apaan :v
    public HistoryAdapter(ArrayList<CategoryOneItem> categoryOneItemArrayList, CategoryOneFragment categoryOneFragment) {
    }


    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public Object getItem(int position) {
        return posts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        LayoutInflater inflater
                = (LayoutInflater) context
                .getSystemService(Context
                        .LAYOUT_INFLATER_SERVICE);

        convertView = inflater
                .inflate(R.layout.item_history,
                        parent, false);
        holder = new ViewHolder();
        holder.image = (ImageView) convertView.findViewById(R.id.thumbImage);
        Picasso.with(context).load("http://openetizen.com" + posts.get(position).getImage()).into(holder.image);
        holder.title = (TextView) convertView
                .findViewById(R.id.title);
        holder.title.setText(posts.get(position).getTitle());

        holder.content = (TextView) convertView
                .findViewById(R.id.content);
        holder.content.setText(Html.fromHtml(posts.get(position).getContent()));

        holder.created_at = (TextView) convertView
                .findViewById(R.id.date);
        holder.created_at.setText(posts.get(position).getCreated_at());

        convertView.setTag(holder);
//
        return convertView;
    }

    private class ViewHolder {

        TextView title;
        TextView content;
        TextView created_at;
        ImageView image;


    }
}
