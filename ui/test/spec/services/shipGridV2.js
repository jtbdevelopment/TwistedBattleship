'use strict';

describe('Service: gameDetails', function () {
    // load the controller's module
    beforeEach(module('tbs.services'));

    var expectedTheme = 'A THEME!';
    var currentPlayer = {source: 'MANUAL', gameSpecificPlayerAttributes: {theme: expectedTheme}};
    var expectedGame = {gridSize: 20};

    var mockPlayerService = {
        currentPlayer: function () {
            return currentPlayer;
        }
    };

    var expectedShips = [{'ship': 'Carrier', 'description': 'Aircraft Carrier', 'gridSize': 5}, {
        'ship': 'Battleship', 'description': 'Battleship', 'gridSize': 4
    }, {'ship': 'Cruiser', 'description': 'Cruiser', 'gridSize': 3}, {
        'ship': 'Submarine'
        , 'description': 'Submarine', 'gridSize': 3
    }, {'ship': 'Destroyer', 'description': 'Destroyer', 'gridSize': 2}
    ];

    var expectedStates = ['Unknown', 'ObscuredEmpty', 'ObscuredShip', 'ObscuredOtherHit', 'ObscuredOtherMiss', 'ObscuredRehit', 'ObscuredMiss'
        , 'ObscuredHit', 'RevealedShip', 'KnownEmpty', 'KnownShip', 'HiddenHit', 'KnownByOtherHit', 'KnownByOtherMiss'
        , 'KnownByRehit', 'KnownByMiss', 'KnownByHit'];

    var expectedCircleData = {
        '10': [{'row': 0, 'column': 0}, {'row': 0, 'column': -1}, {'row': 0, 'column': 1}, {'row': 1, 'column': 0}, {
            'row': -1
            , 'column': 0
        }],
        '15': [{'row': 0, 'column': -2}, {'row': 0, 'column': 2}, {'row': 2, 'column': 0}, {
            'row': -2, 'column': 0
        }, {'row': 1, 'column': 1}, {'row': 1, 'column': -1}, {'row': -1, 'column': -1}, {'row': -1, 'column': 1}],
        '20': [{
            'row': 2, 'column': 2
        }, {'row': 2, 'column': -2}, {'row': -2, 'column': -2}, {'row': -2, 'column': 2}, {
            'row': 2, 'column': 1
        }, {'row': 1, 'column': 2}, {'row': 2, 'column': -1}, {'row': -1, 'column': 2}, {'row': -2, 'column': -1}, {
            'row': -1, 'column': -2
        }, {'row': -2, 'column': 1}, {'row': 1, 'column': -2}]
    };
    var Phaser = {};
    var PhaserGame = {}, PhaserFactory = {
        newGame: sinon.stub()
    };

    var cellStatesDeferred, circlesDeferred, shipsDeferred;
    beforeEach(module(function ($provide) {
        $provide.factory('Phaser', function () {
            return Phaser;
        });

        $provide.factory('tbsPhaserGameFactory', function () {
            return PhaserFactory;
        });
        $provide.factory('tbsCellStates', ['$q', function ($q) {
            return {
                cellStates: function () {
                    cellStatesDeferred = $q.defer();
                    return cellStatesDeferred.promise;
                }
            };
        }]);

        $provide.factory('jtbPlayerService', function () {
            return mockPlayerService;
        });

        $provide.factory('tbsShips', ['$q', function ($q) {
            return {
                ships: function () {
                    shipsDeferred = $q.defer();
                    return shipsDeferred.promise;
                }
            };
        }]);

        $provide.factory('tbsCircles', ['$q', function ($q) {
            return {
                circles: function () {
                    circlesDeferred = $q.defer();
                    return circlesDeferred.promise;
                }
            };
        }]);
    }));

    var service, rootScope;
    var phaserCBs;
    beforeEach(inject(function ($injector, $rootScope) {
        PhaserFactory.newGame.reset();
        phaserCBs = undefined;
        rootScope = $rootScope;
        service = $injector.get('tbsShipGridV2');
    }));

    describe('test initializations with no ships or markers up front', function () {
        beforeEach(function () {
            service.initialize(expectedGame, [], [], undefined);
            shipsDeferred.resolve(expectedShips);
            rootScope.$apply();
            cellStatesDeferred.resolve(expectedStates);
            rootScope.$apply();
            circlesDeferred.resolve(expectedCircleData);
            PhaserFactory.newGame.withArgs(2000, 2000, 'phaser', sinon.match(function (arg4) {
                console.log('here');
                expect(arg4.create).to.be.defined;
                expect(arg4.update).to.be.defined;
                expect(arg4.init).to.be.defined;
                expect(arg4.preload).to.be.defined;
                phaserCBs = arg4;
                return true;
            }, 'fail')).returns(PhaserGame);
            rootScope.$apply();
        });

        it('test initialize gets data from servers and initializes a phaser game', function () {
            expect(phaserCBs).to.be.defined;
        });
    });

});