'use strict';

describe('Controller: GameCtrl', function () {
    // load the controller's module
    beforeEach(module('tbs.controllers'));

    var shipInfo = {
        Type4: {
            ship: 'Type4',
            gridSize: 1,
            description: 'Not used'
        },
        Type1: {
            ship: 'Type1',
            gridSize: 3,
            description: 'Type 1'
        },
        Type2: {
            ship: 'Type2',
            gridSize: 2,
            description: 'Type 2'
        },
        Type3: {
            ship: 'Type3',
            gridSize: 4,
            description: 'Not used'
        }
    };
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
        cellCB: undefined,
        enableCellSelecting: function (cb) {
            this.cellCB = cb;
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
    var rootScope, scope, ctrl, stateSpy, q, phasePromise, ionicLoadingSpy, ionicPopupSpy, timeout,
        ads, actionsSpy, expectedGame, expectedComputedShips;

    beforeEach(inject(function ($rootScope, $controller, $q, $timeout) {
        expectedGame = {
            id: expectedId,
            features: [],
            gamePhase: expectedPhase,
            currentPlayer: currentPlayer.md5,
            players: {
                md3: {},
                md1: {},
                md2: {}
            },
            maskedPlayersState: {
                shipStates: [
                    {
                        ship: 'Type1',
                        healthRemaining: 3,
                        shipGridCells: [{row: 0, column: 0}, {row: 0, column: 1}, {row: 0, column: 2}],
                        shipSegmentHit: [false, false, false]
                    },
                    {
                        ship: 'Type2',
                        healthRemaining: 2,
                        shipGridCells: [{row: 5, column: 5}, {row: 6, column: 5}],
                        shipSegmentHit: [false, false]
                    }
                ],
                consolidatedOpponentView: {
                    table: {a: 'X', b: 2}
                },
                opponentGrids: {
                    md3: {
                        table: {x: 1, y: 2, s: 'X'}
                    }
                },
                opponentViews: {
                    md3: {
                        table: {x: 5, y: 1, s: 'XXX'}
                    }
                }
            }
        };

        expectedComputedShips = [
            {
                horizontal: true,
                row: 0,
                column: 0,
                shipInfo: shipInfo.Type1
            },
            {
                horizontal: false,
                row: 5,
                column: 5,
                shipInfo: shipInfo.Type2
            }
        ];

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
        mockSelectedCell = undefined;
        mockSelectedShip = undefined;
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

        ctrl = $controller('GameV2Ctrl', {
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
            tbsShipGridV2: mockShipService
        });
    }));

    it('initializes', function () {
        expect(scope.gameID).to.equal(expectedId);
        expect(scope.game).to.equal(expectedGame);
        expect(scope.gameDetails).to.equal(mockGameDetails);
        expect(scope.playerKeys).to.deep.equal(['md3', 'md1', 'md2']);
        expect(scope.player).to.equal(currentPlayer);
        expect(scope.showing).to.equal('ALL');
        expect(scope.showingSelf).to.be.false;
        expect(scope.shipHighlighted).to.be.false;
        expect(scope.selectedCell).to.be.undefined;
    });

    describe('tests that involve changing display', function () {
        it('switching to ALL from showing opponent', function () {
            scope.showingSelf = false;
            scope.showing = 'X';

            scope.changePlayer('ALL');
            assert(mockShipService.placeShips.calledWithMatch(expectedGame.maskedPlayersState.shipStates));
            assert(mockShipService.placeCellMarkers.calledWithMatch(expectedGame.maskedPlayersState.consolidatedOpponentView.table));
            expect(scope.showing).to.equal('ALL');
            expect(scope.showingSelf).to.be.true;
        });

        it('switching from ALL to showing md3, self', function () {
            scope.showingSelf = true;
            scope.showing = 'ALL';

            scope.changePlayer('md3');
            assert(mockShipService.placeShips.calledWithMatch(expectedGame.maskedPlayersState.shipStates));
            assert(mockShipService.placeCellMarkers.calledWithMatch(expectedGame.maskedPlayersState.opponentViews.md3.table));
            expect(scope.showing).to.equal('md3');
            expect(scope.showingSelf).to.be.true;
        });

        it('switching from another opponent to showing md3, them', function () {
            scope.showingSelf = false;
            scope.showing = 'md2';

            scope.changePlayer('md3');
            assert(mockShipService.placeShips.calledWithMatch([]));
            assert(mockShipService.placeCellMarkers.calledWithMatch(expectedGame.maskedPlayersState.opponentGrids.md3.table));
            expect(scope.showing).to.equal('md3');
            expect(scope.showingSelf).to.be.false;
        });

        it('switching from md3/them to md3/self', function () {
            scope.showingSelf = false;
            scope.showing = 'md3';

            scope.switchView(true);
            assert(mockShipService.placeShips.calledWithMatch(expectedGame.maskedPlayersState.shipStates));
            assert(mockShipService.placeCellMarkers.calledWithMatch(expectedGame.maskedPlayersState.opponentViews.md3.table));
            expect(scope.showing).to.equal('md3');
            expect(scope.showingSelf).to.be.true;
        });

        it('switching from ALL/self to not self, self is not first player', function () {
            scope.showingSelf = true;
            scope.showing = 'ALL';

            scope.switchView(false);
            assert(mockShipService.placeShips.calledWithMatch([]));
            assert(mockShipService.placeCellMarkers.calledWithMatch(expectedGame.maskedPlayersState.opponentGrids.md3.table));
            expect(scope.showing).to.equal('md3');
            expect(scope.showingSelf).to.be.false;
        });

        it('switching from ALL/self to not self, self is first player', function () {
            scope.showingSelf = true;
            scope.showing = 'ALL';
            scope.playerKeys = [currentPlayer.md5, 'md3', 'md2', 'md1'];

            scope.switchView(false);
            assert(mockShipService.placeShips.calledWithMatch([]));
            assert(mockShipService.placeCellMarkers.calledWithMatch(expectedGame.maskedPlayersState.opponentGrids.md3.table));
            expect(scope.showing).to.equal('md3');
            expect(scope.showingSelf).to.be.false;
        });
    });

    describe('general navigation and simple actions', function () {
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
    });

    describe('tests with cell selected', function () {
        angular.forEach([true, false], function (self) {
            beforeEach(function () {
                mockSelectedCell = {column: 1, row: 2};
                if (self) {
                    scope.showing = selectedOpponent;
                }
                expectedGame.gamePhase = 'Playing';
                rootScope.$broadcast('$ionicView.enter');
                timeout.flush();
                expect(mockShipService.cellCB).to.not.be.undefined;
                mockShipService.cellCB(mockSelectedCell, mockSelectedShip);
                timeout.flush();
            });

            it('ecm on self=' + self, function () {
                scope.showingSelf = self;
                scope.ecm();
                assert(actionsSpy.ecm.calledWith(expectedGame, self ? currentPlayer.md5 : selectedOpponent, mockSelectedCell));
            });

            it('repair on self=' + self, function () {
                scope.showingSelf = self;
                scope.repair();
                assert(actionsSpy.repair.calledWith(expectedGame, self ? currentPlayer.md5 : selectedOpponent, mockSelectedCell));
            });

            it('spy on self=' + self, function () {
                scope.showingSelf = self;
                scope.spy();
                assert(actionsSpy.spy.calledWith(expectedGame, self ? currentPlayer.md5 : selectedOpponent, mockSelectedCell));
            });

            it('missile on self=' + self, function () {
                scope.showingSelf = self;
                scope.missile();
                assert(actionsSpy.missile.calledWith(expectedGame, self ? currentPlayer.md5 : selectedOpponent, mockSelectedCell));
            });

            it('fire on self=' + self, function () {
                scope.showingSelf = self;
                scope.fire();
                assert(actionsSpy.fire.calledWith(expectedGame, self ? currentPlayer.md5 : selectedOpponent, mockSelectedCell));
            });

            it('move on self=' + self, function () {
                scope.showingSelf = self;
                scope.move();
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
            expect(mockShipService.cellCB).to.not.be.undefined;
            //noinspection JSUnusedAssignment
            mockSelectedShip = {hfh: '33', jhf: 33.3};
            mockSelectedCell = {x: '1'};
            mockShipService.cellCB(mockSelectedCell, mockSelectedShip);
            timeout.flush();
            expect(scope.shipHighlighted).to.be.true;
            expect(scope.selectedCell).to.equal(mockSelectedCell);
            mockSelectedShip = undefined;
            mockSelectedCell = undefined;
            mockShipService.cellCB(mockSelectedCell, mockSelectedShip);
            timeout.flush();
            expect(scope.selectedCell).to.be.undefined;
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
