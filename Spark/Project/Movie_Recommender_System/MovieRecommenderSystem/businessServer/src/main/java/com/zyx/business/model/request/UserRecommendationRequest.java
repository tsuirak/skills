package com.zyx.business.model.request;

public class UserRecommendationRequest {
    private int uid;

    private int sum;

    public UserRecommendationRequest(int uid, int sum) {
        this.uid = uid;
        this.sum = sum;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
