package com.kageruka.apitest.photos;

import com.kageruka.apitest.utils.JsonLoader;
import java.util.Map;

public class PhotoPayload {
    public static Map<String, Object> create() {
        return JsonLoader.loadAsMap("testdata/photos/create.json");
    }

    public static Map<String, Object> update() {
        return JsonLoader.loadAsMap("testdata/photos/update.json");
    }
}
