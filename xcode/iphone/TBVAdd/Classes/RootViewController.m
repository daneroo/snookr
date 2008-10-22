//
//  RootViewController.m
//  TBVAdd
//
//  Created by Daniel Lauzon on 15/10/08.
//  Copyright __MyCompanyName__ 2008. All rights reserved.
//

#import "RootViewController.h"
#import "AddObservationViewController.h"
#import "TBVAddAppDelegate.h"
#import "UIKit/UIBarButtonItem.h"
#import "GraphView.h"

@implementation RootViewController


- (void)addStampedObservation:(NSInteger)value {
    [self addObservation:value withStamp:[NSDate date]];
}

- (void)addObservation:(NSInteger)aValue  withStamp:(NSDate *)aStamp {
    Observation *observation = [[Observation alloc] init]; 
    observation.stamp = aStamp;
    observation.value = aValue;
    
    [self addObservation:observation];

    [observation release];
}

- (void)addObservation:(Observation *)observation {
    NSLog(@"Base AddObservation %@ %d", observation.stamp, observation.value);

    [observations addObject:observation];
    
    NSSortDescriptor *sortDescriptor = [[NSSortDescriptor alloc] initWithKey:@"stamp" ascending:NO];
	NSArray *sortDescriptors = [[NSArray alloc] initWithObjects:&sortDescriptor count:1];
	[observations sortUsingDescriptors:sortDescriptors];
	[sortDescriptors release];
	[sortDescriptor release];

    [self saveObservations];
}

- (void) saveObservations {
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
	NSString *documentsDirectory = [paths objectAtIndex: 0];
	NSString *dataFilePath = [documentsDirectory stringByAppendingPathComponent: @"observationdata.xml"];
    
	NSLog (@"writing to file %@", dataFilePath);
    
    // codeblock approach
    // [myArray do:ocblock(:each | [self doMyThingWith:each];)
    
    // make an array of dictionary
    NSMutableArray *nmsa = [[NSMutableArray alloc] init];
    
    NSEnumerator * enumerator = [observations objectEnumerator];
    Observation *observation;
    while(observation = (Observation *)[enumerator nextObject]) {
        NSLog(@">>>> stamp: %@, value: %d", observation.stamp, observation.value);
        
        NSArray *keys = [NSArray arrayWithObjects:@"stamp", @"value", nil];
        NSArray *objects = [NSArray arrayWithObjects:observation.stamp, [NSNumber numberWithInteger:observation.value], nil];
        NSDictionary *dictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
        
        [nmsa addObject:dictionary];
        
    }
    
    
    [nmsa writeToFile: dataFilePath atomically: YES];
    
	NSLog (@"wrote to file %@", dataFilePath);
	if ([[NSFileManager defaultManager] fileExistsAtPath: dataFilePath])
		NSLog (@"file exists");
	else
		NSLog (@"file doesn't exist");
   
}

- (void) loadObservations {
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
	NSString *documentsDirectory = [paths objectAtIndex: 0];
	NSString *dataFilePath = [documentsDirectory stringByAppendingPathComponent: @"observationdata.xml"];
    
	if ([[NSFileManager defaultManager] fileExistsAtPath: dataFilePath]) {
        NSLog (@"file exists");
    } else     {
		NSLog (@"file doesn't exist");
        return;
    }
    
	NSLog (@"reading from file %@", dataFilePath);
    
    
    // make an array of dictionary
    NSMutableArray *nmsa = [NSMutableArray arrayWithContentsOfFile:dataFilePath];
    
    NSEnumerator * enumerator = [nmsa objectEnumerator];
    NSDictionary *dictionary;
    while(dictionary = (NSDictionary *)[enumerator nextObject]) {
        NSDate *stamp = (NSDate *)[dictionary objectForKey:@"stamp"];
        NSNumber *value = (NSNumber *)[dictionary objectForKey:@"value"];
        NSLog(@"<<<< stamp: %@, value: %@", stamp, value);
        
        //[observations addObject:dictionary];
        Observation *observation = [[Observation alloc] init]; 
        observation.stamp = stamp;
        observation.value = [value integerValue];
        
        [observations addObject:observation];

        
    }
    
    
    //[nmsa writeToFile: dataFilePath atomically: YES];
    
	NSLog (@"Read from file %@", dataFilePath);
    
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return [observations count];
}

- (NSString *)tableView:(UITableView *)aTableView titleForHeaderInSection:(NSInteger)section {
	//return @"  Date-Time                         Weight";
    return @"";
}

