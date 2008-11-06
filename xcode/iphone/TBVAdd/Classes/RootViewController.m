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
#import "ObservationCellView.h"
#import "DateUtil.h"

@implementation RootViewController


#pragma mark Observation Data Manip 
- (void)addObservation:(NSInteger)aValue  withStamp:(NSDate *)aStamp {
    Observation *observation = [[Observation alloc] init]; 
    observation.stamp = aStamp;
    observation.value = aValue;
    
    [self addObservation:observation];

    [observation release];
}

- (void)addObservation:(Observation *)observation {
    //NSLog(@"Base AddObservation %@ %d", observation.stamp, observation.value);
    [observations addObject:observation];
    
    NSSortDescriptor *sortDescriptor = [[NSSortDescriptor alloc] initWithKey:@"stamp" ascending:NO];
	NSArray *sortDescriptors = [[NSArray alloc] initWithObjects:&sortDescriptor count:1];
	[observations sortUsingDescriptors:sortDescriptors];
	[sortDescriptors release];
	[sortDescriptor release];

    [self saveObservations];
}

- (void) saveObservations {
    NSDate *startTime = [NSDate date];

    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
	NSString *documentsDirectory = [paths objectAtIndex: 0];
	NSString *dataFilePath = [documentsDirectory stringByAppendingPathComponent: @"observationdata.xml"];
	//NSLog (@"writing to file %@", dataFilePath);
    
    
    // make an array of dictionary
    NSMutableArray *nmsa = [[NSMutableArray alloc] init];
    
    NSEnumerator * enumerator = [observations objectEnumerator];
    Observation *observation;
    while(observation = (Observation *)[enumerator nextObject]) {
        //NSLog(@">>>> stamp: %@, value: %d", observation.stamp, observation.value);
        NSArray *keys = [NSArray arrayWithObjects:@"stamp", @"value", nil];
        NSArray *objects = [NSArray arrayWithObjects:observation.stamp, [NSNumber numberWithInteger:observation.value], nil];
        NSDictionary *dictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
        
        [nmsa addObject:dictionary];
    }
    
    [nmsa writeToFile: dataFilePath atomically: YES];
    [nmsa release];
    
	if ([[NSFileManager defaultManager] fileExistsAtPath: dataFilePath]) {
		//NSLog (@"file exists");
    } else {
		NSLog (@"file doesn't exist");
    }
	//NSLog (@"Wrote %d observations to file %@", [observations count],dataFilePath);
	NSLog (@"Wrote %d observations in %.3fs.", [observations count],-[startTime timeIntervalSinceNow]);
    //[self postObservations:nmsa];
}
- (void) loadObservations {
    NSDate *startTime = [NSDate date];
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
	NSString *documentsDirectory = [paths objectAtIndex: 0];
	NSString *dataFilePath = [documentsDirectory stringByAppendingPathComponent: @"observationdata.xml"];
    
	if ([[NSFileManager defaultManager] fileExistsAtPath: dataFilePath]) {
        //NSLog (@"file exists");
    } else     {
		NSLog (@"file doesn't exist");
        return;
    }
	//NSLog (@"reading from file %@", dataFilePath);
    
    // make an array of dictionary
    NSMutableArray *nmsa = [NSMutableArray arrayWithContentsOfFile:dataFilePath];
    
    NSEnumerator * enumerator = [nmsa objectEnumerator];
    NSDictionary *dictionary;
    while(dictionary = (NSDictionary *)[enumerator nextObject]) {
        NSDate *stamp = (NSDate *)[dictionary objectForKey:@"stamp"];
        NSNumber *value = (NSNumber *)[dictionary objectForKey:@"value"];
        //NSLog(@"<<<< stamp: %@, value: %@", stamp, value);
        
        Observation *observation = [[Observation alloc] init]; 
        observation.stamp = stamp;
        observation.value = [value integerValue];
        
        //NSLog(@"-loadObs retainCount: %d stamp: %d",[observation retainCount],[observation.stamp retainCount]);
        [observations addObject:observation];
        [observation release];
    }
    
	//NSLog (@"Read %d observations from file %@", [observations count],dataFilePath);
	NSLog (@"Read %d observations in %.3fs.", [observations count],-[startTime timeIntervalSinceNow]);
}

/* confirmed working
 */
