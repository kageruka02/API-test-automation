package com.kageruka.apitest.albums;

import com.fasterxml.jackson.core.type.TypeReference;
import com.kageruka.apitest.utils.JsonLoader;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class AlbumDataProvider {
    static Stream<Arguments> validAlbumIds() {
        List<Map<String, Object>> data = JsonLoader.loadAs(
                "testdata/albums/valid_albums.json",
                new TypeReference<List<Map<String, Object>>>() {}
        );
        return data.stream().map(map -> Arguments.of(
                ((Number) map.get("albumId")).intValue(),
                ((Number) map.get("expectedUserId")).intValue(),
                map.get("expectedTitleStart")
        ));
    }

    static Stream<Arguments> invalidAlbumIds() {
        List<Integer> data = JsonLoader.loadAs(
                "testdata/albums/invalid_albums.json",
                new TypeReference<List<Integer>>() {}
        );
        return data.stream().map(Arguments::of);
    }
}
