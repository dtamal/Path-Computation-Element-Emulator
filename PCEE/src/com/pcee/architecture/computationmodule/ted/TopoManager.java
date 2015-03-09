package com.pcee.architecture.computationmodule.ted;

import com.topology.importers.ImportTopology;
import com.topology.primitives.NetworkElement;
import com.topology.primitives.TopologyElement;
import com.topology.primitives.TopologyManager;
import com.topology.primitives.exception.FileFormatException;
import com.topology.primitives.exception.TopologyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by Fran on 3/7/2015.
 */
public class TopoManager {

    private static final Logger log = LoggerFactory.getLogger(TopologyManager.class);

    //Port on which to listen for topology Information Updates
    private static int topologyUpdatePort = 5189;

    //Thread for topology Update Listener
    private static Thread topologyUpdateThread;

    // Static oject instance of the TopologyInformation Class
    static private TopoManager _instance;

    //Topology manager instance
    private TopologyManager manager;

    // Topology Importer used to populate the manager instance
    private ImportTopology importer;

    /**
     * default constructor
     */

    private TopoManager() {
        importer = new SNDLibImportTopology();
        manager = getTopologyManager();

        try {
            importer.importFromFile("./PCEE/sndlib/abilene.xml", manager);
        } catch (TopologyException e) {
            log.error("Topology Exception while parsing test.topology file", e);
            fail("Topology Exception while parsing test.topology file");
        } catch (FileFormatException e) {
            log.error("FileFormat Exception while parsing test.topology file", e);
            fail("FileFormat Exception while parsing test.topology file");
        } catch (IOException e) {
            log.error("IO Exception while parsing test.topology file", e);
            fail("IO Exception while parsing test.topology file");
        }
        /**
         *
         */
//    startTopologyUpdateListener();
        /**
         *
         */
    }

//    @Test
    public void testGetFunctions() {

        TopologyManager manager = getTopologyManager();
        log.info("Creating network element");

        NetworkElement element = null;
        try {
            element = manager.createNetworkElement();
        } catch (TopologyException e) {
            fail("Error while creating network element: " + e.getMessage());
        }

        //Element was created successfully, test get functions
        try {
            log.info("testing getElementByID(id)");
            assertTrue(element.equals(manager.getElementByID(element.getID())));
            log.info("testing getElementByID(id, Class<T>)");
            assertTrue(element.equals(manager.getElementByID(element.getID(), NetworkElement.class)));
            log.info("testing getAllElementByID(Class<T>)");
            assert (manager.getAllElements(TopologyElement.class).contains(element));
            log.info("testing getAllElementByID(Class<T>)");
            assert (manager.getAllElements(NetworkElement.class).contains(element));
        } catch (TopologyException e) {
            fail("Error while fetching test.topology elements from the test.topology manager: " + e.getMessage());
        }

        //Creating connection point
        log.info("Creating connection point and port");
        try {
            element = manager.createNetworkElement();
        } catch (TopologyException e) {
            fail("Error while creating network element: " + e.getMessage());
        }

    }

    protected TopologyManager getTopologyManager() {
        return (TopologyManager) TopoManagerHelper.getInstance();
    }

    /**
     * Function to set the port for topology Updates
     *
     * @param port
     */
    public static void setTopologyUpdatePort(int port) {
        topologyUpdatePort = port;
    }

    public void setImporter() {
        importer = new SNDLibImportTopology();
    }

    public TopologyManager getManager() {
        return manager;
    }

    public void setManager(TopologyManager manager) {
        this.manager = manager;
    }

    public ImportTopology getImporter() {
        return importer;
    }

    public void setImporter(ImportTopology importer) {
        this.importer = importer;
    }

    public static TopoManager get_instance() {
        if (_instance == null)
            _instance = new TopoManager();
        return _instance;
    }


    public static void closeInstance() {
        if (_instance != null)
            _instance = null;
    }
}
