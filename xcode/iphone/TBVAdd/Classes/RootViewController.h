//
//  RootViewController.h
//  TBVAdd
//
//  Created by Daniel Lauzon on 15/10/08.
//  Copyright __MyCompanyName__ 2008. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Observation.h"
#import "ObservationArray.h"

@interface RootViewController : UITableViewController {
    ObservationArray *obsarray;
}
//@property (nonatomic, retain) NSMutableArray *observations;

- (void)addAndSaveObservation:(NSInteger)value  withStamp:(NSDate *)aStamp;
- (void)removeAndSaveObservationAtIndex:(NSUInteger)index;
- (Observation *)getLatestObservation;
- (void) reloadViews;
- (void) popupAddObservationModal:(id)sender;


- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView;
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section;
- (NSString *)tableView:(UITableView *)aTableView titleForHeaderInSection:(NSInteger)section;

- (UITableViewCell *)OLDXIBtableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath;
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath;
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath;


- (void)viewDidLoad;
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath;
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation;

- (void)didReceiveMemoryWarning;
- (void)dealloc;

@end
