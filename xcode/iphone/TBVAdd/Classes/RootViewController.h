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
    NSMutableArray *observations; // array of Observation Objects
}
//@property (nonatomic, retain) NSMutableArray *observations;

- (void)addObservation:(NSInteger)value  withStamp:(NSDate *)aStamp;
- (void)addObservation:(Observation *)observation;
- (void) saveObservations;
- (void) loadObservations;
- (void) postObservations:(id)plist;
- (void) loadObservationsFromURL:(NSURL *)aURL;

- (void) reloadViews;
- (void) addCallback:(id)sender;

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
