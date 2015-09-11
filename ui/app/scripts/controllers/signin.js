'use strict';

angular.module('tbs.controllers')
    //  TODO - finish and move to core
    //  TODO - investigate cordova facebook plugin instead of custom
    .controller('CoreMobileSignInCtrl',
    ['$scope', '$window', '$cookies', '$http', '$state', '$cacheFactory', '$ionicHistory', '$rootScope', 'jtbFacebook', 'ENV', '$ionicLoading',
        function ($scope, $window, $cookies, $http, $state, $cacheFactory, $ionicHistory, $rootScope, jtbFacebook, ENV, $ionicLoading) {
            $scope.message = 'Initializing...';
            $scope.showFacebook = false;
            $scope.showManual = false;
            $scope.csrf = $cookies['XSRF-TOKEN'];
            $scope.facebookPermissions = '';

            $scope.username = '';
            $scope.password = '';
            $scope.rememberme = false;

            function onSuccessfulLogin() {
                $ionicLoading.hide();
                console.log('Logged in');
                clearHttpCache();
                $rootScope.$broadcast('login');
                $ionicHistory.nextViewOptions({
                    disableBack: true
                });
                $state.go('app.games', {}, {reload: true});
            }

            function onFailedLogin() {
                $ionicLoading.hide();
                console.log('Login failed');
                clearHttpCache();
                $scope.message = 'Invalid username or password.';
            }

            function clearHttpCache() {
                $cacheFactory.get('$http').removeAll();
            }

            $scope.manualLogin = function () {
                $ionicLoading.show({
                    template: 'Sending...'
                });
                clearHttpCache();
                $http({
                    transformRequest: function (obj) {
                        var str = [];
                        for (var p in obj) {
                            str.push(encodeURIComponent(p) + '=' + encodeURIComponent(obj[p]));
                        }
                        return str.join('&');
                    },
                    url: '/signin/authenticate',
                    data: {
                        username: $scope.username,
                        password: $scope.password,
                        'remember-me': $scope.rememberme
                    },
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                    method: 'POST'
                }).success(onSuccessfulLogin).error(onFailedLogin);
            };

            function showLoginOptions() {
                $scope.showFacebook = true;
                $scope.showManual = ENV.domain === 'localhost' || ENV.domain.href.indexOf('-dev') > -1;
                $scope.message = '';
            }

            function autoLogin() {
                $scope.showFacebook = false;
                $scope.showManual = false;
                $scope.message = 'Logging in via Facebook';
                clearHttpCache();
                if ($window.location.href.indexOf('file') === 0) {
                    $http.get(ENV.apiEndpoint + '/auth/facebook').success(onSuccessfulLogin).error(onFailedLogin);
                } else {
                    $window.location = ENV.apiEndpoint + '/auth/facebook';
                    //  TODO - doubt this works
                    $scope.$broadcast('login');
                }
            }

            jtbFacebook.canAutoSignIn().then(function (details) {
                clearHttpCache();
                $scope.facebookPermissions = details.permissions;
                if (!details.auto) {
                    showLoginOptions();
                } else {
                    autoLogin();
                }
            }, function () {
                showLoginOptions();
            });

            $scope.fbLogin = function () {
                jtbFacebook.initiateFBLogin().then(function (details) {
                    if (!details.auto) {
                        showLoginOptions();
                    } else {
                        autoLogin();
                    }
                }, function () {
                    showLoginOptions();
                });
            };
        }
    ]
);
