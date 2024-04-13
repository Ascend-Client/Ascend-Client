package io.github.betterclient.fabric.accesswidener;

import java.util.ArrayList;
import java.util.List;

public class AccessWidener {
    public List<String> classes = new ArrayList<>();
    public List<AccessWidenerFieldOrMethod> fields = new ArrayList<>();
    public List<AccessWidenerFieldOrMethod> methods = new ArrayList<>();

    public AccessWidener(String src) {
        for (String line : src.split("\n")) {
            if(line.startsWith("accessible\t") || line.startsWith("transitive-accessible\t")) {
                String[] linee = line.split("\t");
                String className = linee[2].replace('/', '.');

                if(linee[1].equals("class")) {
                    classes.add(className);
                } else if(linee[1].equals("field")) {
                    fields.add(new AccessWidenerFieldOrMethod(className, linee[3], linee[4]));
                } else if(linee[1].equals("method")) {
                    methods.add(new AccessWidenerFieldOrMethod(className, linee[3], linee[4]));
                }
            }
        }
    }
}
