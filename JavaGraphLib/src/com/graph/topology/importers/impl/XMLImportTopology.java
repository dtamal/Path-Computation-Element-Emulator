package com.graph.topology.importers.impl;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import jdsl.graph.api.Vertex;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.graph.elements.edge.EdgeElement;
import com.graph.elements.edge.params.EdgeParams;
import com.graph.elements.edge.params.impl.BasicEdgeParams;
import com.graph.elements.vertex.VertexElement;
import com.graph.graphcontroller.Gcontroller;
import com.graph.topology.importers.ImportTopology;

public class XMLImportTopology extends ImportTopology {

    private static String getTextValue(Element ele, String tagName) {
	String textVal = null;
	NodeList nl = ele.getElementsByTagName(tagName);
	if (nl != null && nl.getLength() > 0) {
	    Element el = (Element) nl.item(0);
	    textVal = el.getFirstChild().getNodeValue();
	}

	return textVal;
    }

    @Override
    public void importTopology(Gcontroller graph, String filename) {

    }

    private static void addVertex(Gcontroller graph, String id) {
	VertexElement vertex = new VertexElement(id, graph, 0, 0);
	graph.addVertex(vertex);
    }

    private static void addEdge(Gcontroller graph, String id, String sourceID, String destID) {

	VertexElement source = new VertexElement(sourceID, graph, 0, 0);
	VertexElement dest = new VertexElement(destID, graph, 0, 0);

	EdgeElement edge = new EdgeElement(id, source, dest, graph);
	EdgeParams params = new BasicEdgeParams(edge, 0, 1, 40);
	edge.setEdgeParams(params);
	graph.addEdge(edge);
    }

    @Override
    public void importTopologyFromString(Gcontroller graph, String[] topology) {
	// TODO Auto-generated method stub

    }

    private static void extractNodes(Gcontroller graph, String filename) {
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

	try {

	    // Using factory get an instance of document builder
	    DocumentBuilder db = dbf.newDocumentBuilder();

	    // parse using builder to get DOM representation of the XML file
	    Document dom = db.parse(filename);

	    Element docEle = dom.getDocumentElement();
	    NodeList domainNodeList = docEle.getElementsByTagName("domain");

	    if (domainNodeList != null && domainNodeList.getLength() > 0) {

		for (int i = 0; i < domainNodeList.getLength(); i++) {
		    Element el = (Element) domainNodeList.item(i);

		    NodeList nodeNodeList = el.getElementsByTagName("node");

		    if (nodeNodeList != null && nodeNodeList.getLength() > 0) {

			for (int j = 0; j < nodeNodeList.getLength(); j++) {
			    Element nodeElement = (Element) nodeNodeList.item(j);

			    NodeList routerIdNodeList = nodeElement.getElementsByTagName("router_id");
			    Element routerIdElement = (Element) routerIdNodeList.item(0);

			    String nodeID = routerIdElement.getFirstChild().getNodeValue();
			    addVertex(graph, nodeID);
			}
		    }

		    // break;
		}
	    }
	} catch (ParserConfigurationException pce) {
	    pce.printStackTrace();
	} catch (SAXException se) {
	    se.printStackTrace();
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	}

    }

    private static void extractLinks(Gcontroller graph, String filename) {
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

	try {

	    // Using factory get an instance of document builder
	    DocumentBuilder db = dbf.newDocumentBuilder();

	    // parse using builder to get DOM representation of the XML file
	    Document dom = db.parse(filename);
	    Element docEle = dom.getDocumentElement();

	    NodeList domainNodeList = docEle.getElementsByTagName("domain");

	    if (domainNodeList != null && domainNodeList.getLength() > 0) {

		for (int i = 0; i < domainNodeList.getLength(); i++) {

		    Element el = (Element) domainNodeList.item(i);
		    NodeList edgeTypeNodeList = el.getElementsByTagName("edge");

		    if (edgeTypeNodeList != null && edgeTypeNodeList.getLength() > 0) {

			for (int j = 0; j < edgeTypeNodeList.getLength(); j++) {
			    Element edgeTypeElement = (Element) edgeTypeNodeList.item(j);
			    NodeList sourceNodeList = edgeTypeElement.getElementsByTagName("source");
			    NodeList destinationNodeList = edgeTypeElement.getElementsByTagName("destination");

			    String source = "";
			    String dest = "";

			    if (sourceNodeList != null && sourceNodeList.getLength() > 0) {
				Element sourceElement = (Element) sourceNodeList.item(0);

				NodeList routerIdNodeList = sourceElement.getElementsByTagName("router_id");
				Element routerIdElement = (Element) routerIdNodeList.item(0);

				source = routerIdElement.getFirstChild().getNodeValue();

			    }
			    if (destinationNodeList != null && destinationNodeList.getLength() > 0) {
				Element destinationElement = (Element) destinationNodeList.item(0);

				NodeList routerIdNodeList = destinationElement.getElementsByTagName("router_id");
				Element routerIdElement = (Element) routerIdNodeList.item(0);

				dest = routerIdElement.getFirstChild().getNodeValue();
			    }

			    addEdge(graph, source + "-" + dest, source, dest);

			}
		    }

		}
	    }
	} catch (ParserConfigurationException pce) {
	    pce.printStackTrace();
	} catch (SAXException se) {
	    se.printStackTrace();
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	}

    }

}
