package com.xiaobukuaipao.youngmam.domain;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;

/**
 * Created by xiaobu1 on 15-5-16.
 */
public class MetaArticle {

    private Article article;
    private String itemDesc;
    private String title;

    public MetaArticle() {
        article = null;
        itemDesc = null;
        title = null;
    }

    public MetaArticle(JSONObject jsonObject) {
        if (jsonObject.getJSONObject(GlobalConstants.JSON_ARTICLE) != null) {
            article = new Article(jsonObject.getJSONObject(GlobalConstants.JSON_ARTICLE));
        }

        if (jsonObject.getString(GlobalConstants.JSON_ITEMDESC) != null) {
            itemDesc = jsonObject.getString(GlobalConstants.JSON_ITEMDESC);
        }

        if (jsonObject.getString(GlobalConstants.JSON_TITLE) != null) {
            title = jsonObject.getString(GlobalConstants.JSON_TITLE);
        }
    }

    public void setArticle(Article article) {
        this.article = article;
    }
    public Article getArticle() {
        return this.article;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }
    public String getItemDesc() {
        return this.itemDesc;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return this.title;
    }
}
