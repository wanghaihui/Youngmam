package com.xiaobukuaipao.youngmam.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.SplashActivity;
import com.xiaobukuaipao.youngmam.domain.UserBase;
import com.xiaobukuaipao.youngmam.listener.OnFollowClickListener;
import com.xiaobukuaipao.youngmam.view.RoundedImageView;

import java.util.List;

/**
 * Created by xiaobu1 on 15-10-20.
 */
public class UserAdapter extends YmamBaseAdapter<UserBase> {

    private SharedPreferences sp;

    public OnFollowClickListener onFollowClickListener;

    public void setOnFollowClickListener(OnFollowClickListener onFollowClickListener) {
        this.onFollowClickListener = onFollowClickListener;
    }

    public UserAdapter(Context context, List<UserBase> datas, int mItemLayoutId) {
        super(context, datas, mItemLayoutId);

        sp = context.getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
    }

    @Override
    public void convert(final ViewHolder viewHolder, final UserBase item, final int position) {
        final RoundedImageView imageView = viewHolder.getView(R.id.avatar);
        Picasso.with(context)
                .load(item.getHeadUrl())
                .fit()
                .centerCrop()
                .placeholder(R.mipmap.default_mama)
                .into(imageView);

        final TextView name = viewHolder.getView(R.id.name);
        name.setText(item.getName());
        if (item.getExpertType() != 0) {
            name.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.mipmap.default_geek), null);
        } else {
            name.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }

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

        final TextView state = viewHolder.getView(R.id.state);
        switch (item.getChildStatus()) {
            case 1:
                state.setText(context.getResources().getString(R.string.str_prepare));
                break;
            case 2:
                state.setText(context.getResources().getString(R.string.str_gravida));
                break;
            case 3:
                state.setText(context.getResources().getString(R.string.str_hava_baby));
                break;
            default:
                state.setText(context.getResources().getString(R.string.str_prepare));
                break;
        }
    }
}
