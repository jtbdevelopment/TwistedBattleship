'use strict';

angular.module('tbs.controllers').controller('PlayerDetailsCtrl',
    ['$scope', 'jtbPlayerService', '$http', '$ionicPopup', '$rootScope', '$timeout',
        function ($scope, jtbPlayerService, $http, $ionicPopup, $rootScope, $timeout) {
            $scope.data = {theme: 'default-theme'};

            $scope.changeTheme = function () {
                $http.put(jtbPlayerService.currentPlayerBaseURL() + '/changeTheme/' + $scope.data.theme)
                    .success(function (data) {
                        console.log(JSON.stringify(data));
                        $timeout(function () {
                            $rootScope.$broadcast('playerUpdate', data.id, data);
                        }, this);
                    })
                    .error(
                        function (data, status, headers, config) {
                            $ionicPopup.alert({
                                title: 'Error updating theme!',
                                template: data
                            });
                            console.error(data + status + headers + config);
                        });
            };

            $scope.$on('$ionicView.enter', function () {
                $scope.player = jtbPlayerService.currentPlayer();
                $scope.data.theme = $scope.player.gameSpecificPlayerAttributes.theme;
            });
        }
    ]
);