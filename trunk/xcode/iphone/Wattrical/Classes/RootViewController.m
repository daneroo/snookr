//
//  RootViewController.m
//  Wattrical
//
//  Created by Daniel Lauzon on 10/11/08.
//  Copyright __MyCompanyName__ 2008. All rights reserved.
//

#import "RootViewController.h"
#import "WattricalAppDelegate.h"
#import "GraphView.h"
#import "ObservationCellView.h"
#import "DateUtil.h"


@implementation RootViewController

#pragma mark Local Controller Hooks 
-(void) popupSettingsModal:(id)sender {
    NSLog(@"Hello from popupSettingsModal");
}

-(void) loadFromLiveFeed {
    //[obsarray addObservation: 99000 withStamp:[NSDate dateWithTimeIntervalSinceNow:-3600*24*5]];
    //[obsarray addObservation:100000 withStamp:[NSDate date]];
    //[obsarray loadObservations];
	NSURL *aURL = [NSURL URLWithString:@"http://192.168.5.2/iMetrical/getTED.php"];
	[obsarray loadObservationsFromURL:aURL];	

    //[self.view setNeedsDisplay];
   	[self.tableView.tableHeaderView setNeedsDisplay];
}

#pragma mark UITableViewDataSource Protocol 

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

/*
- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
	return @"Wattrical Feeds:";
}
*/

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
	return [cellNameArray count];
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *CellIdentifier = @"Cell";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [[[UITableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:CellIdentifier] autorelease];
    }
    
    // Set up the cell
    cell.accessoryType = UITableViewCellAccessoryNone;
	cell.selectionStyle = UITableViewCellSelectionStyleNone;
	cell.text = [cellNameArray objectAtIndex:[indexPath row]];
    
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


/*
// Override to support editing the list
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        // Delete the row from the data source
        [tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:YES];
    }   
    if (editingStyle == UITableViewCellEditingStyleInsert) {
        // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
    }   
}
*/


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

   // Manage the status bar:
    UIApplication *app = [UIApplication sharedApplication];
    app.statusBarStyle = UIStatusBarStyleBlackOpaque;

    // or how about a +readFromFile +retain ?
    obsarray = [[ObservationArray alloc] init];
    [self loadFromLiveFeed];

    // How should I implement : "As fast as possible" ?
	[NSTimer scheduledTimerWithTimeInterval:3.0 target:self selector:@selector(loadFromLiveFeed) userInfo:nil repeats:YES];

    
    //Set the title of the Main View here.
    self.title = @"Wattrical";

    //self.tableView.rowHeight = ROW_HEIGHT;

    UIBarButtonItem *addButton = [[[UIBarButtonItem alloc]
                                   initWithBarButtonSystemItem: UIBarButtonSystemItemCompose
                                   target:self action:@selector(popupSettingsModal:)] autorelease];
	self.navigationItem.rightBarButtonItem = addButton;
    
    
    // setup our table data 
	cellNameArray = [[NSArray arrayWithObjects:@"Live", @"Hour", @"Day", @"Week", @"Month", nil] retain];
    
    //  GraphView as tableHeaderView Height 
#define HEADERVIEW_HEIGHT 160.0
	CGRect newFrame = CGRectMake(0.0, 0.0, self.tableView.bounds.size.width, HEADERVIEW_HEIGHT);
    GraphView *graphView = [[GraphView alloc] initWithFrame:newFrame];
	self.tableView.tableHeaderView = graphView;	// note this will override UITableView's 'sectionHeaderHeight' property
    [graphView release]; // now that it has been retained.
    graphView.observations = obsarray.observations;
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


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning]; // Releases the view if it doesn't have a superview
    // Release anything that's not essential, such as cached data
}


- (void)dealloc {
    [super dealloc];
    [cellNameArray release];
    [obsarray release];
}


@end

