package com.xiaobukuaipao.youngmam;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.greenrobot.event.EventBus;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMSsoHandler;
import com.xiaobukuaipao.youngmam.adapter.PublishPhotoAdapter;
import com.xiaobukuaipao.youngmam.cache.YoungCache;
import com.xiaobukuaipao.youngmam.database.CookieTable;
import com.xiaobukuaipao.youngmam.database.LabelTable;
import com.xiaobukuaipao.youngmam.domain.ActivityParticipateEvent;
import com.xiaobukuaipao.youngmam.domain.ImageModel;
import com.xiaobukuaipao.youngmam.domain.Label;
import com.xiaobukuaipao.youngmam.domain.MinePublishDelayRefreshEvent;
import com.xiaobukuaipao.youngmam.domain.MineQuestionDelayRefreshEvent;
import com.xiaobukuaipao.youngmam.fragment.PhotoViewFragment;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.imagechooser.Bimp;
import com.xiaobukuaipao.youngmam.imagechooser.ImageChooserActivity;
import com.xiaobukuaipao.youngmam.manager.YoungDatabaseManager;
import com.xiaobukuaipao.youngmam.provider.YoungContentProvider;
import com.xiaobukuaipao.youngmam.utils.FileUtil;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.StringUtil;
import com.xiaobukuaipao.youngmam.widget.MaterialAlertDialog2;
import com.xiaobukuaipao.youngmam.widget.YoungActionBar;
import com.xiaobukuaipao.youngmam.wrap.BaseHttpFragmentActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-5-5.
 */
public class PublishActivity extends BaseHttpFragmentActivity implements PublishPhotoAdapter.OnPublishImageDeleteListener {
    private static final String TAG = PublishActivity.class.getSimpleName();

    public static final int TYPE_PUBLISH_ARTICLE = 6;
    public static final int TYPE_PUBLISH_QUESTION = 8;

    public static final int REQUEST_LABEL = 100;

    // private NestedGridView mGridView;
    private GridView mGridView;
    private PublishPhotoAdapter mPublishPhotoAdapter;
    private ArrayList<ImageModel> mSelectedImage;

    private ArrayList<ImageModel> mNextSelected;

    private View mLabelView;

    // 临时文件的目录
    private ArrayList<String> tempPaths = new ArrayList<String>();

    private ArrayList<String> imgIds = new ArrayList<String>();

    // 上传图片传回来的
    private ArrayList<String> imgUrls = new ArrayList<String>();

    // 统计上传图片数量
    private int uploadPhotoCount = 0;

    // 标签
    private TextView tags;

    // 内容
    private EditText mContent;

    private String imgStr;
    private String labelStr;

    // 缓存
    private YoungCache youngCache;

    /**
     * 从上层带过来的Label,如果是从上层带过来的Label,则此时的Label不能选择
     */
    private Label mTag;

    // 同步到QQ空间
    private LinearLayout shareQzoneLayout;
    // 同步到新浪微博
    private LinearLayout shareWeiboLayout;

    private CheckBox shareCheck;
    private boolean isShare = false;

    private RelativeLayout shareLayout;

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

    private String loginType;

    private ArrayList<Label> selectedLabels;

    // 特有的Handler
    private Handler delayHandler = new Handler();

    /**
     * 发布的类型--默认是文章
     */
    private int typePublish = TYPE_PUBLISH_ARTICLE;

