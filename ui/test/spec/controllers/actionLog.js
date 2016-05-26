'use strict';

describe('Controller: ActionLogCtrl', function () {
    // load the controller's module
    beforeEach(module('tbs.controllers'));

    var expectedId = 'tada!';
    var expectedGame = {
        id: expectedId,
        maskedPlayersState: {
            actionLog: [
                {}, {}, {}
            ]
        }
    };
    var mockGameCache = {
        getGameForID: function (id) {
            expect(id).to.equal(expectedId);
            return expectedGame;
        }
    };

    var mockGameDetails = {
        something: function () {
        }
    };
    var rootScope, scope, ctrl, stateSpy;

    beforeEach(inject(function ($rootScope, $controller) {
        stateSpy = {go: sinon.spy(), params: {gameID: expectedId}};
        rootScope = $rootScope;
        scope = rootScope.$new();

        expectedGame.maskedPlayersState.actionLog = [
            {}, {}, {}
        ];
        ctrl = $controller('ActionLogCtrl', {
            $scope: scope,
            $state: stateSpy,
            jtbGameCache: mockGameCache,
            tbsGameDetails: mockGameDetails
        });
    }));

    it('initializes', function () {
        expect(scope.gameID).to.equal(expectedId);
        expect(scope.game).to.equal(expectedGame);
        expect(scope.gameDetails).to.equal(mockGameDetails);
        expect(scope.shownEntries).to.deep.equal([]);
        expect(scope.totalEntries).to.equal(3);
        expect(scope.hasMoreEntries()).to.be.true;
    });

    it('small load more puts remaining items in list', function () {
        expect(scope.shownEntries).to.deep.equal([]);
        expect(scope.totalEntries).to.equal(3);
        expect(scope.hasMoreEntries()).to.be.true;
        var oldBC = rootScope.$broadcast;
        rootScope.$broadcast = sinon.spy();
        scope.loadMore();
        expect(scope.shownEntries).to.deep.equal(expectedGame.maskedPlayersState.actionLog);
        expect(scope.totalEntries).to.equal(3);
        expect(scope.hasMoreEntries()).to.be.false;
        assert(rootScope.$broadcast.calledWithMatch('scroll.infiniteScrollComplete'));
        rootScope.$broadcast = oldBC;
    });

    it('entering resets list and adds more', function () {
        scope.shownEntries = [{x: 1}, {u: 1}];
        scope.totalEntries = 4;
        rootScope.$broadcast('$ionicView.enter');
        expect(scope.shownEntries).to.deep.equal(expectedGame.maskedPlayersState.actionLog);
        expect(scope.totalEntries).to.equal(3);
        expect(scope.hasMoreEntries()).to.be.false;
    });

    describe('working with longer list', function () {
        beforeEach(function () {
            while (expectedGame.maskedPlayersState.actionLog.length < 50) {
                expectedGame.maskedPlayersState.actionLog.push(
                    {
                        newL: expectedGame.maskedPlayersState.actionLog.length
                    }
                );
            }
        });
        it('entering resets list to initial chunk of 20', function () {
            rootScope.$broadcast('$ionicView.enter');
            expect(scope.shownEntries).to.deep.equal(expectedGame.maskedPlayersState.actionLog.slice(30, 50));
            expect(scope.totalEntries).to.equal(50);
            expect(scope.hasMoreEntries()).to.be.true;
        });

        it('first load more adds more', function () {
            rootScope.$broadcast('$ionicView.enter');
            scope.loadMore();
            expect(scope.shownEntries).to.deep.equal(
                expectedGame.maskedPlayersState.actionLog.slice(30, 50).concat(
                    expectedGame.maskedPlayersState.actionLog.slice(10, 30)
                ));
            expect(scope.totalEntries).to.equal(50);
            expect(scope.hasMoreEntries()).to.be.true;
        });

        it('second load more adds last batch', function () {
            rootScope.$broadcast('$ionicView.enter');
            scope.loadMore();
            scope.loadMore();
            expect(scope.shownEntries).to.deep.equal(
                expectedGame.maskedPlayersState.actionLog.slice(30, 50).concat(
                    expectedGame.maskedPlayersState.actionLog.slice(10, 30)
                ).concat(
                    expectedGame.maskedPlayersState.actionLog.slice(0, 10)
                ));
            expect(scope.totalEntries).to.equal(50);
            expect(scope.hasMoreEntries()).to.be.false;
        });

        it('third load more does nothing', function () {
            rootScope.$broadcast('$ionicView.enter');
            scope.loadMore();
            scope.loadMore();
            scope.loadMore();
            expect(scope.shownEntries).to.deep.equal(
                expectedGame.maskedPlayersState.actionLog.slice(30, 50).concat(
                    expectedGame.maskedPlayersState.actionLog.slice(10, 30)
                ).concat(
                    expectedGame.maskedPlayersState.actionLog.slice(0, 10)
                ));
            expect(scope.totalEntries).to.equal(50);
            expect(scope.hasMoreEntries()).to.be.false;
        });

    });

    it('game update for game adds new entries if exist', function () {
        var updatedGame = {
            id: expectedId,
            maskedPlayersState: {
                actionLog: [
                    {new1: 'x'},
                    {new2: 'y'},
                    {notpickedup1: '1'},
                    {notpickedup2: '2'},
                    {ignored: '3'}
                ]
            }
        };
        rootScope.$broadcast('$ionicView.enter');
        expect(scope.shownEntries).to.deep.equal(expectedGame.maskedPlayersState.actionLog);
        expect(scope.totalEntries).to.equal(3);
        expect(scope.hasMoreEntries()).to.be.false;
        rootScope.$broadcast('gameUpdated', expectedGame, updatedGame);
        expect(scope.totalEntries).to.equal(5);
        expect(scope.shownEntries).to.deep.equal(expectedGame.maskedPlayersState.actionLog.concat(updatedGame.maskedPlayersState.actionLog.slice(0, 2)));
        expect(scope.hasMoreEntries()).to.be.false;
    });

    it('game update for game does nothing if new entry count the same as old', function () {
        var updatedGame = {
            id: expectedId,
            maskedPlayersState: {
                actionLog: [
                    {notpickedup1: '1'},
                    {notpickedup2: '2'},
                    {ignored: '3'}
                ]
            }
        };
        rootScope.$broadcast('$ionicView.enter');
        expect(scope.shownEntries).to.deep.equal(expectedGame.maskedPlayersState.actionLog);
        expect(scope.totalEntries).to.equal(3);
        expect(scope.hasMoreEntries()).to.be.false;
        rootScope.$broadcast('gameUpdated', expectedGame, updatedGame);
        expect(scope.totalEntries).to.equal(3);
        expect(scope.shownEntries).to.deep.equal(expectedGame.maskedPlayersState.actionLog);
        expect(scope.hasMoreEntries()).to.be.false;
    });

    it('game update for game does nothing if game ids dont match', function () {
        var updatedGame = {
            id: expectedId + 'X',
            maskedPlayersState: {
                actionLog: [
                    {new1: 'x'},
                    {new2: 'y'},
                    {notpickedup1: '1'},
                    {notpickedup2: '2'},
                    {ignored: '3'}
                ]
            }
        };
        rootScope.$broadcast('$ionicView.enter');
        expect(scope.shownEntries).to.deep.equal(expectedGame.maskedPlayersState.actionLog);
        expect(scope.totalEntries).to.equal(3);
        expect(scope.hasMoreEntries()).to.be.false;
        rootScope.$broadcast('gameUpdated', expectedGame, updatedGame);
        expect(scope.totalEntries).to.equal(3);
        expect(scope.shownEntries).to.deep.equal(expectedGame.maskedPlayersState.actionLog);
        expect(scope.hasMoreEntries()).to.be.false;
    });
});
