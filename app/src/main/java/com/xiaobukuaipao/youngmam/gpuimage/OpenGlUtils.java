package com.xiaobukuaipao.youngmam.gpuimage;

import android.graphics.Bitmap;
import android.hardware.Camera.Size;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.IntBuffer;

/**
 * Created by xiaobu1 on 15-9-1.
 */
public class OpenGlUtils {

    public static final int NO_TEXTURE = -1;

    public static int loadTexture(final Bitmap img, final int usedTexId) {
        return loadTexture(img, usedTexId, true);
    }

    public static int loadTexture(final Bitmap img, final int usedTexId, final boolean recycle) {
        int textures[] = new int[1];
        if (usedTexId == NO_TEXTURE) {
            // 首先要通过OpenGL创建一个新的句柄, 这个句柄作为一个唯一的标识, 在任何想要提到OpenGL中相同的纹理时使用它
            // OpenGL方法可以被用来同时生成多重句柄, 这里只生成一个
            GLES20.glGenTextures(1, textures, 0);

            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);

            // 加载纹理时, 设置MAG时, 为线性采样
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            // 加载纹理时, 设置MIN时, 为线性采样
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);

            // 设置S轴的拉伸方式为截取
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            // 设置T轴的拉伸方式为截取
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            // Android有一个非常有用的工具, 直接加载位图到OpenGL
            // 一个普通的2D位图--所以将GL_TEXTURE_2D作为第一个参数传递
            // 第二个参数是MIP映射, 并需指定要在每个级别使用的图像, 在这里不使用MIP映射, 所以把默认级别0放入
            // 传递位图不使用边界时, 默认传递0
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, img, 0);
        } else {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, usedTexId);
            GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, img);
            textures[0] = usedTexId;
        }

        if (recycle) {
            img.recycle();
        }

        return textures[0];
    }

    public static int loadTexture(final IntBuffer data, final Size size, final int usedTexId) {
        int textures[] = new int[1];
        if (usedTexId == NO_TEXTURE) {
            GLES20.glGenTextures(1, textures, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, size.width, size.height,
                    0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, data);
        } else {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, usedTexId);
            GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, size.width,
                    size.height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, data);
            textures[0] = usedTexId;
        }
        return textures[0];
    }

    public static int loadShader(final String strSource, final int iType) {
        // 存放编译成功shader数量的数组
        int[] compiled = new int[1];

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER) or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int iShader = GLES20.glCreateShader(iType);
        // add the source code to the shader and compile it
        GLES20.glShaderSource(iShader, strSource);
        GLES20.glCompileShader(iShader);

        // 获取Shader的编译情况
        GLES20.glGetShaderiv(iShader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.d("Load Shader Failed", "Compilation\n" + GLES20.glGetShaderInfoLog(iShader));
            return 0;
        }

        return iShader;
    }

    public static int loadProgram(final String strVSource, final String strFSource) {
        int iVShader;
        int iFShader;
        int iProgId;

        // 存放链接成功shader数量的数组
        int[] link = new int[1];

        iVShader = loadShader(strVSource, GLES20.GL_VERTEX_SHADER);
        if (iVShader == 0) {
            Log.d("Load Program", "Vertex Shader Failed");
            return 0;
        }

        iFShader = loadShader(strFSource, GLES20.GL_FRAGMENT_SHADER);
        if (iFShader == 0) {
            Log.d("Load Program", "Fragment Shader Failed");
            return 0;
        }

        // create empty OpenGL ES Program
        iProgId = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(iProgId, iVShader);
        // add the fragment shader to program
        GLES20.glAttachShader(iProgId, iFShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(iProgId);

        GLES20.glGetProgramiv(iProgId, GLES20.GL_LINK_STATUS, link, 0);
        if (link[0] <= 0) {
            Log.d("Load Program", "Linking Failed");
            return 0;
        }

        // 此时可以删除Shader
        GLES20.glDeleteShader(iVShader);
        GLES20.glDeleteShader(iFShader);

        return iProgId;
    }
}
