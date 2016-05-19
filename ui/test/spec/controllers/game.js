'use strict';

describe('Controller: GameCtrl', function () {
    // load the controller's module
    beforeEach(module('tbs.controllers'));

    var shipInfo = {};  //  TODO
    var expectedId = 'tada!';
    var expectedPhase = 'APhase';
    var currentPlayer = {source: 'MANUAL', md5: 'my md 5', theme: 'theme!'};
    var selectedOpponent = 'md3';
    var mockPlayerService = {
        currentPlayer: function () {
            return currentPlayer;
        }
    };
    var mockGameCache = {
        getGameForID: function (id) {
            expect(id).to.equal(expectedId);
            return expectedGame;
        }
    };

    var mockGameDetails = {
        x: function () {
        }
    };

    var mockSelectedCell, mockSelectedShip;
    var mockShipService = {
        highlightCB: undefined,
        activateHighlighting: function (cb) {
            this.highlightCB = cb;
        },
        selectedCell: function () {
            return mockSelectedCell;
        },
        selectedShip: function () {
            return mockSelectedShip;
        },
        initialize: function (game, ships, markers, cb) {
            expect(game).to.equal(expectedGame);
            expect([]).to.deep.equal(ships);
            expect([]).to.deep.equal(markers);
            cb();
        }
    };
    var rootScope, scope, ctrl, stateSpy, q, phasePromise, ionicLoadingSpy, ionicPopupSpy, timeout, ads, actionsSpy, expectedGame;

    beforeEach(inject(function ($rootScope, $controller, $q, $timeout) {
        expectedGame = {
            id: expectedId,
            features: [],
            gamePhase: expectedPhase,
            currentPlayer: currentPlayer.md5,
            players: {
                md1: {},
                md2: {},
                md3: {}
            },
            maskedPlayersState: {
                opponentGrids: {
                    md3: {
                        table: {x: 1, y: 2, s: 'X'}
                    }
                }
            }
        };
        expectedGame.maskedPlayersState.opponentGrids[selectedOpponent] = {table: {x: 1, y: 2, s: 'X'}};
        stateSpy = {go: sinon.spy(), params: {gameID: expectedId}};
        ionicLoadingSpy = {show: sinon.spy(), hide: sinon.spy()};
        ionicPopupSpy = {alert: sinon.spy()};
        ads = {showInterstitial: sinon.spy()};
        rootScope = $rootScope;
        q = $q;
        timeout = $timeout;
        phasePromise = $q.defer();
        scope = rootScope.$new();
        mockSelectedCell = null;
        mockSelectedShip = null;
        mockShipService.placeShips = sinon.spy();
        mockShipService.placeCellMarkers = sinon.spy();
        mockShipService.stop = sinon.spy();
        actionsSpy = {
            repair: sinon.spy(),
            missile: sinon.spy(),
            ecm: sinon.spy(),
            spy: sinon.spy(),
            fire: sinon.spy(),
            declineRematch: sinon.spy(),
            rematch: sinon.spy(),
            move: sinon.spy(),
            quit: sinon.spy(),
            updateCurrentView: sinon.spy()
        };

        ctrl = $controller('GameCtrl', {
            $scope: scope,
            $state: stateSpy,
            tbsActions: actionsSpy,
            $rootScope: rootScope,
            jtbGameCache: mockGameCache,
            tbsGameDetails: mockGameDetails,
            $ionicLoading: ionicLoadingSpy,
            $ionicPopup: ionicPopupSpy,
            jtbPlayerService: mockPlayerService,
            $timeout: timeout,
            tbsAds: ads,
            shipInfo: shipInfo,
            tbsShipGrid: mockShipService
        });
    }));

    it('initializes', function () {
        expect(scope.gameID).to.equal(expectedId);
        expect(scope.game).to.equal(expectedGame);
        expect(scope.gameDetails).to.equal(mockGameDetails);
        expect(scope.playerKeys).to.deep.equal(['md1', 'md2', 'md3']);
        expect(scope.player).to.equal(currentPlayer);
        expect(scope.showing).to.equal('ALL');
        expect(scope.showingSelf).to.be.false;
        expect(scope.shipHighlighted).to.be.false;
    });

    it('shows action log', function () {
        scope.showActionLog();
        assert(stateSpy.go.calledWithMatch('app.actionLog', {gameID: expectedId}));
    });

    it('shows game details', function () {
        scope.showDetails();
        assert(stateSpy.go.calledWithMatch('app.gameDetails', {gameID: expectedId}));
    });

    it('shows help', function () {
        scope.showHelp();
        assert(stateSpy.go.calledWithMatch('app.playhelp'));
    });

    it('declines rematch', function () {
        scope.declineRematch();
        assert(actionsSpy.declineRematch.calledWithMatch(expectedGame));
    });

    it('start rematch', function () {
        scope.rematch();
        assert(actionsSpy.rematch.calledWithMatch(expectedGame));
    });


    it('quit game', function () {
        scope.quit();
        assert(actionsSpy.quit.calledWithMatch(expectedGame));
    });

    describe('tests with cell selected', function () {
        angular.forEach([true, false], function (self) {
            beforeEach(function () {
                mockSelectedCell = {x: 1, y: 2};
                if (self) {
                    scope.showing = selectedOpponent;
                }
            });
            it('ecm on self=' + self, function () {
                scope.showingSelf = self;
                scope.ecm();
                console.log(scope.showingSelf);
                assert(actionsSpy.ecm.calledWith(expectedGame, self ? currentPlayer.md5 : selectedOpponent, mockSelectedCell));
            });

            it('repair on self=' + self, function () {
                scope.showingSelf = self;
                scope.repair();
                console.log(scope.showingSelf);
                assert(actionsSpy.repair.calledWith(expectedGame, self ? currentPlayer.md5 : selectedOpponent, mockSelectedCell));
            });

            it('spy on self=' + self, function () {
                scope.showingSelf = self;
                scope.spy();
                console.log(scope.showingSelf);
                assert(actionsSpy.spy.calledWith(expectedGame, self ? currentPlayer.md5 : selectedOpponent, mockSelectedCell));
            });

            it('missile on self=' + self, function () {
                scope.showingSelf = self;
                scope.missile();
                console.log(scope.showingSelf);
                assert(actionsSpy.missile.calledWith(expectedGame, self ? currentPlayer.md5 : selectedOpponent, mockSelectedCell));
            });

            it('fire on self=' + self, function () {
                scope.showingSelf = self;
                scope.fire();
                console.log(scope.showingSelf);
                assert(actionsSpy.fire.calledWith(expectedGame, self ? currentPlayer.md5 : selectedOpponent, mockSelectedCell));
            });

            it('move on self=' + self, function () {
                scope.showingSelf = self;
                scope.move();
                console.log(scope.showingSelf);
                assert(actionsSpy.move.calledWith(expectedGame, self ? currentPlayer.md5 : selectedOpponent, mockSelectedCell));
            });
        });
    });

    describe('enter view tests', function () {
        afterEach(function () {
            expect(ionicLoadingSpy.show.calledWithMatch({template: 'Loading..'}));
            expect(ionicLoadingSpy.show.callCount).to.equal(1);
            expect(ionicLoadingSpy.hide.callCount).to.equal(1);
            expect(ionicLoadingSpy.hide.calledWithMatch());
        });
        it('game is round over, player is winner', function () {
            expectedGame.gamePhase = 'RoundOver';
            expectedGame.winningPlayer = currentPlayer.md5;
            scope.showing = selectedOpponent;
            scope.showingSelf = false;
            rootScope.$broadcast('$ionicView.enter');
            timeout.flush();
            expect(ionicPopupSpy.alert.callCount).to.equal(1);
            expect(ionicPopupSpy.alert.calledWithMatch({
                title: 'Game is over!',
                template: 'Congratulations Winner!'
            }));
        });

        it('game is round over, player is not winner', function () {
            expectedGame.gamePhase = 'RoundOver';
            expectedGame.winningPlayer = currentPlayer.md5 + 'X';
            scope.showing = selectedOpponent;
            scope.showingSelf = false;
            rootScope.$broadcast('$ionicView.enter');
            timeout.flush();
            expect(ionicPopupSpy.alert.callCount).to.equal(1);
            expect(ionicPopupSpy.alert.calledWithMatch({
                title: 'Game is over!',
                template: 'Better luck next time...'
            }));
        });

        it('game is neither playing or round over', function () {
            expectedGame.gamePhase = 'Other';
            expectedGame.winningPlayer = currentPlayer.md5 + 'X';
            scope.showing = selectedOpponent;
            scope.showingSelf = false;
            rootScope.$broadcast('$ionicView.enter');
            timeout.flush();
        });

        it('game is playing, sets highlighting callback', function () {
            expectedGame.gamePhase = 'Playing';
            expectedGame.winningPlayer = currentPlayer.md5 + 'X';
            scope.showing = selectedOpponent;
            scope.showingSelf = false;
            rootScope.$broadcast('$ionicView.enter');
            timeout.flush();
            expect(mockShipService.highlightCB).to.not.be.undefined;
            mockSelectedShip = {hfh: '33', jhf: 33.3};
            mockShipService.highlightCB();
            timeout.flush();
            expect(scope.shipHighlighted).to.be.true;
            mockSelectedShip = null;
            mockShipService.highlightCB();
            timeout.flush();
            expect(scope.shipHighlighted).to.be.false;
        });
    });

    describe('testing game updates', function () {
        //  Minimal testing up changePlayer here - test elsewhere

        it('handles game update for different game', function () {
            rootScope.$broadcast('gameUpdated', {id: expectedId + 'X'}, {id: expectedId + 'X'});
            expect(ads.showInterstitial.callCount).to.equal(0);
            expect(actionsSpy.updateCurrentView.callCount).to.equal(0);
        });

        it('handles game update for game but not phase change, player was current player, player is current player', function () {
            var updatedGame = {id: expectedId, gamePhase: expectedPhase, currentPlayer: currentPlayer.md5};
            updatedGame.maskedPlayersState = expectedGame.maskedPlayersState;
            scope.showing = selectedOpponent;
            scope.showingSelf = false;
            rootScope.$broadcast('gameUpdated', expectedGame, updatedGame);
            expect(ads.showInterstitial.callCount).to.equal(0);
            expect(actionsSpy.updateCurrentView.callCount).to.equal(0);
            expect(mockShipService.placeShips.calledWithMatch([]));
            expect(mockShipService.placeCellMarkers.calledWithMatch(expectedGame.maskedPlayersState.opponentGrids.md3.table));
            expect(scope.game).to.equal(updatedGame);
        });

        it('handles game update for game but not phase change, player was not current player, player is current player', function () {
            var updatedGame = {id: expectedId, gamePhase: expectedPhase, currentPlayer: currentPlayer.md5};
            updatedGame.maskedPlayersState = expectedGame.maskedPlayersState;
            var oldGame = {id: expectedId, gamePhase: expectedPhase, currentPlayer: 'XYZ'};
            scope.showing = selectedOpponent;
            scope.showingSelf = false;
            rootScope.$broadcast('gameUpdated', oldGame, updatedGame);
            expect(ads.showInterstitial.callCount).to.equal(0);
            expect(actionsSpy.updateCurrentView.callCount).to.equal(0);
            expect(mockShipService.placeShips.calledWithMatch([]));
            expect(mockShipService.placeCellMarkers.calledWithMatch(expectedGame.maskedPlayersState.opponentGrids.md3.table));
            expect(scope.game).to.equal(updatedGame);
        });

        it('handles game update for game but not phase change, player was current player, player is not current player', function () {
            var updatedGame = {id: expectedId, gamePhase: expectedPhase, currentPlayer: 'X'};
            updatedGame.maskedPlayersState = expectedGame.maskedPlayersState;
            var oldGame = {id: expectedId, gamePhase: expectedPhase, currentPlayer: currentPlayer.md5};
            scope.showing = selectedOpponent;
            scope.showingSelf = false;
            rootScope.$broadcast('gameUpdated', oldGame, updatedGame);
            expect(ads.showInterstitial.callCount).to.equal(1);
            expect(actionsSpy.updateCurrentView.callCount).to.equal(0);
            expect(mockShipService.placeShips.calledWithMatch([]));
            expect(mockShipService.placeCellMarkers.calledWithMatch(expectedGame.maskedPlayersState.opponentGrids.md3.table));
            expect(scope.game).to.equal(updatedGame);
        });

        it('handles game update for game for phase change, player was current player, player is current player', function () {
            var updatedGame = {id: expectedId, gamePhase: expectedPhase + 'X', currentPlayer: currentPlayer.md5};
            updatedGame.maskedPlayersState = expectedGame.maskedPlayersState;
            var oldGame = {id: expectedId, gamePhase: expectedPhase, currentPlayer: currentPlayer.md5};
            scope.showing = selectedOpponent;
            scope.showingSelf = false;
            rootScope.$broadcast('gameUpdated', oldGame, updatedGame);
            expect(ads.showInterstitial.callCount).to.equal(0);
            expect(actionsSpy.updateCurrentView.callCount).to.equal(1);
            expect(mockShipService.placeShips.calledWithMatch([]));
            expect(mockShipService.placeCellMarkers.calledWithMatch(expectedGame.maskedPlayersState.opponentGrids.md3.table));
            expect(scope.game).to.equal(updatedGame);
        });

    });
    it('shuts down ship grid on view exit', function () {
        rootScope.$broadcast('$ionicView.leave');
        assert(mockShipService.stop.calledWithMatch());
    });
});
