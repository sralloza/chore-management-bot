package models;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Accessors(chain = true)
public class CallbackQueryData implements Serializable {
    private QueryType type;
    private String weekId;
    private String choreType;
    private Integer queryMessageId;
    private Long tenantId;


    public String encode() {
        return Stream.of(type, weekId, choreType, queryMessageId, tenantId)
            .map(CallbackQueryData::toString)
            .collect(Collectors.joining("/"));
    }

    public static CallbackQueryData decode(String data) {
        List<String> parts = Arrays.stream(data.split("/")).
            map(CallbackQueryData::fromString).collect(Collectors.toList());

        return new CallbackQueryData()
            .setType(getOrNull(parts, 0, QueryType::valueOf))
            .setWeekId(getOrNull(parts, 1, Function.identity()))
            .setChoreType(getOrNull(parts, 2, Function.identity()))
            .setQueryMessageId(getOrNull(parts, 3, Integer::parseInt))
            .setTenantId(getOrNull(parts, 4, Long::parseLong));
    }

    private static String toString(Object o) {
        return o == null ? "" : o.toString();
    }

    private static String fromString(String input) {
        return input.isBlank() ? null : input;
    }

    private static <T> T getOrNull(List<String> parts, int index, Function<String, T> parser) {
        return parts.size() > index ? parser.apply(parts.get(index)) : null;
    }
}
