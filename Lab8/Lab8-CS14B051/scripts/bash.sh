#!/bin/bash

sudo sysctl -w net.ipv4.tcp_window_scaling="1"
for i in "0" "1"
do
	sysctl -w net.ipv4.tcp_sack=$i
	for j in 16777216 268435456
	do
		sysctl -w net.core.wmem_max=$j
		sysctl -w net.core.rmem_max=$j
		for k in reno cubic
		do
			sudo echo $k > /proc/sys/net/ipv4/tcp_congestion_control
			for l in 2ms 50ms
			do
				tc qdisc add dev wlo1 root netem delay $l
				for m in 0.5% 5%
				do
					tc qdisc change dev wlo1 root netem loss $m
					python get_values.py
				done
				tc qdisc del dev wlo1 root
			done
		done
	done
done
