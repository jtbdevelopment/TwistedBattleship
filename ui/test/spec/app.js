'use strict';

describe('testing app js', function () {

    describe('module definition tests', function () {

        var moduleUnderTest;
        var dependencies = [];

        var hasModule = function (module) {
            return dependencies.indexOf(module) >= 0;
        };

        beforeEach(function () {
            moduleUnderTest = angular.module('tbs');
            dependencies = moduleUnderTest.requires;
        });

        it('should load outside dependencies', function () {
            expect(hasModule('coreGamesIonicUi')).to.be.true;
            expect(hasModule('ngCordova')).to.be.true;
            expect(hasModule('ionic')).to.be.true;
            expect(hasModule('angular-multi-select')).to.be.true;
        });

        it('should load config module', function () {
            expect(hasModule('config')).to.be.true;
        });

        it('should load controllers module', function () {
            expect(hasModule('tbs.controllers')).to.be.true;
        });
    });

    describe('config', function () {
        var urlRouterProvider, stateProvider, otherwiseSpy, stateSpy, platform;
        beforeEach(function () {
            module('ui.router');
            angular.module('angular-multi-select', []);
            angular.module('ionic', []);

            module(function ($provide, $stateProvider, $urlRouterProvider) {
                urlRouterProvider = $urlRouterProvider;
                stateProvider = $stateProvider;
                stateSpy = sinon.spy($stateProvider, 'state');
                otherwiseSpy = sinon.spy(urlRouterProvider, 'otherwise');
                platform = {ready: sinon.spy()};
                $provide.value('$ionicPlatform', platform);
            });

            module('tbs');
        });

        beforeEach(inject());

        it('should configure url router default', function () {
            assert(otherwiseSpy.calledWithMatch('network'));
        });

        it('should register /network', function () {
            assert(stateSpy.calledWithMatch('network', {
                url: '/network',
                templateUrl: 'templates/network.html',
                controller: 'CoreIonicNetworkCtrl'
            }));
        });

        it('should register /signin', function () {
            assert(stateSpy.calledWithMatch('signin', {
                url: '/signin',
                templateUrl: 'templates/signin.html',
                controller: 'CoreIonicSignInCtrl'
            }));
        });

        it('should register /signedin', function () {
            assert(stateSpy.calledWithMatch('signedin', {
                url: '/signedin',
                templateUrl: 'templates/signedin.html',
                controller: 'CoreIonicSignedInCtrl'
            }));
        });

        it('should register /app', function () {
            assert(stateSpy.calledWithMatch('app', {
                url: '/app',
                abstract: true,
                templateUrl: 'templates/menu.html'
            }));
        });

        /*
         it('should register /app.create', function() {
         assert(stateSpy.calledWithMatch('app.create', {
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
         }));
         });
         */

        it('should register /app.games', function () {
            assert(stateSpy.calledWithMatch('app.games', {
                url: '/games',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/mainGameList.html',
                        controller: 'MobileGameListCtrl'
                    }
                }
            }));
        });

        it('should register /app.challenged', function () {
            assert(stateSpy.calledWithMatch('app.challenged', {
                url: '/games/challenged/:gameID',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/playerListAndState.html',
                        controller: 'PlayerListAndStateCtrl'
                    }
                }
            }));
        });

        it('should register /app.declined', function () {
            assert(stateSpy.calledWithMatch('app.declined', {
                url: '/games/declined/:gameID',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/playerListAndState.html',
                        controller: 'PlayerListAndStateCtrl'
                    }
                }
            }));
        });

        it('should register /app.quit', function () {
            assert(stateSpy.calledWithMatch('app.quit', {
                url: '/games/quit/:gameID',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/playerListAndState.html',
                        controller: 'PlayerListAndStateCtrl'
                    }
                }
            }));
        });

        it('should register /app.setup', function () {
            assert(stateSpy.calledWithMatch('app.setup', {
                url: '/games/setup/:gameID',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/setup.html',
                        controller: 'SetupGameV2Ctrl'
                    }
                }
            }));
        });

        it('should register /app.playing', function () {
            assert(stateSpy.calledWithMatch('app.playing', {
                url: '/games/playing/:gameID',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/game.html',
                        controller: 'GameV2Ctrl'
                    }
                }
            }));
        });

        it('should register /app.actionLog', function () {
            assert(stateSpy.calledWithMatch('app.actionLog', {
                url: '/games/actionLog/:gameID',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/actionLog.html',
                        controller: 'ActionLogCtrl'
                    }
                }
            }));
        });

        it('should register /app.playerDetails', function () {
            assert(stateSpy.calledWithMatch('app.gameDetails', {
                url: '/games/gameDetails/:gameID',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/gameDetails.html',
                        controller: 'GameDetailsCtrl'
                    }
                }
            }));
        });

        it('should register /app.playerDetails', function () {
            assert(stateSpy.calledWithMatch('app.playerDetails', {
                url: '/games/playerDetails',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/playerDetails.html',
                        controller: 'PlayerDetailsCtrl'
                    }
                }
            }));
        });

        it('should register /app.playhelp', function () {
            assert(stateSpy.calledWithMatch('app.playhelp', {
                url: '/play-help',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/help/help-play.html',
                        controller: 'PlayHelpCtrl'
                    }
                }
            }));
        });

        it('should register /app.nextroundstarted', function () {
            assert(stateSpy.calledWithMatch('app.nextroundstarted', {
                url: '/games/nextroundstarted/:gameID',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/game.html',
                        controller: 'GameV2Ctrl'
                    }
                }
            }));
        });

        it('should register /app.roundover', function () {
            assert(stateSpy.calledWithMatch('app.roundover', {
                url: '/games/roundover/:gameID',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/game.html',
                        controller: 'GameV2Ctrl'
                    }
                }
            }));
        });
    });
});