package com.xiaobukuaipao.youngmam.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.adapter.ChildStatusAdapter;
import com.xiaobukuaipao.youngmam.domain.ChildStatus;

import java.util.List;

/**
 * Created by xiaobu1 on 15-5-19.
 */
public class PopupDialog extends Dialog {

    // 内容View
    View view;
    // 背景View
    View backView;

    Context context;

    ListView listView;

    List<ChildStatus> mChildStatusList;
    ChildStatusAdapter childStatusAdapter;

    StatusItemClickListener statusItemClickListener;

    public PopupDialog(Context context, List<ChildStatus> statusList) {
        super(context, android.R.style.Theme_Translucent);
        this.context = context;
        mChildStatusList = statusList;
    }

    public void setStatusItemClickListener(StatusItemClickListener statusItemClickListener) {
        this.statusItemClickListener = statusItemClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_dialog);

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

        childStatusAdapter = new ChildStatusAdapter(context, mChildStatusList, R.layout.item_string);
        listView.setAdapter(childStatusAdapter);

        setUIListeners();
    }

    private void setUIListeners() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                statusItemClickListener.onStatusItemClick((ChildStatus) parent.getItemAtPosition(position));
                dismiss();
            }
        });
    }

    @Override
    public void show() {
        super.show();
    }

    public interface StatusItemClickListener {
        public void onStatusItemClick(ChildStatus status);
    }
}
