package com.sz.baseuiframe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sz.baseuiframe.Models.PaiHangModel;
import com.sz.baseuiframe.MyAdapter.ListViewAdapter;
import com.sz.baseuiframe.MyAdapter.ViewHolder;

import java.util.List;

public class ListViewAdapterPaiHang extends ListViewAdapter<PaiHangModel.PaiHang> {
    public ListViewAdapterPaiHang(Context context, List<PaiHangModel.PaiHang> datas) {
        super(context, datas, R.layout.list_item_paihang);
    }

    @Override
    public void convert(ViewHolder holder, PaiHangModel.PaiHang paiHang, int position) {
        if (position == 0) {
            ((ImageView) holder.getView(R.id.pm)).setImageBitmap(ImgHelper.getBitmapFormResources(mContext, R.drawable.ph_1));
        } else if (position == 1) {
            ((ImageView) holder.getView(R.id.pm)).setImageBitmap(ImgHelper.getBitmapFormResources(mContext, R.drawable.ph_2));
        } else if (position == 2) {
            ((ImageView) holder.getView(R.id.pm)).setImageBitmap(ImgHelper.getBitmapFormResources(mContext, R.drawable.ph_3));
        } else {
            ((ImageView) holder.getView(R.id.pm)).setVisibility(View.INVISIBLE);
        }
        ((TextView) holder.getView(R.id.tv_title)).setText(paiHang.getDWMC() + "-" + paiHang.getSL());
    }
}
