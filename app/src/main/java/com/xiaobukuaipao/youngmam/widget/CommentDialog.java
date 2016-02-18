package com.xiaobukuaipao.youngmam.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.utils.StringUtil;

/**
 * Created by xiaobu1 on 15-4-27.
 */
public class CommentDialog extends Dialog {
    Context context;

    // 内容View
    View view;
    // 背景View
    View backView;

    String message;
    EditText commentEditText;

    String title;
    TextView titleTextView;

    ImageButton buttonClose;
    Button buttonSend;

    OnSendClickListener onSendClickListener;

    // 回复的评论id
    String originCommentId;

    // 文章的Id
    String articleId;

    public void setOnSendClickListener(OnSendClickListener onSendClickListener) {
        this.onSendClickListener = onSendClickListener;
    }

    public CommentDialog(Context context, String articleId, String title, String originCommentId) {
        super(context, android.R.style.Theme_Translucent);
        this.context = context;
        this.articleId = articleId;
        this.title = title;
        this.originCommentId = originCommentId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_dialog);

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

        titleTextView = (TextView) findViewById(R.id.title);
        setTitle(title);

        buttonClose = (ImageButton) findViewById(R.id.close);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentDialog.super.dismiss();
            }
        });

        commentEditText = (EditText) findViewById(R.id.comment_edit_text);

        commentEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        buttonSend = (Button) findViewById(R.id.send);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtil.isEmpty(commentEditText.getText().toString())) {
                    onSendClickListener.onSendClick(articleId, originCommentId, commentEditText.getText().toString());
                    CommentDialog.super.dismiss();
                } else {
                    Toast.makeText(getContext(), "内容不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void show() {
        super.show();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        if (title == null) {
            titleTextView.setVisibility(View.GONE);
        } else {
            titleTextView.setVisibility(View.VISIBLE);
            titleTextView.setText(title);
        }
    }

    public interface OnSendClickListener {
        public void onSendClick(String articleId, String originCommentId, String message);
    }
}
