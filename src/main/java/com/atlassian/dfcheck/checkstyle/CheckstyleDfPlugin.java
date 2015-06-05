package com.atlassian.dfcheck.checkstyle;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import com.atlassian.dfcheck.DfPlugin;
import com.atlassian.dfcheck.Violation;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class CheckstyleDfPlugin implements DfPlugin
{
    /**
     * Parse a checkstyle report in the following format:
     *
     *   <checkstyle version="6.6">
     *       <file name="src/main/java/Blah.java">
     *           <error line="9" severity="error" message="Empty line" source="com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocParagraphCheck"/>
     *           <error line="15" severity="warning" message="Empty line" source="com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocParagraphCheck"/>
     *       </file>
     *       <file>
     *           <error/>
     *           ...
     *       </file>
     *   </checkstyle>
     * @param checkstyleReport the checkstyle report file in above format
     * @return the parsed check result of the report
     */
    public Map<String, Set<Violation>> parse(File checkstyleReport)
    {
        try
        {
            Map<String, Set<Violation>> fileViolations = Maps.newHashMap();
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(checkstyleReport);
            XPath xpath = XPathFactory.newInstance().newXPath();

            NodeList fileNodes = document.getDocumentElement().getElementsByTagName("file");

            for (int i = 0; i < fileNodes.getLength(); i++)
            {
                Node fileNode = fileNodes.item(i);
                NodeList errors = (NodeList) xpath.evaluate("error[@severity='error']", fileNode, XPathConstants.NODESET);

                if (errors.getLength() > 0)
                {
                    String fileName = fileNode.getAttributes().getNamedItem("name").getNodeValue();
                    Set<Violation> styleViolations = Sets.newHashSet();

                    for (int j = 0; j < errors.getLength(); j++)
                    {
                        Node error = errors.item(j);

                        String linenum = error.getAttributes().getNamedItem("line").getNodeValue();
                        String message = error.getAttributes().getNamedItem("message").getNodeValue();
                        String source = error.getAttributes().getNamedItem("source").getNodeValue();

                        styleViolations.add(new Violation(fileName, linenum, message, "Checkstyle:" + source));
                    }

                    fileViolations.put(fileName, styleViolations);
                }
            }

            return fileViolations;

        }
        catch (ParserConfigurationException e)
        {
            throw new RuntimeException("Parser configuration problem", e);
        }
        catch (SAXException e)
        {
            throw new RuntimeException(e.getMessage(), e);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e.getMessage(), e);
        }
        catch (XPathExpressionException e)
        {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
