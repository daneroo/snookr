//
//  ObservationArray.h
//  TBVAdd
//
//  Created by Daniel Lauzon on 11/11/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Observation.h"


@interface ObservationArray : NSObject {
    NSMutableArray *observations; // array of Observation Objects
}
@property (nonatomic, retain) NSMutableArray *observations;

#pragma mark Observation Data Manip
- (void)addObservation:(NSInteger)value  withStamp:(NSDate *)aStamp;
- (void)addObservation:(Observation *)observation;
- (void)sort;

#pragma mark Observation Data IO
- (void) saveObservations;
- (void) clearObservations;
- (void) loadObservations;
- (void) appendObservations;
- (void) postObservations:(id)plist;
- (void) loadObservationsFromURL:(NSURL *)aURL;
- (void) appendObservationsFromURL:(NSURL *)aURL;

// temporay
- (void) test;
@end
