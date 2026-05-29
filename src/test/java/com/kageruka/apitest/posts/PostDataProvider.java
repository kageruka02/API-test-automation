package com.kageruka.apitest.posts;

import com.fasterxml.jackson.core.type.TypeReference;
import com.kageruka.apitest.utils.JsonLoader;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class PostDataProvider {
    static Stream<Arguments> validPostIds() {
        List<Map<String, Object>> data = JsonLoader.loadAs(
                "testdata/posts/valid_posts.json",
                new TypeReference<List<Map<String, Object>>>() {}
        );
        return data.stream().map(map -> Arguments.of(
                ((Number) map.get("postId")).intValue(),
                ((Number) map.get("expectedUserId")).intValue(),
                map.get("expectedTitleStart")
        ));
    }

    static Stream<Arguments> invalidPostIds(){
        List<Integer> data = JsonLoader.loadAs(
                "testdata/posts/invalid_posts.json",
                new TypeReference<List<Integer>>() {}
        );
        return data.stream().map(Arguments::of);
    }
}
