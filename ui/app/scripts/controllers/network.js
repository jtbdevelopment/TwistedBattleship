'use strict';

angular.module('tbs.controllers')
    //  TODO - finish and move to core?
    .controller('NetworkCtrl',
    ['$scope', '$state', '$ionicHistory', '$rootScope', '$cordovaNetwork', '$timeout',
        function ($scope, $state, $ionicHistory, $rootScope, $cordovaNetwork, $timeout) {

            function online() {
                $ionicHistory.nextViewOptions({
                    disableBack: true
                });
                $state.go('signin', {}, {reload: true});
            }

            function checkOnline() {
                $scope.message = 'Checking network status...';
                //  As Per http://stackoverflow.com/questions/25672502/phonegap-network-connection-cannot-read-property-type-of-undefined
                //  Need a timeout to ensure its initialized
                $timeout(function () {
                    try {
                        if ($cordovaNetwork.isOnline()) {
                            online();
                        }
                    } catch (error) {
                        if (error.message === 'navigator.connection is undefined') {
                            //  Assume a browser and go
                            online();
                            return;
                        }
                        console.log(error);
                    }
                    $scope.message = 'Internet not currently available.';
                }, 1000);
            }

            $scope.$on('$cordovaNetwork:online', function () {
                online();
            });

            $scope.$on('$ionicView.enter', function () {
                checkOnline();
            });

            checkOnline();
        }
    ]
);