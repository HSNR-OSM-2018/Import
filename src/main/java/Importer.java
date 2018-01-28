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
        analyseFile(targetNodes,0);
        analyseFile(targetWays,0);
    }

    private Importer(String pathNodes, String pathWays, String targetPathNodes , String targetPathWays ){
        originNodes = pathNodes;
        originWays = pathWays;
        targetNodes = targetPathNodes;
        targetWays = targetPathWays;
        //filter();
        analyseFile(this.getClass().getResource("/duesseldorf2701.pbf").getPath(), 268326223);
        analyseFile(this.getClass().getResource("/duesseldorf2701/step1.pbf").getPath(), 268326223);
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

    public void Import(){
        String path = this.getClass().getResource("/duesseldorf2701.pbf").getPath();
        String target = path.replace(".pbf","");
        String rfScript = this.getClass().getResource("/roughFilter.sh").getPath();
        ExecuteCommand(rfScript + " " + path + " " + target);
        //TODO check for linux system then execute script else execute only the written filter
    }

    public static void main(String[] args) {
        new Importer("C:\\Users\\Uni\\Desktop\\Routenplanner\\test\\Ddorf\\Node.pbf",
                "C:\\Users\\Uni\\Desktop\\Routenplanner\\test\\Ddorf\\Way.pbf", "","");
    }
}
