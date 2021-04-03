#!/bin/bash

javac dbload.java 
javac dbquery.java



# function test_all() {
#     pageSizes=( 512 1024 2048 4096 8192 16384 32768 65536 131072 )
# }

if [ "$1" = "all" ]; then
    pageSizes=( 512 1024 2048 4096 8192 16384 32768 65536 131072 )
else
    pageSizes=( $1 )
fi



echo "Testing Loading"
for i in "${pageSizes[@]}"
do
    echo "java dbload -p $i ../data.csv "
    java dbload -p $i ../data.csv 
done


echo "Testing Query"
for i in "${pageSizes[@]}"
do
    echo "java dbquery FFFFFFF $i "
    java dbquery FFFFFFF $i 
done

