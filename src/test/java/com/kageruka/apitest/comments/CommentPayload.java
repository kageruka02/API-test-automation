package com.kageruka.apitest.comments;

import com.kageruka.apitest.utils.JsonLoader;
import java.util.Map;

public class CommentPayload {
    public static Map<String, Object> create() {
        return JsonLoader.loadAsMap("testdata/comments/create.json");
    }

    public static Map<String, Object> update() {
        return JsonLoader.loadAsMap("testdata/comments/update.json");
    }
}
