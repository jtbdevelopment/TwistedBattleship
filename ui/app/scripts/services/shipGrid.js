/*global Phaser:false */
'use strict';

angular.module('tbs.services').factory('tbsShipGrid',
    [
        function () {
            var CELL_SIZE = 100;
            var HALF_CELL_SIZE = CELL_SIZE / 2;

            var theme;
            var game;
            var shipsOnGrid = [], shipLocations = [];
            var cellMarkers = [], markersOnGrid = [];
            var highlightSprite = null, highlightX, highlightY;
            var gameWidth, gameHeight, gameScale;
            var phaser;

            var postCreateFunction;
            var highlightCallback;

            function preload() {
                var json = gameHeight === 1000 ? '10x10.json' : gameHeight === 2000 ? '20x20.json' : '15x15.json';
                if (highlightSprite !== null) {
                    highlightSprite.destroy();
                }
                highlightSprite = null;
                highlightCallback = null;
                highlightX = -1;
                highlightY = -1;
                phaser.load.tilemap('grid', '/templates/gamefiles/' + json, null, Phaser.Tilemap.TILED_JSON);
                phaser.load.image('tile', '/images/' + theme + '/tile.png');
                phaser.load.image('highlight', '/images/' + theme + '/highlight.png');
                angular.forEach([
                    'knownbyhit',
                    'knownbymiss',
                    'knownbyothermiss',
                    'knownbyotherhit',
                    'knownbyrehit',
                    'knownship',
                    'knownempty',
                    'obscuredempty',
                    'obscuredship',
                    'unknown'
                ], function (state) {
                    phaser.load.image(state, '/images/' + theme + '/' + state + '.png');
                });
                phaser.load.image('Destroyer', '/images/' + theme + '/destroyer.png');
                phaser.load.image('Submarine', '/images/' + theme + '/submarine.png');

                // TODO
                phaser.load.image('Carrier', '/images/' + theme + '/destroyer.png');
                phaser.load.image('Battleship', '/images/' + theme + '/destroyer.png');
                phaser.load.image('Cruiser', '/images/' + theme + '/destroyer.png');
            }

            function create() {
                var map = phaser.add.tilemap('grid');
                map.addTilesetImage('tile');
                var layer = map.createLayer('base grid');
                layer.resizeWorld();
                phaser.width = gameWidth * gameScale;
                phaser.height = gameHeight * gameScale;
                phaser.world.resize(gameWidth * gameScale, gameHeight * gameScale);

                placeShips();
                placeCellMarkers();

                //  TODO  - look at more
                //phaser.scale.fullScreenScaleMode = Phaser.ScaleManager.NO_SCALE;
                //phaser.scale.startFullScreen(false);

                if (postCreateFunction !== null) {
                    postCreateFunction();
                }
            }

            function computeShipCorners(shipData) {
                var halfShip = (CELL_SIZE * shipData.info.gridSize) / 2;
                shipData.centerX = shipData.sprite.x;
                shipData.centerY = shipData.sprite.y;
                if (shipData.horizontal) {
                    shipData.startX = shipData.centerX - halfShip;
                    shipData.startY = shipData.centerY - HALF_CELL_SIZE;
                    shipData.endX = shipData.centerX + halfShip - 1;
                    shipData.endY = shipData.centerY + HALF_CELL_SIZE - 1;
                } else {
                    shipData.startY = shipData.centerY - halfShip;
                    shipData.startX = shipData.centerX - HALF_CELL_SIZE;
                    shipData.endY = shipData.centerY + halfShip - 1;
                    shipData.endX = shipData.centerX + HALF_CELL_SIZE - 1;
                }
            }

            function placeCellMarkers() {
                angular.forEach(markersOnGrid, function (markersOnGridRow) {
                    angular.forEach(markersOnGridRow, function (markersOnGridCell) {
                        markersOnGridCell.destroy();
                    });
                });
                markersOnGrid = [];
                var row = 0;
                angular.forEach(cellMarkers, function (cellMarkerRow) {
                    var rowArray = [];
                    markersOnGrid.push(rowArray);
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

            function placeShip(horizontal, row, column, shipInfo) {
                var centerX, centerY;
                if (horizontal) {
                    centerY = (row * CELL_SIZE) + HALF_CELL_SIZE;
                    centerX = (column * CELL_SIZE) + (shipInfo.gridSize * HALF_CELL_SIZE);
                } else {
                    centerX = (column * CELL_SIZE) + HALF_CELL_SIZE;
                    centerY = (row * CELL_SIZE) + (shipInfo.gridSize * HALF_CELL_SIZE);
                }
                var ship = phaser.add.sprite(centerX, centerY, shipInfo.ship, 0);
                ship.anchor.setTo(0.5, 0.5);
                ship.angle = horizontal ? 0 : 90;

                var shipOnGrid = {
                    sprite: ship,
                    info: shipInfo,
                    horizontal: horizontal
                };
                computeShipCorners(shipOnGrid);
                shipsOnGrid.push(shipOnGrid);
            }

            function currentMouseCoordinates() {
                var x = phaser.input.mousePointer.x / gameScale;
                var y = phaser.input.mousePointer.y / gameScale;
                return {x: x, y: y};
            }

            function highlightCell() {
                var coords = currentMouseCoordinates();
                var y = Math.floor(coords.y / CELL_SIZE) * CELL_SIZE;
                var x = Math.floor(coords.x / CELL_SIZE) * CELL_SIZE;
                if (highlightSprite === null) {
                    highlightSprite = phaser.add.sprite(x, y, 'highlight', 0);
                }
                if (y !== highlightY || x !== highlightX) {
                    highlightSprite.x = x;
                    highlightSprite.y = y;
                    highlightX = x / CELL_SIZE;
                    highlightY = y / CELL_SIZE;
                }
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

            function placeShips() {
                angular.forEach(shipsOnGrid, function (ship) {
                    ship.sprite.destroy();
                });
                shipsOnGrid = [];
                angular.forEach(shipLocations, function (shipLocation) {
                    placeShip(shipLocation.horizontal, shipLocation.row, shipLocation.column, shipLocation.shipInfo);
                });
            }

            return {
                initialize: function (useTheme, loadedGame, initialShipLocations, initialCellMarkers, postCreateCB) {
                    theme = useTheme;
                    game = loadedGame;
                    gameWidth = game.gridSize * CELL_SIZE;
                    gameHeight = game.gridSize * CELL_SIZE;
                    gameScale = 0.5;
                    shipLocations = initialShipLocations;
                    cellMarkers = initialCellMarkers;
                    postCreateFunction = postCreateCB;
                    phaser = new Phaser.Game(
                        gameWidth,
                        gameHeight,
                        Phaser.AUTO,
                        'phaser',
                        {preload: preload, create: create});
                },

                placeShips: function (newShipLocations) {
                    shipLocations = newShipLocations;
                    placeShips();
                },

                placeCellMarkers: function (newCellMarkers) {
                    cellMarkers = newCellMarkers;
                    placeCellMarkers();
                },

                currentShipsOnGrid: function () {
                    return shipsOnGrid;
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
                halfCellSize: function () {
                    return HALF_CELL_SIZE;
                },

                activateHighlighting: function (highlightCB) {
                    highlightCallback = highlightCB;
                    this.onTap(highlightCell);
                },
                selectedCell: function () {
                    return {x: highlightX, y: highlightY};
                },
                selectedShip: function () {
                    return findShipByCoordinates({x: highlightX * CELL_SIZE, y: highlightY * CELL_SIZE});
                },

                computeShipCorners: function (shipData) {
                    computeShipCorners(shipData);
                },

                currentMouseCoordinates: function () {
                    return currentMouseCoordinates();
                },

                findShipByMouseCoordinates: function () {
                    return findShipByCoordinates(currentMouseCoordinates());
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
            };
        }
    ]
);