#define STAMP_TAG 42
#define OBSERV_TAG 43

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *CellIdentifier = @"FromXIB"; // This is also set in XIB.
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        //NSLog(@"Allocating From XIB");
        UIViewController *vc=[[UIViewController alloc] initWithNibName:@"ObsTBVCell" bundle:nil];
        cell=(UITableViewCell *)vc.view;
    }

    // Set up the cell
    Observation *observation = [observations objectAtIndex:indexPath.row];
    
    /*
	 Cache the formatter. Normally you would use one of the date formatter styles (such as NSDateFormatterShortStyle), but here we want a specific format that excludes seconds.
	 */
    UILabel *label;

	static NSDateFormatter *dateFormatter = nil;
	if (dateFormatter == nil) {
		dateFormatter = [[NSDateFormatter alloc] init];
		[dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
	}
    label = (UILabel *)[cell viewWithTag:STAMP_TAG];
    label.text = [dateFormatter stringFromDate: observation.stamp];


    double d = observation.value / 1000.0;
    label = (UILabel *)[cell viewWithTag:OBSERV_TAG];
    label.text = [[NSString alloc] initWithFormat:@"%.1f", d];
    
    return cell;
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    // Navigation logic -- create and push a new view controller
    /*
	 To conform to the Human Interface Guidelines, selections should not be persistent --
	 deselect the row after it has been selected.
	 */
	[tableView deselectRowAtIndexPath:indexPath animated:YES];
    
}



- (void)viewDidLoad {
    [super viewDidLoad];
    
    // should this be retained ??
    observations = [[NSMutableArray alloc] init];
    [self loadObservations];
    
	//Set the title of the Main View here.
	self.title = @"Weightrical D";

    // Uncomment the following line to add the Edit button to the navigation bar.
    self.navigationItem.leftBarButtonItem = self.editButtonItem;

	UIBarButtonItem *addButton = [[[UIBarButtonItem alloc]
                                   initWithBarButtonSystemItem: UIBarButtonSystemItemAdd
                                   target:self action:@selector(addCallback:)] autorelease];
	self.navigationItem.rightBarButtonItem = addButton;

    //  GraphView as tableHeaderView Height 
#define HEADERVIEW_HEIGHT 160.0
	CGRect newFrame = CGRectMake(0.0, 0.0, self.tableView.bounds.size.width, HEADERVIEW_HEIGHT);
    GraphView *graphView = [[[GraphView alloc] initWithFrame:newFrame] autorelease];
	self.tableView.tableHeaderView = graphView;	// note this will override UITableView's 'sectionHeaderHeight' property
}

// Event handler for modal add Observation
-(void) addCallback:(id)sender {
    NSLog(@"Hello from addCallback Modal");
	AddObservationViewController *addController = [[AddObservationViewController alloc] initWithNibName:@"AddObservationView" bundle:nil];
    NSLog(@"Hello from addCallback init done");
    UINavigationController *navigationController = [[UINavigationController alloc] initWithRootViewController:addController];
	addController.delegate = self;
    navigationController.navigationBar.barStyle = UIBarStyleBlackOpaque; 
	[self presentModalViewController:navigationController animated:YES];
    [addController release];
    
}

// Override to support editing the list
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        // Delete the row from the data source
        [observations removeObjectAtIndex:indexPath.row];
        [self saveObservations];
        [[self tableView] reloadData];
        
        //[tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:YES];
    }   
    /*
    if (editingStyle == UITableViewCellEditingStyleInsert) {
        // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
    } 
    */
}



/*
// Override to support conditional editing of the list
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    // Return NO if you do not want the specified item to be editable.
    return YES;
}
*/


/*
// Override to support rearranging the list
- (void)tableView:(UITableView *)tableView moveRowAtIndexPath:(NSIndexPath *)fromIndexPath toIndexPath:(NSIndexPath *)toIndexPath {
}
*/


/*
// Override to support conditional rearranging of the list
- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath {
    // Return NO if you do not want the item to be re-orderable.
    return YES;
}
*/

/*
- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
}
*/
/*
- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
}
*/
/*
- (void)viewWillDisappear:(BOOL)animated {
}
*/
/*
- (void)viewDidDisappear:(BOOL)animated {
}
*/

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    // Return YES for supported orientations
    //return (interfaceOrientation == UIInterfaceOrientationPortrait);
    return YES;
}

- (void)willRotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration {
    CGSize gfs = self.tableView.tableHeaderView.frame.size; 
    CGSize gbs = self.tableView.tableHeaderView.bounds.size; 
    NSLog(@" willRotate: f:%.0fx%.0f b:%.0f,%.0f", gfs.width,gfs.height, gbs.width,gbs.height);

    /*
    if (toInterfaceOrientation == UIInterfaceOrientationLandscapeRight ||
        toInterfaceOrientation == UIInterfaceOrientationLandscapeLeft) {
        NSLog(@" Setting up landscape");
    }
    if (toInterfaceOrientation == UIInterfaceOrientationPortrait ||
        toInterfaceOrientation == UIInterfaceOrientationPortraitUpsideDown) {
        NSLog(@" Setting up portrait");
    }     
    */
}

- (void)didRotateFromInterfaceOrientation:(UIInterfaceOrientation)fromInterfaceOrientation {
    CGSize gfs = self.tableView.tableHeaderView.frame.size; 
    CGSize gbs = self.tableView.tableHeaderView.bounds.size; 
    NSLog(@" didRotate: f:%.0fx%.0f b:%.0f,%.0f", gfs.width,gfs.height, gbs.width,gbs.height);
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning]; // Releases the view if it doesn't have a superview
    // Release anything that's not essential, such as cached data
}


- (void)dealloc {
    [super dealloc];
}


@end

