//
//  RandUtil.h
//  Wattrical
//
//  Created by Daniel Lauzon on 20/11/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

// use as [RandUtil randomWithMin:A andMax:B];
// and    [RandUtil logRandomWithMin:A andMax:B];

@interface RandUtil : NSObject {

}

+(double) randomWithMin:(double)min andMax:(double)max;
+(double) logRandomWithMin:(double)min andMax:(double)max;

@end
