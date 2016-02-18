package com.xiaobukuaipao.youngmam.gpuimage;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.PointF;
import android.opengl.GLES20;

import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.LinkedList;

/**
 * Created by xiaobu1 on 15-8-31.
 * Image过滤器
 */
public class GPUImageFilter {

    // attribute--输入--为每一个顶点输入一个不同的值, 如position
    // uniform--输入--对于所有顶点都是相同的
    // varying--输出--将会被提供给片段着色器(Fragment Shader)使用

    // attribute是一个输入, 提供关于这个特定顶点的信息
    // attribute类型的变量只有Vertex Shader才有, attribute前辍修饰的变量定义的是每个Vertex的属性变量, 包括位置(position), 颜色, 法线和纹理坐标
    // attribute 类型的变量在Vertex Shader中是只读的, 只能由外部主机程序传入值
    // vec2--两个--浮点型, 整型, 布尔向量
    // vec3--三个--浮点型, 整型, 布尔向量
    // vec4--四个--浮点型, 整型, 布尔向量

    // varying变量提供了顶点着色器, 片元着色器和二者通讯控制模块之间的接口
    // 顶点着色器计算每个顶点的值(如颜色, 纹理坐标等)并将它们写到varying变量中

    // 片元着色器会读取varying变量的值, 并且被读取的值将会作为插值器, 作为图元中片元位置的一个功能信息
    // varying变量对于片元着色器来说是只读的

    // gl_Position--内置变量--输出属性--变换后的顶点的位置, 用于后面的固定的裁剪等操作, 所有的顶点着色器都必须写这个值
    public static final String NO_FILTER_VERTEX_SHADER = "" +
            "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            "\n" +
            "varying vec2 textureCoordinate;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "  gl_Position = position;\n" +
            "  textureCoordinate = inputTextureCoordinate.xy;\n" +
            "}";

    // 精度限定(Precision Qualifiers) -- lowp mediump highp
    // uniform前辍--修饰的变量初始值由外部程序赋值, 在Shader中是只读的, 只能由外部主机程序传入值
    // uniform变量在Vertex Shader和Fragment Shader之间共享, 当使用glUniform***设置了一个uniform变量的值之后, Vertex Shader和Fragment Shader中具有相同的值
    // uniform变量被存储在GPU中的“常量存储区”, 其空间大小是固定的

    // sampler2D--二维纹理句柄
    // gl_FragColor--vec4--输出的颜色用于随后的像素操作
    // texture2D--返回类型的精度为lowp
    // texture2D--第一个参数是采样器(可以是sampler1D sampler2D sampler3D samplerCube sampler1DShadow sampler2DShadow)
    // OpenGL是支持多重纹理的
    // 一般咱们在使用纹理的时候, 默认启动的是GL_TEXTURE0, 也就是glActiveTexture(GL_TEXTURE0)
    // texture2D对纹理进行采样(得到一个纹素texel), 这是一个纹理图片中的像素
    public static final String NO_FILTER_FRAGMENT_SHADER = "" +
            "varying highp vec2 textureCoordinate;\n" +
            "\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "  gl_FragColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "}";

    // 绘制线程队列
    private final LinkedList<Runnable> mRunOnDraw;
    // shader--着色器
    private final String mVertexShader;
    private final String mFragmentShader;

    protected int mGLProgId;
    protected int mGLAttribPosition;
    protected int mGLUniformTexture;
    protected int mGLAttribTextureCoordinate;

    protected int mOutputWidth;
    protected int mOutputHeight;

    private boolean mIsInitialized;

    public GPUImageFilter() {
        this(NO_FILTER_VERTEX_SHADER, NO_FILTER_FRAGMENT_SHADER);
    }

    public GPUImageFilter(final String vertexShader, final String fragmentShader) {
        mRunOnDraw = new LinkedList<Runnable>();
        mVertexShader = vertexShader;
        mFragmentShader = fragmentShader;
    }

    public final void init() {
        onInit();
        mIsInitialized = true;
        onInitialized();
    }

