'use strict';

var GRID_SIZE = 100;
var HALF_GRID = GRID_SIZE / 2;

angular.module('tbs.controllers').controller('SetupGameCtrl',
    ['$scope', 'jtbGameCache', 'jtbPlayerService', 'jtbFacebook', '$http', '$state', '$location', '$ionicModal', '$ionicSideMenuDelegate', 'tbsShips', // 'twAds',
        function ($scope, jtbGameCache, jtbPlayerService, jtbFacebook, $http, $state, $location, $ionicModal, $ionicSideMenuDelegate, tbsShips /*, twAds*/) {
            $ionicSideMenuDelegate.canDragContent(false);
            $scope.theme = 'default';
            $scope.gameID = $state.params.gameID;
            $scope.game = jtbGameCache.getGameForID($scope.gameID);
            $scope.ships = [];
            $scope.shipInfo = [];

            $scope.gameWidth = $scope.game.gridSize * GRID_SIZE;
            $scope.gameHeight = $scope.game.gridSize * GRID_SIZE;
            $scope.gameScale = 0.5;
            $scope.movingShip = null;
            $scope.movingPointerRelativeToShip = {x: 0, y: 0};


            function create() {
                var map = $scope.phaser.add.tilemap('grid');
                map.addTilesetImage('tile');
                var layer = map.createLayer('base grid');
                layer.resizeWorld();
                var row = 0;
                angular.forEach($scope.shipInfo, function (shipInfo) {
                    var halfShipGridLength = (GRID_SIZE * shipInfo.gridSize) / 2;
                    var centerX = 400 + halfShipGridLength;
                    var centerY = (row * GRID_SIZE) + HALF_GRID;
                    var ship = $scope.phaser.add.sprite(centerX, centerY, shipInfo.ship, 0);
                    ship.anchor.setTo(0.5, 0.5);

                    var shipData = {
                        sprite: ship,
                        info: shipInfo,
                        horizontal: true,
                        vertical: false
                    };
                    computeShipCorners(shipData);
                    $scope.ships.push(shipData);
                    row = row + 1;
                });
                $scope.phaser.width = $scope.gameWidth * $scope.gameScale;
                $scope.phaser.height = $scope.gameHeight * $scope.gameScale;
                $scope.phaser.world.resize($scope.gameWidth * $scope.gameScale, $scope.gameHeight * $scope.gameScale);

                $scope.phaser.input.onTap.add(onTap);
                $scope.phaser.input.addMoveCallback(onMove);
                $scope.phaser.input.onDown.add(onDown);
                $scope.phaser.input.onUp.add(onUp);

            }

            function computeShipCorners(shipData) {
                var halfShip = (GRID_SIZE * shipData.info.gridSize) / 2;
                shipData.centerX = shipData.sprite.x;
                shipData.centerY = shipData.sprite.y;
                if (shipData.horizontal) {
                    shipData.startX = shipData.centerX - halfShip;
                    shipData.startY = shipData.centerY - HALF_GRID;
                    shipData.endX = shipData.centerX + halfShip - 1;
                    shipData.endY = shipData.centerY + HALF_GRID - 1;
                } else {
                    shipData.startY = shipData.centerY - halfShip;
                    shipData.startX = shipData.centerX - HALF_GRID;
                    shipData.endY = shipData.centerY + halfShip - 1;
                    shipData.endX = shipData.centerX + HALF_GRID - 1;
                }
            }

            function roundPosition(shipData) {
                var xMod = shipData.startX % GRID_SIZE;
                if (xMod !== 0) {
                    if (xMod < HALF_GRID) {
                        shipData.sprite.x = shipData.sprite.x - xMod;
                    } else {
                        shipData.sprite.x = shipData.sprite.x - xMod + GRID_SIZE;
                    }
                }
                var yMod = shipData.startY % GRID_SIZE;
                if (yMod !== 0) {
                    if (yMod < HALF_GRID) {
                        shipData.sprite.y = shipData.sprite.y - yMod;
                    } else {
                        shipData.sprite.y = shipData.sprite.y - yMod + GRID_SIZE;
                    }
                }
                computeShipCorners(shipData);
                if (shipData.startX < 0) {
                    shipData.sprite.x -= shipData.startX;
                }
                if (shipData.endX >= $scope.gameWidth) {
                    shipData.sprite.x -= (shipData.endX - $scope.gameWidth + 1);
                }
                if (shipData.startY < 0) {
                    shipData.sprite.y -= shipData.startY;
                }
                if (shipData.endY >= $scope.gameHeight) {
                    shipData.sprite.y -= (shipData.endY - $scope.gameHeight + 1);
                }
                computeShipCorners(shipData);
                checkOverlap();
            }

            function checkOverlap() {
                var overlap = false;
                var overlaps = [];
                angular.forEach($scope.ships, function () {
                    overlaps.push(false);
                });

                for (var i = 0; i < $scope.ships.length; ++i) {
                    var outerShip = $scope.ships[i];
                    for (var j = i + 1; j < $scope.ships.length; ++j) {
                        var innerShip = $scope.ships[j];
                        if (outerShip.startX > innerShip.endX ||
                            outerShip.endX < innerShip.startX ||
                            outerShip.startY > innerShip.endY ||
                            outerShip.endY < innerShip.startY) {
                            continue;
                        }

                        overlap = true;
                        overlaps[i] = true;
                        overlaps[j] = true;
                    }
                    if (overlaps[i] === true) {
                        $scope.ships[i].sprite.tint = 0xff0000;
                    } else {
                        $scope.ships[i].sprite.tint = 0xffffff;
                    }
                }
                //  TODO - set overlap button
            }

            function onUp() {
                if ($scope.movingShip !== null) {
                    $scope.movingShip.sprite.tint = 0xffffff;
                    roundPosition($scope.movingShip);
                    $scope.movingShip = null;
                }
            }

            function onMove() {
                if ($scope.movingShip !== null) {
                    var x = $scope.phaser.input.mousePointer.x / $scope.gameScale;
                    var y = $scope.phaser.input.mousePointer.y / $scope.gameScale;
                    $scope.movingShip.sprite.x = x + $scope.movingPointerRelativeToShip.x;
                    $scope.movingShip.sprite.y = y + $scope.movingPointerRelativeToShip.y;
                    computeShipCorners($scope.movingShip);
                }
            }

            function onDown() {
                var x = $scope.phaser.input.mousePointer.x / $scope.gameScale;
                var y = $scope.phaser.input.mousePointer.y / $scope.gameScale;
                for (var i = 0; i < $scope.ships.length; ++i) {
                    if (x >= $scope.ships[i].startX && y >= $scope.ships[i].startY && x < $scope.ships[i].endX && y < $scope.ships[i].endY) {
                        $scope.movingShip = $scope.ships[i];
                        break;
                    }
                }
                if ($scope.movingShip !== null) {
                    $scope.movingPointerRelativeToShip.x = $scope.movingShip.centerX - x;
                    $scope.movingPointerRelativeToShip.y = $scope.movingShip.centerY - y;
                    $scope.movingShip.sprite.tint = 0x00ff00;
                }
            }

            function onTap(pointer, double) {
                if (double) {
                    var ship = null;
                    var x = $scope.phaser.input.mousePointer.x / $scope.gameScale;
                    var y = $scope.phaser.input.mousePointer.y / $scope.gameScale;
                    for (var i = 0; i < $scope.ships.length; ++i) {
                        if (x >= $scope.ships[i].startX && y >= $scope.ships[i].startY && x < $scope.ships[i].endX && y < $scope.ships[i].endY) {
                            ship = $scope.ships[i];
                            break;
                        }
                    }

                    if (ship) {
                        ship.vertical = !(ship.vertical);
                        ship.horizontal = !(ship.horizontal);
                        computeShipCorners(ship);
                        roundPosition(ship);
                        ship.sprite.angle = ship.vertical ? 90 : 0;
                    }
                }
            }

            function preload() {
                var json = $scope.gameHeight === 1000 ? '10x10.json' : $scope.gameHeight === 2000 ? '20x20.json' : '15x15.json';
                $scope.phaser.load.tilemap('grid', '/templates/gamefiles/' + json, null, Phaser.Tilemap.TILED_JSON);
                $scope.phaser.load.image('tile', '/images/default/tile.png');
                $scope.phaser.load.image('Destroyer', '/images/default/destroyer.png');
                $scope.phaser.load.image('Submarine', '/images/default/submarine.png');

                // TODO
                $scope.phaser.load.image('Carrier', '/images/default/destroyer.png');
                $scope.phaser.load.image('Battleship', '/images/default/destroyer.png');
                $scope.phaser.load.image('Cruiser', '/images/default/destroyer.png');
            }

            tbsShips.ships().then(
                function (ships) {
                    $scope.shipInfo = ships;
                    $scope.phaser = new Phaser.Game(
                        $scope.gameWidth,
                        $scope.gameHeight,
                        Phaser.AUTO,
                        'phaser',
                        {preload: preload, create: create});
                },
                function () {
                    //  TODO
                }
            );

        }
    ]
);