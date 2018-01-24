import crosby.binary.osmosis.OsmosisReader;
import crosby.binary.osmosis.OsmosisSerializer;
import org.openstreetmap.osmosis.osmbinary.file.BlockOutputStream;

import java.io.*;

/*
read node - save all nodes | map<id,(lat,lon,bool)>

read way - start and end node marked

write node - write saved nodes

read way - check middle node if in list && write way (correct tags && calc dist)
-> split way there
tags: highway=same maxspeed=km/h length=meter onway=yes/no/-1

 */

public class Importer{

    private String originNodes;
    private String originWays;
    private String targetNodes;
    private String targetWays;

    private Importer(String pathNodes, String pathWays){
        originNodes = pathNodes;
        originWays = pathWays;
        File file = new File(pathNodes);
        targetNodes = file.getParent() + "\\Filter_" + file.getName();
        System.out.println(targetNodes);
        file = new File(pathWays);
        targetWays = file.getParent() + "\\Filter_" + file.getName();
        filter();
    }

    private Importer(String pathNodes, String pathWays, String targetPathNodes , String targetPathWays ){
        originNodes = pathNodes;
        originWays = pathWays;
        targetNodes = targetPathNodes;
        targetWays = targetPathWays;
        filter();
    }

    private void filter() {
        try {
            NodeMarker nodeMarker = new NodeMarker();
            PbfWriter writer = new PbfWriter();

            InputStream inputStream = new FileInputStream(originNodes);
            OsmosisReader reader = new OsmosisReader(inputStream);
            reader.setSink(nodeMarker);
            reader.run();

            inputStream = new FileInputStream(originWays);
            reader = new OsmosisReader(inputStream);
            reader.setSink(nodeMarker);
            reader.run();

            OutputStream outputStream = new FileOutputStream(targetNodes);
            writer.setSink(new OsmosisSerializer(new BlockOutputStream(outputStream)));
            writer.writeNodes(nodeMarker.getNodesMap());
            writer.complete();

            WayReader wayReader = new WayReader(nodeMarker.getNodesMap(), targetWays);
            inputStream = new FileInputStream(originWays);
            reader = new OsmosisReader(inputStream);
            reader.setSink(wayReader);
            reader.run();
        } catch (FileNotFoundException ex){
            ex.printStackTrace();
        }
    }

    private String OsmFilter(String file) {
        try {
            //prüfen ob datei vorhanden ist
            File f = new File(file);
            if (!f.isFile()) {
                System.out.println(file + " is not a file");
                return "Error";
            }

            String origin = file;
            String targetNode = f.getParent() + "/Node_" + f.getName();
            String targetWay = f.getParent() + "/Way_" + f.getName();

            //umwandeln falls im falschen format
            if (origin.endsWith(".pbf")) {
                f = File.createTempFile("tmp.osm", ".o5m");
                String s = ExecuteCommand("osmconvert " + origin + " -o=" + f.getAbsolutePath());
                System.out.println("Convert done to .o5m: " + s);
                origin = f.getAbsolutePath();
            }

            //Filtern und nach pbf umwandeln
            File output = File.createTempFile("output", ".osm");
            if (origin.endsWith(".osm") || origin.endsWith(".o5m")) {
                String para = " --drop-relations --drop-author --drop-version --keep=\"highway=motorway =motorway_link =trunk =trunk_link =primary =primary_link =secondary =secondary_link =tertiary =tertiary_link =living_street =residential =unclassified =service =road\" > ";
                String paraNode = " --drop-ways --drop-tags=\"*=\" > ";
                String paraWay = " --drop-nodes > ";

                String s = ExecuteCommand("osmfilter " + origin + para + targetNode);
                System.out.println("All-Filter done: " + s);

                /*
                //Get Nodes in File
                File output2 = File.createTempFile("outputNode",".osm");
                s = ExecuteCommand("osmfilter " + output.getAbsolutePath() + paraNode + output2.getAbsolutePath());
                System.out.println("Node-Filter done: " + s);
                s = ExecuteCommand("osmconvert " + output2.getAbsolutePath() + " -o=" + targetNode);
                System.out.println("Node convert in .pbf done: " + s);

                //Get Ways in File
                output2 = File.createTempFile("outputWay",".osm");
                s = ExecuteCommand("osmfilter " + output.getAbsolutePath() + paraWay + output2.getAbsolutePath());
                System.out.println("Way-Filter done: " + s);
                s = ExecuteCommand("osmconvert " + output2.getAbsolutePath() + " -o=" + targetWay);
                System.out.println("Way convert in .pbf done: " + s);
                return targetNode;
                */
                return "";
            } else {
                System.out.println(file + " has the wrong File-Type, need pbf/osm/o5m");
            }

        }catch (Exception ex){
            System.out.println("Exception in OsmFilter:");
            ex.printStackTrace();
        }
        return "Error";
    }

    private String ExecuteCommand(String command){
        StringBuffer output = new StringBuffer();

        Process p;
        try{

            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while((line = reader.readLine()) != null){
                output.append(line + "\n");
            }

        } catch (Exception ex){
            ex.printStackTrace();
        }

        return output.toString();
    }

    public static void main(String[] args) {
        new Importer("C:\\Users\\Uni\\Desktop\\Routenplanner\\test\\Node.pbf","C:\\Users\\Uni\\Desktop\\Routenplanner\\test\\Way.pbf");
    }
}
