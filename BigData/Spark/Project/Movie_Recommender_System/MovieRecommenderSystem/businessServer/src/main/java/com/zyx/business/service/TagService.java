package com.zyx.business.service;

import com.zyx.business.model.domain.Tag;
import com.zyx.business.utils.Constant;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class TagService {

    @Autowired
    private MongoClient mongoClient;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TransportClient esClient;

    private MongoCollection<Document> tagCollection;

    private MongoCollection<Document> getTagCollection(){
        if(null == tagCollection)
            tagCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_TAG_COLLECTION);
        return tagCollection;
    }

    private Tag documentToTag(Document document){
        try{
            return objectMapper.readValue(JSON.serialize(document),Tag.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
            return null;
        } catch (JsonMappingException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void newTag(Tag tag){
        try{
            getTagCollection().insertOne(Document.parse(objectMapper.writeValueAsString(tag)));
            updateElasticSearchIndex(tag);
        }catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void updateElasticSearchIndex(Tag tag){
        GetResponse getResponse = esClient.prepareGet(Constant.ES_INDEX,Constant.ES_MOVIE_TYPE,String.valueOf(tag.getMid())).get();
        Object value = getResponse.getSourceAsMap().get("tags");
        UpdateRequest updateRequest = new UpdateRequest(Constant.ES_INDEX,Constant.ES_MOVIE_TYPE,String.valueOf(tag.getMid()));
        try{
            if(value == null){
                updateRequest.doc(XContentFactory.jsonBuilder().startObject().field("tags",tag.getTag()).endObject());
            }else{
                updateRequest.doc(XContentFactory.jsonBuilder().startObject().field("tags",value+"|"+tag.getTag()).endObject());
            }
            esClient.update(updateRequest).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Tag> findMovieTags(int mid){
        List<Tag> tags = new ArrayList<>();
        FindIterable<Document> documents = getTagCollection().find(new Document("mid",mid));
        for (Document document: documents) {
            tags.add(documentToTag(document));
        }
        return tags;
    }

    public List<Tag> findMyMovieTags(int uid, int mid){
        List<Tag> tags = new ArrayList<>();
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.append("uid",uid);
        basicDBObject.append("mid",mid);
        FindIterable<Document> documents = getTagCollection().find(basicDBObject);
        for (Document document: documents) {
            tags.add(documentToTag(document));
        }
        return tags;
    }

    public void removeTag(int eid){
        getTagCollection().deleteOne(new Document("eid",eid));
    }

}
