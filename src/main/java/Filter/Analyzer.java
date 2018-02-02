package Filter;

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.container.v0_6.NodeContainer;
import org.openstreetmap.osmosis.core.container.v0_6.RelationContainer;
import org.openstreetmap.osmosis.core.container.v0_6.WayContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.*;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

import java.util.Map;

public class Analyzer implements Sink{

    private int nodeC = 0;
    private int nTagC = 0;
    private int wayC = 0;
    private int wTagC = 0;
    private int speedC = 0;
    private int highwayC = 0;
    private int lengthC = 0;
    private int length = 0;
    private int lparseError = 0;
    private int oneC = 0;
    private int relationC = 0;
    private int unknownC = 0;

    private long search;
    private long highId;

    public Analyzer(long searchNumber){
        search = searchNumber;
    }

    public Analyzer(){
        search = -10;
    }

    public void process(EntityContainer entityContainer) {
        if(entityContainer instanceof NodeContainer){

            Node node = ((NodeContainer) entityContainer).getEntity();

            if(node.getId() > highId)
                highId = node.getId();

            nodeC++;
            nTagC += node.getTags().size();

            if(node.getId() == search){
                System.out.println("-------------------------");
                System.out.println("Node: " + node.getId());
                System.out.println("_________________________");
            }
        } else if(entityContainer instanceof WayContainer){
            boolean found = false;
            Way way = ((WayContainer) entityContainer).getEntity();
            wayC++;
            for(Tag tag : way.getTags()){
                wTagC++;
                if(tag.getValue().equals(String.valueOf(search)))
                    found = true;
                switch (tag.getKey()){
                    case "maxspeed":
                        speedC++;
                        break;
                    case "length":
                        lengthC++;
                        try {
                            length += Integer.parseInt(tag.getValue());
                        } catch (Exception ex){
                            //ex.printStackTrace();
                            lparseError++;
                        }
                        break;
                    case "highway":
                        highwayC++;
                        break;
                    case "oneway":
                        oneC++;
                }
            }

            for (WayNode node : way.getWayNodes()) {
                if(node.getNodeId() == search)
                    found = true;
            }

            if(found){
                System.out.println("-------------------------");
                System.out.println("Way: " + way.getId());
                for (WayNode node : way.getWayNodes()) {
                    System.out.println("Node: " + node.getNodeId());
                }
                for (Tag tag : way.getTags()) {
                    System.out.println(tag.getKey() + " : " + tag.getValue());
                }
                System.out.println("_________________________");
            }
        } else if(entityContainer instanceof RelationContainer){
            relationC++;
            Relation rel = ((RelationContainer) entityContainer).getEntity();
            boolean found = (rel.getId() == search);
            for (Tag tag : rel.getTags()) {
                if(tag.getValue().equals(String.valueOf(search)))
                    found = true;
            }
            if(found){
                System.out.println("-------------------------");
                System.out.println("Relation: " + rel.getId());
                for (Tag tag : rel.getTags()) {
                    System.out.println(tag.getKey() + " " + tag.getValue());
                }
                System.out.println("_________________________");
            }
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
        if(length>1)System.out.println("Length in Meter: " + length);
        if(lparseError>1)System.out.println("Length parse Error: " + lparseError);

        System.out.println("\nHighest ID: " + highId);
    }

    public void release() {

    }
}
