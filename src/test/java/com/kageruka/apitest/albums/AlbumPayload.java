package com.kageruka.apitest.albums;

import com.kageruka.apitest.utils.JsonLoader;
import java.util.Map;

public class AlbumPayload {
    public static Map<String, Object> create() {
        return JsonLoader.loadAsMap("testdata/albums/create.json");
    }

    public static Map<String, Object> update() {
        return JsonLoader.loadAsMap("testdata/albums/update.json");
    }
}
