package Merger;

import org.openstreetmap.osmosis.core.container.v0_6.NodeContainer;
import org.openstreetmap.osmosis.core.container.v0_6.WayContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.*;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.core.task.v0_6.Source;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public class MergeWriter implements Source {

    private  Sink _sink;

    @Override
    public void setSink(Sink sink) {
        _sink = sink;
    }

    public void complete(){
        _sink.complete();
    }

    private CommonEntityData createEntity(Long idx){
        return new CommonEntityData(idx,1,new Date(),new OsmUser(0,"U"),0);
    }

    private CommonEntityData createEntity(Long idx, Collection<Tag> tags){
        return new CommonEntityData(idx,1,new Date(), new OsmUser(0,"U"),0,tags);
    }

    public void writeNode(Long id, double lat, double lon){
        _sink.process(new NodeContainer(new Node(createEntity(id),lat,lon)));
    }

    public void writeWay(long id, Collection<Tag> tags, List<WayNode> nodes){
        _sink.process(new WayContainer(new Way(createEntity(id,tags),nodes)));
    }
}
