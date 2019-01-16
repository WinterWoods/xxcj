package com.sz.baseuiframe.Models;

public class PersonModel extends BaseModel {
    public class PersonInfo {
private String BH;

        public String getBH() {
            return BH;
        }

        public void setBH(String BH) {
            this.BH = BH;
        }
    }
    private PersonInfo data;

    public PersonInfo getData() {
        return data;
    }

    public void setData(PersonInfo data) {
        this.data = data;
    }
}
