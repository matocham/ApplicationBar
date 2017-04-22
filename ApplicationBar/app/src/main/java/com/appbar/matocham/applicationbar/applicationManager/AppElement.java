package com.appbar.matocham.applicationbar.applicationManager;

/**
 * Created by Mateusz on 18.04.2017.
 */

public class AppElement {
    public static final String DELIMITER = "#";
    private static final int MAX_PEROID = 1000 * 60 * 20;
    private String name;
    private long deleteTimestamp;

    public AppElement(String name, int deleteTimestamp) {
        this.name = name;
        this.deleteTimestamp = deleteTimestamp;
    }

    public AppElement(String parsed) {
        String[] parts = parsed.split(DELIMITER);
        name = parts[0];
        if(parts.length>1){
            deleteTimestamp = Long.parseLong(parts[1]);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDeleteTimestamp() {
        return deleteTimestamp;
    }

    public void setDeleteTimestamp(long deleteTimestamp) {
        this.deleteTimestamp = deleteTimestamp;
    }

    public boolean isDeleted(){
        return deleteTimestamp !=0;
    }

    public boolean isObsolote() {
        return (System.currentTimeMillis()-getDeleteTimestamp()>MAX_PEROID);
    }

    @Override
    public String toString() {
        return name + DELIMITER + deleteTimestamp;
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
