package com.example.translation.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum AdminStatus {
    SUCCESS("success"),
    PENDING("pending"),
    REJECT("reject");

    private final String value;

    @JsonValue
    public String toValue() {
        return this.getValue(); // or fail
    }

    private static final Map<String, AdminStatus> lookup =
            Arrays.stream(values()).collect(Collectors.toMap(AdminStatus::getValue, Function.identity()));

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static AdminStatus forValue(String type) {
        if (type == null) {
            return null;
        }
        return lookup.get(type);
    }
}
