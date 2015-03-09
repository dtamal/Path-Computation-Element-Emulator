package com.pcee.architecture.computationmodule.ted;

import com.topology.impl.primitives.TopologyManagerFactoryImpl;
import com.topology.primitives.TopologyManager;
import com.topology.primitives.TopologyManagerFactory;
import com.topology.primitives.exception.TopologyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopoManagerHelper {

  private static final Logger log = LoggerFactory.getLogger(TopoManagerHelper.class);

  private static TopologyManagerFactory factory = new TopologyManagerFactoryImpl();

  private static final String defaultTopoManagerId = "123";

    public static TopologyManager getInstance() {
      try {
        if (factory.hasTopologyManager(defaultTopoManagerId))
          return factory.getTopologyManager(defaultTopoManagerId);
        else
          return factory.createTopologyManager(defaultTopoManagerId);
      } catch (TopologyException e) {
        log.error("Error in creating a topology manager from factory implementation", e);
      }
      return null;
    }
}
