package com.zyx.business.rest;

import com.zyx.business.model.domain.Tag;
import com.zyx.business.model.recom.Recommendation;
import com.zyx.business.model.request.*;
import com.zyx.business.service.*;
import com.zyx.business.model.domain.User;
import com.zyx.business.model.request.*;
import com.zyx.business.service.*;
import com.zyx.business.utils.Constant;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Random;


@RequestMapping("/rest/movie")
@Controller
public class MovieRestApi {

//    private Logger logger = LoggerFactory.getLogger(MovieRestApi.class);

    private static Logger logger = Logger.getLogger(MovieRestApi.class.getName());

    @Autowired
    private RecommenderService recommenderService;
    @Autowired
    private MovieService movieService;
    @Autowired
    private UserService userService;
    @Autowired
    private RatingService ratingService;
    @Autowired
    private TagService tagService;

    /**
     * 获取推荐的电影【实时推荐6 + 内容推荐4】
     * @param username
     * @param model
     * @return
     */
    // TODO: 2017/10/20  bug 混合推荐结果中，基于内容的推荐，基于MID，而非UID
    @RequestMapping(value = "/guess", produces = "application/json", method = RequestMethod.GET )
    @ResponseBody
    public Model getGuessMovies(@RequestParam("username")String username,@RequestParam("num")int num, Model model) {
        User user = userService.findByUsername(username);
        List<Recommendation> recommendations = recommenderService.getHybridRecommendations(new MovieHybridRecommendationRequest(user.getUid(),num));
        if(recommendations.size()==0){
            String randomGenres = user.getPrefGenres().get(new Random().nextInt(user.getPrefGenres().size()));
            recommendations = recommenderService.getTopGenresRecommendations(new TopGenresRecommendationRequest(randomGenres.split(" ")[0],num));
        }
        model.addAttribute("success",true);
        model.addAttribute("movies",movieService.getHybirdRecommendeMovies(recommendations));
        return model;
    }

    /**
     *
     * @param username
     * @param model
     * @return
     */
    @RequestMapping(value = "/wish", produces = "application/json", method = RequestMethod.GET )
    @ResponseBody
    public Model getWishMovies(@RequestParam("username")String username,@RequestParam("num")int num, Model model) {
        User user = userService.findByUsername(username);
        List<Recommendation> recommendations = recommenderService.getCollaborativeFilteringRecommendations(new UserRecommendationRequest(user.getUid(),num));
        if(recommendations.size()==0){
            String randomGenres = user.getPrefGenres().get(new Random().nextInt(user.getPrefGenres().size()));
            recommendations = recommenderService.getTopGenresRecommendations(new TopGenresRecommendationRequest(randomGenres.split(" ")[0],num));
        }
        model.addAttribute("success",true);
        model.addAttribute("movies",movieService.getRecommendeMovies(recommendations));
        return model;
    }

    /**
     * 获取热门推荐
     * @param model
     * @return
     */
    @RequestMapping(value = "/hot", produces = "application/json", method = RequestMethod.GET )
    @ResponseBody
    public Model getHotMovies(@RequestParam("num")int num, Model model) {
        List<Recommendation> recommendations = recommenderService.getHotRecommendations(new HotRecommendationRequest(num));
        model.addAttribute("success",true);
        model.addAttribute("movies",movieService.getRecommendeMovies(recommendations));
        return model;
    }

    /**
     * 获取投票最多的电影
     * @param model
     * @return
     */
    @RequestMapping(value = "/rate", produces = "application/json", method = RequestMethod.GET )
    @ResponseBody
    public Model getRateMoreMovies(@RequestParam("num")int num, Model model) {
        List<Recommendation> recommendations = recommenderService.getRateMoreRecommendations(new RateMoreRecommendationRequest(num));
        model.addAttribute("success",true);
        model.addAttribute("movies",movieService.getRecommendeMovies(recommendations));
        return model;
    }

    /**
     * 获取新添加的电影
     * @param model
     * @return
     */
    @RequestMapping(value = "/new", produces = "application/json", method = RequestMethod.GET )
    @ResponseBody
    public Model getNewMovies(@RequestParam("num")int num, Model model) {
        model.addAttribute("success",true);
        model.addAttribute("movies",movieService.getNewMovies(new NewRecommendationRequest(num)));
        return model;
    }

