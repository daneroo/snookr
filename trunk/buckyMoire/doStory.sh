#!/bin/sh

ffmpegExec=./ffmpeg/ffmpeg/ffmpeg

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
    local aspectRatio=$5
    
    local wka=work/seqA
    local wkb=work/seqB

    rm -rf $wka $wkb
    mkdir -p $wka
    mkdir -p $wkb
    mkdir -p $outDir;

    echo READY TO BLEND
    echo "dirA:${dirA} dirB:${dirB} "
    echo "wka:${wka} wkb:${wkb} "

    # this just renumbers frames
    convert $dirA/*.png   $wka/seqA-%04d.png
    convert $dirB/*.png   $wkb/seqB-%04d.png

    local nfm1=`echo "scale=0;$numFrames-1"|bc`;
    for i in `seq -f %04g 0 $nfm1`; do
        percent=`echo "scale=0;100*$i/${numFrames}"|bc`;
        complement=`echo "scale=3;100-$percent"|bc`;
        echo AB $i $percent $complement
        composite -depth 8 -dissolve ${percent}x${complement} $wkb/seqB-$i.png $wka/seqA-$i.png $outDir/blendedAB-$i.png
    done
    
    #ffmpeg ??
    $ffmpegExec -r 30 -i $outDir/blendedAB-%04d.png -aspect $aspectRatio -b 9000 -y $outDir/blendedAB.mpg

    #clean up
    #rm -rf $wka $wkb

}

function doStory {
    a=$1;

    local numFrames=120;
    commonargs="-n ${numFrames} -w 720 -h 480 -a $a";

    # CLEANUP
    rm -rf work;

    # These three scripts are identical except for 
    #  workdir, basename and Cam_Factor

    # make work/glassCam0.9
    ./scripts/justMoireGlassCam0.9.sh $commonargs
    # make work/zoomOutGlass
    ./scripts/zoomOutGlass.sh $commonargs
    # make work/glassCam1.4
    ./scripts/justMoireGlassCam1.4.sh $commonargs

    # place of blend glassCam-1.4 / C4F9081

    ./scripts/justMoireC4.sh $commonargs -F 90 -f 81 -C 0.005 -c 0.005 -S 0.005 -s 0.005
    mv work/C4 work/C4F9081
    # place of blend C4F9081 C4F4527
    ./scripts/justMoireC4.sh $commonargs -F 45 -f 27 -C 0.01 -c 0.01 -S 0.01 -s 0.01
    mv work/C4 work/C4F4527

    ./scripts/zoomOutC4.sh $commonargs  -F 45 -f 27 -C 0.01 -c 0.01 -S 0.01 -s 0.01
    ./scripts/fullerMoire.sh $commonargs -F 45 -f 27 -C 0.01 -c 0.01 -S 0.01 -s 0.01

    # do blend glassCam-1.4 C4F9081 
    blend $numFrames work/glassCam1.4 work/C4F9081  work/blendGlassToC4 $a

    # do blend C4F9081 C4F4527
    blend $numFrames work/C4F9081 work/C4F4527 work/blend90To45 $a

    # copy and blend into output/story-$a
    #mv work/*/*.mpg output/story-$a
    cp -p work/glassCam0.9/*mpg      output/story-$a/part01-glassCam0.9-$a.mpg
    cp -p work/zoomOutGlass/*mpg     output/story-$a/part02-zoomOutGlass-$a.mpg
    cp -p work/glassCam1.4/*mpg      output/story-$a/part03-glassCam1.4-$a.mpg
    cp -p work/blendGlassToC4/*mpg   output/story-$a/part04-blendGlassToC4-$a.mpg
    cp -p work/C4F9081/*mpg          output/story-$a/part05-C4F9081-$a.mpg
    cp -p work/blend90To45/*mpg      output/story-$a/part06-blend90To45-$a.mpg
    cp -p work/C4F4527/*mpg          output/story-$a/part07-C4F4527-$a.mpg
    cp -p work/zoomOutC4/*mpg        output/story-$a/part08-zoomOutC4-$a.mpg
    cp -p work/fullerMoire/*mpg      output/story-$a/part09-fullerMoire-$a.mpg

    # rm -rf work

}


#for a in 1.333 1.778 ; do
for a in 1.778 1.333 ; do
    mkdir -p output/story-$a
    doStory $a;
done




