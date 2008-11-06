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

#pragma mark  NSDateFormatter Format Constants

NSString *const iMDateFormatFullISO       = @"yyyy-MM-dd HH:mm:ss";
NSString *const iMDateFormatISOTime       = @"HH:mm:ss";
NSString *const iMDateFormatISODate       = @"yyyy-MM-dd";
NSString *const iMDateFormatShortWeekDay  = @"EEE";
NSString *const iMDateFormatNarrowWeekDay = @"EEEEE";
NSString *const iMDateFormatShortMonth    = @"MMM";
NSString *const iMDateFormatNarrowMonth   = @"MMMMM";
NSString *const iMDateFormatDayOfMonth    = @"d";
NSString *const iMDateFormatHM24          = @"HH:mm";

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