    /**
     * 获取电影详细页面相似的电影集合
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "/same/{id}", produces = "application/json", method = RequestMethod.GET )
    @ResponseBody
    public Model getSameMovie(@PathVariable("id")int id,@RequestParam("num")int num, Model model) {
        List<Recommendation> recommendations = recommenderService.getCollaborativeFilteringRecommendations(new MovieRecommendationRequest(id,num));
        model.addAttribute("success",true);
        model.addAttribute("movies",movieService.getRecommendeMovies(recommendations));
        return model;
    }


    /**
     * 获取单个电影的信息
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "/info/{id}", produces = "application/json", method = RequestMethod.GET )
    @ResponseBody
    public Model getMovieInfo(@PathVariable("id")int id, Model model) {
        model.addAttribute("success",true);
        model.addAttribute("movie",movieService.findByMID(id));
        return model;
    }

    /**
     * 模糊查询电影
     * @param query
     * @param model
     * @return
     */
    @RequestMapping(value = "/search", produces = "application/json", method = RequestMethod.GET )
    @ResponseBody
    public Model getSearchMovies(@RequestParam("query")String query, Model model) {
        List<Recommendation> recommendations = recommenderService.getContentBasedSearchRecommendations(new SearchRecommendationRequest(query,100));
        model.addAttribute("success",true);
        model.addAttribute("movies",movieService.getRecommendeMovies(recommendations));
        return model;
    }

    /**
     * 查询类别电影
     * @param category
     * @param model
     * @return
     */
    @RequestMapping(value = "/genres", produces = "application/json", method = RequestMethod.GET )
    @ResponseBody
    public Model getGenresMovies(@RequestParam("category")String category, Model model) {
        List<Recommendation> recommendations = recommenderService.getContentBasedGenresRecommendations(new SearchRecommendationRequest(category,100));
        model.addAttribute("success",true);
        model.addAttribute("movies",movieService.getRecommendeMovies(recommendations));
        return model;
    }

    /**
     * 获取用户评分过得电影
     * @param username
     * @param model
     * @return
     */
    @RequestMapping(value = "/myrate", produces = "application/json", method = RequestMethod.GET )
    @ResponseBody
    public Model getMyRateMovies(@RequestParam("username")String username, Model model) {
        User user = userService.findByUsername(username);
        model.addAttribute("success",true);
        model.addAttribute("movies",movieService.getMyRateMovies(user.getUid()));
        return model;
    }


    @RequestMapping(value = "/rate/{id}", produces = "application/json", method = RequestMethod.GET )
    @ResponseBody
    public Model rateToMovie(@PathVariable("id")int id,@RequestParam("score")Double score,@RequestParam("username")String username, Model model) {
        User user = userService.findByUsername(username);
        MovieRatingRequest request = new MovieRatingRequest(user.getUid(),id,score);
        boolean complete = ratingService.movieRating(request);
        //埋点日志
        if(complete) {
            System.out.print("=========complete=========");
            logger.info(Constant.MOVIE_RATING_PREFIX + ":" + user.getUid() +"|"+ id +"|"+ request.getScore() +"|"+ System.currentTimeMillis()/1000);
        }
        model.addAttribute("success",true);
        model.addAttribute("message"," 已完成评分！");
        return model;
    }


    @RequestMapping(value = "/tag/{mid}", produces = "application/json", method = RequestMethod.GET )
    @ResponseBody
    public Model getMovieTags(@PathVariable("mid")int mid, Model model) {
        model.addAttribute("success",true);
        model.addAttribute("tags",tagService.findMovieTags(mid));
        return model;
    }

    @RequestMapping(value = "/mytag/{mid}", produces = "application/json", method = RequestMethod.GET )
    @ResponseBody
    public Model getMyTags(@PathVariable("mid")int mid,@RequestParam("username")String username, Model model) {
        User user = userService.findByUsername(username);
        model.addAttribute("success",true);
        model.addAttribute("tags",tagService.findMyMovieTags(user.getUid(),mid));
        return model;
    }

    @RequestMapping(value = "/newtag/{mid}", produces = "application/json", method = RequestMethod.GET )
    @ResponseBody
    public Model addMyTags(@PathVariable("mid")int mid,@RequestParam("tagname")String tagname,@RequestParam("username")String username, Model model) {
        User user = userService.findByUsername(username);
        Tag tag = new Tag(user.getUid(),mid,tagname);
        tagService.newTag(tag);
        model.addAttribute("success",true);
        model.addAttribute("tag",tag);
        return model;
    }

    @RequestMapping(value = "/stat", produces = "application/json", method = RequestMethod.GET )
    @ResponseBody
    public Model getMyRatingStat(@RequestParam("username")String username, Model model) {
        User user = userService.findByUsername(username);
        model.addAttribute("success",true);
        model.addAttribute("stat",ratingService.getMyRatingStat(user));
        return model;
    }

}
