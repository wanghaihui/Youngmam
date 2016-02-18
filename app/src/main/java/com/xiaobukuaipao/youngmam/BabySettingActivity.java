package com.xiaobukuaipao.youngmam;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.squareup.picasso.Picasso;
import com.xiaobukuaipao.youngmam.crop.Crop;
import com.xiaobukuaipao.youngmam.database.MamaTable;
import com.xiaobukuaipao.youngmam.domain.ChildStatus;
import com.xiaobukuaipao.youngmam.fragment.AvatarDialogFragment;
import com.xiaobukuaipao.youngmam.fragment.ModifyDialogFragment;
import com.xiaobukuaipao.youngmam.fragment.ModifyInfoListener;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.imagechooser.ImageChooserActivity;
import com.xiaobukuaipao.youngmam.provider.YoungContentProvider;
import com.xiaobukuaipao.youngmam.utils.FileUtil;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.StringUtil;
import com.xiaobukuaipao.youngmam.widget.PopupDialog;
import com.xiaobukuaipao.youngmam.widget.YoungActionBar;
import com.xiaobukuaipao.youngmam.wrap.BaseHttpFragmentActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by xiaobu1 on 15-4-30.
 */
public class BabySettingActivity extends BaseHttpFragmentActivity implements PopupDialog.StatusItemClickListener{
    private static final String TAG = BabySettingActivity.class.getSimpleName();

    public static final int REQUEST_SETTING = 100;

    // 头像布局
    private RelativeLayout mAvatarLayout;

    private TextView mTxtAvatar;
    private ImageView mImgAvatar;
    private TextView mNickname;
    private TextView mState;

    // 头像
    private String headUrl;
    // 昵称
    private String name;
    // 亲子状态Str
    private String statusStr;
    // 亲子状态
    private int childStatus;
    // 性别
    private int gender = 0;
    // 日期
    private long birthTime;

    private String imagePath;

    // 亲子状态
    private RelativeLayout stateLayout;

    // 昵称
    private RelativeLayout nameLayout;

    // 头像目录
    private String avatarPath = FileUtil.SD_PATH_PHOTO_AVATAR + "avatar.jpg";
    private String avatarServerPath = FileUtil.SD_PATH_PHOTO_AVATAR + "avatar_server.jpg";

    public void initViews() {
        setContentView(R.layout.activity_baby_setting);
        // 设置ActionBar
        actionBar = (YoungActionBar) findViewById(R.id.action_bar);

        setYoungActionBar();

        mAvatarLayout = (RelativeLayout) findViewById(R.id.avatar_layout);
        nameLayout = (RelativeLayout) findViewById(R.id.name_layout);
        stateLayout = (RelativeLayout) findViewById(R.id.state_layout);

        mTxtAvatar = (TextView) findViewById(R.id.txt_avatar);
        mImgAvatar = (ImageView) findViewById(R.id.avatar);
        mNickname = (TextView) findViewById(R.id.txt_nickname);
        mState = (TextView) findViewById(R.id.state);

        // 设置数据
        initDatas();
        // 设置监听器
        setUIListeners();
    }

