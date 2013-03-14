//
//  PlanarViewerController.h
//  G3MiOSDemo
//
//  Created by fpulido on 11/28/12.
//
//

#import <UIKit/UIKit.h>

@interface PlanarViewerController : UIViewController <UIWebViewDelegate>{
    IBOutlet UIWebView *planarWebView; 
}

@property (strong, nonatomic) IBOutlet UIWebView *planarWebView;

@property (strong, nonatomic) IBOutlet UINavigationBar *navigationBar;
@property (strong, nonatomic) IBOutlet UIBarButtonItem *backButton;


- (void) loadPlanarViewerWebView: (NSURL*) url;

@end
