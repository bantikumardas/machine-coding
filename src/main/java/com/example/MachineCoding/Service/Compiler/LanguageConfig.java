package com.example.MachineCoding.Service.Compiler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LanguageConfig {
    public record LangSpec(
            String dockerImage,   // Docker image to use
            String fileName,      // source file name inside container
            String[] compileCmd,  // null if interpreted
            String[] runCmd       // command to execute
    ) {}

    private static final Map<String, LangSpec> CONFIGS = new HashMap<>();

    static {
        CONFIGS.put("java", new LangSpec(
                "openjdk:17-slim",
                "Main.java",
                new String[]{"javac", "Main.java"},
                new String[]{"java", "Main"}
        ));

        CONFIGS.put("python", new LangSpec(
                "python:3.11-slim",
                "main.py",
                null,
                new String[]{"python3", "main.py"}
        ));

        CONFIGS.put("javascript", new LangSpec(
                "node:18-slim",
                "main.js",
                null,
                new String[]{"node", "main.js"}
        ));

        CONFIGS.put("cpp", new LangSpec(
                "gcc:12",
                "main.cpp",
                new String[]{"g++", "-o", "main", "main.cpp"},
                new String[]{"./main"}
        ));

        CONFIGS.put("c", new LangSpec(
                "gcc:12",
                "main.c",
                new String[]{"gcc", "-o", "main", "main.c"},
                new String[]{"./main"}
        ));

        CONFIGS.put("go", new LangSpec(
                "golang:1.21-alpine",
                "main.go",
                null,
                new String[]{"go", "run", "main.go"}
        ));

        CONFIGS.put("rust", new LangSpec(
                "rust:1.74-slim",
                "main.rs",
                new String[]{"rustc", "-o", "main", "main.rs"},
                new String[]{"./main"}
        ));

        CONFIGS.put("ruby", new LangSpec(
                "ruby:3.2-slim",
                "main.rb",
                null,
                new String[]{"ruby", "main.rb"}
        ));

        CONFIGS.put("php", new LangSpec(
                "php:8.2-cli",
                "main.php",
                null,
                new String[]{"php", "main.php"}
        ));

        CONFIGS.put("bash", new LangSpec(
                "bash:5.2",
                "main.sh",
                null,
                new String[]{"bash", "main.sh"}
        ));
    }

    public static Optional<LangSpec> get(String language) {
        return Optional.ofNullable(CONFIGS.get(language.toLowerCase().trim()));
    }

    public static boolean isSupported(String language) {
        return CONFIGS.containsKey(language.toLowerCase().trim());
    }
}
