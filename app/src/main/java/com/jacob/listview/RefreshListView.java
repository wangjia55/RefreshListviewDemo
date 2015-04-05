package com.jacob.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by jacob-wj on 2015/4/5.
 */
public class RefreshListView extends ListView implements AbsListView.OnScrollListener {
    private View mHeaderView;
    private View mFooterView;

    private LayoutInflater mInflater;

    private Status mStatus = Status.None;

    private boolean isAtTopPosition = false;
    private int mHeaderHeight;

    private int mFirstVisibleItem;

    private int mScrollState;

    private OnListViewRefreshListener mListRefreshListener;

    public RefreshListView(Context context) {
        this(context, null);
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView(context);
    }

    private void initView(Context context) {
        mInflater = LayoutInflater.from(context);
        mHeaderView = mInflater.inflate(R.layout.layout_listview_header_view, null);
        addHeaderView(mHeaderView);
        measureView(mHeaderView);
        mHeaderHeight = mHeaderView.getMeasuredHeight();
        setTopPadding(-mHeaderHeight);
        setOnScrollListener(this);
    }

    private void measureView(View view) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int width = getChildMeasureSpec(0, 0, layoutParams.width);
        int height;
        int tempHeight = layoutParams.height;
        if (tempHeight > 0) {
            height = MeasureSpec.makeMeasureSpec(tempHeight, MeasureSpec.EXACTLY);
        } else {
            height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        view.measure(width, height);
    }

    /**
     * 设置header的上边距
     */
    private void setTopPadding(int topPadding) {
        mHeaderView.setPadding(mHeaderView.getPaddingLeft(), topPadding, mHeaderView.getPaddingRight(), mHeaderView.getPaddingBottom());
        mHeaderView.invalidate();
    }


    private void refreshViewByState(){
        TextView textView = (TextView) mHeaderView.findViewById(R.id.text_view_header);
        ImageView imageView = (ImageView) mHeaderView.findViewById(R.id.image_view_header_arrow);
        ProgressBar progressBar = (ProgressBar) mHeaderView.findViewById(R.id.progress_bar_header);
        RotateAnimation rotateAnimRelease = new RotateAnimation(0,180,RotateAnimation.RELATIVE_TO_SELF,
                0.5f,RotateAnimation.RELATIVE_TO_SELF,0.5f);
        rotateAnimRelease.setDuration(500);
        rotateAnimRelease.setFillAfter(true);

        RotateAnimation rotateAnimPull = new RotateAnimation(180,0,RotateAnimation.RELATIVE_TO_SELF,
                0.5f,RotateAnimation.RELATIVE_TO_SELF,0.5f);
        rotateAnimPull.setDuration(500);
        rotateAnimPull.setFillAfter(true);

        switch (mStatus){
            case None:
                setTopPadding(-mHeaderHeight);
                imageView.clearAnimation();
                break;
            case Pull:
                imageView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(GONE);
                textView.setText("下拉刷新");
                imageView.clearAnimation();
                imageView.setAnimation(rotateAnimPull);
                break;
            case Release:
                imageView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(GONE);
                textView.setText("松开可以刷新");
                imageView.clearAnimation();
                imageView.setAnimation(rotateAnimRelease);
                break;
            case Refreshing:
                imageView.clearAnimation();
                setTopPadding(mHeaderHeight);
                imageView.setVisibility(View.GONE);
                progressBar.setVisibility(VISIBLE);
                textView.setText("正在刷新");
                imageView.clearAnimation();
                break;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        mScrollState = scrollState;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mFirstVisibleItem = firstVisibleItem;
    }

    private float startY;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mFirstVisibleItem == 0){
                    startY = ev.getY();
                    isAtTopPosition = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                onMove(ev);
                refreshViewByState();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mStatus == Status.Release){
                    mStatus = Status.Refreshing;
                    //加载最新数据
                    if (mListRefreshListener != null){
                        mListRefreshListener.onHeaderLoading();
                    }
                }else if (mStatus == Status.Pull){
                    mStatus = Status.None;
                }
                refreshViewByState();
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void refreshComplete(){
        mStatus =Status.None;
        isAtTopPosition = false;
        refreshViewByState();
    }

    private void onMove(MotionEvent ev) {
        if (!isAtTopPosition){
            return;
        }

        int distance  = (int) (ev.getY() -startY);
        int topPadding = distance-mHeaderHeight;

        Log.e("TAG",""+distance+"++"+mHeaderHeight+"++"+mScrollState);
        switch (mStatus){
            case None:
                if (distance>0){
                    mStatus = Status.Pull;
                }
                break;
            case Pull:
                setTopPadding(topPadding);
                if ((distance>mHeaderHeight+30) && (mScrollState == SCROLL_STATE_TOUCH_SCROLL)){
                    mStatus = Status.Release;

                }
                break;
            case Release:
                setTopPadding(topPadding);
                if (distance<mHeaderHeight+30 ){
                    mStatus = Status.Pull;
                }else if(distance<=0){
                    mStatus = Status.None;
                    isAtTopPosition = false;
                }
                break;
        }

    }

    enum Status {
        None,
        Pull,
        Release,
        Refreshing
    }


    public interface OnListViewRefreshListener {
        void onHeaderLoading();

        void onFooterLoading();
    }

    public void setOnListViewRefreshListener(OnListViewRefreshListener listener) {
        this.mListRefreshListener = listener;
    }
}
