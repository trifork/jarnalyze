package com.trifork.jarnalyze;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.trifork.jarnalyze.markup.Markup;

public class SpringBootApp extends AbstractApplicationArchive {

    private String archiveName;
    private Path root;
    private Set<ClassFileCollection> internalClassPath = new HashSet<>();

    public SpringBootApp(Path root, String archiveName) {
        this.root = root;
        this.archiveName = archiveName;
    }

    public void load() throws IOException {
        try {
            loadBootInfLib();
            loadBootInfClasses();
        } catch (NotDirectoryException e) {
            throw new IllegalArgumentException("This doesn't look like a spring-boot fat jar (expected to find /BOOT-INF/classes and/or /BOOT-INF/lib)");
        }
    }

    private void loadBootInfClasses() throws IOException {
        Path path = root.resolve(root.getFileSystem().getPath("BOOT-INF", "classes"));
        internalClassPath.add(new ClassFileCollectionImpl("BOOT-INF/classes/", null, path, this));
    }

    private void loadBootInfLib() throws IOException {
        Path path = root.resolve(root.getFileSystem().getPath("BOOT-INF", "lib"));
        Set<Path> jarFiles = listJarFiles(path);

        for (Path jarFilePath : jarFiles) {
            Path resolvedPath = path.resolve(jarFilePath);
            FileSystem jarFS = FileSystems.newFileSystem(resolvedPath);

            internalClassPath.add(new ClassFileCollectionImpl("BOOT-INF/lib/" + jarFilePath.getFileName(), resolvedPath,
                    jarFS.getRootDirectories().iterator().next(), this));
        }
    }

    public Set<Path> listJarFiles(Path root) throws IOException {
        Set<Path> jarFileList = new HashSet<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(root)) {
            for (Path path : stream) {
                if (path.getFileName().toString().endsWith(".jar")) {
                    jarFileList.add(path.getFileName());
                }
            }
        }
        return jarFileList;
    }

    @Override
    public void analyze(CLIOptions options, List<Markup> findings) throws Exception {
        detectInternalClashes(options, findings);
    }

    private void detectInternalClashes(CLIOptions options, List<Markup> findings)
            throws NoSuchAlgorithmException, IOException {

        checkPermutations(options, findings, internalClassPath);
    }

    public Set<ClassFileCollection> getInternalClassPath() {
        return internalClassPath;
    }

    public String toString() {
        return archiveName;
    }
}