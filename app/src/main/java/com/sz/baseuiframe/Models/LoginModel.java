package com.sz.baseuiframe.Models;

public class LoginModel extends BaseModel {
    public class UserInfo {
        public String getYHID() {
            return YHID;
        }

        public void setYHID(String YHID) {
            this.YHID = YHID;
        }

        public String getYHXM() {
            return YHXM;
        }

        public void setYHXM(String YHXM) {
            this.YHXM = YHXM;
        }

        private String YHID;
        private String YHXM;
    }

    public UserInfo getData() {
        return data;
    }

    public void setData(UserInfo data) {
        this.data = data;
    }

    private UserInfo data;

}
