//
//  RootViewController.h
//  TBVAdd
//
//  Created by Daniel Lauzon on 15/10/08.
//  Copyright __MyCompanyName__ 2008. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface RootViewController : UITableViewController {
    NSMutableArray *observations; // array of NSStrings for now
}
//@property (nonatomic, retain) NSMutableArray *observations;

- (void)addRandObservation;
- (void)addStampedObservation:(NSString *)observation;
- (void)addObservation:(NSString *)observation;

    
@end
