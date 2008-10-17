//
//  AddObservationViewController.h
//  Weightrical
//
//  Created by Daniel Lauzon on 09/10/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "RootViewController.h"
@interface AddObservationViewController : UIViewController {
	IBOutlet UIDatePicker *datePicker;
    IBOutlet UIPickerView *weightPicker;
	RootViewController *delegate;

}

@property (nonatomic, retain) IBOutlet UIDatePicker *datePicker;
@property (nonatomic, retain) IBOutlet UIPickerView *weightPicker;
@property (nonatomic, retain) RootViewController *delegate;
    
- (NSInteger)selectedValue;
- (NSDate *)selectedDate;
@end
