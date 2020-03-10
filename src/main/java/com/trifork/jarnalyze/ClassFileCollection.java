package com.trifork.jarnalyze;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

public interface ClassFileCollection {
    Set<String> getClasses();
    String getChecksum() throws NoSuchAlgorithmException, IOException;
}
