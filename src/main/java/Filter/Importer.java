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

public class Importer {

    public static final boolean STATS = true;

    private static Logger logger = Logger.getLogger(Importer.class.getSimpleName());

    private String originNodes;
    private String originWays;
    private String targetNodes;
    private String targetWays;

    private Importer() {
        originNodes = this.getClass().getResource("/germany2801/Node.pbf").getPath();
        originWays = this.getClass().getResource("/germany2801/Way.pbf").getPath();
        File file = new File(originNodes);
        targetNodes = file.getParent() + "\\Filter_" + file.getName();
        System.out.println(targetNodes);
        file = new File(originWays);
        targetWays = file.getParent() + "\\Filter_" + file.getName();
        filter();
    }

    private Importer(String originNodes, String originWays) {
        this.originNodes = originNodes;
        this.originWays = originWays;
        System.out.println("nodes file: " + originNodes);
        System.out.println("ways file: " + originWays);
        File file = new File(originNodes);
        targetNodes = file.getParent() + "/Filter_" + file.getName();
        System.out.println(targetNodes);
        file = new File(originWays);
        targetWays = file.getParent() + "/Filter_" + file.getName();
        filter();
        analyseFile(targetNodes, -10);
        analyseFile(targetWays, -10);
    }

    private Importer(String pathNodes) {
        analyseFile(pathNodes, -1065841);
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
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public void analyseFile(String path, long searchNumber) {
        try {
            InputStream inputStream = new FileInputStream(path);
            OsmosisReader reader = new OsmosisReader(inputStream);
            System.out.println("---------------");
            System.out.println(path);
            reader.setSink(new Analyzer(searchNumber));
            reader.run();
            System.out.println("---------------");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private String executeCommand(String command) {
        StringBuilder output = new StringBuilder();
        try {
            Process p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return output.toString();
    }

    public void linux(String path) {
        String origin = path;
        String target = origin.replace(".pbf", "");
        String rfScript = new File(System.getProperty("user.dir") + "/roughFilter.sh").getPath();
        System.out.println(executeCommand(rfScript + " " + path + " " + target));

        originNodes = target + "Node.pbf";
        originWays = target + "Way.pbf";
        targetNodes = target + "Filter_Nodes.pbf";
        targetWays = target + "Filter_Ways.pbf";
        filter();

        String ffScript = new File(System.getProperty("user.dir") + "/fineFilter.sh").getPath();
        System.out.println(executeCommand(ffScript + " " + targetNodes));
        System.out.println(executeCommand(ffScript + " " + targetWays));
        executeCommand("rm " + target + "All.pbf");
        executeCommand("rm " + originNodes);
        executeCommand("rm " + originWays);
    }

    public void windows(String nodePath, String wayPath){
        originNodes = nodePath;
        originWays = wayPath;
        File file = new File(originNodes);
        targetNodes = file.getParent() + "/Filter_" + file.getName();
        System.out.println(targetNodes);
        file = new File(originWays);
        targetWays = file.getParent() + "/Filter_" + file.getName();
        filter();
    }

    public static void main(String[] args) {
        if (args.length >= 2) {
            String dir = System.getProperty("user.dir") + "/";
            Importer imp = new Importer(dir + args[0], dir + args[1]);
            return;
        }
        if (args.length == 1) {
            Importer imp = new Importer("");
            imp.linux(System.getProperty("user.dir") + "/" + args[0]);
            return;
        }
        logger.info("At least one parameter is required");
    }
}