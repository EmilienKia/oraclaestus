package com.github.emilienkia.oraclaestus;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class Identifier implements Comparable<Identifier> {

    List<String> prefix = new ArrayList<String>();

    List<String> path = new ArrayList<String>();

    transient String normalized = null;

    public Identifier() {
    }

    public Identifier(List<String> prefix, List<String> path) {
        this.prefix = prefix;
        this.path = path;
        // TODO check validity
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

    public int getLength() {
        return path.size();
    }

    public boolean isSimple() {
        return prefix.size()==0 && path.size() == 1;
    }

    public String getLast() {
        if (path.isEmpty()) {
            return null;
        }
        return path.get(path.size() - 1);
    }

    public boolean hasPrefix() {
        return !prefix.isEmpty();
    }

    public List<String> getPrefixAsList() {
        return new ArrayList<>(prefix);
    }

    public String getPrefix() {
        if (prefix.isEmpty()) {
            return "";
        }
        return String.join(".", prefix);
    }

    public Identifier getPrefixAsIdentifier() {
        return new Identifier(prefix);
    }

    public Identifier withoutPrefix() {
        return new Identifier(path);
    }

    private void normalize() {
        if(normalized == null) {
            normalized = "";
            if(!prefix.isEmpty()) {
                normalized = String.join(".", prefix) + ":" + String.join(".", path);
            } else {
                normalized = String.join(".", path);
            }
        }
    }

    public String toString() {
        normalize();
        return normalized;
    }

    private static final Predicate<String> stringNotBlank = Predicate.not(String::isBlank);

    public static Identifier fromString(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            return new Identifier();
        }

        int pos = identifier.indexOf(':');
        if( pos != -1) {
            // Handle prefix
            String prefixPart = identifier.substring(0, pos).trim();
            String pathPart = identifier.substring(pos + 1).trim();
            List<String> prefixList = Arrays.stream(prefixPart.split("\\.")).map(String::trim).filter(stringNotBlank).toList();
            List<String> pathList = Arrays.stream(pathPart.split("\\.")).map(String::trim).filter(stringNotBlank).toList();
            return new Identifier(prefixList, pathList);
        } else {
            identifier = identifier.trim();
            List<String> pathList = Arrays.stream(identifier.split("\\.")).map(String::trim).filter(stringNotBlank).toList();
            return new Identifier(pathList);
        }
    }

    public int compareTo(String s) {
        if (s == null) {
            // This is greater than null
            return 1;
        }
        return compareTo(fromString(s));
    }

    private static int compare(@NonNull List<String> ls1, @NonNull List<String> ls2) {
        int minLength = Math.min(ls1.size(), ls2.size());
        for (int i = 0; i < minLength; i++) {
            int cmp = ls1.get(i).compareTo(ls2.get(i));
            if (cmp != 0) {
                // Return the first non-equal comparison
                return cmp;
            }
        }
        // Compare lengths if all previous elements are equal
        return Integer.compare(ls1.size(), ls2.size());
    }

    @Override
    public int compareTo(@NonNull Identifier o) {
        if (o == null) {
            // This is greater than null
            return 1;
        }

        int comp = compare(this.prefix, o.prefix);
        if (comp != 0) {
            return comp;
        }
        return compare(this.path, o.path);
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
        normalize();
        return normalized.hashCode();
    }

}
