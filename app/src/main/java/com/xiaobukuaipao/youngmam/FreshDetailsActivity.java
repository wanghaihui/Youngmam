package com.xiaobukuaipao.youngmam;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.greenrobot.event.EventBus;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMSsoHandler;
import com.xiaobukuaipao.youngmam.adapter.CommentAdapter;
import com.xiaobukuaipao.youngmam.adapter.HorizontalImageAdapter;
import com.xiaobukuaipao.youngmam.adapter.WrapContentLinearLayoutManager;
import com.xiaobukuaipao.youngmam.cache.YoungCache;
import com.xiaobukuaipao.youngmam.database.CookieTable;
import com.xiaobukuaipao.youngmam.domain.Article;
import com.xiaobukuaipao.youngmam.domain.ArticleCommentEvent;
import com.xiaobukuaipao.youngmam.domain.ArticleDeleteEvent;
import com.xiaobukuaipao.youngmam.domain.ArticleLikeEvent;
import com.xiaobukuaipao.youngmam.domain.Comment;
import com.xiaobukuaipao.youngmam.domain.CommentEvent;
import com.xiaobukuaipao.youngmam.domain.FollowEvent;
import com.xiaobukuaipao.youngmam.domain.Label;
import com.xiaobukuaipao.youngmam.domain.LikeCommentNumberEvent;
import com.xiaobukuaipao.youngmam.domain.MinePublishDelayRefreshEvent;
import com.xiaobukuaipao.youngmam.domain.SizeImage;
import com.xiaobukuaipao.youngmam.domain.Theme;
import com.xiaobukuaipao.youngmam.domain.UserBase;
import com.xiaobukuaipao.youngmam.emoji.BaseEmojiFragment;
import com.xiaobukuaipao.youngmam.emoji.Emoji;
import com.xiaobukuaipao.youngmam.emoji.EmojiFragment;
import com.xiaobukuaipao.youngmam.fragment.PhotoViewFragment;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.listener.OnCommentClickListener;
import com.xiaobukuaipao.youngmam.listener.OnCommentLongClickListener;
import com.xiaobukuaipao.youngmam.listener.OnLikeCommentClickListener;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreContainer;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreHandler;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreListViewContainer;
import com.xiaobukuaipao.youngmam.provider.YoungContentProvider;
import com.xiaobukuaipao.youngmam.utils.DisplayUtil;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.StringUtil;
import com.xiaobukuaipao.youngmam.utils.TimeUtil;
import com.xiaobukuaipao.youngmam.widget.LabelListView2;
import com.xiaobukuaipao.youngmam.widget.MaterialAlertDialog2;
import com.xiaobukuaipao.youngmam.widget.YoungActionBar;
import com.xiaobukuaipao.youngmam.widget.YoungRefreshLayout;
import com.xiaobukuaipao.youngmam.widget.YoungShareBoard;
import com.xiaobukuaipao.youngmam.wrap.BaseCommentFragmentActivity;
import com.xiaobukuaipao.youngmam.wrap.BaseHttpFragmentActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-4-25.
 */
