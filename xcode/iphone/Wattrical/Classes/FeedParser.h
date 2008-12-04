//
//  FeedParser.h
//  Wattrical
//
//  Created by Daniel on 2008-12-04.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface FeedParser : NSObject {
    NSMutableArray *feeds;        // NSMutableArray of Feed objects
}

+ (NSDictionary *)feedsByNameAtURL:(NSURL *)xmlURL;
- (NSDictionary *)feedsByNameAtURL:(NSURL *)xmlURL;
- (NSMutableArray *)parseXMLFileAtURL:(NSURL *)xmlURL;

@end
