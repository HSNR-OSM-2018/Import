import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.container.v0_6.NodeContainer;
import org.openstreetmap.osmosis.core.container.v0_6.WayContainer;
import org.openstreetmap.osmosis.core.container.v0_6.RelationContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

import crosby.binary.osmosis.OsmosisReader;


public class Importer implements Sink {

    private Importer(String path){
        OsmFilter(path);
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
                String para = " --drop-relations --drop-author --drop-version --keep=\"highway=motorway =motorway_link =trunk =trunk_link =primary =primary_link =secondary =secondary_link =tertiary =tertiary_link =living_street =residential =unclassified =service\" > ";
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

    private void Filter(){}

    private double CalcLength(double latA, double lonA, double latB, double lonB){
        double lat = latA - latB;
        double lon = lonA - lonB;
        double dist = Math.sqrt(Math.pow(lat,2) + Math.pow(lon,2));
        return dist;
    }

    //----------Sink-Class------------------------------------------

    public void process(EntityContainer entityContainer) {
        if(entityContainer instanceof NodeContainer){

        } else if(entityContainer instanceof  WayContainer){

        } else if(entityContainer instanceof  RelationContainer){

        } else {
            System.out.println("Unkown Entity");
        }
    }

    public void initialize(Map<String, Object> map) {
    }

    public void complete() {
    }

    public void release() {
    }

    //-------------------------------------------------------

    public static void main(String[] args) {
        new Importer("test.pbf");
    }
}
