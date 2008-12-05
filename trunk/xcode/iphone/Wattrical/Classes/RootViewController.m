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
// temporary
#include "FeedParser.h"

#define TIMER_INTERVAL 3.0

@implementation RootViewController
@synthesize feedsByName;

#pragma mark Local Controller Hooks 

-(void)cycleScope {
    [self setScope:currentScope+1];
}

-(void)setScope:(NSInteger) aScope {
    currentScope = aScope % [cellNameArray count];
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

-(void) updateLogoAnimation {
	NSDate *animateUntil = ((GraphView *)self.tableView.tableHeaderView).animateUntil;

	//NSLog(@"timer %f",[animateUntil timeIntervalSinceNow]);
    if ([animateUntil timeIntervalSinceNow]<=0) { // expired
		[logoAnimTimer invalidate];
		[logoAnimTimer release];
		logoAnimTimer=nil;
		((GraphView *)self.tableView.tableHeaderView).animateUntil = nil;
	}
   	[self.tableView.tableHeaderView setNeedsDisplay];
}

-(void) updateFakeStatusSpeed {
    CGFloat randSpeed = [RandUtil logRandomWithMin:0.1 andMax:1.0];
    NSLog(@"seting random speed to %f",randSpeed);
    [sectionHeaderView setDesiredSpeed:randSpeed];
    [sectionHeaderView setFadingStatus:[NSString stringWithFormat:@"speed=%.2f",randSpeed]];
}

-(void) updateOnMainThreadAfterLoad {
    NSLog(@"updateOnMainThreadAfterLoad MainThread=%d",[NSThread isMainThread]);
	// feedsByName has been replaced..
	// unless error... test and differnet message

	// graphView.observations = nil; from feedsByName[cellNameArray[currentXcope]]
	if (feedsByName) {
		Feed *activeFeed = nil;
		activeFeed = [feedsByName valueForKey:[cellNameArray objectAtIndex:currentScope]];
		if (activeFeed) {
			GraphView *graphView = (GraphView *)self.tableView.tableHeaderView;
			graphView.observations = activeFeed.observations;
		}
	}

	[sectionHeaderView setFadingStatus:[NSString stringWithFormat:@"Last updated: %@",[NSDate date]]];
	
    //[self.view setNeedsDisplay];
   	[self.tableView.tableHeaderView setNeedsDisplay];

	// prevent animation of status::setDesiredSpeed until main animation stoped
	BOOL logoAnimationIsDone = NULL==((GraphView *)self.tableView.tableHeaderView).animateUntil;
    NSLog(@"logoAnimationIsDone: %d",logoAnimationIsDone);
    if (feedsByName) {
		[self.tableView reloadData];
		Feed *liveFeed = [feedsByName valueForKey:@"Live"];
		if (liveFeed &&	logoAnimationIsDone) {
			CGFloat kW = liveFeed.value/1000.0;
			[sectionHeaderView setDesiredSpeed:kW/6.0];
		}
    }
}


- (BOOL)isLocalDataSourceAvailable
{
    static BOOL _isDataSourceAvailable;
    static NSDate *lastCheckedIfLocalDataSourceAvailable = nil;
    //check every 60 seconds
    BOOL checkNetwork = (!lastCheckedIfLocalDataSourceAvailable ||
                         [lastCheckedIfLocalDataSourceAvailable timeIntervalSinceNow] <= -60.0);
    if (checkNetwork) { // Since checking the reachability of a host can be expensive, cache the result and perform the reachability check once.
        if (lastCheckedIfLocalDataSourceAvailable) [lastCheckedIfLocalDataSourceAvailable release];
        lastCheckedIfLocalDataSourceAvailable = [[NSDate date] retain];
        
		/*
		 Fail fast : dl.sologlobe.com:9999 will point to the wrong machine
		    but return immediately if we are inside.
	        192.168.5.2 will fail slower but is our prefered choice
		 So we should try dl.solo, if it fails, then confirm that 192.168.5.2 works
		 */
		NSURL *checkURL = [NSURL URLWithString:@"http://dl.sologlobe.com:9999/iMetrical/pingplist.php"];
		NSMutableArray *nsd = [NSDictionary dictionaryWithContentsOfURL:checkURL];
		if (nsd==nil) {
			checkURL = [NSURL URLWithString:@"http://192.168.5.2/iMetrical/pingplist.php"];
			nsd = [NSDictionary dictionaryWithContentsOfURL:checkURL];
			//NSDate *stamp = (NSDate *)[nsd objectForKey:@"stamp"];
			NSLog(@"ping local: %@",nsd);
			_isDataSourceAvailable = YES;
		}  else {
			NSLog(@"ping remote: %@",nsd);
			_isDataSourceAvailable = NO;
		}
    }
    return _isDataSourceAvailable;
}

-(void) loadFromLiveFeed {
    
    NSDate *now = [NSDate date];
    NSLog(@"loadFromLiveFeed MainThread=%d scope:%d",[NSThread isMainThread],currentScope);

    NSURL *baseURL = [NSURL URLWithString:@"http://dl.sologlobe.com:9999/"];
    if ([self isLocalDataSourceAvailable]) {
        baseURL = [NSURL URLWithString:@"http://192.168.5.2/"];
    }
    NSLog(@"Using baseURL: %@",baseURL);
    NSString *path = [NSString stringWithFormat:@"iMetrical/feeds.php?scope=%d",currentScope];
    NSURL *aURL = [NSURL URLWithString:path relativeToURL:baseURL];

    [UIApplication sharedApplication].networkActivityIndicatorVisible = YES;
	
    self.feedsByName = [FeedParser feedsByNameAtURL:aURL];
	if (YES) {
		int count=0;
		for (id feedName in feedsByName) {
			Feed *feed = (Feed *)[feedsByName valueForKey:feedName];
			count+= [feed.observations count];
		}		
		NSLog(@"Parsed %d feeds with %d observations in %7.2fs",[feedsByName count],count,-[now timeIntervalSinceNow]);
	}
	
    [UIApplication sharedApplication].networkActivityIndicatorVisible = NO;

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
    static NSString *cellReuseIdentifier = @"ObsCellId";
    ObservationCellView *cell = (ObservationCellView *)[tableView dequeueReusableCellWithIdentifier:cellReuseIdentifier];
    if (cell == nil) {
		CGFloat ROW_HEIGHT = self.tableView.rowHeight; //40 set in viewDidLoad
		CGRect startingRect = CGRectMake(0.0, 0.0, 320.0, ROW_HEIGHT);
        cell = [[[ObservationCellView alloc] initWithFrame:startingRect reuseIdentifier:cellReuseIdentifier] autorelease];
        cell.selectionStyle = UITableViewCellSelectionStyleGray; //UITableViewCellSelectionStyleBlue  UITableViewCellSelectionStyleNone
		//cell.accessoryType = UITableViewCellAccessoryNone;
    }
    
    // Set up the cell
	[cell setFeedName:[cellNameArray objectAtIndex:indexPath.row]];
	[cell setObservation:nil];
	
	// feedForScope, or such
	Feed *feedForRow = nil;
	if (feedsByName) {
		feedForRow = [feedsByName valueForKey:[cellNameArray objectAtIndex:indexPath.row]];
	}
	if (feedForRow) {
		Observation *observation =  [[Observation alloc] init];
		observation.stamp = feedForRow.stamp;
		observation.value = feedForRow.value;
		[cell setUnits: (indexPath.row<2) ? @"W" : @"kWh"];
		[cell setObservation:observation];
		[observation release];
	}
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

    feedsByName = [[NSDictionary alloc] init];
    //[self loadFromLiveFeed]; // not now too early

    // How should I implement : "As fast as possible" ?
    currentScope=1; // Hour
	[NSTimer scheduledTimerWithTimeInterval:TIMER_INTERVAL target:self selector:@selector(launchFeedOperationIfRequired) userInfo:nil repeats:YES];
	// one time fire sooner... 0.5 seconds
	//[NSTimer scheduledTimerWithTimeInterval:0.5 target:self selector:@selector(launchFeedOperationIfRequired) userInfo:nil repeats:NO];

	//[NSTimer scheduledTimerWithTimeInterval:8.0 target:self selector:@selector(updateFakeStatusSpeed) userInfo:nil repeats:YES];
	logoAnimTimer = [NSTimer scheduledTimerWithTimeInterval:0.05 target:self selector:@selector(updateLogoAnimation)  userInfo:nil repeats:YES];

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
	graphView.animateUntil = [NSDate dateWithTimeIntervalSinceNow:10.0];

    graphView.observations = nil;
    
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
    [feedsByName release];
    [sectionHeaderView release];
	[logoAnimTimer release]; // should already be nil and released
}


@end

