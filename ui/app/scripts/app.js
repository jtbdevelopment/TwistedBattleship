'use strict';

angular.module('tbs', ['ionic', 'ngCookies', 'tbs.controllers', 'config', 'ion-autocomplete'])

    .run(function ($ionicPlatform, $cookies) {
        $ionicPlatform.ready(function () {
            // Hide the accessory bar by default (remove this to show the accessory bar above the keyboard
            // for form inputs)
            if (window.cordova && window.cordova.plugins.Keyboard) {
                cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
            }
            if (window.StatusBar) {
                // org.apache.cordova.statusbar required
                StatusBar.styleDefault();
            }
            //  TODO - this is not setting it for endpoint domain
            $cookies['jtbdevelopment.twistedbattleship'] = 'true';
        });
    })
    //  TODO - move interceptor to game core?
    // Custom Interceptor for replacing outgoing URLs
    .factory('httpEnvInterceptor', function ($q, $cacheFactory, ENV) {
        return {
            'request': function (config) {
                if (
                    (
                        //  TODO - this better
                        config.url.indexOf('/api') >= 0 ||
                        config.url.indexOf('/auth') >= 0 ||
                        config.url.indexOf('/signout') >= 0 ||
                        config.url.indexOf('/livefeed') >= 0 ||
                        config.url.indexOf('/signin/authenticate') >= 0
                    ) && config.url.indexOf(ENV.apiEndpoint) < 0) {
                    config.url = ENV.apiEndpoint + config.url;
                }
                return config;
            }
        };
    })
    .config(function ($httpProvider) {
        // Pre-process outgoing request URLs
        $httpProvider.interceptors.push('httpEnvInterceptor');

    })
    .config(function ($stateProvider, $urlRouterProvider) {
        $stateProvider

            //  TODO - menu
            .state('app', {
                url: '/app',
                abstract: true,
                templateUrl: 'templates/menu.html'
            })
            .state('app.signin', {
                url: '/signin',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/signin.html'
                    }
                }
            })
            .state('app.create', {
                url: '/create',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/create.html',
                        controller: 'CreateGameCtrl'
                    }
                }
            })
            .state('app.games', {
                url: '/games',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/gamelist.html',
                        controller: 'MobileGameListCtrl'
                    }
                }
            })
            .state('app.game', {
                url: '/games/:gameId',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/game.html'//,
//  TODO
//                        controller: 'MobileGameListCtrl'
                    }
                }
            });

        // if none of the above states are matched, use this as the fallback
        $urlRouterProvider.otherwise('/app/signin');
    });