    /**
     * 设置ActionBar
     */
    private void setYoungActionBar() {
        actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);
        actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_setting));
        actionBar.setRightAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_complete));

        setBackClickListener(this);

        actionBar.getRightFrame().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 设置用户基本信息
                setUserInfo();
            }
        });
    }

    private void setUserInfo() {
        if (!StringUtil.isEmpty(name)) {
            mEventLogic.setBasicInfo(headUrl, name, String.valueOf(childStatus));
            showToast(getResources().getString(R.string.setting));
        } else {
            Toast.makeText(this, "昵称不能为空哦~", Toast.LENGTH_SHORT).show();
        }
    }

    private void initDatas() {
        mTxtAvatar.setText(getString(R.string.str_avatar));

        Cursor cursor = getContentResolver().query(YoungContentProvider.MAMA_CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            headUrl = cursor.getString(cursor.getColumnIndex(MamaTable.COLUMN_HEAD_URL));
            name = cursor.getString(cursor.getColumnIndex(MamaTable.COLUMN_NAME));
            childStatus = cursor.getInt(cursor.getColumnIndex(MamaTable.COLUMN_CHILD_STATUS));

            if (headUrl != null) {
                Picasso.with(this)
                        .load(headUrl)
                        .into(mImgAvatar);
            } else {
                mImgAvatar.setImageResource(R.drawable.mam_default_avatar);
            }

            mNickname.setText(name);

            cursor.close();
        }

    }

    public void executeHttpRequest() {
        /**
         * 获取孩子的基本信息
         */
        mEventLogic.getUserChild();
    }

    private void setUIListeners() {
        mAvatarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BabySettingActivity.this, ImageChooserActivity.class);
                intent.putExtra("show_style", ImageChooserActivity.SHOW_STYLE_VIEW);
                startActivityForResult(intent, Crop.REQUEST_PICK);
            }
        });

        stateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 弹出选择框
                popupStatus();
            }
        });

        mImgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AvatarDialogFragment fragment = new AvatarDialogFragment(headUrl);
                fragment.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_NoActionBar);
                fragment.show(BabySettingActivity.this.getSupportFragmentManager(), "avatar");
            }
        });

        nameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModifyDialogFragment fragment = new ModifyDialogFragment(mNickname.getText().toString());
                fragment.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_NoActionBar);
                fragment.setOnModifyInfoListener(modifyInfoListener);
                fragment.show(BabySettingActivity.this.getSupportFragmentManager(), "name");
            }
        });

    }

    private void popupStatus() {
        List<ChildStatus> statusList = new ArrayList<ChildStatus>();

        ChildStatus prepare = new ChildStatus();
        prepare.setId(1);
        prepare.setName(getResources().getString(R.string.str_prepare));
        statusList.add(prepare);

        ChildStatus gravida = new ChildStatus();
        gravida.setId(2);
        gravida.setName(getResources().getString(R.string.str_gravida));
        statusList.add(gravida);

        ChildStatus hasBaby = new ChildStatus();
        hasBaby.setId(3);
        hasBaby.setName(getResources().getString(R.string.str_hava_baby));
        statusList.add(hasBaby);

        PopupDialog popupDialog = new PopupDialog(this, statusList);
        popupDialog.setStatusItemClickListener(this);
        popupDialog.show();
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
                    headUrl = httpResult.getData();
                    Toast.makeText(this, getString(R.string.uploading_success), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.uploading_failure), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.get_user_child:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;

                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    if (jsonObject != null) {
                        gender = jsonObject.getInteger(GlobalConstants.JSON_GENDER);
                        birthTime = jsonObject.getLong(GlobalConstants.JSON_BIRTHTIME);
                    }
                    setChildState();
                }
                break;

            case R.id.set_basic_info:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;

                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);
                    String message = jsonObject.getString(GlobalConstants.JSON_MSG);
                    if (status == 0) {
                        if (gender == 0) {
                            mEventLogic.setBabyInfo(null, String.valueOf(birthTime));
                        } else {
                            mEventLogic.setBabyInfo(String.valueOf(gender), String.valueOf(birthTime));
                        }
                    } else if (status == 2) {
                        showToast(message);
                    }
                }
                break;

            case R.id.set_baby_info:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;

                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);

                    if (status == 0) {
                        Intent intent = new Intent();
                        intent.putExtra("success", true);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
                break;

            default:
                break;
        }
    }

    private void setChildState() {
        StringBuilder sb = new StringBuilder();

        switch (childStatus) {
            case 1:
                sb.append(getResources().getString(R.string.str_prepare));
                break;
            case 2:
                sb.append(getResources().getString(R.string.str_gravida));
                break;
            case 3:
                sb.append(getResources().getString(R.string.str_hava_baby));
                break;
            default:
                sb.append(getResources().getString(R.string.str_prepare));
                break;
        }

        switch (gender) {
            case 1:
                sb.append(",");
                sb.append(getResources().getString(R.string.str_boy));
                sb.append(",");
                break;
            case 2:
                sb.append(",");
                sb.append(getResources().getString(R.string.str_girl));
                sb.append(",");
                break;
            default:
                break;
        }

        if (birthTime > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sb.append(sdf.format(birthTime));
        }

        statusStr = sb.toString();

        mState.setText(statusStr);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            startCrop(Uri.fromFile(new File(data.getStringExtra("image_path"))));
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        } else if (requestCode == REQUEST_SETTING) {
            if (data != null) {
                gender = data.getIntExtra("gender", 0);
                birthTime = Long.valueOf(data.getStringExtra("birth_time"));
                setChildState();
            }
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
                mImgAvatar.setImageBitmap(revitionImageSize(avatarPath));
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

    //////////////////////////////////////////////////////////////////////////////////////////////////

    // Dialog
    @Override
    public void onStatusItemClick(ChildStatus status) {
        childStatus = status.getId();

        if (childStatus == 1) {
            statusStr = getResources().getString(R.string.str_prepare);
            mState.setText(statusStr);
        } else {
            Intent intent = new Intent(BabySettingActivity.this, MamRegister2Activity.class);
            intent.putExtra("child_status", childStatus);
            intent.putExtra("source_setting", true);
            startActivityForResult(intent, REQUEST_SETTING);
        }

    }

    ModifyInfoListener modifyInfoListener = new ModifyInfoListener() {
        @Override
        public void onDone(String info) {
            name = info;
            mNickname.setText(info);
        }

        @Override
        public void onDismiss() {

        }
    };


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
