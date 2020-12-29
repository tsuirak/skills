package com.zyx.business.model.request;

public class TopGenresRecommendationRequest {

    private int sum;
    private String genres;

    public TopGenresRecommendationRequest(String genres, int sum) {
        this.genres = genres;
        this.sum = sum;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }
}
