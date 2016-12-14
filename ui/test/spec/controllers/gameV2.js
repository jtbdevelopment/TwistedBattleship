'use strict';

describe('Controller: GameCtrl', function () {
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
    var $rootScope, $scope, ctrl, stateSpy, $q, phasePromise, ionicLoadingSpy, ionicPopupSpy, $timeout,
        ads, actionsSpy, expectedGame, expectedComputedShips;

    beforeEach(inject(function (_$rootScope_, $controller, _$q_, _$timeout_) {
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
        $rootScope = _$rootScope_;
        $q = _$q_;
        $timeout = _$timeout_;
        phasePromise = _$q_.defer();
        $scope = $rootScope.$new();
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
            $scope: $scope,
            $state: stateSpy,
            tbsActions: actionsSpy,
            jtbGameCache: mockGameCache,
            $ionicLoading: ionicLoadingSpy,
            $ionicPopup: ionicPopupSpy,
            jtbPlayerService: mockPlayerService,
            jtbIonicAds: ads,
            tbsShipGridV2: mockShipService
        });
    }));

    it('initializes', function () {
        expect(ctrl.gameID).to.equal(expectedId);
        expect(ctrl.game).to.equal(expectedGame);
        expect(ctrl.playerKeys).to.deep.equal(['md3', 'md1', 'md2']);
        expect(ctrl.player).to.equal(currentPlayer);
        expect(ctrl.showing).to.equal('ALL');
        expect(ctrl.showingSelf).to.be.false;
        expect(ctrl.shipHighlighted).to.be.false;
        expect(ctrl.selectedCell).to.be.undefined;
    });

    describe('tests that involve changing display', function () {
        it('switching to ALL from showing opponent', function () {
            ctrl.showingSelf = false;
            ctrl.showing = 'X';

            ctrl.changePlayer('ALL');
            assert(mockShipService.placeShips.calledWithMatch(expectedGame.maskedPlayersState.shipStates));
            assert(mockShipService.placeCellMarkers.calledWithMatch(expectedGame.maskedPlayersState.consolidatedOpponentView.table));
            expect(ctrl.showing).to.equal('ALL');
            expect(ctrl.showingSelf).to.be.true;
        });

        it('switching from ALL to showing md3, self', function () {
            ctrl.showingSelf = true;
            ctrl.showing = 'ALL';

            ctrl.changePlayer('md3');
            assert(mockShipService.placeShips.calledWithMatch(expectedGame.maskedPlayersState.shipStates));
            assert(mockShipService.placeCellMarkers.calledWithMatch(expectedGame.maskedPlayersState.opponentViews.md3.table));
            expect(ctrl.showing).to.equal('md3');
            expect(ctrl.showingSelf).to.be.true;
        });

        it('switching from another opponent to showing md3, them', function () {
            ctrl.showingSelf = false;
            ctrl.showing = 'md2';

            ctrl.changePlayer('md3');
            assert(mockShipService.placeShips.calledWithMatch([]));
            assert(mockShipService.placeCellMarkers.calledWithMatch(expectedGame.maskedPlayersState.opponentGrids.md3.table));
            expect(ctrl.showing).to.equal('md3');
            expect(ctrl.showingSelf).to.be.false;
        });

        it('switching from md3/them to md3/self', function () {
            ctrl.showingSelf = false;
            ctrl.showing = 'md3';

            ctrl.switchView(true);
            assert(mockShipService.placeShips.calledWithMatch(expectedGame.maskedPlayersState.shipStates));
            assert(mockShipService.placeCellMarkers.calledWithMatch(expectedGame.maskedPlayersState.opponentViews.md3.table));
            expect(ctrl.showing).to.equal('md3');
            expect(ctrl.showingSelf).to.be.true;
        });

        it('switching from ALL/self to not self, self is not first player', function () {
            ctrl.showingSelf = true;
            ctrl.showing = 'ALL';

            ctrl.switchView(false);
            assert(mockShipService.placeShips.calledWithMatch([]));
            assert(mockShipService.placeCellMarkers.calledWithMatch(expectedGame.maskedPlayersState.opponentGrids.md3.table));
            expect(ctrl.showing).to.equal('md3');
            expect(ctrl.showingSelf).to.be.false;
        });

        it('switching from ALL/self to not self, self is first player', function () {
            ctrl.showingSelf = true;
            ctrl.showing = 'ALL';
            ctrl.playerKeys = [currentPlayer.md5, 'md3', 'md2', 'md1'];

            ctrl.switchView(false);
            assert(mockShipService.placeShips.calledWithMatch([]));
            assert(mockShipService.placeCellMarkers.calledWithMatch(expectedGame.maskedPlayersState.opponentGrids.md3.table));
            expect(ctrl.showing).to.equal('md3');
            expect(ctrl.showingSelf).to.be.false;
        });
    });

    describe('general navigation and simple actions', function () {
        it('shows action log', function () {
            ctrl.showActionLog();
            assert(stateSpy.go.calledWithMatch('app.actionLog', {gameID: expectedId}));
        });

        it('shows game details', function () {
            ctrl.showDetails();
            assert(stateSpy.go.calledWithMatch('app.gameDetails', {gameID: expectedId}));
        });

        it('shows help', function () {
            ctrl.showHelp();
            assert(stateSpy.go.calledWithMatch('app.playhelp'));
        });

        it('declines rematch', function () {
            ctrl.declineRematch();
            assert(actionsSpy.declineRematch.calledWithMatch(expectedGame));
        });

        it('start rematch', function () {
            ctrl.rematch();
            assert(actionsSpy.rematch.calledWithMatch(expectedGame));
        });


        it('quit game', function () {
            ctrl.quit();
            assert(actionsSpy.quit.calledWithMatch(expectedGame));
        });
    });

    describe('tests with cell selected', function () {
        angular.forEach([true, false], function (self) {
            beforeEach(function () {
                mockSelectedCell = {column: 1, row: 2};
                if (self) {
                    ctrl.showing = selectedOpponent;
                }
                expectedGame.gamePhase = 'Playing';
                $rootScope.$broadcast('$ionicView.enter');
                $timeout.flush();
                expect(mockShipService.cellCB).to.not.be.undefined;
                mockShipService.cellCB(mockSelectedCell, mockSelectedShip);
                $timeout.flush();
            });

            it('ecm on self=' + self, function () {
                ctrl.showingSelf = self;
                ctrl.ecm();
                assert(actionsSpy.ecm.calledWith(expectedGame, self ? currentPlayer.md5 : selectedOpponent, mockSelectedCell));
            });

            it('repair on self=' + self, function () {
                ctrl.showingSelf = self;
                ctrl.repair();
                assert(actionsSpy.repair.calledWith(expectedGame, self ? currentPlayer.md5 : selectedOpponent, mockSelectedCell));
            });

            it('spy on self=' + self, function () {
                ctrl.showingSelf = self;
                ctrl.spy();
                assert(actionsSpy.spy.calledWith(expectedGame, self ? currentPlayer.md5 : selectedOpponent, mockSelectedCell));
            });

            it('missile on self=' + self, function () {
                ctrl.showingSelf = self;
                ctrl.missile();
                assert(actionsSpy.missile.calledWith(expectedGame, self ? currentPlayer.md5 : selectedOpponent, mockSelectedCell));
            });

            it('fire on self=' + self, function () {
                ctrl.showingSelf = self;
                ctrl.fire();
                assert(actionsSpy.fire.calledWith(expectedGame, self ? currentPlayer.md5 : selectedOpponent, mockSelectedCell));
            });

            it('move on self=' + self, function () {
                ctrl.showingSelf = self;
                ctrl.move();
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
            ctrl.showing = selectedOpponent;
            ctrl.showingSelf = false;
            $rootScope.$broadcast('$ionicView.enter');
            $timeout.flush();
            expect(ionicPopupSpy.alert.callCount).to.equal(1);
            expect(ionicPopupSpy.alert.calledWithMatch({
                title: 'Game is over!',
                template: 'Congratulations Winner!'
            }));
        });

        it('game is round over, player is not winner', function () {
            expectedGame.gamePhase = 'RoundOver';
            expectedGame.winningPlayer = currentPlayer.md5 + 'X';
            ctrl.showing = selectedOpponent;
            ctrl.showingSelf = false;
            $rootScope.$broadcast('$ionicView.enter');
            $timeout.flush();
            expect(ionicPopupSpy.alert.callCount).to.equal(1);
            expect(ionicPopupSpy.alert.calledWithMatch({
                title: 'Game is over!',
                template: 'Better luck next time...'
            }));
        });

        it('game is neither playing or round over', function () {
            expectedGame.gamePhase = 'Other';
            expectedGame.winningPlayer = currentPlayer.md5 + 'X';
            ctrl.showing = selectedOpponent;
            ctrl.showingSelf = false;
            $rootScope.$broadcast('$ionicView.enter');
            $timeout.flush();
        });

        it('game is playing, sets highlighting callback', function () {
            expectedGame.gamePhase = 'Playing';
            expectedGame.winningPlayer = currentPlayer.md5 + 'X';
            ctrl.showing = selectedOpponent;
            ctrl.showingSelf = false;
            $rootScope.$broadcast('$ionicView.enter');
            $timeout.flush();
            expect(mockShipService.cellCB).to.not.be.undefined;
            //noinspection JSUnusedAssignment
            mockSelectedShip = {hfh: '33', jhf: 33.3};
            mockSelectedCell = {x: '1'};
            mockShipService.cellCB(mockSelectedCell, mockSelectedShip);
            $timeout.flush();
            expect(ctrl.shipHighlighted).to.be.true;
            expect(ctrl.selectedCell).to.equal(mockSelectedCell);
            mockSelectedShip = undefined;
            mockSelectedCell = undefined;
            mockShipService.cellCB(mockSelectedCell, mockSelectedShip);
            $timeout.flush();
            expect(ctrl.selectedCell).to.be.undefined;
            expect(ctrl.shipHighlighted).to.be.false;
        });
    });

    describe('testing game updates', function () {
        //  Minimal testing up changePlayer here - test elsewhere

        it('handles game update for different game', function () {
            $rootScope.$broadcast('gameUpdated', {id: expectedId + 'X'}, {id: expectedId + 'X'});
            expect(ads.showInterstitial.callCount).to.equal(0);
            expect(actionsSpy.updateCurrentView.callCount).to.equal(0);
        });

        it('handles game update for game but not phase change, player was current player, player is current player', function () {
            var updatedGame = {id: expectedId, gamePhase: expectedPhase, currentPlayer: currentPlayer.md5};
            updatedGame.maskedPlayersState = expectedGame.maskedPlayersState;
            ctrl.showing = selectedOpponent;
            ctrl.showingSelf = false;
            $rootScope.$broadcast('gameUpdated', expectedGame, updatedGame);
            expect(ads.showInterstitial.callCount).to.equal(0);
            expect(actionsSpy.updateCurrentView.callCount).to.equal(0);
            expect(mockShipService.placeShips.calledWithMatch([]));
            expect(mockShipService.placeCellMarkers.calledWithMatch(expectedGame.maskedPlayersState.opponentGrids.md3.table));
            expect(ctrl.game).to.equal(updatedGame);
        });

        it('handles game update for game but not phase change, player was not current player, player is current player', function () {
            var updatedGame = {id: expectedId, gamePhase: expectedPhase, currentPlayer: currentPlayer.md5};
            updatedGame.maskedPlayersState = expectedGame.maskedPlayersState;
            var oldGame = {id: expectedId, gamePhase: expectedPhase, currentPlayer: 'XYZ'};
            ctrl.showing = selectedOpponent;
            ctrl.showingSelf = false;
            $rootScope.$broadcast('gameUpdated', oldGame, updatedGame);
            expect(ads.showInterstitial.callCount).to.equal(0);
            expect(actionsSpy.updateCurrentView.callCount).to.equal(0);
            expect(mockShipService.placeShips.calledWithMatch([]));
            expect(mockShipService.placeCellMarkers.calledWithMatch(expectedGame.maskedPlayersState.opponentGrids.md3.table));
            expect(ctrl.game).to.equal(updatedGame);
        });

        it('handles game update for game but not phase change, player was current player, player is not current player', function () {
            var updatedGame = {id: expectedId, gamePhase: expectedPhase, currentPlayer: 'X'};
            updatedGame.maskedPlayersState = expectedGame.maskedPlayersState;
            var oldGame = {id: expectedId, gamePhase: expectedPhase, currentPlayer: currentPlayer.md5};
            ctrl.showing = selectedOpponent;
            ctrl.showingSelf = false;
            $rootScope.$broadcast('gameUpdated', oldGame, updatedGame);
            expect(ads.showInterstitial.callCount).to.equal(1);
            expect(actionsSpy.updateCurrentView.callCount).to.equal(0);
            expect(mockShipService.placeShips.calledWithMatch([]));
            expect(mockShipService.placeCellMarkers.calledWithMatch(expectedGame.maskedPlayersState.opponentGrids.md3.table));
            expect(ctrl.game).to.equal(updatedGame);
        });

        it('handles game update for game for phase change, player was current player, player is current player', function () {
            var updatedGame = {id: expectedId, gamePhase: expectedPhase + 'X', currentPlayer: currentPlayer.md5};
            updatedGame.maskedPlayersState = expectedGame.maskedPlayersState;
            var oldGame = {id: expectedId, gamePhase: expectedPhase, currentPlayer: currentPlayer.md5};
            ctrl.showing = selectedOpponent;
            ctrl.showingSelf = false;
            $rootScope.$broadcast('gameUpdated', oldGame, updatedGame);
            expect(ads.showInterstitial.callCount).to.equal(0);
            expect(actionsSpy.updateCurrentView.callCount).to.equal(1);
            expect(mockShipService.placeShips.calledWithMatch([]));
            expect(mockShipService.placeCellMarkers.calledWithMatch(expectedGame.maskedPlayersState.opponentGrids.md3.table));
            expect(ctrl.game).to.equal(updatedGame);
        });

    });

    it('shuts down ship grid on view exit', function () {
        $rootScope.$broadcast('$ionicView.leave');
        assert(mockShipService.stop.calledWithMatch());
    });
});
