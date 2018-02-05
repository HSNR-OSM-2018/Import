package Merger;

import crosby.binary.osmosis.OsmosisReader;
import crosby.binary.osmosis.OsmosisSerializer;
import org.openstreetmap.osmosis.osmbinary.file.BlockOutputStream;

import java.io.*;

public class Merger {

    //class:
    //reader , writer
    //function:
    //fileFinder

    public void MakeWar(String dir){
        MergeWriter mw = new MergeWriter();
        try {
        OutputStream outputStream = new FileOutputStream(dir + "Merge.pbf");
        mw.setSink(new OsmosisSerializer(new BlockOutputStream(outputStream)));
        File folder = new File(dir);
        if(folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                if (file.getName().toLowerCase().endsWith(".pbf")) {
                    MergeReader mr = new MergeReader();
                    mr.setWriter(mw);
                    InputStream inputStream = new FileInputStream(file.getAbsolutePath());
                    OsmosisReader reader = new OsmosisReader(inputStream);
                    reader.setSink(mr);
                    reader.run();
                }
            }
        }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mw.complete();
    }

    public void Egg(String file){
        File f = new File(file);
        EggReader eg = new EggReader(f.getPath().replace(".pbf","") + "Egg.pbf");

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        OsmosisReader reader = new OsmosisReader(inputStream);
        reader.setSink(eg);
        System.out.println("start");
        reader.run();
    }

    public static void main(String[] args) {
        Merger merger = new Merger();
        merger.Egg("C:\\Users\\Assares\\Desktop\\OSM\\germany2801\\Filter_Way.pbf");
    }
}
