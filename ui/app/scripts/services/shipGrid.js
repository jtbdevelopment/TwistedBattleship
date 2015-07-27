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
            var gameWidth, gameHeight, gameScale;
            var phaser;

            var postCreateFunction;

            function preload() {
                var json = gameHeight === 1000 ? '10x10.json' : gameHeight === 2000 ? '20x20.json' : '15x15.json';
                phaser.load.tilemap('grid', '/templates/gamefiles/' + json, null, Phaser.Tilemap.TILED_JSON);
                phaser.load.image('tile', '/images/' + theme + '/tile.png');
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
                initialize: function (loadedGame, initialShipLocations, postCreateCB) {
                    theme = 'default';
                    game = loadedGame;
                    gameWidth = game.gridSize * CELL_SIZE;
                    gameHeight = game.gridSize * CELL_SIZE;
                    gameScale = 0.5;
                    shipLocations = initialShipLocations;
                    postCreateFunction = postCreateCB;
                    phaser = new Phaser.Game(
                        gameWidth,
                        gameHeight,
                        Phaser.AUTO,
                        'phaser',
                        {preload: preload, create: create});
                },

                placeShips: function (initialShipLocations) {
                    shipLocations = initialShipLocations;
                    placeShips();
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
                computeShipCorners: function (shipData) {
                    computeShipCorners(shipData);
                },

                currentMouseCoordinates: function () {
                    var x = phaser.input.mousePointer.x / gameScale;
                    var y = phaser.input.mousePointer.y / gameScale;
                    return {x: x, y: y};
                },
                findShipByMouseCoordinates: function () {
                    var coords = this.currentMouseCoordinates();
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