'use strict';

angular.module('tbs.controllers').controller('SetupGameCtrl',
    ['$scope', 'jtbGameCache', 'jtbPlayerService', 'jtbFacebook', '$http', '$state', '$location', '$ionicModal', '$ionicSideMenuDelegate', // 'twAds',
        function ($scope, jtbGameCache, jtbPlayerService, jtbFacebook, $http, $state, $location, $ionicModal, $ionicSideMenuDelegate /*, twAds*/) {
            $ionicSideMenuDelegate.canDragContent(false);
            $scope.theme = 'default';
            $scope.gameID = $state.params.gameID;
            $scope.game = jtbGameCache.getGameForID($scope.gameID);
            $scope.ships = [];

            $scope.gameWidth = 1000; // //$scope.game.gridSize * 100
            $scope.gameHeight = 1000; // //$scope.game.gridSize * 100
            $scope.gameScale = 0.5;
            $scope.movingShip = null;

            $scope.phaser = new Phaser.Game(
                $scope.gameWidth,
                $scope.gameHeight,
                Phaser.AUTO,
                'phaser',
                {preload: preload, create: create});
            function preload() {
                $scope.phaser.load.tilemap('grid', '/templates/gamefiles/10x10.json', null, Phaser.Tilemap.TILED_JSON);
                $scope.phaser.load.image('tile', '/images/default/tile.png');
                $scope.phaser.load.image('destroyer', '/images/default/destroyer.png');
                $scope.phaser.load.image('submarine', '/images/default/submarine.png');
            }


            function create() {
                var map = $scope.phaser.add.tilemap('grid');
                map.addTilesetImage('tile');
                var layer = map.createLayer('base grid');
                layer.resizeWorld();
                angular.forEach(['destroyer', 'submarine'], function (shipType) {
                    var ship = $scope.phaser.add.sprite(300, 300, shipType, 0);
                    $scope.ships.push({sprite: ship});
                });
                //$scope.phaser.input.onTap.add(onTap);
                $scope.phaser.input.addMoveCallback(onMove);
                $scope.phaser.input.onDown.add(onDown);
                $scope.phaser.input.onUp.add(onUp);

                $scope.phaser.width = $scope.gameWidth * $scope.gameScale;
                $scope.phaser.height = $scope.gameHeight * $scope.gameScale;
            }

            function onUp() {
                if ($scope.movingShip != null) {
                    console.log('D' + $scope.phaser.input.mousePointer.x + '/' + $scope.phaser.input.mousePointer.y);
                    $scope.movingShip = null;
                }
            }

            function onMove() {
                if ($scope.movingShip != null) {
                    console.log('M' + $scope.phaser.input.mousePointer.x + '/' + $scope.phaser.input.mousePointer.y);
                }
            }

            function onDown() {
                $scope.movingShip = $scope.ships[0];
                console.log('U' + $scope.phaser.input.mousePointer.x + '/' + $scope.phaser.input.mousePointer.y);
            }

            function onTap(pointer, double) {
                if (double) {
                    var vertical = ($scope.ships[1].angle === 90);
                    var x = $scope.ships[1].x + (vertical ? -100 : 100);
                    var y = $scope.ships[1].y;
                    $scope.ships[1].angle = vertical ? 0 : 90;
                    $scope.ships[1].position.setTo(x, y);
                }
                console.log($scope.ships[1].x + '/' + $scope.ships[1].y);
                console.log(pointer.x + '/' + pointer.y);
            }
        }
    ]
);