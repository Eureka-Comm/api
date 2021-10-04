package com.castellanos94.fuzzylogic.api.model;

public class ResponseModel {
    public enum Status {Done, Running, Failed, Created}

    private String msg = "";
    private Status status;
    private String id = "";

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
