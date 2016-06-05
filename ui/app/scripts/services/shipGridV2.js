'use strict';

//  TODO - testing - Avoiding tests - plan to rewrite a fair bit to use a lot more native phaser functionality
//  for drag/doubletap/compute overlap
angular.module('tbs.services').factory('tbsShipGridV2',
    ['jtbPlayerService', 'tbsCircles', 'tbsCellStates', 'tbsShips', 'Phaser',
        function (jtbPlayerService, tbsCircles, tbsCellStates, tbsShips, Phaser) {
            var CELL_SIZE = 100;
            var HALF_CELL_SIZE = CELL_SIZE / 2;

            var cellStateMap;
            var cellMarkersToPlace = [], cellMarkersOnGrid = [];

            var circleDataFromServers, circleCombinedRelativeCoordinates;
            var circleSprites = [];

            var shipNames = [], shipInfoMap = {};
            var shipsOnGrid = [], shipStatesToPlace = [];

            var highlightSprite = null, highlightedShip = null;
            var highlightedX, highlightedY;

            var theme;

            var gameWidth, gameHeight;
            var phaser;

            var postCreateCallback;
            var highlightCallback;

            function render() {
                angular.forEach(shipsOnGrid, function (shipOnGrid) {
                    phaser.debug.body(shipOnGrid.shipSprite);
                });
            }

            function preload() {
                circleCombinedRelativeCoordinates = [];
                var json;
                switch (gameHeight) {
                    case 1000:
                        json = '10x10.json';
                        angular.forEach(circleDataFromServers['10'], function (coord) {
                            circleCombinedRelativeCoordinates.push(coord);
                        });
                        break;
                    case 1500:
                        angular.forEach(circleDataFromServers['10'], function (coord) {
                            circleCombinedRelativeCoordinates.push(coord);
                        });
                        angular.forEach(circleDataFromServers['15'], function (coord) {
                            circleCombinedRelativeCoordinates.push(coord);
                        });
                        json = '15x15.json';
                        break;
                    case 2000:
                        angular.forEach(circleDataFromServers['10'], function (coord) {
                            circleCombinedRelativeCoordinates.push(coord);
                        });
                        angular.forEach(circleDataFromServers['15'], function (coord) {
                            circleCombinedRelativeCoordinates.push(coord);
                        });
                        angular.forEach(circleDataFromServers['20'], function (coord) {
                            circleCombinedRelativeCoordinates.push(coord);
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
                var offsetX, offsetY;
                if (shipOnGrid.shipState.horizontal) {
                    offsetY = HALF_CELL_SIZE;
                    offsetX = (shipOnGrid.shipInfo.gridSize % 2 == 0) ? 0 : HALF_CELL_SIZE;
                } else {
                    offsetX = HALF_CELL_SIZE;
                    offsetY = (shipOnGrid.shipInfo.gridSize % 2 == 0) ? 0 : HALF_CELL_SIZE;
                }
                shipOnGrid.shipSprite.input.enableSnap(CELL_SIZE, CELL_SIZE, false, true, offsetX, offsetY);
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
                                        {preload: preload, create: create, init: init, render: render});
                                },
                                function (error) {
                                    //  TODO
                                    console.warn(error);
                                }
                            );
                        });
                    });
                },

                enableShipMovement: function () {
                    angular.forEach(shipsOnGrid, function (shipOnGrid) {
                        shipOnGrid.shipSprite.inputEnabled = true;
                        shipOnGrid.shipSprite.input.enableDrag();
                        enableGridSnapping(shipOnGrid);
                    });
                    phaser.input.onTap.add(function (context, isDouble) {
                        if (isDouble) {
                            angular.forEach(shipsOnGrid, function (shipOnGrid) {
                                if (shipOnGrid.shipSprite.input.pointerOver()) {
                                    shipOnGrid.shipState.horizontal = !shipOnGrid.shipState.horizontal;
                                    shipOnGrid.shipSprite.angle = shipOnGrid.shipState.horizontal ? 0 : 90;
                                    var swap = shipOnGrid.shipSprite.body.width;
                                    //noinspection JSSuspiciousNameCombination
                                    shipOnGrid.shipSprite.body.width = shipOnGrid.shipSprite.body.height;
                                    shipOnGrid.shipSprite.body.height = swap;
                                    enableGridSnapping(shipOnGrid);
                                }
                            });
                        }
                    }, this);
                },
                placeShips: function (newShipStates) {
                    shipStatesToPlace = newShipStates;
                    refreshShipsOnGrid();
                    highlightAShip(highlightedX * CELL_SIZE, highlightedY * CELL_SIZE);
                },

                placeCellMarkers: function (newCellMarkers) {
                    cellMarkersToPlace = newCellMarkers;
                    refreshCellMarkersOnGrid();
                },

                currentShipsOnGrid: function () {
                    return shipsOnGrid;
                },
                halfCellSize: function () {
                    return HALF_CELL_SIZE;
                },

                /*
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
