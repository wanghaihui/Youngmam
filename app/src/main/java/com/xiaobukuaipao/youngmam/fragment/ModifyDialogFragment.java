package com.xiaobukuaipao.youngmam.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.widget.YoungActionBar;

/**
 * Created by xiaobu1 on 15-6-2.
 */
@SuppressLint("ValidFragment")
public class ModifyDialogFragment extends DialogFragment {

    // ActionBar
    protected YoungActionBar actionBar;

    protected View view;

    private String name;

    private EditText modifyEditText;

    protected ModifyInfoListener modifyInfoListener;

    public ModifyDialogFragment(String name) {
        this.name = name;
    }

    public void setOnModifyInfoListener(ModifyInfoListener modifyInfoListener) {
        this.modifyInfoListener = modifyInfoListener;
    }

    /**
     * 必须重写的函数
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_modify_dialog, null);
        this.view = view;
        initViewsAndDatas();
        return view;
    }

    private void initViewsAndDatas() {
        // 添加ActionBar
        actionBar = (YoungActionBar) view.findViewById(R.id.action_bar);
        setYoungActionBar();

        modifyEditText = (EditText) view.findViewById(R.id.modify_name);
        modifyEditText.setText(name);
    }

    private void setYoungActionBar() {
        actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);
        actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_nickname));
        actionBar.setRightAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_save));

        /**
         * 设置监听器
         */
        actionBar.getLeftFrame().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        actionBar.getRightFrame().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyInfoListener.onDone(modifyEditText.getText().toString());
                dismiss();
            }
        });
    }
}
