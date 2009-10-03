#!/usr/bin/env python
#


from xml.dom import minidom, Node

def getText(nodelist):
    rc = ""
    for node in nodelist:
        if node.nodeType == node.TEXT_NODE:
            rc = rc + node.data
    return rc

# make a list of stamp,value pairs
def parseIPhonePlistObsData(fileName):
  parsedPairs = [] # wil contain [[stamp1,value1], [stamp2,value2],]
  dom = minidom.parse(fileName)
  # plist/array/dict*/(key,date,key,integer)
  alldicts = dom.getElementsByTagName("dict")
  
  for dict in alldicts:
    stamp = getText(dict.getElementsByTagName('date')[0].childNodes)
    value = getText(dict.getElementsByTagName('integer')[0].childNodes)
    #print "found an observation: %s %s" % (stamp,value)
    parsedPairs.append( [stamp,value] )

  return parsedPairs


def main():
  print "# Converting weightrical observation data"
  #  python convert.py > weightrical-data.txt
  observations = parseIPhonePlistObsData('observationdata-20091002.xml')
  print "# %s, %s" % ('stamp','value')
  for obs in observations:
    print "%s, %s" % (obs[0],obs[1])

if __name__ == '__main__':
  main()
