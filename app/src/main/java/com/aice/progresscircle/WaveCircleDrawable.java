package com.aice.progresscircle;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
public class WaveCircleDrawable extends Drawable {
    private Paint mPaint;
    private ColorFilter colorFilter;
    private int alpha = 255;
    private RectF tempRectF = new RectF();
    private float circleProgress;
    private Path pathCirlce;
//////////
    private ValueAnimator mAnimator;
private int  width;
    private int height ;
private int waveHeight ;// 波浪的最高度
    private int waveWidth  ;//波长
    private float offset ;//偏移量
    private int baseLine ;// 基线，用于控制水位上涨的，这里是写死了没动，你可以不断的设置改变。
    public void setWave(boolean wave) {
        isWave = wave;
    }

    private boolean isWave;
    public void setCircleProgress(float circleProgress) {
        this.circleProgress = circleProgress;
    }

    public WaveCircleDrawable() {
        mPaint=new Paint();
        pathCirlce=new Path();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        resetPaint();
        mPaint.setColor(Color.RED);
        int w = canvas.getWidth();
        int h = canvas.getHeight();
        width=w;
        height=h;
        waveWidth = w;
        waveHeight=h/4;
        baseLine = h;
        final int centerX = w / 2;
        final int centerY = h / 2;
        tempRectF.set(centerX - centerX/2,
                centerY - centerX/2,
                centerX + centerX/2,
                centerY + centerX/2);
        mPaint.setStrokeCap(Paint.Cap.BUTT);
        pathCirlce.addCircle(centerX,centerY,centerX,Path.Direction.CW);
        canvas.drawPath(pathCirlce,mPaint);
        mPaint.setColor(Color.BLUE);
        if (isWave){
            canvas.save();
            canvas.clipPath(pathCirlce);
            canvas.drawPath(getPath(),mPaint);
            canvas.restore();
            updateXControl();
        }else {
            canvas.save();
            canvas.clipPath(pathCirlce);
            Log.v("circleProgress=", circleProgress * h + "=" + h);
            canvas.drawRect(0, circleProgress * h, w, h, mPaint);
            canvas.restore();
        }

    }
    private void updateXControl(){
        if (mAnimator==null){
        //设置一个波长的偏移
         mAnimator = ValueAnimator.ofFloat(0,waveWidth);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatorValue = (float)animation.getAnimatedValue() ;
                offset = animatorValue;//不断的设置偏移量，并重画
                invalidateSelf();
            }
        });
        mAnimator.setDuration(1000);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.start();
        }
    }

    private Path  getPath(){
        int itemWidth = waveWidth/2;//半个波长
        Path mPath = new Path();
        mPath.moveTo(-itemWidth * 3, baseLine*circleProgress);//起始坐标
        //核心的代码就是这里
        for (int i = -3; i < 2; i++) {
            int startX = i * itemWidth;
            mPath.quadTo(
                    startX + itemWidth/2 + offset,//控制点的X,（起始点X + itemWidth/2 + offset)
                    getWaveHeigh( i ),//控制点的Y
                    startX + itemWidth + offset,//结束点的X
                    baseLine*circleProgress//结束点的Y
            );//只需要处理完半个波长，剩下的有for循环自已就添加了。
        }
        //下面这三句话很重要，它是形成了一封闭区间，让曲线以下的面积填充一种颜色，大家可以把这3句话注释了看看效果。
        mPath.lineTo(width,height);
        mPath.lineTo(0,height);
        mPath.close();
        return  mPath;
    }
    private int getWaveHeigh(int num){
        if(num % 2 == 0){
            return (int) (baseLine*circleProgress) + waveHeight;
        }
        return (int)(baseLine*circleProgress) - waveHeight;
    }
    private void resetPaint() {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setAlpha(alpha);
        mPaint.setColorFilter(colorFilter);
    }
    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        this.colorFilter=colorFilter;
    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }
}