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
            $scope.submitDisabled = true;

            $scope.submit = function () {
                var info = {};
                angular.forEach($scope.ships, function (ship) {
                    var cells = [];
                    var startX = ship.startX / GRID_SIZE;
                    var startY = ship.startY / GRID_SIZE;
                    if (ship.horizontal) {
                        for (var i = 0; i < ship.info.gridSize; ++i) {
                            cells.push({column: startX + i, row: startY});
                        }
                    } else {
                        for (var i = 0; i < ship.info.gridSize; ++i) {
                            cells.push({column: startX, row: startY + i});
                        }
                    }
                    info[ship.info.ship] = cells;
                });

                $http.put(jtbPlayerService.currentPlayerBaseURL() + '/game/' + $scope.gameID + '/setup', info).success(function (data) {
                    jtbGameCache.putUpdatedGame(data);
                    $state.go('app.game', {gameID: data.id});
                }).error(function (data, status, headers, config) {
                    //  TODO
                    console.error(data + status + headers + config);
                });
            };

            function placeShip(horizontal, row, column, shipInfo) {
                var centerX, centerY;
                if (horizontal) {
                    centerY = (row * GRID_SIZE) + HALF_GRID;
                    centerX = (column * GRID_SIZE) + (shipInfo.gridSize * HALF_GRID);
                } else {
                    centerX = (column * GRID_SIZE) + HALF_GRID;
                    centerY = (row * GRID_SIZE) + (shipInfo.gridSize * HALF_GRID);
                }
                var ship = $scope.phaser.add.sprite(centerX, centerY, shipInfo.ship, 0);
                ship.anchor.setTo(0.5, 0.5);
                ship.angle = horizontal ? 0 : 90;

                var shipData = {
                    sprite: ship,
                    info: shipInfo,
                    horizontal: horizontal
                };
                computeShipCorners(shipData);
                $scope.ships.push(shipData);
            }

            function placeShips() {
                angular.forEach($scope.game.maskedPlayersState.shipStates, function (value, key) {
                    var shipInfo = $scope.shipInfo.find(function (info) {
                        return info.ship === key;
                    });
                    var horizontal = value.shipGridCells[0].y === value.shipGridCells[1].y;
                    var row = value.shipGridCells[0].y;
                    var column = value.shipGridCells[0].x;
                    placeShip(horizontal, row, column, shipInfo);
                });
                if ($scope.ships.length === 0) {
                    var row = 0;
                    angular.forEach($scope.shipInfo, function (shipInfo) {
                        placeShip(true, row, 4, shipInfo);
                        row = row + 1;
                    });
                }
                checkOverlap();
            }

            function create() {
                var map = $scope.phaser.add.tilemap('grid');
                map.addTilesetImage('tile');
                var layer = map.createLayer('base grid');
                layer.resizeWorld();
                placeShips();
                $scope.phaser.width = $scope.gameWidth * $scope.gameScale;
                $scope.phaser.height = $scope.gameHeight * $scope.gameScale;
                $scope.phaser.world.resize($scope.gameWidth * $scope.gameScale, $scope.gameHeight * $scope.gameScale);

                $scope.phaser.input.onTap.add(onTap);
                $scope.phaser.input.addMoveCallback(onMove);
                $scope.phaser.input.onDown.add(onDown);
                $scope.phaser.input.onUp.add(onUp);

                //  TODO  - look at more
                //$scope.phaser.scale.fullScreenScaleMode = Phaser.ScaleManager.NO_SCALE;
                //$scope.phaser.scale.startFullScreen(false);
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

            function moveOutOfBoundShip(shipData) {
                if (shipData.startX < 0) {
                    shipData.sprite.x -= shipData.startX;
                    computeShipCorners(shipData);
                }
                if (shipData.endX >= $scope.gameWidth) {
                    shipData.sprite.x -= (shipData.endX - $scope.gameWidth + 1);
                    computeShipCorners(shipData);
                }
                if (shipData.startY < 0) {
                    shipData.sprite.y -= shipData.startY;
                    computeShipCorners(shipData);
                }
                if (shipData.endY >= $scope.gameHeight) {
                    shipData.sprite.y -= (shipData.endY - $scope.gameHeight + 1);
                    computeShipCorners(shipData);
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
                moveOutOfBoundShip(shipData);
            }

            function checkOverlap() {
                var overlapExists = false;
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

                        overlapExists = true;
                        overlaps[i] = true;
                        overlaps[j] = true;
                    }
                    if (overlaps[i] === true) {
                        $scope.ships[i].sprite.tint = 0xff0000;
                    } else {
                        $scope.ships[i].sprite.tint = 0xffffff;
                    }
                }
                $scope.submitDisabled = overlapExists;
                $scope.$apply();
            }

            function onUp() {
                if ($scope.movingShip !== null) {
                    $scope.movingShip.sprite.tint = 0xffffff;
                    roundPosition($scope.movingShip);
                    checkOverlap();
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

            function onDown(pointer) {
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
                        ship.horizontal = !(ship.horizontal);
                        computeShipCorners(ship);
                        roundPosition(ship);
                        checkOverlap();
                        ship.sprite.angle = ship.horizontal ? 0 : 90;
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