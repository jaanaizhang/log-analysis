package base;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.*;
import org.json.simple.JSONObject;
import parse.JhistFileParser;
import util.FactorStatistics;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by zhangyun on 4/29/15.
 */
public class MapTask extends Task {
  /**The MapTask processing split, input file paths are split file paths*/
  private List<String> splitFiles;
  private String inputFormat;

  public MapTask( JhistFileParser.TaskInfo taskInfo, Path taskLogPath,
                 LinkedHashMap<String, Node> operators ,List<String> splitFiles,
                 String inputFormat) {
    super(taskLogPath, taskInfo, operators);
    this.splitFiles = new ArrayList<String>();
    this.splitFiles.addAll(splitFiles);
    this.inputFormat = inputFormat;
  }

  public void printAll(){
    System.out.println("TASK_ID: " + taskID);
    System.out.println(taskInfo.getTaskType());
    System.out.println("INPUT FILES:");
    System.out.println(splitFiles);
    System.out.println("INPUT FORMAT: " + inputFormat );
    System.out.println("OPTREE: ");
    for (String k : operators.keySet()){
      System.out.println(k + " => " + operators.get(k).toString());
    }
  }


  public String toJSON() throws IOException {
    JSONObject jobJson = new JSONObject();
    jobJson.put("taskID", taskID);
    Iterator<String> c =
             taskInfo.getCounters().getGroupNames().iterator();
    while(c.hasNext()){
      System.out.println(c.next() + ",");
    }

    LinkedHashMap counterGroup = new LinkedHashMap();
    LinkedHashMap mapcounter = new LinkedHashMap();
    LinkedHashMap redcounter = new LinkedHashMap();
    LinkedHashMap totalcounter = new LinkedHashMap();

    FactorStatistics.mapCounter(mapcounter, taskInfo.getCounters());
    counterGroup.put("mapCounter",mapcounter);


    jobJson.put("Counter",counterGroup);

    StringWriter out = new StringWriter();
    jobJson.writeJSONString(out);

    return jobJson.toJSONString();
  }
}