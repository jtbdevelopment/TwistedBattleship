/*global invokeApplixirVideoUnitExtended:false */
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

            var lastInter = new Date(0);

            function requestAdMobInterstitialAd() {
                $cordovaGoogleAds.prepareInterstitial({adId: amInter, autoShow: false});
            }

            //  Admob

            //  Debugging 
            /*
             document.addEventListener('onAdLoaded', function (e) {
             try {
             console.info('Ad Loaded:' + JSON.stringify(e));
             } catch (ex) {
             console.info('Ad Loaded, not serializable');
             }

             });

             document.addEventListener('onAdPresent', function (e) {
             try {
             console.info('Ad Present:' + JSON.stringify(e));
             } catch (ex) {
             console.info('Ad Present, not serializable');
             }
             });
             */
            
            document.addEventListener('onAdDismiss', function (e) {
                try {
                    //console.info('Ad Dismiss:' + JSON.stringify(e));
                } catch (ex) {
                    //console.info('Ad Dismiss, not serializable');
                }
                if (e.adType === 'interstitial') {
                    lastInter = new Date();
                    requestAdMobInterstitialAd();
                }
            });

            document.addEventListener('onAdFailLoad', function (e) {
                try {
                    //console.info('Ad Load Failed:' + JSON.stringify(e));
                } catch (ex) {
                    //console.info('Ad Load Failed, not serializable');
                }
                if (e.adType === 'interstitial') {
                    requestAdMobInterstitialAd();
                }
            });

            //  they clicked on ad
            document.addEventListener('onAdLeaveApp', function (e) {
                try {
                    //console.info('Ad Leave App:' + JSON.stringify(e));
                } catch (ex) {
                    //console.info('Ad Leave App, not serializable');
                }
                if (e.adType === 'interstitial') {
                    requestAdMobInterstitialAd();
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
                        switch (platform) {
                            case IOS:
                                amInter = AM_IOS_INTER;
                                requestAdMobInterstitialAd();
                                $cordovaGoogleAds.createBanner({
                                    adId: AM_IOS_BANNER,
                                    position: AdMob.AD_POSITION.BOTTOM_CENTER,
                                    autoShow: true
                                });
                                break;
                            case ANDROID:
                                amInter = AM_ANDROID_INTER;
                                requestAdMobInterstitialAd();
                                $cordovaGoogleAds.createBanner({
                                    adId: AM_ANDROID_BANNER,
                                    position: AdMob.AD_POSITION.BOTTOM_CENTER,
                                    autoShow: true
                                });
                                break;
                            case BROWSER:
                                (function (d, s, id) {
                                    var js, fjs = d.getElementsByTagName(s)[0];
                                    if (d.getElementById(id)) {
                                        return;
                                    }
                                    js = d.createElement(s);
                                    js.id = id;
                                    js.src = '//developer.appprizes.com/applixir_richmedia.js';
                                    fjs.parentNode.insertBefore(js, fjs);
                                }(document, 'script', 'applixir-jssdk'));
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
                                    requestAdMobInterstitialAd();
                                }
                                break;
                            case BROWSER:
                                try {
                                    invokeApplixirVideoUnitExtended(false, 'middle', function () {
                                        lastInter = new Date();
                                    });
                                } catch (ex) {
                                    console.warn(JSON.stringify(ex));
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            };
        }
    ]
);
