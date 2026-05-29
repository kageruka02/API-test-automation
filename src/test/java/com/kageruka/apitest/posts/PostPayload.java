package com.kageruka.apitest.posts;

import com.kageruka.apitest.utils.JsonLoader;
import java.util.Map;

public class PostPayload {
    public static Map<String, Object> create() {
        return JsonLoader.loadAsMap("testdata/posts/create.json");
    }

    public static Map<String, Object> update() {
        return JsonLoader.loadAsMap("testdata/posts/update.json");
    }
}
