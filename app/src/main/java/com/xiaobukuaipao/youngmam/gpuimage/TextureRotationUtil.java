package com.xiaobukuaipao.youngmam.gpuimage;

/**
 * Created by xiaobu1 on 15-9-2.
 */
public class TextureRotationUtil {

    /**
     * 纹理无旋转
     */
    public static final float TEXTURE_NO_ROTATION[] = {
        0.0f, 1.0f,
        1.0f, 1.0f,
        0.0f, 0.0f,
        1.0f, 0.0f
    };

    /**
     * 旋转90度
     */
    public static final float TEXTURE_ROTATED_90[] = {
        1.0f, 1.0f,
        1.0f, 0.0f,
        0.0f, 1.0f,
        0.0f, 0.0f
    };

    /**
     * 旋转180度
     */
    public static final float TEXTURE_ROTATED_180[] = {
        1.0f, 0.0f,
        0.0f, 0.0f,
        1.0f, 1.0f,
        0.0f, 1.0f
    };

    /**
     * 旋转270度
     */
    public static final float TEXTURE_ROTATED_270[] = {
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 0.0f,
        1.0f, 1.0f
    };

    private TextureRotationUtil() {

    }

    public static float[] getRotation(final Rotation rotation, final boolean flipHorizontal, final boolean flipVertical) {
        float[] rotatedTex;

        switch (rotation) {
            case ROTATION_90:
                rotatedTex = TEXTURE_ROTATED_90;
                break;
            case ROTATION_180:
                rotatedTex = TEXTURE_ROTATED_180;
                break;
            case ROTATION_270:
                rotatedTex = TEXTURE_ROTATED_270;
                break;
            case NORMAL:
            default:
                rotatedTex = TEXTURE_NO_ROTATION;
                break;
        }

        if (flipHorizontal) {
            rotatedTex = new float[] {
                flip(rotatedTex[0]), rotatedTex[1],
                flip(rotatedTex[2]), rotatedTex[3],
                flip(rotatedTex[4]), rotatedTex[5],
                flip(rotatedTex[6]), rotatedTex[7]
            };
        }

        if (flipVertical) {
            rotatedTex = new float[] {
                rotatedTex[0], flip(rotatedTex[1]),
                rotatedTex[2], flip(rotatedTex[3]),
                rotatedTex[4], flip(rotatedTex[5]),
                rotatedTex[6], flip(rotatedTex[7])
            };
        }

        return rotatedTex;
    }

    private static float flip(final float i) {
        if (i == 0.0f) {
            return 1.0f;
        }
        return 0.0f;
    }
}
