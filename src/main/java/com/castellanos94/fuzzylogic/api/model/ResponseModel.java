package com.castellanos94.fuzzylogic.api.model;

public class ResponseModel {
    public enum Status {Done, Running, Failed, Created}

    private String msg = "";
    private Status status;
    private String id = "";

    public String getMsg() {
        return msg;
    }

    public ResponseModel setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public Status getStatus() {
        return status;
    }

    public ResponseModel setStatus(Status status) {
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
