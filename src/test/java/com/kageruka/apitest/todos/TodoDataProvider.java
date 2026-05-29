package com.kageruka.apitest.todos;

import com.fasterxml.jackson.core.type.TypeReference;
import com.kageruka.apitest.utils.JsonLoader;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class TodoDataProvider {

    static Stream<Arguments> validTodoIds() {
        List<Map<String, Object>> data = JsonLoader.loadAs(
                "testdata/todos/valid_todos.json",
                new TypeReference<List<Map<String, Object>>>() {}
        );
        return data.stream().map(map -> Arguments.of(
                ((Number) map.get("todoId")).intValue(),
                ((Number) map.get("expectedUserId")).intValue(),
                map.get("expectedCompleted")
        ));
    }
    static Stream<Arguments> invalidTodoIds() {
        List<Integer> data = JsonLoader.loadAs(
                "testdata/todos/invalid_todos.json",
                new TypeReference<List<Integer>>() {}
        );
        return data.stream().map(Arguments::of);
    }
}
