package utils;

import org.apache.commons.io.FileUtils;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class DocsHandler {
    public static String parseMD(String fileName) {
        String text = "";
        try {
            File file = new File("docs/" + fileName);
            text = FileUtils.readFileToString(file, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Extension> extensions = Arrays.asList(TablesExtension.create());
        Parser parser = Parser.builder()
                .extensions(extensions)
                .build();
        HtmlRenderer renderer = HtmlRenderer.builder()
                .extensions(extensions)
                .build();
        Node document = parser.parse(text);
        String test = renderer.render(document);
        return renderer.render(document);
    }
}
