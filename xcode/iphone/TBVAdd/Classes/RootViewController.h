//
//  RootViewController.h
//  TBVAdd
//
//  Created by Daniel Lauzon on 15/10/08.
//  Copyright __MyCompanyName__ 2008. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Observation.h"

@interface RootViewController : UITableViewController {
    NSMutableArray *observations; // array of NSStrings for now
}
//@property (nonatomic, retain) NSMutableArray *observations;

- (void)addStampedObservation:(NSInteger)value;
- (void)addObservation:(NSInteger)value  withStamp:(NSDate *)aStamp;
- (void)addObservation:(Observation *)observation;

- (void) saveObservations;
    
@end
