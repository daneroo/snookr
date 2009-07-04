#!/bin/bash                                                                                             
# this file reads a usb serial port
# - first set its params (speed)
# e.g. 
#   read:  stty -F /dev/ttyUSB1 speed
#   write: stty -F /dev/ttyUSB1 speed 57600
# check for non-zero return code: USB does not exist, perm denied...
# read each line

# usage
#  ./serialStampRotate /dev/ttyUSB1 57600 CC1
#  ./serialStampRotate /dev/ttyUSB0 38400 AZ2

# chaeck parameter here: add outputdir ?

# Notes
#  exit status : $?
#  open for read and write: [j]<>filename a.k.a. : exec 3<> /dev/ttyUSB1
#    read -n 4 <&3
#    echo -n . >&3
#  exec 4>&1                              # Save current "value" of stdout.

# this is the name of the current logrotated file
#OUTPUTSTAMPFORMAT="%Y%m%dT%H%M%S%z"
OUTPUTSTAMPFORMAT="%Y%m%dT%H%M00%z"
OUTPUTFILE="DEVICE-2001-02-03T040506+0400.log"

# this should be the /dev/ttyUSBx
#INPUTFILE="../CC2-20090701T225213-0400.log"
INPUTFILE=${1:-"/dev/ttyUSB-NOTEXIST"}
INPUTFILEDESCRIPTOR=3
# use FileDescriptor 3 for input tty: (output too for Aztech)
exec 3<> "${INPUTFILE}"

TTYSPEED=${2:-"57600"}
DEVICEALIAS=${3:-"DEVICE"}
# this is the read timeout: in seconds: has  no  effect if read is not reading input from  the terminal or a pipe
READTIMEOUT=5
# this funcion wraps the line content (pased as argument
stampline() {
    echo `date +%Y-%m-%dT%H:%M:%S%z` $1; 
}

rotatelog() {
    STAMP=`date +${OUTPUTSTAMPFORMAT}`
    OUTPUTFILE="${DEVICEALIAS}-${STAMP}.log"
    # if file already exists, simply return
    if [ -w ${OUTPUTFILE} ]; then
	#echo file ${OUTPUTFILE} already exists and is writeable
	return;
    fi
    # touch the file make sure it exists and is writeable
    touch "${OUTPUTFILE}"
    if [ -w ${OUTPUTFILE} ]; then
	#echo Creating new log file ${OUTPUTFILE}
	echo "<!-- `hostname`: `date +%Y-%m-%dT%H:%M:%S%z`: reading ${INPUTFILE} @ ${TTYSPEED}bps for device ${DEVICEALIAS} -->">>${OUTPUTFILE}
	return
    else
	# could not create outputfile: ERROR
	echo "Could Not Create Output File : ${OUTPUFILE}" >&2
	exit 1
    fi

}

#outer forever loop
while true; do
    rotatelog
    # inner loop -- use -u fd, to read from a fd instead.
    while read -t ${READTIMEOUT} -u 3 line; do 
	stampline "$line"
    done >>"$OUTPUTFILE"

done
