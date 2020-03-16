package com.trifork.jarnalyze;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class ClassFileCollectionImpl implements ClassFileCollection {

    private String displayName;
    private Set<String> classFileList;
    private ApplicationArchive container;
    private Path resourcePath;
    private String checksum;

    public ClassFileCollectionImpl(String displayName, Path resourcePath, Path root, ApplicationArchive container) throws IOException {
        this.displayName = displayName;
        this.resourcePath = resourcePath;
        this.container = container;

        loadClassList(root);
    }

    @Override
    public Set<String> getClasses() {
        return classFileList;
    }

    private void loadClassList(Path root) throws IOException {
        classFileList = new HashSet<>();

        Stream<Path> stream = Files.walk(root).filter(Files::isRegularFile);

        if (container.getIncludePattern() != null) {
            stream = stream.filter(f -> container.getIncludePattern().matcher(f.toString()).matches());
        }
        if (container.getExcludePattern() != null) {
            stream = stream.filter(f -> !container.getExcludePattern().matcher(f.toString()).matches());
        }
        
        stream.forEach(path -> classFileList.add(path.toString()));
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }
    
    public String toString() {
        return displayName + " in " + container.toString();
    }

    @Override
    public String getChecksum() throws NoSuchAlgorithmException, IOException {
        if (checksum == null) {
            checksum = calcChecksum();
        }
        return checksum;
    }

    private String calcChecksum() throws NoSuchAlgorithmException, IOException {
        byte[] b = Files.readAllBytes(resourcePath);
        byte[] hash = MessageDigest.getInstance("MD5").digest(b);

        String digest = Arrays.toString(hash);
        return digest;
    }

    @Override
    public int hashCode() {
        if (resourcePath != null) {
            try {
                return getChecksum().hashCode();
            } catch (NoSuchAlgorithmException | IOException e) {
                e.printStackTrace();
            }
        }
        return displayName.hashCode();
    }
    
    @Override
    public boolean equals(Object other) {
        try {
            return ((ClassFileCollection)other).getChecksum().equals(getChecksum());
        } catch (Exception e) {
            return false;
        }
    }
}
