package in.dream_lab.bm.stream_iot.storm.topo.apps;



import in.dream_lab.bm.stream_iot.storm.bolts.ETL.TAXI.SenMLParseBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.IoTStatsBolt.BlockWindowAverageBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.IoTStatsBolt.DistinctApproxCountBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.IoTStatsBolt.KalmanFilterBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.IoTStatsBolt.MultiLinePlotBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.IoTStatsBolt.SimpleLinearRegressionPredictorBolt;                                                                                                                                                                                                                                                                                                                                                                  
import in.dream_lab.bm.stream_iot.storm.genevents.factory.ArgumentClass;
import in.dream_lab.bm.stream_iot.storm.genevents.factory.ArgumentParser;
import in.dream_lab.bm.stream_iot.storm.sinks.Sink;
import in.dream_lab.bm.stream_iot.storm.spouts.SampleSenMLSpout;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.apache.storm.utils.Utils;

/**
 * @author shilpa
 *
 */
public class StatsWithVisualizationTopology 
{

    public static void main(String[] args) throws Exception {

        ArgumentClass argumentClass = ArgumentParser.parserCLI(args);
        if (argumentClass == null) {
            System.out.println("ERROR! INVALID NUMBER OF ARGUMENTS");
            return;
        }

        String logFilePrefix = argumentClass.getTopoName() + "-" + argumentClass.getExperiRunId() + "-" + argumentClass.getScalingFactor() + ".log";
        String sinkLogFileName = argumentClass.getOutputDirName() + "/sink-" + logFilePrefix;
        String spoutLogFileName = argumentClass.getOutputDirName() + "/spout-" + logFilePrefix;
        String taskPropFilename=argumentClass.getTasksPropertiesFilename();
 
        Config conf = new Config();
        conf.setDebug(false);
        Properties p_=new Properties();
        InputStream input = new FileInputStream(taskPropFilename);
        p_.load(input);


        TopologyBuilder builder = new TopologyBuilder();
        
        String basePathForMultipleSpout="/home/anshu/shilpa/code/data/SYS-6hrs-10filles-splitted-data/";
// 	   String basePathForMultipleSpout="/home/shilpa/Datasets/CityCanvasData/SYS_6hrs_data_10Files/SYS-inputcsv-predict-10spouts200mps-480sec-file/";
 		 
        System.out.println("basePathForMultipleSpout is used -"+basePathForMultipleSpout);
        String spout1InputFilePath=basePathForMultipleSpout+"SYS-inputcsv-predict-10spouts200mps-480sec-file1.csv";
        String spout2InputFilePath=basePathForMultipleSpout+"SYS-inputcsv-predict-10spouts200mps-480sec-file2.csv";
        String spout3InputFilePath=basePathForMultipleSpout+"SYS-inputcsv-predict-10spouts200mps-480sec-file3.csv";

        String spout4InputFilePath = basePathForMultipleSpout+"SYS-inputcsv-predict-10spouts200mps-480sec-file4.csv";
        String spout5InputFilePath=basePathForMultipleSpout+"SYS-inputcsv-predict-10spouts200mps-480sec-file5.csv";
        String spout6InputFilePath=basePathForMultipleSpout+"SYS-inputcsv-predict-10spouts200mps-480sec-file6.csv";
        String spout7InputFilePath=basePathForMultipleSpout+"SYS-inputcsv-predict-10spouts200mps-480sec-file7.csv";
        String spout8InputFilePath=basePathForMultipleSpout+"SYS-inputcsv-predict-10spouts200mps-480sec-file8.csv";
        String spout9InputFilePath=basePathForMultipleSpout+"SYS-inputcsv-predict-10spouts200mps-480sec-file9.csv";
        String spout10InputFilePath=basePathForMultipleSpout+"SYS-inputcsv-predict-10spouts200mps-480sec-file10.csv";
 		 		 
//        builder.setSpout("spout1", new SampleSenMLSpout(spout1InputFilePath, spoutLogFileName, argumentClass.getScalingFactor()),
//                1);
//        builder.setSpout("spout2", new SampleSenMLSpout(spout2InputFilePath, spoutLogFileName, argumentClass.getScalingFactor()),
//                1);
//        builder.setSpout("spout3", new SampleSenMLSpout(spout3InputFilePath, spoutLogFileName, argumentClass.getScalingFactor()),
//                1);
//        builder.setSpout("spout4", new SampleSenMLSpout(spout4InputFilePath, spoutLogFileName, argumentClass.getScalingFactor()),
//                1);
//        builder.setSpout("spout5", new SampleSenMLSpout(spout5InputFilePath, spoutLogFileName, argumentClass.getScalingFactor()),
//                1);
//        builder.setSpout("spout6", new SampleSenMLSpout(spout6InputFilePath, spoutLogFileName, argumentClass.getScalingFactor()),
//                1);
//        builder.setSpout("spout7", new SampleSenMLSpout(spout7InputFilePath, spoutLogFileName, argumentClass.getScalingFactor()),
//                1);
//        builder.setSpout("spout8", new SampleSenMLSpout(spout8InputFilePath, spoutLogFileName, argumentClass.getScalingFactor()),
//                1);
//        builder.setSpout("spout9", new SampleSenMLSpout(spout9InputFilePath, spoutLogFileName, argumentClass.getScalingFactor()),
//                1);
//        builder.setSpout("spout10", new SampleSenMLSpout(spout10InputFilePath, spoutLogFileName, argumentClass.getScalingFactor()),
//                1);
//        
        builder.setSpout("spout", new SampleSenMLSpout(argumentClass.getInputDatasetPathName(), spoutLogFileName, argumentClass.getScalingFactor()),
                1);
//        
        builder.setBolt("ParseSenML",
                new SenMLParseBolt(p_), 10)
                	.shuffleGrouping("spout");
//			        .shuffleGrouping("spout2")
//			        .shuffleGrouping("spout3")
//			        .shuffleGrouping("spout4")
//			        .shuffleGrouping("spout5")
//			        .shuffleGrouping("spout6")
//			        .shuffleGrouping("spout7")
//			        .shuffleGrouping("spout8")
//			        .shuffleGrouping("spout9")
//			        .shuffleGrouping("spout10");
//         
        builder.setBolt("BlockWindowAverageBolt",
                new BlockWindowAverageBolt(p_),1)
                .fieldsGrouping("ParseSenML",new Fields("SENSORID","OBSTYPE"));
        
        builder.setBolt("KalmanFilterBolt",
                new KalmanFilterBolt(p_), 1)
                .fieldsGrouping("ParseSenML",new Fields("SENSORID","OBSTYPE"));

        builder.setBolt("SimpleLinearRegressionPredictorBolt",
                new SimpleLinearRegressionPredictorBolt(p_), 1)
                .fieldsGrouping("KalmanFilterBolt",new Fields("SENSORID","OBSTYPE"));

        builder.setBolt("DistinctApproxCountBolt",
                new DistinctApproxCountBolt(p_), 1)
                .fieldsGrouping("ParseSenML",new Fields("OBSTYPE")); // another change done already

        builder.setBolt("Visualization",
        		new MultiLinePlotBolt(p_), 1)
		        .fieldsGrouping("BlockWindowAverageBolt",new Fields("SENSORID","OBSTYPE") )
		        .fieldsGrouping("SimpleLinearRegressionPredictorBolt", new Fields("SENSORID","OBSTYPE"))
		        .fieldsGrouping("DistinctApproxCountBolt" , new Fields("OBSTYPE"));
        
//        builder.setBolt("AzureBlobUploadTaskBolt",
//                new AzureBlobUploadTaskBolt(p_), 1)
//                .shuffleGrouping("Visualization");
//        
        builder.setBolt("sink", new Sink(sinkLogFileName), 1)
                       .shuffleGrouping("Visualization");


        StormTopology stormTopology = builder.createTopology();

        if (argumentClass.getDeploymentMode().equals("C")) {
            StormSubmitter.submitTopology(argumentClass.getTopoName(), conf, stormTopology);
        } else {
            LocalCluster cluster = new LocalCluster();
            
            cluster.submitTopology(argumentClass.getTopoName(), conf, stormTopology);
            Utils.sleep(7800000);
            System.out.println("Killing topo");
            cluster.killTopology(argumentClass.getTopoName());
            cluster.shutdown();
        }
    }
}
