package com.castellanos94.fuzzylogic.api.model;


import javax.validation.constraints.NotBlank;

public abstract class Query {

    @NotBlank
    protected String name;

    protected boolean isPublic;

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
