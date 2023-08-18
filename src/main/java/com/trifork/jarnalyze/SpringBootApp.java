package com.trifork.jarnalyze;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.trifork.jarnalyze.markup.Markup;

public class SpringBootApp extends AbstractApplicationArchive {

    private String archiveName;
    private Path root;
    private Ear parentEar;
    private Set<ClassFileCollection> externalClassPath = new HashSet<>();
    private Set<ClassFileCollection> internalClassPath = new HashSet<>();

    public SpringBootApp(Path root, String archiveName, Ear parent) {
        this.root = root;
        this.archiveName = archiveName;
        this.parentEar = parent;
    }

    public void load() throws IOException {
        loadBootInfLib();

        loadBootInfClasses();
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
        detectExternalClashes(options, findings);
    }

    private void detectInternalClashes(CLIOptions options, List<Markup> findings)
            throws NoSuchAlgorithmException, IOException {

        checkPermutations(options, findings, internalClassPath);
    }

    private void detectExternalClashes(CLIOptions options, List<Markup> findings)
            throws NoSuchAlgorithmException, IOException {
        Set<ClassFileCollection> externalCp = options.assumeSharedEarClassLoader
                ? parentEar.getAccumulatedLibraryClassPath()
                : externalClassPath;
        for (ClassFileCollection cfs1 : internalClassPath) {
            for (ClassFileCollection cfs2 : externalCp) {
                if (cfs1 == cfs2) {
                    continue;
                }

                Markup finding = checkIntersection(options, cfs1, cfs2);
                if (finding != null) {
                    findings.add(finding);
                }
            }
        }
        if (!options.assumeSharedEarClassLoader) {
            // If no shared classloader, we need to check if the manifest classpath of this
            // war has conflict.
            // If shared classloader, conflicts will be detected in Ear
            checkPermutations(options, findings, externalClassPath);
        }
    }

    public Set<ClassFileCollection> getInternalClassPath() {
        return internalClassPath;
    }

    @Override
    public Pattern getIncludePattern() {
        if (parentEar != null) {
            return parentEar.getIncludePattern();
        }
        return super.getIncludePattern();
    }

    @Override
    public Pattern getExcludePattern() {
        if (parentEar != null) {
            return parentEar.getExcludePattern();
        }
        return super.getExcludePattern();
    }

    public String toString() {
        return archiveName;
    }
}