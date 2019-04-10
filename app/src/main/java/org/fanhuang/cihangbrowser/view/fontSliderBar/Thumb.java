package org.fanhuang.cihangbrowser.view.fontSliderBar;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Lan on 2017-4-5.
 */

public class Thumb {
    private static final float MINIMUM_TARGET_RADIUS = 50;

    private final float mTouchZone;
    private boolean mIsPressed;

    private final float mY;
    private float mX;

    private Paint mPaintNormal;
    private Paint mPaintPressed;

    private float mRadius;
    private int mColorNormal;
    private int mColorPressed;

    public Thumb(float x, float y, int colorNormal, int colorPressed, float radius) {

        mRadius = radius;
        mColorNormal = colorNormal;
        mColorPressed = colorPressed;

        mPaintNormal = new Paint();
        mPaintNormal.setColor(mColorNormal);
        mPaintNormal.setAntiAlias(true);

        mPaintPressed = new Paint();
        mPaintPressed.setColor(mColorPressed);
        mPaintPressed.setAntiAlias(true);

        mTouchZone = (int) Math.max(MINIMUM_TARGET_RADIUS, radius);

        mX = x;
        mY = y;
    }

    public void setX(float x) {
        mX = x;
    }

    public float getX() {
        return mX;
    }

    public boolean isPressed() {
        return mIsPressed;
    }

    public void press() {
        mIsPressed = true;
    }

    public void release() {
        mIsPressed = false;
    }

    public boolean isInTargetZone(float x, float y) {
        if (Math.abs(x - mX) <= mTouchZone && Math.abs(y - mY) <= mTouchZone) {
            return true;
        }
        return false;
    }

    public void draw(Canvas canvas) {
        if (mIsPressed) {
            canvas.drawCircle(mX, mY, mRadius, mPaintPressed);
        } else {
            canvas.drawCircle(mX, mY, mRadius, mPaintNormal);
        }
    }

    public void destroyResources() {
        if(null != mPaintNormal) {
            mPaintNormal = null;
        }
        if(null != mPaintPressed) {
            mPaintPressed = null;
        }
    }
}
