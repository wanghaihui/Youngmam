package com.xiaobukuaipao.youngmam;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.greenrobot.event.EventBus;
import com.xiaobukuaipao.youngmam.adapter.CommentAdapter;
import com.xiaobukuaipao.youngmam.domain.Comment;
import com.xiaobukuaipao.youngmam.domain.CommentEvent;
import com.xiaobukuaipao.youngmam.domain.Topic;
import com.xiaobukuaipao.youngmam.domain.TopicCommentEvent;
import com.xiaobukuaipao.youngmam.emoji.BaseEmojiFragment;
import com.xiaobukuaipao.youngmam.emoji.Emoji;
import com.xiaobukuaipao.youngmam.emoji.EmojiFragment;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.listener.OnCommentClickListener;
import com.xiaobukuaipao.youngmam.listener.OnCommentLongClickListener;
import com.xiaobukuaipao.youngmam.listener.OnLikeCommentClickListener;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreContainer;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreHandler;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreListViewContainer;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.StringUtil;
import com.xiaobukuaipao.youngmam.widget.MaterialAlertDialog2;
import com.xiaobukuaipao.youngmam.widget.YoungActionBar;
import com.xiaobukuaipao.youngmam.wrap.BaseCommentFragmentActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-10-10.
 */
public class TopicCommentActivity extends BaseCommentFragmentActivity implements OnCommentClickListener,
                    BaseEmojiFragment.OnEmojiClickedListener, EmojiFragment.OnKeyboardBackspaceClickedListener,
                    OnLikeCommentClickListener, OnCommentLongClickListener {

    private static final String TAG = TopicCommentActivity.class.getSimpleName();

    private Topic topic;
    /**
     * 评论的翻页id
     */
    private long minCommentId = -1;

    private ListView mCommentListView;
    private CommentAdapter mCommentAdapter;
    private List<Comment> comments;

    private int commentCount;
    private TextView mCommentNum;
    /**
     * 整个的评论Layout
     */
    private LinearLayout commentLayout;

    private LoadMoreListViewContainer loadMoreListViewContainer;

    /**
     * 添加一条评论后, 自动刷新
     */
    private boolean isPullToRefresh = false;

    private View replyLayout;
    private LinearLayout containerLayout;

    // 表情按钮
    private ImageButton replyEmoji;
    // 输入框
    private EditText replyInput;
    // 发送
    private TextView replySend;

    private Handler mShowEmojiHandler = new Handler();

    private EmojiFragment emojiFragment;
    /**
     * Fragment管理
     */
    private FragmentManager fm;
    private FragmentTransaction transaction;

    private boolean isEmojiVisible = false;

    // 回复的Comment Id
    private String originCommentId = null;

    private LinearLayout replyBottomLayout;

    // EventBus
    private EventBus eventBus;

    private boolean deleteChildComment = false;

    public void initViews() {
        setContentView(R.layout.activity_topic_comment);

        // 带评论功能
        setHasComment(true);
        // 设置软键盘可以调整大小
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        eventBus = EventBus.getDefault();
        eventBus.register(this);

        // 设置ActionBar
        actionBar = (YoungActionBar) findViewById(R.id.action_bar);
        setYoungActionBar();

        getIntentDatas();

        mCommentListView = (ListView) findViewById(R.id.comment_list_view);
        addListViewHeader();

        comments = new ArrayList<Comment>();
        mCommentAdapter = new CommentAdapter(this, comments, R.layout.item_comment);
        mCommentAdapter.setFragmentManager(getSupportFragmentManager());
        mCommentAdapter.setOnCommentClickListener(this);
        mCommentAdapter.setOnLikeCommentClickListener(this);
        mCommentAdapter.setOnCommentLongClickListener(this);

        loadMoreListViewContainer = (LoadMoreListViewContainer) findViewById(R.id.load_more_container);
        loadMoreListViewContainer.useDefaultFooter2();
        // binding view and data
        mCommentListView.setAdapter(mCommentAdapter);

        loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                if (topic != null) {
                    mEventLogic.getComment(topic.getBusinessId(), String.valueOf(topic.getBusinessType()),
                            String.valueOf(minCommentId), -1);
                }
            }
        });

        /**
         * 整个的底部输入框架
         */
        replyBottomLayout = (LinearLayout) findViewById(R.id.reply_bottom_layout);

        // 回复布局
        replyLayout = (View) findViewById(R.id.reply);

        replyEmoji = (ImageButton) replyLayout.findViewById(R.id.reply_emoji);
        replyInput = (EditText) replyLayout.findViewById(R.id.reply_content);
        replySend = (TextView) replyLayout.findViewById(R.id.reply_send);
        // 表情布局
        containerLayout = (LinearLayout) findViewById(R.id.footer_for_reply);

        // 设置默认的Fragment
        setBasicFragment();

        setUIListeners();

        // 显示键盘--获取焦点
        replyInput.setFocusable(true);
        replyInput.setFocusableInTouchMode(true);
        replyInput.requestFocus();
        showKeyboard(replyInput);
    }

    /**
     * 设置缺省的Fragment
     */
    private void setBasicFragment() {
        fm = getSupportFragmentManager();
        transaction = fm.beginTransaction();
        emojiFragment = new EmojiFragment();

        transaction.replace(R.id.id_content, emojiFragment);
        transaction.commit();
    }

    /**
     * 设置ActionBar
     */
    private void setYoungActionBar() {
        actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);
        actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.comment));

        setBackClickListener(this);
    }

    private void getIntentDatas() {
        topic = getIntent().getParcelableExtra("topic");
        commentCount = getIntent().getIntExtra("comment_count", 0);
    }

    public void executeHttpRequest() {
        if (topic != null) {
            /**
             * 获取专题的评论列表
             */
            mEventLogic.getComment(topic.getBusinessId(), String.valueOf(topic.getBusinessType()),
                    String.valueOf(minCommentId), -1);
        }
    }

    private void addListViewHeader() {
        View view = this.getLayoutInflater().inflate(R.layout.topic_comment_header, null);

        mCommentNum = (TextView) view.findViewById(R.id.comment_num);
        mCommentNum.setText(this.getResources().getString(R.string.str_comment_num, commentCount));

        commentLayout = (LinearLayout) view.findViewById(R.id.comment_layout);
        commentLayout.setVisibility(View.INVISIBLE);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do nothing
            }
        });

        mCommentListView.addHeaderView(view);
    }

    /**
     * 设置布局监听器
     */
    private void setUIListeners() {

        mCommentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SharedPreferences sp = getSharedPreferences(SplashActivity.YOUNGMAM_UID, MODE_PRIVATE);
                if (sp.getLong(SplashActivity.UID, 0) > 0) {
                    Comment comment = (Comment) parent.getItemAtPosition(position);
                    String name = comment.getUserBase().getName();
                    originCommentId = comment.getCommentId();

                    // if (!comment.getUserBase().getString(GlobalConstants.JSON_USERID).equals(String.valueOf(sp.getLong(SplashActivity.UID, 0)))) {
                    // 此时, 回复--只能回复别人
                    prepareComment();
                    // 评论框设置回复人的姓名
                    replyInput.setHint("回复 " + name + " :");
                    //}
                }
            }
        });

        replyEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 改变Emoji的布局
                changeEmojiLayout();
            }
        });

        replyInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isEmojiVisible) {
                    containerLayout.setVisibility(View.GONE);
                    isEmojiVisible = false;
                    replyEmoji.setSelected(false);

                    showKeyboard(replyInput);
                }
                return false;
            }
        });

        replySend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendReply();
            }
        });

        mCommentListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences sp = getSharedPreferences(SplashActivity.YOUNGMAM_UID, MODE_PRIVATE);
                if (sp.getLong(SplashActivity.UID, 0) > 0) {
                    final Comment comment = (Comment) parent.getItemAtPosition(position);

                    if (comment.getUserBase().getUserId().equals(String.valueOf(sp.getLong(SplashActivity.UID, 0)))) {
                        // 删除评论
                        MaterialAlertDialog2 dialog = new MaterialAlertDialog2(TopicCommentActivity.this,
                                getResources().getString(R.string.comment), getResources().getString(R.string.is_delete_comment), null);

                        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mEventLogic.deleteComment(topic.getBusinessId(), comment.getCommentId());
                            }
                        });

                        dialog.setOnCancelButtonClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {

                            }
                        });

                        dialog.show();
                    }
                }
                return true;
            }
        });
    }

    /**
     * 评论的准备工作
     */
    private void prepareComment() {
        replyLayout.setVisibility(View.VISIBLE);

        replyInput.setFocusable(true);
        replyInput.setFocusableInTouchMode(true);
        replyInput.requestFocus();

        showKeyboard(replyInput);
    }

    /**
     * 发送回复
     */
    private void sendReply() {
        if (!StringUtil.isEmpty(replyInput.getText().toString())) {
            // Old
            mEventLogic.addComment(topic.getBusinessId(), String.valueOf(topic.getBusinessType()),
                    replyInput.getText().toString(), null, originCommentId);
        } else {
            Toast.makeText(this, "评论不能为空", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 初始化Emoji
     */
    private void changeEmojiLayout() {
        if (!isEmojiVisible) {

            hideKeyboard();

            if (replyEmoji.isSelected()) {
                replyEmoji.setSelected(false);
            } else {
                replyEmoji.setSelected(true);
            }

            mShowEmojiHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    containerLayout.setVisibility(View.VISIBLE);
                    isEmojiVisible = true;
                }
            }, 100);
        }
    }

    /**
     * 网络数据返回处理
     * @param msg
     */
    @Override
    public void onResponse(Message msg) {
        switch (msg.what) {
            case R.id.get_comment:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    if (jsonObject != null) {

                        JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_DATA);

                        if (jsonArray != null && jsonArray.size() > 0) {
                            commentLayout.setVisibility(View.VISIBLE);

                            if (isPullToRefresh) {
                                comments.clear();

                                if (deleteChildComment) {
                                    deleteChildComment = false;
                                    EventBus.getDefault().post(new TopicCommentEvent(topic.getBusinessId(), 1, CommentEvent.TYPE_DELETE_COMMENT));
                                } else {
                                    commentCount++;
                                    mCommentNum.setText(this.getResources().getString(R.string.str_fresh_comment_num, commentCount));
                                }
                            } else {
                                mCommentNum.setText(this.getString(R.string.str_fresh_comment_num, commentCount));
                            }

                            for(int i=0; i < jsonArray.size(); i++) {
                                comments.add(new Comment(jsonArray.getJSONObject(i)));
                            }

                            // 经典处理setSelection失效问题--下面三部曲
                            mCommentAdapter.notifyDataSetChanged();

                            if (isPullToRefresh) {
                                if (originCommentId == null) {
                                    mShowEmojiHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            mCommentListView.setSelection(0);
                                        }
                                    });
                                }

                                isPullToRefresh = false;
                            } else {
                                mCommentListView.clearFocus();
                                mCommentListView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mCommentListView.setSelection(0);
                                    }
                                });
                            }

                            minCommentId = jsonObject.getLong(GlobalConstants.JSON_MINCOMMENTID);

                            // load more complete
                            loadMoreListViewContainer.loadMoreFinish((jsonArray == null || (jsonArray != null && jsonArray.size() > 0)), minCommentId > 0);
                        } else {
                            loadMoreListViewContainer.loadMoreFinish(true, false);
                        }

                    }
                }
                break;

            case R.id.add_comment:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;

                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());
                    int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);

                    if (status == 0) {
                        isPullToRefresh = true;

                        // 此时, 发送评论成功, 隐藏所有的发送功能
                        replyInput.setText("");
                        replyInput.setHint("");

                        containerLayout.setVisibility(View.GONE);
                        isEmojiVisible = false;
                        replyEmoji.setSelected(false);

                        hideKeyboard();

                        if (jsonObject.containsKey(GlobalConstants.JSON_BONUSPOINT)) {
                            showCreditDialog(getResources().getString(R.string.str_dialog_credit,
                                    jsonObject.getInteger(GlobalConstants.JSON_BONUSPOINT)));
                        }

                        minCommentId = -1;

                        EventBus.getDefault().post(new TopicCommentEvent(topic.getBusinessId(), 1, CommentEvent.TYPE_ADD_COMMENT));

                        /**
                         * 获取文章的评论列表
                         */
                        mEventLogic.getComment(topic.getBusinessId(), String.valueOf(topic.getBusinessType()),
                                String.valueOf(minCommentId), -1);
                    }
                }

                break;

            case R.id.delete_comment:
                // 删除评论成功
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;

                    String commentId = msg.getData().getString("key");

                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());
                    int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);

                    // 代表删除成功
                    if (status == 0) {

                        if (!deleteChildComment) {
                            int count = 0;

                            for (int i = 0; i < comments.size(); i++) {
                                if (comments.get(i).getCommentId().equals(commentId)) {
                                    if (comments.get(i).getChildrenCommnets() != null &&
                                            comments.get(i).getChildrenCommnets().size() > 0) {
                                        count = comments.get(i).getChildrenCommnets().size() + 1;
                                    } else {
                                        count = 1;
                                    }

                                    comments.remove(i);
                                }
                            }

                            mCommentAdapter.notifyDataSetChanged();

                            EventBus.getDefault().post(new TopicCommentEvent(topic.getBusinessId(), count, CommentEvent.TYPE_DELETE_COMMENT));
                        } else {
                            isPullToRefresh = true;
                            minCommentId = -1;
                            /**
                             * 获取文章的评论列表
                             */
                            mEventLogic.getComment(topic.getBusinessId(), String.valueOf(topic.getBusinessType()),
                                    String.valueOf(minCommentId), -1);
                        }
                    }
                }
                break;
            case R.id.add_like_comment:

                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    String commentId = msg.getData().getString("key");
                    if (jsonObject != null) {

                        if (jsonObject.containsKey(GlobalConstants.JSON_LIKEID)) {

                            for (int i=0; i < comments.size(); i++) {
                                if (comments.get(i).getCommentId().equals(commentId)) {
                                    boolean hasLike = comments.get(i).isHasLiked();
                                    hasLike = !hasLike;
                                    comments.get(i).setHasLiked(hasLike);

                                    int likeCount = comments.get(i).getLikeCount();
                                    likeCount = likeCount + 1;
                                    comments.get(i).setLikeCount(likeCount);

                                    mCommentAdapter.notifyDataSetChanged();
                                }
                            }
                        }

                        if (jsonObject.containsKey(GlobalConstants.JSON_BONUSPOINT)) {
                            showCreditDialog(getResources().getString(R.string.str_dialog_credit,
                                    jsonObject.getInteger(GlobalConstants.JSON_BONUSPOINT)));
                        }
                    }
                }
                break;

            case R.id.delete_like_comment:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    String commentId = msg.getData().getString("key");
                    if (jsonObject != null) {

                        if (jsonObject.getInteger(GlobalConstants.JSON_STATUS) == 0) {

                            for (int i=0; i < comments.size(); i++) {
                                if (comments.get(i).getCommentId().equals(commentId)) {
                                    boolean hasLike = comments.get(i).isHasLiked();
                                    hasLike = !hasLike;
                                    comments.get(i).setHasLiked(hasLike);

                                    int likeCount = comments.get(i).getLikeCount();
                                    likeCount = likeCount - 1;
                                    comments.get(i).setLikeCount(likeCount);

                                    mCommentAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                }

                break;

            default:
                break;
        }
    }

    @Override
    public void onEmojiClicked(Emoji emoji) {
        EmojiFragment.input(replyInput, emoji);
    }

    @Override
    public void onKeyboardBackspaceClicked(View view, int type) {
        switch (type) {
            case EmojiFragment.OnKeyboardBackspaceClickedListener.TYPE_BACKSPACE:
                EmojiFragment.backspace(replyInput);
                break;
            case EmojiFragment.OnKeyboardBackspaceClickedListener.TYPE_KEYBOARD:
                containerLayout.setVisibility(View.GONE);
                isEmojiVisible = false;
                replyEmoji.setSelected(false);

                showKeyboard(replyInput);
                break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onCommentClick(Comment comment) {
        // 显示评论框
        originCommentId = comment.getCommentId();
        // 准备评论
        prepareComment();
        // 评论框设置回复人的姓名
        replyInput.setHint("回复 " + comment.getUserBase().getName() + " :");
    }

    @Override
    public void onCommentLongClick(final Comment comment) {
        SharedPreferences sp = getSharedPreferences(SplashActivity.YOUNGMAM_UID, MODE_PRIVATE);
        if (sp.getLong(SplashActivity.UID, 0) > 0) {

            if (comment.getUserBase().getUserId().equals(String.valueOf(sp.getLong(SplashActivity.UID, 0)))) {
                // 删除评论
                final MaterialAlertDialog2 dialog = new MaterialAlertDialog2(TopicCommentActivity.this,
                        getResources().getString(R.string.comment), getResources().getString(R.string.is_delete_comment), null);

                dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteChildComment = true;
                        mEventLogic.deleteComment(topic.getBusinessId(), comment.getCommentId());
                    }
                });

                dialog.setOnCancelButtonClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                    }
                });

                dialog.show();
            }
        }
    }

    @Override
    public void onLikeCommentClick(Comment comment, int type) {
        if (type == OnLikeCommentClickListener.TYPE_LIKE_COMMENT) {
            mEventLogic.addLikeComment(comment.getArticleId(), String.valueOf(comment.getBusinessType()), comment.getCommentId());
        } else {
            mEventLogic.deleteLikeComment(comment.getArticleId(), String.valueOf(comment.getBusinessType()), comment.getCommentId());
        }
    }

    @Override
    public void hideInput(MotionEvent ev) {
        if (isShouldHideInput(replyBottomLayout, ev)) {
            handleHide();
        }
    }

    public boolean isShouldHideInput(View view, MotionEvent ev) {
        if (view != null && (view instanceof LinearLayout)) {
            int[] l = {0, 0};
            view.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + view.getHeight(),
                    right = left + view.getWidth();
            if (ev.getX() > left && ev.getX() < right
                    && ev.getY() > top && ev.getY() < bottom) {
                // 点击EditText的事件，忽略
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }

    public void handleHide() {
        containerLayout.setVisibility(View.GONE);
        isEmojiVisible = false;
        replyEmoji.setSelected(false);

        originCommentId = null;
        replyInput.setText("");
        replyInput.setHint("");

        hideKeyboard();
    }

    public void onEvent(TopicCommentEvent topicCommentEvent) {
        int count = topicCommentEvent.getCount();

        if (topicCommentEvent.getType() == CommentEvent.TYPE_DELETE_COMMENT) {
            commentCount = commentCount - count;
            mCommentNum.setText(this.getResources().getString(R.string.str_comment, commentCount));
        }
    }
}
