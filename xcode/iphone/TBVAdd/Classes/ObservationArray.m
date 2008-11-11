//
//  ObservationArray.m
//  TBVAdd
//
//  Created by Daniel Lauzon on 11/11/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

#import "ObservationArray.h"


@implementation ObservationArray

@synthesize observations;

#pragma mark Observation Data Manip
- (void)addObservation:(NSInteger)aValue  withStamp:(NSDate *)aStamp {
    Observation *observation = [[Observation alloc] init]; 
    observation.stamp = aStamp;
    observation.value = aValue;
    [self addObservation:observation];
    
    [observation release];
}

// implies sort
- (void)addObservation:(Observation *)observation {
    //NSLog(@"Base AddObservation %@ %d", observation.stamp, observation.value);
    [observations addObject:observation];
    [self sort];
}

- (void)sort {
    NSSortDescriptor *sortDescriptor = [[NSSortDescriptor alloc] initWithKey:@"stamp" ascending:NO];
	NSArray *sortDescriptors = [[NSArray alloc] initWithObjects:&sortDescriptor count:1];
	[observations sortUsingDescriptors:sortDescriptors];
	[sortDescriptors release];
	[sortDescriptor release];
}

#pragma mark Observation Data IO
- (void) saveObservations {
    NSDate *startTime = [NSDate date];
    
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
	NSString *documentsDirectory = [paths objectAtIndex: 0];
	NSString *dataFilePath = [documentsDirectory stringByAppendingPathComponent: @"observationdata.xml"];
	//NSLog (@"writing to file %@", dataFilePath);
    
    
    // make an array of dictionary
    NSMutableArray *nmsa = [[NSMutableArray alloc] init];
    
    NSEnumerator * enumerator = [observations objectEnumerator];
    Observation *observation;
    while(observation = (Observation *)[enumerator nextObject]) {
        //NSLog(@">>>> stamp: %@, value: %d", observation.stamp, observation.value);
        NSArray *keys = [NSArray arrayWithObjects:@"stamp", @"value", nil];
        NSArray *objects = [NSArray arrayWithObjects:observation.stamp, [NSNumber numberWithInteger:observation.value], nil];
        NSDictionary *dictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
        
        [nmsa addObject:dictionary];
    }
    
    [nmsa writeToFile: dataFilePath atomically: YES];
    [nmsa release];
    
	if ([[NSFileManager defaultManager] fileExistsAtPath: dataFilePath]) {
		//NSLog (@"file exists");
    } else {
		NSLog (@"file doesn't exist");
    }
	//NSLog (@"Wrote %d observations to file %@", [observations count],dataFilePath);
	NSLog (@"Wrote %d observations in %.3fs.", [observations count],-[startTime timeIntervalSinceNow]);
    //[self postObservations:nmsa];
}

- (void) loadObservations {
    NSDate *startTime = [NSDate date];
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
	NSString *documentsDirectory = [paths objectAtIndex: 0];
	NSString *dataFilePath = [documentsDirectory stringByAppendingPathComponent: @"observationdata.xml"];
    
	if ([[NSFileManager defaultManager] fileExistsAtPath: dataFilePath]) {
        //NSLog (@"file exists");
    } else     {
		NSLog (@"file doesn't exist");
        return;
    }
	//NSLog (@"reading from file %@", dataFilePath);
    
    // make an array of dictionary
    NSMutableArray *nmsa = [NSMutableArray arrayWithContentsOfFile:dataFilePath];
    
    NSEnumerator * enumerator = [nmsa objectEnumerator];
    NSDictionary *dictionary;
    while(dictionary = (NSDictionary *)[enumerator nextObject]) {
        NSDate *stamp = (NSDate *)[dictionary objectForKey:@"stamp"];
        NSNumber *value = (NSNumber *)[dictionary objectForKey:@"value"];
        //NSLog(@"<<<< stamp: %@, value: %@", stamp, value);
        
        Observation *observation = [[Observation alloc] init]; 
        observation.stamp = stamp;
        observation.value = [value integerValue];
        
        //NSLog(@"-loadObs retainCount: %d stamp: %d",[observation retainCount],[observation.stamp retainCount]);
        [observations addObject:observation];
        [observation release];
    }
    [self sort];
	//NSLog (@"Read %d observations from file %@", [observations count],dataFilePath);
	NSLog (@"Read %d observations in %.3fs.", [observations count],-[startTime timeIntervalSinceNow]);
}