- (void) postObservations:(id)plist {
    NSString *method = @"POST"; // or @"PUT"
    NSLog(@"attempting write to url");
    //BOOL success = [nmsa writeToURL:aURL atomically: YES];
    //NSLog(@"attempting write to url %@ : %d", aURL, success);

    NSString *baseURLString = @"http://192.168.5.2/iMetrical/";
    NSString *resource = @"save.php";
    NSString *urlString = [[NSString alloc] initWithFormat:@"%@%@", baseURLString, resource];
    NSURL *url = [[NSURL alloc] initWithString:urlString];
    NSLog(@"attempting write to url %@", url);

    NSMutableURLRequest *req = [[NSMutableURLRequest alloc] initWithURL:url];
    //[req setHTTPMethod:@"POST"];
    [req setHTTPMethod:method];
    
    NSString *errorStr = nil;
    NSData *paramData = [NSPropertyListSerialization dataFromPropertyList: plist
                                                                   format: NSPropertyListXMLFormat_v1_0
                                                         errorDescription: &errorStr];
    if (errorStr) {
        NSLog(@"Serialization error: %@",errorStr);
        [errorStr release];
    }
    
    [req setHTTPBody: paramData];	
    
    NSHTTPURLResponse* urlResponse = nil;  
    NSError* error = [[NSError alloc] init];  
    NSData *responseData = [NSURLConnection sendSynchronousRequest:req
                                                 returningResponse:&urlResponse   
                                                             error:&error];  
    NSString *result = [[NSString alloc] initWithData:responseData
                                             encoding:NSUTF8StringEncoding];
    NSLog(@"Response Code: %d", [urlResponse statusCode]);
    if ([urlResponse statusCode] >= 200 && [urlResponse statusCode] < 300)
        NSLog(@"Result: %@", result);
    
    [urlString release];
    [url release];
    [result release];
    [req release];
    
}

/*
 This is how I transformed the traineo data:
 cat ~/my_traineo.csv |awk -F','  \
 '{printf("<dict><key>stamp</key><date>%sT04:00:00Z</date><key>value</key><integer>%d</interger></dict>\n",$1,$3*1000)}'

 */
- (void) loadObservationsFromURL:(NSURL *)aURL {
	NSLog (@"reading from URL %@", aURL);
    // make an array of dictionary
    NSMutableArray *nmsa = [NSMutableArray arrayWithContentsOfURL:aURL];
    NSLog(@"Array of dics has %d dics",[nmsa count]);
    NSEnumerator *enumerator = [nmsa objectEnumerator];
    NSDictionary *dictionary;
    while(dictionary = (NSDictionary *)[enumerator nextObject]) {
        NSDate *stamp = (NSDate *)[dictionary objectForKey:@"stamp"];
        NSNumber *value = (NSNumber *)[dictionary objectForKey:@"value"];
        NSLog(@"<<www<< stamp: %@, value: %@", stamp, value);
        
		/*
        Observation *observation = [[Observation alloc] init]; 
        observation.stamp = stamp;
        observation.value = [value integerValue];
        
        [observations addObject:observation];
		*/
    }
        
	NSLog (@"Read from URL %@", aURL);
    
}

#pragma mark Local Controller Hooks 

-(void) reloadViews {
	[[self tableView] reloadData];
	// graphView
	[self.tableView.tableHeaderView setNeedsDisplay];
}

// Event handler for modal add Observation
-(void) addCallback:(id)sender {
    //NSLog(@"Hello from addCallback Modal");
	AddObservationViewController *addController = [[AddObservationViewController alloc] initWithNibName:@"AddObservationView" bundle:nil];
    
    UINavigationController *navigationController = [[UINavigationController alloc] initWithRootViewController:addController];
	addController.delegate = self;
    navigationController.navigationBar.barStyle = UIBarStyleBlackOpaque; 
	[self presentModalViewController:navigationController animated:YES];
    if (observations && [observations count]>0) {
        Observation *obs = (Observation *)[observations objectAtIndex:0];
        [addController setInitialWeight:obs.value];
    } else {
        [addController setInitialWeight:100000];
    }
    
    [addController release];
    
}

#pragma mark UITableViewDataSource Protocol 
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

#pragma mark UITableViewDelegate Protocol 

