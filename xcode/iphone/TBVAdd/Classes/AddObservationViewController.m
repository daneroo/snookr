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
@synthesize weightPicker;
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

- (void)viewWillAppear:(BOOL)animated {
    NSLog(@"viewWillAppear Reset Picker date");
    [datePicker setDate:[NSDate date]];
    //datePicker.date = [NSDate date];
    [weightPicker selectRow:176 inComponent:0 animated:NO];
    
}

- (void)save  {
	static NSDateFormatter *dateFormatter = nil;
	if (dateFormatter == nil) {
		dateFormatter = [[NSDateFormatter alloc] init];
		[dateFormatter setDateFormat:@"HH:mm:ss"];
	}
    NSString *pickedStr = [dateFormatter stringFromDate:[datePicker date]];

    NSLog(@"Hello from save callback date=%@", pickedStr);
    
	[self.delegate addStampedObservation:pickedStr];
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


#pragma mark -
#pragma mark PickerView delegate methods

- (void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component
{
    double val = [pickerView selectedRowInComponent:0] + 0.1l* [pickerView selectedRowInComponent:2];
    
    NSNumber *num = [NSNumber numberWithDouble:val];
    NSLog(@"Picked %d : %d value: %@", component,row,[num stringValue]);
/*
 // report the selection to the UI label
	label.text = [NSString stringWithFormat:@"%@ - %d",
                  [pickerViewArray objectAtIndex:[pickerView selectedRowInComponent:0]], [pickerView selectedRowInComponent:1]];
*/
 }

- (NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component
{
    if (component == 1) {
        return @".";
	}
    return [[NSNumber numberWithInt:row] stringValue];
}

- (CGFloat)pickerView:(UIPickerView *)pickerView widthForComponent:(NSInteger)component
{
	if (component == 0) return 60.0;	// hold thre digits
	if (component == 1) return 20.0;    // hold the decimal point
	if (component == 2) return 40.0;    // hold the decimal digit
	return 40; // whatever
}

- (CGFloat)NOTpickerView:(UIPickerView *)pickerView rowHeightForComponent:(NSInteger)component
{
	return 40.0;
}

- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component
{
    switch (component) {
        case 0:
            return 400;
            break;
        case 2:
            return 10;
            break;
        default:
            return 1;
            break;
    }
}

- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView
{
	return 3;
}



@end
