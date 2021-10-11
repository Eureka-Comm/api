package com.castellanos94.fuzzylogic.api.db;

import com.castellanos94.fuzzylogic.api.model.Query;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "queries")
@JsonInclude(JsonInclude.Include.NON_NULL)

public class EurekaTask {
    public enum Status {Done, Running, Failed, Created}

    @Id
    protected String id;
    protected Status status;
    protected Query query;

    protected String userId;
    protected String msg;

    public EurekaTask() {
        this.status = Status.Created;
    }

    public Query getQuery() {
        return query;
    }

    public String getId() {
        return id;
    }

    public String getMsg() {
        return msg;
    }

    public EurekaTask setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public EurekaTask setId(String id) {
        this.id = id;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public EurekaTask setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public EurekaTask setQuery(Query query) {
        this.query = query;
        return this;
    }

    public Status getStatus() {
        return status;
    }

    public EurekaTask setStatus(Status status) {
        this.status = status;
        return this;
    }

    @Override
    public String toString() {
        return "EurekaTask{" +
                "id='" + id + '\'' +
                ", status=" + status +
                ", query=" + query +
                ", userId='" + userId + '\'' +
                '}';
    }
}
