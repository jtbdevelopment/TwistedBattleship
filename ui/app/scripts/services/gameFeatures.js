'use strict';

//  TODO - make core
angular.module('tbs.services').factory('jtbGameFeatureService', ['$http', function ($http) {
    return {
        features: function () {
            return $http.get('/api/features', {cache: true}).then(function (response) {
                return response.data;
            });
        }
    };
}]);

