package lixiangyu.rtree;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Preconditions;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

class AreaRaw{
    String[] coord;
    String[] southwest;
    String[] northeast;
    String loc_type;
}

class Area{
    // lat2 > lat1; lon2 > lon1
    String name;
    double coord_lat;
    double coord_lon;
    double lat1;
    double lon1;
    double lat2;
    double lon2;
    Area(String name, AreaRaw area_r){
        this.name = name;
        this.coord_lat = Double.parseDouble(area_r.coord[0]);
        this.coord_lon = Double.parseDouble(area_r.coord[1]);

        this.lat1 =  Double.parseDouble(area_r.southwest[0]);
        this.lon1 =  Double.parseDouble(area_r.southwest[1]);
        this.lat2 =  Double.parseDouble(area_r.northeast[0]);
        this.lon2 =  Double.parseDouble(area_r.northeast[1]);
    }

    double get_with(){
        return lat2-lat1;
    }
    double get_length(){
        return lon2-lon1;
    }


    double get_dis(double rate){
        double lat_dis = (this.lat2 - this.lat1)/2;
        double lon_dis = (this.lon2 - this.lon1)/2;

        double dis = rate*(lat_dis+lon_dis)/2;
        return dis;
    }
}


class Point{
    String User;
    String Time;
    String Place;

    Point(){}

    Point(String usr, String t, String pl){
        this.Place = pl;
        this.User = usr;
        this.Time = t;
    }

    public String toString(){
        String text = "User" + this.User + "\t" +
                "Time" + this.Time + "\t" +
                "Place" + this.Place + "\t";
        return text;
    }
}

class UsrPath {
    String id;
    String[][] points;
}


class Tools{

    static long tran_time(String time_string) throws ParseException {
        SimpleDateFormat format =  new SimpleDateFormat("yyyyMMdd HH:mm");

        Date date = format.parse(time_string);
        //        System.out.print("Format To times:"+time);
        return date.getTime();
    }

    public static Map<String, Area> load_loc_map(String loc_path) throws IOException {
        Gson gson=new Gson();
        File loc_file=new File(loc_path);
        String content= FileUtils.readFileToString(loc_file,"UTF-8");
        final Map<String, AreaRaw> loc_raw_map = gson.fromJson(content, new TypeToken<Map<String, AreaRaw>>() {}.getType());

        final HashMap<String, Area> loc_map = new HashMap<String, Area>() {{
            for(Map.Entry<String, AreaRaw> pair:loc_raw_map.entrySet()){
                put(pair.getKey(), new Area(pair.getKey(), pair.getValue()));
            }
        }};

        return loc_map;

    }

    public static ArrayList<Point> load_file(File usr_file) throws IOException {
        String urs_name = usr_file.getName().replace(".txt", "");
        String content= FileUtils.readFileToString(usr_file,"UTF-8");
//        System.out.println("content: "+content);
        Gson gson=new Gson();
        UsrPath usr_path =  gson.fromJson(content, new TypeToken<UsrPath>() {}.getType());

        ArrayList<Point> point_li = new ArrayList<Point>();
        for(int i=0; i < usr_path.points.length; i++){
            String time, place;
            time = usr_path.points[i][0];
            place = usr_path.points[i][1];
            point_li.add(new Point(urs_name, time, place));
        }
        return point_li;

    }

    static class tmp_point{
        String user;
        String time;
        String place;
    }

    public static void SaveJson(Map<String, List<Point>> loc_index, String path) throws IOException {

        Map json_index = new HashMap<String, List<tmp_point>>();
        for (Map.Entry<String, List<Point>> item:loc_index.entrySet()){
            List<tmp_point> p_li = new ArrayList<tmp_point>();
            for (Point p: item.getValue()){
                tmp_point t_p = new tmp_point();
                t_p.user = p.User;
                t_p.time = p.Time;
                t_p.place = p.Place;
                p_li.add(t_p);
            }

            json_index.put(item.getKey(), p_li);
        }
        Gson gson=new Gson();

        FileWriter json_writer = new FileWriter(path);
        gson.toJson(json_index, json_writer);
        json_writer.close();

    }

    public static Map<String, List<Point>> LoadJson(String loc_index_path) throws IOException {

        Gson gson=new Gson();
        File loc_index_file = new File(loc_index_path);
        String content = FileUtils.readFileToString(loc_index_file,"UTF-8");
        Map<String, List<tmp_point>> json_index = gson.fromJson(content, new TypeToken<Map<String,List<tmp_point>>>() {}.getType());

        Map<String, List<Point>> loc_index = new HashMap<String, List<Point>>();
        for (Map.Entry<String, List<tmp_point>> item:json_index.entrySet()){
            List<Point> p_li = new ArrayList<Point>();
            for (tmp_point p: item.getValue()){
                Point t_p = new Point();
                t_p.User = p.user;
                t_p.Time = p.time;
                t_p.Place = p.place;
                p_li.add(t_p);
            }

            loc_index.put(item.getKey(), p_li);
        }


        return loc_index;

    }


    public static void SaveResult(List<Integer> list, String path) throws IOException {

        Gson gson=new Gson();

        FileWriter json_writer = new FileWriter(path);
        gson.toJson(list, json_writer);
        json_writer.close();

    }


}