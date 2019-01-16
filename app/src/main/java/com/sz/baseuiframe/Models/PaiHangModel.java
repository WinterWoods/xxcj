package com.sz.baseuiframe.Models;

import java.util.List;

public class PaiHangModel extends  BaseModel {
    public  class  PaiHang{
        private  String DWMC;
        private String SL;

        public String getDWMC() {
            return DWMC;
        }

        public void setDWMC(String DWMC) {
            this.DWMC = DWMC;
        }

        public String getSL() {
            return SL;
        }

        public void setSL(String SL) {
            this.SL = SL;
        }
    }

    public List<PaiHang> getData() {
        return data;
    }

    public void setData(List<PaiHang> data) {
        this.data = data;
    }

    private  List<PaiHang> data;

}
