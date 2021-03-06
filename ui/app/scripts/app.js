'use strict';

angular.module('tbs', ['tbsUI', 'tbsBackground']);

angular.module('tbsBackground', ['ionic'])
//  Separate module to avoid interfering with tests
    .run(function ($rootScope, $state) {
        $rootScope.$on('gameUpdated', function (message, oldGame, newGame) {
            if (angular.isDefined($state.params.gameID) &&
                $state.params.gameID === oldGame.id &&
                oldGame.gamePhase !== newGame.gamePhase) {
                $state.go('app.' + newGame.gamePhase.toLowerCase(), {gameID: newGame.id});
            }
        });
    });

angular.module('tbsUI', ['ionic', 'ngCordova', 'angular-multi-select', 'tbs.controllers', 'tbs.directives', 'config', 'coreGamesIonicUi'])
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
                templateUrl: 'templates/core-ionic/sign-in/network.html',
                controller: 'CoreIonicNetworkCtrl',
                controllerAs: 'network'
            })
            .state('signin', {
                url: '/signin',
                templateUrl: 'templates/core-ionic/sign-in/sign-in.html',
                controller: 'CoreIonicSignInCtrl',
                controllerAs: 'signIn'
            })
            .state('signedin', {
                url: '/signedin',
                templateUrl: 'templates/core-ionic/sign-in/signed-in.html',
                controller: 'CoreIonicSignedInCtrl',
                controllerAs: 'signedIn'
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
                        templateUrl: 'templates/core-ionic/admin/admin.html',
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
                        controllerAs: 'create',
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
                        templateUrl: 'templates/mainGameList.html'
                    }
                }
            })
            .state('app.challenged', {
                url: '/games/challenged/:gameID',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/playerListAndState.html',
                        controller: 'PlayerListAndStateCtrl',
                        controllerAs: 'playerList'
                    }
                }
            })
            .state('app.declined', {
                url: '/games/declined/:gameID',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/playerListAndState.html',
                        controller: 'PlayerListAndStateCtrl',
                        controllerAs: 'playerList'
                    }
                }
            })
            .state('app.quit', {
                url: '/games/quit/:gameID',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/playerListAndState.html',
                        controller: 'PlayerListAndStateCtrl',
                        controllerAs: 'playerList'
                    }
                }
            })
            .state('app.setup', {
                url: '/games/setup/:gameID',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/setup.html',
                        controller: 'SetupGameV2Ctrl',
                        controllerAs: 'setup'
                    }
                }
            })
            .state('app.playing', {
                url: '/games/playing/:gameID',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/game.html',
                        controller: 'GameV2Ctrl',
                        controllerAs: 'game'
                    }
                }
            })
            .state('app.actionLog', {
                url: '/games/actionLog/:gameID',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/actionLog.html',
                        controller: 'ActionLogCtrl',
                        controllerAs: 'log'
                    }
                }
            })
            .state('app.gameDetails', {
                url: '/games/gameDetails/:gameID',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/gameDetails.html',
                        controller: 'GameDetailsCtrl',
                        controllerAs: 'detail'
                    }
                }
            })
            .state('app.playerDetails', {
                url: '/games/playerDetails',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/playerDetails.html',
                        controller: 'PlayerDetailsCtrl',
                        controllerAs: 'detail'
                    }
                }
            })
            .state('app.playhelp', {
                url: '/play-help',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/help/help-play.html',
                        controller: 'PlayHelpCtrl',
                        controllerAs: 'playHelp'
                    }
                }
            })
            .state('app.roundover', {
                url: '/games/roundover/:gameID',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/game.html',
                        controller: 'GameV2Ctrl',
                        controllerAs: 'game'
                    }
                }
            })
            .state('app.nextroundstarted', {
                url: '/games/nextroundstarted/:gameID',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/game.html',
                        controller: 'GameV2Ctrl',
                        controllerAs: 'game'
                    }
                }
            })
        ;

        // if none of the above states are matched, use this as the fallback
        $urlRouterProvider.otherwise('/network');
    }]);
