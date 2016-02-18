package com.xiaobukuaipao.youngmam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.greenrobot.event.EventBus;
import com.squareup.picasso.Picasso;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.xiaobukuaipao.youngmam.adapter.CommentAdapter;
import com.xiaobukuaipao.youngmam.domain.Article;
import com.xiaobukuaipao.youngmam.domain.ArticleCommentEvent;
import com.xiaobukuaipao.youngmam.domain.ArticleDeleteEvent;
import com.xiaobukuaipao.youngmam.domain.Comment;
import com.xiaobukuaipao.youngmam.domain.CommentEvent;
import com.xiaobukuaipao.youngmam.domain.FollowEvent;
import com.xiaobukuaipao.youngmam.domain.Label;
import com.xiaobukuaipao.youngmam.domain.MineQuestionDelayRefreshEvent;
import com.xiaobukuaipao.youngmam.domain.SizeImage;
import com.xiaobukuaipao.youngmam.domain.Theme;
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
import com.xiaobukuaipao.youngmam.utils.TimeUtil;
import com.xiaobukuaipao.youngmam.widget.LabelListView2;
import com.xiaobukuaipao.youngmam.widget.MaterialAlertDialog2;
import com.xiaobukuaipao.youngmam.widget.NineGridLayout;
import com.xiaobukuaipao.youngmam.widget.YoungActionBar;
import com.xiaobukuaipao.youngmam.widget.YoungRefreshLayout;
import com.xiaobukuaipao.youngmam.widget.YoungShareBoard;
import com.xiaobukuaipao.youngmam.wrap.BaseCommentFragmentActivity;
import com.xiaobukuaipao.youngmam.wrap.BaseHttpFragmentActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-9-25.
 */
