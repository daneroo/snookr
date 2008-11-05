//
//  GraphView.h
//  CustomCellView
//
//  Created by Daniel Lauzon on 22/10/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

/*
 The graphview's jobs is to render the data contained in it's
   NSMutableArray *observations property
 
 Data analysis:
   finding value range bounds (stamp,value) in {}..{}
 
 */
@interface GraphView : UIView {
    NSMutableArray *observations;

    NSTimeInterval minTime,maxTime;
    CGFloat minVal,maxVal,startVal,goalVal;
	int daysAgo;
}

@property(nonatomic, retain) NSMutableArray *observations;

@end
