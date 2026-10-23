package org.p1_funcionario.util;

import java.nio.file.Paths;

public class PathFXML {
    public static String pathBase() {
        return Paths.get("src/main/java/org/p1-funcionario/view").toAbsolutePath().toString();
    }
}
