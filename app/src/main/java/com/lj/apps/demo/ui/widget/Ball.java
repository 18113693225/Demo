package com.lj.apps.demo.ui.widget;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;

import com.lj.apps.demo.R;
import com.lj.apps.demo.Utils.TimGL2Utils;


/**
 * Created by Administrator on 2016/10/21.
 */
public class Ball implements Renderer {
    Context mContext;
    private int mProgram;
    private int mAPositionHandler;
    private int mUProjectMatrixHandler;
    private int mATextureCoordHandler;
    private final float[] projectMatrix = new float[16];
    private int mSize;
    private FloatBuffer vertexBuff;

    private FloatBuffer textureBuff;
    private int textrueID;

    public Ball(Context context) {
        mContext = context;
        init();
    }

    public void init() {
        int perVertex = 36;

        double perRadius = 2 * Math.PI / (float) perVertex;
        double perW = 1 / (float) perVertex;
        double perH = 1 / (float) (perVertex);

        ArrayList<Float> vetList = new ArrayList<Float>();
        ArrayList<Float> textureList = new ArrayList<Float>();
        for (int a = 0; a < perVertex; a++) {
            for (int b = 0; b < perVertex; b++) {
                float w1 = (float) (a * perH);
                float h1 = (float) (b * perW);

                float w2 = (float) ((a + 1) * perH);
                float h2 = (float) (b * perW);

                float w3 = (float) ((a + 1) * perH);
                float h3 = (float) ((b + 1) * perW);

                float w4 = (float) (a * perH);
                float h4 = (float) ((b + 1) * perW);

                textureList.add(h1);
                textureList.add(w1);

                textureList.add(h2);
                textureList.add(w2);

                textureList.add(h3);
                textureList.add(w3);

                textureList.add(h3);
                textureList.add(w3);

                textureList.add(h4);
                textureList.add(w4);

                textureList.add(h1);
                textureList.add(w1);

                float x1 = (float) (Math.sin(a * perRadius / 2) * Math.cos(b
                        * perRadius));
                float z1 = (float) (Math.sin(a * perRadius / 2) * Math.sin(b
                        * perRadius));
                float y1 = (float) Math.cos(a * perRadius / 2);

                float x2 = (float) (Math.sin((a + 1) * perRadius / 2) * Math
                        .cos(b * perRadius));
                float z2 = (float) (Math.sin((a + 1) * perRadius / 2) * Math
                        .sin(b * perRadius));
                float y2 = (float) Math.cos((a + 1) * perRadius / 2);

                float x3 = (float) (Math.sin((a + 1) * perRadius / 2) * Math
                        .cos((b + 1) * perRadius));
                float z3 = (float) (Math.sin((a + 1) * perRadius / 2) * Math
                        .sin((b + 1) * perRadius));
                float y3 = (float) Math.cos((a + 1) * perRadius / 2);

                float x4 = (float) (Math.sin(a * perRadius / 2) * Math
                        .cos((b + 1) * perRadius));
                float z4 = (float) (Math.sin(a * perRadius / 2) * Math
                        .sin((b + 1) * perRadius));
                float y4 = (float) Math.cos(a * perRadius / 2);

                vetList.add(x1);
                vetList.add(y1);
                vetList.add(z1);

                vetList.add(x2);
                vetList.add(y2);
                vetList.add(z2);

                vetList.add(x3);
                vetList.add(y3);
                vetList.add(z3);

                vetList.add(x3);
                vetList.add(y3);
                vetList.add(z3);

                vetList.add(x4);
                vetList.add(y4);
                vetList.add(z4);

                vetList.add(x1);
                vetList.add(y1);
                vetList.add(z1);
            }
        }
        mSize = vetList.size() / 3;
        float texture[] = new float[mSize * 2];
        for (int i = 0; i < texture.length; i++) {
            texture[i] = textureList.get(i);
        }
        textureBuff = ByteBuffer.allocateDirect(texture.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureBuff.put(texture);
        textureBuff.position(0);

        float vet[] = new float[mSize * 3];
        for (int i = 0; i < vet.length; i++) {
            vet[i] = vetList.get(i);
        }
        vertexBuff = ByteBuffer.allocateDirect(vet.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuff.put(vet);
        vertexBuff.position(0);

    }

    @Override
    public void onDrawFrame(GL10 arg0) {

        rotateM(mCurrMatrix, 0, -xAngle, 1, 0, 0);
        rotateM(mCurrMatrix, 0, -yAngle, 0, 1, 0);
        rotateM(mCurrMatrix, 0, -zAngle, 0, 0, 1);

        glClearColor(1, 1, 1, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glActiveTexture(GLES20.GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textrueID);
        glUniformMatrix4fv(mUProjectMatrixHandler, 1, false,
                getFinalMVPMatrix(), 0);
        glDrawArrays(GL_TRIANGLES, 0, mSize);
    }

    public float xAngle;
    public float yAngle;
    public float zAngle;

    final float mCurrMatrix[] = new float[16];

    final float mMVPMatrix[] = new float[16];

    public float[] getFinalMVPMatrix() {
        Matrix.multiplyMM(mMVPMatrix, 0, projectMatrix, 0, mCurrMatrix, 0);
        Matrix.setIdentityM(mCurrMatrix, 0);
        return mMVPMatrix;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        float ratio = width / (float) height;
        frustumM(projectMatrix, 0, -ratio, ratio, -1, 1, 1, 20);

        Matrix.setIdentityM(mCurrMatrix, 0);
        Matrix.setIdentityM(mMVPMatrix, 0);

        translateM(projectMatrix, 0, 0, 0, -2);
        // rotateM(projectMatrix, 0, -90, 1, 0, 0);
        scaleM(projectMatrix, 0, 4, 4, 4);

        mProgram = TimGL2Utils.getProgram(mContext);
        glUseProgram(mProgram);

        mAPositionHandler = glGetAttribLocation(mProgram, "aPosition");
        mUProjectMatrixHandler = glGetUniformLocation(mProgram,
                "uProjectMatrix");
        mATextureCoordHandler = glGetAttribLocation(mProgram, "aTextureCoord");

        textrueID = TimGL2Utils.initTexture(mContext, R.drawable.p1);

        glVertexAttribPointer(mAPositionHandler, 3, GL_FLOAT, false, 0,
                vertexBuff);
        glVertexAttribPointer(mATextureCoordHandler, 2, GL_FLOAT, false, 0,
                textureBuff);

        glEnableVertexAttribArray(mAPositionHandler);
        glEnableVertexAttribArray(mATextureCoordHandler);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }
}
