/**
 *  This file is part of Path Computation Element Emulator (PCEE).
 *
 *  PCEE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  PCEE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with PCEE.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pcee.architecture.networkmodule;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;


import com.pcee.architecture.ModuleEnum;
import com.pcee.architecture.ModuleManagement;
import com.pcee.common.Address;
import com.pcee.logger.Logger;
import com.pcee.protocol.message.PCEPCommonMessageHeader;
import com.pcee.protocol.message.PCEPComputationFactory;
import com.pcee.protocol.message.PCEPMessage;


/**
 * Implementation of the Network Module 
 * 
 * @author Marek Drogon
 * @author Mohit Chamania
 */

public class NetworkModuleImpl extends NetworkModule {

	//Management Object used to forward communications between the different modules
	private ModuleManagement lm;

	//Java NIO selector object used to monitor incoming connection requests as well as data read requests
	private Selector selector;

	//Thread instance used to operate the Selector
	private Thread selectorthread;


	//Boolean flag used by the selector thread for graceful stop
	private boolean selectorStop = false;

	//Port at which the selector threads listens for incoming PCEP connections, default value is 4189
	private int port;


	//Map to store correlation between the session ID and the corresponding Selection Key
	private HashMap<String, SelectionKey> addressToSelectionKeyHashMap = new HashMap<String, SelectionKey>();

	//Map to store correlation between the session ID and the corresponding socket channel 
	private HashMap<String, SocketChannel> addressToSocketChannelHashMap = new HashMap<String, SocketChannel>();

	//Map based buffer to store partial messages received by the selector during a read cycle
	private HashMap<String, String> partialMessageHashMap = new HashMap<String, String>();


	//Queues of Socket Channels for registering connections gracefully in the socket layer
	private LinkedBlockingQueue<SocketChannel> registerConnQueue = new LinkedBlockingQueue<SocketChannel>();


	//Boolean flag to indicate if the Network Module is used on the server side (indicating if it should listen for new connection requests
	private boolean isServer;

	/**Default Constructor
	 * 
	 * @param isServer
	 * @param layerManagement
	 */
	public NetworkModuleImpl(boolean isServer, ModuleManagement layerManagement) {
		lm = layerManagement;
		port = 4189;
		this.isServer = isServer;
		this.start();
	}
	
	public NetworkModuleImpl(boolean isServer, ModuleManagement layerManagement, int port) {
		lm = layerManagement;
		this.port = port;
		this.isServer = isServer;
		this.start();
	}

	public void stop() {
		//Clear mappings 
		addressToSelectionKeyHashMap.clear();
		addressToSocketChannelHashMap.clear();
		partialMessageHashMap.clear();
		registerConnQueue.clear();
		// Close the selector
		selectorStop = true;
		selector.wakeup();
	}

	public void start() {
		initSelectorParams();
		startSelectorThread();
	}


	public void receiveMessage(PCEPMessage message, ModuleEnum sourceLayer) {
		localDebugger("Entering: receiveMessage(PCEPMessage message)");
		localDebugger("| message: " + message.contentInformation());
		localDebugger("| sourceLayer: " + sourceLayer);
		writeSocket(message);
	}

	public void sendMessage(PCEPMessage message, ModuleEnum targetLayer) {
		localDebugger("Entering: sendMessage(PCEPMessage message)");
		localDebugger("| message: " + message.contentInformation());
		localDebugger("| targetLayer: " + targetLayer);

		switch (targetLayer) {
		case SESSION_MODULE:
			lm.getSessionModule().receiveMessage(message, ModuleEnum.NETWORK_MODULE);
			break;
		case COMPUTATION_MODULE:
			// Not possible
			break;
		case CLIENT_MODULE:
			// Not possible
			break;
		default:
			localLogger("Error in sendMessage(PCEPMessage message, LayerEnum targetLayer)");
			localLogger("Wrong target Layer");
			break;
		}
	}


