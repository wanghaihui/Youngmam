package com.xiaobukuaipao.youngmam;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.youngmam.crop.Crop;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.imagechooser.ImageChooserActivity;
import com.xiaobukuaipao.youngmam.utils.FileUtil;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.StringUtil;
import com.xiaobukuaipao.youngmam.widget.YoungActionBar;
import com.xiaobukuaipao.youngmam.wrap.BaseHttpFragmentActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Created by xiaobu1 on 15-4-28.
 */
public class MamRegister1Activity extends BaseHttpFragmentActivity {
    private static final String TAG = MamRegister1Activity.class.getSimpleName();

    public static final int CHILD_STATUS_BEFORE_PREGNANT = 1;
    public static final int CHILD_STATUS_PREGNANT = 2;
    public static final int CHILD_STATUS_CHILD = 3;

    private ImageView mAvatar;
    private ImageButton next;

    private String imagePath;

    /**
     * 上传的图片在服务器上的地址
     */
    private String imageServerPath;

    /**
     * 昵称--nickname
     */
    private EditText mNickEditText;
    private String mNickname;

    /**
     * 亲子状态--默认是有孩子
     */
    private int childStatus = CHILD_STATUS_CHILD;

    // 备孕中
    private TextView mPrepare;
    // 准妈妈
    private TextView mPregnant;
    // 家有小宝
    private TextView mChild;

    private FrameLayout mAvatarLayout;

    // 头像目录
    private String avatarPath = FileUtil.SD_PATH_PHOTO_AVATAR + "avatar.jpg";
    private String avatarServerPath = FileUtil.SD_PATH_PHOTO_AVATAR + "avatar_server.jpg";

    /**
     * 初始化View
     */
    public void initViews() {
        setContentView(R.layout.activity_mam_register1);

        // 设置ActionBar
        actionBar = (YoungActionBar) findViewById(R.id.action_bar);
        setYoungActionBar();

        mAvatar = (ImageView) findViewById(R.id.avatar);

        mNickEditText = (EditText) findViewById(R.id.nick_name);
        mNickEditText.addTextChangedListener(nextWatcher);

        mPrepare = (TextView) findViewById(R.id.prepare);
        mPregnant = (TextView) findViewById(R.id.pregnant);
        mChild = (TextView) findViewById(R.id.child);

        next = (ImageButton) findViewById(R.id.next);

        mAvatarLayout = (FrameLayout) findViewById(R.id.avatar_layout);

        setUIListeners();
    }

