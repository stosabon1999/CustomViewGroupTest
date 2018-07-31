package ru.production.ssobolevsky.customviewgroup;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

public class CustomGroup extends ViewGroup {

    private int screenHeight = 0;
    private int screenWidth = 0;


    public CustomGroup(Context context) {
        this(context, null, 0);
    }

    public CustomGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenHeight = size.y;
        screenWidth = size.x;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        int maxHeight = 0;
        int maxWidth = 0;
        int childState = 0;
        int height = 0;
        int rowCount = 0;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() != GONE) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                maxHeight = maxHeight + Math.max(maxHeight, child.getMeasuredHeight());
                height = height + child.getMeasuredHeight();
                if ((height / screenHeight) > rowCount) {
                    maxWidth = maxWidth + child.getMeasuredWidth();
                    rowCount++;
                } else {
                    maxWidth = Math.max(maxWidth, child.getMeasuredWidth());
                }
                childState = combineMeasuredStates(childState, child.getMeasuredState());
            }
        }

        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());

        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec, childState << MEASURED_HEIGHT_STATE_SHIFT));

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int count = getChildCount();
        int curWidth, curHeight, curLeft, curTop, maxHeight;

        final int childLeft = getPaddingLeft();
        final int childTop = getPaddingTop();
        final int childRight = getMeasuredWidth() - getPaddingRight();
        final int childBottom = getMeasuredHeight() - getPaddingBottom();
        final int childWidth = childRight - childLeft;
        final int childHeight = childBottom - childTop;

        curLeft = childLeft;
        curTop = childTop;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                child.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST));
                curWidth = child.getMeasuredWidth();
                curHeight = child.getMeasuredHeight();
                if (curTop + curHeight >= childBottom) {
                      curTop = 0;
                      curLeft += curWidth;
                }

                child.layout(curLeft, curTop, curLeft + curWidth, curTop + curHeight);
                curTop += curHeight;
            }

        }
    }
}
