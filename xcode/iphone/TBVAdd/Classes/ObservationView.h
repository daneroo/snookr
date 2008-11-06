//
//  ObservationView.h
//  TBVAdd
//
//  Created by Daniel Lauzon on 28/10/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Observation.h"

@interface ObservationView : UIView {
	Observation *observation;
}

@property (nonatomic, retain) Observation *observation;

@end
