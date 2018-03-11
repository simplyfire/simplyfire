package controllers;

import models.DocPage;
import org.apache.commons.io.FileUtils;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import play.mvc.Controller;
import play.mvc.Result;
import utils.DocsHandler;
import views.html.doc.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class DocsController extends Controller {

    private final List<DocPage> pageList = Arrays.asList(
            new DocPage(routes.DocsController.getstarted().url(), "Get Started"),
            new DocPage(routes.DocsController.auth().url(), "Auth"),
            new DocPage(routes.DocsController.project().url(), "Project"),
            new DocPage(routes.DocsController.file().url(), "File")
    );


    public Result getstarted() {
        return ok(index.render(pageList, pageList.get(0), getstarted.render()));
    }

    public Result auth() {
        return ok(index.render(pageList, pageList.get(1), auth.render()));
    }

    public Result project() {
        return ok(index.render(pageList, pageList.get(2), project.render()));
    }

    public Result file() {
        return ok(index.render(pageList, pageList.get(3), file.render()));
    }
}
