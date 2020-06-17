package cn.lxbest.wb2020.workchat.Views;

import android.content.Context;
import android.util.AttributeSet;

public class XYImageView extends androidx.appcompat.widget.AppCompatImageView {
    public XYImageView(Context context) {
        super(context);
    }

    public XYImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XYImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
