package models;

import lombok.Getter;

@Getter
public enum QueryType {
    COMPLETE_TASK("completeTask");

    private final String name;

    QueryType(String name) {
        this.name = name;
    }
}
