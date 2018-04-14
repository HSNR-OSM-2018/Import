#!/bin/sh

osmconvert $1 -o=output123.o5m
osmfilter ./output123.o5m --drop-relations --drop-author --drop-version --keep="highway=motorway =motorway_link =trunk =trunk_link =primary =primary_link =secondary =secondary_link =tertiary =tertiary_link =living_street =residential =unclassified =service =road" -o=./output456.o5m
osmfilter ./output456.o5m --drop-ways --drop-tags="*=" -o=./outputNode123.o5m
osmfilter ./output456.o5m --drop-nodes -o=./outputWay123.o5m
filter='All.pbf'
node='Node.pbf'
way='Way.pbf'
osmconvert ./output456.o5m -o=$2$filter
osmconvert ./outputNode123.o5m -o=$2$node
osmconvert ./outputWay123.o5m -o=$2$way
rm -f ./output456.o5m
rm -f ./outputNode123.o5m
rm -f ./outputWay123.o5m
rm -f ./output123.o5m