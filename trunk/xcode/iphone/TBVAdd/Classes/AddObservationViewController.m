//
//  AddObservationViewController.m
//  Weightrical
//
//  Created by Daniel Lauzon on 09/10/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

#import "AddObservationViewController.h"


@implementation AddObservationViewController
@synthesize datePicker;
@synthesize delegate;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
	if (self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil]) {
		self.title = @"Add Observation";
        
        UIBarButtonItem *saveButtonItem = [[[UIBarButtonItem alloc]
                                            initWithBarButtonSystemItem: UIBarButtonSystemItemSave
                                            target:self action:@selector(save)] autorelease];
        self.navigationItem.rightBarButtonItem = saveButtonItem;
        
        UIBarButtonItem *cancelButtonItem = [[[UIBarButtonItem alloc]
                                              initWithBarButtonSystemItem: UIBarButtonSystemItemCancel
                                              target:self action:@selector(cancel)] autorelease];
        
        self.navigationItem.leftBarButtonItem = cancelButtonItem;
        
    }
	return self;
}

- (void)save  {
    NSLog(@"Hello from save callback date=%@", [[datePicker date] description]);
	[self.delegate addObservation:[[datePicker date] description]];
    [[self.delegate tableView] reloadData];
	[self dismissModalViewControllerAnimated:YES];
}
- (void)cancel  {
    NSLog(@"Hello from cancel callback");
	[self dismissModalViewControllerAnimated:YES];
}


/*
 // Implement loadView to create a view hierarchy programmatically.
 - (void)loadView {
 }
 */

/*
 // Implement viewDidLoad to do additional setup after loading the view.
 - (void)viewDidLoad {
 [super viewDidLoad];
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
