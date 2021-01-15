package com.zyx.business.service;

import com.zyx.business.model.domain.Rating;
import com.zyx.business.model.domain.User;
import com.zyx.business.model.request.MovieRatingRequest;
import com.zyx.business.utils.Constant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.io.IOException;

@Service
public class RatingService {

    @Autowired
    private MongoClient mongoClient;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private Jedis jedis;

    private MongoCollection<Document> ratingCollection;

    private MongoCollection<Document> getRatingCollection() {
        if (null == ratingCollection)
            ratingCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_RATING_COLLECTION);
        return ratingCollection;
    }

    private Rating documentToRating(Document document) {
        Rating rating = null;
        try {
            rating = objectMapper.readValue(JSON.serialize(document), Rating.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rating;

    }

    public boolean movieRating(MovieRatingRequest request) {
        Rating rating = new Rating(request.getUid(), request.getMid(), request.getScore());
        updateRedis(rating);
        if (ratingExist(rating.getUid(), rating.getMid())) {
            return updateRating(rating);
        } else {
            return newRating(rating);
        }
    }

    private void updateRedis(Rating rating) {
        if (jedis.exists("uid:" + rating.getUid()) && jedis.llen("uid:" + rating.getUid()) >= Constant.REDIS_MOVIE_RATING_QUEUE_SIZE) {
            jedis.rpop("uid:" + rating.getUid());
        }
        jedis.lpush("uid:" + rating.getUid(), rating.getMid() + ":" + rating.getScore());
    }

    public boolean newRating(Rating rating) {
        try {
            getRatingCollection().insertOne(Document.parse(objectMapper.writeValueAsString(rating)));
            return true;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean ratingExist(int uid, int mid) {
        return null != findRating(uid, mid);
    }

    public boolean updateRating(Rating rating) {
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.append("uid", rating.getUid());
        basicDBObject.append("mid", rating.getMid());
        getRatingCollection().updateOne(basicDBObject,
                new Document().append("$set", new Document("score", rating.getScore())));
        return true;
    }


    public Rating findRating(int uid, int mid) {
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.append("uid", uid);
        basicDBObject.append("mid", mid);
        FindIterable<Document> documents = getRatingCollection().find(basicDBObject);
        if (documents.first() == null)
            return null;
        return documentToRating(documents.first());
    }

    public void removeRating(int uid, int mid) {
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.append("uid", uid);
        basicDBObject.append("mid", mid);
        getRatingCollection().deleteOne(basicDBObject);
    }

    public int[] getMyRatingStat(User user) {
        FindIterable<Document> documents = getRatingCollection().find(new Document("uid", user.getUid()));
        int[] stats = new int[10];
        for (Document document : documents) {
            Rating rating = documentToRating(document);
            Long index = Math.round(rating.getScore() / 0.5);
            stats[index.intValue()] = stats[index.intValue()] + 1;
        }
        return stats;
    }

}
