package com.sz.baseuiframe.Models;

public class BanBenModel extends BaseModel {
    private  BanBenInfo data;

    public BanBenInfo getData() {
        return data;
    }

    public void setData(BanBenInfo data) {
        this.data = data;
    }

   public   class  BanBenInfo{
        private  String RTN;

        public String getRTN() {
            return RTN;
        }

        public void setRTN(String RTN) {
            this.RTN = RTN;
        }
    }
}
