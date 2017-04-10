#!/bin/bash
for fname in 2 4
do
  for p in 0.01 0.02 0.03 0.05 0.1
  do
    java SlottedAloha2 -N 50 -W $fname -p $p -M 500
  done
done