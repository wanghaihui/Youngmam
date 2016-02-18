package com.xiaobukuaipao.youngmam.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.adapter.StringPhotoPagerAdapter;
import com.xiaobukuaipao.youngmam.domain.CommonAction;
import com.xiaobukuaipao.youngmam.filter.util.FileUtils;
import com.xiaobukuaipao.youngmam.widget.CommonActionDialog;
import com.xiaobukuaipao.youngmam.widget.JazzyViewPager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by xiaobu1 on 15-6-5.
 */
@SuppressLint("ValidFragment")
public class PhotoViewFragment extends DialogFragment implements StringPhotoPagerAdapter.OnViewPagerPhotoClickListener,
            StringPhotoPagerAdapter.OnViewPagerPhotoLongClickListener, CommonActionDialog.OnCommonActionClickListener {

    private static final String TAG = ViewPagerDialogFragment.class.getSimpleName();

    private JazzyViewPager viewPager;
    private int mCurrentItem;

    /**
     * 所有的图片列表
     */
    private List<String> mPicList = new ArrayList<String>();

    private StringPhotoPagerAdapter mPhotoAdapter;

    private LinearLayout mDotsLayout;

    // 记录当前选中位置
    private int currentIndex;

    private List<CommonAction> commonActionList;

    private String currentUrl;

    public PhotoViewFragment() {
        super();
    }

    public PhotoViewFragment(List<String> picList, int currentItem) {
        mPicList.addAll(picList);
        mCurrentItem = currentItem;
    }

    /**
     * 必须重写的函数
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View picViewPager = inflater.inflate(R.layout.fragment_photo_view, null);
        mDotsLayout = (LinearLayout) picViewPager.findViewById(R.id.guide_dots_layout);
        viewPager = (JazzyViewPager) picViewPager.findViewById(R.id.view_pager);

        initViewsAndDatas();
        return picViewPager;
    }

    private void initViewsAndDatas() {

        if (mPicList != null && mPicList.size() > 1) {
            for (int i = 0; i < mPicList.size(); i++) {
                ImageView imageView = new ImageView(this.getActivity());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                imageView.setLayoutParams(params);
                imageView.setPadding(0, 0, 6, 0);
                imageView.setImageResource(R.drawable.guide_dot2);

                mDotsLayout.addView(imageView);
            }

            currentIndex = 0;
            // 设置为白色,即选中状态
            mDotsLayout.getChildAt(currentIndex).setEnabled(false);
        }

        setPhotoLists(mPicList);
        viewPager.setOnPageChangeListener(mOnPageChangeListener);
        viewPager.setCurrentItem(mCurrentItem);
        viewPager.setPageMargin(0);

        commonActionList = new ArrayList<CommonAction>();
        commonActionList.add(new CommonAction(CommonAction.ACTION_SAVE, "保存图片"));

        setUIListeners();
    }

    /**
     * 设置图片列表
     * @param mPhotos
     */
    public void setPhotoLists(List<String> mPhotos) {
        mPhotoAdapter = new StringPhotoPagerAdapter(viewPager);
        mPhotoAdapter.setOnViewPagerPhotoClickListener(this);
        mPhotoAdapter.setOnViewPagerPhotoLongClickListener(this);
        mPhotoAdapter.setPhotoLists(mPhotos);
        viewPager.setAdapter(mPhotoAdapter);
    }

    public void notifyChange() {
        mPhotoAdapter.notifyDataSetChanged();
    }

    private void setUIListeners() {

    }

    /**
     * 设置当前的点
     */
    private void setCurrentDot(int position) {
        if (position < 0 || position > mPicList.size() - 1 || currentIndex == position) {
            return;
        }

        mDotsLayout.getChildAt(position).setEnabled(false);
        mDotsLayout.getChildAt(currentIndex).setEnabled(true);

        currentIndex = position;
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            // 设置底部小点选中状态
            setCurrentDot(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onViewPagerPhotoClick() {
        this.dismiss();
    }

    @Override
    public void onViewPagerPhotoLongClick(String url) {
        currentUrl = url;
        // 此时, 下载图片
        CommonActionDialog commonActionDialog = new CommonActionDialog(this.getActivity(), commonActionList);
        commonActionDialog.setOnCommonActionClickListener(this);
        commonActionDialog.show();
    }

    @Override
    public void onCommonActionClick(CommonAction commonAction) {
        switch (commonAction.getActionId()) {
            case CommonAction.ACTION_SAVE:
                savePicture();
                break;
            default:
                break;
        }
    }

    /**
     * 保存图片
     */
    private void savePicture() {
        //获取内部存储状态
        String state = Environment.getExternalStorageState();
        //如果状态不是mounted, 无法读写
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this.getActivity(), "外部存储器不存在", Toast.LENGTH_SHORT).show();
            return;
        }

        // 确定文件名
        Calendar now = new GregorianCalendar();
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String fileName = simpleDate.format(now.getTime());

        new LoadImageTask(this.getActivity(), currentUrl, fileName).execute();

    }

    private static class LoadImageTask extends AsyncTask<Void, Void, Bitmap> {
        private final Context context;
        private final String url;
        private final String fileName;

        public LoadImageTask(Context context, String url, String fileName) {
            this.context = context;
            this.url = url;
            this.fileName = fileName;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {

            try {
                // 输入流
                InputStream inputStream;
                inputStream = new URL(url).openStream();
                return BitmapFactory.decodeStream(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            try {
                File fileFolder = new File(FileUtils.getInstance().getPhotoDownloadPath());
                if (!fileFolder.exists()) {
                    FileUtils.getInstance().mkdir(fileFolder);
                }

                File file = new File(FileUtils.getInstance().getPhotoDownloadPath() + "/" + fileName + ".jpg");

                FileOutputStream out = new FileOutputStream(file);
                result.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();

                //保存图片后发送广播通知更新数据库
                Uri uri = Uri.fromFile(file);
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));

                Toast.makeText(context, "图片已保存:" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
