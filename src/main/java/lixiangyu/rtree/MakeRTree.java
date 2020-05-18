package lixiangyu.rtree;

import com.github.davidmoten.rtree.InternalStructure;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.Serializer;
import com.github.davidmoten.rtree.Serializers;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.google.gson.reflect.TypeToken;
//import com.sun.org.apache.xml.internal.serialize.Serializer;

import org.apache.commons.io.FileUtils;
import com.google.gson.Gson;


import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class MakeRTree
{
    public static RTree<String, Geometry> get_rtree(String tar_name, Map<String, List<Point>> loc_index, String type) throws IOException {
        String rtree_path = String.format("./rtree_%s_%s_save", tar_name, type);
        File file = new File(rtree_path);
        Serializer<String, Geometry> serializer = Serializers.flatBuffers().utf8();

        if(file.exists()){
            InputStream is = new FileInputStream(file);

            long lengthBytes = file.length();
            RTree<String, Geometry> tree =
                    serializer.read(is, lengthBytes, InternalStructure.SINGLE_ARRAY);

            assert tree != null;

            tree.visualize(2400,1200)
                    .save("target/mytree.png");
            return tree;
        }
        else{

            RTree<String, Geometry> tree = RTree.star().maxChildren(4).create();
            tree = MakeRTree.load(tree, "./data/new_location.json", loc_index, type);

            OutputStream os = new FileOutputStream(file);
            serializer.write(tree, os);
            tree.visualize(7200,3600)
                    .save("target/mytree.png");
            return tree;
        }


    }



    public static RTree<String, Geometry> add(RTree<String, Geometry> tree, double x, double y){
        double delt = 0.5;
        tree = tree.add("1", Geometries.rectangle(x,y, x+delt,y+delt));
        return tree;
    }

    public static RTree<String, Geometry> load(RTree<String, Geometry> tree, String loc_path, Map<String, List<Point>> loc_index, String type) throws IOException {
        Map<String, Area> loc_map = Tools.load_loc_map(loc_path);

        for(Map.Entry<String, List<Point>> item:loc_index.entrySet()){
            if(item.getValue().size()>0){
                Area area = loc_map.get(item.getKey());
                if(Math.abs(area.lon2-area.lon1) > 180){
                    System.out.println("lon "+area.lon2+" " + area.lon1);

                    continue;
                }
                if(Math.abs(area.lat2-area.lat1) > 90){
                    System.out.println("lat "+area.lat2+" " + area.lat1);
                }
                if(type.equals("point")){
                    tree = tree.add(area.name, Geometries.pointGeographic(area.coord_lon, area.coord_lat));
                }else{
                    tree = tree.add(area.name, Geometries.rectangleGeographic(area.lon1,area.lat1, area.lon2,area.lat2));
                }

            }

        }

        return tree;
    }

}
