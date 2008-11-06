//
//  DateUtil.m
//  TBVAdd
//
//  Created by Daniel Lauzon on 06/11/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

#import "DateUtil.h"

#define ITERATIONS 10000

@implementation DateUtil

#pragma mark Class Methods

+ (NSString *) formatDate:(NSDate *)d withFormat:(NSString *)fmt {
    DateUtil *du = [DateUtil getInstance];
    return [du formatDate:d withFormat:fmt];
}

- (id) init {
    if ((self = [super init])) {
        sharedDateFormatter = [[NSDateFormatter alloc] init];
    }
    return self;
}

- (void)dealloc {
    [sharedDateFormatter release];
    [super dealloc];
}

#pragma mark Instance Methods

- (NSString *) formatDate:(NSDate *)d withFormat:(NSString *)fmt {
    [sharedDateFormatter setDateFormat:fmt];
    NSString *text= [sharedDateFormatter stringFromDate: d];
    return text;
}


-(void) speedTest {
    NSDate *drawStart;
    NSTimeInterval duration;

    NSDate *d = [NSDate date];
    NSString *fmt =  @"yyyy-MM-dd HH:mm:ss";
    NSDateFormatter *reusableDateFormatter = [[NSDateFormatter alloc] init];

    drawStart = [NSDate date];
    for (int i=0;i<ITERATIONS;i++) {
        [reusableDateFormatter setDateFormat:fmt];
        NSString *text= [reusableDateFormatter stringFromDate: d];
        if (i%(ITERATIONS-1)==0) {
            //NSLog(@" it %28d : %@",i,text);
        }
    }

    duration = -[drawStart timeIntervalSinceNow];
    NSLog(@"2-format     : %12.1f i/s %5.3f s",(1.0*ITERATIONS)/duration,duration);

    drawStart = [NSDate date];
    for (int i=0;i<ITERATIONS;i++) {
        [reusableDateFormatter setDateFormat:fmt];
        NSString *text= [reusableDateFormatter stringFromDate: d];
        if (i%(ITERATIONS-1)==0) {
            //NSLog(@" it %28d : %@",i,text);
        }
    }
    duration = -[drawStart timeIntervalSinceNow];
    NSLog(@"3-use        : %12.1f i/s %5.3f s",(1.0*ITERATIONS)/duration,duration);

    [reusableDateFormatter release];

    drawStart = [NSDate date];
    for (int i=0;i<ITERATIONS;i++) {
        NSString *text= [self formatDate:d withFormat:fmt];
        if (i%(ITERATIONS-1)==0) {
            //NSLog(@" it %28d : %@",i,text);
        }
    }
    duration = -[drawStart timeIntervalSinceNow];
    NSLog(@"5-meth format: %12.1f i/s %5.3f s",(1.0*ITERATIONS)/duration,duration);

    drawStart = [NSDate date];
    for (int i=0;i<ITERATIONS;i++) {
        NSString *text= [DateUtil formatDate:d withFormat:fmt];
        if (i%(ITERATIONS-1)==0) {
            //NSLog(@" it %28d : %@",i,text);
        }
    }
    duration = -[drawStart timeIntervalSinceNow];
    NSLog(@"6+meth format: %12.1f i/s %5.3f s",(1.0*ITERATIONS)/duration,duration);
}
 
#pragma mark private Class Methods

+ (DateUtil *)threadInstance {
    NSString *threadInstanceKey=@"iMDateUtilInstanceKey";
    NSMutableDictionary *thDict = [[NSThread currentThread] threadDictionary];
    DateUtil *instance = [thDict objectForKey:threadInstanceKey];
    if (!instance) {
        instance = [[DateUtil alloc] init];
        [thDict setObject:instance forKey:threadInstanceKey];
        NSLog(@"+New Thread Instance");
    }
    return instance;
}

+ (DateUtil *)getInstance {
    return [DateUtil threadInstance];
}

@end
