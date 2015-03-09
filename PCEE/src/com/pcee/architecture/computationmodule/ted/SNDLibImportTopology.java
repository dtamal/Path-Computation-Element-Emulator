package com.pcee.architecture.computationmodule.ted;

import com.helpers.benchmark.annotation.Benchmark;
import com.topology.importers.ImportTopology;
import com.topology.primitives.*;
import com.topology.primitives.exception.FileFormatException;
import com.topology.primitives.exception.TopologyException;
import com.topology.primitives.properties.keys.TEPropertyKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class SNDLibImportTopology implements ImportTopology {

  private static final Logger log = LoggerFactory.getLogger(com.topology.impl.importers.sndlib.SNDLibImportTopology.class);

  private void createNodes(Document doc, TopologyManager manager) throws FileFormatException, TopologyException {
    NodeList list = doc.getElementsByTagName("nodes");
    if (list.getLength()!=1) {
      throw new FileFormatException("The document should only have one tag with the list of all network elements");
    }

    //create nodes
    NodeList nesList = list.item(0).getChildNodes();
    for (int i=0;i<nesList.getLength(); i++) {
      Node ne = nesList.item(i);
      if (ne.getNodeType() == Node.ELEMENT_NODE) {
        //Create the network element
        Element neVals = (Element) ne;
        String label = neVals.getAttribute("id");
        NetworkElement node = manager.createNetworkElement();
        node.setLabel(label);
        log.info("Generating new network element. Label: " + node.getLabel());

        //Populate coordinates
        NodeList coordTagList = neVals.getElementsByTagName("coordinates");
        if ((coordTagList!=null) && (coordTagList.getLength()>0)) {
          Node coords = coordTagList.item(0);
          if (coords.getNodeType()== Node.ELEMENT_NODE) {
            node.addProperty(TEPropertyKey.XCOORD, Double.parseDouble(((Element)coords).getElementsByTagName("x").item(0).getTextContent()));
            node.addProperty(TEPropertyKey.YCOORD, Double.parseDouble(((Element) coords).getElementsByTagName("y").item(0).getTextContent()));
            log.info("Coordinates for node: " + node.getLabel() + ", (X, Y): (" + node.getProperty(TEPropertyKey.XCOORD) + ", " + node.getProperty(TEPropertyKey.YCOORD) + ")");
          }
        }

        //generate a single port in the network element
        ConnectionPoint cp = manager.createPort(node);
        cp.setLabel(node.getLabel());
        log.info("Port Created: " + cp.getLabel());
      }
    }

  }

  private void createLinks(Document doc, TopologyManager manager) throws FileFormatException, TopologyException {
    NodeList list = doc.getElementsByTagName("links");
    if (list.getLength()!=1) {
      throw new FileFormatException("The document should only have one tag with the list of all network elements");
    }

    //create nodes
    NodeList linksList = list.item(0).getChildNodes();
    for (int i=0;i<linksList.getLength(); i++) {
      Node linkDesc = linksList.item(i);
      if (linkDesc.getNodeType() == Node.ELEMENT_NODE) {
        Element linkVals = (Element) linkDesc;
        String label = linkVals.getAttribute("id");

        //Get link endpoints
        String aEndLabel = linkVals.getElementsByTagName("source").item(0).getTextContent();
        String zEndLabel = linkVals.getElementsByTagName("target").item(0).getTextContent();
        Port aEnd = manager.getSingleElementByLabel(aEndLabel, Port.class);
        Port zEnd = manager.getSingleElementByLabel(zEndLabel, Port.class);

        Link link = manager.createLink(aEnd.getID(), zEnd.getID());
        link.setLabel(label);
        link.setDirected(false);
        log.info("New link created from " + aEnd.getLabel() + " to " + zEnd.getLabel());
      }
    }
  }

  @Override
  public void importFromFile(String fileName, TopologyManager manager) throws TopologyException, FileFormatException, IOException {
    log.info("Starting scan of test.topology based on the SNDLib XML test.topology format");
    File topoFile = new File(fileName);
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = null;
    Document doc = null;
    try {
      builder = factory.newDocumentBuilder();
      doc = builder.parse(topoFile);
    } catch (ParserConfigurationException e) {
      log.error("Error while setting up XML parser", e);
      throw new FileFormatException(e.getMessage());
    } catch (SAXException e) {
      log.error("File Parsing Exception", e);
      throw new FileFormatException(e.getMessage());
    }
    //Document should not be null
    if (doc!=null) {
      //Start scan of elements
      doc.getDocumentElement().normalize();
//      createNodes(doc, manager);
//      createLinks(doc, manager);
        createLinks(doc,manager, createIPNodes(doc, manager));
    } else {
      log.error("No document found");
    }
  }

    private HashMap<String,String> createIPNodes(Document doc, TopologyManager manager) throws FileFormatException, TopologyException {
        NodeList list = doc.getElementsByTagName("nodes");
        if (list.getLength() != 1) {
            throw new FileFormatException("The document should only have one tag with the list of all network elements");
        }

        HashMap<String,String> nodeNameToIP = new HashMap<String,String>();
        //create nodes
        NodeList nesList = list.item(0).getChildNodes();
        int nodeCounter = 1;
        for (int i = 0; i < nesList.getLength(); i++) {
            Node ne = nesList.item(i);
            if (ne.getNodeType() == Node.ELEMENT_NODE) {
                //Create the network element
                Element neVals = (Element) ne;
                String label = neVals.getAttribute("id");
                NetworkElement node = manager.createNetworkElement();
                String ipLabel = "192.169.2." + nodeCounter++;
                nodeNameToIP.put(label, ipLabel);
                node.setLabel(ipLabel);
                log.info("Generating new network element. Label: " + node.getLabel());

                //Populate coordinates
                NodeList coordTagList = neVals.getElementsByTagName("coordinates");
                if ((coordTagList != null) && (coordTagList.getLength() > 0)) {
                    Node coords = coordTagList.item(0);
                    if (coords.getNodeType() == Node.ELEMENT_NODE) {
                        node.addProperty(TEPropertyKey.XCOORD, Double.parseDouble(((Element) coords).getElementsByTagName("x").item(0).getTextContent()));
                        node.addProperty(TEPropertyKey.YCOORD, Double.parseDouble(((Element) coords).getElementsByTagName("y").item(0).getTextContent()));
                        log.info("Coordinates for node: " + node.getLabel() + ", (X, Y): (" + node.getProperty(TEPropertyKey.XCOORD) + ", " + node.getProperty(TEPropertyKey.YCOORD) + ")");
                    }
                }

                //generate a single port in the network element
                ConnectionPoint cp = manager.createPort(node);
                cp.setLabel(node.getLabel());

                log.info("Port Created: " + cp.getLabel());
            }
        }
        return nodeNameToIP;
    }

    private void createLinks(Document doc, TopologyManager manager,  HashMap<String,String> nodeNameToIP) throws FileFormatException, TopologyException {
        NodeList list = doc.getElementsByTagName("links");
        if (list.getLength() != 1) {
            throw new FileFormatException("The document should only have one tag with the list of all network elements");
        }

        //create nodes
        NodeList linksList = list.item(0).getChildNodes();
        for (int i = 0; i < linksList.getLength(); i++) {
            Node linkDesc = linksList.item(i);
            if (linkDesc.getNodeType() == Node.ELEMENT_NODE) {
                Element linkVals = (Element) linkDesc;
                String label = linkVals.getAttribute("id");

                //Get link endpoints
                String aEndLabel = linkVals.getElementsByTagName("source").item(0).getTextContent();
                String zEndLabel = linkVals.getElementsByTagName("target").item(0).getTextContent();

                Port aEnd = manager.getSingleElementByLabel(nodeNameToIP.get(aEndLabel), Port.class);
                Port zEnd = manager.getSingleElementByLabel(nodeNameToIP.get(zEndLabel), Port.class);

                Link link = manager.createLink(aEnd.getID(), zEnd.getID());
                link.setLabel(label);
                link.setDirected(false);
                log.info("New link created from " + aEnd.getLabel() + " to " + zEnd.getLabel());
            }
        }
    }
}
