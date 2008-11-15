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

#define TIMER_INTERVAL 2.0
@implementation RootViewController

#pragma mark Local Controller Hooks 
-(void) popupSettingsModal:(id)sender {
    NSLog(@"Hello from popupSettingsModal");
}

-(void) loadFromLiveFeed {
    
    NSDate *now = [NSDate date];

    //[obsarray addObservation: 99000 withStamp:[NSDate dateWithTimeIntervalSinceNow:-3600*24*1]];
	//NSURL *aURL = [NSURL URLWithString:@"http://192.168.5.2/iMetrical/getTED.php"];
	NSURL *aURL = [NSURL URLWithString:@"http://192.168.5.2/iMetrical/iPhoneTest.php"];

	//[obsarray appendObservationsFromURL:aURL];	  
	//[obsarray loadObservationsFromURL:aURL];	  
    [obsarray test];
    NSLog(@"time to load (%3d) obs : %7.2f",[obsarray.observations count],-[now timeIntervalSinceNow]);

    //[self.view setNeedsDisplay];
   	[self.tableView.tableHeaderView setNeedsDisplay];
}

#pragma mark UITableViewDataSource Protocol 

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

/*
- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
	//return @"Wattrical Feeds:";
	return @"Status:";
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
        //cell.textColor = [UIColor darkGrayColor];
        //cell.selectedTextColor = [UIColor colorWithRed:0.0 green:1.0/3.0 blue:0.0 alpha:1.0];
        cell.selectionStyle = UITableViewCellSelectionStyleGray; //UITableViewCellSelectionStyleBlue  UITableViewCellSelectionStyleNone
    }
    
    // Set up the cell
    cell.accessoryType = UITableViewCellAccessoryNone;
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


// Override to support editing the list
/*
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
	[NSTimer scheduledTimerWithTimeInterval:TIMER_INTERVAL target:self selector:@selector(loadFromLiveFeed) userInfo:nil repeats:YES];

    
    //Set the title of the Main View here.
    self.title = @"Wattrical";

    self.tableView.rowHeight = 40;
    self.tableView.sectionHeaderHeight=5;
    self.tableView.backgroundColor = [UIColor colorWithRed:0.0 green:1.0/3.0 blue:0.0 alpha:1.0];


    UIBarButtonItem *addButton = [[[UIBarButtonItem alloc]
                                   initWithBarButtonSystemItem: UIBarButtonSystemItemCompose
                                   target:self action:@selector(popupSettingsModal:)] autorelease];
	self.navigationItem.rightBarButtonItem = addButton;
    
    
    // setup our table data 
	cellNameArray = [[NSArray arrayWithObjects:@"Live", @"Hour", @"Day", @"Week", @"Month", nil] retain];
    
    //  GraphView as tableHeaderView Height 
#define HEADERVIEW_HEIGHT 200.0
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

