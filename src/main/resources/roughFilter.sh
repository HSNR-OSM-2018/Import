#!/bin/sh

osmconvert $1 -o=output123.o5m
osmfilter ./output123.o5m --drop-relations --drop-author --drop-version --keep="highway=motorway =motorway_link =trunk =trunk_link =primary =primary_link =secondary =secondary_link =tertiary =tertiary_link =living_street =residential =unclassified =service =road" -o=./output456.osm
osmfilter ./output456.osm --drop-ways --drop-tags="*=" -o=./outputNode123.osm
osmfilter ./output456.osm --drop-nodes -o=./outputWay123.osm
filter='Filter.pbf'
node='FilterNode.pbf'
way='FilterWay.pbf'
osmconvert ./output456.osm -o=$2$filter
osmconvert ./outputNode123.osm -o=$2$node
osmconvert ./outputWay123.osm -o=$2$way
rm -f ./output456.osm
rm -f ./outputNode123.osm
rm -f ./outputWay123.osm
rm -f ./output123.o5m