'use strict';

angular.module('tbs.services').factory('tbsCircles', ['$http', function ($http) {
    return {
        features: function () {
            return $http.get('/api/circles', {cache: true}).then(function (response) {
                return response.data;
            });
        }
    };
}]);

