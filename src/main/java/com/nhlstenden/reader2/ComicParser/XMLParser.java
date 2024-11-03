package com.nhlstenden.reader2.ComicParser;

import com.nhlstenden.reader2.models.Serie;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class XMLParser {

    public Serie parseXml(File xmlFile) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            NodeList nodeList = document.getElementsByTagName("ComicInfo");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);

                String series = getElementTextContent(element, "Series");
                String number = getElementTextContent(element, "Number");
                String web = getElementTextContent(element, "Web");
                String summary = getElementTextContent(element, "Summary");
                String notes = getElementTextContent(element, "Notes");
                String publisher = getElementTextContent(element, "Publisher");
                String genre = getElementTextContent(element, "Genre");
                String pageCount = getElementTextContent(element, "PageCount");
                String languageISO = getElementTextContent(element, "LanguageISO");
                String author = getElementTextContent(element, "writer");

                // String Name, String Description, Boolean Completed, Boolean Favorite, Integer CurrentChapter, boolean Read, String Gerne

                return new Serie(
                        series,
                        summary,
                        false,
                        false,
                        null,
                        false,
                        genre
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getElementTextContent(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return null;
    }
}