#!/bin/bash
counter=0
for i in 1 4
do
  for m in 1 1.5
  do
  	for n in 0.5 1
  	do
  		for f in 0.1 0.3
  		do
  			for p in 0.01 0.0001
  			do
    			java CongestionControl -i $i -m $m -n $n -f $f -s $p -T 5000 -o outfile+$counter.csv
    			counter=$((counter+1))
    		done
    	done
    done
  done
done