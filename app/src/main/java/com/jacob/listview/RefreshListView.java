package com.jacob.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by jacob-wj on 2015/4/5.
 */
public class RefreshListView extends ListView implements AbsListView.OnScrollListener{
    private View mHeaderView;
    private View mFooterView;

    private LayoutInflater mInflater ;

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
        mHeaderView = mInflater.inflate(R.layout.layout_listview_header_view,null);


    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }



    public interface  OnListViewRefreshListener{
        void onHeaderLoading();
        void onFooterLoading();
    }

    public void setOnListViewRefreshListener(OnListViewRefreshListener listener){
        this.mListRefreshListener = listener;
    }
}
