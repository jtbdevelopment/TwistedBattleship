'use strict';

describe('Service: gameActions', function () {
    beforeEach(module('tbs.services'));

    var service, httpBackend;

    var game = {id: 'theid'};
    var returnedGame = {id: 'from the server'};
    var playerUrl = 'http://1234';
    var gameUrl = playerUrl + '/game/' + game.id + '/';
    var gameCacheSpy;
    var stateSpy;
    var ionicLoadingSpy;
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
    var mockIonicAlert;

    beforeEach(module(function ($provide) {
        mockIonicAlert = {
            lastParams: {}
        };
        mockIonicSheetShow.lastParams = {};
        gameCacheSpy = {putUpdatedGame: sinon.spy()};
        stateSpy = {go: sinon.spy()};
        ionicLoadingSpy = {hide: sinon.spy(), show: sinon.spy()};

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
            return mockIonicAlert;
        }]);
        $provide.factory('jtbPlayerService', [function () {
            return mockPlayerService;
        }]);
    }));


    var q;
    beforeEach(inject(function ($httpBackend, $injector, $q) {
        q = $q;
        mockIonicAlert.alert = function (params) {
            this.lastParams = params;
            return q.defer().promise;
        };

        httpBackend = $httpBackend;
        service = $injector.get('tbsActions');
    }));

    describe('for each successful action', function () {
        function standardAssertsForSuccess() {
            assert(ionicLoadingSpy.show.calledOnce);
            assert(ionicLoadingSpy.show.calledWithMatch(expectedSendingLoading));
            httpBackend.flush();
            assert(gameCacheSpy.putUpdatedGame.calledOnce);
            assert(gameCacheSpy.putUpdatedGame.calledWithMatch(returnedGame));
            assert(ionicLoadingSpy.show.calledOnce);
            assert(ionicLoadingSpy.hide.calledOnce);
        }

        it('can accept game', function () {
            httpBackend.expectPUT(gameUrl + 'accept').respond(returnedGame);
            service['accept'](game);
            standardAssertsForSuccess();
        });

        it('can rematch game', function () {
            httpBackend.expectPUT(gameUrl + 'rematch').respond(returnedGame);
            service['rematch'](game);
            standardAssertsForSuccess();
            assert(stateSpy.go.calledOnce);
            assert(stateSpy.go.calledWithMatch());
        });

        var expectedConfirmText = {
            reject: 'Reject this game!',
            quit: 'Quit this game!',
            declineRematch: 'Decline further rematches.'
        };
        angular.forEach(['reject', 'quit', 'declineRematch'], function (action) {
            it('can ' + action + ' game after confirmation', function () {
                service[action](game);
                expect(mockIonicSheetShow.lastParams.buttons).to.deep.equal([]);
                expect(mockIonicSheetShow.lastParams.destructiveText).to.equal(expectedConfirmText[action]);
                expect(mockIonicSheetShow.lastParams.titleText).to.deep.equal('Are you sure?');
                expect(mockIonicSheetShow.lastParams.cancelText).to.deep.equal('Cancel');

                var url = (action === 'declineRematch') ? gameUrl + 'endRematch' : gameUrl + action;
                httpBackend.expectPUT(url).respond(returnedGame);
                mockIonicSheetShow.lastParams.destructiveButtonClicked();
                standardAssertsForSuccess();
            });
        });

        angular.forEach(['reject', 'quit', 'declineRematch'], function (action) {
            it('can ' + action + ' game does nothing after cancel confirmation', function () {
                service[action](game);
                expect(mockIonicSheetShow.lastParams.buttons).to.deep.equal([]);
                expect(mockIonicSheetShow.lastParams.destructiveText).to.equal(expectedConfirmText[action]);
                expect(mockIonicSheetShow.lastParams.titleText).to.deep.equal('Are you sure?');
                expect(mockIonicSheetShow.lastParams.cancelText).to.deep.equal('Cancel');

                expect(ionicLoadingSpy.show.callCount).to.equal(0);
                expect(gameCacheSpy.putUpdatedGame.callCount).to.equal(0);
                expect(ionicLoadingSpy.hide.callCount).to.equal(0);
            });
        });

        it('can setup game', function () {
            var positions = {some: 'stuff', here: 32};
            httpBackend.expectPUT(gameUrl + 'setup', positions).respond(returnedGame);
            service.setup(game, positions);
            standardAssertsForSuccess();
        });

        angular.forEach(['fire', 'spy', 'missile', 'repair', 'move', 'ecm'], function (action) {
            var opponent = 'opp';
            var gridCell = {column: 5, row: 10};
            it('can ' + action + ' game', function () {
                var expectedTarget = {player: opponent, coordinate: {row: gridCell.row, column: gridCell.column}};
                httpBackend.expectPUT(gameUrl + action, expectedTarget).respond(returnedGame);
                service[action](game, opponent, gridCell);
                standardAssertsForSuccess();
            });
        });
    });

    describe('for each failed action on server side', function () {
        function standardAssertsForError(error) {
            assert(ionicLoadingSpy.show.calledOnce);
            assert(ionicLoadingSpy.show.calledWithMatch(expectedSendingLoading));
            httpBackend.flush();
            assert(ionicLoadingSpy.show.calledOnce);
            assert(ionicLoadingSpy.hide.calledOnce);
            expect(gameCacheSpy.putUpdatedGame.callCount).to.equal(0);
            var expectedError = {
                title: 'Error updating game!',
                template: error
            };
            expect(mockIonicAlert.lastParams).to.deep.equal(expectedError);
        }

        it('can accept game fails', function () {
            var error = 'Cannot!';
            httpBackend.expectPUT(gameUrl + 'accept').respond(-1, error);
            service['accept'](game);
            standardAssertsForError(error);
        });

        it('can rematch game fails', function () {
            var error = 'Cannot rematch!';
            httpBackend.expectPUT(gameUrl + 'rematch').respond(-2, error);
            service['rematch'](game);
            standardAssertsForError(error);
            assert(stateSpy.go.calledOnce);
            assert(stateSpy.go.calledWithMatch());
        });

        var expectedConfirmText = {
            reject: 'Reject this game!',
            quit: 'Quit this game!',
            declineRematch: 'Decline further rematches.'
        };
        angular.forEach(['reject', 'quit', 'declineRematch'], function (action) {
            it('can ' + action + ' game after confirmation fails', function () {
                var error = 'Cannot for some reason!';
                service[action](game);
                expect(mockIonicSheetShow.lastParams.buttons).to.deep.equal([]);
                expect(mockIonicSheetShow.lastParams.destructiveText).to.equal(expectedConfirmText[action]);
                expect(mockIonicSheetShow.lastParams.titleText).to.deep.equal('Are you sure?');
                expect(mockIonicSheetShow.lastParams.cancelText).to.deep.equal('Cancel');

                var url = (action === 'declineRematch') ? gameUrl + 'endRematch' : gameUrl + action;
                httpBackend.expectPUT(url).respond(500, error);
                mockIonicSheetShow.lastParams.destructiveButtonClicked();
                standardAssertsForError(error);
            });
        });

        angular.forEach(['fire', 'spy', 'missile', 'repair', 'move', 'ecm'], function (action) {
            var opponent = 'opp';
            var gridCell = {column: 5, row: 10};
            it('can ' + action + ' game fails', function () {
                var error = 'Cannot for some reason!';
                var expectedTarget = {player: opponent, coordinate: {row: gridCell.row, column: gridCell.column}};
                httpBackend.expectPUT(gameUrl + action, expectedTarget).respond(600, error);
                service[action](game, opponent, gridCell);
                standardAssertsForError(error);
            });
        });

        it('can setup game fails server side', function () {
            var positions = {some: 'stuff', here: 32};
            var error = 'Cannot for some reason!';
            httpBackend.expectPUT(gameUrl + 'setup', positions).respond(-30, error);
            service.setup(game, positions);
            standardAssertsForError(error);
        });
    });

    var phases = ['Challenged', 'Declined', 'Quit', 'Setup', 'Playing', 'RoundOver', 'NextRoundStarted'];
    describe('update current view does nothing when phases match', function () {
        angular.forEach(phases, function (phase) {
            it('testing phase ' + phase + ' to ' + phase, function () {
                var oldGame = {gamePhase: phase};
                var updatedGame = {gamePhase: phase};
                service.updateCurrentView(oldGame, updatedGame);
                expect(stateSpy.go.callCount).to.equal(0);
            });
        })
    });

    describe('update current view goes to app.games when challenged/next round/declined', function () {
        angular.forEach(['Challenged', 'NextRoundStarted', 'Declined'], function (phase) {
            it('testing phase switch  to ' + phase, function () {
                var oldGame = {gamePhase: 'X'};
                var updatedGame = {gamePhase: phase};
                service.updateCurrentView(oldGame, updatedGame);
                expect(stateSpy.go.callCount).to.equal(1);
                assert(stateSpy.go.calledWithMatch('app.games'));
            });
        })
    });

    describe('update current view goes to game detail when not challenged/next round/declined', function () {
        angular.forEach(['Quit', 'Setup', 'Playing', 'RoundOver'], function (phase) {
            it('testing phase switch  to ' + phase, function () {
                var oldGame = {gamePhase: 'X'};
                var updatedGame = {gamePhase: phase, id: 'X'};
                service.updateCurrentView(oldGame, updatedGame);
                expect(stateSpy.go.callCount).to.equal(1);
                assert(stateSpy.go.calledWithMatch('app.' + phase.toLowerCase(), {gameID: updatedGame.id}));
            });
        })
    });
});

