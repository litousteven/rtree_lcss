package lixiangyu.rtree;

import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import sun.security.krb5.internal.crypto.Aes128;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Similarity {
    Map<String, Area> loc_map = null;

    public static void main(String[] args) throws ParseException, IOException {
        System.out.println(Tools.tran_time("20141203 19:33")-Tools.tran_time("20141103 19:33"));
//        2592000000
//        Similarity sim = new Similarity();
//        sim.compare("fb","6","tw","6");
//        sim.compare("fb","6","tw","7");
//        sim.compare("fb","6","tw","8");
//        sim.compare("fb","6","tw","10");
//        System.out.println(result);
    }

    Similarity() throws IOException {
        String loc_path = "./data/new_location.json";
        loc_map = Tools.load_loc_map(loc_path);

    }


    double compare(String src_name1, String user_id1, String src_name2, String user_id2) throws IOException {
        ArrayList<Point> u1_li = Tools.load_file(new File(String.format("./data/%s/%s.txt", src_name1, user_id1)));
//        System.out.println(u1_li.toString());

        ArrayList<Point> u2_li = Tools.load_file(new File(String.format("./data/%s/%s.txt", src_name2, user_id2)));
//        System.out.println(u2_li.toString());
//        System.exit(1);
        double result = find_lcss(u1_li, u2_li)/(u1_li.size()+u2_li.size());
//        System.out.println(result);
        return result;
    }

    double find_lcss(List<Point> list1, List<Point> list2){
        int ver = list1.size();
        int hor = list2.size();

        double[][] c = new double[ver+1][hor+1];
        String[][] flag = new String[ver+1][hor+1];

        for (int i = 0; i < ver; i++){
            for(int j=0; j < hor; j++){
                double lt = c[i][j] + cal_point_sim(list1.get(i), list2.get(j));
                double l = c[i+1][j];
                double t = c[i][j+1];

                if(lt >= l && lt >= t){
                    c[i+1][j+1] = lt;
                    flag[i+1][j+1] = "lt";
                }
                else if(l >= lt && l >= t){
                    c[i+1][j+1] = l;
                    flag[i+1][j+1] = "l";
                }
                else{
                    c[i+1][j+1] = t;
                    flag[i+1][j+1] = "t";
                }
            }
        }
//        System.out.println(Arrays.deepToString(c));
//        System.out.println(Arrays.deepToString(flag));

        return c[ver][hor];
    }

    double cal_point_sim(Point p1, Point p2){
        Area a1 = loc_map.get(p1.Place);
        Area a2 = loc_map.get(p2.Place);

        double a1_w = a1.get_with();
        double a1_l = a1.get_length();
        double a2_w = a2.get_with();
        double a2_l = a2.get_length();
        double dis_x = Math.abs(a1.coord_lat-a2.coord_lat);
        double dis_y = Math.abs(a1.coord_lon-a2.coord_lon);
        double sim_hor = (a1_w+a2_w)/(dis_x+a1_w+a2_w);
        double sim_ver = (a1_l+a2_l)/(dis_y+a1_l+a2_l);
//        System.out.println(sim_hor+" "+sim_ver+" "+sim_hor*sim_ver);
        double sim = sim_hor*sim_ver;
        return sim;
    }


}
