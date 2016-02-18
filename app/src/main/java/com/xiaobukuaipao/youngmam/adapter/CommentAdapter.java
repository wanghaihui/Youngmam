package com.xiaobukuaipao.youngmam.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xiaobukuaipao.youngmam.OtherActivity;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.domain.Comment;
import com.xiaobukuaipao.youngmam.domain.SizeImage;
import com.xiaobukuaipao.youngmam.font.FontTextView;
import com.xiaobukuaipao.youngmam.font.TypefaceManager;
import com.xiaobukuaipao.youngmam.listener.OnCommentClickListener;
import com.xiaobukuaipao.youngmam.listener.OnCommentLongClickListener;
import com.xiaobukuaipao.youngmam.listener.OnLikeCommentClickListener;
import com.xiaobukuaipao.youngmam.utils.DisplayUtil;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.TimeUtil;
import com.xiaobukuaipao.youngmam.view.FloorCommentView;
import com.xiaobukuaipao.youngmam.view.RoundedImageView;
import com.xiaobukuaipao.youngmam.widget.NineGridLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-4-27.
 */
public class CommentAdapter extends YmamBaseAdapter<Comment> {

    private OnCommentClickListener onCommentClickListener;
    private OnLikeCommentClickListener onLikeCommentClickListener;

    private OnCommentLongClickListener onCommentLongClickListener;

    private FragmentManager fragmentManager;

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public void setOnCommentClickListener(OnCommentClickListener onCommentClickListener) {
        this.onCommentClickListener = onCommentClickListener;
    }

    public void setOnLikeCommentClickListener(OnLikeCommentClickListener onLikeCommentClickListener) {
        this.onLikeCommentClickListener = onLikeCommentClickListener;
    }

    public void setOnCommentLongClickListener(OnCommentLongClickListener onCommentLongClickListener) {
        this.onCommentLongClickListener = onCommentLongClickListener;
    }

    public CommentAdapter(Context context, List<Comment> datas, int mItemLayoutId) {
        super(context, datas, mItemLayoutId);
    }

    @Override
    public void convert(final ViewHolder viewHolder, final Comment item, final int position) {
        final RoundedImageView avatar = viewHolder.getView(R.id.avatar);

        Picasso.with(context)
                .load(item.getUserBase().getHeadUrl())
                .into(avatar);

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OtherActivity.class);
                intent.putExtra("userId", item.getUserBase().getUserId());
                intent.putExtra("userName", item.getUserBase().getName());
                context.startActivity(intent);
            }
        });

        viewHolder.setText(R.id.name, item.getUserBase().getName());

        final TextView likeComment = viewHolder.getView(R.id.like_comment);
        likeComment.setText(String.valueOf(item.getLikeCount()));
        if (item.isHasLiked()) {
            likeComment.setSelected(true);
        } else {
            likeComment.setSelected(false);
        }

        viewHolder.setText(R.id.comment, item.getContent());

        final NineGridLayout nineGridLayout = viewHolder.getView(R.id.nine_grid_layout);
        if (item.getImgs() != null && item.getImgs().size() > 0) {
            ArrayList<SizeImage> images = new ArrayList<SizeImage>();

            int length = item.getImgs().size();

            for(int i=0; i < length; i++) {
                images.add(new SizeImage(item.getImgs().getJSONObject(i)));
            }

            nineGridLayout.setVisibility(View.VISIBLE);
            nineGridLayout.setImageDatas(images, fragmentManager);
        } else {
            nineGridLayout.setVisibility(View.GONE);
        }

        viewHolder.setText(R.id.time, TimeUtil.handleTime(context, item.getCreateTime()));

        final ImageView vLine = viewHolder.getView(R.id.v_line);
        final FloorCommentView floorCommentView = viewHolder.getView(R.id.other_comments);

        if (item.getChildrenCommnets() != null && item.getChildrenCommnets().size() > 0) {
            floorCommentView.setVisibility(View.VISIBLE);
            vLine.setVisibility(View.VISIBLE);

            int length = item.getChildrenCommnets().size();

            // 先移除所有的View
            floorCommentView.removeAllViews();
            for (int i=0; i < length; i++) {
                final Comment comment = new Comment(item.getChildrenCommnets().getJSONObject(i));

                FontTextView fontTextView = new FontTextView(context);
                fontTextView.setBackgroundResource(R.color.label_item_bg);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                fontTextView.setPadding(0, DisplayUtil.dip2px(context, 3),0, DisplayUtil.dip2px(context, 3));
                fontTextView.setLayoutParams(params);

                TypefaceManager.getInstance().setTypeface(fontTextView, "fzltxihk");
                fontTextView.setTextColor(context.getResources().getColor(R.color.color_505050));

                // 设置文本--通过SpannableStringBuilder
                SpannableStringBuilder ssb = new SpannableStringBuilder();
                //再构造一个改变字体颜色的Span
                ForegroundColorSpan spanFrom = new ForegroundColorSpan(context.getResources().getColor(R.color.color_ff4c51));
                ForegroundColorSpan spanTo = new ForegroundColorSpan(context.getResources().getColor(R.color.color_ff4c51));
                int start = ssb.length();
                ssb.append(comment.getUserBase().getName());
                ssb.setSpan(spanFrom, start, ssb.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.append(" 回复 ");

                start = ssb.length();
                ssb.append(comment.getOriginUserBase().getString(GlobalConstants.JSON_NAME));
                ssb.setSpan(spanTo, start, ssb.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.append(" : ");
                ssb.append(comment.getContent());
                fontTextView.setText(ssb);

                floorCommentView.addView(fontTextView);

                fontTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCommentClickListener.onCommentClick(comment);
                    }
                });

                fontTextView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        onCommentLongClickListener.onCommentLongClick(comment);
                        return true;
                    }
                });

            }
        } else {
            floorCommentView.setVisibility(View.GONE);
            vLine.setVisibility(View.GONE);
        }

        likeComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!item.isHasLiked()) {
                    onLikeCommentClickListener.onLikeCommentClick(item, OnLikeCommentClickListener.TYPE_LIKE_COMMENT);
                } else {
                    onLikeCommentClickListener.onLikeCommentClick(item, OnLikeCommentClickListener.TYPE_UNLIKE_COMMENT);
                }
            }
        });
    }

}