	public void registerConnection(Address address, boolean connected, boolean connectionInitialized) {
		localDebugger("Entering: registerConnection(Address address, boolean connected, boolean connectionInitialized)");
		localDebugger("| address: " + address.getAddress());
		localDebugger("| connected" + connected);
		localDebugger("| connectionInitialized" + connectionInitialized);

		//Register connection in the network only used when other modules want to establish connection with a remote peer. 
		//Synchronization of session state assumed to be handled by the other modules 
		localLogger("Trying to initialise a Connection to " + address.getAddress());
		try {
			//Opening a new connection to the remote peer
			SocketChannel socketChannel = SocketChannel.open();
			socketChannel.socket().setReuseAddress(true);
			socketChannel.connect(new InetSocketAddress(address.getAddressWithoutPort(), port));

			if (socketChannel.isConnected() == true) {
				localDebugger("Connected to " +address.getAddressWithoutPort() + ":" + port);
				Address remoteAddress = new Address(socketChannel.socket().getInetAddress().getHostAddress(), socketChannel.socket().getPort());

				//Configuring socket channel properties
				socketChannel.configureBlocking(false);

				//Register SocketChannel first
				insertSocketChannelToHashMap(remoteAddress, socketChannel);

				//This step intimates the state machine that connection is established. State Machine can then send out The first OPEN message 
				lm.getSessionModule().registerConnection(remoteAddress, true, true);

				//Socket is registered with the selector only after state machine is initialized so that an OPEN message is not received before an OPEN message has been sent out
				registerConnQueue.add(socketChannel);
				selector.wakeup();

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void closeConnection(Address address) {
		localDebugger("Entering: closeConnection(Address address)");
		localDebugger("| address: " + address.getAddress());
		
		
		SelectionKey key =  getSelectionKeyFromHashMap(address);
		if (key !=null) {
			SocketChannel socketChannel = (SocketChannel)key.channel();
			if (socketChannel != null) {
				try {
					//close the socket channel
					socketChannel.close();
					//cancel the key with the selector
					key.cancel();
				} catch (IOException e) {
					localDebugger("IOException in closing socket ");
				}
			}
		}
		//Remove the mappings from the different Map structures
		removePartialMessageFromHashMap(address);
		removeSelectionKey(address);
		removeSocketChannel(address);
	}



	private void startSelectorThread() {
		// Anonymous Class
		// volatile boolean selectorStop = false;
		selectorthread = new Thread() {
			public void run() {

				while (!selectorStop) {
					try {

						localLogger("Listening for Events");
						selector.select();

						//Processing Events received from the selector
						Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
						while (keyIterator.hasNext()) {

							SelectionKey key = keyIterator.next();
							localDebugger("Removing Current key from the KeyIterator");
							keyIterator.remove();

							if (key.isValid()) {
								if (key.isAcceptable()) {
									localDebugger("Entering: key.isAcceptable()");
									localDebugger("| key: " + key.toString());

									SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
									if (socketChannel != null) {
										//Call function to receive a new Connection
										connectionReceived(socketChannel);
									}

								} else if (key.isReadable()) {
									localDebugger("Entering: key.isReadable()");
									localDebugger("| key: " + key.toString());

									if (((SocketChannel) key.channel()).socket().isClosed() == false) {
										readSocket(key);
									} else
										key.cancel();

								}
							}
						}


						//Register new sockets into the selector
						while (registerConnQueue.size() != 0) {
							localLogger("Registering new Connection");
							SocketChannel socketChannel = registerConnQueue.take();
							//Retreiving SelectionKey associated with socket channel 
							SelectionKey key = socketChannel.register(selector, SelectionKey.OP_READ);
							//Register the socket channel in the hash map
							Address address = new Address(socketChannel.socket().getInetAddress().getHostAddress().trim(), socketChannel.socket().getPort());
							insertSelectionKeyToHashMap(address, key);
						}

						//If selector is scheduled for stopping, close the selector and terminate the thread
						if (selectorStop){
							localLogger("Closing the Selector");
							selector.close();
							break;								
						}
					} catch (IOException e) {
						localDebugger("IOException with the selector");
						e.printStackTrace();
					} catch (InterruptedException e) {
						localDebugger("Thread Interrupted when reading new connections from the register connection queue");
					}
				}


			}

		};
		selectorthread.setName("SelectorThread-NetworkHandler");
		selectorthread.start();
	}


	/**Function to initialize the selector, and if server, start a serversocketchannel to recieve connections*/
	private void initSelectorParams() {
		localDebugger("Entering: receiveConnection(boolean isServer)");
		localDebugger("| isServer: " + isServer);

		try {
			selector = Selector.open();

			if (isServer) {
				ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
				serverSocketChannel.socket().bind(new InetSocketAddress(port));
				serverSocketChannel.configureBlocking(false);
				serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**Function to accept new Incoming connections from the serversocket
	 * 
	 * @param socketChannel
	 * @throws IOException
	 */
	private void connectionReceived(SocketChannel socketChannel) throws IOException {
		localDebugger("Entering: connectionReceived(SocketChannel socketChannel)");
		localDebugger("| socketChannel: " + socketChannel.toString());

		String addressString = socketChannel.socket().getInetAddress().getHostAddress().trim();
		int port = socketChannel.socket().getPort();
		Address address = new Address(addressString, port);
		
		
		//Check if a selection key is already registered
		if (getSelectionKeyFromHashMap(address)==null) {
			//Configure Socket Properties
			socketChannel.configureBlocking(false);
			insertSocketChannelToHashMap(address, socketChannel);
			lm.getSessionModule().registerConnection(address, true, false);
			localLogger("New Connection Accepted, registering with selector");
			SelectionKey key = socketChannel.register(selector, SelectionKey.OP_READ);
			insertSelectionKeyToHashMap(address, key);

		} else {
			localLogger("Terminating incoming connection as a connection from the IP address" + address.getAddress() + " already exists.");
			socketChannel.close();
		}
	}

	private void readSocket(SelectionKey key) {
		localDebugger("Entering: readSocket(SelectionKey key)");
		localDebugger("| key: " + key.toString());
		ByteBuffer messageBuffer = ByteBuffer.allocate(2000);

		SocketChannel inputSocketChannel = (SocketChannel) key.channel();
		// System.out.println("Reading Data From remote address:" + inputSocketChannel.socket().getInetAddress().getHostAddress().trim() + ":" + inputSocketChannel.socket().getPort());

		String addressString = inputSocketChannel.socket().getInetAddress().getHostAddress().trim();
		int port = inputSocketChannel.socket().getPort();
		Address address = new Address(addressString, port);

		if (inputSocketChannel.isConnected()) {
			try {
				//Appends the initial buffer string to the incoming message string
				String messageString = getPartialMessageFromHashMap(address);
				//Clear the buffer for this string
				removePartialMessageFromHashMap(address);

				int loopCount = 0;
				int flag = 0;
				byte[] receivedMessageByteArray = null;
				while (true) {
					// //System.out.println("\t\t\tReading from Input Buffer Queue");
					messageBuffer.clear();
					int byteCounter;
					byteCounter = inputSocketChannel.read(messageBuffer);
					localLogger("byteCounter = " + byteCounter);

					if (byteCounter == -1) {
						localDebugger("Socket Shut Down Cleanly, Closing Connection for address: " + address.getAddress());
						lm.getSessionModule().closeConnection(address);
						break;
					}

					if (byteCounter < -1) {
						//Unknown error
						localDebugger("Unknown error in socket. Closing Connection from address: " + address.getAddress());
						lm.getSessionModule().closeConnection(address);
						break;
					}

					if (byteCounter == 0) {
						// //System.out.println("LoopCount == " + loopCount);
						if ((loopCount == 0) && (flag == 0)) {
							flag = 1;
							continue;
						}
						else if ((loopCount == 0) && (flag == 1)){
							// Selector in read loop with no data to read, Closing Connection 
							localDebugger("Selector in read loop with no data to read, Closing Connection from address: " + address.getAddress());
							lm.getSessionModule().closeConnection(address);							
							break;
						}
						break;
					}
					messageBuffer.flip();

					receivedMessageByteArray = new byte[byteCounter];

					messageBuffer.get(receivedMessageByteArray);

					//Append the received string onto the existing buffered data
					messageString += PCEPComputationFactory.byteArrayToRawMessage(receivedMessageByteArray);
					// //System.out.println("MessageString = " + messageString);
					loopCount++;
				}

				String messageStringTrimed = messageString.trim();

				if (messageStringTrimed.length() != 0) {
					LinkedList<String> messages = parseMultipleMessages(messageStringTrimed, address);
					Iterator<String> iter = messages.iterator();
					while (iter.hasNext()) {
						String temp = iter.next();
						byte[] messageByteArray = PCEPComputationFactory.rawMessageToByteArray(temp);

						PCEPMessage receivedMessage = new PCEPMessage(messageByteArray);
						receivedMessage.setAddress(address);

						localLogger("Reading Message from " + address.getAddress());
						localLogger("| " + receivedMessage.contentInformation());
						localLogger("| " + receivedMessage.binaryInformation());
						localLogger("| " + receivedMessage.toString());

						sendMessage(receivedMessage, ModuleEnum.SESSION_MODULE);

					}
				} 
			} catch (IOException e) {
				localDebugger("Error when reading from socket for address " + address.getAddress() + " Closing connection");
				lm.getSessionModule().closeConnection(address);
			}

		} else {
			localDebugger("Input Channel Closed for " + address.getAddress() + " Closing connection");
			lm.getSessionModule().closeConnection(address);
		}
	}


	/**Function to check if the incoming string has multiple concatenated messages
	 * if one of the messages is smaller than a full message, adds this message to the insertPartialMessageToHashMap based buffer
	 * @param binaryString
	 * @param address
	 * @return LinkedList<String> containing the complete concatenated messages
	 */
	private LinkedList<String> parseMultipleMessages(String binaryString, Address address) {
		LinkedList<String> output = new LinkedList<String>();
		while (binaryString.length() > 0) {
			if (binaryString.length() >= 32) {
				String currentHeaderString = binaryString.substring(0, 32);
				PCEPCommonMessageHeader messageHeader = new PCEPCommonMessageHeader(currentHeaderString);
				int bitLength = messageHeader.getLengthDecimalValue() * 8;

				if (binaryString.length() >= bitLength) {
					String subString = binaryString.substring(0, bitLength);
					output.add(subString);
					binaryString = binaryString.substring(bitLength);
				} else {
					insertPartialMessageToHashMap(address, binaryString);
					binaryString = "";
					break;
				}
			} else {
				insertPartialMessageToHashMap(address, binaryString);
				binaryString = "";
				break;
			}
		}
		return output;
	}

	/** Function to write a PCEPMessage to the network
	 * 
	 * @param message
	 */
	private void writeSocket(PCEPMessage message) {
		localDebugger("Entering: writeSocket(PCEPMessage message)");
		localDebugger("| message: " + message.contentInformation());


		SocketChannel outputSocketChannel = getSocketChannelFromHashMap(message.getAddress());

		if (outputSocketChannel != null) {
			if (outputSocketChannel.isConnected() == true) {
				localLogger("Preparing to send message to " + message.getAddress().getAddress());
				localLogger("| " + message.contentInformation());
				localLogger("| " + message.binaryInformation());
				localLogger("| " + message.toString());

				byte[] messageByteArray = message.getMessageByteArray();
				// System.out.println("Sending data to " + message.getAddress().getAddress() + ", Size = " + messageByteArray.length);
				ByteBuffer messageBuffer = ByteBuffer.allocate(messageByteArray.length);
				messageBuffer.clear();

				messageBuffer.put(messageByteArray);
				messageBuffer.flip();
				while (messageBuffer.hasRemaining()) {
					try {
						synchronized (outputSocketChannel) {
							outputSocketChannel.write(messageBuffer);
						}
					} catch (ClosedByInterruptException e) {
						// System.out.println("Problem is synchronous operation");
						break;
					} catch (IOException e) {
						// System.out.println("Connection Closed Illegally");
						// lm.getSessionLayer().closeConnection(message.getAddress());
						break;
					}
				}
			} else {
				localLogger("Socket Channel is not connected");
				lm.getSessionModule().closeConnection(message.getAddress());
			}
		} else {
			localLogger("Did not find Socket Channel in Hash map");
			lm.getSessionModule().closeConnection(message.getAddress());
		}

	}

	/**Function to retrieve a socket channel from the hash map
	 * 
	 * @param address
	 * @return
	 */
	private SocketChannel getSocketChannelFromHashMap(Address address) {
		localDebugger("Entering: getSocketChannel(Address address)");
		localDebugger("| address: " + address.getAddress());
		localLogger("Getting SocketChannel for " + address.getAddress());
		//Check if mapping exists
		if (addressToSocketChannelHashMap.containsKey(address.getAddress()))
			return addressToSocketChannelHashMap.get(address.getAddress());
		else
			return null;
	}

	/**Function to retrieve a Selection Key from the hash map
	 * 
	 * @param address
	 * @return
	 */
	private SelectionKey getSelectionKeyFromHashMap(Address address) {
		localDebugger("Entering: getSelectionKey(Address address)");
		localDebugger("| address: " + address.getAddress());
		localLogger("Getting SelectionKey for " + address.getAddress());

		//Check if mapping exists
		if (addressToSelectionKeyHashMap.containsKey(address.getAddress()))
			return addressToSelectionKeyHashMap.get(address.getAddress());
		else
			return null;
	}

	/** Function to insert a selection key to the hash map
	 * 
	 * @param address
	 * @param key
	 */
	private void insertSelectionKeyToHashMap(Address address, SelectionKey key) {
		localDebugger("Entering: insertSelectionKeyToHashMap(Address address, SelectionKey key)");
		localDebugger("| address: " + address.getAddress());
		addressToSelectionKeyHashMap.put(address.getAddress(), key);
	}

	/** Function to insert the socket channel to hash map
	 * 
	 * @param address
	 * @param channel
	 */
	private void insertSocketChannelToHashMap(Address address, SocketChannel channel) {
		localDebugger("Entering: insertSocketChannelToHashMap(Address address, SocketChannel socketChannel)");
		localDebugger("| address: " + address.getAddress());
		addressToSocketChannelHashMap.put(address.getAddress(), channel);
	}


	/**Function to remove the selection key from hash map
	 * 
	 * @param address
	 */
	private void removeSelectionKey(Address address) {
		localDebugger("Entering: removeSelectionKey(Address address)");
		localDebugger("| address: " + address.getAddress());
		addressToSelectionKeyHashMap.remove(address.getAddress());
	}

	/**Function to remove the socket channel from hash map
	 * 
	 * @param address
	 */
	private void removeSocketChannel(Address address) {
		localDebugger("Entering: removeSocketChannelToHashMap(Address address)");
		localDebugger("| address: " + address.getAddress());
		addressToSocketChannelHashMap.remove(address.getAddress());
	}

	/**Function to retrieve a partial message string from the hash map based buffer
	 * 
	 * @param address
	 * @return
	 */
	private String getPartialMessageFromHashMap(Address address) {
		localDebugger("Entering: getPartialMessageFromHashMap(Address address)");
		localDebugger("| address: " + address.getAddress());

		localLogger("Getting String for " + address.getAddress());
		if (partialMessageHashMap.containsKey(address.getAddress()))
			return partialMessageHashMap.get(address.getAddress());
		else
			return "";
	}

	/**Function to insert a partial message string into the hash map based buffer
	 * 
	 * @param address
	 * @param partialMessage
	 */
	private void insertPartialMessageToHashMap(Address address, String partialMessage) {
		localDebugger("Entering: insertPartialMessageToHashMap(Address address, String partialMessage)");
		localDebugger("| address: " + address.getAddress());
		localDebugger("| Partial Message: " + partialMessage);

		localLogger("Inserting Partial String for " + address.getAddress());
		// System.out.println("Inserting Partial String for " + address.getAddress() + ", Stirng = " + partialMessage);
		partialMessageHashMap.put(address.getAddress(), partialMessage);
	}

	/**Function to remove the partial message entry from the hash map based buffer
	 * 
	 * @param address
	 */
	private void removePartialMessageFromHashMap(Address address) {
		localDebugger("Entring: removePartialMessageFromHashMap(Address address)");
		localDebugger("| address: " + address.getAddress());

		localLogger("Removing Partial Message for " + address.getAddress());

		if (partialMessageHashMap.containsKey(address.getAddress()))
			partialMessageHashMap.remove(address.getAddress());
	}

	/**Logger Event for logging events inside the network module
	 * 
	 * @param event
	 */
	private void localLogger(String event) {
		Logger.logSystemEvents("[NetworkLayer]     " + event);
	}

	/**Logger Event for logging debugging information inside the network module
	 * 
	 * @param event
	 */
	private void localDebugger(String event) {
		Logger.debugger("[NetworkLayer]     " + event);
	}


}
