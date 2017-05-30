package com.appbar.matocham.applicationbar.applicationManager;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Created by Mateusz on 18.04.2017.
 */

public class AppElement {
    public static final String DELIMITER = "#";
    private static final int MAX_PEROID = 1000 * 60 * 20;
    private String name;
    private long removeTimestamp;

    public AppElement(){
        //constructor used by Jackson
    }

    public AppElement(String name, int RemoveTimestamp) {
        this.name = name;
        this.removeTimestamp = RemoveTimestamp;
    }

    @Deprecated
    public AppElement(String parsed) {
        String[] parts = parsed.split(DELIMITER);
        name = parts[0];
        if (parts.length > 1) {
            removeTimestamp = Long.parseLong(parts[1]);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getRemoveTimestamp() {
        return removeTimestamp;
    }

    public void setRemoveTimestamp(long removeTimestamp) {
        this.removeTimestamp = removeTimestamp;
    }

    @JsonIgnore
    public boolean isRemoved() {
        return removeTimestamp != 0;
    }

    @JsonIgnore
    public boolean isObsolote() {
        return isRemoved() && (System.currentTimeMillis() - getRemoveTimestamp() > MAX_PEROID);
    }

    public void markAsRemoved() {
        removeTimestamp = System.currentTimeMillis();
    }

    public void markAsValid() {
        removeTimestamp = 0;
    }

    @Override
    public String toString() {
        return name + DELIMITER + removeTimestamp;
    }

    @Override
    public boolean equals(Object obj) {
        return this.hashCode() == obj.hashCode() && (obj instanceof AppElement);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }


}
