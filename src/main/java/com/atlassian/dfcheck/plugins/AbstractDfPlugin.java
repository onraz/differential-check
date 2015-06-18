package com.atlassian.dfcheck.plugins;

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

import com.atlassian.dfcheck.core.DfPlugin;
import com.atlassian.dfcheck.core.Violation;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Abstract Differential Check Plugin Template
 * <p>
 * To be used when a plugin groups errors by a source file, i.e. when the following hierarchy is implied:
 * </p>
 * <pre>
 * {@code
 *  <PluginReport>
 *      <FileElement>
 *          <ErrorElement></ErrorElement>
 *          <ErrorElement></ErrorElement>
 *          ...
 *      </FileElement>
 *      <FileElement>
 *          ...
 *      </FileElement>
 *  </PluginReport>
 * }
 * </pre>
 * <p>
 * If a plugin doesn't group errors by file, consider implementing the {@link DfPlugin} interface directly.
 * </p>
 */
public abstract class AbstractDfPlugin implements DfPlugin
{
    public final Map<String, Set<Violation>> parse(File pluginReport)
    {
        try
        {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(pluginReport);
            XPath xpath = XPathFactory.newInstance().newXPath();

            NodeList fileNodes = (NodeList) xpath.evaluate(getFileElementXpath(), document, XPathConstants.NODESET);

            Map<String, Set<Violation>> fileViolations = Maps.newHashMap();
            for (int i = 0; i < fileNodes.getLength(); i++)
            {
                Node fileNode = fileNodes.item(i);
                NodeList errors = (NodeList) xpath.evaluate(getViolationElementXpath(), fileNode, XPathConstants.NODESET);
                // if the source file has errors, go through them and create violations
                if (errors.getLength() > 0)
                {
                    String fileName = getFileNameFromFileNode(fileNode);
                    Set<Violation> styleViolations = Optional.fromNullable(fileViolations.get(fileName))
                                                                .or(Sets.<Violation>newHashSet());
                    for (int j = 0; j < errors.getLength(); j++)
                    {
                        Node errorNode = errors.item(j);
                        styleViolations.add(constructViolation(fileNode, errorNode));
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
            throw new RuntimeException("Invalid Xpath Expression: " + e.getMessage(), e);
        }
    }

    /**
     * Construct a violation object representing the error reported for the fileNode.
     *
     * @param fileNode the source file which has an error reported by this plugin
     * @param errorNode the error element contains the details of the error reported against the file
     * @return the violation object representing the error reported for the fileNode
     */
    protected abstract Violation constructViolation(Node fileNode, Node errorNode);

    /**
     * @param fileNode the node representing a file element
     * @return the file name represented by the fileNode
     */
    protected abstract String getFileNameFromFileNode(Node fileNode);

    /**
     * @return the xpath expression that yields the file level element
     */
    protected abstract String getFileElementXpath();

    /**
     * @return the xpath expression that yields the violation (e.g. bugs/errors) elements
     */
    protected abstract String getViolationElementXpath();
}
