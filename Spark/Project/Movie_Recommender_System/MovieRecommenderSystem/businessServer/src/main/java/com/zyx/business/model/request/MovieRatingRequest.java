package com.zyx.business.model.request;

public class MovieRatingRequest {

    private int uid;

    private int mid;

    private Double score;

    public MovieRatingRequest(int uid, int mid, Double score) {
        this.uid = uid;
        this.mid = mid;
        this.score = score;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public Double getScore() {
        return Double.parseDouble(String.format("%.2f",score/2D));
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
