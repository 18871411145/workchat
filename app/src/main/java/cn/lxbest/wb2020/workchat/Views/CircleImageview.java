package cn.lxbest.wb2020.workchat.Views;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class CircleImageview extends AppCompatImageView {


    //画笔
    private Paint mPaint;
    //圆形图片的半径
    private int mRadius;
    //图片的宿放比例
    private float mScale;


    public CircleImageview(Context context) {
        super(context);
    }

    public CircleImageview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleImageview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //由于是圆形，宽高应保持一致
        int size = Math.min(getMeasuredWidth(), getMeasuredHeight());
        mRadius = size / 2;
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint = new Paint();
        Drawable drawable = getDrawable();

        if(null!=drawable){

            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mScale = (mRadius * 2.0f) / Math.min(bitmap.getHeight(), bitmap.getWidth());

            Matrix matrix = new Matrix();
            matrix.setScale(mScale, mScale);
            bitmapShader.setLocalMatrix(matrix);
            mPaint.setShader(bitmapShader);

            canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
        }else super.onDraw(canvas);

    }
}