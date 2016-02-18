package com.xiaobukuaipao.youngmam.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.photoview.PhotoView;
import com.xiaobukuaipao.youngmam.photoview.PhotoViewAttacher;

/**
 * Created by xiaobu1 on 15-5-19.
 */
@SuppressLint("ValidFragment")
public class AvatarDialogFragment extends DialogFragment {

    private String headUrl;

    private PhotoView avatar;

    public AvatarDialogFragment(String headUrl) {
        this.headUrl = headUrl;
    }

    /**
     * 必须重写的函数
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View avatarLayout = inflater.inflate(R.layout.fragment_avatar_dialog, null);
        avatar = (PhotoView) avatarLayout.findViewById(R.id.avatar);
        return avatarLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Picasso.with(this.getActivity()).load(headUrl).into(avatar);

        setUIListeners();
    }

    private void setUIListeners() {

        avatar.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                AvatarDialogFragment.this.dismiss();
            }
        });

    }
}
