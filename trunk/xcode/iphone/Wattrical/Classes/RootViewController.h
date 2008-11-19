//
//  RootViewController.h
//  Wattrical
//
//  Created by Daniel Lauzon on 10/11/08.
//  Copyright __MyCompanyName__ 2008. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Observation.h"
#import "ObservationArray.h"
#import "StatusSectionHeaderView.h"

@interface RootViewController : UITableViewController {
	NSArray *cellNameArray;
    ObservationArray *obsarray;
    StatusSectionHeaderView *sectionHeaderView;
}

@end
