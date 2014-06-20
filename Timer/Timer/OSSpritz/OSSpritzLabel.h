//
//  OSSpritzLabel.h
//  OpenSpritzDemo
//
//  Created by Francesco Mattia on 08/03/14.
//  Copyright (c) 2014 Fr4ncis. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface OSSpritzLabel : UIView

@property (nonatomic, strong) NSString *text;
@property (nonatomic, assign) NSInteger wordsPerMinute;

- (void)start;
- (void)pause;
- (void)gotoWordAtLocation:(int)location;

@end

@protocol OSSpritzLabelDelegate <NSObject>

- (void)highlightRange:(NSRange)range;

@end

@interface OSSpritzLabel ()

@property (nonatomic, strong) id<OSSpritzLabelDelegate> delegate;

@end
