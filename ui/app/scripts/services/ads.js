/*global AdMob:false */
'use strict';

//  TODO - web site ads
angular.module('tbs.services').factory('tbsAds',
    ['$cordovaGoogleAds', '$cordovaDevice',
        function ($cordovaGoogleAds, $cordovaDevice) {
            var TIME_BETWEEN_INTERSTITIALS = 2 * 60 * 1000;  // 2 minutes
            var IOS = 'iOS';
            var BROWSER = 'browser';
            var ANDROID = 'Android';
            var AM_ANDROID_BANNER = 'ca-app-pub-8812482609918940/3876007710';
            var AM_ANDROID_INTER = 'ca-app-pub-8812482609918940/5352740910';
            var AM_IOS_INTER = 'ca-app-pub-8812482609918940/9839986116';
            var AM_IOS_BANNER = 'ca-app-pub-8812482609918940/2316719315';

            var initialized = false;
            var platform = '';
            var amInter = '';

            function requestAMInter() {
                $cordovaGoogleAds.prepareInterstitial({adId: amInter, autoShow: false});
            }

            var lastInter = new Date(0);

            //  Admob
            document.addEventListener('onAdDismiss', function (e) {
                console.info('Ad Dismiss:' + JSON.stringify(e));
                if (e.adType === 'interstitial') {
                    lastInter = new Date();
                    requestAMInter();
                }
            });
            document.addEventListener('onAdLoaded', function (e) {
                console.info('Ad Loaded:' + JSON.stringify(e));
            });
            document.addEventListener('onAdFailLoad', function (e) {
                console.info('Ad Load Failed:' + JSON.stringify(e));
                if (e.adType === 'interstitial') {
                    requestAMInter();
                }
            });
            document.addEventListener('onAdPresent', function (e) {
                console.info('Ad Present:' + JSON.stringify(e));
            });
            document.addEventListener('onAdLeaveApp', function (e) {
                console.info('Ad Leave App:' + JSON.stringify(e));
                if (e.adType === 'interstitial') {
                    requestAMInter();
                }
            });

            return {
                initialize: function () {
                    if (!initialized) {
                        try {
                            platform = $cordovaDevice.getPlatform();
                        } catch (ex) {
                            platform = BROWSER;
                        }
                        console.log('Platform: ' + platform);
                        switch (platform) {
                            case IOS:
                                amInter = AM_IOS_INTER;
                                requestAMInter();
                                $cordovaGoogleAds.createBanner({
                                    adId: AM_IOS_BANNER,
                                    position: AdMob.AD_POSITION.BOTTOM_CENTER,
                                    autoShow: true
                                });
                                break;
                            case ANDROID:
                                amInter = AM_ANDROID_INTER;
                                requestAMInter();
                                $cordovaGoogleAds.createBanner({
                                    adId: AM_ANDROID_BANNER,
                                    position: AdMob.AD_POSITION.BOTTOM_CENTER,
                                    autoShow: true
                                });
                                break;
                            default:
                                break;
                        }
                        initialized = true;
                    }
                },

                showInterstitial: function () {
                    if (((new Date()) - lastInter ) >= TIME_BETWEEN_INTERSTITIALS) {
                        switch (platform) {
                            case IOS:
                            case ANDROID:
                                try {
                                    $cordovaGoogleAds.showInterstitial();
                                } catch (ex) {
                                    console.warn(JSON.stringify(ex));
                                    requestAMInter();
                                }
                                break;
                            default:
                                break;
                        }
                    }
                },

                showBanner: function () {

                }
            };
        }
    ]
);
