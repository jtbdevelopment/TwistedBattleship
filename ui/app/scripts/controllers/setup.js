'use strict';

var GRID_SIZE = 100;
var HALF_GRID = GRID_SIZE / 2;

angular.module('tbs.controllers').controller('SetupGameCtrl',
    ['$scope', 'jtbGameCache', 'jtbPlayerService', 'jtbFacebook', '$http', '$state', '$location', '$ionicModal', '$ionicSideMenuDelegate', 'tbsShips', // 'twAds',
        function ($scope, jtbGameCache, jtbPlayerService, jtbFacebook, $http, $state, $location, $ionicModal, $ionicSideMenuDelegate, tbsShips /*, twAds*/) {
            $ionicSideMenuDelegate.canDragContent(false);
            $scope.theme = 'default';
            $scope.gameID = $state.params.gameID;
//            $scope.game = jtbGameCache.getGameForID($scope.gameID);
            $scope.ships = [];
            $scope.shipInfo = [];

            $scope.gameWidth = 1000; // //$scope.game.gridSize * GRID_SIZE
            $scope.gameHeight = 1000; // //$scope.game.gridSize * GRID_SIZE
            $scope.gameScale = 0.5;
            $scope.movingShip = null;
            $scope.movingPointerRelativeToShip = {x: 0, y: 0};

            tbsShips.ships().then(
                function (ships) {
                    $scope.shipInfo = ships;
                    $scope.phaser = new Phaser.Game(
                        $scope.gameWidth,
                        $scope.gameHeight,
                        Phaser.AUTO,
                        'phaser',
                        {preload: preload, create: create});
                    function preload() {
                        $scope.phaser.load.tilemap('grid', '/templates/gamefiles/10x10.json', null, Phaser.Tilemap.TILED_JSON);
                        $scope.phaser.load.image('tile', '/images/default/tile.png');
                        $scope.phaser.load.image('Destroyer', '/images/default/destroyer.png');
                        $scope.phaser.load.image('Submarine', '/images/default/submarine.png');

                        // TODO
                        $scope.phaser.load.image('Carrier', '/images/default/destroyer.png');
                        $scope.phaser.load.image('Battleship', '/images/default/destroyer.png');
                        $scope.phaser.load.image('Cruiser', '/images/default/destroyer.png');
                    }
                },
                function () {
                    //  TODO
                }
            );


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
                        centerX: centerX,
                        centerY: centerY,
                        horizontal: true,
                        vertical: false
                    };
                    computeShipCorners(shipData);
                    console.log('ship ' + shipData.info.ship + ' ' + shipData.startX + '/' + shipData.startY + ' to ' + shipData.endX + '/' + shipData.endY);
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
                if (shipData.horizontal) {
                    shipData.startX = shipData.centerX - halfShip;
                    shipData.startY = shipData.centerY - HALF_GRID;
                    shipData.endX = shipData.centerX + halfShip;
                    shipData.endY = shipData.centerY + HALF_GRID;
                } else {
                    shipData.startY = shipData.centerY - halfShip;
                    shipData.startX = shipData.centerX - HALF_GRID;
                    shipData.endY = shipData.centerY + halfShip;
                    shipData.endX = shipData.centerX + HALF_GRID;
                }
            }

            function onUp() {
                if ($scope.movingShip != null) {
                    var x = $scope.phaser.input.mousePointer.x / $scope.gameScale;
                    var y = $scope.phaser.input.mousePointer.y / $scope.gameScale;
                    console.log('dropped ' + $scope.movingShip.info.ship + ' at ' + x + '/' + y + '(' + $scope.phaser.input.mousePointer.x + '/' + $scope.phaser.input.mousePointer.y + ')');
                    console.log($scope.movingShip.info.ship + ' at ' + $scope.movingShip.startX + '/' + $scope.movingShip.startY + ' to ' + $scope.movingShip.endX + '/' + $scope.movingShip.endY + ')');
                    $scope.movingShip.sprite.tint = 0xffffff;
                    $scope.movingShip = null;
                }
            }

            function onMove() {
                if ($scope.movingShip != null) {
                    var x = $scope.phaser.input.mousePointer.x / $scope.gameScale;
                    var y = $scope.phaser.input.mousePointer.y / $scope.gameScale;
                    var centerX = x + $scope.movingPointerRelativeToShip.x;
                    var centerY = y + $scope.movingPointerRelativeToShip.y;
                    $scope.movingShip.sprite.x = centerX;
                    $scope.movingShip.sprite.y = centerY;
                    $scope.movingShip.centerX = centerX;
                    $scope.movingShip.centerY = centerY;
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
                if ($scope.movingShip != null) {
                    $scope.movingPointerRelativeToShip.x = $scope.movingShip.centerX - x;
                    $scope.movingPointerRelativeToShip.y = $scope.movingShip.centerY - y;
                    $scope.movingShip.sprite.tint = 0xff00ff;
                    console.log('picked up ' + $scope.movingShip.info.ship + ' at ' + x + '/' + y + '(' + $scope.phaser.input.mousePointer.x + '/' + $scope.phaser.input.mousePointer.y + ')');
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
                        console.log('double tapped ' + ship.info.ship + ' at ' + x + '/' + y + '(' + $scope.phaser.input.mousePointer.x + '/' + $scope.phaser.input.mousePointer.y + ')');
                        console.log(ship.info.ship + ' at ' + ship.startX + '/' + ship.startY + ' to ' + ship.endX + '/' + ship.endY + ')');
                        ship.vertical = !(ship.vertical);
                        ship.horizontal = !(ship.horizontal);
                        computeShipCorners(ship);
                        ship.sprite.angle = ship.vertical ? 90 : 0;
                        console.log(ship.info.ship + ' at ' + ship.startX + '/' + ship.startY + ' to ' + ship.endX + '/' + ship.endY + ')');
                    }
                }
            }
        }
    ]
);