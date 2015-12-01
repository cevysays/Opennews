package com.openetizen.cevysays.opennews.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Cevy Yufindra on 09/06/2015.
 */
public class CategoryOneItem implements Parcelable {
    public static final String API = "http://openetizen.com/opennews.json";

    private String article_id;
    private String username;
    private String user_id;
    private String title;
    private String content;
    private String category_cd;
    private String publish_status;
    private String created_at;
    private String updated_at;
    private String image;

    public CategoryOneItem(String image, String title, String created_at, String username, String content, String category_cd) {
        this.image = image;
        this.title = title;
        this.created_at = created_at;
        this.username = username;
        this.content = content;
        this.category_cd = category_cd;
    }

    private CategoryOneItem(Parcel in) {
        user_id = in.readString();
        username = in.readString();
        title = in.readString();
        content = in.readString();
        created_at = in.readString();
        image = in.readString();
        category_cd = in.readString();
        article_id = in.readString();
    }


    public static String getAPI() {
        return API;
    }

    public String getArticle_id() {
        return article_id;
    }

    public void setArticle_id(String article_id) {
        this.article_id = article_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory_cd() {
        return category_cd;
    }

    public void setCategory_cd(String category_cd) {
        this.category_cd = category_cd;
    }

    public String getPublish_status() {
        return publish_status;
    }

    public void setPublish_status(String publish_status) {
        this.publish_status = publish_status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
//        this.image = image;
        image = image.replace("-340x160", "");
        image = image.replace("http://", "");
        this.image = "http://i2.wp.com/" + image + "?resize=400%2C242";
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_id);
        dest.writeString(username);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(created_at);
        dest.writeString(image);
        dest.writeString(category_cd);
        dest.writeString(article_id);
    }

    public static final Creator<CategoryOneItem> CREATOR
            = new Creator<CategoryOneItem>() {
        public CategoryOneItem createFromParcel(Parcel in) {
            return new CategoryOneItem(in);
        }

        public CategoryOneItem[] newArray(int size) {
            return new CategoryOneItem[size];
        }
    };
}
