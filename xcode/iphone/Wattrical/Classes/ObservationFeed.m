//
//  ObservationFeed.m
//  Wattrical
//
//  Created by Daniel Lauzon on 12/11/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

#import "ObservationFeed.h"
#import "Observation.h"

@implementation ObservationFeed

- (void) printStack {
    NSEnumerator * enumerator = [stack objectEnumerator];
    NSObject *o;
    if ([stack count]==0) NSLog(@">>>> stack EMPTY");
    while(o = [enumerator nextObject]) {
        //NSLog(@"  >>>>: %@", o);
        NSLog(@"  >>>>: retain %d", [o retainCount]);
    }
    //NSLog(@">>>> stack: STOP   (%d)",[stack count]);
}

- (NSMutableArray *)parseXMLFileAtURL:(NSURL *)xmlURL {
    NSDate *now = [NSDate date];
	//stories = [[NSMutableArray alloc] init];
    
	// here, for some reason you have to use NSClassFromString when trying to alloc NSXMLParser, otherwise you will get an object not found error
	// this may be necessary only for the toolchain
	NSXMLParser *plistParser = [[NSXMLParser alloc] initWithContentsOfURL:xmlURL];
	//NSLog(@"Parser didInit");
    
	// Set self as the delegate of the parser so that it will receive the parser delegate methods callbacks.
	[plistParser setDelegate:self];
    
	// Depending on the XML document you're parsing, you may want to enable these features of NSXMLParser.
	[plistParser setShouldProcessNamespaces:NO];
	[plistParser setShouldReportNamespacePrefixes:NO];
	[plistParser setShouldResolveExternalEntities:NO];

    // setup stack and observation array
    stack = [[NSMutableArray alloc] init];
    observations = [[NSMutableArray alloc] init];
    
	[plistParser parse];

    // release the stack
    [stack release];
    stack=nil;
    // autorelease the returning array
    NSMutableArray *returnedObservations = [observations autorelease];
    observations = nil;
    // release the xmlparser;
    [plistParser release];
    

    NSLog(@"Parsed (%3d) obs in %7.2fs",count,-[now timeIntervalSinceNow]);
    return returnedObservations;
}

#pragma mark NSXMLParser delegate

/*
- (void)parserDidStartDocument:(NSXMLParser *)parser {
	NSLog(@"Start Doc");
}
 */

- (void)parser:(NSXMLParser *)parser parseErrorOccurred:(NSError *)parseError {
    
	NSString * errorString = [NSString stringWithFormat:@"Unable to fetch feed (Error code %i )", [parseError code]];
	NSLog(@"error parsing XML: %@", errorString);

    // Set status instead...
    // TODO THIS is probably not the place for this....
	//UIAlertView * errorAlert = [[UIAlertView alloc] initWithTitle:@"Network Problem" message:errorString delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
	//[errorAlert show];
}

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName attributes:(NSDictionary *)attributeDict{
	//NSLog(@"found this element: %@", elementName);
    //NSMutableString *ms = [[NSMutableString alloc] init];
    NSMutableString *ms = [NSMutableString stringWithCapacity:20]; // to avoid releasing
    [stack addObject:ms];
}

- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName{
    
    //NSLog(@"ended element: %@", elementName);
    NSString *accumulatedString = [stack lastObject];
    [stack removeLastObject];
    //NSLog(@"   POP +++nms retainCount %d",[accumulatedString retainCount]);

    //[self printStack];
    //NSLog(@"poped: %@ inside %@", accumulatedString,elementName);
    
	if ([elementName isEqualToString:@"dict"]) {
        count++;
        //NSLog(@"dict: %@ -> %d",lastStamp,lastValue);
        Observation *observation = [[Observation alloc] init]; 
        observation.stamp = lastStamp;
        observation.value = lastValue;
        [lastStamp release];
        lastStamp=nil;
        
        [observations addObject:observation];
        [observation release];
        
    } else 	if ([elementName isEqualToString:@"integer"]) {
        NSScanner* scanner = [NSScanner scannerWithString:accumulatedString];
        NSInteger value;
        if([scanner scanInteger:&value] == YES) {
            lastValue = value;
        } else {
            lastValue = -1;
        }
    } else 	if ([elementName isEqualToString:@"date"]) {
        NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
        NSString *iMDateFormatPlist = @"yyyy-MM-dd'T'HH:mm:ss'Z'"; // with a Z
        [formatter setDateFormat:iMDateFormatPlist];
        NSDate *theDate = [formatter dateFromString:accumulatedString];
        [formatter release];
        lastStamp = [theDate retain];
    }
}

- (void)parser:(NSXMLParser *)parser foundCharacters:(NSString *)string{
	//NSLog(@"found characters: %@", string);
    NSMutableString *ms = (NSMutableString *)[stack lastObject];
    [ms appendString:string];
}

/*
- (void)parserDidEndDocument:(NSXMLParser *)parser {
}
*/

#pragma mark Constructor/Destructor
- (id) init {
    if ((self = [super init])) {
        count = 0;
    }
    return self;
}

- (void)dealloc {
    // these should all be clean (nil)
    //NSLog(@"ObservationFeed -dealloc");
    if (stack) NSLog(@"stack is not nil");
    if (observations) NSLog(@"observations is not nil");
    if (lastStamp) NSLog(@"lastStamp is not nil");
    [stack release];
    [observations release];
    [lastStamp release]; // should always be null though
    [super dealloc];
}

@end
