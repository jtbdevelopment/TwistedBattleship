'use strict';

describe('Controller: ActionLogCtrl', function () {
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
    var $rootScope, scope, ctrl, stateSpy;

    beforeEach(inject(function (_$rootScope_, $controller) {
        stateSpy = {go: sinon.spy(), params: {gameID: expectedId}};
        $rootScope = _$rootScope_;
        scope = $rootScope.$new();

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
        expect(ctrl.gameID).to.equal(expectedId);
        expect(ctrl.game).to.equal(expectedGame);
        expect(ctrl.gameDetails).to.equal(mockGameDetails);
        expect(ctrl.shownEntries).to.deep.equal([]);
        expect(ctrl.totalEntries).to.equal(3);
        expect(ctrl.hasMoreEntries()).to.be.true;
    });

    it('small load more puts remaining items in list', function () {
        expect(ctrl.shownEntries).to.deep.equal([]);
        expect(ctrl.totalEntries).to.equal(3);
        expect(ctrl.hasMoreEntries()).to.be.true;
        var oldBC = $rootScope.$broadcast;
        $rootScope.$broadcast = sinon.spy();
        ctrl.loadMore();
        expect(ctrl.shownEntries).to.deep.equal(expectedGame.maskedPlayersState.actionLog);
        expect(ctrl.totalEntries).to.equal(3);
        expect(ctrl.hasMoreEntries()).to.be.false;
        assert($rootScope.$broadcast.calledWithMatch('scroll.infiniteScrollComplete'));
        $rootScope.$broadcast = oldBC;
    });

    it('entering resets list and adds more', function () {
        ctrl.shownEntries = [{x: 1}, {u: 1}];
        ctrl.totalEntries = 4;
        $rootScope.$broadcast('$ionicView.enter');
        expect(ctrl.shownEntries).to.deep.equal(expectedGame.maskedPlayersState.actionLog);
        expect(ctrl.totalEntries).to.equal(3);
        expect(ctrl.hasMoreEntries()).to.be.false;
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
            $rootScope.$broadcast('$ionicView.enter');
            expect(ctrl.shownEntries).to.deep.equal(expectedGame.maskedPlayersState.actionLog.slice(30, 50));
            expect(ctrl.totalEntries).to.equal(50);
            expect(ctrl.hasMoreEntries()).to.be.true;
        });

        it('first load more adds more', function () {
            $rootScope.$broadcast('$ionicView.enter');
            ctrl.loadMore();
            expect(ctrl.shownEntries).to.deep.equal(
                expectedGame.maskedPlayersState.actionLog.slice(30, 50).concat(
                    expectedGame.maskedPlayersState.actionLog.slice(10, 30)
                ));
            expect(ctrl.totalEntries).to.equal(50);
            expect(ctrl.hasMoreEntries()).to.be.true;
        });

        it('second load more adds last batch', function () {
            $rootScope.$broadcast('$ionicView.enter');
            ctrl.loadMore();
            ctrl.loadMore();
            expect(ctrl.shownEntries).to.deep.equal(
                expectedGame.maskedPlayersState.actionLog.slice(30, 50).concat(
                    expectedGame.maskedPlayersState.actionLog.slice(10, 30)
                ).concat(
                    expectedGame.maskedPlayersState.actionLog.slice(0, 10)
                ));
            expect(ctrl.totalEntries).to.equal(50);
            expect(ctrl.hasMoreEntries()).to.be.false;
        });

        it('third load more does nothing', function () {
            $rootScope.$broadcast('$ionicView.enter');
            ctrl.loadMore();
            ctrl.loadMore();
            ctrl.loadMore();
            expect(ctrl.shownEntries).to.deep.equal(
                expectedGame.maskedPlayersState.actionLog.slice(30, 50).concat(
                    expectedGame.maskedPlayersState.actionLog.slice(10, 30)
                ).concat(
                    expectedGame.maskedPlayersState.actionLog.slice(0, 10)
                ));
            expect(ctrl.totalEntries).to.equal(50);
            expect(ctrl.hasMoreEntries()).to.be.false;
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
        $rootScope.$broadcast('$ionicView.enter');
        expect(ctrl.shownEntries).to.deep.equal(expectedGame.maskedPlayersState.actionLog);
        expect(ctrl.totalEntries).to.equal(3);
        expect(ctrl.hasMoreEntries()).to.be.false;
        $rootScope.$broadcast('gameUpdated', expectedGame, updatedGame);
        expect(ctrl.totalEntries).to.equal(5);
        expect(ctrl.shownEntries).to.deep.equal(expectedGame.maskedPlayersState.actionLog.concat(updatedGame.maskedPlayersState.actionLog.slice(0, 2)));
        expect(ctrl.hasMoreEntries()).to.be.false;
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
        $rootScope.$broadcast('$ionicView.enter');
        expect(ctrl.shownEntries).to.deep.equal(expectedGame.maskedPlayersState.actionLog);
        expect(ctrl.totalEntries).to.equal(3);
        expect(ctrl.hasMoreEntries()).to.be.false;
        $rootScope.$broadcast('gameUpdated', expectedGame, updatedGame);
        expect(ctrl.totalEntries).to.equal(3);
        expect(ctrl.shownEntries).to.deep.equal(expectedGame.maskedPlayersState.actionLog);
        expect(ctrl.hasMoreEntries()).to.be.false;
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
        $rootScope.$broadcast('$ionicView.enter');
        expect(ctrl.shownEntries).to.deep.equal(expectedGame.maskedPlayersState.actionLog);
        expect(ctrl.totalEntries).to.equal(3);
        expect(ctrl.hasMoreEntries()).to.be.false;
        $rootScope.$broadcast('gameUpdated', expectedGame, updatedGame);
        expect(ctrl.totalEntries).to.equal(3);
        expect(ctrl.shownEntries).to.deep.equal(expectedGame.maskedPlayersState.actionLog);
        expect(ctrl.hasMoreEntries()).to.be.false;
    });
});
