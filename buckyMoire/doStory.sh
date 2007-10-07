#!/bin/sh

function logStderr { #echo to stderr.
    echo $1 1>&2
}

function blend {
    # assume directories dirA and dirB both have exactly N images...
    # take two directories with N images (cyclic animations)
    # make a new directory with composited (progressive dissolve) version of both
    local numFrames=$1;
    local dirA=$2;
    local dirB=$3;
    local outDir=$4;
    
    local wka=work/seqA
    local wkb=work/seqB

    rm -rf $wka $wkb
    mkdir -p $wka
    mkdir -p $wkb
    mkdir -p $outDir;

    # this just renumbers frames
    convert $dirA/*.png   work/seqA/seqA-%03d.png
    convert $dirB/*.png   work/seqB/seqB-%03d.png

    # number of digits: 0-9->1 10-99->2 100-999->3 etc
    # digits:== floor ( log10(numFrames) )
    local digits=`echo "t=(l($numFrames)/l(10));scale=0;print t/1"|bc -l`;
    local nfm1=`echo "scale=0;$numFrames-1"|bc -l`;
    for i in `seq -f %0${digits}g 0 $nfm1`; do
        percent=`echo "scale=0;100*$i/120"|bc`;
        complement=`echo "scale=3;100-$percent"|bc`;
        echo AB $i $percent $complement
        composite -dissolve ${percent}x${complement} $workDir/seqB/seqB-$i.png $workDir/seqA/seqA-$i.png $workDir/seqAB/seqAB-$i.png
    done
    
    #clean up
    rm -rf $wka $wkb

}

function doStory {
    a=$1;

    local numFrames=12;
    commonargs="-n ${numFrames} -w 90 -h 60 -a $a";

    rm -rf work;
    # These three scripts are identical except for 
    #  workdir, basename and Cam_Factor

    # make work/glassCam0.9
    ./scripts/justMoireGlassCam0.9.sh $commonargs
    # make work/zoomOutGlass
    ./scripts/zoomOutGlass.sh $commonargs
    # make work/glassCam1.4
    ./scripts/justMoireGlassCam1.4.sh $commonargs

    # blend glassCam-1.4 / C4F9081
    ./scripts/justMoireC4.sh $commonargs -F 90 -f 81 -C 0.005 -c 0.005 -S 0.005 -s 0.005
    mv work/C4 work/C4F9081
    # blend C4F9081 C4F4527
    ./scripts/justMoireC4.sh $commonargs -F 45 -f 27 -C 0.01 -c 0.01 -S 0.01 -s 0.01
    mv work/C4 work/C4F4527

    ./scripts/zoomOutC4.sh $commonargs  -F 45 -f 27 -C 0.01 -c 0.01 -S 0.01 -s 0.01
    ./scripts/fullerMoire.sh $commonargs -F 45 -f 27 -C 0.01 -c 0.01 -S 0.01 -s 0.01

    # blend glassCam-1.4 C4F9081 
    blend $numFrames work/glassCam1.4 work/C4F9081  work/blendGlassToC4
    # ffmpeg ?

    # blend C4F9081 C4F4527
    blend $numFrames work/C4F9081 work/C4F4527 work/blend90To45
    # ffmpeg ?

    # copy and blend into output/story-$a
    #mv work/*/*.mpg output/story-$a

    # rm -rf work

}


#for a in 1.778 1.333 ; do
for a in 1.778  ; do
    mkdir -p output/story-$a
    doStory $a;
done




