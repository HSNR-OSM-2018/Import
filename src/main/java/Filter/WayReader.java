package Filter;

import java.io.*;
import java.util.*;

import crosby.binary.osmosis.OsmosisSerializer;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.container.v0_6.WayContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

import org.openstreetmap.osmosis.osmbinary.file.BlockOutputStream;

public class WayReader implements Sink{

    private PbfWriter _writer;
    private String _targetPath;
    private Map<Long,NodeInfo> _nodeMap;

    public WayReader(Map<Long,NodeInfo> nodes, String targetPath) throws FileNotFoundException {
        _nodeMap = nodes;
        _targetPath = targetPath;
        _writer = new PbfWriter();
        OutputStream outputStream = new FileOutputStream(_targetPath);
        _writer.setSink(new OsmosisSerializer(new BlockOutputStream(outputStream)));
    }

    public void process(EntityContainer entityContainer) {
        if(entityContainer instanceof WayContainer){
            Way way = ((WayContainer) entityContainer).getEntity();
            String highway = "";
            short maxSpeed = 0;
            int length = 0;
            String oneway = "no";
            for (Tag tag : way.getTags()) {
                switch (tag.getKey().toLowerCase()){
                    case "highway":
                        highway = tag.getValue();
                        break;

                    case "maxspeed":
                        maxSpeed = tagToSpeed(tag.getValue());
                        break;

                    case "oneway":
                        oneway = tag.getValue();
                        break;

                    case "junction":
                        if((tag.getValue().equalsIgnoreCase("roundabout")) && oneway.equalsIgnoreCase("no")){
                            oneway = "yes";
                        }
                        break;
                }
            }
            if(maxSpeed == 0){
                maxSpeed = getStandardSpeed(highway);
            }
            Set<Tag> tags = new HashSet<>();
            tags.add(new Tag("highway",highway));
            tags.add(new Tag("maxspeed", String.valueOf(maxSpeed)));
            tags.add(new Tag("oneway",oneway));
            WayNode startNode = new WayNode(way.getWayNodes().get(0).getNodeId());
            for(int i=1;i<way.getWayNodes().size();i++){
                length += _nodeMap.get(way.getWayNodes().get(i-1).getNodeId()).distToNode(_nodeMap.get(way.getWayNodes().get(i).getNodeId()));
                if(_nodeMap.get(way.getWayNodes().get(i).getNodeId()).isMarked()){
                    List<WayNode> wayNodes = new ArrayList<>();
                    wayNodes.add(startNode);
                    wayNodes.add(new WayNode(way.getWayNodes().get(i).getNodeId()));
                    startNode = new WayNode(way.getWayNodes().get(i).getNodeId());
                    Tag lengthTag = new Tag("length", String.valueOf(length));
                    tags.add(lengthTag);
                    _writer.writeWay(way.getId(),tags,wayNodes);
                    tags.remove(lengthTag);
                    length = 0;
                }
            }
        }
    }

    private short tagToSpeed(String tagString){
        short speed;
        String tag = tagString.replace(" ","").toLowerCase();
        speed = saveParseString(tag);
        if(speed == 0) {
            if (tagString.equalsIgnoreCase("walk")) {
                speed = 8;
            } else if (tag.endsWith("mph")) {
                speed = saveParseString(tag.replace("mph", ""));
                speed *= 1.609344;
            } else if(tag.endsWith("kmh")){
                speed = saveParseString(tag.replace("kmh",""));
            } else if(tag.endsWith("knots")){
                speed = saveParseString(tag.replace("knots",""));
                speed *= 1.851999999984;
            }
        }
        return speed;
    }

    private short saveParseString(String value){
        try{
            return Short.parseShort(value);
        } catch (NumberFormatException ex){
            return 0;
        }
    }

    private short getStandardSpeed(String highway){
        switch (highway.toLowerCase().replace("_link","")) {
            case "motorway":
                return 130;

            case "trunk":
                return 100;

            case "primary":
                return 60;

            case "secondary":
                return 60;

            case "tertiary":
                return 60;

            case "living_street":
                return 15;

            case "residential":
                return 30;

            case "unclassified":
                return 30;

            case "service":
                return 30;

            default:
                return 60;

        }
    }

    public void initialize(Map<String, Object> map) {

    }

    public void complete() {
        _writer.complete();
    }

    public void release() {

    }
}
