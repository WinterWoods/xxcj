package com.sz.baseuiframe.Models;

public class WanChengModel {
    public  class  Wancheng{
        private String DWMC;
        private String DQRW;
        private  String RWSL;

        public String getDWMC() {
            return DWMC;
        }

        public void setDWMC(String DWMC) {
            this.DWMC = DWMC;
        }

        public String getDQRW() {
            return DQRW;
        }

        public void setDQRW(String DQRW) {
            this.DQRW = DQRW;
        }

        public String getRWSL() {
            return RWSL;
        }

        public void setRWSL(String RWSL) {
            this.RWSL = RWSL;
        }
    }
    private  Wancheng data;

    public Wancheng getData() {
        return data;
    }

    public void setData(Wancheng data) {
        this.data = data;
    }
}
