'use strict';

describe('Service: shipGridV2', function () {
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
        '10': [
            {'row': 0, 'column': 0},
            {'row': 0, 'column': -1},
            {'row': 0, 'column': 1},
            {'row': 1, 'column': 0},
            {'row': -1, 'column': 0}
        ],
        '15': [
            {'row': 0, 'column': -2},
            {'row': 0, 'column': 2},
            {'row': 2, 'column': 0},
            {'row': -2, 'column': 0},
            {'row': 1, 'column': 1},
            {'row': 1, 'column': -1},
            {'row': -1, 'column': -1},
            {'row': -1, 'column': 1}
        ],
        '20': [
            {'row': 2, 'column': 2},
            {'row': 2, 'column': -2},
            {'row': -2, 'column': -2},
            {'row': -2, 'column': 2},
            {'row': 2, 'column': 1},
            {'row': 1, 'column': 2},
            {'row': 2, 'column': -1},
            {'row': -1, 'column': 2},
            {'row': -2, 'column': -1},
            {'row': -1, 'column': -2},
            {'row': -2, 'column': 1},
            {'row': 1, 'column': -2}
        ]
    };
    var testCircleData = [];
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
        input: {
            activePointer: {active: 1, pointer: 'it is'},
            onDown: {
                onDownCB: undefined,
                add: function (cb) {
                    this.onDownCB = cb;
                }
            },
            onTap: {
                onTapCB: undefined,
                onTapContext: undefined,
                add: function (cb, context) {
                    this.onTapCB = cb;
                    this.onTapContext = context;
                }
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
        testCircleData = angular.copy(expectedCircleData['10']);
        testCircleData = testCircleData.concat(expectedCircleData['15']);
        testCircleData = testCircleData.concat(expectedCircleData['20']);
        testCircleData.splice(0, 1);
        Phaser.Rectangle.intersects.reset();
        PhaserFactory.newGame.reset();
        PhaserGame.input.onDown.onDownCB = undefined;
        PhaserGame.input.onTap.onTapCB = undefined;
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

    function makeSprite() {
        return {
            destroy: sinon.spy()
        };
    }

    function makeShipSprite(name) {
        var sprite = {
            name: name,
            destroy: sinon.spy(),
            body: {
                collideWorldBounds: false,
                debug: false,
                width: 200,
                height: 195,
                setSize: sinon.spy(),
                hitTest: sinon.stub(),
            },
            input: {
                events: undefined,
                startDrag: function (pointer) {
                    expect(pointer).to.equal(PhaserGame.input.activePointer);
                    this.events.onDragStart.dragStartCB();
                },
                stopDrag: function (pointer) {
                    expect(pointer).to.equal(PhaserGame.input.activePointer);
                    this.events.onDragStop.dragStopCB();
                },
                enableDrag: sinon.spy(),
                disableSnap: sinon.spy(),
                enableSnap: sinon.spy()
            },
            events: {
                onDragStart: {
                    dragStartCB: undefined,
                    add: function (cb) {
                        this.dragStartCB = cb;
                    }
                },
                onDragStop: {
                    dragStopCB: undefined,
                    add: function (cb) {
                        this.dragStopCB = cb;
                    }
                }
            },
            anchor: {
                setTo: sinon.spy()
            },
            width: 200,
            height: 195,
            angle: undefined,
            x: undefined,
            y: undefined,
            tint: undefined,
            getBounds: sinon.stub()
        };
        sprite.input.events = sprite.events;
        return sprite;
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

        it('stopping game 2nd time does nothing', function () {
            service.stop();
            assert(PhaserGame.destroy.calledWithMatch());
            expect(PhaserGame.destroy.callCount).to.equal(1);
            service.stop();
            expect(PhaserGame.destroy.callCount).to.equal(1);
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

        it('place some ships onto grid', function () {
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
            expect(service.currentShipsOnGrid()).to.deep.equal(shipStates);
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
            expect(carrierSprite.tint).to.be.undefined;

            expect(destroyerSprite.body.debug).to.be.true;
            expect(destroyerSprite.body.collideWorldBounds).to.be.true;
            expect(destroyerSprite.height).to.equal(193);
            expect(destroyerSprite.width).to.equal(198);
            expect(destroyerSprite.angle).to.equal(90);
            assert(destroyerSprite.body.setSize.calledWithMatch(195, 200, 0, 0));
            assert(destroyerSprite.anchor.setTo.calledWithMatch(0.5, 0.5));
            expect(destroyerSprite.x).to.equal(650);
            expect(destroyerSprite.y).to.equal(600);
            expect(destroyerSprite.tint).to.be.undefined;

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
            expect(service.currentShipsOnGrid()).to.deep.equal(shipStates);
            expect(destroyerSprite.destroy.calledWithMatch());
            expect(carrierSprite.destroy.calledWithMatch());
            expect(PhaserGame.physics.arcade.enable.calledWithMatch(submarineSprite));
            expect(PhaserGame.physics.arcade.enable.calledWithMatch(newDestroyer));

            expect(newDestroyer.body.debug).to.be.true;
            expect(newDestroyer.body.collideWorldBounds).to.be.true;
            expect(newDestroyer.height).to.equal(193);
            expect(newDestroyer.width).to.equal(198);
            expect(newDestroyer.angle).to.equal(90);
            assert(newDestroyer.body.setSize.calledWithMatch(195, 200, 0, 0));
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

        it('place some markers onto grid', function () {
            var markers = [['Unknown'], ['KnownByMiss', 'KnownByHit'], ['KnownByMiss']];

            var unknownSprite = makeSprite();
            var knownByMissSprite1 = makeSprite();
            var knownByHitSprite = makeSprite();
            var knownByMissSprite2 = makeSprite();
            PhaserGame.add.sprite.withArgs(0, 0, 'unknown', 0).returns(unknownSprite);
            PhaserGame.add.sprite.withArgs(0, 100, 'knownbymiss', 0).returns(knownByMissSprite1);
            PhaserGame.add.sprite.withArgs(100, 100, 'knownbyhit', 0).returns(knownByHitSprite);
            PhaserGame.add.sprite.withArgs(0, 200, 'knownbymiss', 0).returns(knownByMissSprite2);
            service.placeCellMarkers(markers);

            markers = [['KnownShip', 'Unknown'], ['KnownByHit'], ['ObscuredHit']];
            PhaserGame.add.sprite.withArgs(0, 0, 'knownship', 0).returns({});
            PhaserGame.add.sprite.withArgs(100, 0, 'unknown', 0).returns({});
            PhaserGame.add.sprite.withArgs(0, 100, 'knownbyhit', 0).returns({});
            PhaserGame.add.sprite.withArgs(0, 200, 'obscuredhit', 0).returns({});
            service.placeCellMarkers(markers);
            assert(unknownSprite.destroy.calledWithMatch());
            assert(knownByHitSprite.destroy.calledWithMatch());
            assert(knownByMissSprite2.destroy.calledWithMatch());
            assert(knownByMissSprite1.destroy.calledWithMatch());
        });

        describe('testing enable cell selecting', function () {
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
            var carrierSprite, destroyerSprite;
            var selectionCB;
            var centerHighlight;
            var highlights;

            function generateStubReturns() {
                PhaserGame.add.sprite.withArgs(0, 0, 'highlight', 0).returns(centerHighlight);
                angular.forEach(testCircleData, function (circleCell, index) {
                    PhaserGame.add.sprite.withArgs(index, index, 'extendedhighlight', 0).returns(highlights[index]);
                });
            }

            beforeEach(function () {
                centerHighlight = makeSprite();
                highlights = [];
                angular.forEach(testCircleData, function () {
                    highlights.push(makeSprite());
                });
                selectionCB = sinon.spy();
                carrierSprite = makeShipSprite();
                destroyerSprite = makeShipSprite();
                PhaserGame.add.sprite.withArgs(0, 0, 'Carrier', 0).returns(carrierSprite);
                PhaserGame.add.sprite.withArgs(0, 0, 'Destroyer', 0).returns(destroyerSprite);

                service.placeShips(shipStates);
                generateStubReturns(0, 0);
                phaserCBs.preload();
                phaserCBs.create();
                expect(angular.isDefined(PhaserGame.input.onDown.onDownCB)).to.be.false;
                service.enableCellSelecting(selectionCB);
                expect(angular.isDefined(PhaserGame.input.onDown.onDownCB)).to.be.true;
            });

            function verifyHighlightPositions(baseX, baseY) {
                expect(centerHighlight.x).to.equal(baseX * 100);
                expect(centerHighlight.y).to.equal(baseY * 100);
                angular.forEach(testCircleData, function (circleCell, index) {
                    expect(highlights[index].x).to.equal((circleCell.column + baseX) * 100);
                    expect(highlights[index].y).to.equal((circleCell.row + baseY) * 100);
                });
            }

            it('test initializes to 0, 0 and returns carrier', function () {
                assert(selectionCB.calledWithMatch({row: 0, column: 0}, shipStates[0]));
                verifyHighlightPositions(0, 0);
                expect(carrierSprite.tint).to.equal(0x00ff00);
                expect(destroyerSprite.tint).to.equal(undefined);
            });

            it('test on select with undefined context does not call any further', function () {
                selectionCB.reset();
                PhaserGame.input.onDown.onDownCB();
                expect(selectionCB.callCount).to.equal(0);
                verifyHighlightPositions(0, 0);
            });

            it('test selecting a different cell with no ship highlighted', function () {
                PhaserGame.input.onDown.onDownCB({worldX: 420, worldY: 480});
                assert(selectionCB.calledWithMatch({row: 4, column: 4}, undefined));
                verifyHighlightPositions(4, 4);
                expect(carrierSprite.tint).to.equal(0xffffff);
                expect(destroyerSprite.tint).to.equal(undefined);
            });


            it('test selecting a different cell with another ship highlighted', function () {
                PhaserGame.input.onDown.onDownCB({worldX: 620, worldY: 580});
                assert(selectionCB.calledWithMatch({row: 5, column: 6}, shipStates[1]));
                verifyHighlightPositions(6, 5);
                expect(carrierSprite.tint).to.equal(0xffffff);
                expect(destroyerSprite.tint).to.equal(0x00ff00);
            });
        });

        describe('testing ship movement', function () {
            var shipStates = [
                {
                    ship: 'Carrier',
                    horizontal: true,
                    shipGridCells: [
                        {row: 0, column: 1},
                        {row: 0, column: 2},
                        {row: 0, column: 3},
                        {row: 0, column: 4},
                        {row: 0, column: 5}
                    ]
                },
                {
                    ship: 'Destroyer',
                    horizontal: true,
                    shipGridCells: [
                        {row: 1, column: 1},
                        {row: 1, column: 2}
                    ]
                },
                {
                    ship: 'Submarine',
                    horizontal: true,
                    shipGridCells: [
                        {row: 2, column: 1},
                        {row: 2, column: 2},
                        {row: 2, column: 3}
                    ]
                }
            ];
            var carrierSprite, destroyerSprite, submarineSprite;
            var sprites;
            var overlappingChangedCB = sinon.spy();

            beforeEach(function () {
                overlappingChangedCB.reset();
                carrierSprite = makeShipSprite('carrier');
                destroyerSprite = makeShipSprite('destroyer');
                submarineSprite = makeShipSprite('submarine');
                sprites = [carrierSprite, destroyerSprite, submarineSprite];
                PhaserGame.add.sprite.withArgs(0, 0, 'Carrier', 0).returns(carrierSprite);
                PhaserGame.add.sprite.withArgs(0, 0, 'Destroyer', 0).returns(destroyerSprite);
                PhaserGame.add.sprite.withArgs(0, 0, 'Submarine', 0).returns(submarineSprite);

                service.placeShips(shipStates);
                phaserCBs.preload();
                phaserCBs.create();

                expect(angular.isDefined(PhaserGame.input.onTap.onTapCB)).to.be.false;
                service.enableShipMovement(overlappingChangedCB);
                expect(angular.isDefined(PhaserGame.input.onTap.onTapCB)).to.be.true;
            });

            it('initializes to correct status', function () {
                angular.forEach(sprites, function (sprite) {
                    expect(sprite.inputEnabled).to.be.true;
                    assert(sprite.input.enableDrag.calledWithMatch());
                    assert(sprite.input.disableSnap.calledWithMatch());
                    expect(angular.isDefined(sprite.events.onDragStart.dragStartCB)).to.be.true;
                    expect(angular.isDefined(sprite.events.onDragStop.dragStopCB)).to.be.true;
                });
                assert(destroyerSprite.input.enableSnap.calledWithMatch(100, 100, true, true, 0, 50));
                assert(carrierSprite.input.enableSnap.calledWithMatch(100, 100, true, true, 50, 50));
                assert(submarineSprite.input.enableSnap.calledWithMatch(100, 100, true, true, 50, 50));
            });

            it('calling update in initial state calls cb with no overlap', function () {
                phaserCBs.update();
                assert(overlappingChangedCB.calledWithMatch(false));
            });

            it('test dragging start marks tint', function () {
                submarineSprite.events.onDragStart.dragStartCB();
                expect(submarineSprite.tint).to.equal(0xff0000);
            });

            it('test dragging stop marks tint, recomputes same grid cells', function () {
                var expectedCells = angular.copy(shipStates[2].shipGridCells);
                submarineSprite.events.onDragStart.dragStartCB();
                submarineSprite.getBounds.returns({x: 100, y: 200});
                submarineSprite.events.onDragStop.dragStopCB();
                expect(service.currentShipsOnGrid()[2].shipGridCells).to.deep.equal(expectedCells);
                expect(submarineSprite.tint).to.equal(0xffffff);
            });

            it('test dragging stop marks tint, recomputes moved horizontal grid cells', function () {
                submarineSprite.events.onDragStart.dragStartCB();
                submarineSprite.getBounds.returns({x: 200, y: 300});
                submarineSprite.events.onDragStop.dragStopCB();
                expect(service.currentShipsOnGrid()[2].shipGridCells).to.deep.equal(
                    [
                        {row: 3, column: 2},
                        {row: 3, column: 3},
                        {row: 3, column: 4}
                    ]);
                expect(submarineSprite.tint).to.equal(0xffffff);
            });

            it('test dragging stop marks tint, recomputes moved vertical grid cells', function () {
                submarineSprite.events.onDragStart.dragStartCB();
                service.currentShipsOnGrid()[2].horizontal = false;
                submarineSprite.getBounds.returns({x: 500, y: 300});
                submarineSprite.events.onDragStop.dragStopCB();
                expect(service.currentShipsOnGrid()[2].shipGridCells).to.deep.equal(
                    [
                        {row: 3, column: 5},
                        {row: 4, column: 5},
                        {row: 5, column: 5}
                    ]);
                expect(submarineSprite.tint).to.equal(0xffffff);
            });

            it('test double clicking rotates ship', function () {
                destroyerSprite.input.disableSnap.reset();
                destroyerSprite.input.enableSnap.reset();
                destroyerSprite.body.height = 100;
                destroyerSprite.body.width = 200;
                var pointer = {worldX: 32, worldY: 16};
                carrierSprite.body.hitTest.withArgs(pointer.worldX, pointer.worldY).returns(false);
                submarineSprite.body.hitTest.withArgs(pointer.worldX, pointer.worldY).returns(false);
                destroyerSprite.body.hitTest.withArgs(pointer.worldX, pointer.worldY).returns(true);
                PhaserGame.input.onTap.onTapCB(pointer, true);
                var destroyer = service.currentShipsOnGrid()[1];
                expect(destroyer.horizontal).to.be.false;
                expect(destroyerSprite.angle).to.equal(90);
                assert(destroyerSprite.body.setSize.calledWithMatch(100, 200, 0, 0));
                assert(destroyerSprite.input.disableSnap.calledWithMatch());
                assert(destroyerSprite.input.enableSnap.calledWithMatch(100, 100, true, true, 50, 0));
            });

            it('test update after double click calls recompute, but does not resnap yet', function () {
                phaserCBs.update();

                var pointer = {worldX: 32, worldY: 16};
                carrierSprite.body.hitTest.withArgs(pointer.worldX, pointer.worldY).returns(false);
                submarineSprite.body.hitTest.withArgs(pointer.worldX, pointer.worldY).returns(false);
                destroyerSprite.body.hitTest.withArgs(pointer.worldX, pointer.worldY).returns(true);

                destroyerSprite.input.isDragged = true;
                PhaserGame.input.onTap.onTapCB(pointer, true);

                var destroyerBounds = {d: 1, x: 300, y: 300}, subBounds = {s: 1}, carrierBounds = {c: 1};
                destroyerSprite.getBounds.returns(destroyerBounds);
                carrierSprite.getBounds.returns(carrierBounds);
                submarineSprite.getBounds.returns(subBounds);

                Phaser.Rectangle.intersects.withArgs(carrierBounds, destroyerBounds).returns(false);
                Phaser.Rectangle.intersects.withArgs(carrierBounds, subBounds).returns(false);
                Phaser.Rectangle.intersects.withArgs(destroyerBounds, carrierBounds).returns(false);
                Phaser.Rectangle.intersects.withArgs(destroyerBounds, subBounds).returns(true);
                Phaser.Rectangle.intersects.withArgs(subBounds, destroyerBounds).returns(true);
                Phaser.Rectangle.intersects.withArgs(subBounds, carrierBounds).returns(false);

                overlappingChangedCB.reset();
                //   first pass
                destroyerSprite.input.enableSnap.reset();
                destroyerSprite.input.disableSnap.reset();
                phaserCBs.update();
                assert(overlappingChangedCB.calledWithMatch(true));
                expect(destroyerSprite.tint).to.equal(0xff0000);
                expect(submarineSprite.tint).to.equal(0xff0000);

                expect(destroyerSprite.input.disableSnap.callCount).to.equal(0);
                expect(destroyerSprite.input.enableSnap.callCount).to.equal(0);
                expect(service.currentShipsOnGrid()[1].shipGridCells).to.deep.equal([{row: 1, column: 1}, {
                    row: 1,
                    column: 2
                }]);
                phaserCBs.update();
                expect(service.currentShipsOnGrid()[1].shipGridCells).to.deep.equal([{row: 3, column: 3}, {
                    row: 4,
                    column: 3
                }]);
            });

        });
    });

});