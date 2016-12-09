'use strict';

describe('Controller: MobileGameListCtrl', function () {
    beforeEach(module('tbs.controllers'));

    var currentPlayer = {source: 'MANUAL', md5: 'my md 5'};
    var mockPlayerService = {
        currentPlayer: function () {
            return currentPlayer;
        }
    };

    var expectedClassifications = ['Class2', 'Class1', 'Class3', 'Class4'];
    var expectedIcons = {Class1: 'Icon1', Class2: 'IconX', Class3: 'hey', Class4: 'spiral'};
    var expectedGames = {
        Class1: [{id: '1'}, {id: '10'}],
        Class2: [],
        Class3: [{id: '3'}, {id: '7'}],
        Class4: [{id: '17'}, {id: '2'}]
    };
    var mockClassifier = {
        getIcons: function () {
            return expectedIcons;
        },
        getClassifications: function () {
            return expectedClassifications;
        }
    };

    var expectedInitialized = false;
    var mockGameCache = {
        initialized: function () {
            return expectedInitialized;
        },
        getGamesForPhase: function (phase) {
            return expectedGames[phase];
        }
    };

    var mockGameDetails = {
        afunction: function () {
        }
    };
    var $rootScope, $scope, ctrl, stateSpy;

    beforeEach(inject(function (_$rootScope_, $controller) {
        stateSpy = {go: sinon.spy()};
        $rootScope = _$rootScope_;
        $scope = $rootScope.$new();
        ctrl = $controller('MobileGameListCtrl', {
            $scope: $scope,
            $state: stateSpy,
            jtbGameClassifier: mockClassifier,
            jtbGameCache: mockGameCache,
            jtbPlayerService: mockPlayerService,
            tbsGameDetails: mockGameDetails
        });
    }));

    it('initializes when cache not ready yet', function () {
        angular.forEach(expectedClassifications, function (classification) {
            expect(ctrl.games[classification].games).to.deep.equal([]);
            expect(ctrl.games[classification].icon).to.equal(expectedIcons[classification]);
            expect(ctrl.games[classification].hideGames).to.be.false;
            expect(ctrl.games[classification].label).to.equal(classification);
        });
        expect(ctrl.md5).to.equal('');
        expect(ctrl.phasesInOrder).to.deep.equal(expectedClassifications);
        expect(ctrl.gameDetails).to.equal(mockGameDetails);
    });

    it('after game caches loaded event', function () {
        var oldBC = $rootScope.$broadcast;
        sinon.spy($rootScope, "$broadcast");
        $rootScope.$broadcast('gameCachesLoaded');
        angular.forEach(expectedClassifications, function (classification) {
            expect(ctrl.games[classification].games).to.deep.equal(expectedGames[classification]);
        });
        expect(ctrl.md5).to.equal(currentPlayer.md5);
        assert($rootScope.$broadcast.calledWithMatch('scroll.refreshComplete'));
        $rootScope.$broadcast = oldBC;
    });

    it('switch flag for hide games', function () {
        expect(ctrl.games[expectedClassifications[2]].hideGames).to.be.false;
        ctrl.switchHideGames(expectedClassifications[2]);
        expect(ctrl.games[expectedClassifications[2]].hideGames).to.be.true;
        ctrl.switchHideGames(expectedClassifications[2]);
        expect(ctrl.games[expectedClassifications[2]].hideGames).to.be.false;
    });

    it('new game action goes to setup', function () {
        ctrl.createNew();
        assert(stateSpy.go.calledWithMatch('app.create'));
    });

    describe('controller loaded after cache loaded', function () {
        beforeEach(inject(function (_$rootScope_, $controller) {
            expectedInitialized = true;
            $rootScope = _$rootScope_;
            $scope = $rootScope.$new();

            ctrl = $controller('MobileGameListCtrl', {
                $scope: $scope,
                $state: stateSpy,
                jtbGameClassifier: mockClassifier,
                jtbGameCache: mockGameCache,
                jtbPlayerService: mockPlayerService,
                tbsGameDetails: mockGameDetails
            });
        }));

        it('initializes games immediately', function () {
            angular.forEach(expectedClassifications, function (classification) {
                expect(ctrl.games[classification].games).to.deep.equal(expectedGames[classification]);
                expect(ctrl.games[classification].icon).to.equal(expectedIcons[classification]);
                expect(ctrl.games[classification].hideGames).to.be.false;
                expect(ctrl.games[classification].label).to.equal(classification);
            });
            expect(ctrl.md5).to.equal(currentPlayer.md5);
            expect(ctrl.phasesInOrder).to.deep.equal(expectedClassifications);
            expect(ctrl.gameDetails).to.equal(mockGameDetails);
        });
    });

});
