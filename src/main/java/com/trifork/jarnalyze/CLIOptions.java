package com.trifork.jarnalyze;

import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

import com.trifork.jarnalyze.renderer.OutputFormat;

public class CLIOptions {
	@Option(name="-v", usage="Be verbose about what's going on")
	boolean verbose;

	@Option(name="-sharedlibs", usage="Assume ear libs are loaded by a shared classloader")
	boolean assumeSharedEarClassLoader;
	
	@Option(name="-format", usage="Specify output format")
	OutputFormat outputFormat;
	
	@Option(name="-include", usage="Include pattern for resources (regex format)")
	String includePattern;
	
    @Option(name="-exclude", usage="Exclude pattern for resources (regex format)")
    String excludePattern;

	@Argument
	List<String> arguments = new ArrayList<String>();
}
