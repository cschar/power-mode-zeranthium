package com.cschar.pmode3.tools;

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.util.DocTrees;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.StandardDoclet;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;


public class JsonConfigMarkdownDoclet2 extends StandardDoclet {

    public void printElement(DocTrees trees, Element e) {
        DocCommentTree docCommentTree = trees.getDocCommentTree(e);
        if (docCommentTree != null) {

            if(e.toString().contains("loadJSONConfig")){
//                System.out.println("===================[FOUND DOCSTRING for loadJSONConfig]====================");

                for( DocTree blockTag : docCommentTree.getBlockTags()){
//                System.out.println("\t" + blockTag.getClass().getName());
//                System.out.println("\t" + blockTag.getKind());
//                System.out.println("\t" + blockTag);

                    var type = blockTag.toString().split(" ", 2)[0];
                    var content = blockTag.toString().split(" ", 2)[1];
//                    System.out.println("\t" + "type: \n\t\t" + type);
//                    System.out.println("\t" + "content: \n\t\t" + content);

                    if ( type.equals("@val1") ||
                         type.equals("@val2") ||
                         type.equals("@val3")) {
                           System.out.println("``` \n" + content + "\n```\n");
                    }

                    if ( type.equals("@exampleConfig")) {
                        System.out.println("```json \n" + content + "\n```\n");
                    }
                }

            }

//            System.out.println("Element (" + e.getKind() + ": "
//                    + e + ") has the following comments:");
//            System.out.println("Entire body: \n " + docCommentTree.getFullBody());
//            System.out.println("Block tags:");
//            for( DocTree blockTag : docCommentTree.getBlockTags()){
//                System.out.println("\t" + blockTag);
//            }
        }
    }


    @Override
    public boolean run(DocletEnvironment docEnv) {
//        reporter.print(Kind.NOTE, "overviewfile: " + overviewfile);
        // get the DocTrees utility class to access document comments
        DocTrees docTrees = docEnv.getDocTrees();
        //DocTree is a tag
//        docEnv.getJavaFileManager().



        System.out.println("\n\n ===== processing ===== ");
//        for(Element e : docEnv.getIncludedElements()){
//            System.out.println("elements: " + e.getKind().toString() + " : " + e.getSimpleName());
//
//            if (e.getKind().toString().equals("PACKAGE")) {
//                System.out.println("\t---- package found ---");
//                for(Element i : e.getEnclosedElements()){
//                    System.out.println("\telement: " + i.getKind() + " : " + i.getSimpleName());
//                }
//            }
//
//            if (e.getKind().toString().equals("MODULE")) {
//                System.out.println("\t---- module found ---");
//                for(Element i : e.getEnclosedElements()){
//                    System.out.println("\telement: " + i.getKind() + " : " + i.getSimpleName());
//                }
//            }
//        }

//        System.out.println(docEnv.getClass().descriptorString());



        for (TypeElement t : ElementFilter.typesIn(docEnv.getIncludedElements())) {
//            System.out.println(t.getKind() + ":" + t);
            for (Element e : t.getEnclosedElements()) {
                printElement(docTrees, e);
            }

//            printElement(docTrees, t.getEnclosingElement());


        }
        return true;
    }

    @Override
    public String getName() {
        return "JsonConfigMarkdownDoclet";
    }

}
