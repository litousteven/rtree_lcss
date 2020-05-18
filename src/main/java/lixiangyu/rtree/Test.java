package lixiangyu.rtree;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Geometry;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Test {


    public static void main(String[] args) throws IOException {
        String user_name;
        String src_name = "fb";
        String tar_name = "fs";


        Search search = new Search(src_name, tar_name, 500, 1.2, 0.2);

        Integer count = 0;
        Integer can_num_sum = 0;

        ArrayList<Integer> rank_li = new ArrayList<Integer>();
        double rank_sum = 0;
        double score_sum = 0;

        for(String usr_file:search.match_li){
            user_name = usr_file.replace(".txt","");
            System.out.println("## User: "+user_name);
            ArrayList<Point> point_li = Tools.load_file(new File(String.format("./data/%s/%s.txt", src_name, user_name)));

            Map<String, Double> candidates = search.SearchUser(point_li);

            Similarity sim = new Similarity();
            for(Map.Entry<String, Double>  cand:candidates.entrySet()){
                double sim_value =  sim.compare(src_name,user_name,tar_name,cand.getKey());
                candidates.put(cand.getKey(),sim_value);
            }
//            System.out.println(candidates.toString());

            System.out.printf("points number=%d\t candidates num=%d\n", point_li.size(), candidates.size());
            can_num_sum +=candidates.size();
            System.out.println(candidates.containsKey(user_name));

            if(candidates.containsKey(user_name)){

                List<Double> values=new ArrayList<Double>(candidates.values());
                Collections.sort(values);
                Collections.reverse(values);
                double score = candidates.get(user_name);
                int rank = values.indexOf(candidates.get(user_name));
                rank_sum += rank;
                score_sum += score;
                rank_li.add(rank);
                System.out.printf("score=%f, rank=%d\n",score, rank);
                count += 1;
            }

        }

        double avg_num = can_num_sum.doubleValue()/search.match_li.size();
        System.out.printf("can_avg_num=%f ,recall=%f\n",avg_num, count.doubleValue()/search.match_li.size());
        double avg_score = score_sum/count;
        double avg_rank = rank_sum/count;
        System.out.printf("avg_score=%f ,avg_rank=%f\n",avg_score, avg_rank);
        Tools.SaveResult(rank_li, "rank.json");

    }


}
