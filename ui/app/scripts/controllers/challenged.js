'use strict';

angular.module('tbs.controllers').controller('ChallengedCtrl',
    ['$scope', 'tbsGameDetails', 'jtbGameCache', 'jtbGameFeatureService', 'jtbPlayerService', 'jtbFacebook', '$http', '$state', '$location', '$ionicModal',// 'twAds',
        function ($scope, tbsGameDetails, jtbGameCache, jtbGameFeatureService, jtbPlayerService, jtbFacebook, $http, $state, $location, $ionicModal/*, twAds*/) {
            var yes = 'checkmark';
            var no = 'close';

            $scope.gameID = $state.params.gameId;
            $scope.gameDetails = tbsGameDetails;

            function initialize() {
                $scope.game = jtbGameCache.getGameForID($scope.gameID);
                $scope.player = jtbPlayerService.currentPlayer();
                $scope.ecmEnabled = $scope.game.features.indexOf('ECMEnabled') >= 0 ? yes : no;
                $scope.spyingEnabled = $scope.game.features.indexOf('SpyEnabled') >= 0 ? yes : no;
                $scope.repairsEnabled = $scope.game.features.indexOf('EREnabled') >= 0 ? yes : no;
                $scope.moveEnabled = $scope.game.features.indexOf('EMEnabled') >= 0 ? yes : no;
                $scope.criticalsEnabled = $scope.game.features.indexOf('CriticalEnabled') >= 0 ? yes : no;
                $scope.gridSize = $scope.game.features.indexOf('Grid10x10') >= 0 ? '10x10' : $scope.game.features.indexOf('Grid15x15') >= 0 ? '15x15' : '20x20';
                $scope.intel = $scope.game.features.indexOf('IsolatedIntel') >= 0 ? 'Isolated' : 'Shared';
                $scope.moves = $scope.game.features.indexOf('Single') >= 0 ? '1' : 'Per Ship';
            }

            $scope.accept = function () {
//  TODO
//                twAds.showAdPopup().result.then(function () {
                $http.put(jtbPlayerService.currentPlayerBaseURL() + '/game/' + $scope.gameID + '/accept').success(function (data) {
                    //twGameDisplay.processGameUpdateForScope($scope, data);
                }).error(function (data, status, headers, config) {
                    //showMessage(data);
                    console.error(data + status + headers + config);
                });
//                });
            };

            $scope.reject = function () {
//                var modal = showConfirmDialog();
                //               modal.result.then(function () {
                $http.put(jtbPlayerService.currentPlayerBaseURL() + '/game/' + $scope.gameID + '/reject').success(function (data) {
                    //twGameDisplay.processGameUpdateForScope($scope, data);
                }).error(function (data, status, headers, config) {
                    //showMessage(data);
                    console.error(data + status + headers + config);
                });
//                });
            };

            initialize();
        }
    ]
);