package com.xiaobukuaipao.youngmam.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.SplashActivity;
import com.xiaobukuaipao.youngmam.domain.Expert;
import com.xiaobukuaipao.youngmam.domain.SizeImage;
import com.xiaobukuaipao.youngmam.listener.OnFollowClickListener;
import com.xiaobukuaipao.youngmam.widget.FourColumnGridLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-7-2.
 */
public class ExpertAdapter extends YmamBaseAdapter<Expert> {
    private static final String TAG = ExpertAdapter.class.getSimpleName();

    private FragmentManager fragmentManager;

    private OnFollowClickListener onFollowClickListener;

    private SharedPreferences sp;

    public void setOnFollowClickListener(OnFollowClickListener onFollowClickListener) {
        this.onFollowClickListener = onFollowClickListener;
    }

    public ExpertAdapter(Context context, List<Expert> datas, int mItemLayoutId) {
        super(context, datas, mItemLayoutId);

        sp = context.getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void convert(final ViewHolder viewHolder, final Expert item, final int position) {
        final ImageView avatar = viewHolder.getView(R.id.avatar);

        Picasso.with(context)
                .load(item.getHeadUrl())
                .placeholder(R.mipmap.default_article_mama)
                .error(R.mipmap.default_article_mama)
                .fit()
                .centerCrop()
                .into(avatar);

        ((TextView) viewHolder.getView(R.id.name)).setText(item.getName());

        ((TextView) viewHolder.getView(R.id.type)).setText(item.getExpertName());

        final TextView follow = viewHolder.getView(R.id.follow);

        if (sp.getLong(SplashActivity.UID, 0) > 0) {

            if (!item.getUserId().equals(String.valueOf(sp.getLong(SplashActivity.UID, 0)))) {
                follow.setVisibility(View.VISIBLE);

                if (item.isHasFollowed()) {
                    // 此时已经关注
                    follow.setSelected(true);
                    follow.setText(context.getResources().getString(R.string.has_followed));
                    follow.setTextColor(context.getResources().getColor(R.color.color_ff4c51));

                    follow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 取消关注
                            onFollowClickListener.onFollowClick(item.getUserId(),
                                    OnFollowClickListener.TYPE_FOLLOW_DELETE);
                        }
                    });
                } else {
                    // 此时没有关注
                    follow.setSelected(false);
                    follow.setText(context.getResources().getString(R.string.add_follow));
                    follow.setTextColor(context.getResources().getColor(R.color.white));

                    follow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 加关注
                            onFollowClickListener.onFollowClick(item.getUserId(),
                                    OnFollowClickListener.TYPE_FOLLOW_ADD);
                        }
                    });
                }
            } else {
                follow.setVisibility(View.GONE);
            }
        } else {
            follow.setVisibility(View.GONE);
        }

        final FourColumnGridLayout fourColumnGridLayout = viewHolder.getView(R.id.four_column_grid_layout);

        if (item.getLastImgs() != null && item.getLastImgs().size() > 0) {
            ArrayList<SizeImage> images = new ArrayList<SizeImage>();

            int length = item.getLastImgs().size();

            for(int i=0; i < length; i++) {
                images.add(new SizeImage(item.getLastImgs().getJSONObject(i)));
            }

            fourColumnGridLayout.setVisibility(View.VISIBLE);
            fourColumnGridLayout.setImageDatas(images, fragmentManager);
        } else {
            fourColumnGridLayout.setVisibility(View.GONE);
        }

    }
}
