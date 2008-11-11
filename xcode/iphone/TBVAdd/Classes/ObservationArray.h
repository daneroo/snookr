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

- (void)addObservation:(NSInteger)value  withStamp:(NSDate *)aStamp;
- (void)addObservation:(Observation *)observation;

- (void) saveObservations;
- (void) loadObservations;
- (void) postObservations:(id)plist;
- (void) loadObservationsFromURL:(NSURL *)aURL;

@end
