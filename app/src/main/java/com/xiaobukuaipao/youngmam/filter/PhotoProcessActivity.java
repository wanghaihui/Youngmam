package com.xiaobukuaipao.youngmam.filter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.youngmam.AppActivityManager;
import com.xiaobukuaipao.youngmam.PublishActivity;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.domain.ImageModel;
import com.xiaobukuaipao.youngmam.domain.Label;
import com.xiaobukuaipao.youngmam.domain.Sticker;
import com.xiaobukuaipao.youngmam.domain.StickerGroup;
import com.xiaobukuaipao.youngmam.filter.util.EffectUtil;
import com.xiaobukuaipao.youngmam.filter.util.FileUtils;
import com.xiaobukuaipao.youngmam.filter.util.GPUImageFilterTools;
import com.xiaobukuaipao.youngmam.filter.util.ImageUtils;
import com.xiaobukuaipao.youngmam.filter.view.StickerImageViewDrawableOverlay;
import com.xiaobukuaipao.youngmam.gpuimage.GPUImageFilter;
import com.xiaobukuaipao.youngmam.gpuimage.GPUImageView;
import com.xiaobukuaipao.youngmam.hlistview.HListView;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.utils.DisplayUtil;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.StringUtil;
import com.xiaobukuaipao.youngmam.utils.TimeUtil;
import com.xiaobukuaipao.youngmam.wrap.BaseHttpFragmentActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by xiaobu1 on 15-9-1.
 */
public class PhotoProcessActivity extends BaseHttpFragmentActivity {

    private static final String TAG = PhotoProcessActivity.class.getSimpleName();

    public static final int MAX_IMAGE_LENGTH = 2048;

    public static final int TYPE_WATERMARK_STICKER = 0;
    public static final int TYPE_WATERMARK = 1;
    public static final int TYPE_STICKER = 2;

    private ArrayList<ImageModel> mSelectedImage;
    private Label mTag;

    // 滤镜图片
    private GPUImageView mGPUImageView;
    // 大图滤镜图片
    private GPUImageView mLargeGPUImageView;
    private boolean isLarge = false;

    // 当前的图片
    private Bitmap currentBitmap;
    // 用于预览的小图片
    private Bitmap smallImagePreview;

    private View overlay;
    private StickerImageViewDrawableOverlay mImageView;

    private RelativeLayout drawArea;

    // 底部按钮
    // 水印
    private TextView btnWatermark;
    // 贴纸
    private TextView btnSticker;
    // 滤镜
    private TextView btnFilter;

    private RelativeLayout watermarkLayout;
    private List<StickerGroup> watermarkGroupList;
    private HListView watermarkGroupView;
    private StickerGroupAdapter watermarkGroupAdapter;
    private List<Sticker> watermarkList;
    private HListView watermarkView;
    private StickerAdapter watermarkAdapter;

    private RelativeLayout filterLayout;
    private HListView filterView;

    private RelativeLayout stickerLayout;
    private HListView stickerView;
    private StickerAdapter stickerAdapter;
    private List<Sticker> stickerList;

    private HListView stickerGroupView;
    private StickerGroupAdapter stickerGroupAdapter;
    private List<StickerGroup> stickerGroupList;

    private ImageButton btnBack;
    private ImageButton btnNext;

    // 维护选中的贴纸的数据结构
    private List<Sticker> selectedStickerList;

    // 公共变量
    private int[] sourceWH;
    private int[] overlays;
    private GPUImageFilter currentFilter;

