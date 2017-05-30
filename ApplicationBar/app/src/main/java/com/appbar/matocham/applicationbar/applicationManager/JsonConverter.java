package com.appbar.matocham.applicationbar.applicationManager;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.CollectionType;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by matocham on 30.05.2017.
 */

public class JsonConverter {
    ObjectMapper mapper;
    CollectionType widgetsListCollectionType;

    public JsonConverter() {
        mapper = new ObjectMapper();
        widgetsListCollectionType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, NewWidget.class);
    }

    public String writeValueAsString(Object objectToMap) throws IOException {
        return mapper.writeValueAsString(objectToMap);
    }

    public ArrayList<NewWidget> readValue(String jsonValue) throws IOException {
        return mapper.readValue(jsonValue, widgetsListCollectionType);
    }
}
