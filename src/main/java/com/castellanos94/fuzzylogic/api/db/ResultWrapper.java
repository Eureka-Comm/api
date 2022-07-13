package com.castellanos94.fuzzylogic.api.db;

import com.castellanos94.fuzzylogicgp.core.ResultTask;
import com.castellanos94.fuzzylogicgp.core.TaskType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "results")

public class ResultWrapper {
    @Id
    protected String id;

    protected  String taskId;
    protected TaskType job;
    protected ResultTask result;

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }

    public TaskType getJob() {
        return job;
    }

    public ResultTask getResult() {
        return result;
    }

    public String getId() {
        return id;
    }

    public void setJob(TaskType job) {
        this.job = job;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setResult(ResultTask result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "ResultWrapper{" +
                "id='" + id + '\'' +
                ", job=" + job +
                ", result=" + result +
                '}';
    }
}
