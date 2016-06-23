'use strict';

describe('Service: gameDetails', function () {
    // load the controller's module
    beforeEach(module('tbs.services'));

    var expectedTheme = 'mytheme';
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
    var Phaser = {
        Rectangle: {
            intersects: sinon.stub()
        },
        Tilemap: {
            TILED_JSON: 'tiled-json'
        },
        ScaleManager: {
            SHOW_ALL: 'show-all'
        },
        Physics: {
            ARCADE: 'arcade!'
        }
    };
    var PhaserGame = {
        destroy: sinon.stub(),
        load: {
            tilemap: sinon.spy(),
            image: sinon.spy()
        },
        add: {
            tilemap: sinon.stub(),
            image: sinon.stub(),
            sprite: sinon.stub()
        },
        physics: {
            startSystem: sinon.spy(),
            arcade: {
                enable: sinon.spy()
            }
        },
        scale: {}
    }, PhaserFactory = {
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
        Phaser.Rectangle.intersects.reset();
        PhaserFactory.newGame.reset();
        PhaserGame.destroy.reset();
        PhaserGame.add.image.reset();
        PhaserGame.add.tilemap.reset();
        PhaserGame.load.image.reset();
        PhaserGame.load.tilemap.reset();
        PhaserGame.physics.startSystem.reset();
        PhaserGame.physics.arcade.enable.reset();
        PhaserGame.scale = {
            scaleMode: undefined,
            pageAlignHorizontally: undefined,
            pageAlignVertically: undefined,
            windowConstraints: {
                bottom: undefined
            }
        };
        phaserCBs = undefined;
        rootScope = $rootScope;
        service = $injector.get('tbsShipGridV2');
    }));

    function makeShipSprite() {
        return {
            destroy: sinon.spy(),
            body: {
                collideWorldBounds: false,
                debug: false,
                width: 200,
                height: 195
            },
            anchor: {
                setTo: sinon.spy()
            },
            width: 200,
            height: 195,
            angle: undefined,
            x: undefined,
            y: undefined
        };
    }

    describe('test initializations with no ships or markers up front', function () {
        var postCreateCB;
        beforeEach(function () {
            postCreateCB = sinon.spy();
            service.initialize(expectedGame, [], [], postCreateCB);
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

        it('stopping game', function () {
            service.stop();
            assert(PhaserGame.destroy.calledWithMatch())
        });

        it('preload phaser sets up phaser', function () {
            phaserCBs.preload();
            assert(PhaserGame.load.tilemap.calledWithMatch('grid', 'templates/gamefiles/20x20.json', null, Phaser.Tilemap.TILED_JSON));
            assert(PhaserGame.load.image.calledWithMatch('tile', 'images/' + expectedTheme + '/tile.png'));
            assert(PhaserGame.load.image.calledWithMatch('highlight', 'images/' + expectedTheme + '/highlight.png'));
            assert(PhaserGame.load.image.calledWithMatch('extendedhighlight', 'images/' + expectedTheme + '/extendedhighlight.png'));
            angular.forEach(expectedShips, function (ship) {
                assert(PhaserGame.load.image.calledWithMatch(ship.ship, 'images/' + expectedTheme + '/' + angular.lowercase(ship.ship) + '.png'));
            });
            angular.forEach(expectedStates, function (state) {
                var lower = angular.lowercase(state);
                assert(PhaserGame.load.image.calledWithMatch(lower, 'images/' + expectedTheme + '/' + lower + '.png'));
            })
        });

        it('init sets up scale', function () {
            phaserCBs.init();
            expect(PhaserGame.scale.scaleMode).to.equal(Phaser.ScaleManager.SHOW_ALL);
        });

        it('create setups tilemap', function () {
            var tilemap = {
                createLayer: sinon.spy(),
                addTilesetImage: sinon.spy()
            };
            PhaserGame.add.tilemap.withArgs('grid').returns(tilemap);
            phaserCBs.create();
            assert(tilemap.addTilesetImage.calledWithMatch('tile'));
            assert(tilemap.createLayer.calledWithMatch('base grid'));
            expect(PhaserGame.scale.pageAlignHorizontally).to.be.false;
            expect(PhaserGame.scale.pageAlignVertically).to.be.false;
            expect(PhaserGame.scale.windowConstraints.bottom).to.equal('visual');
            assert(PhaserGame.physics.startSystem.calledWithMatch(Phaser.Physics.ARCADE));
            assert(postCreateCB.calledWithMatch());
        });

        it('update in this condition pretty much does nothing', function () {
            phaserCBs.update();
        });

        it('place some ships onto blank grid', function () {
            var shipStates = [
                {
                    ship: 'Carrier',
                    horizontal: true,
                    shipGridCells: [{row: 0, column: 0}]
                },
                {
                    ship: 'Destroyer',
                    horizontal: false,
                    shipGridCells: [{row: 5, column: 6}]
                }
            ];
            var carrierSprite = makeShipSprite();
            var destroyerSprite = makeShipSprite();
            PhaserGame.add.sprite.withArgs(0, 0, 'Carrier', 0).returns(carrierSprite);
            PhaserGame.add.sprite.withArgs(0, 0, 'Destroyer', 0).returns(destroyerSprite);
            service.placeShips(shipStates);
            expect(PhaserGame.physics.arcade.enable.calledWithMatch(carrierSprite));
            expect(PhaserGame.physics.arcade.enable.calledWithMatch(destroyerSprite));
            expect(carrierSprite.body.debug).to.be.true;
            expect(carrierSprite.body.collideWorldBounds).to.be.true;
            expect(carrierSprite.height).to.equal(193);
            expect(carrierSprite.width).to.equal(198);
            expect(carrierSprite.angle).to.equal(0);
            expect(carrierSprite.body.height).to.equal(195);
            expect(carrierSprite.body.width).to.equal(200);
            assert(carrierSprite.anchor.setTo.calledWithMatch(0.5, 0.5));
            expect(carrierSprite.x).to.equal(250);
            expect(carrierSprite.y).to.equal(50);

            expect(destroyerSprite.body.debug).to.be.true;
            expect(destroyerSprite.body.collideWorldBounds).to.be.true;
            expect(destroyerSprite.height).to.equal(193);
            expect(destroyerSprite.width).to.equal(198);
            expect(destroyerSprite.angle).to.equal(90);
            expect(destroyerSprite.body.height).to.equal(200);
            expect(destroyerSprite.body.width).to.equal(195);
            assert(destroyerSprite.anchor.setTo.calledWithMatch(0.5, 0.5));
            expect(destroyerSprite.x).to.equal(650);
            expect(destroyerSprite.y).to.equal(600);

            var submarineSprite = makeShipSprite();
            var newDestroyer = makeShipSprite();
            shipStates = [
                {
                    ship: 'Destroyer',
                    horizontal: false,
                    shipGridCells: [{row: 5, column: 6}]
                },
                {
                    ship: 'Submarine',
                    horizontal: true,
                    shipGridCells: [{row: 3, column: 3}]
                }
            ];
            PhaserGame.add.sprite.withArgs(0, 0, 'Submarine', 0).returns(submarineSprite);
            PhaserGame.add.sprite.withArgs(0, 0, 'Destroyer', 0).returns(newDestroyer);
            service.placeShips(shipStates);
            expect(destroyerSprite.destroy.calledWithMatch());
            expect(carrierSprite.destroy.calledWithMatch());
            expect(PhaserGame.physics.arcade.enable.calledWithMatch(submarineSprite));
            expect(PhaserGame.physics.arcade.enable.calledWithMatch(newDestroyer));

            expect(newDestroyer.body.debug).to.be.true;
            expect(newDestroyer.body.collideWorldBounds).to.be.true;
            expect(newDestroyer.height).to.equal(193);
            expect(newDestroyer.width).to.equal(198);
            expect(newDestroyer.angle).to.equal(90);
            expect(newDestroyer.body.height).to.equal(200);
            expect(newDestroyer.body.width).to.equal(195);
            assert(newDestroyer.anchor.setTo.calledWithMatch(0.5, 0.5));
            expect(newDestroyer.x).to.equal(650);
            expect(newDestroyer.y).to.equal(600);

            expect(submarineSprite.body.debug).to.be.true;
            expect(submarineSprite.body.collideWorldBounds).to.be.true;
            expect(submarineSprite.height).to.equal(193);
            expect(submarineSprite.width).to.equal(198);
            expect(submarineSprite.angle).to.equal(0);
            expect(submarineSprite.body.height).to.equal(195);
            expect(submarineSprite.body.width).to.equal(200);
            assert(submarineSprite.anchor.setTo.calledWithMatch(0.5, 0.5));
            expect(submarineSprite.x).to.equal(450);
            expect(submarineSprite.y).to.equal(350);
        });
    });

});