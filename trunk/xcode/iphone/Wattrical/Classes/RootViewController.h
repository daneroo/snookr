//
//  RootViewController.h
//  Wattrical
//
//  Created by Daniel Lauzon on 10/11/08.
//  Copyright __MyCompanyName__ 2008. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Observation.h"
#import "Feed.h"
#import "StatusSectionHeaderView.h"

@interface RootViewController : UITableViewController {
	NSArray *cellNameArray;
	NSDictionary *feedsByName;
    StatusSectionHeaderView *sectionHeaderView;
    NSInteger currentScope; // rename as index into cellnameArray..
	NSTimer *logoAnimTimer;
}
@property(nonatomic, retain) NSDictionary *feedsByName;

- (void)cycleScope;
- (void)setScope:(NSInteger) aScope;
- (void)launchFeedOperationIfRequired;

@end
