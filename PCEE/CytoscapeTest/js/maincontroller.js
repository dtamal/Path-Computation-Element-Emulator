window.oldLogs = [];

app.controller('logCtrl', [
		'$scope',
		'$http',
		'$timeout',
		function logCtrl($scope, $http, $timeout) {
			$scope.log = [];

			(function getLogs() {
				if (isConnected) {
					$http.get('http://localhost:8081/ctrl/logs').success(
							function(data) {

								for ( var i = 0; i < data.length; i++) {
									var index = oldLogs.length;
									oldLogs[index] = {};
									oldLogs[index] = data[i];
								}
								var out = [];
//								var logCount = 50;
								this.clearFrame('clientIframe');
								for ( var i = 0; i < oldLogs.length; i++) {
									this.write("clientIframe", '['
											+ oldLogs[i].level + '] '
											+ oldLogs[i].message + '<br/>');
//									logCount--;
//									if (logCount == 0)
//										break;
								}
								$scope.log = out;

							});
				}
				$timeout(getLogs, 2000);
			})();

		} ]);

app
		.controller(
				'clientForm',
				function($scope, $http) {
					$scope.connectionIP = '127.0.0.1';
					$scope.connectionPort = '8080';

					$scope.connectToServer = function() {
						if (!isConnected) {
							(function setConnection() {

								$http
										.get(
												'http://localhost:'
														+ $scope.connectionPort
														+ '/ctrl/server/status')
										.success(
												function(data) {
													$http
															.get(
																	'http://localhost:8081/ctrl/client/connect')
															.success(
																	function(
																			data) {
																		// alert('Connection
																		// established:
																		// '
																		// +
																		// data);

																		this
																				.connectClient();

																		$http
																				.get(
																						'http://'
																								+ $scope.connectionIP
																								+ ':'
																								+ $scope.connectionPort
																								+ '/ctrl/server/topology/nodes')
																				.success(
																						function(
																								nodes) {
																							$http
																									.get(
																											'http://'
																													+ $scope.connectionIP
																													+ ':'
																													+ $scope.connectionPort
																													+ '/ctrl/server/topology/links')
																									.success(
																											function(
																													links) {
																												drawNetworkgraph(
																														nodes,
																														links);
																											});

																						})
																				.error(
																						function(
																								data) {
																							alert('Error trying to get the topology');

																						});

																	})
															.error(
																	function(
																			data) {
																		alert('Error: Connection failed');

																	});

												}).error(function(data) {
											alert('Error: Connection failed');

										});
							})();
						} else {
							(function disconnection() {

								$http
										.get(
												'http://localhost:8081/ctrl/client/disconnect')
										.success(function(data) {
											// alert('Disconnected from the
											// server');
											this.connectClient();

										})
										.error(
												function(data) {
													alert('Error: Disconnection failed');

												});
							})();
						}
					}

					$scope.srcAddr = '192.169.2.1';
					$scope.dstAddr = '192.169.2.14';

					$scope.sendRequest = function() {
						(function sendRequest() {
							
							$http
							.get(
									'http://localhost:'
											+ $scope.connectionPort
											+ '/ctrl/server/status')
							.success(
									function(data) {
										var params = $scope.srcAddr + " " + $scope.dstAddr;
										$http
												.post(
														'http://localhost:8081/ctrl/client/request',
														JSON.stringify(params)).success(
														function(data) {
															// alert('Received path: ' +
															// data);
															highlightPath(data);

														}).error(function() {
													alert('Error: wrong path received');

												});
									})
									.error(
											function(data) {
												alert('Server is not running. Stopping the client...');
												(function disconnection() {

													$http
															.get(
																	'http://localhost:8081/ctrl/client/disconnect')
															.success(function(data) {
																// alert('Disconnected from the
																// server');
																this.connectClient();

															})
															.error(
																	function(data) {
																		alert('Error: Disconnection failed');

																	});
												})();

											});
							
						})();

					}
				});