    // 初始化
    public void onInit() {
        mGLProgId = OpenGlUtils.loadProgram(mVertexShader, mFragmentShader);
        // get handle(句柄) to vertex shader's position member
        // glGetAttribLocation方法--获取着色器程序中, 指定为attribute类型变量的句柄id
        mGLAttribPosition = GLES20.glGetAttribLocation(mGLProgId, "position");
        // glGetUniformLocation方法--获取着色器程序中, 指定为uniform类型变量的句柄id
        mGLUniformTexture = GLES20.glGetUniformLocation(mGLProgId, "inputImageTexture");

        mGLAttribTextureCoordinate = GLES20.glGetAttribLocation(mGLProgId, "inputTextureCoordinate");

        mIsInitialized = true;
    }

    public void onInitialized() {

    }

    public final void destroy() {
        mIsInitialized = false;
        GLES20.glDeleteProgram(mGLProgId);
        onDestroy();
    }

    public void onDestroy() {

    }

    public void onOutputSizeChanged(final int width, final int height) {
        mOutputWidth = width;
        mOutputHeight = height;
    }

    /**
     * 具体的绘制流程
     */
    public void onDraw(final int textureId, final FloatBuffer cubeBuffer, final FloatBuffer textureBuffer) {
        GLES20.glUseProgram(mGLProgId);

        runPendingOnDrawTasks();

        if (!mIsInitialized) {
            return;
        }

        cubeBuffer.position(0);
        GLES20.glVertexAttribPointer(mGLAttribPosition, 2, GLES20.GL_FLOAT, false, 0, cubeBuffer);
        GLES20.glEnableVertexAttribArray(mGLAttribPosition);

        textureBuffer.position(0);
        GLES20.glVertexAttribPointer(mGLAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(mGLAttribTextureCoordinate);

        if (textureId != OpenGlUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glUniform1i(mGLUniformTexture, 0);
        }

        onDrawArraysPre();

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(mGLAttribPosition);
        GLES20.glDisableVertexAttribArray(mGLAttribTextureCoordinate);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    protected void onDrawArraysPre() {

    }

    protected void runPendingOnDrawTasks() {
        while (!mRunOnDraw.isEmpty()) {
            mRunOnDraw.removeFirst().run();
        }
    }

    public boolean isInitialized() {
        return mIsInitialized;
    }

    public int getOutputWidth() {
        return mOutputWidth;
    }

    public int getOutputHeight() {
        return mOutputHeight;
    }

    public int getProgram() {
        return mGLProgId;
    }

    public int getAttribPosition() {
        return mGLAttribPosition;
    }

    public int getAttribTextureCoordinate() {
        return mGLAttribTextureCoordinate;
    }

    public int getUniformTexture() {
        return mGLUniformTexture;
    }

    protected void setInteger(final int location, final int intValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform1i(location, intValue);
            }
        });
    }

    protected void setFloat(final int location, final float floatValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform1f(location, floatValue);
            }
        });
    }

    protected void setFloatVec2(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform2fv(location, 1, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void setFloatVec3(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform3fv(location, 1, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void setFloatVec4(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform4fv(location, 1, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void setFloatArray(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform1fv(location, arrayValue.length, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void setPoint(final int location, final PointF point) {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                float[] vec2 = new float[2];
                vec2[0] = point.x;
                vec2[1] = point.y;
                GLES20.glUniform2fv(location, 1, vec2, 0);
            }
        });
    }

    protected void setUniformMatrix3f(final int location, final float[] matrix) {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                GLES20.glUniformMatrix3fv(location, 1, false, matrix, 0);
            }
        });
    }

    protected void setUniformMatrix4f(final int location, final float[] matrix) {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                GLES20.glUniformMatrix4fv(location, 1, false, matrix, 0);
            }
        });
    }

    protected void runOnDraw(final Runnable runnable) {
        synchronized (mRunOnDraw) {
            mRunOnDraw.addLast(runnable);
        }
    }

    public static String loadShader(String file, Context context) {
        try {
            AssetManager assetManager = context.getAssets();
            InputStream ims = assetManager.open(file);

            String re = convertStreamToString(ims);
            ims.close();
            return re;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

}
