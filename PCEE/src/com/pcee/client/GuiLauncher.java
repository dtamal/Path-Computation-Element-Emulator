package com.pcee.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.pcee.protocol.message.objectframe.impl.PCEPExplicitRouteObject;
import com.pcee.protocol.message.objectframe.impl.PCEPGenericExplicitRouteObjectImpl;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.EROSubobjects;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PCEPAddress;
import com.pcee.protocol.response.PCEPResponseFrame;
import com.pcee.server.ServerLauncher;
import javax.swing.DefaultComboBoxModel;

public class GuiLauncher extends JFrame implements ActionListener{

	private boolean it = false;
	private JPanel contentPane;
	
	private JButton startServer;
	private JButton makeRequest;
	
	private JCheckBox itRequest;
	private JComboBox cpu;
	private JComboBox ram;
	private JComboBox storage;
	
	private JTextField source;
	private JTextField destination;
	
	private JLabel result;
	
	private int CPU;
	private int RAM;
	private int Storage;
	private String src;
	private String dest;
	private String resultString;
	private JButton btnInitConnection;
	
	private PCEPResponseFrame responseFrame;
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GuiLauncher frame = new GuiLauncher();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GuiLauncher() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 650, 450);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		startServer = new JButton("Start Server");
		startServer.addActionListener(this);
		startServer.setActionCommand("start");
		startServer.setBounds(223, 11, 167, 32);
		contentPane.add(startServer);
		
		makeRequest = new JButton("Make Request");
		makeRequest.addActionListener(this);
		makeRequest.setActionCommand("request");
		makeRequest.setBounds(223, 272, 167, 32);
		contentPane.add(makeRequest);
		
		itRequest = new JCheckBox("IT Request");
		itRequest.addActionListener(this);
		itRequest.setActionCommand("it");
		itRequest.setBounds(223, 94, 79, 38);
		contentPane.add(itRequest);
		
		cpu = new JComboBox();
		cpu.setModel(new DefaultComboBoxModel(new String[] {"1", "2", "3", "4", "5", "6", "7", "8"}));
		cpu.setBounds(362, 103, 46, 20);
		contentPane.add(cpu);
		
		ram = new JComboBox();
		ram.setModel(new DefaultComboBoxModel(new String[] {"32", "64", "128", "256"}));
		ram.setBounds(362, 138, 46, 20);
		contentPane.add(ram);
		
		storage = new JComboBox();
		storage.setModel(new DefaultComboBoxModel(new String[] {"10", "20", "30", "40", "50", "60", "70", "80"}));
		storage.setBounds(362, 171, 46, 20);
		contentPane.add(storage);
		
		JLabel lblCpu = new JLabel("CPU:");
		lblCpu.setBounds(306, 106, 46, 14);
		contentPane.add(lblCpu);
		
		JLabel lblRam = new JLabel("RAM:");
		lblRam.setBounds(306, 141, 46, 14);
		contentPane.add(lblRam);
		
		JLabel lblStorage = new JLabel("Storage:");
		lblStorage.setBounds(306, 174, 46, 14);
		contentPane.add(lblStorage);
		
		JLabel lblSource = new JLabel("Source: ");
		lblSource.setBounds(96, 224, 46, 14);
		contentPane.add(lblSource);
		
		JLabel lblDestination = new JLabel("Destination:");
		lblDestination.setBounds(328, 227, 72, 14);
		contentPane.add(lblDestination);
		
		source = new JTextField();
		source.setBounds(152, 221, 127, 20);
		source.setText("192.169.2.1");
		contentPane.add(source);
		source.setColumns(20);
		
		destination = new JTextField();
		destination.setColumns(20);
		destination.setText("192.169.2.14");
		destination.setBounds(399, 224, 127, 20);
		contentPane.add(destination);
		
		JLabel lblResult = new JLabel("Result: ");
		lblResult.setBounds(96, 331, 46, 14);
		contentPane.add(lblResult);
		
		result = new JLabel("");
		result.setHorizontalAlignment(SwingConstants.CENTER);
		result.setBounds(152, 331, 374, 70);
		contentPane.add(result);
		
		btnInitConnection = new JButton("Init Connection");
		btnInitConnection.addActionListener(this);
		btnInitConnection.setActionCommand("init");
		btnInitConnection.setBounds(223, 55, 167, 32);
		contentPane.add(btnInitConnection);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("start")){
			serverUp();
			startServer();
		} else if (e.getActionCommand().equals("init")){
			connectionInited();
			initConnection();
		}else if(e.getActionCommand().equals("request")){
			prepareRequest();
			sendRequest();
		} else {
			this.it = itRequest.isSelected();
		}
	}
	
	private void startServer(){
		ServerLauncher.main(new String[0]);
	}
	
	private void serverUp(){
		startServer.setText("Server is up!");
		startServer.setBackground(Color.GREEN);
		startServer.setForeground(Color.WHITE);
		startServer.setEnabled(false);
	}
	
	private void prepareRequest(){
		this.src = source.getText();
		if(it){
			this.CPU = Integer.parseInt((String)cpu.getSelectedItem());
			this.RAM = Integer.parseInt((String)ram.getSelectedItem());
			this.Storage = Integer.parseInt((String)storage.getSelectedItem());
			sendRequestWithIT();
		}else{
			this.dest = destination.getText();
			sendRequest();
		}
	}
	
	private void sendRequest(){
		this.responseFrame = ClientTest.getPath(src, dest, 10);
		updateResult();
	}
	
	private void sendRequestWithIT(){
		this.responseFrame = ClientTest.getPath(src, 10, this.CPU, this.RAM, this.Storage);
		updateResult();
	}
	
	private void connectionInited(){
		btnInitConnection.setText("Connected with server");
		btnInitConnection.setBackground(Color.GREEN);
		btnInitConnection.setForeground(Color.WHITE);
		btnInitConnection.setEnabled(false);
	}
	
	private void initConnection(){
		ClientTest.initClient();
	}
	
	private void updateResult(){
		LinkedList<PCEPExplicitRouteObject> paths;
		ArrayList<EROSubobjects> addresses;
		if(this.responseFrame.extractNoPathObject()==null){
			resultString = "Path Exists!<br>";
			paths = responseFrame.extractExplicitRouteObjectList();
			for(PCEPExplicitRouteObject path : paths){
				addresses = ((PCEPGenericExplicitRouteObjectImpl)path).getTraversedVertexList();
				for(EROSubobjects address : addresses){
					resultString += ((PCEPAddress)address).getIPv4Address(false) + " - ";
				}
			}
		}else{
			resultString = "No Path between " + src + " and " + dest + " exists!";
		}
		
		this.result.setText("<html>" + resultString + "</html>");
		
	}
}
