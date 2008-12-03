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
	NSString *feedname;
	NSString *units;
}

@property (nonatomic, retain) Observation *observation;
@property (nonatomic, retain) NSString *feedname;
@property (nonatomic, retain) NSString *units;

@end
