package vipjokerstudio.com.telegramchart.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.renderscript.Matrix2f;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import vipjokerstudio.com.telegramchart.R;
import vipjokerstudio.com.telegramchart.util.MathUtil;
import vipjokerstudio.com.telegramchart.util.UiUtil;


public class CustomSwitch extends View implements ValueAnimator.AnimatorUpdateListener {
    private int mFrontColor;
    private int mBackColor;
    private int mBackPressedColor;
    boolean isOffset = false;//TODO remove it

    private Paint mFrontPaint;
    private Paint mBackPaint;
    private Paint mPaintStroke;
    private Paint checkboxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private TextPaint mTextPaint;
    private float animValue = 0;

    private Path checkBoxPath = new Path();
    private int radius;
    private int roundRadius;
    private int innerPadding;

    private boolean mIsChecked;
    private RectF mRect;
    private RectF mRectStroke;
    private Rect textBounds = new Rect();
    private RectF iconRect = new RectF();
    private CharSequence title;
    private float textWidth;
    private GestureDetector mGestureDetector;

    private OnCheckedChangeListener mListener;
    private int textSize = UiUtil.dpToPx(15);// 40;
    private int strokeWidth = 5;

    private int mainColor = Color.BLACK;

    public CustomSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomSwitch, 0, 0);
        try {
            mBackColor = a.getColor(R.styleable.CustomSwitch_back_color, Color.parseColor("#ffffff"));
            mFrontColor = a.getColor(R.styleable.CustomSwitch_thumb_color, Color.parseColor("#00ff00"));
            mBackPressedColor = a.getColor(R.styleable.CustomSwitch_back_color_pressed, Color.parseColor("#00ffff"));
            innerPadding = (int) a.getDimension(R.styleable.CustomSwitch_inner_padding, 5);
            roundRadius = (int) a.getDimension(R.styleable.CustomSwitch_round_radius, 25);
            title = a.getText(R.styleable.CustomSwitch_customTitle);


        } finally {
            a.recycle();
        }
        init();
    }

    public CustomSwitch(Context context) {
        super(context);
        init();
    }

    private void init() {
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setFakeBoldText(true);
        iconRect.set(0, 0, 40, 40);

        mFrontPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFrontPaint.setColor(mFrontColor);
        mFrontPaint.setStyle(Paint.Style.FILL);
        mFrontPaint.setAntiAlias(true);

        mBackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackPaint.setColor(mBackColor);
        mBackPaint.setStyle(Paint.Style.FILL);
        mBackPaint.setAntiAlias(true);


        mPaintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintStroke.setColor(Color.BLACK);
        mPaintStroke.setStrokeWidth(strokeWidth);
        mPaintStroke.setStyle(Paint.Style.STROKE);
        mPaintStroke.setAntiAlias(true);
        checkboxPaint.setColor(Color.WHITE);
        checkboxPaint.setStrokeWidth(6);
        checkboxPaint.setStrokeCap(Paint.Cap.ROUND);
        checkboxPaint.setStrokeJoin(Paint.Join.ROUND);
        checkboxPaint.setStyle(Paint.Style.STROKE);

        checkBoxPath.moveTo(3, 12);
        checkBoxPath.rLineTo(6, 6);
        checkBoxPath.rLineTo(12, -12);
        Matrix matrix = new Matrix();
        matrix.setRectToRect(new RectF(0, 0, 24, 24), iconRect, Matrix.ScaleToFit.CENTER);

        checkBoxPath.transform(matrix);

        // checkBox.setBounds(iconRect);

        mRect = new RectF(0, 0, UiUtil.dpToPx(30), UiUtil.dpToPx(15));
        mRectStroke = new RectF(0, 0, UiUtil.dpToPx(30), UiUtil.dpToPx(15));

        mGestureDetector = new GestureDetector(this.getContext(), new CustomGestureDetector());

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minWidth = (int) (getPaddingLeft() + getPaddingRight() + textWidth + iconRect.width());
//         int  minWidth =200;

        int width = resolveSizeAndState(minWidth, widthMeasureSpec, 1);

        int minHeight = getPaddingTop() + getPaddingBottom() + textSize;

        int height = resolveSizeAndState(minHeight, heightMeasureSpec, 0);

        setMeasuredDimension(width, height);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBackground(canvas);
        drawStroke(canvas);
//        drawCircle(canvas);
        drawCheckBox(canvas);
        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        if (title != null) {


            canvas.drawText(title.toString(), getWidth() / 2 - textBounds.exactCenterX() + (iconRect.width() / 2 * animValue), getHeight() / 2 - textBounds.exactCenterY(), mTextPaint);
        }
    }

    private void drawCheckBox(Canvas canvas) {
        if (!isOffset) {

            checkBoxPath.offset(getPaddingLeft() / 2, (getHeight() - iconRect.height()) / 2);
            isOffset = true;
        }


        checkboxPaint.setAlpha((int) (animValue * 255));
        canvas.drawPath(checkBoxPath, checkboxPaint);
    }


    private void drawBackground(Canvas canvas) {
        mRect.set(0,
                0,
                getWidth(),
                getHeight());


        canvas.drawRoundRect(mRect, mRect.height() / 2, mRect.height() / 2, mBackPaint);
    }

    private void drawStroke(Canvas _canvas) {

        mRectStroke.set(0,
                0,
                getWidth(),
                getHeight());

        float value = MathUtil.lerp(strokeWidth, getHeight() / 2f, animValue);
        mPaintStroke.setStrokeWidth(value);

//        if(animValue == 1){
//            mPaintStroke.setStyle(Paint.Style.FILL);
//        }else{
//            mPaintStroke.setStyle(Paint.Style.STROKE);
//        }

        mRectStroke.inset(value / 2f, value / 2f);


        _canvas.drawRoundRect(mRectStroke, mRectStroke.height() / 2, mRectStroke.height() / 2, mPaintStroke);
    }


    private float countXValue() {
        return getThumbPath() + radius + getPaddingLeft() + innerPadding;
    }

    private float getThumbPath() {
        return (mRect.width() - 2 * radius - innerPadding * 2) * animValue;
    }

    public boolean isChecked() {
        return mIsChecked;
    }

    public void setChecked(boolean isChecked) {
        this.mIsChecked = isChecked;
        if (this.mIsChecked) {
            animValue = 1;

            mTextPaint.setColor(Color.WHITE);


            mBackPaint.setColor(mBackPressedColor);
        } else {
            mTextPaint.setColor(mainColor);

            animValue = 0;
            mBackPaint.setColor(mBackColor);
        }


        invalidate();
    }

    private void check() {
        mIsChecked = true;
        final ValueAnimator animator = ValueAnimator.ofFloat(0, 1);

        animator.setDuration(200);
        animator.addUpdateListener(this);
//        animator.setInterpolator(new OvershootInterpolator());
        animator.start();
    }

    private void unCheck() {
        mIsChecked = false;
        final ValueAnimator animator = ValueAnimator.ofFloat(1, 0);

        animator.setDuration(200);
        animator.addUpdateListener(this);
//        animator.setInterpolator(new OvershootInterpolator());
        animator.start();

    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        animValue = (float) animation.getAnimatedValue();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }


    private void switchStates() {
        if (mIsChecked) {
            unCheck();

            mTextPaint.setColor(mainColor);
        } else {

            check();
            mTextPaint.setColor(Color.WHITE);

        }

        if (mListener != null) mListener.onStateChanged(this, mIsChecked);
    }


    public void setOnCheckedChangeListener(OnCheckedChangeListener _listener) {
        mListener = _listener;

    }

    public void setTitle(String title) {
        this.title = title;
        if (title != null) {


            textWidth = mTextPaint.measureText(title);
            mTextPaint.getTextBounds(title, 0, title.length(), textBounds);
        }
    }

    public void setCustomColor(int color) {
        mainColor = color;

        mPaintStroke.setColor(color);
        if (mIsChecked) {

            mTextPaint.setColor(Color.WHITE);
        } else {
            mTextPaint.setColor(mainColor);
        }

    }

    public static interface OnCheckedChangeListener {
        public void onStateChanged(View view, boolean isChecked);
    }

    private class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            switchStates();
            return true;
        }

        @Override
        public boolean onContextClick(MotionEvent e) {
            return super.onContextClick(e);
        }
    }


}


