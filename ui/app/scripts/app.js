'use strict';

angular.module('tbs', ['ionic', 'ngCordova', 'angular-multi-select', 'tbs.controllers', 'tbs.directives', 'config', 'coreGamesIonicUi'])
    .constant('Phaser', window.Phaser)
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
    .config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
        $stateProvider
            .state('network', {
                url: '/network',
                templateUrl: 'templates/network.html',
                controller: 'CoreIonicNetworkCtrl'
            })
            .state('signin', {
                url: '/signin',
                templateUrl: 'templates/signin.html',
                controller: 'CoreIonicSignInCtrl'
            })
            .state('signedin', {
                url: '/signedin',
                templateUrl: 'templates/signedin.html',
                controller: 'CoreIonicSignedInCtrl'
            })
            .state('app', {
                url: '/app',
                abstract: true,
                templateUrl: 'templates/menu.html'
            })
            .state('app.admin', {
                url: '/admin',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/admin/admin.html',
                        controller: 'CoreAdminCtrl',
                        controllerAs: 'admin'
                    }
                }
            })
            .state('app.create', {
                url: '/create',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/createWizard.html',
                        controller: 'CreateGameCtrl',
                        resolve: {
                            features: function (jtbGameFeatureService) {
                                return jtbGameFeatureService.features();
                            }
                        }
                    }
                }
            })
            .state('app.games', {
                url: '/games',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/mainGameList.html',
                        controller: 'MobileGameListCtrl'
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
                        controller: 'SetupGameV2Ctrl'
                    }
                }
            })
            .state('app.playing', {
                url: '/games/playing/:gameID',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/game.html',
                        controller: 'GameV2Ctrl'
                    }
                }
            })
            .state('app.actionLog', {
                url: '/games/actionLog/:gameID',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/actionLog.html',
                        controller: 'ActionLogCtrl'
                    }
                }
            })
            .state('app.gameDetails', {
                url: '/games/gameDetails/:gameID',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/gameDetails.html',
                        controller: 'GameDetailsCtrl'
                    }
                }
            })
            .state('app.playerDetails', {
                url: '/games/playerDetails',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/playerDetails.html',
                        controller: 'PlayerDetailsCtrl'
                    }
                }
            })
            .state('app.playhelp', {
                url: '/play-help',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/help/help-play.html',
                        controller: 'PlayHelpCtrl'
                    }
                }
            })
            .state('app.roundover', {
                url: '/games/roundover/:gameID',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/game.html',
                        controller: 'GameV2Ctrl'
                    }
                }
            })
            .state('app.nextroundstarted', {
                url: '/games/nextroundstarted/:gameID',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/game.html',
                        controller: 'GameV2Ctrl'
                    }
                }
            })
        ;

        // if none of the above states are matched, use this as the fallback
        $urlRouterProvider.otherwise('/network');
    }]);
