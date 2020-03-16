package com.trifork.jarnalyze;

import java.util.regex.Pattern;

public abstract class AbstractApplicationArchive implements ApplicationArchive {
    private Pattern includePattern;
    private Pattern excludePattern;

    @Override
    public void setIncludePattern(Pattern includePattern) {
        this.includePattern = includePattern;
    }
    
    @Override
    public Pattern getIncludePattern() {
        return includePattern;
    }

    @Override
    public void setExcludePattern(Pattern excludePattern) {
        this.excludePattern = excludePattern;
    }
    
    @Override
    public Pattern getExcludePattern() {
        return excludePattern;
    }
}
