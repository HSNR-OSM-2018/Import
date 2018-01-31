package Filter;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.osmosis.core.container.v0_6.*;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

public class NodeMarker implements Sink{

    private Map<Long,NodeInfo> _nodes;

    public NodeMarker(){
        _nodes = new HashMap<Long, NodeInfo>();
    }

    public void process(EntityContainer entityContainer) {
        if(entityContainer instanceof NodeContainer){

            Node node = ((NodeContainer) entityContainer).getEntity();
            NodeInfo info = new NodeInfo(node.getLatitude(),node.getLongitude());
            _nodes.put(node.getId(),info);
            node = null;

        } else if(entityContainer instanceof WayContainer){

            Way way = ((WayContainer) entityContainer).getEntity();
            _nodes.get(way.getWayNodes().get(0).getNodeId()).setMarked(true);
            _nodes.get(way.getWayNodes().get(way.getWayNodes().size()-1).getNodeId()).setMarked(true);
            way = null;

        } else if(entityContainer instanceof RelationContainer){
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
        System.out.println("Map länge: " + _nodes.size());
    }

    public Map<Long, NodeInfo> getNodesMap(){
        return _nodes;
    }
}
