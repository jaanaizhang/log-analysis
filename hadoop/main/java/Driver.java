import base.Job;
import conf.LAConf;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import parse.JHParser;
import parse.JhistFileParser;
import parse.LogInput;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

/**
 * Created by zhangyun on 4/24/15.
 */

public class Driver {
  private static Log LOG = LogFactory.getLog(Driver.class);
  private LogInput logInput;
  private LAConf conf;

  public Driver( ) {

  }


  public static void main(String[] args ){
    LAConf conf = new LAConf();


    try{
      LogAnalyzer LA = new LogAnalyzer(conf);
      LA.analyze();

      System.out.println(" ============================================ ");
//      Path path = tmpJob.getJHPath();
//      FileSystem fs = path.getFileSystem(conf);
//      JobHistoryParser jp = new JobHistoryParser( fs,path);
//      for( Map.Entry<TaskID, JobHistoryParser.TaskInfo> task : jp.parse().getAllTasks().entrySet()){
//        System.out.println("task = " + task.getKey() +"\n value = ");
//        task.getValue().printAll();
//      }
      System.out.println(" ============================================ ");



    }catch (IOException e){
      e.printStackTrace();
    }
  }
}
