//
//  ObservationView.m
//  TBVAdd
//
//  Created by Daniel Lauzon on 28/10/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

#import "ObservationView.h"


@implementation ObservationView

// Define LabelTags:
#define DAY_LBL_TAG 42
#define MONTH_LBL_TAG 43
#define TIME_LBL_TAG 44
#define OBS_LBL_TAG 45
// Column Layout Geometry
#define MARGIN 10

@synthesize observation;

- (id)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        // Initialization code
		dateFormatter = [[NSDateFormatter alloc] init];
		[dateFormatter setDateFormat:@"h:mm a"];
		self.opaque = YES;
		self.backgroundColor = [UIColor whiteColor];
        UIColor *sharedGreenTextColor = [UIColor colorWithRed:0.0 green:1.0/3.0 blue:0.0 alpha:1.0];
        
        CGRect lblFrame;
        UILabel *lbl;
        UIFont *font;
        //Day Label
        lblFrame = CGRectMake(MARGIN, 0.0, 50, self.bounds.size.height*2/3);
		lbl= [[UILabel alloc] initWithFrame:lblFrame];
        lbl.tag = DAY_LBL_TAG;
        lbl.text = @"10";
        font = [UIFont boldSystemFontOfSize:20];
        lbl.font = font;
        [font release];
        lbl.textColor = sharedGreenTextColor;
        //lbl.backgroundColor = [UIColor colorWithWhite:.8 alpha:0.5];
        lbl.textAlignment = UITextAlignmentCenter;
		[self addSubview:lbl];
        [lbl release];

        //Month Label
        lblFrame = CGRectMake(MARGIN, self.bounds.size.height/2, 50, self.bounds.size.height/2);
		lbl= [[UILabel alloc] initWithFrame:lblFrame];
        lbl.tag = MONTH_LBL_TAG;
        lbl.text = @"Oct";
        font = [UIFont boldSystemFontOfSize:10];
        lbl.font = font;
        [font release];
        //lbl.textColor = sharedGreenTextColor;
        //lbl.backgroundColor = [UIColor colorWithWhite:.5 alpha:0.5];
        lbl.textAlignment = UITextAlignmentCenter;
		[self addSubview:lbl];
        [lbl release];

        //Time Label
        lblFrame = CGRectMake(MARGIN+50, 0, 100, self.bounds.size.height);
		lbl= [[UILabel alloc] initWithFrame:lblFrame];
        lbl.tag = MONTH_LBL_TAG;
        lbl.text = @"12:34:56";
        font = [UIFont boldSystemFontOfSize:18];
        lbl.font = font;
        [font release];
        lbl.textColor = sharedGreenTextColor;
        //lbl.backgroundColor = [UIColor colorWithWhite:.5 alpha:0.5];
        //lbl.textAlignment = UITextAlignmentLeft;
		[self addSubview:lbl];
        [lbl release];
        
        // Observation Label (weight)
        lblFrame = CGRectMake(self.bounds.size.width-100-MARGIN, 5, 100, self.bounds.size.height-10);
		lbl= [[UILabel alloc] initWithFrame:lblFrame];
        font = [UIFont boldSystemFontOfSize:20];
        lbl.font = font;
        [font release];
        lbl.text = @"199.9";
        lbl.tag = OBS_LBL_TAG;
        //lbl.textColor = [UIColor blackColor];
        //lbl.backgroundColor = [UIColor colorWithWhite:.5 alpha:0.5];
        lbl.textAlignment = UITextAlignmentRight;
		lbl.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin;
		[self addSubview:lbl];
        [lbl release];

        //[sharedGreenTextColor release];
    }
    return self;
}

- (void)setObservation:(Observation *)newObservation {
	NSLog(@"SETOBSERVATION CALLED");
	// If the time zone wrapper changes, update the date formatter and abbreviation string.
	if (observation != newObservation) {
		[observation release];
		observation = [newObservation retain];
        
        [dateFormatter setDateFormat:@"h:mm a"];

	}
	// May be the same wrapper, but the date may have changed, so mark for redisplay
	[self setNeedsDisplay];
}

- (void)NOTdrawRect:(CGRect)rect {
    NSLog(@"-drawRect w:%f   %@",rect.size.width,observation.stamp);
    // Drawing code
    double d = observation.value / 1000.0;
    NSString *text = [[NSString alloc] initWithFormat:@"%.1f", d]; //@"2008-10-28 15:43:26";
    //CGFloat smallFontSize = [UIFont smallSystemFontSize]; //=12
    CGFloat fontSize = [UIFont systemFontSize]; //=12
    fontSize=20;
	UIFont *font = [UIFont boldSystemFontOfSize:fontSize];

    [text drawAtPoint:CGPointMake(10,10) withFont:font];
    
    CGContextRef context = UIGraphicsGetCurrentContext();
	CGContextSetRGBStrokeColor(context, 0.0, 0.0, 0.0, 1.0);
	CGContextMoveToPoint(context, self.bounds.size.width,self.bounds.size.height);
	CGContextAddLineToPoint(context, 0,0);
	CGContextStrokePath(context);
    
}


- (void)dealloc {
    [dateFormatter release];
    [super dealloc];
}


@end
