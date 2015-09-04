/*global Phaser:false */
'use strict';

angular.module('tbs.services').factory('tbsShipGrid',
    ['jtbPlayerService', 'tbsCircles', 'tbsCellStates', 'tbsShips',
        function (jtbPlayerService, tbsCircles, tbsCellStates, tbsShips) {
            var CELL_SIZE = 100;
            var HALF_CELL_SIZE = CELL_SIZE / 2;

            var circles;
            var circle;
            var circleSprites = [];
            var cellStates;
            var shipNames;
            var theme;
            var game;
            var shipsOnGrid = [], shipLocations = [];
            var cellMarkers = [], markersOnGrid = [];
            var highlightSprite = null, highlightX, highlightY, highlightShip = null;
            var gameWidth, gameHeight;
            var phaser;

            var postCreateFunction;
            var highlightCallback;

            function preload() {
                circle = [];
                var json;
                switch (gameHeight) {
                    case 1000:
                        json = '10x10.json';
                        angular.forEach(circles['10'], function (coord) {
                            circle.push(coord);
                        });
                        break;
                    case 1500:
                        angular.forEach(circles['10'], function (coord) {
                            circle.push(coord);
                        });
                        angular.forEach(circles['15'], function (coord) {
                            circle.push(coord);
                        });
                        json = '15x15.json';
                        break;
                    case 2000:
                        angular.forEach(circles['10'], function (coord) {
                            circle.push(coord);
                        });
                        angular.forEach(circles['15'], function (coord) {
                            circle.push(coord);
                        });
                        angular.forEach(circles['20'], function (coord) {
                            circle.push(coord);
                        });
                        json = '20x20.json';
                        break;
                }
                circle.splice(0, 1);
                angular.forEach(circleSprites, function (circleSprite) {
                    circleSprite.destroy();
                });
                circleSprites = [];

                if (highlightSprite !== null) {
                    highlightSprite.destroy();
                }
                highlightSprite = null;
                highlightCallback = null;
                highlightX = -1;
                highlightY = -1;
                phaser.load.tilemap('grid', 'templates/gamefiles/' + json, null, Phaser.Tilemap.TILED_JSON);
                phaser.load.image('tile', 'images/' + theme + '/tile.png');
                phaser.load.image('highlight', 'images/' + theme + '/highlight.png');
                phaser.load.image('extendedhighlight', 'images/' + theme + '/extendedhighlight.png');
                angular.forEach(cellStates, function (state) {
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

                placeShips();
                placeCellMarkers();

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

            function currentContextCoordinates(context) {
                var x = context.worldX;
                var y = context.worldY;
                return {x: x, y: y};
            }

            function highlightAShip(x, y) {
                if (highlightShip !== null) {
                    highlightShip.sprite.tint = 0xffffff;
                }
                highlightShip = findShipByCoordinates({x: x, y: y});
                if (highlightShip !== null) {
                    highlightShip.sprite.tint = 0x00ff00;
                }
            }

            function highlightCell(context) {
                var coords = currentContextCoordinates(context);
                var y = Math.floor(coords.y / CELL_SIZE) * CELL_SIZE;
                var x = Math.floor(coords.x / CELL_SIZE) * CELL_SIZE;
                if (highlightSprite === null) {
                    highlightSprite = phaser.add.sprite(x, y, 'highlight', 0);
                    angular.forEach(circle, function () {
                        circleSprites.push(phaser.add.sprite(x, y, 'extendedhighlight', 0));
                    });
                }
                highlightSprite.x = x;
                highlightSprite.y = y;
                highlightX = x / CELL_SIZE;
                highlightY = y / CELL_SIZE;
                for (var i = 0; i < circle.length; ++i) {
                    var coord = circle[i];
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
                stop: function () {
                    if (angular.isDefined(phaser)) {
                        phaser.destroy();
                        phaser = null;
                    }
                },
                initialize: function (loadedGame, initialShipLocations, initialCellMarkers, postCreateCB) {
                    theme = jtbPlayerService.currentPlayer().gameSpecificPlayerAttributes.theme;
                    game = loadedGame;
                    gameWidth = game.gridSize * CELL_SIZE;
                    gameHeight = game.gridSize * CELL_SIZE;
                    shipLocations = initialShipLocations;
                    cellMarkers = initialCellMarkers;
                    postCreateFunction = postCreateCB;
                    tbsShips.ships().then(function (shipData) {
                        shipNames = [];
                        angular.forEach(shipData, function (singleShip) {
                            shipNames.push(singleShip.ship);
                        });
                        tbsCellStates.cellStates().then(function (stateData) {
                            cellStates = stateData;
                            tbsCircles.circles().then(
                                function (circleData) {
                                    circles = circleData;
                                    phaser = new Phaser.Game(
                                        gameWidth,
                                        gameHeight,
                                        Phaser.AUTO,
                                        'phaser',
                                        {preload: preload, create: create, init: init});
                                },
                                function (error) {
                                    //  TODO
                                    console.warn(error);
                                }
                            );
                        });
                    });
                },

                placeShips: function (newShipLocations) {
                    shipLocations = newShipLocations;
                    placeShips();
                    highlightAShip(highlightX * CELL_SIZE, highlightY * CELL_SIZE);
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
                    this.onDown(highlightCell);
                },
                deactivateHighlighting: function () {
                    highlightCallback = undefined;
                    this.onDown(undefined);
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
            };
        }
    ]
);
