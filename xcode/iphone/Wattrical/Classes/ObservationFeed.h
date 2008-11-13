//
//  ObservationFeed.h
//  Wattrical
//
//  Created by Daniel Lauzon on 12/11/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

/* First Implenetation
    This is because of the flaky behaviour of
        [NSMutableArray arrayWithContentsOfURL:aURL];

 This Class Is Meant to produce and Observation Array, from an xml plist:
 
     <?xml version="1.0"?><!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
     <plist version="1.0"><array>
     <dict><key>stamp</key><date>2008-11-13T12:47:49Z</date><key>value</key><integer>432</integer></dict>
     <dict><key>stamp</key><date>2008-11-13T04:47:49Z</date><key>value</key><integer>9045</integer></dict>
     <dict><key>stamp</key><date>2008-11-12T20:47:49Z</date><key>value</key><integer>5523</integer></dict>
     </array></plist>
 
     Using the event based processing of NSXMLParser
    with delegate callbacks:
     - (void)parserDidStartDocument:(NSXMLParser *)parser;
     - (void)parser:(NSXMLParser *)parser parseErrorOccurred:(NSError *)parseError;
     - (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName attributes:(NSDictionary *)attributeDict;
     - (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName;
     - (void)parser:(NSXMLParser *)parser foundCharacters:(NSString *)string;
     - (void)parserDidEndDocument:(NSXMLParser *)parser {

    Make a very specific processor:
    on array start : new NSMutableArray
    keep last   date,integer map them to stamp,value in observation
    on dict end, add new Observation to observations;
 
 */

#import <UIKit/UIKit.h>

@interface ObservationFeed : NSObject {
    NSInteger count;
    NSMutableArray *stack;
    NSMutableArray *observations; // NSMutableArray og Observations
    NSDate *lastStamp;   // last parsed <date/> element
    NSInteger lastValue; // last parsed <integer/> element
}

- (NSMutableArray *)parseXMLFileAtURL:(NSURL *)xmlURL;

@end
