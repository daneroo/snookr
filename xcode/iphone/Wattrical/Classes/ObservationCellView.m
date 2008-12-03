//
//  ObservationCellView.m
//  TBVAdd
//
//  Created by Daniel Lauzon on 28/10/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

#import "ObservationCellView.h"
#import "ObservationView.h"


@implementation ObservationCellView

@synthesize observationView;

- (id)initWithFrame:(CGRect)frame reuseIdentifier:(NSString *)reuseIdentifier {
    if (self = [super initWithFrame:frame reuseIdentifier:reuseIdentifier]) {
        // Initialization code
		// Create an observation view and add it as a subview of self's contentView.
		CGRect ovFrame = CGRectMake(0.0, 0.0, self.contentView.bounds.size.width, self.contentView.bounds.size.height);
		observationView = [[ObservationView alloc] initWithFrame:ovFrame];
		observationView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
		[self.contentView addSubview:observationView];
    }
    return self;
}


- (void)setSelected:(BOOL)selected animated:(BOOL)animated {

    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void)setFeedName:(NSString *)feedname { 
	observationView.feedname = feedname;
}

- (void)setUnits:(NSString *)units {
	observationView.units = units;
}

- (void)setObservation:(Observation *)newObservation {
	// Pass the observation to the view
	observationView.observation = newObservation;
}



- (void)dealloc {
    [super dealloc];
}


@end
