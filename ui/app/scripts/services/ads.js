/*global AdMob:false */
'use strict';

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

            //  Admob
            document.addEventListener('onAdDismiss', function () {
                lastInter = new Date();
                requestAMInter();
            });

            var lastInter = new Date(0);

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
                                $cordovaGoogleAds.showInterstitial();
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
