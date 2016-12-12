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
                platform = {
                    cb: undefined,
                    ready: function (newCB) {
                        this.cb = newCB;
                    }
                };
                $provide.value('$ionicPlatform', platform);
                window.atmosphere = {};
                $provide.value('$ionicLoading', {});
                $provide.value('$ionicPopup', {});
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
                templateUrl: 'templates/core-ionic/sign-in/network.html',
                controller: 'CoreIonicNetworkCtrl',
                controllerAs: 'network'
            }));
        });

        it('should register /signin', function () {
            assert(stateSpy.calledWithMatch('signin', {
                url: '/signin',
                templateUrl: 'templates/core-ionic/sign-in/sign-in.html',
                controller: 'CoreIonicSignInCtrl',
                controllerAs: 'signIn'
            }));
        });

        it('should register /signedin', function () {
            assert(stateSpy.calledWithMatch('signedin', {
                url: '/signedin',
                templateUrl: 'templates/core-ionic/sign-in/signed-in.html',
                controller: 'CoreIonicSignedInCtrl',
                controllerAs: 'signedIn'
            }));
        });

        it('should register /app', function () {
            assert(stateSpy.calledWithMatch('app', {
                url: '/app',
                abstract: true,
                templateUrl: 'templates/menu.html'
            }));
        });

        it('should register /app.create', function () {
            assert(stateSpy.calledWith(sinon.match('app.create'), sinon.match(function (arg2) {
                expect(arg2.url).to.equal('/create');
                expect(arg2.views.menuContent.templateUrl).to.equal('templates/createWizard.html');
                expect(arg2.views.menuContent.controller).to.equal('CreateGameCtrl');
                expect(arg2.views.menuContent.controllerAs).to.equal('create');
                expect(arg2.views.menuContent.resolve.features).to.be.defined;
                var expectedReturn = {x: 1};
                var stub = {features: sinon.stub()};
                stub.features.returns(expectedReturn);
                expect(arg2.views.menuContent.resolve.features(stub)).to.equal(expectedReturn);
                return true;
            }, 'app.create params')));
        });

        it('should register /app.admin', function () {
            assert(stateSpy.calledWithMatch('app.admin', {
                url: '/admin',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/core-ionic/admin/admin.html',
                        controller: 'CoreAdminCtrl',
                        controllerAs: 'admin'
                    }
                }
            }));
        });

        it('should register /app.games', function () {
            assert(stateSpy.calledWithMatch('app.games', {
                url: '/games',
                views: {
                    'menuContent': {
                        templateUrl: 'templates/mainGameList.html'
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
                        controller: 'PlayerListAndStateCtrl',
                        controllerAs: 'playerList'
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
                        controller: 'PlayerListAndStateCtrl',
                        controllerAs: 'playerList'
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
                        controller: 'PlayerListAndStateCtrl',
                        controllerAs: 'playerList'
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
                        controller: 'SetupGameV2Ctrl',
                        controllerAs: 'setup'
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
                        controller: 'GameV2Ctrl',
                        controllerAs: 'game'
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
                        controller: 'ActionLogCtrl',
                        controllerAs: 'log'
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
                        controller: 'GameDetailsCtrl',
                        controllerAs: 'detail'
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
                        controller: 'PlayerDetailsCtrl',
                        controllerAs: 'detail'
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
                        controller: 'GameV2Ctrl',
                        controllerAs: 'game'
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
                        controller: 'GameV2Ctrl',
                        controllerAs: 'game'
                    }
                }
            }));
        });

        it('hides keyboard if cordova keyoard defined', function () {
            window.cordova = {};
            window.cordova.plugins = {Keyboard: {hideKeyboardAccessoryBar: sinon.spy()}};
            platform.cb();
            expect(window.cordova.plugins.Keyboard.hideKeyboardAccessoryBar.calledWithMatch(true));
        });

        it('does not hide keyboard if keyboard not defined', function () {
            window.cordova = {};
            window.cordova.plugins = {};
            platform.cb();
        });

        it('hides statusbar if cordova statusbar defined', function () {
            window.StatusBar = {hide: sinon.spy()};
            platform.cb();
            expect(window.StatusBar.hide.calledWithMatch());
        });
    });
});