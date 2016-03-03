package org.efix.util.generator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.compare;

public class FieldGenerator {

    private static final String PACKAGE = "org.efix.message.field";
    private static final String PACKAGE_PATH = PACKAGE.replace('.', '/');
    private static final String TAG_CLASS_NAME = "Tag";

    private static final Comparator<Map.Entry<String, String>> BY_VALUE = (o1, o2) -> {
        String v1 = o1.getValue();
        String v2 = o2.getValue();
        return v1.length() == v2.length() ? v1.compareTo(v2) : compare(v1.length(), v2.length());
    };

    private final File inFile;
    private final File outDirectory;

    public FieldGenerator(File inFile, File outDirectory) {
        this.inFile = inFile;
        this.outDirectory = outDirectory;
    }

    private void generate() throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inFile);

        Element fieldsElement = (Element) doc.getElementsByTagName("fields").item(0);
        NodeList fields = fieldsElement.getElementsByTagName("field");

        Map<String, String> tags = new HashMap<>(1024);
        for (int i = 0; i < fields.getLength(); i++) {
            Element field = (Element) fields.item(i);
            String fieldName = field.getAttribute("name");
            String fieldNumber = field.getAttribute("number");
            String fieldType = field.getAttribute("type");

            if (tags.containsKey(fieldName))
                throw new IllegalArgumentException("field with this name already exists " + fieldName);

            tags.put(fieldName, fieldNumber);

            NodeList values = field.getElementsByTagName("value");
            if (values.getLength() > 0)
                generateValueClass(fieldName, fieldType, values);
        }

        generateTagClass(tags);
    }


    private void generateValueClass(String className, String fieldType, NodeList valueList) throws IOException {
        Map<String, String> values = new HashMap<>();
        for (int j = 0; j < valueList.getLength(); j++) {
            Element element = (Element) valueList.item(j);
            String valueName = element.getAttribute("description");
            String value = element.getAttribute("enum");
            if (values.containsKey(valueName))
                throw new IllegalArgumentException("value with this name already exists " + valueName);

            values.put(valueName, value);
        }

        switch (fieldType) {
            case "CHAR":
                generateByteValueClass(className, values);
                break;
            case "INT":
            case "NUMINGROUP":
            case "LENGTH":
                generateIntValueClass(className, values);
                break;
            case "COUNTRY":
            case "CURRENCY":
            case "EXCHANGE":
            case "LANGUAGE":
            case "MULTIPLECHARVALUE":
            case "MULTIPLESTRINGVALUE":
            case "STRING":
                if (canPresentAsByteValues(values))
                    generateByteValueClass(className, values);
                else
                    generateByteSequenceValueClass(className, values);
                break;
            default:
                throw new UnsupportedOperationException("unsupported field type " + fieldType);
        }
    }

    private void generateTagClass(Map<String, String> tags) throws IOException {
        generateIntValueClass(TAG_CLASS_NAME, tags);
    }

    private void generateByteValueClass(String className, Map<String, String> values) throws IOException {
        String header = String.format("package %s;\n\n\npublic class %s {\n\n", PACKAGE, className);

        StringBuilder body = new StringBuilder(1024);
        values.entrySet().stream().sorted(BY_VALUE).forEach(entry -> {
            String valueName = entry.getKey();
            String value = entry.getValue();
            body.append("    public static final byte ").append(valueName).append(" = '").append(value).append("';\n");
        });


        String footer = "\n}";
        String content = header + body + footer;

        writeClass(className, content);
    }

    private void generateIntValueClass(String className, Map<String, String> values) throws IOException {
        String header = String.format("package %s;\n\n\npublic class %s {\n\n", PACKAGE, className);

        StringBuilder body = new StringBuilder(1024);
        values.entrySet().stream().sorted(BY_VALUE).forEach(entry -> {
            String valueName = entry.getKey();
            String value = entry.getValue();
            body.append("    public static final int ").append(valueName).append(" = ").append(value).append(";\n");
        });


        String footer = "\n}";
        String content = header + body + footer;

        writeClass(className, content);
    }

    private void generateByteSequenceValueClass(String className, Map<String, String> values) throws IOException {
        String header = String.format("package %s;\n\n" +
                        "import ByteSequence;\n\n" +
                        "import static ByteSequenceWrapper.of;\n\n\n" +
                        "public class %s {\n\n",
                PACKAGE, className);

        StringBuilder body = new StringBuilder(1024);
        values.entrySet().stream().sorted(BY_VALUE).forEach(entry -> {
            String valueName = entry.getKey();
            String value = entry.getValue();

            body.append("    public static final ByteSequence ")
                    .append(valueName).append(" = of(\"").append(value).append("\");\n");
        });


        String footer = "\n}";
        String content = header + body + footer;

        writeClass(className, content);
    }

    private boolean canPresentAsByteValues(Map<String, String> values) {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            if (entry.getValue().length() > 1)
                return false;
        }

        return true;
    }

    private void writeClass(String name, String content) throws IOException {
        File directory = new File(outDirectory, PACKAGE_PATH);
        File file = new File(directory, name + ".java");
        if (!directory.exists() && !directory.mkdirs())
            throw new IllegalStateException("Cant create directory " + directory);

        try (Writer writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        }
    }

    public static void main(String[] args) throws Exception {
        File inFile = new File(args[0]);
        File outDirectory = new File(args[1]);

        new FieldGenerator(inFile, outDirectory).generate();
    }

}