    public void initViews() {
        setContentView(R.layout.activity_photo_process);

        getIntentDatas();

        drawArea = (RelativeLayout) findViewById(R.id.drawing_view_container);
        mGPUImageView = (GPUImageView) findViewById(R.id.gpuimage);
        mLargeGPUImageView = (GPUImageView) findViewById(R.id.large_gpuimage);

        btnWatermark = (TextView) findViewById(R.id.btn_watermark);
        btnSticker = (TextView) findViewById(R.id.btn_sticker);
        btnFilter = (TextView) findViewById(R.id.btn_filter);

        watermarkGroupList = new ArrayList<StickerGroup>();

        /**
         * 滤镜
         */
        filterLayout = (RelativeLayout) findViewById(R.id.filter_layout);
        filterView = (HListView) findViewById(R.id.filter_view);

        /**
         * 水印
         */
        watermarkLayout = (RelativeLayout) findViewById(R.id.watermark_layout);
        watermarkGroupView = (HListView) findViewById(R.id.watermark_group_view);
        watermarkView = (HListView) findViewById(R.id.watermark_view);

        watermarkGroupList = new ArrayList<StickerGroup>();
        watermarkGroupAdapter= new StickerGroupAdapter(this, watermarkGroupList, R.layout.item_bottom_sticker_group);
        watermarkGroupView.setAdapter(watermarkGroupAdapter);
        watermarkGroupView.setOnItemClickListener(new com.xiaobukuaipao.youngmam.hlistview.AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(com.xiaobukuaipao.youngmam.hlistview.AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < watermarkGroupList.size(); i++) {
                    if (i == position) {
                        watermarkGroupList.get(i).setSelected(true);
                    } else {
                        watermarkGroupList.get(i).setSelected(false);
                    }
                }

                watermarkGroupAdapter.notifyDataSetChanged();

                StickerGroup stickerGroup = watermarkGroupList.get(position);
                if (stickerGroup != null) {
                    // 此时, 默认去第一种贴纸
                    mEventLogic.getWatermarkById(stickerGroup.getGroupId());
                }
            }
        });

        watermarkList = new ArrayList<Sticker>();
        watermarkAdapter = new StickerAdapter(this, watermarkList, R.layout.item_bottom_sticker);
        watermarkView.setAdapter(watermarkAdapter);
        watermarkView.setOnItemClickListener(new com.xiaobukuaipao.youngmam.hlistview.AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(com.xiaobukuaipao.youngmam.hlistview.AdapterView<?> parent, View view, int position, long id) {

                Sticker sticker = watermarkList.get(position);

                EffectUtil.addStickerImage(PhotoProcessActivity.this, mImageView, sticker, new EffectUtil.StickerCallback() {
                    @Override
                    public void onRemoveSticker(Sticker sticker) {

                    }
                });

                for (int i = 0; i < watermarkList.size(); i++) {
                    if (i == position) {
                        watermarkList.get(i).setSelected(true);
                    } else {
                        watermarkList.get(i).setSelected(false);
                    }
                }

                watermarkAdapter.notifyDataSetChanged();
            }
        });

        /**
         * 贴纸
         */
        stickerLayout = (RelativeLayout) findViewById(R.id.sticker_layout);

        // 处理选中的贴纸
        selectedStickerList = new ArrayList<Sticker>();

        stickerList = new ArrayList<Sticker>();
        stickerView = (HListView) findViewById(R.id.sticker_view);
        stickerAdapter = new StickerAdapter(this, stickerList, R.layout.item_bottom_sticker);
        stickerView.setAdapter(stickerAdapter);
        stickerView.setOnItemClickListener(new com.xiaobukuaipao.youngmam.hlistview.AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(com.xiaobukuaipao.youngmam.hlistview.AdapterView<?> parent, View view, int position, long id) {

                Sticker sticker = stickerList.get(position);

                int count = selectedStickerList.size();

                if (count >= 3) {
                    if (!sticker.isSelected()) {
                        Toast.makeText(PhotoProcessActivity.this, getResources().getString(R.string.str_more_than_three), Toast.LENGTH_SHORT).show();
                    }
                    return;
                }

                EffectUtil.addStickerImage(PhotoProcessActivity.this, mImageView, sticker, new EffectUtil.StickerCallback() {
                    @Override
                    public void onRemoveSticker(Sticker sticker) {
                        sticker.setSelected(false);
                        for(int i = 0; i < stickerList.size(); i++) {
                            if (stickerList.get(i).getStickerId().equals(sticker.getStickerId())) {
                                stickerList.get(i).setSelected(false);
                            }
                        }

                        stickerAdapter.notifyDataSetChanged();

                        for (int i = 0; i < selectedStickerList.size(); i++) {
                            if (selectedStickerList.get(i).getStickerId().equals(sticker.getStickerId())) {
                                selectedStickerList.remove(sticker);
                            }
                        }
                    }
                });

                sticker.setSelected(true);
                selectedStickerList.add(sticker);
                stickerAdapter.notifyDataSetChanged();
            }
        });

        stickerGroupList = new ArrayList<StickerGroup>();
        stickerGroupView = (HListView) findViewById(R.id.sticker_group_view);
        stickerGroupAdapter = new StickerGroupAdapter(this, stickerGroupList, R.layout.item_bottom_sticker_group);
        stickerGroupView.setAdapter(stickerGroupAdapter);

        stickerGroupView.setOnItemClickListener(new com.xiaobukuaipao.youngmam.hlistview.AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(com.xiaobukuaipao.youngmam.hlistview.AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < stickerGroupList.size(); i++) {
                    if (i == position) {
                        stickerGroupList.get(i).setSelected(true);
                    } else {
                        stickerGroupList.get(i).setSelected(false);
                    }
                }

                stickerGroupAdapter.notifyDataSetChanged();

                StickerGroup stickerGroup = stickerGroupList.get(position);
                if (stickerGroup != null) {
                    // 此时, 默认去第一种贴纸
                    mEventLogic.getStickerById(stickerGroup.getGroupId());
                }
            }
        });

        btnBack = (ImageButton) findViewById(R.id.back);
        btnNext = (ImageButton) findViewById(R.id.next);

        // 清空操作
        EffectUtil.clear();

        initView();

        // 展示ProgressBar
        showProgress(getResources().getString(R.string.loading));

        // 初始化水印
        initWatermark();

        /**
         * 先检测布局完成之后, 再加载图片
         */
        drawArea.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                drawArea.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                int drawAreaWidth = drawArea.getMeasuredWidth();
                int drawAreaHeight = drawArea.getMeasuredHeight();

                sourceWH = ImageUtils.calculateSourceImageWH(mSelectedImage.get(mSelectedImage.size() - 1).path);
                overlays = ImageUtils.calculateDrawableOverlay(sourceWH[0], sourceWH[1],
                        drawAreaWidth, drawAreaHeight);

                if (sourceWH[0] > overlays[0] || sourceWH[1] > overlays[1]) {

                    mLargeGPUImageView.setSize(new GPUImageView.Size(overlays[0], overlays[1]));

                    if (sourceWH[0] > MAX_IMAGE_LENGTH && sourceWH[1] > MAX_IMAGE_LENGTH) {
                        if(sourceWH[0] >= sourceWH[1]) {
                            // 以宽为主
                            int width = MAX_IMAGE_LENGTH;
                            int height = MAX_IMAGE_LENGTH * sourceWH[1] / sourceWH[0];
                            mLargeGPUImageView.setSize(new GPUImageView.Size(width, height));
                        } else {
                            // 以高为主
                            int height = MAX_IMAGE_LENGTH;
                            int width = MAX_IMAGE_LENGTH * sourceWH[0] / sourceWH[1];
                            mLargeGPUImageView.setSize(new GPUImageView.Size(width, height));
                        }
                    } else if (sourceWH[0] > MAX_IMAGE_LENGTH && sourceWH[1] <= MAX_IMAGE_LENGTH) {
                        // 以宽为主
                        int width = MAX_IMAGE_LENGTH;
                        int height = MAX_IMAGE_LENGTH * sourceWH[1] / sourceWH[0];
                        mLargeGPUImageView.setSize(new GPUImageView.Size(width, height));
                    } else if (sourceWH[0] <= MAX_IMAGE_LENGTH && sourceWH[1] > MAX_IMAGE_LENGTH) {
                        int height = MAX_IMAGE_LENGTH;
                        int width = MAX_IMAGE_LENGTH * sourceWH[0] / sourceWH[1];
                        mLargeGPUImageView.setSize(new GPUImageView.Size(width, height));
                    } else {
                        mLargeGPUImageView.setSize(new GPUImageView.Size(sourceWH[0], sourceWH[1]));
                    }

                    isLarge = true;
                }

                mGPUImageView.setSize(new GPUImageView.Size(overlays[0], overlays[1]));

                RelativeLayout.LayoutParams rparams = new RelativeLayout.LayoutParams(overlays[0], overlays[1]);
                rparams.addRule(RelativeLayout.CENTER_IN_PARENT);
                mGPUImageView.setLayoutParams(rparams);
                mGPUImageView.setId(1);

                // 移出屏幕
                RelativeLayout.LayoutParams rparams1 = new RelativeLayout.LayoutParams(overlays[0],
                        overlays[1]);
                rparams1.addRule(RelativeLayout.RIGHT_OF, mGPUImageView.getId());
                rparams1.setMargins((DisplayUtil.getScreenWidth(PhotoProcessActivity.this) - overlays[0]) / 2 , 0, 0, 0);
                mLargeGPUImageView.setLayoutParams(rparams1);

                ImageUtils.asyncLoadImag(PhotoProcessActivity.this, Uri.fromFile(new File(mSelectedImage.get(mSelectedImage.size() - 1).path)), new ImageUtils.LoadImageCallback() {
                    @Override
                    public void callback(Bitmap result) {
                        currentBitmap = result;

                        mGPUImageView.setImage(currentBitmap);

                        // 设置贴纸背景跟Bitmap一样大
                        // 同时初始化滤镜图片
                        setBackParams(mGPUImageView);

                        hideProgress();
                    }
                });

                /**
                 * 异步加载缩略图
                 */
                ImageUtils.asyncLoadSmallImage(PhotoProcessActivity.this, Uri.fromFile(new File(mSelectedImage.get(mSelectedImage.size() - 1).path)), new ImageUtils.LoadImageCallback() {
                    @Override
                    public void callback(Bitmap result) {
                        smallImagePreview = result;

                        // 加载完小图片之后, 就加载滤镜
                        initFilterBar();
                    }
                });

            }
        });

        // 初始化按钮监听器
        initUIListeners();
    }

    /**
     * 选中水印
     */
    private void initWatermark() {
        watermarkLayout.setVisibility(View.VISIBLE);
        stickerLayout.setVisibility(View.GONE);
        filterLayout.setVisibility(View.GONE);

        btnWatermark.setSelected(true);
        btnSticker.setSelected(false);
        btnFilter.setSelected(false);

        btnWatermark.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getResources().getDrawable(R.mipmap.process_image_selected));
        btnSticker.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        btnFilter.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }

    public void executeHttpRequest() {
        // 水印取分组
        mEventLogic.getWatermarkGroup(TYPE_WATERMARK);
        // 取贴纸分组
        mEventLogic.getStickerGroup(TYPE_STICKER);
    }

    private void getIntentDatas() {
        mSelectedImage = getIntent().getParcelableArrayListExtra("selected_image");
        mTag = getIntent().getParcelableExtra("label");
    }

    private void initView() {
        // 添加贴纸的画布
        // overlay -- 覆盖的意思
        overlay = LayoutInflater.from(this).inflate(R.layout.view_drawable_overlay, null);
        // 设置GPUImageView的宽高
        mImageView = (StickerImageViewDrawableOverlay) overlay.findViewById(R.id.drawable_overlay);
        drawArea.addView(overlay);
    }

    private void setBackParams(GPUImageView gpuImageView) {
        int drawAreaWidth = drawArea.getMeasuredWidth();
        int drawAreaHeight = drawArea.getMeasuredHeight();
        int[] sourceWH = ImageUtils.calculateSourceImageWH(mSelectedImage.get(mSelectedImage.size() - 1).path);
        int[] overlays = ImageUtils.calculateDrawableOverlay(sourceWH[0], sourceWH[1],
                drawAreaWidth, drawAreaHeight);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(overlays[0], overlays[1]);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mImageView.setLayoutParams(params);
        overlay.setLayoutParams(params);
    }

    private void initUIListeners() {
        // 水印
        btnWatermark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initWatermark();
            }
        });

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                watermarkLayout.setVisibility(View.GONE);
                stickerLayout.setVisibility(View.GONE);
                filterLayout.setVisibility(View.VISIBLE);

                btnWatermark.setSelected(false);
                btnSticker.setSelected(false);
                btnFilter.setSelected(true);

                btnFilter.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getResources().getDrawable(R.mipmap.process_image_selected));
                btnSticker.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                btnWatermark.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

                // 每次都初始化一下, 不会花屏--注意处理
                if (filterLayout.getVisibility() != View.VISIBLE) {
                    initFilterBar();
                }
            }
        });

        btnSticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                watermarkLayout.setVisibility(View.GONE);
                stickerLayout.setVisibility(View.VISIBLE);
                filterLayout.setVisibility(View.GONE);

                btnWatermark.setSelected(false);
                btnSticker.setSelected(true);
                btnFilter.setSelected(false);

                btnSticker.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getResources().getDrawable(R.mipmap.process_image_selected));
                btnWatermark.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                btnFilter.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePicture();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * 保存图片
     */
    private void savePicture() {
        if (isLarge) {
            final Bitmap newBitmap = Bitmap.createBitmap(mLargeGPUImageView.getGPUImageViewWidth(), mLargeGPUImageView.getGPUImageViewHeight(),
                    Bitmap.Config.ARGB_8888);

            Canvas cv = new Canvas(newBitmap);

            RectF dst = new RectF(0, 0 - 5, mLargeGPUImageView.getGPUImageViewWidth() + 10, mLargeGPUImageView.getGPUImageViewHeight() + 10);

            mLargeGPUImageView.setImage(currentBitmap);

            if (currentFilter != null) {
                mLargeGPUImageView.setFilter(currentFilter);
            }

            try {
                cv.drawBitmap(mLargeGPUImageView.capture(), null, dst, null);
            } catch (InterruptedException e) {
                e.printStackTrace();
                cv.drawBitmap(currentBitmap, null, dst, null);
            }

            float ratio;

            float widthRatio = (float) mLargeGPUImageView.getGPUImageViewWidth() / overlays[0];
            float heightRatio = (float) mLargeGPUImageView.getGPUImageViewHeight() / overlays[1];

            if (widthRatio < heightRatio) {
                ratio = widthRatio;
            } else {
                ratio = heightRatio;
            }

            EffectUtil.applyOnSave(cv, mImageView, ratio);

            new SavePicToFileTask().execute(newBitmap);
        } else {
            final Bitmap newBitmap = Bitmap.createBitmap(mGPUImageView.getGPUImageViewWidth(), mGPUImageView.getGPUImageViewHeight(),
                    Bitmap.Config.ARGB_8888);

            Canvas cv = new Canvas(newBitmap);

            // 临时的解决黑边问题方案--具体原因还得研究
            RectF dst = new RectF(0, 0 - 5, mGPUImageView.getGPUImageViewWidth() + 10, mGPUImageView.getGPUImageViewHeight() + 10);

            try {
                cv.drawBitmap(mGPUImageView.capture(), null, dst, null);
            } catch (InterruptedException e) {
                e.printStackTrace();
                cv.drawBitmap(currentBitmap, null, dst, null);
            }

            // 加贴纸
            EffectUtil.applyOnSave(cv, mImageView);

            new SavePicToFileTask().execute(newBitmap);
        }
    }

    private class SavePicToFileTask extends AsyncTask<Bitmap,Void,String> {
        Bitmap bitmap;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress("图片处理中...");
        }

        @Override
        protected String doInBackground(Bitmap... params) {
            String fileName = null;
            try {
                bitmap = params[0];

                String picName = TimeUtil.dtFormat(new Date(), "yyyyMMddHHmmss");
                fileName = ImageUtils.saveToFile(FileUtils.getInstance().getPhotoSavedPath() + "/"+ picName, true, bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return fileName;
        }

        @Override
        protected void onPostExecute(String fileName) {
            super.onPostExecute(fileName);
            // 隐藏进度框
            hideProgress();

            if (StringUtil.isEmpty(fileName)) {
                return;
            }

            mSelectedImage.remove(mSelectedImage.size() - 1);
            ImageModel imageModel = new ImageModel();
            imageModel.path = fileName;

            mSelectedImage.add(imageModel);

            // 跳到发布页
            Intent intent = new Intent(PhotoProcessActivity.this, PublishActivity.class);
            intent.putParcelableArrayListExtra("selected_image", (ArrayList<ImageModel>) mSelectedImage);
            if (mTag != null) {
                intent.putExtra("label", mTag);
            }
            startActivity(intent);

            AppActivityManager.getInstance().finishActivity(PhotoProcessActivity.class);
        }
    }

    /**
     * 初始化滤镜--此处涉及花屏问题
     */
    private void initFilterBar() {
        final List<FilterEffect> filters = EffectService.getInstance().getLocalFilters();
        final FilterAdapter filterAdapter = new FilterAdapter(PhotoProcessActivity.this, filters, smallImagePreview);
        filters.get(0).setSelected(true);
        filterView.setAdapter(filterAdapter);

        filterView.setOnItemClickListener(new com.xiaobukuaipao.youngmam.hlistview.AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(com.xiaobukuaipao.youngmam.hlistview.AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < filters.size(); i++) {
                    if (i == position) {
                        filters.get(i).setSelected(true);
                    } else {
                        filters.get(i).setSelected(false);
                    }
                }

                if (filterAdapter.getSelectFilter() != position) {
                    filterAdapter.setSelectFilter(position);

                    currentFilter = GPUImageFilterTools.createFilterForType(
                            PhotoProcessActivity.this, filters.get(position).getType());
                    mGPUImageView.setFilter(currentFilter);
                }

                filterAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 网络数据返回处理
     * @param msg
     */
    @Override
    public void onResponse(Message msg) {
        switch (msg.what) {
            case R.id.get_sticker_group:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    if (jsonObject != null) {
                        JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_DATA);

                        if (jsonArray != null && jsonArray.size() > 0) {
                            for (int i=0; i < jsonArray.size(); i++) {
                                stickerGroupList.add(new StickerGroup(jsonArray.getJSONObject(i)));
                            }

                            stickerGroupList.get(0).setSelected(true);

                            stickerGroupAdapter.notifyDataSetChanged();

                            // 此时, 默认去第一种贴纸
                            mEventLogic.getStickerById(stickerGroupList.get(0).getGroupId());
                        }
                    }
                }
                break;

            case R.id.get_sticker_by_id:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    stickerList.clear();

                    if (jsonObject != null) {
                        JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_DATA);

                        if (jsonArray != null && jsonArray.size() > 0) {
                            for (int i=0; i < jsonArray.size(); i++) {
                                stickerList.add(new Sticker(jsonArray.getJSONObject(i)));
                            }

                            for (int i = 0; i < selectedStickerList.size(); i++) {
                                for (int j = 0; j < stickerList.size(); j++) {
                                    if (stickerList.get(j).getStickerId().equals(selectedStickerList.get(i).getStickerId())) {
                                        stickerList.get(j).setSelected(true);
                                    }
                                }
                            }

                            stickerAdapter.notifyDataSetChanged();
                        }
                    }
                }
                break;

            case R.id.get_watermark_group:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    if (jsonObject != null) {
                        Log.d(TAG, "watermark : " + httpResult.getData());

                        JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_DATA);

                        if (jsonArray != null && jsonArray.size() > 0) {
                            for (int i=0; i < jsonArray.size(); i++) {
                                watermarkGroupList.add(new StickerGroup(jsonArray.getJSONObject(i)));
                            }

                            watermarkGroupList.get(0).setSelected(true);

                            watermarkGroupAdapter.notifyDataSetChanged();

                            // 此时, 默认去第一种水印
                            mEventLogic.getWatermarkById(watermarkGroupList.get(0).getGroupId());
                        }
                    }
                }

                break;

            case R.id.get_watermark_by_id:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    watermarkList.clear();

                    if (jsonObject != null) {

                        JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_DATA);

                        if (jsonArray != null && jsonArray.size() > 0) {
                            for (int i=0; i < jsonArray.size(); i++) {
                                watermarkList.add(new Sticker(jsonArray.getJSONObject(i)));
                            }

                            watermarkAdapter.notifyDataSetChanged();
                        }
                    }
                }
                break;

            default:
                break;
        }
    }
}
