'use strict';

//  TODO - avoiding testing a lot of the phaser/grid stuff as plan to revamp it to use more phaser native
//  and remove so much self work
describe('Controller: SetupGameV2Ctrl', function () {
    // load the controller's module
    beforeEach(module('tbs.controllers'));

    var expectedId = 'tada!';
    var expectedPhase = 'APhase';
    var selectedOpponent = 'md3';
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

    var shipsOnGrid;
    var mockShipService = {
        overlapCB: undefined,
        currentShipsOnGrid: function () {
            return shipsOnGrid;
        },
        initialize: function (game, ships, markers, cb) {
            expect(game).to.equal(expectedGame);
            expect(ships).to.deep.equal(game.maskedPlayersState.shipStates);
            expect(ships).to.not.equal(game.maskedPlayersState.shipStates);
            angular.forEach(ships, function (ship, index) {
                expect(ship).to.deep.equal(game.maskedPlayersState.shipStates[index]);
                expect(ship).to.not.equal(game.maskedPlayersState.shipStates[index]);
            });
            expect([]).to.deep.equal(markers);
            cb();
        },
        enableShipMovement: function (cb) {
            expect(cb).to.be.defined;
            this.overlapCB = cb;
        }
    };
    var rootScope, scope, ctrl, stateSpy, q, phasePromise, ionicLoadingSpy, timeout, ionicModal,
        actionsSpy, expectedGame, ionicSideMenuDelegate;
    var modalHelpPromise, modalHelp;

    beforeEach(inject(function ($rootScope, $controller, $q, $timeout) {
        rootScope = $rootScope;
        q = $q;
        timeout = $timeout;
        scope = rootScope.$new();
        modalHelpPromise = $q.defer();
        modalHelp = {show: sinon.spy(), hide: sinon.spy(), remove: sinon.spy()};
        ionicModal = {fromTemplateUrl: sinon.stub()};
        ionicModal.fromTemplateUrl.withArgs('templates/help/help-setup.html', {
            scope: scope,
            animation: 'slide-in-up'
        }).returns(modalHelpPromise.promise);
        ionicSideMenuDelegate = {canDragContent: sinon.spy()};
        shipsOnGrid = [];
        expectedGame = {
            id: expectedId,
            features: [],
            gamePhase: expectedPhase,
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

        expectedGame.maskedPlayersState.opponentGrids[selectedOpponent] = {table: {x: 1, y: 2, s: 'X'}};
        stateSpy = {go: sinon.spy(), params: {gameID: expectedId}};
        ionicLoadingSpy = {show: sinon.spy(), hide: sinon.spy()};
        phasePromise = $q.defer();
        mockShipService.placeShips = sinon.spy();
        mockShipService.placeCellMarkers = sinon.spy();
        mockShipService.stop = sinon.spy();
        actionsSpy = {
            quit: sinon.spy(),
            setup: sinon.spy(),
            submit: sinon.spy(),
            updateCurrentView: sinon.spy()
        };

        ctrl = $controller('SetupGameV2Ctrl', {
            $scope: scope,
            $state: stateSpy,
            tbsActions: actionsSpy,
            tbsGameDetails: mockGameDetails,
            jtbGameCache: mockGameCache,
            $timeout: timeout,
            $ionicLoading: ionicLoadingSpy,
            $ionicModal: ionicModal,
            $ionicSideMenuDelegate: ionicSideMenuDelegate,
            tbsShipGridV2: mockShipService
        });
    }));

    it('initializes', function () {
        expect(scope.gameID).to.equal(expectedId);
        expect(scope.game).to.equal(expectedGame);
        expect(scope.gameDetails).to.equal(mockGameDetails);
        assert(ionicSideMenuDelegate.canDragContent.calledWithMatch(false));
        expect(scope.movingShip).to.equal(null);
        expect(scope.submitDisabled).to.equal(true);
        expect(scope.movingPointerRelativeToShip).to.deep.equal({x: 0, y: 0});
        expect(scope.helpModal).to.be.undefined;
        modalHelpPromise.resolve(modalHelp);
        rootScope.$apply();
        expect(scope.helpModal).to.equal(modalHelp);
    });

    it('on enter, enables ship movement and registers callback', function () {
        rootScope.$broadcast('$ionicView.enter');
        timeout.flush();
        expect(mockShipService.overlapCB).to.be.defined;
    });

    it('submit disabled driven by enable ship movement callback', function () {
        rootScope.$broadcast('$ionicView.enter');
        timeout.flush();
        mockShipService.overlapCB(false);
        timeout.flush();
        expect(scope.submitDisabled).to.be.false;
        mockShipService.overlapCB(true);
        timeout.flush();
        expect(scope.submitDisabled).to.be.true;
    });

    describe('tests involving help', function () {
        beforeEach(function () {
            modalHelpPromise.resolve(modalHelp);
            rootScope.$apply();
        });

        it('shows help', function () {
            scope.showHelp();
            expect(modalHelp.show.calledWithMatch());
        });

        it('close help', function () {
            scope.closeHelp();
            expect(modalHelp.hide.calledWithMatch());
        });

        it('removes help on $destroy', function () {
            rootScope.$broadcast('$destroy');
            expect(modalHelp.remove.calledWithMatch());
        });
    });

    it('navigate to game details', function () {
        scope.showDetails();
        assert(stateSpy.go.calledWithMatch('app.gameDetails', {gameID: expectedId}));
    });

    it('quit game', function () {
        scope.quit();
        assert(actionsSpy.quit.calledWithMatch(expectedGame));
    });

    it('shuts down ship grid on view exit', function () {
        rootScope.$broadcast('$ionicView.leave');
        assert(mockShipService.stop.calledWithMatch());
    });

    it('submit sends in ships from grid', function () {
        shipsOnGrid.push({ship: 'ship1', shipGridCells: [{}, {field: 'value'}], other: 'value'});
        shipsOnGrid.push({ship: 'ship2', shipGridCells: [{a: 1}, {b: 'value'}], other: 'v'});
        shipsOnGrid.push({ship: 'ship3', shipGridCells: [{a: 2}, {b: 3}]});
        var expectedPayload = [];
        angular.forEach(shipsOnGrid, function (shipOnGrid) {
            expectedPayload.push({ship: shipOnGrid.ship, coordinates: shipOnGrid.shipGridCells});
        });
        scope.submit();
        assert(actionsSpy.setup.calledWithMatch(expectedGame, expectedPayload));
    });

    describe('testing game updates', function () {
        it('handles game update for different game', function () {
            rootScope.$broadcast('gameUpdated', {id: expectedId + 'X'}, {id: expectedId + 'X'});
            expect(actionsSpy.updateCurrentView.callCount).to.equal(0);
        });

        it('handles game update for game', function () {
            var updatedGame = {id: expectedId};
            rootScope.$broadcast('gameUpdated', expectedGame, updatedGame);
            expect(actionsSpy.updateCurrentView.callCount).to.equal(1);
            assert(actionsSpy.updateCurrentView.calledWithMatch(expectedGame, updatedGame));
            expect(scope.game).to.equal(updatedGame);
        });
    });
});
