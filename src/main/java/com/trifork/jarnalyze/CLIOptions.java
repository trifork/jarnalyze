package com.trifork.jarnalyze;

import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

public class CLIOptions {
	@Option(name="-v", usage="Be verbose about what's going on")
	boolean verbose;
	
	@Argument
	List<String> arguments = new ArrayList<String>();
}
