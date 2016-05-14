'use strict';

describe('Service: gameActions', function () {
    // load the controller's module
    beforeEach(module('tbs.services'));

    var service, httpBackend;

    var game = {id: 'theid'};
    var returnedGame = {id: 'from the server'};
    var playerUrl = 'http://1234';
    var gameUrl = playerUrl + '/game/' + game.id + '/';
    var updatedGames = [];
    var gameCacheSpy;
    var stateSpy;
    var ionicLoadingSpy;
    var ionicPopupSpy;
    var mockPlayerService = {
        currentPlayerBaseURL: function () {
            return playerUrl;
        }
    };
    var expectedSendingLoading = {
        template: 'Sending...'
    };
    var mockIonicSheetShow = {
        lastParams: {},
        show: function (params) {
            this.lastParams = params;
        }
    };
    beforeEach(module(function ($provide) {
        gameCacheSpy = {putUpdatedGame: sinon.spy()};
        stateSpy = {go: sinon.spy()};
        ionicLoadingSpy = {hide: sinon.spy(), show: sinon.spy()};
        ionicPopupSpy = {alert: sinon.spy()};

        $provide.factory('jtbGameCache', [function () {
            return gameCacheSpy;
        }]);
        $provide.factory('$state', [function () {
            return stateSpy;
        }]);
        $provide.factory('$ionicActionSheet', [function () {
            return mockIonicSheetShow;
        }]);
        $provide.factory('$ionicLoading', [function () {
            return ionicLoadingSpy;
        }]);
        $provide.factory('$ionicPopup', [function () {
            return ionicPopupSpy;
        }]);
        $provide.factory('jtbPlayerService', [function () {
            return mockPlayerService;
        }]);
    }));


    // Initialize the controller and a mock scope
    beforeEach(inject(function ($httpBackend, $injector) {
        httpBackend = $httpBackend;
        service = $injector.get('tbsActions');
    }));

    describe('for each successful no confirmation required action', function () {
        angular.forEach(['accept'], function (action) {
            it('can ' + action + ' game', function () {
                httpBackend.expectPUT(gameUrl + action).respond(returnedGame);
                service[action](game);
                assert(ionicLoadingSpy.show.calledOnce);
                assert(ionicLoadingSpy.show.calledWithMatch(expectedSendingLoading));
                httpBackend.flush();
                assert(gameCacheSpy.putUpdatedGame.calledOnce);
                assert(gameCacheSpy.putUpdatedGame.calledWithMatch(returnedGame));
                assert(ionicLoadingSpy.show.calledOnce);
                assert(ionicLoadingSpy.hide.calledOnce);
            });
        });

        angular.forEach(['fire', 'spy', 'missile', 'repair', 'move', 'ecm'], function (action) {
            var opponent = 'opp';
            var gridCell = {x: 5, y: 10};
            it('can ' + action + ' game', function () {
                var expectedTarget = {player: opponent, coordinate: {row: gridCell.y, column: gridCell.x}};
                httpBackend.expectPUT(gameUrl + action, expectedTarget).respond(returnedGame);

                service[action](game, opponent, gridCell);
                assert(ionicLoadingSpy.show.calledOnce);
                assert(ionicLoadingSpy.show.calledWithMatch(expectedSendingLoading));
                httpBackend.flush();
                assert(gameCacheSpy.putUpdatedGame.calledOnce);
                assert(gameCacheSpy.putUpdatedGame.calledWithMatch(returnedGame));
                assert(ionicLoadingSpy.show.calledOnce);
                assert(ionicLoadingSpy.hide.calledOnce);
            });
        });
    });
});

