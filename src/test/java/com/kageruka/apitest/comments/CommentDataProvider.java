package com.kageruka.apitest.comments;

import com.fasterxml.jackson.core.type.TypeReference;
import com.kageruka.apitest.utils.JsonLoader;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class CommentDataProvider {
    static Stream<Arguments> validCommentIds() {
        List<Map<String, Object>> data = JsonLoader.loadAs(
                "testdata/comments/valid_comments.json",
                new TypeReference<List<Map<String, Object>>>() {}
        );
        return data.stream().map(map -> Arguments.of(
                ((Number) map.get("commentId")).intValue(),
                ((Number) map.get("expectedPostId")).intValue()
        ));
    }

    static Stream<Arguments> invalidCommentIds() {
        List<Integer> data = JsonLoader.loadAs(
                "testdata/comments/invalid_comments.json",
                new TypeReference<List<Integer>>() {}
        );
        return data.stream().map(Arguments::of);
    }
}
