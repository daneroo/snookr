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
#import "RandUtil.h"
// for Reachability
#import <SystemConfiguration/SystemConfiguration.h>

#define TIMER_INTERVAL 3.0
@implementation RootViewController

#pragma mark Local Controller Hooks 

-(void)cycleScope {
    [self setScope:currentScope+1];
}

-(void)setScope:(NSInteger) aScope {
    NSInteger maxScope = 5;
    currentScope = aScope % (maxScope+1);
    NSLog(@"currentScope has been set to: %d",currentScope);
    [self launchFeedOperationIfRequired];
    
    //NSIndexPath *currentIndexPath = [self.tableView indexPathForSelectedRow];
    //NSLog(@"Current selected row: %@",currentIndexPath.row);
    //NSIndexPath *indexPath = [NSIndexPath indexPathWithIndex:currentScope];
    //[self.tableView selectRowAtIndexPath:indexPath animated:YES scrollPosition:UITableViewScrollPositionNone];
    //NSLog(@"New selected row: %@",indexPath.row);
}

-(void) popupSettingsModal:(id)sender {
    NSLog(@"Hello from popupSettingsModal");
}

-(void) updateFakeStatusSpeed {
    CGFloat randSpeed = [RandUtil logRandomWithMin:0.1 andMax:1.0];
    NSLog(@"seting random speed to %f",randSpeed);
    [sectionHeaderView setDesiredSpeed:randSpeed];
    [sectionHeaderView setFadingStatus:[NSString stringWithFormat:@"speed=%.2f",randSpeed]];
}

-(void) updateOnMainThreadAfterLoad {
    NSLog(@"updateOnMainThreadAfterLoad MainThread=%d",[NSThread isMainThread]);
    //[self.view setNeedsDisplay];
   	[self.tableView.tableHeaderView setNeedsDisplay];

    if ([obsarray.observations count]>0) {
        Observation *observation =  (Observation *)[obsarray.observations objectAtIndex:0];
        CGFloat kW = observation.value/1000.0;
        [sectionHeaderView setFadingStatus:[NSString stringWithFormat:@"%.2f kW  %.0f kWh/d",kW,kW*24.0]];
        [sectionHeaderView setDesiredSpeed:kW/6.0];
    }
}

//  on 3G -->
//  return 262147 = kSCNetworkReachabilityFlagsTransientConnection = 1<<0 |
//                 kSCNetworkReachabilityFlagsReachable = 1<<1           |
//                 kSCNetworkReachabilityFlagsIsWWAN = 1<< 18
// on Newton
// return 131074 = kSCNetworkReachabilityFlagsIsDirect - 1<<17 |
//                 kSCNetworkReachabilityFlagsReachable = 1<<1

- (BOOL)isLocalDataSourceAvailable
{
    static BOOL _isDataSourceAvailable;
    static NSDate *lastCheckedIfLocalDataSourceAvailable = nil;
    //check every 30 seconds
    BOOL checkNetwork = (!lastCheckedIfLocalDataSourceAvailable ||
                         [lastCheckedIfLocalDataSourceAvailable timeIntervalSinceNow] <= -30.0);
    if (checkNetwork) { // Since checking the reachability of a host can be expensive, cache the result and perform the reachability check once.
        if (lastCheckedIfLocalDataSourceAvailable) [lastCheckedIfLocalDataSourceAvailable release];
        lastCheckedIfLocalDataSourceAvailable = [[NSDate date] retain];
        
        Boolean success;    
        const char *host_name = "192.168.5.2";
        
        SCNetworkReachabilityRef reachability = SCNetworkReachabilityCreateWithName(NULL, host_name);
        SCNetworkReachabilityFlags flags;
        success = SCNetworkReachabilityGetFlags(reachability, &flags);
        NSLog(@"Reachability flags: %d",flags);
        _isDataSourceAvailable = success && (flags & kSCNetworkFlagsReachable) && !(flags & kSCNetworkFlagsConnectionRequired);
        CFRelease(reachability);
    }
    //return NO;
    return _isDataSourceAvailable;
}

