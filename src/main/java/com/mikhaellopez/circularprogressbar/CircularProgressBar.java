package com.mikhaellopez.circularprogressbar;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Mikhael LOPEZ on 16/10/2015.
 */

public class CircularProgressBar extends View {

    float highStroke;

    // Properties
    private float backgroundStrokeWidth = getResources().getDimension(R.dimen.default_background_stroke_width);
    private int backgroundColor = Color.GRAY;
    private float shadowStrokeWidth = 0;

    // Object used to draw
    private RectF rectF;
    private Paint backgroundPaint;
    private BlurMaskFilter shadowFilter;

    // wheel
    private float wheelPosition = 0;
    private ObjectAnimator wheelAnimator;

    // progresses
    ArrayList<Progress> progresses;

    //region Constructor & Init Method
    public CircularProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        rectF = new RectF();
        progresses = new ArrayList<>();

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircularProgressBar, 0, 0);
        //Reading values from the XML layout
        try {
            backgroundStrokeWidth = typedArray.getDimension(R.styleable.CircularProgressBar_cpb_background_progressbar_width, backgroundStrokeWidth);
            backgroundColor = typedArray.getInt(R.styleable.CircularProgressBar_cpb_background_progressbar_color, backgroundColor);
            shadowStrokeWidth = typedArray.getDimension(R.styleable.CircularProgressBar_cpb_shadow_width, shadowStrokeWidth);
        } finally {
            typedArray.recycle();
        }

        // Init Background
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(backgroundStrokeWidth);

        setLayerType(LAYER_TYPE_SOFTWARE, null);

        shadowFilter = new BlurMaskFilter(shadowStrokeWidth * 3.5f, BlurMaskFilter.Blur.INNER);

        wheelAnimator = ObjectAnimator.ofFloat(this, "wheelPosition", 0.0f, 360.0f);
        wheelAnimator.setRepeatMode(ObjectAnimator.RESTART);
        wheelAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        wheelAnimator.setInterpolator(new LinearInterpolator());
        wheelAnimator.setDuration(1500);
    }
    //endregion

    //region Draw Method
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();

        canvas.translate(0, shadowStrokeWidth / 2);
        backgroundPaint.setMaskFilter(shadowFilter);
        backgroundPaint.setStrokeWidth(shadowStrokeWidth);
        canvas.drawCircle(rectF.centerX(), rectF.centerY(), (rectF.width() - backgroundStrokeWidth - shadowStrokeWidth / 2) / 2, backgroundPaint);

        canvas.restore();

        backgroundPaint.setMaskFilter(null);
        backgroundPaint.setStrokeWidth(backgroundStrokeWidth);

        canvas.drawOval(rectF, backgroundPaint);
        for (Progress progress : progresses) {
            float angle = ((float) 360 * progress.progress) / 100.0f;
            canvas.drawArc(rectF, progress.startAngle + wheelPosition, angle, false, progress.foregroundPaint);
        }
    }
    //endregion

    //region Mesure Method
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int min = Math.min(width, height);
        setMeasuredDimension(min, min);
        highStroke = backgroundStrokeWidth;
        for (Progress progress : progresses)
            if (progress.strokeWidth > highStroke)
                highStroke = progress.strokeWidth;
        rectF.set(0 + highStroke / 2, 0 + highStroke / 2, min - highStroke / 2, min - highStroke / 2);
    }
    //endregion

    //region Method Get/Set

    public float getWheelPosition() {
        return wheelPosition;
    }

    public void setWheelPosition(float wheelPosition) {
        this.wheelPosition = wheelPosition;
        invalidate();
    }

    public float getBackgroundProgressBarWidth() {
        return backgroundStrokeWidth;
    }

    public void setBackgroundProgressBarWidth(float backgroundStrokeWidth) {
        this.backgroundStrokeWidth = backgroundStrokeWidth;
        backgroundPaint.setStrokeWidth(backgroundStrokeWidth);
        requestLayout();//Because it should recalculate its bounds
        invalidate();
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        backgroundPaint.setColor(backgroundColor);
        invalidate();
        requestLayout();
    }
    //endregion

    public void startWheelMode() {
        wheelAnimator.start();
    }

    public void stopWheelMode() {
        wheelAnimator.cancel();
    }

    public void addProgress(Progress progress) {
        progress.owner = this;
        progresses.add(progress);
        invalidate();
        requestLayout();
    }

    public void removeProgress(Progress progress) {
        progress.owner = null;
        progresses.remove(progress);
        invalidate();
        requestLayout();
    }

    public void removeProgress(int index) {
        Progress progress = progresses.remove(index);
        progress.owner = null;
        invalidate();
        requestLayout();
    }

    public void clearProgress() {
        while (progresses.size() > 0) {
            removeProgress(0);
        }
    }

    public static class Progress {

        private float startAngle;
        private float progress;
        private float strokeWidth;
        private int color;

        private Paint foregroundPaint;

        private CircularProgressBar owner;

        public Progress(float startAngle, float progress, float progressWidth, int color) {

            foregroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            foregroundPaint.setStyle(Paint.Style.STROKE);
            foregroundPaint.setStrokeCap(Paint.Cap.ROUND);
            this.startAngle = startAngle;
            this.progress = progress;
            this.strokeWidth = progressWidth;
            this.color = color;

            foregroundPaint.setStrokeWidth(strokeWidth);
            foregroundPaint.setColor(color);
        }

        public float getProgress() {
            return progress;
        }

        public void setProgress(float progress) {
            this.progress = progress;
            owner.invalidate();
        }

        public float getProgressBarWidth() {
            return strokeWidth;
        }

        public void setProgressBarWidth(float width) {
            strokeWidth = width;
            foregroundPaint.setStrokeWidth(strokeWidth);
            owner.requestLayout();//Because it should recalculate its bounds
            owner.invalidate();
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
            foregroundPaint.setColor(color);
            owner.invalidate();
            owner.requestLayout();
        }

        public float getStartAngle() {
            return startAngle;
        }

        public void setStartAngle(float startAngle) {
            this.startAngle = startAngle;
            owner.invalidate();
        }

        public void setProgressWithAnimation(float progress, int duration) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "progress", progress);
            objectAnimator.setDuration(duration);
            objectAnimator.setInterpolator(new DecelerateInterpolator());
            objectAnimator.start();
        }

    }
}
