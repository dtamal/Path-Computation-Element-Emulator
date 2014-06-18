app.controller('serverLauncher', function($scope, $http) {
	$scope.serverIP = '127.0.0.1';
	$scope.serverPort = '8080';

	$scope.launchServer = function() {
			(function getStatus() {
				$http
						.get(
								'http://' + $scope.serverIP + ':'
										+ $scope.serverPort
										+ '/ctrl/server/status').success(
								function() {
									alert('Server already started');
								}).error(function(data) {
									alert('Starting the server' + data);
									runServer(false);
									$http
									.get(
											'http://' + $scope.serverIP + ':'
													+ $scope.serverPort
													+ '/ctrl/server/start').success(
											function() {
												alert('The server is running');
											}).error(function(data) {
												alert('Error: It is not possible launch the server' + data);
									});
						});

			})();

	}

});
