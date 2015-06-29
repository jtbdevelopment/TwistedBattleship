'use strict';

angular.module('tbs.controllers').controller('SetupGameCtrl',
    ['$scope', 'jtbGameCache', 'jtbPlayerService', 'jtbFacebook', '$http', '$state', '$location', '$ionicModal',// 'twAds',
        function ($scope, jtbGameCache, jtbPlayerService, jtbFacebook, $http, $state, $location, $ionicModal/*, twAds*/) {
            $scope.theme = 'default';
            $scope.gameID = $state.params.gameID;
            $scope.game = jtbGameCache.getGameForID($scope.gameID);

            $scope.phaser = new Phaser.Game(
                $scope.game.gridSize * 100,
                $scope.game.gridSize * 100,
                Phaser.AUTO,
                'phaser',
                {preload: preload, create: create});
            function preload() {
                $scope.phaser.load.tilemap('grid', '/templates/gamefiles/10x10.json', null, Phaser.Tilemap.TILED_JSON);
                $scope.phaser.load.image('tile', '/images/default/tile.png');
                $scope.phaser.load.image('destroyer', '/images/default/destroyer.png');
                $scope.phaser.load.image('knownbyhit', '/images/default/knownbyhit.png');
            }


            function create() {
                var map = $scope.phaser.add.tilemap('grid');
                map.addTilesetImage('tile');
                var layer = map.createLayer('base grid');
                layer.resizeWorld();
                var ship = $scope.phaser.add.sprite(0, 0, 'destroyer', 0);
                ship.inputEnabled = true;
                ship.input.enableDrag();
                //layer.resizeWorld();                //$scope.phaser.scale.scaleMode = Phaser.ScaleManager.USER_SCALE;
                //$scope.phaser.camera.scale.x = 0.50;
                //$scope.phaser.camera.scale.y = 0.50;
            }


        }
    ]
);