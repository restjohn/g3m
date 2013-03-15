//
//  PlanarViewerController.m
//  G3MiOSDemo
//
//  Created by fpulido on 11/28/12.


#import "PlanarViewerController.h"

@implementation PlanarViewerController

@synthesize planarWebView;
@synthesize navigationBar;
@synthesize backButton;

- (void)viewDidUnload {
    [self setPlanarWebView:nil];
    [self setNavigationBar:nil];
    [self setBackButton:nil];
    [super viewDidUnload];
}

- (void) loadPlanarViewerWebView: (NSURL*) url{
    
    //NSLog(@"URL DESCRIPTION: %@",url.debugDescription);
//    self.navigationItem.backBarButtonItem = backButton;
//    backButton.target=self;
//    backButton.action=@selector(close); 

    //CGRect webFrame = self.planarWebView.bounds;
    //CGRect webFrame = [[UIScreen mainScreen] applicationFrame];
    //CGRect appFrame = [[UIScreen mainScreen] applicationFrame];
    //CGRect webFrame = CGRectMake(0, 44, appFrame.size.width, appFrame.size.height-44);
    //planarWebView = [[UIWebView alloc] initWithFrame:webFrame];

//    self.planarWebView.delegate = self;
//    [self.view addSubview:self.planarWebView];
    [self.planarWebView loadRequest:[NSURLRequest requestWithURL:url]];
}

- (void) close {
    //[self.parentViewController dismissModalViewControllerAnimated:YES];
    [self.presentingViewController dismissModalViewControllerAnimated:NO];

}

- (void) viewDidLoad{
    [super viewDidLoad];
    self.navigationItem.backBarButtonItem = backButton;
    backButton.target=self;
    backButton.action=@selector(close);
    
    self.planarWebView.delegate = self;
    self.planarWebView.autoresizesSubviews = NO;
    self.planarWebView.scalesPageToFit = NO;
    self.planarWebView.autoresizingMask = UIViewAutoresizingNone;
    [self.view addSubview:self.planarWebView];
}


@end
