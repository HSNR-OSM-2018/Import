package Filter;

import crosby.binary.osmosis.OsmosisReader;
import crosby.binary.osmosis.OsmosisSerializer;
import org.openstreetmap.osmosis.osmbinary.file.BlockOutputStream;

import java.io.*;
import java.util.logging.Logger;

/*
read node - save all nodes | map<id,(lat,lon,bool)>

read way - start and end node marked

write node - write saved nodes

read way - check middle node if in list && write way (correct tags && calc dist)
-> split way there
tags: highway=same maxspeed=km/h length=meter onway=yes/no/-1

 */

public class Importer{

    private static Logger logger = Logger.getLogger(Importer.class.getSimpleName());

    private String originNodes;
    private String originWays;
    private String targetNodes;
    private String targetWays;

    private Importer(){
        originNodes = "C:\\Users\\Assares\\Desktop\\OSM\\europa1401\\Node.pbf";
        originWays = "C:\\Users\\Assares\\Desktop\\OSM\\europa1401\\Node.pbf";
        File file = new File(originNodes);
        targetNodes = file.getParent() + "\\Filter_" + file.getName();
        System.out.println(targetNodes);
        file = new File(originWays);
        targetWays = file.getParent() + "\\Filter_" + file.getName();
        filter();
    }

    private Importer(String pathNodes){
        analyseFile("C:\\Users\\Assares\\Desktop\\OSM\\europa1401\\All.pbf",-1065841);
    }

    private void filter() {
        try {
            NodeMarker nodeMarker = new NodeMarker();
            PbfWriter writer = new PbfWriter();

            InputStream inputStream = new FileInputStream(originNodes);
            OsmosisReader reader = new OsmosisReader(inputStream);
            reader.setSink(nodeMarker);
            reader.run();
            logger.info("Read nodes done");

            inputStream = new FileInputStream(originWays);
            reader = new OsmosisReader(inputStream);
            System.gc();
            reader.setSink(nodeMarker);
            reader.run();
            logger.info("Read ways, mark nodes done");

            OutputStream outputStream = new FileOutputStream(targetNodes);
            writer.setSink(new OsmosisSerializer(new BlockOutputStream(outputStream)));
            writer.writeNodes(nodeMarker.getNodesMap());
            writer.complete();
            logger.info("Write nodes done");
            writer = null;
            System.gc();

            WayReader wayReader = new WayReader(nodeMarker.getNodesMap(), targetWays);
            inputStream = new FileInputStream(originWays);
            reader = new OsmosisReader(inputStream);
            System.gc();
            reader.setSink(wayReader);
            reader.run();
            logger.info("Write ways done");
        } catch (FileNotFoundException ex){
            ex.printStackTrace();
        }
    }

    public void analyseFile(String path, long searchNumber){
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

        originNodes = target + "Node.pbf";
        originWays = target + "Way.pbf";
        targetNodes = target + "Filter_Nodes.pbf";
        targetWays = target + "Filter_Ways.pbf";
        filter();

        String ffScript = this.getClass().getResource("/fineFilter.sh").getPath();
        ExecuteCommand(ffScript + " " + targetNodes);
        ExecuteCommand(ffScript + " " + targetWays);
    }

    public static void main(String[] args) {
        Importer imp = new Importer("");
        //imp.linux("");
    }
}
