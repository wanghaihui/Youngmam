package com.xiaobukuaipao.youngmam.imagechooser;

import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.greenrobot.event.EventBus;
import com.xiaobukuaipao.youngmam.PublishActivity;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.domain.ImageModel;
import com.xiaobukuaipao.youngmam.domain.Label;
import com.xiaobukuaipao.youngmam.filter.PhotoProcessActivity;
import com.xiaobukuaipao.youngmam.fragment.ViewPagerDialogFragment;
import com.xiaobukuaipao.youngmam.imagechooser.ImageChooserAdapter.AdapterEventListener;
import com.xiaobukuaipao.youngmam.utils.SDCardUtil;
import com.xiaobukuaipao.youngmam.widget.YoungActionBar;
import com.xiaobukuaipao.youngmam.wrap.BaseHttpFragmentActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xiaobu1 on 15-4-9.
 */
public class ImageChooserActivity extends BaseHttpFragmentActivity implements ImageDirPopupWindow.OnImageDirSelectedListener {
    private static final String TAG = ImageChooserActivity.class.getSimpleName();

    public static final int SHOW_STYLE_MULTIPLE = 0;
    public static final int SHOW_STYLE_VIEW = 1;

    public static final int MEDIA_TYPE_IMAGE = 1;

    private GridView mChooserGrid;
    private ImageChooserAdapter mImageChooserAdapter;

    /**
     * 图片扫描任务
     */
    private ImageLoaderTask imageLoaderTask = null;

    /**
     * 保存当前目录的所有图片的URL列表
     */
    private List<ImageModel> mCurrentImageList;

    /**
     * 图片文件夹PopupWindow
     */
    private ImageDirPopupWindow mImageDirPopupWindow;
    // 屏幕高度
    private int mScreenHeight;

    // 底部View
    private View mChooserBottom;

    // 选择的数量
    private TextView selectNum;

    /**
     * 相机拍照
     */
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;

    /**
     * 当前选择的图片
     */
    private List<ImageModel> mSelectedImage;

    /**
     * 当前拍照的照片
     */
    private String mCurrentPhotoPath;

    /**
     * 照片选择是单选模式还是多选模式,默认是多选
     */
    private int showStyle;

    /**
     * 从上层带过来的Label
     */
    private Label mTag;

    /**
     * 从上层带过来的选择的图片
     */
    private ArrayList<ImageModel> mPublishSelectedImage;

    /**
     * 记录当前的选择的目录
     */
    private String currentDirectory;

    private static final String CURRENT_PHOTO_PATH = "current_photo_path";

    private EventBus eventBus;

    private int typePublish;

    public void initViews() {
        setContentView(R.layout.activity_image_chooser);

        getIntentDatas();

        eventBus = EventBus.getDefault();
        eventBus.register(this);

        // 设置ActionBar
        actionBar = (YoungActionBar) findViewById(R.id.action_bar);
        setYoungActionBar();

        currentDirectory = getResources().getString(R.string.str_all_images);

        mChooserGrid = (GridView) findViewById(R.id.chooser_grid);

        mChooserBottom = (View) findViewById(R.id.chooser_bottom_layout);

        selectNum = (TextView) findViewById(R.id.select_num);

        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        mScreenHeight = outMetrics.heightPixels;

        mSelectedImage = new ArrayList<ImageModel>();

        if (mPublishSelectedImage != null) {
            for(int i=0; i < mPublishSelectedImage.size(); i++) {
                mSelectedImage.add(mPublishSelectedImage.get(i));
            }

            selectNum.setText(mSelectedImage.size() + "/9");
        }

        // 加载图片
        loadImages();
        // 设置监听器
        setUIListeners();
    }

    private void getIntentDatas() {
        showStyle = getIntent().getIntExtra("show_style", SHOW_STYLE_MULTIPLE);
        mTag = getIntent().getParcelableExtra("label");
        Log.d(TAG, "show style : " + showStyle);

        mPublishSelectedImage = getIntent().getParcelableArrayListExtra("publish_selected_image");

        typePublish = getIntent().getIntExtra("type_publish", PublishActivity.TYPE_PUBLISH_ARTICLE);
    }