public class FreshDetailsActivity extends BaseCommentFragmentActivity implements LabelListView2.OnLabelClickListener,
        YoungShareBoard.OnShareSuccessListener, BaseEmojiFragment.OnEmojiClickedListener, EmojiFragment.OnKeyboardBackspaceClickedListener,
        OnCommentClickListener, OnLikeCommentClickListener, OnCommentLongClickListener {

    private static final int MAX_SIZE = 2048;

    private static final String TAG = FreshDetailsActivity.class.getSimpleName();

    public static final String FIRST_GUIDE_ARTICLE = "first_guide_article";

    private LinearLayout bottomLayout;
    private TextView likeText;
    private TextView commentText;
    private TextView shareText;

    private List<String> mPhotoList = new ArrayList<String>();

    private List<SizeImage> mSizeImageList = new ArrayList<SizeImage>();

    private ImageView mAvatar;
    private TextView mName;
    private TextView mTime;
    private ImageView mGeek;
    private TextView follow;

    private TextView mIntroduce;

    private TextView mLikeNum;

    private ImageButton imageButton;
    private List<UserBase> mAvatars;

    private TextView mCommentNum;

    private ListView mCommentListView;
    private CommentAdapter mCommentAdapter;
    private List<Comment> comments;

    /**
     * 文章Id
     */
    private String articleId;

    /**
     * 喜欢这篇文章的人的翻页id
     */
    private long minLikeId = -1;

    /**
     * 喜欢的人布局
     */
    private LinearLayout mLikeLayout;

    /**
     * 评论的翻页id
     */
    private long minCommentId = -1;

    /**
     * 整个的评论Layout
     */
    private LinearLayout commentLayout;

    /**
     * 整篇文章的详情
     */
    private Article article = null;

    /**
     * 下拉刷新
     */
    private boolean isPullToRefresh = false;

    // 缓存
    private YoungCache youngCache;

    private String userId;

    /**
     * 分享控制器
     */
    final UMSocialService mController = UMServiceFactory.getUMSocialService(GlobalConstants.UMENG_DESCRIPTOR);
    // 分享的内容
    private String shareContent;
    // 分享的内容的链接
    private String targetUrl;
    // 分享的图片--活动或文章或主题
    private String imageUrl;

    // 图片列表
    private LinearLayout photoLayout;

    private RelativeLayout labelLayout;
    private LabelListView2 labelListView2;
    private List<Label> labels;

    private boolean likeRefresh = false;

    /**
     * 弹出PopupWindow
     */
    private PopupWindow popWindow;

    // 横向的RecyclerView
    private RecyclerView mRecyclerView;
    private HorizontalImageAdapter horizontalImageAdapter;
    private WrapContentLinearLayoutManager mLayoutManager;

    private LoadMoreListViewContainer loadMoreListViewContainer;
    // 下拉刷新 -- SwipeRefreshLayout
    private YoungRefreshLayout swipeRefreshLayout;

    private boolean refresh = false;

    private View parentView;

    // 回复的Comment Id
    private String originCommentId = null;

    private LinearLayout replyBottomLayout;

    private View replyLayout;
    private LinearLayout containerLayout;

    // 图片按钮
    // private ImageButton replyPhoto;
    // 表情按钮
    private ImageButton replyEmoji;
    // 输入框
    private EditText replyInput;
    // 发送
    private TextView replySend;

    // 键盘是否可见
    private boolean isKeyBoardVisible = false;
    private boolean isEmojiVisible = false;

    private Handler mShowEmojiHandler = new Handler();

    private EmojiFragment emojiFragment;

    /**
     * Fragment管理
     */
    private FragmentManager fm;
    private FragmentTransaction transaction;

    // EventBus
    private EventBus eventBus;

    private boolean deleteChildComment = false;

    private boolean commentFlag = false;

    public void initViews() {
        setContentView(R.layout.activity_fresh_details);

        eventBus = EventBus.getDefault();
        eventBus.register(this);

        parentView = (View) findViewById(R.id.fresh_detail_layout);
        // 带评论功能
        setHasComment(true);
        // 设置软键盘可以调整大小
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        // 设置ActionBar
        actionBar = (YoungActionBar) findViewById(R.id.action_bar);
        setYoungActionBar();

        getIntentDatas();

        youngCache = YoungCache.get(this);

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

        likeText = (TextView) findViewById(R.id.like);
        commentText = (TextView) findViewById(R.id.comment);
        shareText = (TextView) findViewById(R.id.share);

        // 配置需要分享的相关平台
        configPlatforms(mController);
        // 设置数据
        initDatas();

        // 设置默认的Fragment
        setBasicFragment();

        // 如果开始就评论
        if (commentFlag) {
            commentArticle();
        }
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

    private void addListViewHeader() {
        View view = this.getLayoutInflater().inflate(R.layout.article_detail_header, null);

        photoLayout = (LinearLayout) view.findViewById(R.id.photo_layout);

        labelLayout = (RelativeLayout) view.findViewById(R.id.label_layout);
        labelListView2 = (LabelListView2) view.findViewById(R.id.article_label_list);

        mAvatar = (ImageView) view.findViewById(R.id.avatar);
        mName = (TextView) view.findViewById(R.id.name);
        mTime = (TextView) view.findViewById(R.id.time);
        mGeek = (ImageView) view.findViewById(R.id.geek);
        follow = (TextView) view.findViewById(R.id.follow);

        mIntroduce = (TextView) view.findViewById(R.id.introduce);

        mLikeLayout = (LinearLayout) view.findViewById(R.id.like_layout);
        mLikeLayout.setVisibility(View.INVISIBLE);

        mLikeNum = (TextView) view.findViewById(R.id.like_num);

        imageButton = (ImageButton) view.findViewById(R.id.more);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.like_members);

        mRecyclerView.setHasFixedSize(true);

        mCommentNum = (TextView) view.findViewById(R.id.comment_num);

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

    private void popupUserGuide() {
        // 如果是第一次进入
        final SharedPreferences sp = getSharedPreferences(GlobalConstants.YOUNGMAM_USERGUIDE, MODE_PRIVATE);

        if (sp.getBoolean(FIRST_GUIDE_ARTICLE, true)) {
            /**
             * 定位PopupWindow, 通过设置Gravity, 确定PopupWindow的大致位置
             * 首先获得状态栏的高度, 再获取Action bar的高度, 这两者相加设置y方向的offset样PopupWindow就显示在actionbar的下方
             * 通过dp计算出px，就可以在不同密度屏幕统一X方向的offset.但是要注意不要让背景阴影大于所设置的offset
             * 否则阴影的宽度为offset
             */
            // 获取状态栏高度
            Rect frame = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            // 状态栏高度--frame.top
            // 减去阴影宽度, 适配UI
            int xOffset = frame.top + DisplayUtil.dip2px(this, 54) / 4 + 1;
            // 设置x方向offset为48dp
            int yOffset = DisplayUtil.dip2px(this, 42f);

            View popView = getLayoutInflater().inflate(R.layout.user_guide_collect, null);
            // popView即popupWindow的布局, ture设置focusAble
            popWindow = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

            popWindow.setFocusable(false);
            // 必须设置BackgroundDrawable后setOutsideTouchable(true)才会有效
            // 这里在XML中定义背景, 所以这里设置为null;
            popWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
            // 点击外部关闭
            popWindow.setOutsideTouchable(false);

            popWindow.setAnimationStyle(android.R.style.Animation_Dialog);    // 设置一个动画
            // 设置Gravity, 让它显示在右上角
            popWindow.showAtLocation(parentView, Gravity.RIGHT | Gravity.TOP, yOffset, xOffset);

            ((TextView) popView.findViewById(R.id.tips)).setText(getResources().getString(R.string.str_collect_tips));

            popView.findViewById(R.id.tips).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sp.edit().putBoolean(FIRST_GUIDE_ARTICLE, false).commit();
                    popWindow.dismiss();
                }
            });
        } else {
            sp.edit().putBoolean(FIRST_GUIDE_ARTICLE, false).commit();
        }

    }

    /**
     * 设置ActionBar
     */
    private void setYoungActionBar() {
        actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);
        actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_fresh_detail));

        setBackClickListener(this);
    }

    private void getIntentDatas() {
        articleId = getIntent().getStringExtra("article_id");
        commentFlag = getIntent().getBooleanExtra("comment_flag", false);
    }

    private void initDatas() {
        mLayoutManager = new WrapContentLinearLayoutManager(this, DisplayUtil.getScreenWidth(this) - DisplayUtil.dip2px(this, 66));
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        labels = new ArrayList<Label>();

        mAvatars = new ArrayList<UserBase>();

        horizontalImageAdapter = new HorizontalImageAdapter(this, mAvatars);
        mRecyclerView.setAdapter(horizontalImageAdapter);

        Cursor cursor = getContentResolver().query(YoungContentProvider.COOKIE_CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            long uid = cursor.getLong(cursor.getColumnIndex(CookieTable.COLUMN_UID));
            userId = String.valueOf(uid);
            cursor.close();
        }

        setUIListeners();
    }

    private void setUIListeners() {
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FreshDetailsActivity.this, MembersOfLikeActivity.class);
                if (article != null) {
                    intent.putExtra("like_count", article.getLikeCount());
                    intent.putExtra("article_id", article.getArticleId());
                }
                startActivity(intent);
            }
        });

        likeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(FreshDetailsActivity.this, "doLikeBtnClicked");
                likeArticle();
            }
        });

        /**
         * 评论
         */
        commentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(FreshDetailsActivity.this, "doCommentBtnClicked");
                commentArticle();
            }
        });

        shareText.setOnClickListener(new View.OnClickListener() {
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
                    String name = comment.getUserBase().getName();
                    originCommentId = comment.getCommentId();

                    // if (!comment.getUserBase().getString(GlobalConstants.JSON_USERID).equals(userId)) {
                        // 此时, 回复--只能回复别人
                        prepareComment();
                        // 评论框设置回复人的姓名
                        replyInput.setHint("回复 " + name + " :");
                    //}
                }
            }
        });

        mCommentListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences sp = getSharedPreferences(SplashActivity.YOUNGMAM_UID, MODE_PRIVATE);
                if (sp.getLong(SplashActivity.UID, 0) > 0) {
                    final Comment comment = (Comment) parent.getItemAtPosition(position);

                    if (comment.getUserBase().getUserId().equals(userId)) {
                        // 删除评论
                        MaterialAlertDialog2 dialog = new MaterialAlertDialog2(FreshDetailsActivity.this,
                                getResources().getString(R.string.comment), getResources().getString(R.string.is_delete_comment), null);

                        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mEventLogic.cancelAll();
                                mEventLogic.deleteComment(articleId, comment.getCommentId());
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

        // 点击头像
        mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memberDetails();
            }
        });

        mLikeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FreshDetailsActivity.this, MembersOfLikeActivity.class);
                if (article != null) {
                    intent.putExtra("like_count", article.getLikeCount());
                    intent.putExtra("article_id", article.getArticleId());
                }
                startActivity(intent);
            }
        });

        /*photoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PhotoViewFragment fragment = new PhotoViewFragment(mPhotoList, position);
                fragment.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_NoActionBar_Fullscreen);
                fragment.show(FreshDetailsActivity.this.getSupportFragmentManager(), "viewpager");
            }
        });*/

        replyEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 改变Emoji的布局
                changeEmojiLayout();
            }
        });

        /*replyPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePhotoLayout();
            }
        });*/

        replyInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isEmojiVisible) {
                    containerLayout.setVisibility(View.GONE);
                    isEmojiVisible = false;
                    replyEmoji.setSelected(false);

                    showKeyboard(replyInput);
                    isKeyBoardVisible = true;
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
            // Old
            // mEventLogic.addArticleComment(articleId, replyInput.getText().toString(), originCommentId);
            mEventLogic.addComment(articleId, String.valueOf(TYPE_BUSINESS_ARTICLE), replyInput.getText().toString(), null, originCommentId);
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

    private void memberDetails() {
        Intent intent = new Intent(this, OtherActivity.class);
        intent.putExtra("userId", article.getUserBase().getUserId());
        intent.putExtra("userName", article.getUserBase().getName());
        startActivity(intent);
    }

    private void likeArticle() {
        SharedPreferences sp = getSharedPreferences(SplashActivity.YOUNGMAM_UID, MODE_PRIVATE);
        if (sp.getLong(SplashActivity.UID, 0) > 0) {
            if (article.getHasLiked()) {
                // 此时是喜欢,点击执行取消喜欢操作
                mEventLogic.deleteLike2(article.getArticleId(), String.valueOf(article.getBusinessType()), null);
            } else {
                // 此时是不喜欢,点击执行喜欢操作
                mEventLogic.addLike(article.getArticleId(), String.valueOf(article.getBusinessType()));
            }
        } else {
            // 跳到登录页
            Intent intent = new Intent(FreshDetailsActivity.this, RegisterAndLoginActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 评论文章
     */
    private void commentArticle() {
        SharedPreferences sp = getSharedPreferences(SplashActivity.YOUNGMAM_UID, MODE_PRIVATE);
        if (sp.getLong(SplashActivity.UID, 0) > 0) {
            // 显示评论框
            originCommentId = null;
            // 准备评论
            prepareComment();
        } else {
            // 跳到登录页
            Intent intent = new Intent(FreshDetailsActivity.this, RegisterAndLoginActivity.class);
            startActivity(intent);
        }
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
        isKeyBoardVisible = true;
    }

    /**
     * 分享
     */
    private void share() {
        YoungShareBoard shareBoard = new YoungShareBoard(this);
        shareBoard.setShareParams(article.getArticleId(), article.getBusinessType());
        shareBoard.setOnShareSuccessListener(this);
        shareBoard.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
        setShareContent(mController, targetUrl, shareContent, imageUrl, BaseHttpFragmentActivity.SHARE_TYPE_ARTICLE);

        /**
         * 针对微博
         */
        mController.setShareContent(StringUtil.buildWeiboShareArticle(shareContent, targetUrl));
        mController.setShareMedia(new UMImage(this, imageUrl));
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    public void executeHttpRequest() {
        if (Long.valueOf(articleId) > 0) {
            /**
             * 获取文章详情
             */
            mEventLogic.getArticle(articleId);

            /**
             * 获取喜欢这篇文章的人的列表
             */
            mEventLogic.getArticleLike(articleId, String.valueOf(minLikeId));
        } else {
            // 此时,文章不存在
            bottomLayout.setVisibility(View.GONE);

            MaterialAlertDialog2 dialog = new MaterialAlertDialog2(FreshDetailsActivity.this,
                    getResources().getString(R.string.str_delete), getResources().getString(R.string.this_article_deleted), null);
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

    /**
     * 网络数据返回处理
     * @param msg
     */
    @Override
    public void onResponse(Message msg) {
        switch (msg.what) {
            case R.id.get_article:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    // 首先判断此文章是否存在
                    if (jsonObject.containsKey(GlobalConstants.JSON_STATUS)) {
                        int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);
                        if (status == 22) {
                            // 此时代表文章已经被删除
                            // 弹出Dialog
                            bottomLayout.setVisibility(View.GONE);

                            MaterialAlertDialog2 dialog = new MaterialAlertDialog2(FreshDetailsActivity.this,
                                    getResources().getString(R.string.str_delete), getResources().getString(R.string.this_article_deleted), null);
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

                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                    bottomLayout.setVisibility(View.VISIBLE);

                    article = new Article(jsonObject);

                    Log.d(TAG, "article : " + httpResult.getData());

                    // 喜欢的人数
                    if (article != null) {
                        mLikeNum.setText(this.getString(R.string.str_fresh_like_num, article.getLikeCount()));
                    }

                    if (article.getUserBase().getUserId().equals(userId)) {
                        actionBar.setRightAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_delete));
                        actionBar.getRightFrame().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteArticle();
                            }
                        });
                    } else {
                        /**
                         * 收藏
                         */
                        /*actionBar.setRightAction(YoungActionBar.Type.IMAGE, R.mipmap.collect_pressed, "");
                        actionBar.getRightFrame().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                collectArticle();
                            }
                        });*/
                    }

                    targetUrl = GlobalConstants.SHARE_ARTICLE + article.getArticleId();

                    if (article.getHasLiked()) {
                        likeText.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.like_press), null, null, null);
                    } else {
                        likeText.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.like_normal), null, null, null);
                    }

                    likeText.setText(this.getResources().getString(R.string.str_like, article.getLikeCount()));
                    commentText.setText(this.getResources().getString(R.string.str_comment, article.getCommentCount()));

                    // 此时,获得此文章喜欢的人数和评论的数目
                    EventBus.getDefault().post(new LikeCommentNumberEvent(article.getArticleId(), article.getLikeCount(), article.getCommentCount()));

                    if (article.getImgs() != null && article.getImgs().size() > 0) {

                        imageUrl = article.getImgs().getJSONObject(0).getString(GlobalConstants.JSON_URL);

                        for (int i=0; i < article.getImgs().size(); i++) {
                            if (article.getImgs().getJSONObject(i).containsKey(GlobalConstants.JSON_ORIGINALIMGURL)) {
                                mPhotoList.add(article.getImgs().getJSONObject(i).getString(GlobalConstants.JSON_ORIGINALIMGURL));
                            } else {
                                mPhotoList.add(article.getImgs().getJSONObject(i).getString(GlobalConstants.JSON_URL));
                            }
                        }

                        for (int i=0; i < article.getImgs().size(); i++) {
                            final int position = i;

                            SizeImage sizeImage = new SizeImage(article.getImgs().getJSONObject(i));

                            ImageView imageView = new ImageView(this);
                            photoLayout.addView(imageView);

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT);
                            params.setMargins(0,
                                    0,
                                    0,
                                    this.getResources().getDimensionPixelSize(R.dimen.activity_basic_margin_5dp));
                            imageView.setLayoutParams(params);

                            int width = DisplayUtil.getScreenWidth(this) - 2 * DisplayUtil.dip2px(this, 10);

                            if (width > MAX_SIZE) {
                                width = MAX_SIZE;
                            }

                            if (sizeImage.getImgHeight() > 0 && sizeImage.getImgWidth() > 0) {

                                int height = width * sizeImage.getImgHeight() / sizeImage.getImgWidth();

                                if (height > MAX_SIZE) {
                                    height = MAX_SIZE;
                                }

                                Glide.with(this)
                                        .load(sizeImage.getUrl())
                                        .error(R.mipmap.default_loading)
                                        .override(width, height)
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(imageView);
                            } else {
                                Glide.with(this)
                                        .load(sizeImage.getUrl())
                                        .error(R.mipmap.default_loading)
                                        .override(width, width)
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(imageView);
                            }

                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    PhotoViewFragment fragment = new PhotoViewFragment(mPhotoList, position);
                                    fragment.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_NoActionBar_Fullscreen);
                                    fragment.show(FreshDetailsActivity.this.getSupportFragmentManager(), "viewpager");
                                }
                            });
                        }
                    }

                    if (article.getTags() != null && article.getTags().size() > 0) {
                        labelLayout.setVisibility(View.VISIBLE);

                        for(int i=0; i < article.getTags().size(); i++) {
                            labels.add(new Label(article.getTags().getJSONObject(i)));
                        }

                        labelListView2.setLabels(labels);
                        labelListView2.setOnLabelClickListener(this);
                    } else {
                        labelLayout.setVisibility(View.GONE);
                    }

                    Picasso.with(this)
                            .load(article.getUserBase().getHeadUrl())
                            .fit()
                            .centerCrop()
                            .into(mAvatar);

                    mName.setText(article.getUserBase().getName());
                    mTime.setText(TimeUtil.handleTime(this, article.getCreateTime()));

                    if (article.getUserBase().getExpertType() != 0) {
                        mGeek.setVisibility(View.VISIBLE);
                    } else {
                        mGeek.setVisibility(View.GONE);
                    }

                    SharedPreferences sp = getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
                    if (sp.getLong(SplashActivity.UID, 0) > 0) {

                        if (!article.getUserBase().getUserId().equals(String.valueOf(sp.getLong(SplashActivity.UID, 0)))) {
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
                        } else {
                            follow.setVisibility(View.GONE);
                        }
                    } else {
                        follow.setVisibility(View.GONE);
                    }

                    shareContent = StringUtil.isEmpty(article.getContent()) ? "" : article.getContent();

                    if (!StringUtil.isEmpty(article.getContent())) {
                        mIntroduce.setVisibility(View.VISIBLE);
                        mIntroduce.setText(article.getContent());
                    } else {
                        mIntroduce.setVisibility(View.GONE);
                    }

                    if (article.getArticleId() != null) {
                        // 刷新Id
                        articleId = article.getArticleId();
                        /**
                         * 获取文章的评论列表
                         */
                        mEventLogic.getComment(article.getArticleId(), String.valueOf(article.getBusinessType()), String.valueOf(minCommentId), -1);
                    }
                }

                break;

            case R.id.get_article_like:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;

                    if (httpResult.getData() != null) {
                        JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                        if (jsonObject != null) {

                            JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_DATA);

                            if (jsonArray != null && jsonArray.size() > 0) {
                                mLikeLayout.setVisibility(View.VISIBLE);

                                if (likeRefresh) {
                                    mAvatars.clear();
                                    likeRefresh = false;
                                }

                                for (int i = 0; i < jsonArray.size(); i++) {
                                    mAvatars.add(new Article(jsonArray.getJSONObject(i)).getUserBase());
                                }

                                horizontalImageAdapter.notifyDataSetChanged();
                            } else {
                                // 整个隐藏
                                mLikeLayout.setVisibility(View.GONE);
                            }

                        } else {
                            mLikeLayout.setVisibility(View.GONE);
                        }
                    } else {
                        // 此时没有返回数据
                        mLikeLayout.setVisibility(View.GONE);
                    }
                } else {
                    // 此时没有返回数据
                    mLikeLayout.setVisibility(View.GONE);
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

                            mCommentNum.setText(this.getString(R.string.str_fresh_comment_num, article.getCommentCount()));

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

            case R.id.delete_like2:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    String articleId = msg.getData().getString("key");

                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());
                    int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);

                    if (status == 0) {
                        if (articleId != null) {
                            if (articleId.equals(article.getArticleId())) {
                                article.setHasLiked(false);
                                likeText.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.like_normal), null, null, null);

                                int likeCount = article.getLikeCount();
                                if (likeCount > 0) {
                                    likeCount--;
                                }
                                article.setLikeCount(likeCount);
                                likeText.setText(this.getResources().getString(R.string.str_like, likeCount));
                                mLikeNum.setText(this.getString(R.string.str_fresh_like_num, article.getLikeCount()));

                                // 进程间传递消息
                                EventBus.getDefault().post(new ArticleLikeEvent(article.getArticleId(), ArticleLikeEvent.TYPE_DELETE_LIKE));

                                minLikeId = -1;
                                likeRefresh = true;
                                mEventLogic.getArticleLike(article.getArticleId(), String.valueOf(minLikeId));
                            }
                        }
                    }
                }
                break;

            case R.id.add_like:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    String articleId = msg.getData().getString("key");

                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());
                    int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);

                    if (status == 0) {
                        if (articleId != null) {
                            if (articleId.equals(article.getArticleId())) {
                                article.setHasLiked(true);
                                likeText.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.like_press), null, null, null);

                                int likeCount = article.getLikeCount();
                                likeCount++;
                                article.setLikeCount(likeCount);
                                likeText.setText(this.getResources().getString(R.string.str_like, likeCount));
                                mLikeNum.setText(this.getString(R.string.str_fresh_like_num, article.getLikeCount()));

                                EventBus.getDefault().post(new ArticleLikeEvent(article.getArticleId(), ArticleLikeEvent.TYPE_ADD_LIKE));

                                minLikeId = -1;
                                likeRefresh = true;
                                mEventLogic.getArticleLike(article.getArticleId(), String.valueOf(minLikeId));
                            }
                        }

                        // 1
                        if (jsonObject.containsKey(GlobalConstants.JSON_BONUSPOINT)) {
                            showCreditDialog(getResources().getString(R.string.str_dialog_credit,
                                    jsonObject.getInteger(GlobalConstants.JSON_BONUSPOINT)));
                        }

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
                        isKeyBoardVisible = false;

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
                                mCommentNum.setText(this.getString(R.string.str_fresh_comment_num, article.getCommentCount()));
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

            case R.id.delete_article:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    String articleId = msg.getData().getString("key");

                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());
                    int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);

                    // 此时,代表删除文章成功
                    if (status == 0) {
                        if (articleId != null) {
                            youngCache.remove(YoungCache.CACHE_MINE_PUBLISH);

                            // 刷新我的发布页
                            EventBus.getDefault().post(new MinePublishDelayRefreshEvent(true));
                            EventBus.getDefault().post(new ArticleDeleteEvent(articleId));

                            finish();
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

                        if (jsonObject.getInteger(GlobalConstants.JSON_STATUS) == 0) {

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

            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMSsoHandler ssoHandler = SocializeConfig.getSocializeConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    @Override
    public void onLabelClick(TextView labelView, Label label) {
        Theme theme = new Theme(null, null, new Label(label.getId(), label.getTitle()));
        Intent intent = new Intent(this, SearchDetailActivity.class);
        intent.putExtra("tag_id", label.getId());
        intent.putExtra("share_businessType", SearchDetailActivity.TYPE_BUSINESS_ARTICLE);
        startActivity(intent);
    }

    @Override
    public void onShareSuccess(String articleId, int shareType) {
        mEventLogic.shareBonus(articleId, shareType);
    }

    /**
     * 删除这篇文章
     */
    private void deleteArticle() {
        MaterialAlertDialog2 dialog = new MaterialAlertDialog2(this,
                getResources().getString(R.string.mine_publish), getResources().getString(R.string.is_delete_article), null);
        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEventLogic.cancelAll();
                mEventLogic.deleteArticle(article.getArticleId());
            }
        });
        dialog.setOnCancelButtonClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

        dialog.show();
    }

    /**
     * 收藏这篇文章
     */
    private void collectArticle() {
        if (popWindow != null) {
            SharedPreferences sp = getSharedPreferences(GlobalConstants.YOUNGMAM_USERGUIDE, MODE_PRIVATE);
            sp.edit().putBoolean(FIRST_GUIDE_ARTICLE, false).commit();
            popWindow.dismiss();
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
                isKeyBoardVisible = true;
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
        isKeyBoardVisible = false;

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
                MaterialAlertDialog2 dialog = new MaterialAlertDialog2(FreshDetailsActivity.this,
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
            Intent intent = new Intent(FreshDetailsActivity.this, RegisterAndLoginActivity.class);
            startActivity(intent);
        }
    }

    public void onEvent(ArticleCommentEvent articleCommentEvent) {
        if (articleCommentEvent.getType() == CommentEvent.TYPE_DELETE_COMMENT) {
            article.setCommentCount(article.getCommentCount() - articleCommentEvent.getCount());
            commentText.setText(this.getResources().getString(R.string.str_comment, article.getCommentCount()));
        } else if (articleCommentEvent.getType() == CommentEvent.TYPE_ADD_COMMENT) {
            article.setCommentCount(article.getCommentCount() + articleCommentEvent.getCount());
            commentText.setText(this.getResources().getString(R.string.str_comment, article.getCommentCount()));
        }
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

}