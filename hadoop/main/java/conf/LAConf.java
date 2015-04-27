package conf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.StringInterner;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import util.Tuple;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.Map;
import java.util.Properties;


/**
 * Created by zhangyun on 4/22/15.
 * The configuration of project, it is used to add
 * some configuration options
 */
public class LAConf extends Configuration {
  private static Log LOG = LogFactory.getLog(LAConf.class);
  private static String hadoopConfPath;
  private static Properties props = prop();

  private static final String username = props.getProperty("username") ;
  private static final String dsf = props.getProperty("fs.defaultFS") ;
  private static Tuple<String,String> NMLOG = new Tuple<String,String>
          ("yarn.nodemanager.remote-app-log-dir",
                  props.getProperty("yarn.node.manager.remote-app-log-dir"));
  private static Tuple<String,String> JHLOG = new Tuple<String,String>
          ("mapreduce.jobhistory.done-dir",
                  props.getProperty("mapreduce.jobhistory.done-dir"));

  /**
   * Preprocessing
   */
  static {
    for (Map.Entry<String, String> env : System.getenv().entrySet()) {
      if (env.getKey().equals("HADOOP_CONF_DIR") || env.getKey().equals("HADOOP_CONF")) {
        LOG.info("Found HADOOP_CONF_DIR value in env");
        hadoopConfPath = env.getValue();
        break;
      }
    }
  }

  public LAConf(){
    super();
    parseConfiguration();
  }

  private static Properties prop(){
    Properties  props = new Properties();
     File[] propFiles = new File("./hadoop/main/resources/").listFiles(
             new FilenameFilter() {
       @Override
       public boolean accept(File dir, String name) {
         return name.endsWith(".properties");
       }
     });

    try {
      for (File file : propFiles) {
        LOG.info("Loading configuration from : " + file.getAbsolutePath());
        FileInputStream fis = new FileInputStream(file.getAbsoluteFile());
        props.load(fis);
        fis.close();
      }
    }catch (IOException e){
      LOG.error("The configuration could not load!");
    }
    return props;
  }

  private void parseConfiguration( ){
    String yarnSite = hadoopConfPath + "/yarn-site.xml";
    String ylaPath = parseConfiguration( yarnSite , NMLOG.getKey());
    NMLOG.setValue(ylaPath);
    System.out.println(">>>>>>>" +NMLOG.getKey() +"   " +NMLOG.getValue());
    String jhPath = hadoopConfPath + "/mapred-site.xml";
    String jobHistoryPath = parseConfiguration( jhPath , JHLOG.getKey());
    JHLOG.setValue(jobHistoryPath);
    System.out.println(">>>>>>>" +JHLOG.getKey() +"   " +JHLOG.getValue());
  }

  /**
   * Parsing hadoop configuration files ,in this method is used to parse
   * yarn-site.xml and mapred-site.xml files
   * @param logFilePath
   * @param name
   * @return
   */
  private String parseConfiguration( String logFilePath , String name){
    try {
      DocumentBuilderFactory docBuilderFactory
              = DocumentBuilderFactory.newInstance();

      DocumentBuilder builder = docBuilderFactory.newDocumentBuilder();

      File file = new File(logFilePath);
      Document doc = builder.parse(file);
      if (doc == null) {
        throw new RuntimeException("Configuration files parsing failed!");
      }
      Element root = doc.getDocumentElement();

      if (!"configuration".equals(root.getTagName()))
        LOG.fatal("bad conf file: top-level element not <configuration>");

      NodeList props = root.getChildNodes();
      for (int i = 0; i < props.getLength(); i++) {
        Node propNode = props.item(i);

        if (!(propNode instanceof Element)) {
          continue;
        }

        Element prop = (Element) propNode;
        if (!"property".equals(prop.getTagName()))
          LOG.warn("bad conf file: element not <property>");
        NodeList fields = prop.getChildNodes();

        boolean found = false;
        for (int j = 0; j < fields.getLength(); j++) {
          Node fieldNode = fields.item(j);
          if (!(fieldNode instanceof Element))
            continue;
          Element field = (Element) fieldNode;
          if ("name".equals(field.getTagName()) && field.hasChildNodes()) {
            String attr = StringInterner.weakIntern(
                    ((Text) field.getFirstChild()).getData().trim());
            if( attr.equals(name)) {
              found = true;
            }
          }
          if ("value".equals(field.getTagName()) && field.hasChildNodes()) {
            String value = StringInterner.weakIntern(
                    ((Text) field.getFirstChild()).getData());
            if( ! value.equals("null") && found) {
              return value;
            }
          }
        }
      }
    } catch (IOException e){
      LOG.debug("Configuration files IO  error!");
      e.printStackTrace();
    } catch( ParserConfigurationException e){
      LOG.debug("Parse configuration files error!");
      e.printStackTrace();
    }catch(SAXException e){
      LOG.debug("Parse configuration files Exception!");
      e.printStackTrace();
    }
    return null;
  }

  public String getYarnLogPath(){
    return NMLOG.getValue();
  }

  public String getJHLogPath(){
    return JHLOG.getValue();
  }

  public String getDfs(){
    return dsf;
  }

  public String getUsername(){ return username;}
}



