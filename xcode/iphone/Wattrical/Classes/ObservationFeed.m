//
//  ObservationFeed.m
//  Wattrical
//
//  Created by Daniel Lauzon on 12/11/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

#import "ObservationFeed.h"


@implementation ObservationFeed
- (void)parseXMLFileAtURL:(NSURL *)xmlURL {
    NSDate *now = [NSDate date];
	//stories = [[NSMutableArray alloc] init];
    
	// here, for some reason you have to use NSClassFromString when trying to alloc NSXMLParser, otherwise you will get an object not found error
	// this may be necessary only for the toolchain
	NSXMLParser *rssParser = [[NSXMLParser alloc] initWithContentsOfURL:xmlURL];
	NSLog(@"Parser didInit");
    
	// Set self as the delegate of the parser so that it will receive the parser delegate methods callbacks.
	[rssParser setDelegate:self];
    
	// Depending on the XML document you're parsing, you may want to enable these features of NSXMLParser.
	[rssParser setShouldProcessNamespaces:NO];
	[rssParser setShouldReportNamespacePrefixes:NO];
	[rssParser setShouldResolveExternalEntities:NO];
    
	NSLog(@"Parser willParse");
	[rssParser parse];
    
    NSLog(@"Parsed (%3d) obs in %7.2fs",count,-[now timeIntervalSinceNow]);

    [rssParser release];
}

- (void)parserDidStartDocument:(NSXMLParser *)parser {
	NSLog(@"Start Doc");
}

- (void)parser:(NSXMLParser *)parser parseErrorOccurred:(NSError *)parseError {
	NSString * errorString = [NSString stringWithFormat:@"Unable to fetch feed (Error code %i )", [parseError code]];
	NSLog(@"error parsing XML: %@", errorString);
    
	//UIAlertView * errorAlert = [[UIAlertView alloc] initWithTitle:@"Network Problem" message:errorString delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
	//[errorAlert show];
}

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName attributes:(NSDictionary *)attributeDict{
	//NSLog(@"found this element: %@", elementName);
	//currentElement = [elementName copy];
}

- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName{
    
    //NSLog(@"ended element: %@", elementName);
	if ([elementName isEqualToString:@"dict"]) {
        count++;
    }
}

- (void)parser:(NSXMLParser *)parser foundCharacters:(NSString *)string{
	//NSLog(@"found characters: %@", string);
	// save the characters for the current item...
    /*
	if ([currentElement isEqualToString:@"title"]) {
		[currentTitle appendString:string];
	} else if ([currentElement isEqualToString:@"link"]) {
		[currentLink appendString:string];
	} else if ([currentElement isEqualToString:@"description"]) {
		[currentSummary appendString:string];
	} else if ([currentElement isEqualToString:@"pubDate"]) {
		[currentDate appendString:string];
	}
     */
}

- (void)parserDidEndDocument:(NSXMLParser *)parser {
    
	//[activityIndicator stopAnimating];
	//[activityIndicator removeFromSuperview];
    
	//NSLog(@"All done!");
	//NSLog(@"stories array has %d items", [stories count]);
	//[newsTable reloadData];
}

#pragma mark Constructor/Destructor
- (id) init {
    if ((self = [super init])) {
        count = 0;
    }
    return self;
}

- (void)dealloc {
    [super dealloc];
}

@end
