package Eidi._123Fastq.Cli;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class Arguments {
    static final class Usage extends IllegalArgumentException {
        Usage(String message) {
            super(message);
        }
    }

    private final Map<String, List<String>> values = new HashMap<>();
    private final Set<String> flags = new HashSet<>();

    static Arguments parse(String[] tokens, String[] options, String[] switches, String... repeatable) {
        List<String> allowedOptions = Arrays.asList(options);
        List<String> allowedFlags = Arrays.asList(switches);
        List<String> repeating = Arrays.asList(repeatable);
        Arguments arguments = new Arguments();
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            if (!token.startsWith("--")) {
                throw new Usage("Unexpected argument '" + token + "'.");
            }
            int equals = token.indexOf('=');
            String key = token.substring(2, equals < 0 ? token.length() : equals);
            if (allowedFlags.contains(key) || key.equals("quiet")) {
                if (equals >= 0 || !arguments.flags.add(key)) {
                    throw new Usage("--" + key + " is a flag and can appear once.");
                }
            } else if (allowedOptions.contains(key)) {
                List<String> entries = arguments.values.computeIfAbsent(key, ignored -> new ArrayList<>());
                if (!entries.isEmpty() && !repeating.contains(key)) {
                    throw new Usage("--" + key + " can appear once.");
                }
                String value = equals >= 0 ? token.substring(equals + 1) : null;
                if (value == null && i + 1 < tokens.length && !tokens[i + 1].startsWith("--")) {
                    value = tokens[++i];
                }
                if (value == null || value.isEmpty()) {
                    throw new Usage("Provide a value for --" + key + ".");
                }
                entries.add(value);
            } else {
                throw new Usage("Unknown option --" + key + ".");
            }
        }
        return arguments;
    }

    boolean flag(String key) {
        return flags.contains(key);
    }

    boolean has(String key) {
        return values.containsKey(key);
    }

    List<String> all(String key) {
        return values.getOrDefault(key, Collections.emptyList());
    }

    String value(String key, String fallback) {
        return has(key) ? all(key).get(0) : fallback;
    }

    String required(String key) {
        String value = value(key, null);
        if (value == null) {
            throw new Usage("Missing required option --" + key + ".");
        }
        return value;
    }

    Path path(String key) {
        return Paths.get(required(key));
    }

    Path optionalPath(String key) {
        return has(key) ? path(key) : null;
    }

    List<Path> paths(String key) {
        List<Path> paths = new ArrayList<>();
        for (String item : all(key)) {
            paths.add(Paths.get(item));
        }
        if (paths.isEmpty()) {
            required(key);
        }
        return paths;
    }

    int integer(String key, int fallback, int minimum, int maximum) {
        return has(key) ? number("--" + key, required(key), minimum, maximum) : fallback;
    }

    static int number(String label, String value, int minimum, int maximum) {
        try {
            int parsed = Integer.parseInt(value);
            if (parsed >= minimum && parsed <= maximum) {
                return parsed;
            }
        } catch (NumberFormatException ignored) {
        }
        throw new Usage(label + " must be an integer from " + minimum + " to " + maximum + ".");
    }
}
