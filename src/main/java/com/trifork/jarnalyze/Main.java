package com.trifork.jarnalyze;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.xml.stream.XMLStreamException;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import com.trifork.jarnalyze.markup.Markup;
import com.trifork.jarnalyze.renderer.ColoredConsoleRenderer;
import com.trifork.jarnalyze.renderer.ConsoleRenderer;
import com.trifork.jarnalyze.renderer.HtmlRenderer;
import com.trifork.jarnalyze.renderer.Renderer;

/**
 * @author Jeppe Sommer
 */
public class Main {

    public static void main(String[] args) throws Exception {
        new Main().doMain(args);
    }

    private ApplicationArchive rootArchive;

    public void doMain(String[] args) throws Exception {
        CLIOptions options = new CLIOptions();
        CmdLineParser parser = new CmdLineParser(options);

        if (args.length < 1) {
            parser.printUsage(System.out);
            System.exit(-1);
        }

        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            return;
        }

        try {
            validateOptions(options);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }

        try {
            load(options);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        analyze(options);
    }

    private void validateOptions(CLIOptions options) throws InvalidArgumentException {
        if (options.arguments.size() != 1) {
            throw new InvalidArgumentException(
                    "Expected exactly one archive argument, got " + options.arguments.size());
        }

        for (String argument : options.arguments) {
            if (!argument.endsWith(".ear") && !argument.endsWith(".war")) {
                throw new InvalidArgumentException("Only .ear and .war files are currently supported");
            }

            if (!new File(argument).canRead()) {
                throw new InvalidArgumentException("Cannot read archive \"" + argument + "\"");
            }
        }
    }

    private void load(CLIOptions options) throws IOException, XMLStreamException {

        String archive = options.arguments.get(0);

        Path archivePath = Paths.get(archive);

        FileSystem jarFS = FileSystems.newFileSystem(archivePath);

        if (archive.endsWith(".ear")) {
            Ear ear = new Ear(jarFS, archive);
            ear.load();
            rootArchive = ear;
        } else if (archive.endsWith(".war")) {
            War war = new War(jarFS.getRootDirectories().iterator().next(), archive, null);
            war.load();
            rootArchive = war;
        }
    }

    private void analyze(CLIOptions options) throws Exception {
        ArrayList<Markup> findings = new ArrayList<>();
        rootArchive.analyze(options, findings);

        OutputStream out = System.out;
        
        Renderer renderer;
        switch (options.outputFormat) {
        case CONSOLE:
        default:
            renderer = new ConsoleRenderer(out);
            break;
        case COLORCONSOLE:
            WindowsConsoleSupport.enableWindowsConsoleColors();
            renderer = new ColoredConsoleRenderer(out);
            break;
        case HTML: 
            renderer = new HtmlRenderer(out);
        }

        for (Markup finding: findings) {
            finding.visit(renderer);
        }

        renderer.close();
    }
}

