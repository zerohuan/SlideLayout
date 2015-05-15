package com.luckymore.yjh.slide.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.luckymore.yjh.slide.R;


/**
 * Created by yjh on 15-5-12.
 */
public class SlideLayout extends FrameLayout {
    //轮播容器viewPager
    private ViewPager viewPager;
    //标示位置的点集
    private LinearLayout dots;
    //viewPager的资源ID
    private int viewPagerId;
    //包含点的LinearLayout
    private int dotsId;
    //该手机的px-dp比例倍数
    private float scale;
    //ViewPager的Adapter
    private PagerAdapter adapter;
    //是否自动播放
    private boolean isAutoPlay;
    //播放的间隔
    private int interval;
    //当前页位置
    private int currentItem;
    //圆点半径
    private float dotRadius;
    //是否正在轮播运行
    private boolean isRunning;
    //位于当前页,点标志的颜色
    private int onDotColor;
    //未位于当前页,点标志的颜色
    private int offDotColor;
    //点边框颜色
    private int strokeColor;

    private final static int SCROLL_WHAT = 0x7549;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case SCROLL_WHAT:
                    synchronized(SlideLayout.this) {
                        viewPager.setCurrentItem(currentItem);
                        sendScrollMessage(interval);
                    }
                    break;
            }
        }
    };

    /**
     * 开始自动轮播
     */
    public void startAutoScroll() {
        if(!isRunning) {
            isRunning = true;
            sendScrollMessage(interval);
        }
    }

    /**
     * 滚动到下一页
     * @param delayTimeInMills
     */
    private synchronized void sendScrollMessage(long delayTimeInMills) {
        /** remove messages before, keeps one message is running at most **/
        handler.removeMessages(SCROLL_WHAT);
        currentItem = (currentItem + 1) %  adapter.getCount();
        //通过该方法实现定时轮播
        handler.sendEmptyMessageDelayed(SCROLL_WHAT, delayTimeInMills);
    }

    public SlideLayout(Context context) {
        super(context);
        init(context);
    }

    public SlideLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SlideLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * 获取自定义参数
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet...attrs) {
        if(attrs.length > 0) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs[0], R.styleable.SlideLayout);
            viewPagerId = typedArray.getResourceId(R.styleable.SlideLayout_viewpagerId, -1);
            dotsId = typedArray.getResourceId(R.styleable.SlideLayout_dotsId, -1);
            scale = getResources().getDisplayMetrics().density;
            isAutoPlay = typedArray.getBoolean(R.styleable.SlideLayout_autoPlay, false);
            interval = typedArray.getInteger(R.styleable.SlideLayout_slide_interval, 4000);
            dotRadius = typedArray.getDimension(R.styleable.SlideLayout_dotRadius, 2f * scale);
            onDotColor = typedArray.getColor(R.styleable.SlideLayout_onDotColor, 0x77FFFFFF);
            offDotColor = typedArray.getColor(R.styleable.SlideLayout_offDotColor, 0x77000000);
            strokeColor = typedArray.getColor(R.styleable.SlideLayout_strokeColor, 0x77626262);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if(viewPagerId != -1 && dotsId != -1) {
            viewPager = (ViewPager)findViewById(viewPagerId);
            dots = (LinearLayout)findViewById(dotsId);

            viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i2) {

                }

                @Override
                public void onPageSelected(int i) {
                    currentItem = i;
                    for(int j = 0; j < viewPager.getAdapter().getCount(); j++) {
                        DotView dot = (DotView)dots.getChildAt(j);
                        if(j == i)
                            dot.setOn(true);
                        else
                            dot.setOn(false);
                        dot.invalidate();
                    }
                }

                @Override
                public void onPageScrollStateChanged(int i) {
                    switch (i) {
                        case 1:// 手势滑动，空闲中
                            isAutoPlay = false;
                            break;
                        case 2:// 界面切换中
                            isAutoPlay = true;
                            break;
                        case 0:// 滑动结束，即切换完毕或者加载完毕
                            // 当前为最后一张，此时从右向左滑，则切换到第一张
                            if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1 && !isAutoPlay) {
                                viewPager.setCurrentItem(0);
                            }
                            // 当前为第一张，此时从左向右滑，则切换到最后一张
                            else if (viewPager.getCurrentItem() == 0 && !isAutoPlay) {
                                viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 1);
                        }
                            break;
                    }
                }
            });
        }
    }

    /**
     * 在UI线程中调用，注入ViewPager的adapter
     * @param adapter
     */
    public void setViewPagerAdapter(PagerAdapter adapter) {
        this.adapter = adapter;
        viewPager.setAdapter(adapter);
        updateUI();
    }

    /**
     * 在UI线程中调用，修改adapter中数据后, 更新UI
     */
    public synchronized void updateUI() {
        adapter.notifyDataSetChanged();
        resetDots();
        if(isAutoPlay)
            startAutoScroll();
    }

    /**
     * 重新生成点集UI
     */
    public void resetDots() {
        int dotCount = viewPager.getAdapter().getCount();
        int oldDotCount = dots.getChildCount();
        for(int i = 0; i < dotCount; i ++) {
            DotView dot;
            if(i >= oldDotCount) {
                dot = new DotView(getContext());
                dots.addView(dot);
            } else {
                dot = (DotView)dots.getChildAt(i);
            }
            if(i == viewPager.getCurrentItem()) {
                dot.setOn(true);
            } else {
                dot.setOn(false);
            }
        }
    }

    /**
     * 点状UI, 正方形View, 包含一个圆形点
     */
    public class DotView extends View {
        private Paint mPaint = new Paint();
        //两种状态, 是否是当前页
        private boolean isOn;
        //正方形边长
        private int mSize;

        public DotView(Context context) {
            super(context);
        }

        public DotView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public DotView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        /**
         * 比onDraw先执行
         * @param widthMeasureSpec
         * @param heightMeasureSpec
         */
        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            mSize = (int)Math.ceil(dotRadius) * 3;
            setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
        }

        private int measureWidth(int measureSpec) {
            int result = 0;
            int specMode = MeasureSpec.getMode(measureSpec);
            int specSize = MeasureSpec.getSize(measureSpec);

            if (specMode == MeasureSpec.EXACTLY) {
                // We were told how big to be
                result = specSize;
            } else {
                // Measure the text
                result = mSize + getPaddingLeft() + getPaddingRight();
                if (specMode == MeasureSpec.AT_MOST) {
                    // Respect AT_MOST value if that was what is called for by
                    // measureSpec
                    result = Math.min(result, specSize);// 60,480
                }
            }

            return result;
        }

        private int measureHeight(int measureSpec) {
            int result = 0;
            int specMode = MeasureSpec.getMode(measureSpec);
            int specSize = MeasureSpec.getSize(measureSpec);

            if (specMode == MeasureSpec.EXACTLY) {
                // We were told how big to be
                result = specSize;
            } else {
                // Measure the text (beware: ascent is a negative number)
                result = mSize + getPaddingTop() + getPaddingBottom();
                if (specMode == MeasureSpec.AT_MOST) {
                    // Respect AT_MOST value if that was what is called for by
                    // measureSpec
                    result = Math.min(result, specSize);
                }
            }
            return result;
        }

        //绘制点
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            mPaint.setAntiAlias(true);
            if(isOn()) {
                mPaint.setColor(onDotColor);
            } else {
                mPaint.setColor(offDotColor);
            }
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(dotRadius, dotRadius, dotRadius, mPaint);

            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(0.7f * scale);
            mPaint.setColor(strokeColor);
            canvas.drawCircle(dotRadius, dotRadius, dotRadius, mPaint);
        }

        public boolean isOn() {
            return isOn;
        }

        public void setOn(boolean isOn) {
            this.isOn = isOn;
        }
    }

    //Setter && Getter
    public PagerAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(PagerAdapter adapter) {
        this.adapter = adapter;
    }
}
