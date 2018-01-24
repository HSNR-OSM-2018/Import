import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.container.v0_6.NodeContainer;
import org.openstreetmap.osmosis.core.container.v0_6.RelationContainer;
import org.openstreetmap.osmosis.core.container.v0_6.WayContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

import java.util.Map;

public class CollectStats implements Sink{

    private int nodeC = 0;
    private int nTagC = 0;
    private int wayC = 0;
    private int wTagC = 0;
    private int speedC = 0;
    private int highwayC = 0;
    private int lengthC = 0;
    private int oneC = 0;
    private int relationC = 0;
    private int unknownC = 0;

    public void process(EntityContainer entityContainer) {
        if(entityContainer instanceof NodeContainer){

            Node node = ((NodeContainer) entityContainer).getEntity();
            nodeC++;
            nTagC += node.getTags().size();

        } else if(entityContainer instanceof WayContainer){

            Way way = ((WayContainer) entityContainer).getEntity();
            wayC++;
            for(Tag tag : way.getTags()){
                wTagC++;
                switch (tag.getKey()){
                    case "maxspeed":
                        speedC++;
                        break;
                    case "length":
                        lengthC++;
                        break;
                    case "highway":
                        highwayC++;
                        break;
                    case "oneway":
                        oneC++;
                }
            }

        } else if(entityContainer instanceof RelationContainer){

            relationC++;
            //Not needed
        } else {

            unknownC++;

        }
    }

    public void initialize(Map<String, Object> map) {

    }

    public void complete() {

        System.out.println();
        System.out.println("Anzahl");
        if(nodeC>1)System.out.println("Nodes: " + nodeC);
        if(nTagC>1)System.out.println("Tags an Nodes: " + nTagC);
        if(wayC>1)System.out.println("Ways: " + wayC);
        if(wTagC>1)System.out.println("Tags an Ways: " + wTagC);
        if(relationC>1)System.out.println("Realtions: " + relationC);
        if(unknownC>1)System.out.println("Unkown Entity: " + unknownC);
        System.out.println();
        System.out.println("Tags");
        if(highwayC>1)System.out.println("Highway: " + highwayC);
        if(speedC>1)System.out.println("Maxspeed: " + speedC);
        if(oneC>1)System.out.println("Oneway: " + oneC);
        if(lengthC>1)System.out.println("Length: " + lengthC);
    }

    public void release() {

    }
}
