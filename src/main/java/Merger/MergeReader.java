package Merger;

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.container.v0_6.NodeContainer;
import org.openstreetmap.osmosis.core.container.v0_6.WayContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

import java.util.Map;

public class MergeReader implements Sink{

    private MergeWriter _writer;

    public void setWriter(MergeWriter writer){
        _writer = writer;
    }

    @Override
    public void process(EntityContainer entityContainer) {
        if(entityContainer instanceof NodeContainer){

            Node node = ((NodeContainer) entityContainer).getEntity();
            _writer.writeNode(node.getId(),node.getLatitude(),node.getLongitude());
            node = null;

        } else if(entityContainer instanceof WayContainer){

            Way way = ((WayContainer) entityContainer).getEntity();
            _writer.writeWay(way.getId(),way.getTags(),way.getWayNodes());
            way = null;

        }
    }

    @Override
    public void initialize(Map<String, Object> map) {

    }

    @Override
    public void complete() {

    }

    @Override
    public void release() {

    }
}
