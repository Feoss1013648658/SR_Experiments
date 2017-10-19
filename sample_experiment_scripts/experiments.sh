#! /bin/bash


### EXP1
#declare -a arr_query=(1 2 10)
#for i in "${arr_query[@]}" 
#do
#	java -Xms5g -Xmx20g -jar CityBench.jar rate=1.0 frequency=1.0 duration=10m queryDuplicates=1 startDate=2014-08-11T11:00:00 endDate=2014-10-01T00:00:00 engine=csparql query=Q${i}C.txt  > logs/logQ${i}C.log
	
#	java -Xms5g -Xmx20g -jar CityBench.jar rate=1.0 frequency=2.0 duration=10m queryDuplicates=1 startDate=2014-08-11T11:00:00 endDate=2014-10-01T00:00:00 engine=csparql query=Q${i}C.txt  > logs/logQ${i}C.log
	
#	java -Xms5g -Xmx20g -jar CityBench.jar rate=1.0 frequency=1.0 duration=10m queryDuplicates=1 startDate=2014-08-11T11:00:00 endDate=2014-10-01T00:00:00 engine=tsreasoner query=R${i}C.txt > logs/logR${i}C.log	
	
#	java -Xms5g -Xmx20g -jar CityBench.jar rate=1.0 frequency=2.0 duration=10m queryDuplicates=1 startDate=2014-08-11T11:00:00 endDate=2014-10-01T00:00:00 engine=tsreasoner query=R${i}C.txt > logs/logR${i}C.log	
	
#done
#echo "DONE-EXP1"


### EXP2

##streaming test: lubm
declare -a arr_rate=(1000 3000 5000 10000)
for j in "${arr_rate[@]}" 
do

   	java -Xms5g -Xmx20g -jar CityBench.jar rate=$j frequency=1.0 duration=10m queryDuplicates=1 startDate=2014-08-11T11:00:00 endDate=2014-10-01T00:00:00 engine=tsreasoner query=lubm.txt > logs/log_lubm_${j}.log
   			
   	java -Xms5g -Xmx20g -jar CityBench.jar rate=$j frequency=1.0 duration=10m queryDuplicates=1 startDate=2014-08-11T11:00:00 endDate=2014-10-01T00:00:00 engine=ptsreasoner query=lubm.txt > logs/log_lubm_p_${j}.log

done
echo "DONE_lubm_stream"


##static test: lubm

declare -a arr_rate=(5000 10000 30000 60000 100000)

java -Xms5g -Xmx20g -jar CityBench_jena.jar > logs/log_lubm_static_jena.log

for j in "${arr_rate[@]}"
do
   		   		
   		java -Xms5g -Xmx20g -jar CityBench.jar rate=$j frequency=1.0 duration=0m queryDuplicates=1 startDate=2014-08-11T11:00:00 endDate=2014-10-01T00:00:00 engine=tsreasoner query=lubm_static_${j}.txt > logs/log_lubm_static_${j}.log
   		
   		java -Xms5g -Xmx20g -jar CityBench.jar rate=$j frequency=1.0 duration=0m queryDuplicates=1 startDate=2014-08-11T11:00:00 endDate=2014-10-01T00:00:00 engine=ptsreasoner query=lubm_static_${j}.txt > logs/log_lubm_static_${j}.log
   		
done 


##EXP3

declare -a arr_rate=(1000 3000 5000 10000)
for j in "${arr_rate[@]}" 
do
		
   	java -jar CityBench.jar rate=$j frequency=1.0 duration=10m queryDuplicates=1 startDate=2014-08-11T11:00:00 endDate=2014-10-01T00:00:00 engine=tsreasoner query=lubm_neg.txt > logs/log_lubm_neg_${j}.log
   		
   	java -jar CityBench.jar rate=$j frequency=1.0 duration=10m queryDuplicates=1 startDate=2014-08-11T11:00:00 endDate=2014-10-01T00:00:00 engine=ptsreasoner query=lubm_neg.txt > logs/log_lubm_neg_p_${j}.log
   		
   	
done 
echo "DONE_EXP3"

echo "DONE ALL TEST!!!!"
