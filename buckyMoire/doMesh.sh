#!/bin/sh

function logStderr { #echo to stderr.
    echo $1 1>&2
}

function doMesh {
    a=$1;
    of=$2;
    if=$3;

    if [ $if -eq $of ]; then 
        return;
    fi
    rm -rf work;
    ./scripts/buckyMoire.sh -a $a -F $of -f $if -n 120 -w 720 -h 480 -t T_Chrome_5A
    mv work/*.mpg output/mesh-$a
    mv work/thumb*.png output/mesh-$a
    mv work/still*.png output/mesh-$a
}


for a in 1.778 1.333 ; do
    mkdir -p output/mesh-$a
    for o in 24 27 30 33 36 39 42 45 48; do
	for i in 24 27 30 33 36 39 42 45 48; do
	    doMesh $a $o $i;
	done
    done
done




