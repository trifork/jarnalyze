package com.trifork.jarnalyze;

import java.util.List;

import com.trifork.jarnalyze.markup.Markup;

public interface ApplicationArchive {
    void analyze(CLIOptions options, List<Markup> findings) throws Exception;
}
