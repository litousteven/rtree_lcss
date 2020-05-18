package lixiangyu.rtree;


import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Geometry;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Search {
    RTree<String, Geometry> rtree;
    //    Map<String, ArrayList<Point>>  scr_loc_index;
    Map<String, List<Point>> tar_loc_index;
    Map<String, Area> loc_map;
    double ext_rate;
    String src_name;
    String tar_name;
    Integer maxnum;
    List<String> match_li;
    double thre;

    Search(String src_name, String tar_name, Integer maxnum, double ext_rate, double thre) throws IOException {
        this.src_name = src_name;
        this.tar_name = tar_name;
        this.ext_rate = ext_rate;
        this.maxnum = maxnum;
        this.thre = thre;
        String loc_path = "./data/new_location.json";

//        this.scr_loc_index = MakeIndex.get_index(src_name);
        long startTime =  System.currentTimeMillis();
        this.tar_loc_index = MakeIndex.get_index(tar_name);
        long endTime =  System.currentTimeMillis();
        double usedTime = (endTime-startTime)/1000.0;
        System.out.println("build index used time " + usedTime + "s");


        startTime =  System.currentTimeMillis();
        this.rtree =  MakeRTree.get_rtree(tar_name, this.tar_loc_index, "point");
        endTime =  System.currentTimeMillis();
        usedTime = (endTime-startTime)/1000.0;
        System.out.println("build tree used time " + usedTime + "s");

        this.loc_map = Tools.load_loc_map(loc_path);

        find_mathc(100);
    }


    void find_mathc(Integer count){
        String src_dir_path = String.format("./data/%s", this.src_name);
        File src_dir = new File(src_dir_path);
        File[] src_file_li = src_dir.listFiles();
        assert src_file_li != null;
        List<String> src_name_li = new ArrayList<String>();
        for(File usr_file:src_file_li){
            src_name_li.add(usr_file.getName());
        }


        String tar_dir_path = String.format("./data/%s", this.tar_name);
        File tar_dir = new File(tar_dir_path);
        File[] tar_file_li = tar_dir.listFiles();
        assert tar_file_li != null;
        List<String> match_name_li = new ArrayList<String>();
        for(File usr_file:tar_file_li){
            if(src_name_li.contains(usr_file.getName()))
                match_name_li.add(usr_file.getName());
            if(match_name_li.size()>count)
                break;
        }

        this.match_li = match_name_li;

    }

    Map<String, Double> SearchUser(ArrayList<Point> src_point_li){
        ArrayList<Point> result_li = new ArrayList<Point>();
        for(Point p: src_point_li){
            result_li.addAll(SearchPoint(p));
        }

        final Map<String, Integer> tmp_user_li = new HashMap<String, Integer>();


        for (Point p: result_li){
            if(tmp_user_li.containsKey(p.User)){
                tmp_user_li.put(p.User, tmp_user_li.get(p.User)+1);
            }
            else {
                tmp_user_li.put(p.User, 1);
            }
        }
        final double length = src_point_li.size();


        final Map<String, Double> user_li = new HashMap<String, Double>(){{
            for(Map.Entry<String, Integer> user: tmp_user_li.entrySet()){
                double score = user.getValue().doubleValue()/length;
                if(score>=thre){
                    put(user.getKey(), (score));
                }

            }
        }};



        return user_li;
    }

    ArrayList<Point> SearchPoint(Point p){
        Area a = this.loc_map.get(p.Place);

//        final Iterable<Entry<String, Geometry>> it = this.rtree.search(Geometries.rectangle(a.lon1, a.lat1, a.lon2, a.lat2), a.get_dis(0.2)).toBlocking().toIterable();
//        final Iterable<Entry<String, Geometry>> it = this.rtree.nearest(Geometries.rectangle(a.lon1, a.lat1, a.lon2, a.lat2), a.get_dis(0.2), 500).toBlocking().toIterable();

        final Iterable<Entry<String, Geometry>> it = this.rtree.nearest(Geometries.pointGeographic(a.coord_lon, a.coord_lat), a.get_dis(ext_rate), maxnum).toBlocking().toIterable();
//        final Iterable<Entry<String, Geometry>> it = this.rtree.search(Geometries.pointGeographic(a.coord_lon, a.coord_lat), a.get_dis(10.0)).toBlocking().toIterable();;

        ArrayList<Point> point_li = new ArrayList<Point>();
        for(Entry<String, Geometry> item: it){
//            if(!this.tar_loc_index.containsKey(item.value()))
//                continue;
            List<Point> tmp_point_li = this.tar_loc_index.get(item.value());
//            if (tmp_point_li.size()<1)
//                continue;
            point_li.addAll(tmp_point_li);
        }

        return point_li;
    }

}