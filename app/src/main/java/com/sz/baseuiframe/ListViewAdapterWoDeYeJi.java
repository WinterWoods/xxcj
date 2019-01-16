package com.sz.baseuiframe;

import android.content.Context;
import android.widget.TextView;

import com.sz.baseuiframe.Models.ListModel;
import com.sz.baseuiframe.MyAdapter.ListViewAdapter;
import com.sz.baseuiframe.MyAdapter.ViewHolder;

import java.util.List;

public class ListViewAdapterWoDeYeJi extends ListViewAdapter<ListModel.ListInfo> {

    //MyAdapter需要一个Context，通过Context获得Layout.inflater，然后通过inflater加载item的布局
    public ListViewAdapterWoDeYeJi(Context context, List<ListModel.ListInfo> datas) {
        super(context, datas, R.layout.list_item_wdyj);
    }

    @Override
    public void convert(ViewHolder holder, ListModel.ListInfo data,int position) {

        ((TextView) holder.getView(R.id.tv_title)).setText(data.getXM()+"("+data.getBH()+")");
        ((TextView) holder.getView(R.id.tv_other)).setText(data.getBZ());

         ((TextView) holder.getView(R.id.tv_sj)).setText(data.getTJSJ());
        ((TextView) holder.getView(R.id.tv_zt)).setText(data.getZT());
/*
        TextView tv = holder.getView(R.id.titleTv);
        tv.setText(...);

       ImageView view = getView(viewId);
       Imageloader.getInstance().loadImag(view.url);
*/
    }
}


