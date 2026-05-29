package com.kageruka.apitest.users;

import com.fasterxml.jackson.core.type.TypeReference;
import com.kageruka.apitest.utils.JsonLoader;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class UserDataProvider {
    static Stream<Arguments> validUserIds() {
        List<Map<String, Object>> data = JsonLoader.loadAs(
                "testdata/users/valid_users.json",
                new TypeReference<List<Map<String, Object>>>() {}
        );
        return data.stream().map(map -> Arguments.of(
                ((Number) map.get("userId")).intValue(),
                map.get("expectedName"),
                map.get("expectedUsername")
        ));
    }

    static Stream<Arguments> invalidUserIds() {
        List<Integer> data = JsonLoader.loadAs(
                "testdata/users/invalid_users.json",
                new TypeReference<List<Integer>>() {}
        );
        return data.stream().map(Arguments::of);
    }
}
