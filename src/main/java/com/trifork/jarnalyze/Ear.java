package com.trifork.jarnalyze;

import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.trifork.jarnalyze.markup.ItemList;
import com.trifork.jarnalyze.markup.Markup;

public class Ear implements ApplicationArchive, Container {
    private FileSystem earFS;
    private String archiveName;
    private Map<String, ClassFileCollection> libs = new HashMap<>();
    private List<War> wars = new ArrayList<>();

    public Ear(FileSystem jarFS, String archiveName) {
        this.earFS = jarFS;
        this.archiveName = archiveName;
    }

    public void load() throws IOException, XMLStreamException {
        loadWars();
    }

    private void loadWars() throws IOException, XMLStreamException {
        List<String> webUris = loadApplicationXml();

        for (String webUri: webUris) {
            Path path = earFS.getPath(webUri);
            FileSystem warFS = FileSystems.newFileSystem(path);
            Path path2 = warFS.getRootDirectories().iterator().next();
            War war = new War(path2, webUri, this);
            war.load();
            
            wars .add(war);
        }
    }

    private List<String> loadApplicationXml() throws IOException, XMLStreamException {
        ArrayList<String> webUris = new ArrayList<>();

        Path path = earFS.getPath("META-INF", "application.xml");

        try (ReadableByteChannel rbc = Files.newByteChannel(path, EnumSet.of(StandardOpenOption.READ))) {

            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLEventReader reader = xmlInputFactory.createXMLEventReader(Channels.newInputStream(rbc));

            boolean in = false;
            
            while (reader.hasNext()) {
                XMLEvent ev = reader.nextEvent();
                if (ev.isStartElement()) {
                    StartElement startElement = ev.asStartElement();
                    if (startElement.getName().getLocalPart().equals("web-uri")) {
                        in = true;
                    }
                } else if (ev.isEndElement()) {
                    EndElement endElement = ev.asEndElement();
                    if (endElement.getName().getLocalPart().equals("web-uri")) {
                        in = false;
                    }
                } else if (in) {
                    if (ev.isCharacters()) {
                        String webUri = ev.asCharacters().getData();
                        webUris.add(webUri);
                    }

                }
            }

            return webUris;
        }
    }

    public ClassFileCollection getLibraryClassPath(String path) throws IOException {
        ClassFileCollection col = libs.get(path);
        if (col == null) {
            Path pathToJar = earFS.getPath(path);
            if (!Files.exists(pathToJar)) {
                return null;
            } else {
                FileSystem jarFS = FileSystems.newFileSystem(pathToJar);
                Path rootPathWithinJar = jarFS.getRootDirectories().iterator().next();

                col = new ClassFileCollectionImpl(path, pathToJar, rootPathWithinJar, this);
                libs.put(path, col);
            }
        }
        return col;
    }

    @Override
    public void analyze(CLIOptions options, List<Markup> findings) throws Exception {
        HashSet<ClassFileCollection> accumulatedWarCp = new HashSet<>();
        HashSet<ClassFileCollection> candidatesForSharing = new HashSet<>();

        for (War war: wars) {
            war.analyze(options, findings);
            
            Set<ClassFileCollection> cp = war.getInternalClassPath();
            HashSet<ClassFileCollection> intersection = new HashSet<ClassFileCollection>(cp);
            intersection.retainAll(accumulatedWarCp);
            for(ClassFileCollection cfs: intersection) {
                candidatesForSharing.add(cfs);
            }
            accumulatedWarCp.addAll(cp);
        }
        
        for (ClassFileCollection cfs: candidatesForSharing) {
            Markup finding = new Markup();
            ItemList itemList = finding.headline("Candidate for sharing: " + cfs.getDisplayName()).itemList();
            for (War war: wars) {
                if (war.getInternalClassPath().contains(cfs)) {
                    itemList.item().strong("Contained by " + war);
                }
            }
            
            findings.add(finding);
        }
    }

    @Override
    public String toString() {
        return archiveName;
    }

    public Set<ClassFileCollection> getAccumulatedLibraryClassPath() {
        return new HashSet<ClassFileCollection>(libs.values());
    }
}
