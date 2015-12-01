package com.openetizen.cevysays.opennews.adapters;

import android.content.Context;
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
public class CategoryOneAdapter extends BaseAdapter {
    private ArrayList<CategoryOneItem> posts;
    private Context context;

    public CategoryOneAdapter(ArrayList<CategoryOneItem> posts, Context context) {
        this.posts = posts;
        this.context = context;
    }

    //mbuh apaan :v
    public CategoryOneAdapter(ArrayList<CategoryOneItem> categoryOneItemArrayList, CategoryOneFragment categoryOneFragment) {
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


        /*if (convertView == null) {
            LayoutInflater inflater
                    = (LayoutInflater) context
                    .getSystemService(Context
                            .LAYOUT_INFLATER_SERVICE);

            convertView = inflater
                    .inflate(R.layout.item_category_one,
                            parent, false);

            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.image);

            convertView.setTag(holder);
        } else {*/
        LayoutInflater inflater
                = (LayoutInflater) context
                .getSystemService(Context
                        .LAYOUT_INFLATER_SERVICE);

        convertView = inflater
                .inflate(R.layout.item_category_one,
                        parent, false);
        holder = new ViewHolder();
        holder.image = (ImageView) convertView.findViewById(R.id.image);
        Picasso.with(context).load("http://openetizen.com" + posts.get(position).getImage()).into(holder.image);
        holder.title = (TextView) convertView
                .findViewById(R.id.title);
        holder.title.setText(posts.get(position).getTitle());

        holder.created_at = (TextView) convertView
                .findViewById(R.id.created_at);
        holder.created_at.setText(posts.get(position).getCreated_at());

        holder.username = (TextView) convertView.findViewById(R.id.username);
        holder.username.setText(posts.get(position).getUsername());


//        holder.content = (TextView) convertView.findViewById(R.id.deskripsi);
//        holder.content.setText(posts.get(position).getContent());

        convertView.setTag(holder);
        //}
//        else
//        {
//            holder = (ViewHolder) convertView.getTag();
//        }

//        Picasso.with(context)
//                .load(posts.get(position).getImage())
//                .into(holder.image);
//        holder.user_id
//                .setText(posts.get(position).getUser_id());
//        holder.title
//                .setText(posts.get(position).getTitle());
//        holder.created_at
//                .setText(posts.get(position).getCreated_at());
//
        return convertView;
    }

    private class ViewHolder {
//        ImageView gambar;
//        TextView judul;
//        TextView tanggal;

        TextView article_id;
        TextView username;
        TextView title;
        TextView content;
        TextView category_cd;
        TextView publish_status;
        TextView created_at;
        TextView updated_at;
        ImageView image;


    }
}
