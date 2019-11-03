package com.rainbean.bigimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;

public class BigImageView extends View implements GestureDetector.OnGestureListener, View.OnTouchListener {

    private final Rect mRect;
    private final BitmapFactory.Options mOptions;
    private final GestureDetector mGestureDetector;
    private final Scroller mScroller;
    private int mImageWidth;
    private int mImageHeight;
    private BitmapRegionDecoder mDecoder;
    private int mViewWidth;
    private int mViewHeight;
    private float mScale;

    private Bitmap mBitmap;

    private Matrix matrix;

    public BigImageView(Context context) {
        this(context,null);
    }

    public BigImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);

    }

    public BigImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        第一步，设置View需要的成员变量
        mRect = new Rect();
//        内存复用
        mOptions = new BitmapFactory.Options();
//        手势识别
        mGestureDetector = new GestureDetector(context,this);
//        滚动类
        mScroller = new Scroller(context);

        setOnTouchListener(this);
    }

//    设置图片，得到图片信息
    public void setImage(InputStream is) {
//        获取图片的宽和高，并不加载图片到内存中
        mOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeStream(is, null, mOptions);

        mImageWidth = mOptions.outWidth;
        mImageHeight = mOptions.outHeight;

//        开启复用
        mOptions.inMutable = true;

        mOptions.inPreferredConfig = Bitmap.Config.RGB_565;

        mOptions.inJustDecodeBounds = false;

//        区域解码器
        try {
            mDecoder = BitmapRegionDecoder.newInstance(is, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        requestLayout();
    }

//    开始测量
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        mViewWidth = getMeasuredWidth();
        mViewHeight = getMeasuredHeight();

        mRect.top = 0;
        mRect.left = 0;
        mRect.right = mImageWidth;

        mScale = mViewWidth/mImageWidth;
        mRect.bottom = (int) (mViewHeight/mScale);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDecoder==null){
            return;
        }
//        内存复用
        mOptions.inBitmap = mBitmap;
//        指定解码区域
        mBitmap = mDecoder.decodeRegion(mRect,mOptions);
//        得到一个矩阵进行缩放
        matrix = new Matrix();
        matrix.setScale(mScale,mScale);
        canvas.drawBitmap(mBitmap,matrix,null);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        直接将事件交给手势事件
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
//        如果移动没有停止，强行停止
        if (!mScroller.isFinished()){
            mScroller.forceFinished(true);
        }
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//        上下移动时，Rect改变显示区域
        mRect.offset(0, (int) distanceY);
//        移动的时候要处理达到顶部和底部的事件
        if (mRect.bottom>=mImageHeight){
            mRect.bottom = mImageHeight;
            mRect.top = mImageHeight-(int)(mViewHeight/mScale);
        }
        if (mRect.top<0){
            mRect.top = 0;
            mRect.bottom = (int) (mViewHeight/mScale);
        }
        invalidate();

        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        mScroller.fling(0,mRect.top,0,-(int)velocityY,
                0,0,0, (int) (mImageHeight-mViewHeight/mScale));

        return false;
    }

    @Override
    public void computeScroll() {
        if (mScroller.isFinished()){
            return;
        }


        if (mScroller.computeScrollOffset()){
            mRect.top = mScroller.getCurrY();
            mRect.bottom = mRect.top+(int)(mViewHeight/mScale);
            invalidate();
        }


    }
}
