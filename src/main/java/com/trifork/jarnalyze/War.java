package com.trifork.jarnalyze;

import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.NoSuchAlgorithmException;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.Manifest;

public class War implements ApplicationArchive, Container {

    private String archiveName;
    private Path root;
    private Ear parentEar;
    private Set<ClassFileCollection> externalClassPath = new HashSet<>();
    private Set<ClassFileCollection> internalClassPath = new HashSet<>();

    public War(Path root, String archiveName, Ear parent) {
        this.root = root;
        this.archiveName = archiveName;
        this.parentEar = parent;
    }

    public void load() throws IOException {
        loadExternalClasspath();

        loadWebInfLib();

        loadWebInfClasses();
    }

    private void loadWebInfClasses() throws IOException {
        Path path = root.resolve(root.getFileSystem().getPath("WEB-INF", "classes"));
        internalClassPath.add(new ClassFileCollectionImpl("WEB-INF/classes/", null, path, this));
    }

    private void loadWebInfLib() throws IOException {
        Path path = root.resolve(root.getFileSystem().getPath("WEB-INF", "lib"));
        Set<Path> jarFiles = listJarFiles(path);

        for (Path jarFilePath : jarFiles) {
            Path resolvedPath = path.resolve(jarFilePath);
            FileSystem jarFS = FileSystems.newFileSystem(resolvedPath);

            internalClassPath.add(new ClassFileCollectionImpl("WEB-INF/lib/" + jarFilePath.getFileName(), resolvedPath,
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

    private void loadExternalClasspath() throws IOException {
        if (parentEar != null) {
            Path path = root.resolve(root.getFileSystem().getPath("META-INF", "MANIFEST.MF"));

            try (ReadableByteChannel rbc = Files.newByteChannel(path, EnumSet.of(StandardOpenOption.READ))) {
                // read file
                Manifest manifest = new Manifest(Channels.newInputStream(rbc));

                String classPathAttr = manifest.getMainAttributes().getValue("Class-Path");

                if (classPathAttr != null) {
                    String[] classPath = classPathAttr.split(" ");

                    for (String jarFilePath : classPath) {

                        ClassFileCollection libCp = parentEar.getLibraryClassPath(jarFilePath);
                        if (libCp == null) {
                            Findings.log.warning(
                                    this.archiveName + " has an unresolved Class-Path reference to " + jarFilePath);
                        }
                        externalClassPath.add(libCp);
                    }
                }
            }
            System.out.println("Size of external cp for " + this + " is " + externalClassPath.size());
        }
    }

    @Override
    public void analyze(CLIOptions options) throws Exception {
        detectInternalClashes(options);
        detectExternalClashes(options);
    }

    private void detectInternalClashes(CLIOptions options) throws NoSuchAlgorithmException, IOException {

        for (ClassFileCollection cfs1 : internalClassPath) {
            for (ClassFileCollection cfs2 : internalClassPath) {
                if (cfs1 == cfs2) {
                    continue;
                }

                checkIntersection(cfs1, cfs2);
            }
        }
    }

    private void detectExternalClashes(CLIOptions options) throws NoSuchAlgorithmException, IOException {
        for (ClassFileCollection cfs1 : internalClassPath) {
            Set<ClassFileCollection> externalCp = options.assumeSharedEarClassLoader
                    ? parentEar.getAccumulatedLibraryClassPath()
                    : externalClassPath;
            for (ClassFileCollection cfs2 : externalCp) {
                if (cfs1 == cfs2) {
                    continue;
                }

                checkIntersection(cfs1, cfs2);
            }
        }
    }

    private void checkIntersection(ClassFileCollection cfs1, ClassFileCollection cfs2)
            throws NoSuchAlgorithmException, IOException {
        Set<String> intersection = new HashSet<String>(cfs2.getClasses()); // use
                                                                           // the
                                                                           // copy
                                                                           // constructor
        intersection.retainAll(cfs1.getClasses());
        if (intersection.size() > 0) {
            if (isIdentical(cfs1, cfs2)) {
                Findings.log.info("Duplicated resource " + System.lineSeparator() + "    " + cfs1 + " and "
                        + System.lineSeparator() + "    " + cfs2);

            } else {
                Findings.log.info("Clash between " + System.lineSeparator() + "    " + cfs1 + " and "
                        + System.lineSeparator() + "    " + cfs2);

                for (String clash : intersection) {
                    Findings.log.fine("        - clashing on " + clash);
                }
            }
        }
    }

    private boolean isIdentical(ClassFileCollection cfs1, ClassFileCollection cfs2)
            throws NoSuchAlgorithmException, IOException {
        String checksum1 = cfs1.getChecksum();
        String checksum2 = cfs2.getChecksum();

        return checksum1.equals(checksum2);
    }

    public Set<ClassFileCollection> getInternalClassPath() {
        return internalClassPath;
    }

    public String toString() {
        return archiveName;
    }
}