public class QuestionDetailsActivity extends BaseCommentFragmentActivity implements LabelListView2.OnLabelClickListener,
        BaseEmojiFragment.OnEmojiClickedListener, EmojiFragment.OnKeyboardBackspaceClickedListener, OnLikeCommentClickListener,
        OnCommentClickListener, OnCommentLongClickListener, YoungShareBoard.OnShareSuccessListener {

    private static final String TAG = QuestionDetailsActivity.class.getSimpleName();

    private long articleId = -1;

    private ImageView avatar;
    private TextView name;
    private ImageView geek;
    private TextView time;

    private TextView follow;

    private TextView introduce;
    private NineGridLayout nineGridLayout;
    // 标签
    private LabelListView2 labelsView;

    private TextView commentText;

    private ListView mCommentListView;
    private CommentAdapter mCommentAdapter;
    private List<Comment> comments;

    private LoadMoreListViewContainer loadMoreListViewContainer;
    // 下拉刷新 -- SwipeRefreshLayout
    private YoungRefreshLayout swipeRefreshLayout;

    private LinearLayout bottomLayout;

    /**
     * 评论的翻页id
     */
    private long minCommentId = -1;

    private Article article;

    /**
     * 整个的评论Layout
     */
    private LinearLayout commentLayout;

    private boolean refresh = false;

    /**
     * 下拉刷新
     */
    private boolean isPullToRefresh = false;

    private boolean deleteChildComment = false;

    // 回复的Comment Id
    private String originCommentId = null;

    private Handler mShowEmojiHandler = new Handler();

    private TextView answer;
    private TextView share;

    // EventBus
    private EventBus eventBus;

    private LinearLayout replyBottomLayout;
    private View replyLayout;
    private LinearLayout containerLayout;

    // 表情按钮
    private ImageButton replyEmoji;
    // 输入框
    private EditText replyInput;
    // 发送
    private TextView replySend;

    /**
     * Fragment管理
     */
    private FragmentManager fm;
    private FragmentTransaction transaction;

    private EmojiFragment emojiFragment;

    private boolean isEmojiVisible = false;

    private boolean commentFlag = false;

    /**
     * 分享控制器
     */
    final UMSocialService mController = UMServiceFactory.getUMSocialService(GlobalConstants.UMENG_DESCRIPTOR);

    public void initViews() {
        setContentView(R.layout.activity_question_details);

        getIntentDatas();

        eventBus = EventBus.getDefault();
        eventBus.register(this);

        // 带评论功能
        setHasComment(true);
        // 设置软键盘可以调整大小
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        // 设置ActionBar
        actionBar = (YoungActionBar) findViewById(R.id.action_bar);
        setYoungActionBar();

        mCommentListView = (ListView) findViewById(R.id.comment_list_view);
        addListViewHeader();

        comments = new ArrayList<Comment>();
        mCommentAdapter = new CommentAdapter(this, comments, R.layout.item_comment);
        mCommentAdapter.setFragmentManager(getSupportFragmentManager());
        mCommentAdapter.setOnCommentClickListener(this);
        mCommentAdapter.setOnLikeCommentClickListener(this);
        mCommentAdapter.setOnCommentLongClickListener(this);

        swipeRefreshLayout = (YoungRefreshLayout) findViewById(R.id.ptr_frame);
        swipeRefreshLayout.setVisibility(View.GONE);

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        refresh = true;
                        minCommentId = -1;
                        if (article != null) {
                            mEventLogic.getComment(article.getArticleId(), String.valueOf(article.getBusinessType()), String.valueOf(minCommentId), -1);
                        }
                    }
                }, 1000);
            }
        });

        loadMoreListViewContainer = (LoadMoreListViewContainer) findViewById(R.id.load_more_container);
        loadMoreListViewContainer.useDefaultFooter2();
        // binding view and data
        mCommentListView.setAdapter(mCommentAdapter);
        loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                if (article != null) {
                    mEventLogic.getComment(article.getArticleId(), String.valueOf(article.getBusinessType()), String.valueOf(minCommentId), -1);

                }
            }
        });

        /**
         * 整个的底部输入框架
         */
        replyBottomLayout = (LinearLayout) findViewById(R.id.reply_bottom_layout);

        // 底部布局
        bottomLayout = (LinearLayout) findViewById(R.id.bottom_layout);
        bottomLayout.setVisibility(View.INVISIBLE);

        // 回复布局
        replyLayout = (View) findViewById(R.id.reply);
        replyLayout.setVisibility(View.INVISIBLE);
        // replyPhoto = (ImageButton) replyLayout.findViewById(R.id.reply_photo);
        replyEmoji = (ImageButton) replyLayout.findViewById(R.id.reply_emoji);
        replyInput = (EditText) replyLayout.findViewById(R.id.reply_content);
        replySend = (TextView) replyLayout.findViewById(R.id.reply_send);
        // 表情布局
        containerLayout = (LinearLayout) findViewById(R.id.footer_for_reply);

        answer = (TextView) findViewById(R.id.answer);
        share = (TextView) findViewById(R.id.share);

        // 配置需要分享的相关平台
        configPlatforms(mController);

        // 设置默认的Fragment
        setBasicFragment();

        setUIListeners();

        // 如果开始就评论
        if (commentFlag) {
            answerQuestion();
        }
    }

    private void getIntentDatas() {
        articleId = Long.valueOf(getIntent().getStringExtra("article_id"));
        commentFlag = getIntent().getBooleanExtra("comment_flag", false);
    }

    /**
     * 设置ActionBar
     */
    private void setYoungActionBar() {
        actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);
        actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_fresh_detail));
        setBackClickListener(this);
    }

    private void addListViewHeader() {
        View view = this.getLayoutInflater().inflate(R.layout.question_detail_header, null);

        avatar = (ImageView) view.findViewById(R.id.avatar);
        name = (TextView) view.findViewById(R.id.name);
        geek = (ImageView) view.findViewById(R.id.geek);
        time = (TextView) view.findViewById(R.id.time);

        follow = (TextView) view.findViewById(R.id.follow);

        introduce = (TextView) view.findViewById(R.id.introduce);
        nineGridLayout = (NineGridLayout) view.findViewById(R.id.nine_grid_layout);
        labelsView = (LabelListView2) view.findViewById(R.id.article_label_list);

        commentLayout = (LinearLayout) view.findViewById(R.id.comment_layout);
        commentLayout.setVisibility(View.INVISIBLE);

        commentText = (TextView) view.findViewById(R.id.comment_num);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mCommentListView.addHeaderView(view);
    }

    public void executeHttpRequest() {
        /**
         * 获得问答详情
         */
        mEventLogic.getQuestionDetail(String.valueOf(articleId));
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

    private void setUIListeners() {
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (article != null) {
                    Intent intent = new Intent(QuestionDetailsActivity.this, OtherActivity.class);
                    intent.putExtra("userId", article.getUserBase().getUserId());
                    intent.putExtra("userName", article.getUserBase().getName());
                    startActivity(intent);
                }
            }
        });

        /**
         * 评论
         */
        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answerQuestion();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });

        mCommentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SharedPreferences sp = getSharedPreferences(SplashActivity.YOUNGMAM_UID, MODE_PRIVATE);
                if (sp.getLong(SplashActivity.UID, 0) > 0) {
                    Comment comment = (Comment) parent.getItemAtPosition(position);
                    if (comment != null) {
                        String name = comment.getUserBase().getName();
                        originCommentId = comment.getCommentId();

                        prepareComment();
                        // 评论框设置回复人的姓名
                        replyInput.setHint("回复 " + name + " :");
                    }
                }
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
                        MaterialAlertDialog2 dialog = new MaterialAlertDialog2(QuestionDetailsActivity.this,
                                getResources().getString(R.string.comment), getResources().getString(R.string.is_delete_comment), null);

                        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mEventLogic.cancelAll();
                                if (article != null) {
                                    mEventLogic.deleteComment(article.getArticleId(), comment.getCommentId());
                                }
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

        /**
         * 关注按钮
         */
        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                follow();
            }
        });
    }

    /**
     * 关注
     */
    private void follow() {
        if (article != null) {
            if (article.getUserBase().isHasFollowed()) {
                // 此时, 取消关注
                mEventLogic.deleteFollow(article.getUserBase().getUserId());
            } else {
                // 此时, 加关注
                mEventLogic.addFollow(article.getUserBase().getUserId());
            }
        }
    }

    /**
     * 发送回复
     */
    private void sendReply() {
        if (!StringUtil.isEmpty(replyInput.getText().toString())) {
            mEventLogic.addComment(article.getArticleId(), String.valueOf(article.getBusinessType()), replyInput.getText().toString(), null, originCommentId);
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
     * 评论文章
     */
    private void answerQuestion() {
        SharedPreferences sp = getSharedPreferences(SplashActivity.YOUNGMAM_UID, MODE_PRIVATE);
        if (sp.getLong(SplashActivity.UID, 0) > 0) {
            // 显示评论框
            originCommentId = null;
            // 准备评论
            prepareComment();
        } else {
            // 跳到登录页
            Intent intent = new Intent(QuestionDetailsActivity.this, RegisterAndLoginActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 分享
     */
    private void share() {
        String targetUrl = GlobalConstants.SHARE_ARTICLE + article.getArticleId();
        String shareContent = article.getContent();
        String imageUrl = null;
        if (article.getImgs() != null && article.getImgs().size() > 0) {
            imageUrl = article.getImgs().getJSONObject(0).getString(GlobalConstants.JSON_URL);
        }

        YoungShareBoard shareBoard = new YoungShareBoard(this);
        shareBoard.setShareParams(article.getArticleId(), article.getBusinessType());
        shareBoard.setOnShareSuccessListener(this);
        shareBoard.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
        setShareContent(mController, targetUrl, shareContent, imageUrl, BaseHttpFragmentActivity.SHARE_TYPE_QUESTION);

        // 微博
        mController.setShareContent(StringUtil.buildWeiboShareQuestion(shareContent, targetUrl));
        mController.setShareMedia(new UMImage(this, imageUrl));
    }

    /**
     * 评论的准备工作
     */
    private void prepareComment() {
        bottomLayout.setVisibility(View.GONE);
        replyLayout.setVisibility(View.VISIBLE);

        replyInput.setFocusable(true);
        replyInput.setFocusableInTouchMode(true);
        replyInput.requestFocus();

        showKeyboard(replyInput);
    }

    /**
     * 网络数据返回处理
     * @param msg
     */
    @Override
    public void onResponse(Message msg) {
        switch (msg.what) {
            case R.id.get_question_detail:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                    bottomLayout.setVisibility(View.VISIBLE);

                    if (jsonObject != null) {
                        // 首先判断此文章是否存在
                        if (jsonObject.getInteger(GlobalConstants.JSON_STATUS) != null) {
                            int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);
                            if (status == 22) {
                                // 此时代表文章已经被删除
                                // 弹出Dialog
                                bottomLayout.setVisibility(View.GONE);

                                MaterialAlertDialog2 dialog = new MaterialAlertDialog2(QuestionDetailsActivity.this,
                                        getResources().getString(R.string.str_delete), getResources().getString(R.string.this_question_deleted), null);
                                dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        finish();
                                    }
                                });

                                dialog.show();
                                // 不可见
                                dialog.setButtonCancelVisible(false);
                                return;
                            }
                        }

                        article = new Article(jsonObject);

                        if (article.getUserBase() != null) {

                            SharedPreferences sp = getSharedPreferences(SplashActivity.YOUNGMAM_UID, MODE_PRIVATE);
                            if (sp.getLong(SplashActivity.UID, 0) > 0) {
                                if (article.getUserBase().getUserId().equals(String.valueOf(sp.getLong(SplashActivity.UID, 0)))) {
                                    actionBar.setRightAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_delete));
                                    actionBar.getRightFrame().setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            deleteQuestion();
                                        }
                                    });

                                    follow.setVisibility(View.GONE);

                                } else {

                                    follow.setVisibility(View.VISIBLE);
                                    if (article.getUserBase().isHasFollowed()) {
                                        // 此时已经关注
                                        follow.setSelected(true);
                                        follow.setText(getResources().getString(R.string.has_followed));
                                        follow.setTextColor(getResources().getColor(R.color.color_ff4c51));
                                    } else {
                                        // 此时没有关注
                                        follow.setSelected(false);
                                        follow.setText(getResources().getString(R.string.add_follow));
                                        follow.setTextColor(getResources().getColor(R.color.white));
                                    }

                                }
                            } else {
                                follow.setVisibility(View.GONE);
                            }

                            Picasso.with(this).
                                    load(article.getUserBase().getHeadUrl())
                                    .placeholder(R.mipmap.default_article_mama)
                                    .fit()
                                    .centerCrop()
                                    .into(avatar);

                            name.setText(article.getUserBase().getName());

                            if (article.getUserBase().getExpertType() != 0) {
                                geek.setVisibility(View.VISIBLE);
                            } else {
                                geek.setVisibility(View.GONE);
                            }
                        }

                        time.setText(TimeUtil.handleTime(this, article.getCreateTime()));

                        if (!StringUtil.isEmpty(article.getContent())) {
                            introduce.setVisibility(View.VISIBLE);
                            introduce.setText(article.getContent());
                        } else {
                            introduce.setVisibility(View.GONE);
                        }

                        if (article.getImgs() != null && article.getImgs().size() > 0) {
                            ArrayList<SizeImage> images = new ArrayList<SizeImage>();

                            int length = article.getImgs().size();

                            for(int i=0; i < length; i++) {
                                images.add(new SizeImage(article.getImgs().getJSONObject(i)));
                            }

                            nineGridLayout.setVisibility(View.VISIBLE);
                            nineGridLayout.setImageDatas(images, getSupportFragmentManager());
                        } else {
                            nineGridLayout.setVisibility(View.GONE);
                        }

                        if (article.getTags() != null && article.getTags().size() > 0) {
                            ((View) labelsView.getParent()).setVisibility(View.VISIBLE);

                            ArrayList<Label> labels = new ArrayList<Label>();
                            for(int i = 0; i < article.getTags().size(); i++) {
                                labels.add(new Label(article.getTags().getJSONObject(i)));
                            }

                            labelsView.setLabels(labels);
                            labelsView.setOnLabelClickListener(this);
                        } else {
                            ((View) labelsView.getParent()).setVisibility(View.GONE);
                        }

                        commentText.setText(this.getResources().getString(R.string.str_fresh_like_num, article.getCommentCount()));

                        answer.setText(this.getResources().getString(R.string.str_answer_count, article.getCommentCount()));

                        if (article.getArticleId() != null) {
                            /**
                             * 获取问题的评论列表
                             */
                            mEventLogic.getComment(article.getArticleId(), String.valueOf(article.getBusinessType()), String.valueOf(minCommentId), -1);
                        }
                    }

                }

                break;

            case R.id.get_comment:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;

                    if (httpResult.getData() != null) {

                        JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                        if (jsonObject != null) {

                            // 在这里操作数据
                            JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_DATA);

                            commentLayout.setVisibility(View.VISIBLE);

                            if (refresh) {
                                comments.clear();
                                swipeRefreshLayout.setRefreshing(false);
                                refresh = false;
                            }

                            if (isPullToRefresh) {
                                comments.clear();
                                if (deleteChildComment) {
                                    deleteChildComment = false;
                                }
                            }

                            commentText.setText(this.getString(R.string.str_fresh_comment_num, article.getCommentCount()));

                            if (jsonArray != null && jsonArray.size() > 0) {
                                for (int i = 0; i < jsonArray.size(); i++) {
                                    comments.add(new Comment(jsonArray.getJSONObject(i)));
                                }
                            }

                            mCommentAdapter.notifyDataSetChanged();

                            // 不显示评论列表
                            if (comments != null && comments.size() > 0) {
                                commentLayout.setVisibility(View.VISIBLE);
                            } else {
                                commentLayout.setVisibility(View.INVISIBLE);
                            }

                            if (isPullToRefresh) {

                                if (originCommentId == null) {
                                    mShowEmojiHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            mCommentListView.setSelection(1);
                                        }
                                    });
                                }

                                isPullToRefresh = false;
                            }

                            minCommentId = jsonObject.getLong(GlobalConstants.JSON_MINCOMMENTID);

                            // load more complete
                            loadMoreListViewContainer.loadMoreFinish((jsonArray == null || (jsonArray != null && jsonArray.size() > 0)), minCommentId > 0);
                        } else {
                            loadMoreListViewContainer.loadMoreFinish(true, false);

                            swipeRefreshLayout.setRefreshing(false);

                            // 不显示评论列表
                            if (comments != null && comments.size() > 0) {
                                commentLayout.setVisibility(View.VISIBLE);
                            } else {
                                commentLayout.setVisibility(View.INVISIBLE);
                            }
                        }
                    } else {
                        swipeRefreshLayout.setRefreshing(false);

                        // 此时没有返回数据-- 不显示评论列表
                        if (comments != null && comments.size() > 0) {
                            commentLayout.setVisibility(View.VISIBLE);
                        } else {
                            commentLayout.setVisibility(View.INVISIBLE);
                        }
                    }
                } else {
                    swipeRefreshLayout.setRefreshing(false);

                    if (comments != null && comments.size() > 0) {
                        commentLayout.setVisibility(View.VISIBLE);
                    } else {
                        commentLayout.setVisibility(View.INVISIBLE);
                    }
                }
                break;

            case R.id.add_comment:
                // 发送评论成功
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;

                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());
                    int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);

                    if (status == 0) {
                        isPullToRefresh = true;

                        // 此时, 发送评论成功, 隐藏所有的发送功能
                        replyInput.setText("");

                        bottomLayout.setVisibility(View.VISIBLE);
                        replyLayout.setVisibility(View.GONE);

                        containerLayout.setVisibility(View.GONE);
                        isEmojiVisible = false;
                        replyEmoji.setSelected(false);

                        hideKeyboard();

                        if (jsonObject.containsKey(GlobalConstants.JSON_BONUSPOINT)) {
                            showCreditDialog(getResources().getString(R.string.str_dialog_credit,
                                    jsonObject.getInteger(GlobalConstants.JSON_BONUSPOINT)));
                        }

                        EventBus.getDefault().post(new ArticleCommentEvent(article.getArticleId(), 1, CommentEvent.TYPE_ADD_COMMENT));

                        /**
                         * 获取文章的评论列表
                         */
                        mEventLogic.getComment(article.getArticleId(), String.valueOf(article.getBusinessType()), String.valueOf(-1), -1);
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

                            EventBus.getDefault().post(new ArticleCommentEvent(article.getArticleId(), count, CommentEvent.TYPE_DELETE_COMMENT));

                            if (article.getCommentCount() > 0) {
                                commentLayout.setVisibility(View.VISIBLE);
                                commentText.setText(this.getString(R.string.str_fresh_comment_num, article.getCommentCount()));
                            } else {
                                commentLayout.setVisibility(View.GONE);
                            }
                        } else {
                            isPullToRefresh = true;
                            minCommentId = -1;

                            EventBus.getDefault().post(new ArticleCommentEvent(article.getArticleId(), 1, CommentEvent.TYPE_DELETE_COMMENT));
                            /**
                             * 获取文章的评论列表
                             */
                            mEventLogic.getComment(article.getArticleId(), String.valueOf(article.getBusinessType()), String.valueOf(minCommentId), -1);
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

            case R.id.delete_question:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    String articleId = msg.getData().getString("key");

                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());
                    int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);

                    // 此时,代表删除文章成功
                    if (status == 0) {
                        if (articleId != null) {
                            EventBus.getDefault().post(new MineQuestionDelayRefreshEvent(true));
                            EventBus.getDefault().post(new ArticleDeleteEvent(articleId));
                            finish();
                        }
                    }

                }
                break;

            case R.id.add_follow:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    String userId = msg.getData().getString("key");

                    if (jsonObject != null) {
                        if (jsonObject.getInteger(GlobalConstants.JSON_STATUS) == 0) {
                            EventBus.getDefault().post(new FollowEvent(userId, FollowEvent.TYPE_ADD_FOLLOW));
                        }
                    }
                }
                break;
            case R.id.delete_follow:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    String userId = msg.getData().getString("key");

                    if (jsonObject != null) {
                        if (jsonObject.getInteger(GlobalConstants.JSON_STATUS) == 0) {
                            EventBus.getDefault().post(new FollowEvent(userId, FollowEvent.TYPE_DELETE_FOLLOW));
                        }
                    }
                }
                break;

            case R.id.share_bonus:
                // 发送评论成功
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;

                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());
                    int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);

                    if (status == 0) {
                        // 3
                        if (jsonObject.containsKey(GlobalConstants.JSON_BONUSPOINT)) {
                            showCreditDialog(getResources().getString(R.string.str_dialog_credit,
                                    jsonObject.getInteger(GlobalConstants.JSON_BONUSPOINT)));
                        }
                    }
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onLabelClick(TextView labelView, Label label) {
        Theme theme = new Theme(null, null, new Label(label.getId(), label.getTitle()));
        Intent intent = new Intent(this, SearchDetailActivity.class);
        intent.putExtra("tag_id", label.getId());
        intent.putExtra("share_businessType", SearchDetailActivity.TYPE_BUSINESS_QUESTION);
        intent.putExtra("publish_type", SearchDetailActivity.TYPE_PUBLISH_QUESTION);
        startActivity(intent);
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
        bottomLayout.setVisibility(View.VISIBLE);

        replyLayout.setVisibility(View.GONE);

        containerLayout.setVisibility(View.GONE);
        isEmojiVisible = false;
        replyEmoji.setSelected(false);

        hideKeyboard();

        // 显示评论框
        originCommentId = null;
        replyInput.setHint("");
    }

    @Override
    public void onBackPressed() {
        if (replyLayout.getVisibility() == View.VISIBLE) {
            handleHide();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onLikeCommentClick(Comment comment, int type) {
        SharedPreferences sp = getSharedPreferences(SplashActivity.YOUNGMAM_UID, MODE_PRIVATE);
        if (sp.getLong(SplashActivity.UID, 0) > 0) {

            if (type == OnLikeCommentClickListener.TYPE_LIKE_COMMENT) {
                mEventLogic.addLikeComment(comment.getArticleId(), String.valueOf(comment.getBusinessType()), comment.getCommentId());
            } else {
                mEventLogic.deleteLikeComment(comment.getArticleId(), String.valueOf(comment.getBusinessType()), comment.getCommentId());
            }

        } else {
            // 跳到登录页
            Intent intent = new Intent(QuestionDetailsActivity.this, RegisterAndLoginActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 重写--Comment点击接口
     */
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
                MaterialAlertDialog2 dialog = new MaterialAlertDialog2(QuestionDetailsActivity.this,
                        getResources().getString(R.string.comment), getResources().getString(R.string.is_delete_comment), null);

                dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteChildComment = true;
                        mEventLogic.deleteComment(article.getArticleId(), comment.getCommentId());
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

    /**
     * 删除这篇文章
     */
    private void deleteQuestion() {
        MaterialAlertDialog2 dialog = new MaterialAlertDialog2(this,
                getResources().getString(R.string.mine_publish), getResources().getString(R.string.is_delete_question), null);
        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEventLogic.cancelAll();
                if (article != null) {
                    mEventLogic.deleteQuestion(article.getArticleId());
                }
            }
        });
        dialog.setOnCancelButtonClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

        dialog.show();
    }

    public void onEvent(FollowEvent followEvent) {
        if (followEvent.getType() == FollowEvent.TYPE_ADD_FOLLOW) {
            // 此时, 添加关注
            boolean hasFollowed = article.getUserBase().isHasFollowed();
            hasFollowed = !hasFollowed;
            article.getUserBase().setHasFollowed(hasFollowed);

            if (article.getUserBase().isHasFollowed()) {
                // 此时已经关注
                follow.setSelected(true);
                follow.setText(getResources().getString(R.string.has_followed));
                follow.setTextColor(getResources().getColor(R.color.color_ff4c51));
            }
        } else if (followEvent.getType() == FollowEvent.TYPE_DELETE_FOLLOW) {
            // 此时, 取消关注
            boolean hasFollowed = article.getUserBase().isHasFollowed();
            hasFollowed = !hasFollowed;
            article.getUserBase().setHasFollowed(hasFollowed);

            if (!article.getUserBase().isHasFollowed()) {
                // 此时没有关注
                follow.setSelected(false);
                follow.setText(getResources().getString(R.string.add_follow));
                follow.setTextColor(getResources().getColor(R.color.white));
            }
        }
    }

    public void onEvent(ArticleCommentEvent articleCommentEvent) {
        if (articleCommentEvent.getType() == CommentEvent.TYPE_DELETE_COMMENT) {
            article.setCommentCount(article.getCommentCount() - articleCommentEvent.getCount());
            answer.setText(this.getResources().getString(R.string.str_comment, article.getCommentCount()));
        } else if (articleCommentEvent.getType() == CommentEvent.TYPE_ADD_COMMENT) {
            article.setCommentCount(article.getCommentCount() + articleCommentEvent.getCount());
            answer.setText(this.getResources().getString(R.string.str_comment, article.getCommentCount()));
        }
    }

    @Override
    public void onShareSuccess(String articleId, int shareType) {
        mEventLogic.shareBonus(articleId, shareType);
    }
}
