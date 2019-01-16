package com.sz.baseuiframe.Models;

public class PrintModel extends BaseModel {
    public class PrintInfo {
        private String BH;
        private String ZJ;
        private String XM;
        private String XB;
        private String CSRQ;
        private String MZ;
        private String BZ;
        private String DD;
        private String DH;

        public String getDD() {
            return DD;
        }

        public void setDD(String DD) {
            this.DD = DD;
        }

        public String getDH() {
            return DH;
        }

        public void setDH(String DH) {
            this.DH = DH;
        }

        public String getBZ() {
            return BZ;
        }

        public void setBZ(String BZ) {
            this.BZ = BZ;
        }

        public String getMZ() {
            return MZ;
        }

        public void setMZ(String MZ) {
            this.MZ = MZ;
        }

        public String getBH() {
            return BH;
        }

        public void setBH(String BH) {
            this.BH = BH;
        }

        public String getZJ() {
            return ZJ;
        }

        public void setZJ(String ZJ) {
            this.ZJ = ZJ;
        }

        public String getXM() {
            return XM;
        }

        public void setXM(String XM) {
            this.XM = XM;
        }

        public String getXB() {
            return XB;
        }

        public void setXB(String XB) {
            this.XB = XB;
        }

        public String getCSRQ() {
            return CSRQ;
        }

        public void setCSRQ(String CSRQ) {
            this.CSRQ = CSRQ;
        }

        public String getHJD() {
            return HJD;
        }

        public void setHJD(String HJD) {
            this.HJD = HJD;
        }

        public String getXZZ() {
            return XZZ;
        }

        public void setXZZ(String XZZ) {
            this.XZZ = XZZ;
        }

        public String getYHXM() {
            return YHXM;
        }

        public void setYHXM(String YHXM) {
            this.YHXM = YHXM;
        }

        public String getDWMC() {
            return DWMC;
        }

        public void setDWMC(String DWMC) {
            this.DWMC = DWMC;
        }

        public String getTJSJ() {
            return TJSJ;
        }

        public void setTJSJ(String TJSJ) {
            this.TJSJ = TJSJ;
        }

        private String HJD;
        private String XZZ;
        private String YHXM;
        private String DWMC;
        private String TJSJ;
    }

    private PrintInfo data;

    public PrintInfo getData() {
        return data;
    }

    public void setData(PrintInfo data) {
        this.data = data;
    }
}