    /**
     * 设置ActionBar
     */
    private void setYoungActionBar() {
        actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);
        actionBar.setMiddleAction(YoungActionBar.Type.TEXT_IMAGE, /*R.drawable.btn_select_gallery*/R.mipmap.pop_gallery,
                getResources().getString(R.string.str_all_images));

        // actionBar.setRightAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_next));

        setBackClickListener(this);

        actionBar.getMiddleFrame().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取状态栏高度
                Rect frame = new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
                // 状态栏高度：frame.top
                // 弹出相册
                // mImageDirPopupWindow.setAnimationStyle(R.style.anim_dir_popup);
                mImageDirPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
                mImageDirPopupWindow.showAtLocation(actionBar, Gravity.TOP,
                        0, (int) ImageChooserActivity.this.getResources().getDimension(R.dimen.action_bar_height) + frame.top);

                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = .3f;
                getWindow().setAttributes(lp);

                actionBar.setMiddleAction(YoungActionBar.Type.TEXT_IMAGE, R.mipmap.pull_gallery, currentDirectory);
            }
        });

        // 下一步
        /*actionBar.getRightFrame().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedImage.size() > 0) {
                    Intent intent = new Intent(ImageChooserActivity.this, PhotoProcessActivity.class);
                    intent.putParcelableArrayListExtra("selected_image", (ArrayList<ImageModel>) mSelectedImage);
                    if (mTag != null) {
                        intent.putExtra("label", mTag);
                    }
                    startActivity(intent);

                    finish();
                } else {
                    Toast.makeText(ImageChooserActivity.this, getResources().getString(R.string.str_at_least_one_pic), Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }

    private void loadImages() {
        if (!SDCardUtil.hasExternalStorage()) {
            return;
        }

        // 如果线程正在执行
        if (imageLoaderTask != null && imageLoaderTask.getStatus() == AsyncTask.Status.RUNNING) {
            return;
        }

        // 展示ProgressBar
        showProgress(getResources().getString(R.string.please_waiting));

        imageLoaderTask = new ImageLoaderTask(this, new OnTaskResultListener() {
            @Override
            public void onResult(boolean success, String error, Object result) {
                // 如果加载成功
                if (success && (result != null) && (result instanceof ArrayList)) {

                    hideProgress();

                    setImageAdapter((ArrayList<ImageDirectory>) result);
                    initImageDirPopupWindow((ArrayList<ImageDirectory>) result);
                } else {
                    // 加载失败
                }
            }
        });

        // 执行异步加载任务
        TaskExecuteUtil.execute(imageLoaderTask);
    }

    /**
     * 初始化图片文件夹的PopupWindow
     */
    private void initImageDirPopupWindow(List<ImageDirectory> data) {
        mImageDirPopupWindow = new ImageDirPopupWindow(LayoutInflater.from(this).inflate(R.layout.image_dir_list, null),
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (mScreenHeight * 0.7), data);

        mImageDirPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);

                actionBar.setMiddleAction(YoungActionBar.Type.TEXT_IMAGE, R.mipmap.pop_gallery, currentDirectory);
            }
        });

        mImageDirPopupWindow.setOnImageDirSelectedListener(this);
    }

    private void setUIListeners() {
        // 图片列表点击查看大图
        mChooserGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (mCurrentImageList.get(position).isFunction) {
                    // 说明,此时是拍照功能
                    if (mSelectedImage.size() < 9) {
                        captureImage();
                    } else {
                        Toast.makeText(ImageChooserActivity.this, getResources().getString(R.string.reached_max_size), Toast.LENGTH_SHORT).show();
                    }
                    return;
                }

                if (showStyle == SHOW_STYLE_MULTIPLE) {
                    // 进入展示大图页
                    // showPagerView(position);

                    if (typePublish == PublishActivity.TYPE_PUBLISH_ARTICLE) {
                        // 此时, 直接跑到滤镜贴纸页面--美化图片
                        beautifyImage(mCurrentImageList.get(position));
                    } else if (typePublish == PublishActivity.TYPE_PUBLISH_QUESTION) {
                        mSelectedImage.add(mCurrentImageList.get(position));

                        // 此时是发布问题
                        Intent intent = new Intent(ImageChooserActivity.this, PublishActivity.class);
                        intent.putParcelableArrayListExtra("selected_image", (ArrayList<ImageModel>) mSelectedImage);
                        if (mTag != null) {
                            intent.putExtra("label", mTag);
                        }
                        startActivity(intent);

                        finish();
                    }

                } else if (showStyle == SHOW_STYLE_VIEW) {
                    // 进入裁剪页
                    Intent intent = new Intent();
                    intent.putExtra("image_path", mCurrentImageList.get(position).path);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    /**
     * 美化图片
     * @param imageModel
     */
    private void beautifyImage(ImageModel imageModel) {
        mSelectedImage.add(imageModel);

        Log.d(TAG, "mSelectedImage size : " + mSelectedImage.size());

        Intent intent = new Intent(ImageChooserActivity.this, PhotoProcessActivity.class);
        intent.putParcelableArrayListExtra("selected_image", (ArrayList<ImageModel>) mSelectedImage);
        if (mTag != null) {
            intent.putExtra("label", mTag);
        }
        startActivity(intent);

        finish();
    }

    /**
     * 展示相册大图
     */
    private void showPagerView(int position) {
        /*int containsNum = 0;
        for (int i=0; i < mSelectedImage.size(); i++) {
            for (int j=0; j < mCurrentImageList.size(); j++) {
                if (mSelectedImage.get(i).path != null && mCurrentImageList.get(j).path != null &&
                        mCurrentImageList.get(j).path.equals(mSelectedImage.get(i).path)) {
                    containsNum++;
                }
            }
        }*/
        ViewPagerDialogFragment fragment = new ViewPagerDialogFragment(mCurrentImageList, position, mSelectedImage );
        // fragment.setCanSelectedNum(9 - (mSelectedImage.size() - containsNum));
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_NoActionBar_Fullscreen);
        fragment.setOnEventListener(mViewPagerListener);
        fragment.show(this.getSupportFragmentManager(), "viewpager");
    }

    /**
     * 拍照
     */
    private void captureImage() {
        // 此时,目前只支持调用系统相机
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    /**
     * 构建GridView的适配器
     */
    private void setImageAdapter(List<ImageDirectory> data) {
        if (mCurrentImageList == null) {
            mCurrentImageList = new ArrayList<ImageModel>();
        } else {
            mCurrentImageList.clear();
        }

        // 添加Take Photo按钮
        ImageModel item = new ImageModel();
        item.isFunction = true;
        mCurrentImageList.add(item);

        int size = data.get(0).getImages().size();
        for (int i = 0; i < size; i++) {
            mCurrentImageList.add(data.get(0).getImages().get(i));
        }

        for(int i=0; i < mSelectedImage.size(); i++) {
            for(int j=0; j < mCurrentImageList.size(); j++) {
                if ((mCurrentImageList.get(j).path != null) && (mCurrentImageList.get(j).path).equals(mSelectedImage.get(i).path)) {
                    mCurrentImageList.get(j).isSelected = true;
                }
            }
        }

        // 此时,保存了手机中所有的图片信息
        mImageChooserAdapter = new ImageChooserAdapter(this, mCurrentImageList, R.layout.item_image_chooser,
                mAdapterEventListener, mSelectedImage, showStyle);

        mChooserGrid.setAdapter(mImageChooserAdapter);
    }

    /**
     * PopupWindow文件夹被选择
     */
    @Override
    public void onSelected(ImageDirectory directory) {
        mCurrentImageList.clear();

        actionBar.setMiddleAction(YoungActionBar.Type.TEXT_IMAGE, R.mipmap.pop_gallery, directory.getDirName());
        currentDirectory = directory.getDirName();
        Log.d(TAG, "select directory :" + directory.getDirName());

        // if (directory.getDirName().equals("所有图片")) {
        ImageModel item = new ImageModel();
        item.isFunction = true;
        mCurrentImageList.add(item);
        //}

        int size = directory.getImages().size();
        for (int i = 0; i < size; i++) {
            mCurrentImageList.add(directory.getImages().get(i));
        }

        for(int i=0; i < mSelectedImage.size(); i++) {
            for(int j=0; j < mCurrentImageList.size(); j++) {
                if (mCurrentImageList.get(j).path != null && mCurrentImageList.get(j).path.equals(mSelectedImage.get(i).path)) {
                    mCurrentImageList.get(j).isSelected = true;
                }
            }
        }

        mImageChooserAdapter.notifyDataSetChanged();
    }


    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


    /** Create a file Uri for saving an image or video */
    private Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "/Youngmam/Camera");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("Youngmam", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else {
            return null;
        }

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg";

        return mediaFile;
    }

    /**
     * 结果返回
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent

                //if (data == null) {
                    // 获得图片路径,跳到Publish页
                    /*String[] projection = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(fileUri, projection, null, null, null);
                    cursor.moveToFirst();
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));*/
                    /**
                     * 将拍的照片加到Media Store中
                     */
                    galleryAddPic();

                    ImageModel imageModel = new ImageModel();
                    imageModel.path = mCurrentPhotoPath;

                    if (showStyle == SHOW_STYLE_MULTIPLE) {
                        /*mSelectedImage.add(imageModel);
                        Intent intent = new Intent(ImageChooserActivity.this, PublishActivity.class);
                        intent.putParcelableArrayListExtra("selected_image", (ArrayList<ImageModel>) mSelectedImage);
                        startActivity(intent);*/

                        beautifyImage(imageModel);
                    } else if (showStyle == SHOW_STYLE_VIEW) {
                        // 照相完后进入裁剪页
                        Intent intent = new Intent();
                        intent.putExtra("image_path", imageModel.path);
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                /*} else {
                    Log.d(TAG, "capture image data null");
                }*/

            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }

        }

    }

    //////////////////////////////////////////////////////////////////////////////
    private AdapterEventListener mAdapterEventListener = new ImageChooserAdapter.AdapterEventListener() {
        @Override
        public void onItemSelectedStatusChange(int position, boolean isSelected) {
            updateSelectedNum();
        }
    };

    private void updateSelectedNum() {
        // 临时这种写法,以后需要扩展
        selectNum.setText(mSelectedImage.size() + "/9");
    }

    ViewPagerListener mViewPagerListener = new ViewPagerListener() {

        @Override
        public void onDone() {
            Intent intent = new Intent(ImageChooserActivity.this, PublishActivity.class);

            intent.putParcelableArrayListExtra("selected_image", (ArrayList<ImageModel>) mSelectedImage);
            if (mTag != null) {
                intent.putExtra("label", mTag);
            }
            startActivity(intent);

            finish();
        }

        @Override
        public void onDismiss() {
            updateSelectedNum();
            mImageChooserAdapter.notifyDataSetChanged();
        }

    };

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 为什么要这样做呢? 因为有的android机器上调用拍照等外部程序后回到本Activity后会先执行到ondestory(), 然后重新执行onCreate()流程
     * 这有时候是一个很蛋疼的问题, 重新来了一遍, 所改变的数据可以都会被重新初始化
     */
    // 保存
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(CURRENT_PHOTO_PATH, mCurrentPhotoPath);
        super.onSaveInstanceState(outState);
    }

    // 读取
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mCurrentPhotoPath = savedInstanceState.getString(CURRENT_PHOTO_PATH);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onResponse(Message msg) {
        switch (msg.what) {
            default:
                break;
        }
    }

}
