package com.castellanos94.fuzzylogic.api.model;

import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotBlank;

public abstract class Query {

    @Id
    protected String id;
    @NotBlank
    protected String name;

    protected boolean isPublic;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPublic() {
        return isPublic;
    }
}
