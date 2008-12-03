//
//  ObservationView.m
//  TBVAdd
//
//  Created by Daniel Lauzon on 28/10/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

#import "ObservationView.h"
#import "DateUtil.h"


@implementation ObservationView

// Define LabelTags:
#define FEEDNAME_LBL_TAG 42
#define OBS_LBL_TAG 43
#define UNITS_LBL_TAG 44
// Column Layout Geometry
#define MARGIN 15
#define FEEDNAME_COL_WIDTH 100
#define OBS_COL_WIDTH 100
#define UNITS_COL_WIDTH 40

@synthesize observation;
@synthesize feedname;
@synthesize units;

- (id)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        // Initialization code
		self.opaque = YES;
		//self.backgroundColor = [UIColor whiteColor]; // blanks Grouped Corners
        UIColor *sharedGreenTextColor = [UIColor colorWithRed:0.0 green:1.0/3.0 blue:0.0 alpha:1.0];
        
        CGRect lblFrame;
        UILabel *lbl;
        UIFont *font;
		
        //Feed Name Label
        lblFrame = CGRectMake(MARGIN, 0, FEEDNAME_COL_WIDTH, self.bounds.size.height-5);
		lbl= [[UILabel alloc] initWithFrame:lblFrame];
        lbl.tag = FEEDNAME_LBL_TAG;
        font = [UIFont systemFontOfSize:20];
        lbl.font = font;
        lbl.textColor = [UIColor darkGrayColor];
        lbl.backgroundColor = [UIColor clearColor];
        lbl.textAlignment = UITextAlignmentLeft;
		[self addSubview:lbl];
        [lbl release];

		//Unit Label
        lblFrame = CGRectMake(self.bounds.size.width-UNITS_COL_WIDTH-MARGIN, 0, UNITS_COL_WIDTH, self.bounds.size.height-5);
		lbl= [[UILabel alloc] initWithFrame:lblFrame];
        lbl.tag = UNITS_LBL_TAG;
        font = [UIFont systemFontOfSize:20];
        lbl.font = font;
        lbl.textColor = [UIColor darkGrayColor];
        lbl.backgroundColor = [UIColor clearColor];
        lbl.textAlignment = UITextAlignmentLeft;
		lbl.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin;
		[self addSubview:lbl];
        [lbl release];
		
        
        // Observation Label (weight)
        lblFrame = CGRectMake(self.bounds.size.width-OBS_COL_WIDTH-UNITS_COL_WIDTH-MARGIN, 0, OBS_COL_WIDTH, self.bounds.size.height-5);
		lbl= [[UILabel alloc] initWithFrame:lblFrame];
        font = [UIFont boldSystemFontOfSize:20];
        lbl.font = font;
        lbl.tag = OBS_LBL_TAG;
		lbl.textColor = sharedGreenTextColor;
        lbl.backgroundColor = [UIColor clearColor];
        lbl.textAlignment = UITextAlignmentRight;
		lbl.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin;
		[self addSubview:lbl];
        [lbl release];
    }
    return self;
}

- (void)setObservation:(Observation *)newObservation {
	UILabel *label;
	if (observation != newObservation) {
		[observation release];
		observation = [newObservation retain];
		
		// Units
		label = (UILabel *)[self viewWithTag:UNITS_LBL_TAG];
		label.text = units;

		// Observation
		if ([units caseInsensitiveCompare:@"W"]==NSOrderedSame) {
			double d = observation.value;// / 1000.0;
			label = (UILabel *)[self viewWithTag:OBS_LBL_TAG];
			label.text = [[NSString alloc] initWithFormat:@"%.0f ", d];
		} else {
			double d = observation.value* 24.0 / 1000.0;
			label = (UILabel *)[self viewWithTag:OBS_LBL_TAG];
			label.text = [[NSString alloc] initWithFormat:@"%.1f ", d];
		}
	}
	// Feed name
	label = (UILabel *)[self viewWithTag:FEEDNAME_LBL_TAG];
	label.text = feedname;
	// May be the same wrapper, but the date may have changed, so mark for redisplay
	[self setNeedsDisplay];
}


- (void)dealloc {
    [observation release];
    [super dealloc];
}


@end
