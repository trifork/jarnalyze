package com.trifork.jarnalyze;

import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

public class CLIOptions {
	@Option(name="-v", usage="Be verbose about what's going on")
	boolean verbose;

	@Option(name="-sharedlibs", usage="Assume ear libs are loaded by a shared classloader")
	boolean assumeSharedEarClassLoader;
	
	@Option(name="-color", usage="Use ANSI color codes in output")
	boolean enableConsoleColors;
	
	@Argument
	List<String> arguments = new ArrayList<String>();
}
