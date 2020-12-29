package com.zyx.business.service;

import com.zyx.business.model.domain.Movie;
import com.zyx.business.model.domain.Rating;
import com.zyx.business.model.recom.Recommendation;
import com.zyx.business.model.request.NewRecommendationRequest;
import com.zyx.business.utils.Constant;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MovieService {

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private ObjectMapper objectMapper;

    private MongoCollection<Document> movieCollection;
    private MongoCollection<Document> averageMoviesScoreCollection;
    private MongoCollection<Document> rateCollection;

    private MongoCollection<Document> getMovieCollection(){
        if(null == movieCollection)
            movieCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_MOVIE_COLLECTION);
        return movieCollection;
    }

    private MongoCollection<Document> getAverageMoviesScoreCollection(){
        if(null == averageMoviesScoreCollection)
            averageMoviesScoreCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_AVERAGE_MOVIES_SCORE_COLLECTION);
        return averageMoviesScoreCollection;
    }

    private MongoCollection<Document> getRateCollection(){
        if(null == rateCollection)
            rateCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_RATING_COLLECTION);
        return rateCollection;
    }

    public List<Movie> getRecommendeMovies(List<Recommendation> recommendations){
        List<Integer> ids = new ArrayList<>();
        for (Recommendation rec: recommendations) {
            ids.add(rec.getMid());
        }
        return getMovies(ids);
    }

    public List<Movie> getHybirdRecommendeMovies(List<Recommendation> recommendations){
        List<Integer> ids = new ArrayList<>();
        for (Recommendation rec: recommendations) {
            ids.add(rec.getMid());
        }
        return getMovies(ids);
    }

    public List<Movie> getMovies(List<Integer> mids){
        FindIterable<Document> documents = getMovieCollection().find(Filters.in("mid",mids));
        List<Movie> movies = new ArrayList<>();
        for (Document document: documents) {
            movies.add(documentToMovie(document));
        }
        return movies;
    }

    private Movie documentToMovie(Document document){
        Movie movie = null;
        try{
            movie = objectMapper.readValue(JSON.serialize(document),Movie.class);
            Document score = getAverageMoviesScoreCollection().find(Filters.eq("mid",movie.getMid())).first();
            if(null == score || score.isEmpty())
                movie.setScore(0D);
            else
                movie.setScore((Double) score.get("avg",0D));
        }catch (IOException e) {
            e.printStackTrace();
        }
        return movie;
    }

    private Rating documentToRating(Document document){
        Rating rating = null;
        try{
            rating = objectMapper.readValue(JSON.serialize(document),Rating.class);
        }catch (IOException e) {
            e.printStackTrace();
        }
        return rating;
    }

    public boolean movieExist(int mid){
        return null != findByMID(mid);
    }

    public Movie findByMID(int mid){
        Document document = getMovieCollection().find(new Document("mid",mid)).first();
        if(document == null || document.isEmpty())
            return null;
        return documentToMovie(document);
    }

    public void removeMovie(int mid){
        getMovieCollection().deleteOne(new Document("mid",mid));
    }

    public List<Movie> getMyRateMovies(int uid){
        FindIterable<Document> documents = getRateCollection().find(Filters.eq("uid",uid));
        List<Integer> ids = new ArrayList<>();
        Map<Integer,Double> scores = new HashMap<>();
        for (Document document: documents) {
            Rating rating = documentToRating(document);
            ids.add(rating.getMid());
            scores.put(rating.getMid(),rating.getScore());
        }
        List<Movie> movies = getMovies(ids);
        for (Movie movie: movies) {
            movie.setScore(scores.getOrDefault(movie.getMid(),movie.getScore()));
        }

        return movies;
    }

    public List<Movie> getNewMovies(NewRecommendationRequest request){
        FindIterable<Document> documents = getMovieCollection().find().sort(Sorts.descending("issue")).limit(request.getSum());
        List<Movie> movies = new ArrayList<>();
        for (Document document: documents) {
            movies.add(documentToMovie(document));
        }
        return movies;
    }

}
