'use strict';

angular.module('tbs.controllers')
    //  TODO - finish and move to core?
    .controller('NetworkCtrl',
    ['$scope', '$state', '$ionicHistory', '$rootScope', '$cordovaNetwork', '$timeout',
        function ($scope, $state, $ionicHistory, $rootScope, $cordovaNetwork, $timeout) {
            $scope.message = 'Checking network status...';

            function online() {
                $ionicHistory.nextViewOptions({
                    disableBack: true
                });
                $state.go('signin', {}, {reload: true});
            }

            $scope.$on('$cordovaNetwork:online', function () {
                online();
            });

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
                    }
                    console.log(error);
                }
            }, 1000);

            $scope.message = 'Internet not currently available.';

        }
    ]
);