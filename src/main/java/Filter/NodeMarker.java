package Filter;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.openstreetmap.osmosis.core.container.v0_6.*;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

public class NodeMarker implements Sink {

    private Logger logger = Logger.getLogger(NodeMarker.class.getSimpleName());
    private double nodeCount = 0;
    private double wayCount = 0;

    private Map<Long, NodeInfo> _nodes;

    public NodeMarker() {
        _nodes = new HashMap<>();
    }

    public void process(EntityContainer entityContainer) {
        if (entityContainer instanceof NodeContainer) {
            if (Importer.STATS) {
                nodeCount++;
                if (nodeCount % 100000 == 0) {
                    logger.info(String.format("processed %1$.0f nodes", nodeCount));
                }
            }
            Node node = ((NodeContainer) entityContainer).getEntity();
            NodeInfo info = new NodeInfo(node.getLatitude(), node.getLongitude());
            _nodes.put(node.getId(), info);
            node = null;

        } else if (entityContainer instanceof WayContainer) {
            if (Importer.STATS) {
                wayCount++;
                if (wayCount % 100000 == 0) {
                    logger.info(String.format("processed %1$.0f ways", wayCount));
                }
            }
            Way way = ((WayContainer) entityContainer).getEntity();
            _nodes.get(way.getWayNodes().get(0).getNodeId()).setMarked(true);
            _nodes.get(way.getWayNodes().get(way.getWayNodes().size() - 1).getNodeId()).setMarked(true);
            way = null;

        } else if (entityContainer instanceof RelationContainer) {
            //Not needed
        } else {
            System.out.println("Unkown Entity");
        }
    }

    public void initialize(Map<String, Object> map) {
    }

    public void complete() {
    }

    public void release() {
        logger.info(String.format("processed %1$.0f nodes", nodeCount));
        logger.info(String.format("processed %1$.0f ways", wayCount));
        System.out.println("Map länge: " + _nodes.size());
    }

    public Map<Long, NodeInfo> getNodesMap() {
        return _nodes;
    }
}