/* confirmed working
 */
- (void) postObservations:(id)plist {
    NSString *method = @"POST"; // or @"PUT"
    NSLog(@"attempting write to url");
    //BOOL success = [nmsa writeToURL:aURL atomically: YES];
    //NSLog(@"attempting write to url %@ : %d", aURL, success);
    
    NSString *baseURLString = @"http://192.168.5.2/iMetrical/";
    NSString *resource = @"save.php";
    NSString *urlString = [[NSString alloc] initWithFormat:@"%@%@", baseURLString, resource];
    NSURL *url = [[NSURL alloc] initWithString:urlString];
    NSLog(@"attempting write to url %@", url);
    
    NSMutableURLRequest *req = [[NSMutableURLRequest alloc] initWithURL:url];
    //[req setHTTPMethod:@"POST"];
    [req setHTTPMethod:method];
    
    NSString *errorStr = nil;
    NSData *paramData = [NSPropertyListSerialization dataFromPropertyList: plist
                                                                   format: NSPropertyListXMLFormat_v1_0
                                                         errorDescription: &errorStr];
    if (errorStr) {
        NSLog(@"Serialization error: %@",errorStr);
        [errorStr release];
    }
    
    [req setHTTPBody: paramData];	
    
    NSHTTPURLResponse* urlResponse = nil;  
    NSError* error = [[NSError alloc] init];  
    NSData *responseData = [NSURLConnection sendSynchronousRequest:req
                                                 returningResponse:&urlResponse   
                                                             error:&error];  
    NSString *result = [[NSString alloc] initWithData:responseData
                                             encoding:NSUTF8StringEncoding];
    NSLog(@"Response Code: %d", [urlResponse statusCode]);
    if ([urlResponse statusCode] >= 200 && [urlResponse statusCode] < 300)
        NSLog(@"Result: %@", result);
    
    [urlString release];
    [url release];
    [result release];
    [req release];
    
}

/*
 This is how I transformed the traineo data:
 cat ~/my_traineo.csv |awk -F','  \
 '{printf("<dict><key>stamp</key><date>%sT04:00:00Z</date><key>value</key><integer>%d</interger></dict>\n",$1,$3*1000)}'
 
 */
- (void) loadObservationsFromURL:(NSURL *)aURL {
	NSLog (@"reading from URL %@", aURL);
    // make an array of dictionary
    NSMutableArray *nmsa = [NSMutableArray arrayWithContentsOfURL:aURL];
    NSLog(@"Array of dics has %d dics",[nmsa count]);
    NSEnumerator *enumerator = [nmsa objectEnumerator];
    NSDictionary *dictionary;
    while(dictionary = (NSDictionary *)[enumerator nextObject]) {
        NSDate *stamp = (NSDate *)[dictionary objectForKey:@"stamp"];
        NSNumber *value = (NSNumber *)[dictionary objectForKey:@"value"];
        NSLog(@"<<www<< stamp: %@, value: %@", stamp, value);
        
		/*
         Observation *observation = [[Observation alloc] init]; 
         observation.stamp = stamp;
         observation.value = [value integerValue];
         
         [observations addObject:observation];
         */
    }
    
	NSLog (@"Read from URL %@", aURL);
    
}

#pragma mark Constructor/Destructor
- (id) init {
    if ((self = [super init])) {
        observations = [[NSMutableArray alloc] init];
    }
    return self;
}

- (void)dealloc {
    [observations release];
    [super dealloc];
}

@end
