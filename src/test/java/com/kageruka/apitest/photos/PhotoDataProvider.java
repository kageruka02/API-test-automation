package com.kageruka.apitest.photos;

import com.fasterxml.jackson.core.type.TypeReference;
import com.kageruka.apitest.utils.JsonLoader;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class PhotoDataProvider {
    static Stream<Arguments> validPhotoIds() {
        List<Map<String, Object>> data = JsonLoader.loadAs(
                "testdata/photos/valid_photos.json",
                new TypeReference<List<Map<String, Object>>>() {}
        );
        return data.stream().map(map -> Arguments.of(
                ((Number) map.get("photoId")).intValue(),
                ((Number) map.get("expectedAlbumId")).intValue()
        ));
    }

    static Stream<Arguments> invalidPhotoIds() {
        List<Integer> data = JsonLoader.loadAs(
                "testdata/photos/invalid_photos.json",
                new TypeReference<List<Integer>>() {}
        );
        return data.stream().map(Arguments::of);
    }
}
