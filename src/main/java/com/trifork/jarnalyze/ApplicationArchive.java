package com.trifork.jarnalyze;

import java.util.List;
import java.util.regex.Pattern;

import com.trifork.jarnalyze.markup.Markup;

public interface ApplicationArchive {
    void analyze(CLIOptions options, List<Markup> findings) throws Exception;
    void setIncludePattern(Pattern includePattern);
    Pattern getIncludePattern();
    void setExcludePattern(Pattern excludePattern);
    Pattern getExcludePattern();
    void load() throws Exception;
}
