'use strict';

describe('Controller: SetupGameCtrl', function () {
    // load the controller's module
    beforeEach(module('tbs.controllers'));

//    [ , '$ionicSideMenuDelegate',
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

    var mockSelectedCell, mockSelectedShip;
    var mockShipService = {
        onDownCB: undefined,
        onMoveCB: undefined,
        onTapCB: undefined,
        onUpCB: undefined,
        onDown: function (cb) {
            this.onDownCB = cb;
        },
        onMove: function (cb) {
            this.onMoveCB = cb;
        },
        onTap: function (cb) {
            this.onTapCB = cb;
        },
        onUp: function (cb) {
            this.onUpCB = cb;
        },
        initialize: function (game, ships, markers, cb) {
            expect(game).to.equal(expectedGame);
            expect([]).to.deep.equal(ships);
            expect([]).to.deep.equal(markers);
            cb();
        }
    };
    var rootScope, scope, ctrl, stateSpy, q, phasePromise, ionicLoadingSpy, timeout, ionicModal,
        actionsSpy, expectedGame, expectedComputedShips, ionicSideMenuDelegate;
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
        phasePromise = $q.defer();
        mockSelectedCell = null;
        mockSelectedShip = null;
        mockShipService.placeShips = sinon.spy();
        mockShipService.placeCellMarkers = sinon.spy();
        mockShipService.stop = sinon.spy();
        actionsSpy = {
            quit: sinon.spy(),
            setup: sinon.spy()
        };

        ctrl = $controller('SetupGameCtrl', {
            $scope: scope,
            $state: stateSpy,
            shipInfo: shipInfo,
            tbsActions: actionsSpy,
            tbsGameDetails: mockGameDetails,
            jtbGameCache: mockGameCache,
            $timeout: timeout,
            $ionicLoading: ionicLoadingSpy,
            $ionicModal: ionicModal,
            $ionicSideMenuDelegate: ionicSideMenuDelegate,
            tbsShipGrid: mockShipService
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

    it('shuts down ship grid on view exit', function () {
        rootScope.$broadcast('$ionicView.leave');
        assert(mockShipService.stop.calledWithMatch());
    });

});
