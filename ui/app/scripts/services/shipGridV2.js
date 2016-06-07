'use strict';

//  TODO - testing - Avoiding tests - plan to rewrite a fair bit to use a lot more native phaser functionality
//  for drag/doubletap/compute overlap
angular.module('tbs.services').factory('tbsShipGridV2',
    ['jtbPlayerService', 'tbsCircles', 'tbsCellStates', 'tbsShips', 'Phaser',
        function (jtbPlayerService, tbsCircles, tbsCellStates, tbsShips, Phaser) {
            var CELL_SIZE = 100;
            var HALF_CELL_SIZE = CELL_SIZE / 2;
            var POSITIVE_TINT = 0x00ff00;
            var NEGATIVE_TINT = 0xff0000;
            var NO_TINT = 0xffffff;

            var cellStateMap;
            var cellMarkersToPlace = [], cellMarkersOnGrid = [];

            var circleDataFromServers, circleCombinedRelativeCoordinates;
            var circleSprites = [];

            var shipNames = [], shipInfoMap = {};
            var shipsOnGrid = [], shipStatesToPlace = [];
            var hasOverlappingShips = undefined, movementEnabled = false;
            var recomputeSnapping = [], recomputeNextLoop = [];
            var shipsOverlappingChangedCB = undefined;

            var highlightSprite = null, highlightedShip = null;
            var highlightedX, highlightedY;

            var theme;

            var gameWidth, gameHeight;
            var phaser;

            var postCreateCallback;
            var highlightCallback;

            function update() {
                if (movementEnabled) {
                    //  Rotating a ship can leave them off snap grid
                    //  Need to force a re-snap - however, need to wait one update cycle before doing it
                    //  so other computations from drag and rotate are applied
                    angular.forEach(recomputeNextLoop, function (shipOnGrid) {
                        shipOnGrid.shipSprite.input.startDrag(phaser.input.activePointer);
                        shipOnGrid.shipSprite.input.stopDrag(phaser.input.activePointer);
                    });
                    recomputeNextLoop = recomputeSnapping;
                    recomputeSnapping = [];

                    var lastHasOverlap = hasOverlappingShips;
                    checkForOverlap();
                    if (lastHasOverlap !== hasOverlappingShips && angular.isDefined(shipsOverlappingChangedCB)) {
                        shipsOverlappingChangedCB(hasOverlappingShips);
                    }
                }
            }

            function preload() {
                circleCombinedRelativeCoordinates = [];
                var json;
                switch (gameHeight) {
                    case 1000:
                        json = '10x10.json';
                        angular.forEach(circleDataFromServers['10'], function (coordinate) {
                            circleCombinedRelativeCoordinates.push(coordinate);
                        });
                        break;
                    case 1500:
                        angular.forEach(circleDataFromServers['10'], function (coordinate) {
                            circleCombinedRelativeCoordinates.push(coordinate);
                        });
                        angular.forEach(circleDataFromServers['15'], function (coordinate) {
                            circleCombinedRelativeCoordinates.push(coordinate);
                        });
                        json = '15x15.json';
                        break;
                    case 2000:
                        angular.forEach(circleDataFromServers['10'], function (coordinate) {
                            circleCombinedRelativeCoordinates.push(coordinate);
                        });
                        angular.forEach(circleDataFromServers['15'], function (coordinate) {
                            circleCombinedRelativeCoordinates.push(coordinate);
                        });
                        angular.forEach(circleDataFromServers['20'], function (coordinate) {
                            circleCombinedRelativeCoordinates.push(coordinate);
                        });
                        json = '20x20.json';
                        break;
                }

                //  Get rid of 0,0 so it can be highlighted explicitly
                circleCombinedRelativeCoordinates.splice(0, 1);

                angular.forEach(cellMarkersOnGrid, function (cellMarkerOnGrid) {
                    cellMarkerOnGrid.destroy();
                });
                cellMarkersOnGrid = [];

                angular.forEach(circleSprites, function (circleSprite) {
                    circleSprite.destroy();
                });
                circleSprites = [];

                if (highlightSprite !== null) {
                    highlightSprite.destroy();
                }
                highlightSprite = null;
                highlightedX = -1;
                highlightedY = -1;

                angular.forEach(shipsOnGrid, function (shipOnGrid) {
                    shipOnGrid.shipSprite.destroy();
                });
                shipsOnGrid = [];

                highlightCallback = null;
                //  TODO - see if we can have one tilemap file
                phaser.load.tilemap('grid', 'templates/gamefiles/' + json, null, Phaser.Tilemap.TILED_JSON);
                phaser.load.image('tile', 'images/' + theme + '/tile.png');
                phaser.load.image('highlight', 'images/' + theme + '/highlight.png');
                phaser.load.image('extendedhighlight', 'images/' + theme + '/extendedhighlight.png');
                angular.forEach(cellStateMap, function (state) {
                    var lower = angular.lowercase(state);
                    phaser.load.image(lower, 'images/' + theme + '/' + lower + '.png');
                });
                angular.forEach(shipNames, function (shipName) {
                    phaser.load.image(shipName, 'images/' + theme + '/' + angular.lowercase(shipName) + '.png');
                });
            }

            function init() {
                phaser.scale.scaleMode = Phaser.ScaleManager.SHOW_ALL;
            }

            function create() {
                var map = phaser.add.tilemap('grid');
                map.addTilesetImage('tile');
                map.createLayer('base grid');

                phaser.scale.pageAlignHorizontally = false;
                phaser.scale.pageAlignVertically = false;
                phaser.scale.windowConstraints.bottom = 'visual';
                phaser.physics.startSystem(Phaser.Physics.ARCADE);

                refreshShipsOnGrid();
                refreshCellMarkersOnGrid();

                if (postCreateCallback !== null) {
                    postCreateCallback();
                }

                hasOverlappingShips = undefined;
            }

            function refreshCellMarkersOnGrid() {
                angular.forEach(cellMarkersOnGrid, function (markersOnGridRow) {
                    angular.forEach(markersOnGridRow, function (markersOnGridCell) {
                        markersOnGridCell.destroy();
                    });
                });
                cellMarkersOnGrid = [];
                var row = 0;
                angular.forEach(cellMarkersToPlace, function (cellMarkerRow) {
                    var rowArray = [];
                    cellMarkersOnGrid.push(rowArray);
                    var col = 0;
                    angular.forEach(cellMarkerRow, function (cellMarkerCell) {
                        var type = angular.lowercase(cellMarkerCell);
                        var marker = phaser.add.sprite(col * CELL_SIZE, row * CELL_SIZE, type, 0);
                        rowArray.push(marker);
                        ++col;
                    });
                    ++row;
                });
            }

            //  Takes shipState from server
            function placeShip(shipState) {
                var firstCell = shipState.shipGridCells[0];
                var shipInfo = shipInfoMap[shipState.ship];
                var shipSprite = phaser.add.sprite(0, 0, shipState.ship, 0);
                shipSprite.angle = shipState.horizontal ? 0 : 90;
                phaser.physics.arcade.enable(shipSprite);
                shipSprite.body.collideWorldBounds = true;
                shipSprite.body.debug = true;
                shipSprite.anchor.setTo(0.5, 0.5);
                shipSprite.width = shipSprite.width - 2;
                shipSprite.height = shipSprite.height - 2;
                var centerX, centerY;
                if (shipState.horizontal) {
                    centerY = (firstCell.row * CELL_SIZE) + HALF_CELL_SIZE;
                    centerX = (firstCell.column * CELL_SIZE) + (shipInfo.gridSize * HALF_CELL_SIZE);
                } else {
                    centerY = (firstCell.row * CELL_SIZE) + (shipInfo.gridSize * HALF_CELL_SIZE);
                    centerX = (firstCell.column * CELL_SIZE) + HALF_CELL_SIZE;
                }
                shipSprite.x = centerX;
                shipSprite.y = centerY;

                var shipOnGrid = {
                    shipSprite: shipSprite,
                    shipState: shipState,
                    shipInfo: shipInfo
                };
                shipsOnGrid.push(shipOnGrid);
            }

            /*
             function currentContextCoordinates(context) {
             var x = context.worldX;
             var y = context.worldY;
             return {x: x, y: y};
             }

             function highlightAShip(x, y) {
             if (highlightedShip !== null) {
             highlightedShip.shipSprite.tint = 0xffffff;
             }
             highlightedShip = findShipByCoordinates({x: x, y: y});
             if (highlightedShip !== null) {
             highlightedShip.shipSprite.tint = 0x00ff00;
             }
             }

             function highlightCell(context) {
             var coords = currentContextCoordinates(context);
             var y = Math.floor(coords.y / CELL_SIZE) * CELL_SIZE;
             var x = Math.floor(coords.x / CELL_SIZE) * CELL_SIZE;
             if (highlightSprite === null) {
             highlightSprite = phaser.add.sprite(x, y, 'highlight', 0);
             angular.forEach(circleCombinedRelativeCoordinates, function () {
             circleSprites.push(phaser.add.sprite(x, y, 'extendedhighlight', 0));
             });
             }
             highlightSprite.x = x;
             highlightSprite.y = y;
             highlightedX = x / CELL_SIZE;
             highlightedY = y / CELL_SIZE;
             for (var i = 0; i < circleCombinedRelativeCoordinates.length; ++i) {
             var coord = circleCombinedRelativeCoordinates[i];
             var sprite = circleSprites[i];
             sprite.x = x + (coord.column * CELL_SIZE);
             sprite.y = y + (coord.row * CELL_SIZE);
             }
             highlightAShip(x, y);
             if (highlightCallback !== null) {
             highlightCallback();
             }
             }

             function findShipByCoordinates(coords) {
             for (var i = 0; i < shipsOnGrid.length; ++i) {
             var shipOnGrid = shipsOnGrid[i];
             if (coords.x >= shipOnGrid.startX &&
             coords.y >= shipOnGrid.startY &&
             coords.x < shipOnGrid.endX &&
             coords.y < shipOnGrid.endY) {
             return shipOnGrid;
             }
             }
             return null;
             }
             */

            function refreshShipsOnGrid() {
                angular.forEach(shipsOnGrid, function (ship) {
                    ship.shipSprite.destroy();
                });
                shipsOnGrid = [];
                angular.forEach(shipStatesToPlace, function (shipState) {
                    placeShip(shipState);
                });
            }

            function enableGridSnapping(shipOnGrid) {
                shipOnGrid.shipSprite.input.disableSnap();
                var offsetX, offsetY;
                if (shipOnGrid.shipState.horizontal) {
                    offsetY = HALF_CELL_SIZE;
                    offsetX = (shipOnGrid.shipInfo.gridSize % 2 === 0) ? 0 : HALF_CELL_SIZE;
                } else {
                    offsetX = HALF_CELL_SIZE;
                    offsetY = (shipOnGrid.shipInfo.gridSize % 2 === 0) ? 0 : HALF_CELL_SIZE;
                }
                shipOnGrid.shipSprite.input.enableSnap(CELL_SIZE, CELL_SIZE, false, true, offsetX, offsetY);
            }

            function recomputeGridCells(shipOnGrid) {
                var cells = [], i;
                var bounds = shipOnGrid.shipSprite.getBounds();
                var column = Math.round(bounds.x / CELL_SIZE);
                var row = Math.round(bounds.y / CELL_SIZE);
                if (shipOnGrid.shipState.horizontal) {
                    for (i = 0; i < shipOnGrid.shipInfo.gridSize; i++) {
                        cells.push({row: row, column: column + i});
                    }
                } else {
                    for (i = 0; i < shipOnGrid.shipInfo.gridSize; i++) {
                        cells.push({row: row + i, column: column});
                    }
                }
                shipOnGrid.shipState.shipGridCells = cells;
                console.log(JSON.stringify(shipOnGrid.shipState));
            }

            //noinspection JSUnusedLocalSymbols
            function rotateShipOnDoubleClick(context, isDouble) {
                if (isDouble) {
                    angular.forEach(shipsOnGrid, function (shipOnGrid) {
                        if (shipOnGrid.shipSprite.input.pointerOver()) {
                            shipOnGrid.shipState.horizontal = !shipOnGrid.shipState.horizontal;
                            var swap = shipOnGrid.shipSprite.body.width;
                            //noinspection JSSuspiciousNameCombination
                            shipOnGrid.shipSprite.body.width = shipOnGrid.shipSprite.body.height;
                            shipOnGrid.shipSprite.body.height = swap;
                            shipOnGrid.shipSprite.angle = shipOnGrid.shipState.horizontal ? 0 : 90;
                            enableGridSnapping(shipOnGrid);
                            //  rotate can leave ship off snap lines
                            recomputeSnapping.push(shipOnGrid);
                        }
                    });
                }
            }

            function checkForOverlap() {
                hasOverlappingShips = false;
                angular.forEach(shipsOnGrid, function (shipOnGrid) {
                    if (shipOnGrid.shipSprite.tint === NEGATIVE_TINT) {
                        if (angular.isDefined(shipOnGrid.shipSprite.input)) {
                            if (shipOnGrid.shipSprite.input.isDragged) {
                                shipOnGrid.shipSprite.tint = POSITIVE_TINT;
                            } else {
                                shipOnGrid.shipSprite.tint = NO_TINT;
                            }
                        }
                    }
                });

                angular.forEach(shipsOnGrid, function (shipOnGrid1, index1) {
                    var bounds1 = shipOnGrid1.shipSprite.getBounds();
                    angular.forEach(shipsOnGrid, function (shipOnGrid2, index2) {
                        if (index1 !== index2) {
                            var bounds2 = shipOnGrid2.shipSprite.getBounds();
                            if (Phaser.Rectangle.intersects(bounds1, bounds2)) {
                                hasOverlappingShips = true;
                                shipOnGrid1.shipSprite.tint = NEGATIVE_TINT;
                                shipOnGrid2.shipSprite.tint = NEGATIVE_TINT;
                            }
                        }
                    }, this);
                });
            }

            return {
                stop: function () {
                    if (angular.isDefined(phaser)) {
                        phaser.destroy();
                        phaser = null;
                    }
                },
                initialize: function (loadedGame, initialShipStates, initialCellMarkers, postCreateCB) {
                    theme = jtbPlayerService.currentPlayer().gameSpecificPlayerAttributes.theme;
                    gameWidth = loadedGame.gridSize * CELL_SIZE;
                    gameHeight = loadedGame.gridSize * CELL_SIZE;
                    shipStatesToPlace = initialShipStates;
                    cellMarkersToPlace = initialCellMarkers;
                    postCreateCallback = postCreateCB;
                    tbsShips.ships().then(function (shipData) {
                        shipNames = [];
                        shipInfoMap = {};
                        angular.forEach(shipData, function (singleShip) {
                            shipNames.push(singleShip.ship);
                            shipInfoMap[singleShip.ship] = singleShip;
                        });
                        tbsCellStates.cellStates().then(function (stateData) {
                            cellStateMap = stateData;
                            tbsCircles.circles().then(
                                function (circleData) {
                                    circleDataFromServers = circleData;
                                    phaser = new Phaser.Game(
                                        gameWidth,
                                        gameHeight,
                                        Phaser.AUTO,
                                        'phaser',
                                        {preload: preload, create: create, init: init, update: update});
                                },
                                function (error) {
                                    //  TODO
                                    console.warn(error);
                                }
                            );
                        });
                    });
                },

                enableShipMovement: function (overlappingChangedCB) {
                    shipsOverlappingChangedCB = overlappingChangedCB;
                    movementEnabled = true;
                    angular.forEach(shipsOnGrid, function (shipOnGrid) {
                        shipOnGrid.shipSprite.inputEnabled = true;
                        shipOnGrid.shipSprite.input.enableDrag();
                        enableGridSnapping(shipOnGrid);
                        shipOnGrid.shipSprite.events.onDragStart.add(function () {
                            shipOnGrid.shipSprite.tint = NEGATIVE_TINT;
                        });
                        shipOnGrid.shipSprite.events.onDragStop.add(function () {
                            shipOnGrid.shipSprite.tint = NO_TINT;
                            recomputeGridCells(shipOnGrid);
                        });
                    });
                    phaser.input.onTap.add(rotateShipOnDoubleClick, this);
                },

                placeShips: function (newShipStates) {
                    shipStatesToPlace = newShipStates;
                    refreshShipsOnGrid();
                    //  TODO 
                    //highlightAShip(highlightedX * CELL_SIZE, highlightedY * CELL_SIZE);
                },

                placeCellMarkers: function (newCellMarkers) {
                    cellMarkersToPlace = newCellMarkers;
                    refreshCellMarkersOnGrid();
                },

                currentShipsOnGrid: function () {
                    var ships = [];
                    angular.forEach(shipsOnGrid, function (shipOnGrid) {
                        ships.push(shipOnGrid.shipState);
                    });
                    return ships;
                },

                /*
                 halfCellSize: function () {
                 return HALF_CELL_SIZE;
                 },

                 gameWidth: function () {
                 return gameWidth;
                 },
                 gameHeight: function () {
                 return gameHeight;
                 },
                 cellSize: function () {
                 return CELL_SIZE;
                 },

                 activateHighlighting: function (highlightCB) {
                 highlightCallback = highlightCB;
                 this.onDown(highlightCell);
                 },
                 selectedCell: function () {
                 return {x: highlightedX, y: highlightedY};
                 },
                 selectedShip: function () {
                 return findShipByCoordinates({x: highlightedX * CELL_SIZE, y: highlightedY * CELL_SIZE});
                 },

                 computeShipCorners: function (shipData) {
                 computeShipCorners(shipData);
                 },

                 currentContextCoordinates: function (context) {
                 return currentContextCoordinates(context);
                 },

                 findShipByContextCoordinates: function (context) {
                 return findShipByCoordinates(currentContextCoordinates(context));
                 },

                 onMove: function (cb) {
                 phaser.input.addMoveCallback(cb);
                 },

                 onDown: function (cb) {
                 phaser.input.onDown.add(cb);
                 },

                 onUp: function (cb) {
                 phaser.input.onUp.add(cb);
                 },

                 onTap: function (cb) {
                 phaser.input.onTap.add(cb);
                 }
                 */
                placeholder: function () {
                }
            };
        }
    ]
);
