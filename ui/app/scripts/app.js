'use strict';

angular.module('tbs', ['ionic', 'tbs.controllers', 'tbs.directives', 'config', 'ion-autocomplete'])
    .config(['$httpProvider', function ($httpProvider) {
        $httpProvider.defaults.withCredentials = true;
    }])
    .run(function ($ionicPlatform) {
        $ionicPlatform.ready(function () {
            // Hide the accessory bar by default (remove this to show the accessory bar above the keyboard
            // for form inputs)
            if (window.cordova && window.cordova.plugins.Keyboard) {
                cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
            }
            if (window.StatusBar) {
                // org.apache.cordova.statusbar required
                StatusBar.hide();
            }
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
            //,
            /*
            //  TODO?
            'responseError': function (rejection) {
                console.log('responseError:' + JSON.stringify(rejection));
                $q.reject(rejection);
            },
            //  TODO
            'requestError': function (rejection) {
                console.log('requestError:' + JSON.stringify(rejection));
                $q.reject(rejection);
            },
            'response': function (response) {
                return response;
            }
             */
        };
    })
    .config(function ($httpProvider) {
        // Pre-process outgoing request URLs
        $httpProvider.interceptors.push('httpEnvInterceptor');

    })
    .config(function ($stateProvider, $urlRouterProvider) {
        $stateProvider
            .state('network', {
                url: '/network',
                templateUrl: 'templates/network.html'
            })
            .state('signin', {
                url: '/signin',
                templateUrl: 'templates/signin.html'
            })
            .state('app', {
                url: '/app',
                abstract: true,
                templateUrl: 'templates/menu.html'
            })
            .state('app.create', {
                url: '/create',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/create.html',
                        controller: 'CreateGameCtrl',
                        resolve: {
                            features: function (jtbGameFeatureService) {
                                return jtbGameFeatureService.features();
                            },
                            friends: function (jtbPlayerService) {
                                return jtbPlayerService.currentPlayerFriends();
                            }
                        }
                    }
                }
            })
            .state('app.games', {
                url: '/games',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/gamelist.html',
                        controller: 'MobileGameListCtrl',
                        resolve: {
                            phases: function (jtbGamePhaseService) {
                                return jtbGamePhaseService.phases();
                            }
                        }
                    }
                }
            })
            .state('app.challenged', {
                url: '/games/challenged/:gameID',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/playerListAndState.html',
                        controller: 'PlayerListAndStateCtrl'
                    }
                }
            })
            .state('app.declined', {
                url: '/games/declined/:gameID',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/playerListAndState.html',
                        controller: 'PlayerListAndStateCtrl'
                    }
                }
            })
            .state('app.quit', {
                url: '/games/quit/:gameID',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/playerListAndState.html',
                        controller: 'PlayerListAndStateCtrl'
                    }
                }
            })
            .state('app.setup', {
                url: '/games/setup/:gameID',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/setup.html',
                        controller: 'SetupGameCtrl',
                        resolve: {
                            shipInfo: function (tbsShips) {
                                return tbsShips.ships();
                            }
                        }
                    }
                }
            })
            .state('app.playing', {
                url: '/games/playing/:gameID',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/game.html',
                        controller: 'GameCtrl',
                        resolve: {
                            shipInfo: function (tbsShips) {
                                return tbsShips.ships();
                            }
                        }
                    }
                }
            })
            .state('app.help', {
                url: '/help',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/help/help-play.html',
                        controller: 'MainCtrl'
                    }
                }
            })
            .state('app.roundover', {
                url: '/games/roundover/:gameID',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/game.html',
                        controller: 'GameCtrl',
                        resolve: {
                            shipInfo: function (tbsShips) {
                                return tbsShips.ships();
                            }
                        }
                    }
                }
            })
            .state('app.nextroundstarted', {
                url: '/games/nextroundstarted/:gameID',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/game.html',
                        controller: 'GameCtrl',
                        resolve: {
                            shipInfo: function (tbsShips) {
                                return tbsShips.ships();
                            }
                        }
                    }
                }
            })
        ;

        // if none of the above states are matched, use this as the fallback
        $urlRouterProvider.otherwise('/network');
    });
