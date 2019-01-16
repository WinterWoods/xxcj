package com.sz.baseuiframe.Models;

import android.icu.text.SymbolTable;

import java.util.List;

public class ListModel extends BaseModel {
    public  class  ListInfo{
        private String TJSJ;
        private String BH;
        private String XM;
        private String ZT;
        private String BZ;

        public String getBZ() {
            return BZ;
        }

        public void setBZ(String BZ) {
            this.BZ = BZ;
        }

        public String getTJSJ() {
            return TJSJ;
        }

        public void setTJSJ(String TJSJ) {
            this.TJSJ = TJSJ;
        }

        public String getBH() {
            return BH;
        }

        public void setBH(String BH) {
            this.BH = BH;
        }

        public String getXM() {
            return XM;
        }

        public void setXM(String XM) {
            this.XM = XM;
        }

        public String getZT() {
            return ZT;
        }

        public void setZT(String ZT) {
            this.ZT = ZT;
        }
    }

    public List<ListInfo> getData() {
        return data;
    }

    public void setData(List<ListInfo> data) {
        this.data = data;
    }

    private List<ListInfo> data;

}