    /**
     * 设置ActionBar
     */
    private void setYoungActionBar() {
        actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);
        actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, getString(R.string.str_mam_basic_info));

        setBackClickListener(this);
    }

    private void setUIListeners() {

        mAvatarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MamRegister1Activity.this, ImageChooserActivity.class);
                intent.putExtra("show_style", ImageChooserActivity.SHOW_STYLE_VIEW);
                startActivityForResult(intent, Crop.REQUEST_PICK);
            }
        });

        /**
         * 备孕中
         */
        mPrepare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPrepare.setEnabled(false);
                mPregnant.setEnabled(true);
                mChild.setEnabled(true);
                childStatus = CHILD_STATUS_BEFORE_PREGNANT;
            }
        });

        /**
         * 准妈妈
         */
        mPregnant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPrepare.setEnabled(true);
                mPregnant.setEnabled(false);
                mChild.setEnabled(true);
                childStatus = CHILD_STATUS_PREGNANT;
            }
        });

        /**
         * 家有小宝
         */
        mChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPrepare.setEnabled(true);
                mPregnant.setEnabled(true);
                mChild.setEnabled(false);
                childStatus = CHILD_STATUS_CHILD;
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 在这里执行网络请求
                 */
                mNickname = mNickEditText.getText().toString();
                if (!StringUtil.isEmpty(imageServerPath) && !StringUtil.isEmpty(mNickname)) {
                    mEventLogic.setBasicInfo(imageServerPath, mNickname, String.valueOf(childStatus));
                } else {
                    if (StringUtil.isEmpty(imageServerPath)) {
                        Toast.makeText(MamRegister1Activity.this, getResources().getString(R.string.str_upload_avatar_error), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MamRegister1Activity.this, getResources().getString(R.string.str_nickname_error), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private TextWatcher nextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!StringUtil.isEmpty(mNickEditText.getText().toString()) && !StringUtil.isEmpty(imageServerPath)) {
                next.setImageResource(R.drawable.btn_register);
            } else {
                next.setImageResource(R.mipmap.btn_register_unpress);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "request code : " + requestCode);
        Log.d(TAG, "result code : " + resultCode);

        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            startCrop(Uri.fromFile(new File(data.getStringExtra("image_path"))));
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
    }


    // 裁剪
    private void startCrop(Uri source) {
        File file = new File(getCacheDir(), "crop");
        imagePath = file.getAbsolutePath();
        Uri destination = Uri.fromFile(file);
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Uri uri = Crop.getOutput(result);
            try {
                savefile(uri);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            try {
                FileUtil.saveBitmap(revitionImageSize(avatarPath), avatarServerPath);
                mAvatar.setImageBitmap(revitionImageSize(avatarPath));
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 此时,进行网络上传
            mEventLogic.uploadAvatar(avatarServerPath);

            showProgress(getString(R.string.uploading));

        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

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

        while (true) {
            if ((options.outWidth >> i <= 3000) && (options.outHeight >> i <= 3000)) {
                in = new BufferedInputStream(new FileInputStream(new File(path)));
                options.inSampleSize = (int) Math.pow(2.0D, i);
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeStream(in, null, options);
                break;
            }
            i += 1;
        }

        // 变化角度
        // 在这里获得图片的旋转角度
        int angle = readPictureDegree(path);

        return rotateImageView(angle, bitmap);
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

    /**
     * 网络数据返回处理
     * @param msg
     */
    @Override
    public void onResponse(Message msg) {
        switch (msg.what) {
            case R.id.upload_avatar:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    imageServerPath = httpResult.getData();
                    Log.d(TAG, "imageServerPath : " + imageServerPath);
                    Toast.makeText(this, getString(R.string.uploading_success), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.uploading_failure), Toast.LENGTH_SHORT).show();
                }

                if (!StringUtil.isEmpty(mNickEditText.getText().toString()) && !StringUtil.isEmpty(imageServerPath)) {
                    next.setImageResource(R.drawable.btn_register);
                } else {
                    next.setImageResource(R.mipmap.btn_register_unpress);
                }
                break;

            case R.id.set_basic_info:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;

                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());
                    int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);
                    String message = jsonObject.getString(GlobalConstants.JSON_MSG);

                    if (status == 0) {
                        if (childStatus == CHILD_STATUS_BEFORE_PREGNANT) {

                            AppActivityManager.getInstance().popAllActivity();

                            Intent intent = new Intent(MamRegister1Activity.this, HuaYoungActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(MamRegister1Activity.this, MamRegister2Activity.class);
                            intent.putExtra("child_status", childStatus);
                            startActivity(intent);
                        }
                    } else {
                        showToast(message);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void savefile(Uri sourceuri) throws IOException {
        // We'll read in one kB at a time
        final int chunkSize = 1024;
        byte[] imageData = new byte[chunkSize];

        InputStream in = null;
        OutputStream out = null;
        File file = null;

        try {
            File avatarDir = new File(FileUtil.SD_PATH_PHOTO_AVATAR);
            if(avatarDir.exists()) {
                FileUtil.deleteFolder(FileUtil.SD_PATH_PHOTO_AVATAR);
            }
            FileUtil.createFolder(FileUtil.SD_PATH_PHOTO_AVATAR);
            file = FileUtil.createFile(avatarPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try{
            in = getContentResolver().openInputStream(sourceuri);
            out = new FileOutputStream(file);

            int bytesRead;
            while ((bytesRead = in.read(imageData)) > 0) {
                out.write(Arrays.copyOfRange(imageData, 0, Math.max(0, bytesRead)));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            in.close();
            out.close();
        }
    }

}
