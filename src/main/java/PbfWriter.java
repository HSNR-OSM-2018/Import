import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openstreetmap.osmosis.core.container.v0_6.NodeContainer;
import org.openstreetmap.osmosis.core.container.v0_6.WayContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.*;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.core.task.v0_6.Source;


public class PbfWriter implements Source{

    private int mCountWay = 0;
    private int mCountNode = 0;

    private Sink _sink;

    public void setSink(Sink sink) {
        _sink = sink;
    }

    public void writeNodes(Map<Long,NodeInfo> nodes){
        for (Map.Entry<Long, NodeInfo> entry : nodes.entrySet())
        {
            writeNode(entry.getKey(),entry.getValue().Lat(),entry.getValue().Lon());
        }
    }

    private void writeNode(Long id, double lat, double lon){
        _sink.process(new NodeContainer(new Node(createEntity(id),lat,lon)));
        mCountNode++;
    }

    public void writeWay(long id, Collection<Tag> tags, List<WayNode> nodes){
        _sink.process(new WayContainer(new Way(createEntity(id,tags),nodes)));
        mCountWay++;
    }

    public void complete(){
        _sink.complete();
        System.out.println("Nodes: " + mCountNode + " Ways: " + mCountWay);
    }

    private CommonEntityData createEntity(Long idx){
        return new CommonEntityData(idx,1,new Date(),new OsmUser(0,"U"),idx);
    }

    private CommonEntityData createEntity(Long idx, Collection<Tag> tags){
        return new CommonEntityData(idx,1,new Date(), new OsmUser(0,"U"),idx,tags);
    }
}
