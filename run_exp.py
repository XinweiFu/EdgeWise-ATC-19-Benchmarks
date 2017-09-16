import os
import sys
import subprocess
import time
import script
import tarfile
import shutil
import csv

storm_exe = os.environ['STORM']
jar_name = sys.argv[1]
in_topo_name = sys.argv[2]

num_experiments = 5
input_rates = [60, 120, 360, 720, 1440]
num_events = [21600, 43200, 129600, 259200, 518400]

property_files = {
		 "etl" : "etl_topology.properties",
		 "pred" : "tasks_CITY.properties",
		 "stat" : "stats_with_vis_topo.properties",
		 "train" : "iot_train_topo_city.properties"
		 }

topo_qualified_path = {
			"etl" : "in.dream_lab.bm.stream_iot.storm.topo.apps.ETLTopology",
			"pred" : "in.dream_lab.bm.stream_iot.storm.topo.apps.IoTPredictionTopologySYS",
			"stat" : "in.dream_lab.bm.stream_iot.storm.topo.apps.StatsWithVisualizationTopology",
			"train" : "in.dream_lab.bm.stream_iot.storm.topo.apps.IoTTrainTopologySYS",
			"wordcount" : "in.dream_lab.bm.stream_iot.storm.topo.apps.ETLTopology"
			}

data_files = {
	      "etl" : "SYS_sample_data_senml.csv",
	      "train" : "inputFileForTimerSpout-CITY.csv",
	      "pred" : "SYS_sample_data_senml.csv",
	      "stat" : "SYS_sample_data_senml.csv"
	     }

data_file = data_files[in_topo_name]
topo = topo_qualified_path[in_topo_name]
path = "/home/fuxinwei/iot"
pi_outdir = "/home/pi/topo_run_outdir/reg-SYS"
os.chdir(path)
prop_file = property_files[in_topo_name]
exp_result_dir = "/home/fuxinwei/iot/experiment_results"

# Clean output directories on PIs
cmd = "dsh -aM -c rm " + pi_outdir + "/*"
process = subprocess.Popen(cmd.split(), stdout=subprocess.PIPE)
output, error = process.communicate()

if error is None:
	print "Delected output directories on PIs"

# Run the experiments now.
commands = []
executed_topologies=[]

for i in range(len(input_rates)):
	jar_path  = os.getcwd() + "/" + jar_name
	topo_unique_name = in_topo_name + "_" + str(input_rates[i])

	executed_topologies.append(topo_unique_name)

	command_pre = storm_exe + " jar " + jar_path + " " + topo + " C " +  topo_unique_name  + " " + data_file + " 1 1 " + pi_outdir + " " + prop_file + " " + topo_unique_name
	commands.append(command_pre + " " + str(input_rates[i]) + " " + str(num_events[i]))
	
	print commands[i] + "\n"
	process = subprocess.Popen(commands[i].split(), stdout=subprocess.PIPE)
	output, error = process.communicate()
	#print output

spout_files = []
sink_files = []
for i in range(len(executed_topologies)):
	spout_files.append(pi_outdir + "/" + "spout-" + executed_topologies[i] + "-1-1.0.log")
	sink_files.append(pi_outdir + "/" + "sink-" + executed_topologies[i] + "-1-1.0.log")


#print spout_files
#print sink_files

# Experiments started....
# Wait until they're done on the PIs (~ 12 minutes)

start = time.time()
time.sleep(12 * 60) #for now, make do with 10 minutes
end = time.time()
print "waited for: " + str(end-start) + " secs"

# Check which experiment was run on which PI device...
devices = []
for i in range(len(spout_files)):
	cmd = "dsh -aM -c ls " + spout_files[i]
	print cmd + "\n"
	process = subprocess.Popen(cmd.split(), stdout=subprocess.PIPE)
	output, error = process.communicate()
	devices.append(output.split(":")[0])
	#print "Printing output\n\n"
	#print output	

if not os.path.isdir(exp_result_dir):
	os.mkdir(exp_result_dir)


# Copy result files from the R.PIs to this machine
for i in range(len(devices)):
	#cmd = "scp " + devices[i] + ":\\{" + spout_files[i] + "," + sink_files[i] + "\\} " + exp_result_dir+"/"
	process = subprocess.Popen(["scp", devices[i] + ":" + spout_files[i], exp_result_dir+"/"], stdout=subprocess.PIPE)
	output, error = process.communicate()
	# check for errors/output
	process = subprocess.Popen(["scp", devices[i] + ":" + sink_files[i], exp_result_dir+"/"], stdout=subprocess.PIPE)
	output, error = process.communicate()
	# check for errors/outpu


# output files have been copied to this machine
# run script on these files to generate results...

csv_file_name = "experiment-"+jar_name+"-"+in_topo_name+".csv"

os.chdir(exp_result_dir)
with open (csv_file_name, "a") as csv_file:
	for i in range(num_experiments):
		spout_file = spout_files[i].split("/")[-1]
		sink_file = sink_files[i].split("/")[-1]
		resultObject = script.get_results(executed_topologies[i], spout_file, sink_file)
		results = resultObject.get_csv_rep(in_topo_name)
		csv_file.write(executed_topologies[i])
		csv_file.write("\n")
        	for line in results:
			csv_file.write(line)
			csv_file.write("\n")
	#TODO: store these results in a csv file...

# get all the spout/sink logs and generated images and archive them.
all_files = os.listdir(os.getcwd())
tarfile_name = "experiment-"+jar_name+"-"+in_topo_name+ "-" + time.strftime("%Y-%m-%d-%H:%M:%S", time.localtime()) + ".tar"
out = tarfile.open(tarfile_name, mode='w')

try:
	for _file in all_files:
		out.add(_file)
finally:
	out.close()
	
shutil.copy(tarfile_name, path)

# change directory back...
os.chdir(path)
shutil.rmtree(exp_result_dir)
