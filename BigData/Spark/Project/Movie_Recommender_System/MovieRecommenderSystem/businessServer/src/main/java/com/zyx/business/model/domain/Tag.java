package com.zyx.business.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class Tag {

    @JsonIgnore
    private String _id;

    private int uid;

    private int mid;

    private String tag;

    private long timestamp;

    public Tag(int uid, int mid, String tag) {
        this.uid = uid;
        this.mid = mid;
        this.tag = tag;
        this.timestamp = new Date().getTime();
    }

    public Tag() {
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}