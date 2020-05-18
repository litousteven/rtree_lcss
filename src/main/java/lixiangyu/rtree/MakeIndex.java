package lixiangyu.rtree;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MakeIndex{
    static Map<String, List<Point>> get_index(String tar_name) throws IOException {


        String index_path = String.format("loc_index_%s.json", tar_name);
        File index_file = new File(index_path);
        Map<String, List<Point>> map = null;
        if(index_file.exists()){
            map = Tools.LoadJson(index_path);
        }
        else{
            map = MakeIndex.make_index(index_file, "./data/new_location.json");
            Tools.SaveJson(map, index_path);
        }
        return map;
    }

    static Map<String, List<Point>> make_index(File index_file ,String loc_path) throws IOException {
        Map<String, List<Point>> map = new HashMap<String, List<Point>> ();
        Map<String, Area> loc_map = Tools.load_loc_map(loc_path);
        for (Map.Entry<String, Area> pair: loc_map.entrySet()){
            map.put(pair.getKey(), new ArrayList<Point>());
        }

        File[] file_list = index_file.listFiles();
        for(File usr_file: file_list){
            ArrayList<Point> point_li =  Tools.load_file(usr_file);
            for(Point p: point_li){
                map.get(p.Place).add(p);
            }

        }
        return map;
    }
}