    /**
     * 初始化Views
     */
    public void initViews() {
        setContentView(R.layout.activity_publish);
        // 设置ActionBar
        actionBar = (YoungActionBar) findViewById(R.id.action_bar);
        setYoungActionBar();

        getIntentDatas();

        youngCache = YoungCache.get(this);

        mGridView = (GridView) findViewById(R.id.publish_image_grid_view);

        mPublishPhotoAdapter = new PublishPhotoAdapter(this, mSelectedImage, R.layout.item_delete_image);
        mPublishPhotoAdapter.setOnPublishImageDeleteListener(this);
        mGridView.setAdapter(mPublishPhotoAdapter);

        mLabelView = (View) findViewById(R.id.select_label_layout);

        mContent = (EditText) findViewById(R.id.publish_edit);
        // 隐藏输入法
        // ViewUtil.hideSoftInput(this, mContent);

        tags = (TextView) findViewById(R.id.tags);

        if (mTag != null) {
            if (Integer.valueOf(mTag.getId()) > 0) {
                labelStr = mTag.getId();
            } else {
                labelStr = mTag.getTitle();
            }
            tags.setText(StringUtil.buildTag(mTag.getTitle()));
        }

        shareLayout = (RelativeLayout) findViewById(R.id.share_layout);

        // 同步到QQ空间
        shareQzoneLayout = (LinearLayout) findViewById(R.id.share_qzone);
        // 同步到新浪微博
        shareWeiboLayout = (LinearLayout) findViewById(R.id.share_weibo);

        shareCheck = (CheckBox) findViewById(R.id.async_check);

        // 读取此用户的login type
        loginType = YoungDatabaseManager.getInstance().getLoginType();

        Log.d(TAG, "login type :" + loginType);
        if (!StringUtil.isEmpty(loginType)) {
            if (loginType.equals(CookieTable.LOGIN_TYPE_QQ)) {
                shareCheck.setChecked(true);
                isShare = true;
                shareQzoneLayout.setVisibility(View.VISIBLE);
                shareWeiboLayout.setVisibility(View.GONE);
            } else {
                if (loginType.equals(CookieTable.LOGIN_TYPE_WEIBO)) {
                    isShare = true;
                    shareCheck.setChecked(true);
                } else {
                    isShare = false;
                    shareCheck.setChecked(false);
                }
                shareWeiboLayout.setVisibility(View.VISIBLE);
                shareQzoneLayout.setVisibility(View.GONE);
            }
        } else {
            isShare = false;
            shareCheck.setChecked(false);
            shareWeiboLayout.setVisibility(View.VISIBLE);
            shareQzoneLayout.setVisibility(View.GONE);
        }

        shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shareCheck.isChecked()) {
                    isShare = false;
                    shareCheck.setChecked(false);
                    MobclickAgent.onEvent(PublishActivity.this, "publishShareCancelled");
                } else {
                    isShare = true;
                    shareCheck.setChecked(true);
                    MobclickAgent.onEvent(PublishActivity.this, "publishTopicShareBtnClicked");
                }
            }
        });

        shareCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isShare = true;
                    shareCheck.setChecked(true);
                    MobclickAgent.onEvent(PublishActivity.this, "publishTopicShareBtnClicked");
                } else {
                    isShare = false;
                    shareCheck.setChecked(false);
                    MobclickAgent.onEvent(PublishActivity.this, "publishShareCancelled");
                }
            }
        });

        // 创建临时图片目录
        try {
            FileUtil.createFolder(FileUtil.SD_PATH_PHOTO_TEMP);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 配置需要分享的相关平台
        configPlatforms(mController);

        setUIListeners();
    }

    /**
     * 设置ActionBar
     */
    private void setYoungActionBar() {
        actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);
        actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_publish_topic));
        actionBar.setRightAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_complete));

        actionBar.getLeftFrame().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        actionBar.getRightFrame().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPhotos();
            }
        });
    }

    private void getIntentDatas() {
        typePublish = getIntent().getIntExtra("type_publish", TYPE_PUBLISH_ARTICLE);

        if (typePublish == TYPE_PUBLISH_ARTICLE) {
            mSelectedImage = getIntent().getParcelableArrayListExtra("selected_image");
            mTag = getIntent().getParcelableExtra("label");
        } else {
            if (typePublish == TYPE_PUBLISH_QUESTION) {
                // 此时, 没有选择图片, 所以需要设置mSelectedImage为空的
                mSelectedImage = new ArrayList<ImageModel>();
                mTag = getIntent().getParcelableExtra("label");
            }
        }

        ImageModel imageModel = new ImageModel();
        imageModel.isFunction = true;
        mSelectedImage.add(imageModel);
    }

    private void setUIListeners() {

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mSelectedImage.get(position).isFunction) {
                    // 此时进入照片选择页
                    // Bimp.bitmaps.clear();
                    ArrayList<ImageModel> selectImage = new ArrayList<ImageModel>();
                    for (int i = 0; i < mSelectedImage.size() - 1; i++) {
                        selectImage.add(mSelectedImage.get(i));
                    }

                    Intent intent = new Intent(PublishActivity.this, ImageChooserActivity.class);
                    intent.putParcelableArrayListExtra("publish_selected_image", selectImage);
                    intent.putExtra("type_publish", typePublish);
                    startActivity(intent);
                    return;
                } else {
                    List<String> mPhotoList = new ArrayList<String>();
                    for (int i = 0; i < mSelectedImage.size() - 1; i++) {
                        mPhotoList.add(mSelectedImage.get(i).path);
                    }

                    PhotoViewFragment fragment = new PhotoViewFragment(mPhotoList, position);
                    fragment.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_NoActionBar_Fullscreen);
                    fragment.show(PublishActivity.this.getSupportFragmentManager(), "viewpager");
                }
            }
        });

        mLabelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTag != null) {
                    if (selectedLabels != null) {
                        selectedLabels.add(mTag);
                    } else {
                        selectedLabels = new ArrayList<Label>();
                        selectedLabels.add(mTag);
                    }

                    // 将此Label加入到数据库中
                    insertToDatabase(Long.valueOf(mTag.getId()), mTag.getTitle());
                }

                Intent intent = new Intent(PublishActivity.this, LabelActivity.class);
                if (selectedLabels != null && selectedLabels.size() > 0) {
                    intent.putParcelableArrayListExtra("label_list", selectedLabels);
                }
                intent.putExtra("type", typePublish);
                startActivityForResult(intent, REQUEST_LABEL);
            }
        });

    }

    public synchronized void insertToDatabase(Long tagId, String name) {

        Cursor cursor = getContentResolver().query(YoungContentProvider.LABEL_CONTENT_URI, null, null, null, null);
        // 遍历
        if (cursor != null && cursor.moveToFirst()) {
            do {
                if (tagId == cursor.getInt(cursor.getColumnIndex(LabelTable.COLUMN_TAG_ID))) {

                    getContentResolver().delete(YoungContentProvider.LABEL_CONTENT_URI,
                            LabelTable.COLUMN_TAG_ID + "=?", new String[] { String.valueOf(tagId)});
                }

            } while (cursor.moveToNext());

            cursor.close();
        }

        ContentValues values = new ContentValues();
        values.put(LabelTable.COLUMN_TAG_ID, tagId);
        values.put(LabelTable.COLUMN_NAME, name);
        // 插入数据库
        getContentResolver().insert(YoungContentProvider.LABEL_CONTENT_URI, values);
    }

    ///////////////////////////////////////////////////////
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // must store the new intent unless getIntent() will return the old one
        setIntent(intent);
        processExtraData();
    }

    /**
     * 处理额外数据
     */
    private void processExtraData() {
        if (mNextSelected != null) {
            mNextSelected.clear();
        }

        mNextSelected = getIntent().getParcelableArrayListExtra("selected_image");

        ImageModel imageModel = new ImageModel();
        imageModel.path = null;
        imageModel.isFunction = true;

        mSelectedImage.clear();

        for(int i=0; i < mNextSelected.size(); i++) {
            mSelectedImage.add(mNextSelected.get(i));
        }

        mSelectedImage.add(imageModel);
        mPublishPhotoAdapter.notifyDataSetChanged();
    }

    /**
     * 上传图片
     */
    private void uploadPhotos() {
        if (typePublish == TYPE_PUBLISH_ARTICLE) {
            if (isShare && StringUtil.isEmpty(mContent.getText().toString()) && shareQzoneLayout.getVisibility() == View.VISIBLE) {
                Toast.makeText(this, "分享到QQ空间需要写点文字哦~", Toast.LENGTH_SHORT).show();
            } else {
                MobclickAgent.onEvent(PublishActivity.this, "doPublishClick");

                if (!StringUtil.isEmpty(labelStr) && (mSelectedImage != null && mSelectedImage.size() > 1)) {
                    // 具体的执行
                    concreteExecute();
                } else {
                    if (StringUtil.isEmpty(labelStr)) {
                        Toast.makeText(this, getResources().getString(R.string.str_select_label), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, getResources().getString(R.string.str_at_least_select_image), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else if (typePublish == TYPE_PUBLISH_QUESTION) {

            if (!StringUtil.isEmpty(labelStr) && !StringUtil.isEmpty(mContent.getText().toString())) {
                // 具体的执行
                if (mSelectedImage != null && mSelectedImage.size() > 1) {
                    // 如果存在照片, 才执行这里
                    concreteExecute();
                } else {
                    mEventLogic.createQuestion(mContent.getText().toString(), imgStr, labelStr);
                }
            } else {
                if (StringUtil.isEmpty(labelStr)) {
                    Toast.makeText(this, getResources().getString(R.string.str_select_label), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.str_please_have_question), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * 具体的执行函数
     */
    private void concreteExecute() {
        showProgress(getString(R.string.publishing));

        isMultiRequest = true;
        // 首先将所有的图片进行压缩处理
        uploadPhotoCount = 0;

        // 隐藏软键盘
        hideSoftInput();

        new AsyncTask<String, Void, Boolean>() {

            protected Boolean doInBackground(String... params) {
                try {
                    FileUtil.deleteFolderFiles(FileUtil.SD_PATH_PHOTO_TEMP);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < mSelectedImage.size() - 1; i++) {
                    String path = FileUtil.SD_PATH_PHOTO_TEMP + i + ".jpg";
                    try {
                        FileUtil.saveBitmap(revitionImageSize(mSelectedImage.get(i).path), path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    tempPaths.add(path);
                }

                return true;
            }

            protected void onPostExecute(Boolean result) {
                // desc目前设为空
                if (typePublish == TYPE_PUBLISH_ARTICLE) {
                    mEventLogic.uploadImage(tempPaths.get(uploadPhotoCount), 1, "");
                } else if (typePublish == TYPE_PUBLISH_QUESTION) {
                    mEventLogic.uploadImage(tempPaths.get(uploadPhotoCount), 0, "");
                }
            }
        }.execute();

    }

    /**
     * 隐藏软键盘
     */
    private void hideSoftInput() {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 网络数据返回处理
     * @param msg
     */
    @Override
    public void onResponse(Message msg) {
        switch (msg.what) {
            case R.id.upload_image:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;

                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    imgIds.add(jsonObject.getString(GlobalConstants.JSON_IMGID));
                    imgUrls.add(jsonObject.getString(GlobalConstants.JSON_IMGURL));

                    uploadPhotoCount++;

                    if (uploadPhotoCount < mSelectedImage.size() - 1) {
                        // 继续上传图片
                        if (typePublish == TYPE_PUBLISH_ARTICLE) {
                            mEventLogic.uploadImage(tempPaths.get(uploadPhotoCount), 1, "");
                        } else {
                            mEventLogic.uploadImage(tempPaths.get(uploadPhotoCount), 0, "");
                        }
                    } else {
                        // 结束
                        uploadPhotoCount = 0;

                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i < imgIds.size(); i++) {
                            sb.append(imgIds.get(i));
                            sb.append(",");
                        }

                        imgStr = sb.substring(0, sb.length() - 1);

                        if (!StringUtil.isEmpty(imgStr) && !StringUtil.isEmpty(labelStr)) {

                            if (typePublish == TYPE_PUBLISH_ARTICLE) {
                                mEventLogic.saveArticle(mContent.getText().toString(), imgStr, labelStr);
                            } else if (typePublish == TYPE_PUBLISH_QUESTION) {
                                mEventLogic.createQuestion(mContent.getText().toString(), imgStr, labelStr);
                            }
                        } else {
                            if (StringUtil.isEmpty(labelStr)) {
                                Toast.makeText(this, getResources().getString(R.string.str_select_label), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                break;

            case R.id.create_question:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;

                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());
                    int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);
                    String message = jsonObject.getString(GlobalConstants.JSON_MSG);

                    String articleId = jsonObject.getString(GlobalConstants.JSON_ARTICLEID);

                    if (status == 0) {

                        // 清空发的照片
                        try {
                            FileUtil.deleteFolder(FileUtil.SD_PATH_PHOTO_TEMP);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        // 此时, 发布问题成功
                        // 刷新我的发布页
                        EventBus.getDefault().post(new MineQuestionDelayRefreshEvent(true));

                        hideProgress();

                        if (jsonObject.containsKey(GlobalConstants.JSON_BONUSPOINT)) {
                            showCreditDialog(getResources().getString(R.string.str_dialog_credit, jsonObject.getInteger(GlobalConstants.JSON_BONUSPOINT)));
                        }

                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                        // 此时,同步到微博或者QQ空间
                        if (isShare) {
                            /**
                             * 针对微博
                             */
                            if (!loginType.equals(CookieTable.LOGIN_TYPE_QQ)) {

                                StringBuilder sb = new StringBuilder();
                                sb.append("在");
                                sb.append("#");
                                sb.append("花样妈妈");
                                sb.append("#");
                                sb.append("上发布了一篇有趣的帖子, 快来看看吧");
                                sb.append("~~");
                                sb.append((mContent.getText().toString()).length() > 20 ?
                                        (mContent.getText().toString()).substring(0, 20) : mContent.getText().toString());
                                sb.append(">>");
                                sb.append(GlobalConstants.SHARE_ARTICLE + articleId);
                                sb.append(" (");
                                sb.append("@");
                                sb.append("花样妈妈youngmam");
                                sb.append(")");

                                mController.setShareContent(sb.toString());
                                mController.setShareMedia(new UMImage(this, imgUrls.get(0)));

                                Bimp.bitmaps.clear();

                                mController.postShare(this, SHARE_MEDIA.SINA, new SocializeListeners.SnsPostListener() {

                                    @Override
                                    public void onStart() {
                                        Log.d(TAG, "开始分享");
                                        showProgress(getResources().getString(R.string.str_sharing));
                                    }

                                    @Override
                                    public void onComplete(SHARE_MEDIA platform, int code, SocializeEntity socializeEntity) {
                                        String showText = platform.toString();

                                        hideProgress();
                                        finish();
                                        if (code == StatusCode.ST_CODE_SUCCESSED) {
                                            showText += "分享成功";
                                            MobclickAgent.onEvent(PublishActivity.this, "publishShareSuccessed");
                                        } else {
                                            showText += "分享失败";
                                        }

                                    }
                                });


                            } else {
                                // 分享到QQ空间
                                QZoneShareContent qZoneShareContent = new QZoneShareContent();
                                qZoneShareContent.setShareContent((mContent.getText().toString()).length() > 20 ?
                                        (mContent.getText().toString()).substring(0, 20) : mContent.getText().toString());
                                qZoneShareContent.setTitle(getResources().getString(R.string.str_share_title_publish_article));


                                qZoneShareContent.setTargetUrl(GlobalConstants.SHARE_ARTICLE + articleId);
                                UMImage urlImage = new UMImage(this, imgUrls.get(0));
                                qZoneShareContent.setShareMedia(urlImage);
                                mController.setShareMedia(qZoneShareContent);

                                mController.postShare(this, SHARE_MEDIA.QZONE, new SocializeListeners.SnsPostListener() {

                                    @Override
                                    public void onStart() {
                                        Log.d(TAG, "开始分享");
                                        showProgress(getResources().getString(R.string.str_sharing));
                                    }

                                    @Override
                                    public void onComplete(SHARE_MEDIA platform, int code, SocializeEntity socializeEntity) {
                                        String showText = platform.toString();

                                        hideProgress();
                                        finish();

                                        if (code == StatusCode.ST_CODE_SUCCESSED) {
                                            showText += "分享成功";
                                            MobclickAgent.onEvent(PublishActivity.this, "publishShareSuccessed");
                                        } else {
                                            showText += "分享失败";
                                        }
                                    }
                                });

                            }
                        } else {
                            Bimp.bitmaps.clear();

                            if (jsonObject.containsKey(GlobalConstants.JSON_BONUSPOINT)) {
                                delayHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 1100);
                            } else {
                                finish();
                            }
                        }
                    } else {
                        // 此时, 是返回不成功的情况, 保存失败
                        hideProgress();
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.save_article:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;

                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());
                    int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);
                    String message = jsonObject.getString(GlobalConstants.JSON_MSG);

                    String articleId = jsonObject.getString(GlobalConstants.JSON_ARTICLEID);

                    if (status == 0) {

                        // 清空发的照片
                        try {
                            FileUtil.deleteFolder(FileUtil.SD_PATH_PHOTO_TEMP);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        // 此时,发布文章成功
                        youngCache.remove(YoungCache.CACHE_MINE_PUBLISH);
                        // 刷新我的发布页
                        EventBus.getDefault().post(new MinePublishDelayRefreshEvent(true));

                        hideProgress();

                        if (jsonObject.containsKey(GlobalConstants.JSON_BONUSPOINT)) {
                            showCreditDialog(getResources().getString(R.string.str_dialog_credit, jsonObject.getInteger(GlobalConstants.JSON_BONUSPOINT)));
                        }

                        // 发送广播通知
                        if (typePublish == TYPE_PUBLISH_ARTICLE) {
                            // 说明此时是从参加活动页面或者话题页过来的,所以需要刷新页面
                            EventBus.getDefault().post(new ActivityParticipateEvent(true));
                        }

                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                        // 此时,同步到微博或者QQ空间
                        if (isShare) {
                            /**
                             * 针对微博
                             */
                            if (!loginType.equals(CookieTable.LOGIN_TYPE_QQ)) {

                                StringBuilder sb = new StringBuilder();
                                sb.append("在");
                                sb.append("#");
                                sb.append("花样妈妈");
                                sb.append("#");
                                sb.append("上发布了一篇有趣的帖子,快来看看吧");
                                sb.append("~~");
                                sb.append((mContent.getText().toString()).length() > 20 ?
                                        (mContent.getText().toString()).substring(0, 20) : mContent.getText().toString());
                                sb.append(">>");
                                sb.append(GlobalConstants.SHARE_ARTICLE + articleId);
                                sb.append(" (");
                                sb.append("@");
                                sb.append("花样妈妈youngmam");
                                sb.append(")");

                                mController.setShareContent(sb.toString());
                                mController.setShareMedia(new UMImage(this, imgUrls.get(0)));

                                Bimp.bitmaps.clear();

                                mController.postShare(this, SHARE_MEDIA.SINA, new SocializeListeners.SnsPostListener() {

                                    @Override
                                    public void onStart() {
                                        Log.d(TAG, "开始分享");
                                        showProgress(getResources().getString(R.string.str_sharing));
                                    }

                                    @Override
                                    public void onComplete(SHARE_MEDIA platform, int code, SocializeEntity socializeEntity) {
                                        String showText = platform.toString();

                                        hideProgress();
                                        finish();
                                        if (code == StatusCode.ST_CODE_SUCCESSED) {
                                            showText += "分享成功";
                                            MobclickAgent.onEvent(PublishActivity.this, "publishShareSuccessed");
                                        } else {
                                            showText += "分享失败";
                                        }

                                    }
                                });


                            } else {
                                // 分享到QQ空间
                                QZoneShareContent qZoneShareContent = new QZoneShareContent();
                                qZoneShareContent.setShareContent((mContent.getText().toString()).length() > 20 ?
                                        (mContent.getText().toString()).substring(0, 20) : mContent.getText().toString());
                                qZoneShareContent.setTitle(getResources().getString(R.string.str_share_title_publish_article));


                                qZoneShareContent.setTargetUrl(GlobalConstants.SHARE_ARTICLE + articleId);
                                UMImage urlImage = new UMImage(this, imgUrls.get(0));
                                qZoneShareContent.setShareMedia(urlImage);
                                mController.setShareMedia(qZoneShareContent);

                                mController.postShare(this, SHARE_MEDIA.QZONE, new SocializeListeners.SnsPostListener() {

                                    @Override
                                    public void onStart() {
                                        Log.d(TAG, "开始分享");
                                        showProgress(getResources().getString(R.string.str_sharing));
                                    }

                                    @Override
                                    public void onComplete(SHARE_MEDIA platform, int code, SocializeEntity socializeEntity) {
                                        String showText = platform.toString();

                                        hideProgress();
                                        finish();

                                        if (code == StatusCode.ST_CODE_SUCCESSED) {
                                            showText += "分享成功";
                                            MobclickAgent.onEvent(PublishActivity.this, "publishShareSuccessed");
                                        } else {
                                            showText += "分享失败";
                                        }
                                    }
                                });

                            }
                        } else {
                            Bimp.bitmaps.clear();

                            if (jsonObject.containsKey(GlobalConstants.JSON_BONUSPOINT)) {
                                delayHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 1100);
                            } else {
                                finish();
                            }
                        }

                    } else {
                        // 此时, 是返回不成功的情况, 保存失败
                        hideProgress();
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 压缩图片
     * @param path
     */
    private Bitmap revitionImageSize(String path) throws IOException {
        if (path == null) {
            Log.d(TAG, "path is null");
        }
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(path)));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(in, null, options);
        in.close();

        int i = 0;
        Bitmap bitmap = null;

        /*while (true) {
            if ((options.outWidth >> i <= 4000) && (options.outHeight >> i <= 4000)) {
                in = new BufferedInputStream(new FileInputStream(new File(path)));
                options.inSampleSize = (int) Math.pow(2.0D, i);
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeStream(in, null, options);
                break;
            }
            i += 1;
        }*/
        in = new BufferedInputStream(new FileInputStream(new File(path)));
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeStream(in, null, options);

        // 变化角度
        // 在这里获得图片的旋转角度
        int angle = readPictureDegree(path);

        return rotateImageView(angle, bitmap);
    }

    public int getBitmapSize(Bitmap bitmap){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){    // API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1){ //API 12
            return bitmap.getByteCount();
        }
        return bitmap.getRowBytes() * bitmap.getHeight();                //earlier version
    }

    /**
     * 得到图片的旋转角度
     */
    public int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);

            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return degree;
    }

    /**
     * 旋转图片
     */
    private Bitmap rotateImageView(int angle, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap rotatedBitmap = null;

        if (bitmap != null) {
            rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }

        return rotatedBitmap;
    }

    ///////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PublishActivity.REQUEST_LABEL && resultCode == RESULT_OK) {
            // 标签页请求
            if (selectedLabels != null && selectedLabels.size() > 0) {
                selectedLabels.clear();
            }
            selectedLabels = data.getParcelableArrayListExtra("label_list");

            Log.d(TAG, "selectedLabels size : " + selectedLabels.size());

            StringBuilder sb = new StringBuilder();
            StringBuilder sbIds = new StringBuilder();
            for(int i=0; i < selectedLabels.size(); i++) {
                sb.append("#");
                sb.append(selectedLabels.get(i).getTitle());
                if (selectedLabels.get(i).getId().equals("-1")) {
                    sbIds.append(selectedLabels.get(i).getTitle());
                    sbIds.append(",");
                } else {
                    sbIds.append(selectedLabels.get(i).getId());
                    sbIds.append(",");
                }
            }

            tags.setText(sb.toString());

            if (!StringUtil.isEmpty(sbIds.toString())) {
                labelStr = sbIds.substring(0, sbIds.length() - 1);
            } else {
                labelStr = null;
            }
            Log.d(TAG, "labelStr : " + labelStr);
        }

        UMSsoHandler ssoHandler = SocializeConfig.getSocializeConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    // 重写onBackPress方法
    @Override
    public void	onBackPressed() {
        MaterialAlertDialog2 dialog = new MaterialAlertDialog2(PublishActivity.this,
                getResources().getString(R.string.publish_article), getResources().getString(R.string.str_abandon_this_publish),
                getResources().getString(R.string.str_abandon));
        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bimp.bitmaps.clear();

                try {
                    FileUtil.deleteFolderFiles(FileUtil.SD_PATH_PHOTO_TEMP);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                finish();
            }
        });
        dialog.setOnCancelButtonClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });
        dialog.show();
    }

    @Override
    public void onPublishImageDelete(int position) {
        mSelectedImage.remove(position);
        mPublishPhotoAdapter.notifyDataSetChanged();
    }
}