-(void) loadFromLiveFeed {
    
    NSDate *now = [NSDate date];
    NSLog(@"loadFromLiveFeed MainThread=%d scope:%d",[NSThread isMainThread],currentScope);

	//NSURL *aURL = [NSURL URLWithString:@"http://192.168.5.2/iMetrical/getTED.php"];
	//NSURL *aURL = [NSURL URLWithString:@"http://192.168.5.2/iMetrical/iPhoneTest.php"];
    //NSURL *aURL = [NSURL URLWithString:@"http://dl.sologlobe.com:9999/iMetrical/tedLive.php"];
    //NSURL *aURL = [NSURL URLWithString:@"http://192.168.5.2/iMetrical/tedLive.php"];

    NSURL *baseURL = [NSURL URLWithString:@"http://dl.sologlobe.com:9999/"];
    if ([self isLocalDataSourceAvailable]) {
        baseURL = [NSURL URLWithString:@"http://192.168.5.2/"];
    }
    NSLog(@"Using baseURL: %@",baseURL);
    //NSString *path = [NSString stringWithFormat:@"iMetrical/tedLive.php?scope=%d",currentScope];
    NSString *path = [NSString stringWithFormat:@"iMetrical/wattrical.php?scope=%d",currentScope];
    NSURL *aURL = [NSURL URLWithString:path relativeToURL:baseURL];

	//[obsarray appendObservationsFromURL:aURL];	  
	//[obsarray loadObservationsFromURL:aURL];
    [UIApplication sharedApplication].networkActivityIndicatorVisible = YES;
    [obsarray loadObservationFeedFromURL:aURL];
    [UIApplication sharedApplication].networkActivityIndicatorVisible = NO;
    NSLog(@"time to load (%3d) obs : %7.2f",[obsarray.observations count],-[now timeIntervalSinceNow]);

    // Must call the redraw stuff on main thread...
    //[self updateOnMainThreadAfterLoad];
    [self performSelectorOnMainThread:@selector(updateOnMainThreadAfterLoad) withObject:nil waitUntilDone:NO];
}

#pragma mark NSOperations

//TODO make this a instance VAR
static NSOperationQueue *oq=nil;

- (void)launchFeedOperationIfRequired {
    // Other method of launching
    //[NSThread detachNewThreadSelector:@selector(getEarthquakeData) toTarget:self withObject:nil];

    if (!oq) { oq = [NSOperationQueue new];}

    NSLog(@"-Launching Operation: |q|=%d",[[oq operations] count]);
    // NOT if already has work! : to be refined
    if ([[oq operations] count]>0) {
        NSLog(@"Feed Operation already Queued - call me later!");
        return;
    }
    
    NSInvocationOperation* theOp = [[[NSInvocationOperation alloc] initWithTarget:self 
                                                                         selector:@selector(loadFromLiveFeed) object:nil] autorelease];
    [oq addOperation:theOp];
    NSLog(@"+Launched  Operation: |q|=%d",[[oq operations] count]);
    
}

#pragma mark UITableViewDataSource Protocol 

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
	//return @"Wattrical Feeds:";
	//return @"Status:";
    return @"";
}


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
	return [cellNameArray count];
}


#pragma mark UITableViewDelegate Protocol

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
    NSLog(@"didSelect: %d",indexPath.row);
	[tableView deselectRowAtIndexPath:indexPath animated:YES];
    [self setScope:indexPath.row];
}

/* use     self.tableView.sectionHeaderHeight=XXX; instead
- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 20.0;
}
*/ 
- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    return sectionHeaderView;
}
#pragma mark Other
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
    //[self loadFromLiveFeed];

    // How should I implement : "As fast as possible" ?
    currentScope=0;
	//[NSTimer scheduledTimerWithTimeInterval:TIMER_INTERVAL target:self selector:@selector(loadFromLiveFeed) userInfo:nil repeats:YES];
	[NSTimer scheduledTimerWithTimeInterval:TIMER_INTERVAL target:self selector:@selector(launchFeedOperationIfRequired) userInfo:nil repeats:YES];

	//[NSTimer scheduledTimerWithTimeInterval:8.0 target:self selector:@selector(updateFakeStatusSpeed) userInfo:nil repeats:YES];

    //Set the title of the Main View here.
    self.title = @"Wattrical";

    self.tableView.rowHeight = 40;
    self.tableView.backgroundColor = [UIColor colorWithRed:0.0 green:1.0/3.0 blue:0.0 alpha:1.0];


    UIBarButtonItem *addButton = [[[UIBarButtonItem alloc]
                                   initWithBarButtonSystemItem: UIBarButtonSystemItemCompose
                                   target:self action:@selector(popupSettingsModal:)] autorelease];
	self.navigationItem.rightBarButtonItem = addButton;
    
    
    // setup our table data 
	cellNameArray = [[NSArray arrayWithObjects:@"Live", @"Hour", @"Day", @"Week", @"Month", nil] retain];
    
    //  GraphView as tableHeaderView Height 
#define HEADERVIEW_HEIGHT 180.0
	CGRect newFrame = CGRectMake(0.0, 0.0, self.tableView.bounds.size.width, HEADERVIEW_HEIGHT);
    GraphView *graphView = [[GraphView alloc] initWithFrame:newFrame];
    graphView.rootViewController = self; // hook back to us
	self.tableView.tableHeaderView = graphView;	// note this will override UITableView's 'sectionHeaderHeight' property
    [graphView release]; // now that it has been retained.
    graphView.observations = obsarray.observations;
    
#define SECTIONHEADERVIEW_HEIGHT 15.0
    // Section Header View : used for status
    self.tableView.sectionHeaderHeight=SECTIONHEADERVIEW_HEIGHT;
    sectionHeaderView = [[StatusSectionHeaderView alloc] initWithFrame:CGRectMake(0, 0, self.tableView.bounds.size.width, SECTIONHEADERVIEW_HEIGHT)];
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
    [sectionHeaderView release];
}


@end

