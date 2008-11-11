//
//  ObservationCellView.h
//  TBVAdd
//
//  Created by Daniel Lauzon on 28/10/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
@class Observation;
@class ObservationView;


@interface ObservationCellView : UITableViewCell {
    ObservationView *observationView;
}

- (void)setObservation:(Observation *)newObservation;
@property (nonatomic, retain) ObservationView *observationView;

@end
