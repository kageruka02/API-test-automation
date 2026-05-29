package com.kageruka.apitest.todos;

import com.kageruka.apitest.utils.JsonLoader;
import java.util.Map;

public class TodoPayload {
    public static Map<String, Object> create() {
        return JsonLoader.loadAsMap("testdata/todos/create.json");
    }

    public static Map<String, Object> update() {
        return JsonLoader.loadAsMap("testdata/todos/update.json");
    }
}