#define STAMP_TAG 42
#define OBSERV_TAG 43

- (UITableViewCell *)OLDXIBtableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *CellIdentifier = @"FromXIB"; // This is also set in XIB.
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        //NSLog(@"Allocating From XIB");
        UIViewController *vc=[[UIViewController alloc] initWithNibName:@"ObsTBVCell" bundle:nil];
        cell=(UITableViewCell *)vc.view;
    }
    
    // Set up the cell
    Observation *observation = [observations objectAtIndex:indexPath.row];
    UILabel *label;
    
    label = (UILabel *)[cell viewWithTag:STAMP_TAG];
    label.text = [DateUtil formatDate:observation.stamp withFormat:iMDateFormatFullISO];
    
    double d = observation.value / 1000.0;
    label = (UILabel *)[cell viewWithTag:OBSERV_TAG];
    label.text = [[NSString alloc] initWithFormat:@"%.1f", d];
    
    return cell;
}

#define ROW_HEIGHT 40
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
	/*
    if (indexPath.row%2 == 0 ) {
        return [self OLDXIBtableView:tableView cellForRowAtIndexPath:indexPath];
    }
     */	    
    
    static NSString *cellReuseIdentifier = @"ObsCellId";
    ObservationCellView *cell = (ObservationCellView *)[tableView dequeueReusableCellWithIdentifier:cellReuseIdentifier];
    if (cell == nil) {
		CGRect startingRect = CGRectMake(0.0, 0.0, 320.0, ROW_HEIGHT);
        cell = [[[ObservationCellView alloc] initWithFrame:startingRect reuseIdentifier:cellReuseIdentifier] autorelease];
    }
    //NSLog(@"cellForRow retainCount: %d",[cell retainCount]); // OK retain==1
    
    // Set up the cell
    Observation *observation = [observations objectAtIndex:indexPath.row];
    [cell setObservation:observation];
    
	/*
    if ((indexPath.row%4)==300) {
        double d = observation.value / 1000.0;
        cell.text = [[NSString alloc] initWithFormat:@"%.1f", d];
    }
	 */
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


// Override to support editing the list
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        // Delete the row from the data source
        [observations removeObjectAtIndex:indexPath.row];
        [self saveObservations];
		[self reloadViews];
        
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

#pragma mark UIViewController Override 

- (void)viewDidLoad {
    [super viewDidLoad];
    
    observations = [[NSMutableArray alloc] init];
    [self loadObservations];
	//NSURL *aURL = [NSURL URLWithString:@"http://192.168.5.2/iMetrical/traineodata.xml"];
	//[self loadObservationsFromURL:aURL];	
	
	//Set the title of the Main View here.
	self.title = @"Weightrical D";
    self.tableView.rowHeight = ROW_HEIGHT;
    
    
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
    
    graphView.observations = observations;
}

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

/*
- (void)willRotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration {
    CGSize gfs = self.tableView.tableHeaderView.frame.size; 
    CGSize gbs = self.tableView.tableHeaderView.bounds.size; 
    NSLog(@" willRotate: f:%.0fx%.0f b:%.0f,%.0f", gfs.width,gfs.height, gbs.width,gbs.height);

    / *
    if (toInterfaceOrientation == UIInterfaceOrientationLandscapeRight ||
        toInterfaceOrientation == UIInterfaceOrientationLandscapeLeft) {
        NSLog(@" Setting up landscape");
    }
    if (toInterfaceOrientation == UIInterfaceOrientationPortrait ||
        toInterfaceOrientation == UIInterfaceOrientationPortraitUpsideDown) {
        NSLog(@" Setting up portrait");
    }     
    * /
}
*/

/*
- (void)didRotateFromInterfaceOrientation:(UIInterfaceOrientation)fromInterfaceOrientation {
    CGSize gfs = self.tableView.tableHeaderView.frame.size; 
    CGSize gbs = self.tableView.tableHeaderView.bounds.size; 
    NSLog(@" didRotate: f:%.0fx%.0f b:%.0f,%.0f", gfs.width,gfs.height, gbs.width,gbs.height);
}
*/


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning]; // Releases the view if it doesn't have a superview
    // Release anything that's not essential, such as cached data
}


- (void)dealloc {
    [observations release];
    [super dealloc];
}


@end

