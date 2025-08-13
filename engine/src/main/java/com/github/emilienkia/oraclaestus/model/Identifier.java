package com.github.emilienkia.oraclaestus.model;

import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Identifier implements Comparable<Identifier> {

    List<String> path = new ArrayList<String>();

    transient String normalizedPath = null;

    public Identifier() {
    }

    public Identifier(List<String> path) {
        this.path = path;
        // TODO check validity
    }

    public Identifier(String... path) {
        this(Arrays.asList(path));
    }

    public List<String> getPath() {
        return new ArrayList<>(path);
    }

    public boolean isValid() {
        return !path.isEmpty() && path.stream().allMatch(part -> part != null && !part.isEmpty());
        // TODO add check content validity
    }

    public String getLast() {
        if (path.isEmpty()) {
            return null;
        }
        return path.get(path.size() - 1);
    }

    private void normalizePath() {
        if(normalizedPath == null) {
            normalizedPath = String.join(".", path);
        }
    }

    public String toString() {
        normalizePath();
        return normalizedPath;
    }

    public static Identifier fromString(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            return new Identifier();
        }
        identifier = identifier.trim();
        String[] parts = identifier.split("\\.");
        return new Identifier(Arrays.asList(parts));
    }

    public int compareTo(String s) {
        if (s == null) {
            // This is greater than null
            return 1;
        }
        return compareTo(fromString(s));
    }

    @Override
    public int compareTo(@NonNull Identifier o) {
        if (o == null) {
            // This is greater than null
            return 1;
        }
        int minLength = Math.min(this.path.size(), o.path.size());
        for (int i = 0; i < minLength; i++) {
            int cmp = this.path.get(i).compareTo(o.path.get(i));
            if (cmp != 0) {
                // Return the first non-equal comparison
                return cmp;
            }
        }
        // Compare lengths if all previous elements are equal
        return Integer.compare(this.path.size(), o.path.size());
    }

    public boolean equals(String str) {
        return compareTo(str) == 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof String str) return compareTo(str) == 0;
        if (!(obj instanceof Identifier other)) return false;
        return compareTo(other) == 0;
    }

    @Override
    public int hashCode() {
        normalizePath();
        return normalizedPath.hashCode();
    }

}
