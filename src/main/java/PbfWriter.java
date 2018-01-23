import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;

import org.openstreetmap.osmosis.core.container.v0_6.NodeContainer;
import org.openstreetmap.osmosis.core.container.v0_6.WayContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.CommonEntityData;
import org.openstreetmap.osmosis.core.domain.v0_6.OsmUser;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.core.task.v0_6.Source;


public class PbfWriter implements Source{

    private int mCountWay = 0;
    private int mCountNode = 0;

    private Sink mSink;

    public void setSink(Sink sink) {
        mSink = sink;
    }

    public void writeNode(){

        mCountNode++;
    }

    public void writeWay(){

        mCountWay++;
    }

    public void complete(){
        mSink.complete();
        System.out.println("Nodes: " + mCountNode + " Ways: " + mCountWay);
    }

    private CommonEntityData createEntity(int idx){
        return new CommonEntityData(idx,1,new Date(),new OsmUser(idx,"User"),idx);
    }
}
