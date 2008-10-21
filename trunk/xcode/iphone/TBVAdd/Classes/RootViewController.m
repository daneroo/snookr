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

@implementation RootViewController

- (void)addRandObservation {
    // from  (110.0 to 200.0 )*1000;
    //  == (1100 + 900*rnd01)*100;
    NSInteger rnd = (random()%900 + 1100)*100;
    NSLog(@" rand %d", rnd);
    
    [self addStampedObservation:rnd];
}

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
        
        /*for (id key in dictionary) {
         NSLog(@"key: %@, value: %@", key, [dictionary objectForKey:key]);
         }*/
        
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
	return @"  Date-Time                         Weight";
}

#define STAMP_TAG 42
#define OBSERV_TAG 43

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *CellIdentifier = @"FromXIB"; // This is also set in XIB.
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        NSLog(@"Allocating From XIB");
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
    if ([observations count]==0) {
        [self addRandObservation];
    }
    
	//Set the title of the Main View here.
	self.title = @"TBV Add";

    // Uncomment the following line to add the Edit button to the navigation bar.
    self.navigationItem.leftBarButtonItem = self.editButtonItem;

	UIBarButtonItem *addButton = [[[UIBarButtonItem alloc]
                                   initWithBarButtonSystemItem: UIBarButtonSystemItemAdd
                                   target:self action:@selector(addCallback:)] autorelease];
/*	UIBarButtonItem *addButton = [[[UIBarButtonItem alloc]
                                   initWithTitle:@"Add" 
                                   style:UIBarButtonItemStyleBordered 
                                   target:self action:@selector(addCallback:)] autorelease];
*/	
	self.navigationItem.rightBarButtonItem = addButton;
    
}

//Event handler when add is clicked.
-(void) addCallbackRand:(id)sender {
    NSLog(@"Hello from rand addCallback");
    [self addRandObservation];

    // Limited updates with animation...
/*
 //NSIndexPath *indexPath = [NSIndexPath indexPathForRow:([observations count]-1) inSection:0];
	NSIndexPath *indexPath = [NSIndexPath indexPathForRow:(0) inSection:0];
	NSArray *paths = [NSArray arrayWithObjects:indexPath];
	[[self tableView] insertRowsAtIndexPaths:paths withRowAnimation:YES];
*/
    
    [[self tableView] reloadData];
    
}

// Event handler for modal add Observation
-(void) addCallback:(id)sender {
    NSLog(@"Hello from addCallback Modal");
	AddObservationViewController *addController = [[AddObservationViewController alloc] initWithNibName:@"AddObservationView" bundle:nil];
    NSLog(@"Hello from addCallback init done");
    UINavigationController *navigationController = [[UINavigationController alloc] initWithRootViewController:addController];
	addController.delegate = self;
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
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning]; // Releases the view if it doesn't have a superview
    // Release anything that's not essential, such as cached data
}


- (void)dealloc {
    [super dealloc];
}


@end

