package com.trifork.jarnalyze;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.trifork.jarnalyze.markup.Item;
import com.trifork.jarnalyze.markup.ItemList;
import com.trifork.jarnalyze.markup.Markup;

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

    protected void checkPermutations(CLIOptions options, List<Markup> findings,
            Set<ClassFileCollection> classFileCollection) throws NoSuchAlgorithmException, IOException {
        // Check all pair permutations of internalClassPath:
        List<ClassFileCollection> list = new ArrayList<>(classFileCollection);
        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {

                ClassFileCollection cfs1 = list.get(i);
                ClassFileCollection cfs2 = list.get(j);

                Markup finding = checkIntersection(options, cfs1, cfs2);
                if (finding != null) {
                    findings.add(finding);
                }
            }
        }
    }

    protected Markup checkIntersection(CLIOptions options, ClassFileCollection cfs1, ClassFileCollection cfs2)
            throws NoSuchAlgorithmException, IOException {

        Markup finding = null;

        Set<String> intersection = new HashSet<String>(cfs2.getClasses());

        intersection.retainAll(cfs1.getClasses());
        if (intersection.size() > 0) {
            if (isIdentical(cfs1, cfs2)) {

                finding = new Markup();
                finding.warningText("Duplicate resource ").itemList().item().strong(cfs1).item().strong(cfs2);

            } else {
                finding = new Markup().errorText("Clash detected");
                Item item = finding.itemList().item().strong(cfs1).item().strong(cfs2);

                if (options.verbose) {
                    ItemList itemList = item.itemList();
                    for (String clash : intersection) {
                        itemList.item().plain("clashing on " + clash);
                    }
                }
            }
        }
        return finding;
    }

    private boolean isIdentical(ClassFileCollection cfs1, ClassFileCollection cfs2)
            throws NoSuchAlgorithmException, IOException {
        String checksum1 = cfs1.getChecksum();
        String checksum2 = cfs2.getChecksum();

        return checksum1.equals(checksum2);
    }

}
