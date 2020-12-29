package com.zyx.business.model.recom;

/**
 * 推荐项目的包装
 */
public class Recommendation {

    // 电影ID
    private int mid;

    // 电影的推荐得分
    private Double score;

    public Recommendation() {
    }

    public Recommendation(int mid, Double score) {
        this.mid = mid;
        this.score = score;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
