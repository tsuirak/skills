package com.zyx.business.utils;

public class Constant {

    //************** FOR MONGODB ****************

    public static String MONGODB_DATABASE = "recommender";

    public static String MONGODB_USER_COLLECTION= "User";

    public static String MONGODB_MOVIE_COLLECTION = "Movie";

    public static String MONGODB_RATING_COLLECTION = "Rating";

    public static String MONGODB_TAG_COLLECTION = "Tag";

    public static String MONGODB_AVERAGE_MOVIES_SCORE_COLLECTION = "AverageMovies";

    public static String MONGODB_MOVIE_RECS_COLLECTION = "MovieRecs";

    public static String MONGODB_RATE_MORE_MOVIES_COLLECTION = "RateMoreMovies";

    public static String MONGODB_RATE_MORE_MOVIES_RECENTLY_COLLECTION = "RateMoreRecentlyMovies";

    public static String MONGODB_STREAM_RECS_COLLECTION = "StreamRecs";

    public static String MONGODB_USER_RECS_COLLECTION = "UserRecs";

    public static String MONGODB_GENRES_TOP_MOVIES_COLLECTION = "GenresTopMovies";

    //************** FOR ELEASTICSEARCH ****************

    public static String ES_INDEX = "recommender";

    public static String ES_MOVIE_TYPE = "Movie";


    //************** FOR MOVIE RATING ******************

    public static String MOVIE_RATING_PREFIX = "MOVIE_RATING_PREFIX";

    public static int REDIS_MOVIE_RATING_QUEUE_SIZE = 40;
}
