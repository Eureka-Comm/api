package com.castellanos94.fuzzylogic.api.model;

import com.castellanos94.fuzzylogic.api.db.EurekaTask;

public class ResponseModel {

    private String msg = "";
    private EurekaTask.Status status;
    private String id = "";

    public String getMsg() {
        return msg;
    }

    public ResponseModel setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public EurekaTask.Status getStatus() {
        return status;
    }

    public ResponseModel setStatus(EurekaTask.Status status) {
        this.status = status;
        return this;
    }

    public String getId() {
        return id;
    }

    public ResponseModel setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public String toString() {
        return "ResponseModel{" +
                "msg='" + msg + '\'' +
                ", status=" + status +
                ", id='" + id + '\'' +
                '}';
    }
}
