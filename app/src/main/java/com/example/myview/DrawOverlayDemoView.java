package com.example.myview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.myview.utils.Utils;

public class DrawOverlayDemoView extends View {

    private float leftStartAngle = -225;
    private float leftSweepAngle = 225;

    private float rightStartAngle = -180;
    private float rightSweepAngle = 225;

    private float Process = 0;//纵向增长的进度值

    private Paint mPaint;


    /*心形路径*/
    private Path mPath;
    /*贝塞尔曲线路径*/
    private Path quadPath;

    public DrawOverlayDemoView(Context context) {
        this(context, null);
    }

    public DrawOverlayDemoView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawOverlayDemoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mPath = new Path();
        quadPath = new Path();
        mPaint.setColor(getResources().getColor(R.color.white));
        mPaint.setTextSize(Utils.dp2px(20));
        mPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        setHeartPath();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("---", "startAnimator");
                startAnimator();
            }
        }, 2000);
    }

    private String drawText = "风中的一匹狼";

    private Paint topPain = new Paint(Paint.ANTI_ALIAS_FLAG);

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.clipPath(mPath);

        mPaint.setColor(getResources().getColor(R.color.colorPrimary));
        canvas.drawPath(mPath, mPaint);

        canvas.save();
        setQuadToPath(true);
        mPaint.setColor(getResources().getColor(R.color.colorPrimary));
        canvas.clipPath(topPath);
        canvas.drawPath(topPath, mPaint);
        mPaint.setColor(getResources().getColor(R.color.white));
        canvas.drawText(drawText, getWidth() / 2f, getHeight() / 2f, mPaint);
        canvas.restore();

        canvas.save();
        setQuadToPath(false);
        mPaint.setColor(getResources().getColor(R.color.colorAccent));
        canvas.clipPath(quadPath);
        canvas.drawPath(quadPath, mPaint);
        mPaint.setColor(getResources().getColor(R.color.colorPrimary));
        canvas.drawText(drawText, getWidth() / 2f, getHeight() / 2f, mPaint);
        canvas.restore();

    }

    ObjectAnimator animatorSet;

    public float getProcess() {
        return Process;
    }

    public void setProcess(float Process) {
        this.Process = Process;
        invalidate();
    }


    // 动起来
    public void startAnimator() {

        if (animatorSet == null) {
            animatorSet = ObjectAnimator.ofFloat(this, "Process", 0, 1f);
            animatorSet.setDuration(5000);
            animatorSet.setRepeatCount(ValueAnimator.INFINITE);
            animatorSet.start();
        }
    }

    private Path topPath = new Path();

    /*
     * 画二阶贝塞尔曲线
     * isTopBottom 是截取文字上半部分还是下半部分
     * */
    private void setQuadToPath(boolean isTopPath) {

        Path basePath = new Path();
        topPath.reset();
        quadPath.reset();


        float width = getWidth();
        float hight = getHeight();
        float crest = Utils.dp2px(25);

        float cresetBaseline = hight - (hight + crest) * Process;
        float cresetTopY = cresetBaseline - crest;
        float cresetBottonY = cresetBaseline + crest;


        basePath.moveTo(-width + width * Process, cresetBaseline);

        basePath.quadTo(-width * 3f / 4 + width * Process, cresetTopY,
                -width / 2f + width * Process, cresetBaseline);

        basePath.quadTo(-width / 4f + width * Process, cresetBottonY,
                width * Process, cresetBaseline);

        basePath.quadTo(width / 4f + width * Process, cresetTopY,
                width / 2f + width * Process, cresetBaseline);

        basePath.quadTo(width * 3f / 4f + width * Process, cresetBottonY,
                width + width * Process, cresetBaseline);

        if (isTopPath) {
            basePath.lineTo(width, 0);
            basePath.lineTo(0, 0);
            basePath.lineTo(0, cresetBaseline);
            basePath.close();
            topPath = basePath;
        } else {
            basePath.lineTo(width / 2f, hight);
            basePath.lineTo(0, hight / 2f);
            basePath.close();
            quadPath = basePath;
        }
    }

    /*
     * 画心形
     * */
    private void setHeartPath() {
        float width = getWidth();
        float height = getHeight();

        RectF leftRectf = new RectF();
        leftRectf.left = 0;
        leftRectf.top = 0;
        leftRectf.right = width / 2f;
        leftRectf.bottom = height / 2f;
        mPath.addArc(leftRectf, leftStartAngle, leftSweepAngle);

        RectF rightRectf = new RectF();
        rightRectf.left = width / 2f;
        rightRectf.top = 0;
        rightRectf.right = width;
        rightRectf.bottom = height / 2f;
        mPath.arcTo(rightRectf, rightStartAngle, rightSweepAngle);

        mPath.lineTo(width / 2f, height);
    }
}
