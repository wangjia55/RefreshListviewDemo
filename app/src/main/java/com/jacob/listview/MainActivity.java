package com.jacob.listview;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends FragmentActivity implements RefreshListView.OnListViewRefreshListener {

    private RefreshListView mRefreshListView;
    private RefreshListAdapter mRefreshAdapter;
    private List<GamesBean> mGamesList  = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRefreshListView = (RefreshListView) findViewById(R.id.refresh_list_view);
        mRefreshListView.setOnListViewRefreshListener(this);
        initData();
        mRefreshAdapter = new RefreshListAdapter(mGamesList);
        mRefreshListView.setAdapter(mRefreshAdapter);
    }


    private void initData(){
        for (int i = 0; i < 10; i++) {
            GamesBean gamesBean = new GamesBean(R.drawable.ic_menu_6,"GAME"+i);
            mGamesList.add(gamesBean);
        }
    }

    @Override
    public void onHeaderLoading() {

    }

    @Override
    public void onFooterLoading() {

    }


    private class RefreshListAdapter extends BaseAdapter{

        private List<GamesBean> gamesBeanList = new ArrayList<>();

        private RefreshListAdapter(List<GamesBean> mGamesList) {
            this.gamesBeanList = mGamesList;
        }

        public void setListData(List<GamesBean> mGamesList){
            gamesBeanList.clear();
            gamesBeanList.addAll(mGamesList);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return gamesBeanList.size();
        }

        @Override
        public GamesBean getItem(int position) {
            return gamesBeanList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder ;
            if (convertView == null){
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(getApplication()).inflate(R.layout.layout_list_item,null);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.text_view_name);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_view_avatar);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            GamesBean gamesBean = getItem(position);
            viewHolder.textView.setText(gamesBean.getName());
            return convertView;
        }

        class ViewHolder{
            TextView textView;
            ImageView imageView;
        }
    }

}
