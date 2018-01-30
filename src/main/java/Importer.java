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

    private Importer(){

        originNodes = this.getClass().getResource("/europa1401/Node.pbf").getPath();
        originWays = this.getClass().getResource("/europa1401/Way.pbf").getPath();
        File file = new File(originNodes);
        targetNodes = file.getParent() + "\\Filter_" + file.getName();
        System.out.println(targetNodes);
        file = new File(originWays);
        targetWays = file.getParent() + "\\Filter_" + file.getName();
        filter();
        analyseFile(targetNodes, -10);
        analyseFile(targetWays, -10);
    }

    private Importer(String pathNodes){
        analyseFile(this.getClass().getResource("/europa1401.pbf").getPath(), -10);
        analyseFile(this.getClass().getResource("/europa1401/Filter_Node.pbf").getPath(), -10);
        analyseFile(this.getClass().getResource("/europa1401/Filter_Way.pbf").getPath(), -10);
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

    private void analyseFile(String path, long searchNumber){
        try{
            InputStream inputStream = new FileInputStream(path);
            OsmosisReader reader = new OsmosisReader(inputStream);
            System.out.println("---------------");
            System.out.println(path);
            reader.setSink(new Analyzer(searchNumber));
            reader.run();
            System.out.println("---------------");
        } catch (FileNotFoundException ex){
            ex.printStackTrace();
        }
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

    public void linux(String path){
        String origin = path;
        String target = origin.replace(".pbf","");
        String rfScript = this.getClass().getResource("/roughFilter.sh").getPath();
        ExecuteCommand(rfScript + " " + path + " " + target);
    }

    public static void main(String[] args) {
        new Importer();
    }
}
