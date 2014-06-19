//
//  EZTableViewCell.h
//  Timer
//
//  Created by msavula on 4/15/14.
//  Copyright (c) 2014 Eleks. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface EZTableViewCell : UITableViewCell

@property (nonatomic, strong) IBOutlet UILabel *titleLabel;
@property (nonatomic, strong) IBOutlet UILabel *valueLabel;
@property (nonatomic, strong) IBOutlet UILabel *subtitleLabel;

@end
