'use strict';

angular.module('tbs.services').factory('tbsShipGridV2',
    ['jtbPlayerService', 'tbsCircles', 'tbsCellStates', 'tbsShips', 'Phaser', 'tbsPhaserGameFactory',
        function (jtbPlayerService, tbsCircles, tbsCellStates, tbsShips, Phaser, tbsPhaserGameFactory) {
            var CELL_SIZE = 100;
            var HALF_CELL_SIZE = CELL_SIZE / 2;
            var POSITIVE_TINT = 0x00ff00;
            var NEGATIVE_TINT = 0xff0000;
            var NO_TINT = 0xffffff;

            var cellStateMap;
            var cellMarkersToPlace = [], cellMarkersOnGrid = [];

            var circleDataFromServers, circleCombinedRelativeCoordinates;
            var circleSprites = [], centerCircleSprite;

            var shipNames = [], shipInfoMap = {};
            var shipsOnGrid = [], shipStatesToPlace = [];
            var hasOverlappingShips, movementEnabled = false;
            var recomputeSnapping = [], recomputeNextLoop = [];
            var shipsOverlappingChangedCB;

            var selectedShip, lastSelectionContext;

            var theme;

            var gameWidth, gameHeight;
            var phaser;

            var postCreateCallback;
            var cellSelectionCB;

            function update() {
                if (movementEnabled) {
                    //  Rotating a ship can leave the ship incorrectly aligned with snap grid
                    //  Need to force a re-snap - however, need to wait one update cycle before doing it
                    //  so other computations from drag and rotate and boundaries are applied from phaser
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

                clearExistingCellMarkersOnGrid();
                clearExistingShipsOnGrid();

                angular.forEach(circleSprites, function (circleSprite) {
                    circleSprite.destroy();
                });
                circleSprites = [];

                if (angular.isDefined(centerCircleSprite)) {
                    centerCircleSprite.destroy();
                }
                centerCircleSprite = undefined;
                selectedShip = undefined;

                cellSelectionCB = undefined;
                lastSelectionContext = undefined;

                movementEnabled = false;

                phaser.load.tilemap('grid', 'templates/gamefiles/' + json, null, Phaser.Tilemap.TILED_JSON);
                var themeImages = 'images/' + theme + '/';
                phaser.load.image('tile', themeImages + 'tile.png');
                phaser.load.image('highlight', themeImages + 'highlight.png');
                phaser.load.image('extendedhighlight', themeImages + 'extendedhighlight.png');
                angular.forEach(cellStateMap, function (state) {
                    var lower = angular.lowercase(state);
                    phaser.load.image(lower, themeImages + lower + '.png');
                });
                angular.forEach(shipNames, function (shipName) {
                    phaser.load.image(shipName, themeImages + angular.lowercase(shipName) + '.png');
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

                if (angular.isDefined(postCreateCallback)) {
                    postCreateCallback();
                }

                hasOverlappingShips = undefined;
            }

            function clearExistingCellMarkersOnGrid() {
                angular.forEach(cellMarkersOnGrid, function (markersOnGridRow) {
                    angular.forEach(markersOnGridRow, function (markersOnGridCell) {
                        markersOnGridCell.destroy();
                    });
                });
                cellMarkersOnGrid = [];
            }

            function refreshCellMarkersOnGrid() {
                clearExistingCellMarkersOnGrid();
                angular.forEach(cellMarkersToPlace, function (cellMarkerRow, row) {
                    var rowArray = [];
                    cellMarkersOnGrid.push(rowArray);
                    angular.forEach(cellMarkerRow, function (cellMarkerCell, col) {
                        var type = angular.lowercase(cellMarkerCell);
                        var marker = phaser.add.sprite(col * CELL_SIZE, row * CELL_SIZE, type, 0);
                        rowArray.push(marker);
                    });
                });
            }

            //  Takes shipState from server
            function placeShip(shipState) {
                var firstCell = shipState.shipGridCells[0];
                var shipInfo = shipInfoMap[shipState.ship];
                var shipSprite = phaser.add.sprite(0, 0, shipState.ship, 0);
                shipSprite.width = shipSprite.width - 2;
                shipSprite.height = shipSprite.height - 2;
                shipSprite.angle = shipState.horizontal ? 0 : 90;
                phaser.physics.arcade.enable(shipSprite);
                shipSprite.body.collideWorldBounds = true;
                shipSprite.body.debug = true;
                shipSprite.anchor.setTo(0.5, 0.5);
                var centerX, centerY;
                if (shipState.horizontal) {
                    centerY = (firstCell.row * CELL_SIZE) + HALF_CELL_SIZE;
                    centerX = (firstCell.column * CELL_SIZE) + (shipInfo.gridSize * HALF_CELL_SIZE);
                } else {
                    centerY = (firstCell.row * CELL_SIZE) + (shipInfo.gridSize * HALF_CELL_SIZE);
                    centerX = (firstCell.column * CELL_SIZE) + HALF_CELL_SIZE;
                    var swap = shipSprite.body.width;
                    //noinspection JSSuspiciousNameCombination
                    shipSprite.body.width = shipSprite.body.height;
                    shipSprite.body.height = swap;
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

            function drawCircleCenteredOnCell(row, column) {
                var y = row * CELL_SIZE;
                var x = column * CELL_SIZE;
                if (angular.isUndefined(centerCircleSprite)) {
                    centerCircleSprite = phaser.add.sprite(x, y, 'highlight', 0);
                    angular.forEach(circleCombinedRelativeCoordinates, function (cell, index) {
                        circleSprites.push(phaser.add.sprite(x + index, y + index, 'extendedhighlight', 0));
                    });
                }
                centerCircleSprite.x = x;
                centerCircleSprite.y = y;
                angular.forEach(circleCombinedRelativeCoordinates, function (relativeCoordinate, index) {
                    var extendedCircleSprite = circleSprites[index];
                    extendedCircleSprite.x = x + (relativeCoordinate.column * CELL_SIZE);
                    extendedCircleSprite.y = y + (relativeCoordinate.row * CELL_SIZE);
                });
            }

            function highlightShipIfInSelectedCell(row, column) {
                if (angular.isDefined(selectedShip)) {
                    selectedShip.shipSprite.tint = NO_TINT;
                }
                selectedShip = undefined;
                angular.forEach(shipsOnGrid, function (shipOnGrid) {
                    angular.forEach(shipOnGrid.shipState.shipGridCells, function (shipGridCell) {
                        if (shipGridCell.row === row && shipGridCell.column === column) {
                            selectedShip = shipOnGrid;
                        }
                    });
                });
                if (angular.isDefined(selectedShip)) {
                    selectedShip.shipSprite.tint = POSITIVE_TINT;
                }
            }

            function selectCellCB(context) {
                if (angular.isUndefined(context)) {
                    return;
                }
                //  phaser seems to reuse context so need to make a copy of variables
                lastSelectionContext = {worldX: context.worldX, worldY: context.worldY};
                var row = Math.floor(context.worldY / CELL_SIZE);
                var column = Math.floor(context.worldX / CELL_SIZE);
                drawCircleCenteredOnCell(row, column);
                highlightShipIfInSelectedCell(row, column);
                if (angular.isDefined(cellSelectionCB)) {
                    cellSelectionCB(
                        {
                            row: row,
                            column: column
                        },
                        angular.isDefined(selectedShip) ? selectedShip.shipState : undefined);
                }
            }

            function clearExistingShipsOnGrid() {
                angular.forEach(shipsOnGrid, function (ship) {
                    ship.shipSprite.destroy();
                });
                shipsOnGrid = [];
            }

            function refreshShipsOnGrid() {
                clearExistingShipsOnGrid();
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
                initialize: function (loadedGame, initialShipStates, initialCellMarkers, postCreateCB) {
                    theme = jtbPlayerService.currentPlayer().gameSpecificPlayerAttributes.theme;
                    gameWidth = loadedGame.gridSize * CELL_SIZE;
                    gameHeight = loadedGame.gridSize * CELL_SIZE;
                    shipStatesToPlace = angular.copy(initialShipStates);
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
                                    phaser = tbsPhaserGameFactory.newGame(
                                        gameWidth,
                                        gameHeight,
                                        'phaser',
                                        {
                                            preload: preload,
                                            create: create,
                                            init: init,
                                            update: update
                                        });
                                }
                            );
                        });
                    });
                },

                stop: function () {
                    if (angular.isDefined(phaser)) {
                        phaser.destroy();
                        phaser = undefined;
                    }
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

                enableCellSelecting: function (selectionCB) {
                    cellSelectionCB = selectionCB;
                    phaser.input.onDown.add(selectCellCB);
                    selectCellCB({worldX: 0, worldY: 0});
                },

                placeShips: function (newShipStates) {
                    shipStatesToPlace = angular.copy(newShipStates);
                    refreshShipsOnGrid();
                    selectCellCB(lastSelectionContext);
                },

                currentShipsOnGrid: function () {
                    var ships = [];
                    angular.forEach(shipsOnGrid, function (shipOnGrid) {
                        ships.push(shipOnGrid.shipState);
                    });
                    return ships;
                },

                placeCellMarkers: function (newCellMarkers) {
                    cellMarkersToPlace = newCellMarkers;
                    refreshCellMarkersOnGrid();
                }

            };
        }
    ]
);
