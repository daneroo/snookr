#!/bin/sh

ffmpegExec=./ffmpeg/ffmpeg/ffmpeg

function logStderr { #echo to stderr.
    echo $1 1>&2
}

function annotate {
  f=$1;
  msg=$2;

  echo annotate $f : $msg
  cp -p $f work/tmp.png
  # convert $f $f : seems to work : writing over input
  convert -font DejaVu-LGC-Sans-Bold work/tmp.png  -fill white  -undercolor '#00000080'  -gravity South-West -annotate +20+20 " $msg "     $f;
}
function doStory {
    a=$1;

    rm -rf work
    # for each part (1-9)
    for i in `seq -f %02g 1 9`; do
        echo "story a/r: $a part $i";
        mkdir -p work/p$i;
        $ffmpegExec -i renders/story-$a/part$i-*.mpg work/p$i/frame-%04d.png
    done

    local currentFrame=0;
    mkdir -p work/all
    for d in p01 p01 p01 p02 p03 p03 p03 p04 p05 p05 p05 p06 p07 p07 p07 p08 p09 p09 p09; do
        for i in work/$d/*.png; do
            file=`printf "work/all/all-%04d.png" $currentFrame`;
            echo $i linked to $file
            ln -f $i $file
            let currentFrame=currentFrame+1;
            
            #msg=`printf "Story-%04d" $currentFrame`;
            #annotate $file $msg;
        done
    done  

    $ffmpegExec -r 30 -i work/all/all-%04d.png -aspect $a -b 9000 -y output/story-$a.mpg
}

function doMesh {
    a=$1;
    of=$2;
        
    echo "doMesh: $of $a"
    
    rm -rf work;
    local currentFrame=0;
    mkdir -p work/all
    for if in 24 27 30 33 36 39 42 45 48; do
        if [ $if -eq $of ]; then 
            continue;
        fi
        echo "  doMesh: $of $if $a"
        wkdir="work/F$of$if";
        mkdir -p $wkdir
        $ffmpegExec -i renders/mesh-$a/buckyMoireF$of$if-*.mpg $wkdir/frame-%04d.png

        for i in $wkdir/*.png; do
            file=`printf "work/all/all-%04d.png" $currentFrame`;
            echo $i linked to $file
            ln -f $i $file
            let currentFrame=currentFrame+1;
            msg="[$of-$if]"
            annotate $file $msg;
        done
    done
    $ffmpegExec -r 30 -i work/all/all-%04d.png -aspect $a -b 9000 -y output/mesh-F$of-$a.mpg

}


for a in 1.778 1.333 ; do

    mkdir -p output
    doStory $a;

    for o in 24 27 30 33 36 39 42 45 48; do
        doMesh $a $o;
    done
done
