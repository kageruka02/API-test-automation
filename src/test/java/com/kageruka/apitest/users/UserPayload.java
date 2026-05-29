package com.kageruka.apitest.users;

import com.kageruka.apitest.utils.JsonLoader;
import java.util.Map;

public class UserPayload {
    public static Map<String, Object> create() {
        return JsonLoader.loadAsMap("testdata/users/create.json");
    }

    public static Map<String, Object> update() {
        return JsonLoader.loadAsMap("testdata/users/update.json");
    }
}
