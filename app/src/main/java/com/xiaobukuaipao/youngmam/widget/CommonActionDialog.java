package com.xiaobukuaipao.youngmam.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.adapter.CommonActionAdapter;
import com.xiaobukuaipao.youngmam.domain.CommonAction;

import java.util.List;

/**
 * Created by xiaobu1 on 15-11-3.
 */
public class CommonActionDialog extends Dialog {

    // 内容View
    View view;
    // 背景View
    View backView;

    Context context;

    ListView listView;

    List<CommonAction> commonActionList;
    CommonActionAdapter commonActionAdapter;

    OnCommonActionClickListener onCommonActionClickListener;

    public CommonActionDialog(Context context, List<CommonAction> commonActionList) {
        super(context, android.R.style.Theme_Translucent);
        this.context = context;
        this.commonActionList = commonActionList;
    }

    public void setOnCommonActionClickListener(OnCommonActionClickListener onCommonActionClickListener) {
        this.onCommonActionClickListener = onCommonActionClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_popup_dialog);

        view = (RelativeLayout) findViewById(R.id.dialog_content);
        backView = (RelativeLayout) findViewById(R.id.dialog_root_view);
        backView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getX() < view.getLeft()
                        || event.getX() >view.getRight()
                        || event.getY() > view.getBottom()
                        || event.getY() < view.getTop()) {
                    dismiss();
                }
                return false;
            }
        });

        listView = (ListView) findViewById(R.id.list_view);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        commonActionAdapter = new CommonActionAdapter(context, commonActionList, R.layout.item_string);
        listView.setAdapter(commonActionAdapter);

        setUIListeners();
    }

    private void setUIListeners() {
        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(android.widget.AdapterView<?> parent, View view, int position, long id) {
                onCommonActionClickListener.onCommonActionClick((CommonAction) parent.getItemAtPosition(position));
                dismiss();
            }
        });
    }

    @Override
    public void show() {
        super.show();
    }

    public interface OnCommonActionClickListener {
        void onCommonActionClick(CommonAction commonAction);
    }
}
