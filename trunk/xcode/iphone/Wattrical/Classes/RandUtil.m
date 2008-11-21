//
//  RandUtil.m
//  Wattrical
//
//  Created by Daniel Lauzon on 20/11/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

#import "RandUtil.h"


@implementation RandUtil

// use as [RandUtil randomWithMin:A andMax:B];
+(double) randomWithMin:(double)min andMax:(double) max {
    static long rez = 100000;
    double r01 = ((double)(random()%rez))/rez;
    return r01*(max-min)+min;
}

// use as [RandUtil logRandomWithMin:A andMax:B];
+(double) logRandomWithMin:(double)min andMax:(double) max {
    double lr = [RandUtil randomWithMin:log(min) andMax:log(max)];
    //NSLog(@"log %f < %f < %f",log(min),lr,log(max));
    //NSLog(@"lin %f < %f < %f",min,exp(lr),max);
    return exp(lr);
}

@end
