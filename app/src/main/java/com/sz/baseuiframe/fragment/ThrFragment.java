package com.sz.baseuiframe.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheung.android.base.baseuiframe.activity.BaseFragment;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.extras.SoundPullEventListener;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.sdsmdg.tastytoast.TastyToast;
import com.sz.baseuiframe.CommHttp;
import com.sz.baseuiframe.ListViewAdapterWoDeYeJi;
import com.sz.baseuiframe.Models.ListModel;
import com.sz.baseuiframe.Models.PersonModel;
import com.sz.baseuiframe.MyAdapter.PullDownView;
import com.sz.baseuiframe.MyApp;
import com.sz.baseuiframe.R;
import com.sz.baseuiframe.TestActivity;
import com.sz.baseuiframe.ToastUtils;
import com.sz.baseuiframe.okhttp.callback.Callback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Request;

/**
 * author: C_CHEUNG
 * created on: 2017/12/11
 * description:
 */
public class ThrFragment extends BaseFragment {
    private int pager=1;

    private ListView listView;
    List<ListModel.ListInfo> listData;
    private ListViewAdapterWoDeYeJi mAdapter;
    private PullToRefreshListView mPullRefreshListView;
    private Handler handler = new Handler();
    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_thr;
    }

    @Override
    protected void initUI(View parentView) {
        listData=new ArrayList<>();

        loadData();

        initPTRListView(parentView);
        initListView(parentView);
        initIndicator();
        //一打开应用就自动刷新，下面语句可以写到刷新按钮里面
        mPullRefreshListView.setRefreshing(true);
    }
    private void initIndicator()
    {
        ILoadingLayout startLabels = mPullRefreshListView
                .getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("你可劲拉，拉...");// 刚下拉时，显示的提示
        startLabels.setRefreshingLabel("好嘞，正在刷新...");// 刷新时
        startLabels.setReleaseLabel("你敢放，我就敢刷新...");// 下来达到一定距离时，显示的提示

        ILoadingLayout endLabels = mPullRefreshListView.getLoadingLayoutProxy(
                false, true);
        endLabels.setPullLabel("你可劲拉，拉...");// 刚下拉时，显示的提示
        endLabels.setRefreshingLabel("好嘞，正在刷新...");// 刷新时
        endLabels.setReleaseLabel("你敢放，我就敢刷新...");// 下来达到一定距离时，显示的提示
    }
    private void loadData(){
        CommHttp.post("jjilu.php")
                .addParams("YHID", MyApp.instance.userInfo.getYHID())
                .addParams("PAGE",  String.valueOf(pager))
                .build().execute(new Callback<ListModel>() {
            @Override
            public void onBefore(Request request, int id) {
                //ToastUtils.show(mActivity, "正在加载数据...", TastyToast.LENGTH_SHORT,TastyToast.INFO);
            }

            @Override
            public void onAfter(int id) {
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtils.show(mActivity, "加载数据出错了，稍后再试...", TastyToast.LENGTH_SHORT,TastyToast.ERROR);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        mPullRefreshListView.onRefreshComplete();
                    }
                });
            }

            @Override
            public void onResponse(ListModel response, int id) {
                if(response.getCode()==200){
                    if(pager==1)
                    {
                        listData.clear();
                    }
                    pager++;
                    //ToastUtils.show(mActivity, "加载成功...", TastyToast.LENGTH_SHORT,TastyToast.SUCCESS);
                    listData.addAll(response.getData());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                            mPullRefreshListView.onRefreshComplete();
                        }
                    });
                }
            }
        });
    }
    /**
     * 设置下拉刷新的listview的动作
     */
    private void initPTRListView(View parentView) {
        mPullRefreshListView = parentView.findViewById(R.id.pull_refresh_list);
        //设置拉动监听器
        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //listData.clear();
                pager=1;
                loadData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadData();
            }
        });
        mPullRefreshListView.setPullToRefreshOverScrollEnabled(true);
        //mPullRefreshListView.getMode();//得到模式
        //上下都可以刷新的模式。这里有两个选择：Mode.PULL_FROM_START，Mode.BOTH，PULL_FROM_END
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);

        /**
         * 设置反馈音效
         */
//        SoundPullEventListener<ListView> soundListener = new SoundPullEventListener<ListView>(this);
//        soundListener.addSoundEvent(PullToRefreshBase.State.PULL_TO_REFRESH, R.raw.pull_event);
//        soundListener.addSoundEvent(State.RESET, R.raw.reset_sound);
//        soundListener.addSoundEvent(State.REFRESHING, R.raw.refreshing_sound);
//        mPullRefreshListView.setOnPullEventListener(soundListener);
    }
    /**
     * 设置listview的适配器
     */
    private void initListView(View parentView) {
        //通过getRefreshableView()来得到一个listview对象
        listView = mPullRefreshListView.getRefreshableView();
        mAdapter = new ListViewAdapterWoDeYeJi(parentView.getContext(),listData);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mActivity, TestActivity.class);
                intent.putExtra("BH",listData.get(position-1).getBH());
                startActivity(intent);
            }
        });
        listView.setAdapter(mAdapter);
    }
}
