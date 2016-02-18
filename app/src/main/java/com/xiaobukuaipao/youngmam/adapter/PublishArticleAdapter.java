package com.xiaobukuaipao.youngmam.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xiaobukuaipao.youngmam.FreshDetailsActivity;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.domain.Article;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;

import java.util.List;

/**
 * Created by xiaobu1 on 15-5-20.
 */
public class PublishArticleAdapter extends YmamBaseAdapter<Article> {

    public PublishArticleAdapter(Context context, List<Article> datas, int mItemLayoutId) {
        super(context, datas, mItemLayoutId);
    }

    @Override
    public void convert(final ViewHolder viewHolder, final Article item, final int position) {

        final FrameLayout imageLayout = viewHolder.getView(R.id.image_layout);
        ImageView imageView = viewHolder.getView(R.id.image_item);

        Glide.with(context)
                .load(item.getImgs().getJSONObject(0).getString(GlobalConstants.JSON_URL))
                .centerCrop()
                .placeholder(R.mipmap.default_loading)
                .into(imageView);

        /**
         * 监听器
         */
        imageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item != null) {
                    Intent intent = new Intent(context, FreshDetailsActivity.class);
                    intent.putExtra("article_id", item.getArticleId());
                    context.startActivity(intent);
                }
            }
        });
    }
}
