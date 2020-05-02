package com.example.startproject2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


public class PaintBoard extends View {

    Canvas mCanvas;
    Bitmap mBitmap;
    Paint mPaint;
    float lastX, lastY;
    Path mPath = new Path();

    static final float TOUCH_TOLERANCE = 8;

    public PaintBoard(Context context) {
        super(context, null);
        Log.d("paintBoard","생성자 호출");
        init();
    }

    public PaintBoard(Context context, AttributeSet attr) {
        super(context, attr);
        init();
    }

    private void init(){
        Log.d("paintBoard","init 호출");
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(3.0F);
        this.lastX = -1;
        this.lastY = -1;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if(mBitmap != null){
            Log.d("onSizeChanged", "not null");
            return;
        }
        Bitmap img = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();
        canvas.setBitmap(img);
        canvas.drawColor(Color.WHITE);
        mBitmap = img;
        mCanvas = canvas;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mBitmap != null)
            canvas.drawBitmap(mBitmap,0,0,null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                mPath.rewind();
            case MotionEvent.ACTION_MOVE:
                processMove(event);
                break;
            case MotionEvent.ACTION_DOWN:
                touchDown(event);
                break;
        }
        invalidate();
        return true;
    }

    private void touchDown(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        lastX = x;
        lastY = y;
        mPath.moveTo(x, y);
        mCanvas.drawPath(mPath, mPaint);
    }

    private void processMove(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float dx = Math.abs(x - lastX);
        float dy = Math.abs(y - lastY);
        if (dx >= TOUCH_TOLERANCE || dy >=
                TOUCH_TOLERANCE) {
            float cX = (x + lastX) / 2;
            float cY = (y + lastY) / 2;
            mPath.quadTo(lastX, lastY, cX, cY);
            lastX = x;
            lastY = y;
            mCanvas.drawPath(mPath, mPaint);
        }
    }

    public void eraseCanvas(){
        mBitmap.eraseColor(Color.WHITE);
        invalidate();
    }

    public void changeBitmap(Bitmap bitmap) {
        if(bitmap == null){
            Log.d("file2","change >> null");
        }else{
            Log.d("file2","change >> " + bitmap.toString());
        }
        Bitmap copyBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        //Bitmap copyBitmap = bitmap.createBitmap(bitmap);
        Canvas canvas = new Canvas();
        canvas.setBitmap(copyBitmap);
        //canvas.drawColor(Color.WHITE);
        mBitmap = copyBitmap;
        mCanvas = canvas;
        invalidate();
    }
}
