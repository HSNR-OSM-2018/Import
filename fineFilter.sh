
osmconvert $1 -o=./output135.o5m
osmfilter ./output135.o5m —drop-version —drop-author -o=./output135.osm
rm -f $1
osmconvert ./output135.osm -o=$1
rm -f ./output135.o5m
rm -f ./output135.osm
