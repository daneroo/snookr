#!/bin/sh

function logStderr { #echo to stderr.
    echo $1 1>&2
}

function doMesh {
    of=$1;
    if=$2;

    if [ $if -eq $of ]; then 
        return;
    fi
    rm -rf work;
    ./scripts/buckyMoire.sh -F $of -f $if -n 120 -w 800 -h 600 -t T_Chrome_5A
    mv work/*.mpg output
    mv work/thumb*.png output
    mv work/still*.png output
}

mkdir -p output

for o in 24 27 30 33 36 39 42 45 48; do
    for i in 24 27 30 33 36 39 42 45 48; do
        doMesh $o $i;
    done
done




