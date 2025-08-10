package com.github.emilienkia.oraclaestus.model;

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
    }

    public Identifier(String... path) {
        this.path.addAll(Arrays.asList(path));
    }

    public List<String> getPath() {
        return new ArrayList<>(path);
    }

    public boolean isValid() {
        return !path.isEmpty() && path.stream().allMatch(part -> part != null && !part.isEmpty());
    }

    public boolean isOld() {
        return !path.isEmpty() && path.getFirst()!=null && path.getFirst().startsWith("~");
    }

    public Identifier getNewIdentifier() {
        if(isOld()) {
            List<String> newPath = new ArrayList<>(this.path);
            newPath.set(0, newPath.get(0).substring(1)); // Remove the leading '~'
            return new Identifier(newPath);
        } else {
            return this;
        }
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
        if (identifier == null || identifier.isEmpty()) {
            return new Identifier();
        }
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
