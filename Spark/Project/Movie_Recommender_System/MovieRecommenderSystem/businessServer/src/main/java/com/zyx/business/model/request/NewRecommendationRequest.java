package com.zyx.business.model.request;

public class NewRecommendationRequest {

    private int sum;

    public NewRecommendationRequest(int sum) {
        this.sum = sum;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }
}
