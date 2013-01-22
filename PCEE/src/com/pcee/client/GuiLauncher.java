package com.pcee.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.global.GlobalCfg;
import com.pcee.protocol.message.objectframe.impl.PCEPBandwidthObject;
import com.pcee.protocol.message.objectframe.impl.PCEPExplicitRouteObject;
import com.pcee.protocol.message.objectframe.impl.PCEPGenericExplicitRouteObjectImpl;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.EROSubobjects;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PCEPAddress;
import com.pcee.protocol.response.PCEPResponseFrame;
import com.pcee.server.ServerLauncher;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JRadioButton;

/**
 * @author Yuesheng Zhong
 *
 */
public class GuiLauncher extends JFrame implements ActionListener {

	private static boolean responseArrives = false;
	private JPanel contentPane;

	private JButton startServer;
	private JButton makeRequest;

	private double bandwidth;
	private double delay;

	private JRadioButton singlePath;
	private JRadioButton multiPath;

	private JTextField source;
	private JTextField destination;

	private JLabel result;
	private static JLabel wResult;

	private String src;
	private String dest;
	private String resultString;
	private JButton btnInitConnection;

	private JComboBox bw;

	private PCEPResponseFrame responseFrame;
	private JLabel lblDelay;
	private JComboBox dl;

	private JLabel requestFormat;

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
		setResizable(false);
		setTitle("PCEE Single Domain Multipath Support");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 650, 550);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		startServer = new JButton("Start Server");
		startServer.addActionListener(this);
		startServer.setActionCommand("start");
		startServer.setBounds(10, 11, 614, 32);
		contentPane.add(startServer);

		makeRequest = new JButton("Make Request");
		makeRequest.addActionListener(this);
		makeRequest.setActionCommand("request");
		makeRequest.setBounds(10, 212, 614, 32);
		contentPane.add(makeRequest);

		JLabel lblSource = new JLabel("Source: ");
		lblSource.setBounds(20, 185, 46, 14);
		contentPane.add(lblSource);

		JLabel lblDestination = new JLabel("Destination:");
		lblDestination.setBounds(215, 185, 72, 14);
		contentPane.add(lblDestination);

		source = new JTextField();
		source.setBounds(86, 182, 80, 20);
		source.setText("192.169.2.1");
		contentPane.add(source);
		source.setColumns(20);

		destination = new JTextField();
		destination.setColumns(20);
		destination.setText("192.169.2.14");
		destination.setBounds(286, 182, 87, 20);
		contentPane.add(destination);

		JLabel lblResult = new JLabel("Result: ");
		lblResult.setBounds(20, 418, 72, 14);
		contentPane.add(lblResult);

		result = new JLabel("");
		result.setHorizontalAlignment(SwingConstants.CENTER);
		result.setBounds(76, 340, 548, 161);
		wResult = result;
		contentPane.add(result);

		btnInitConnection = new JButton("Init Connection");
		btnInitConnection.addActionListener(this);
		btnInitConnection.setActionCommand("init");
		btnInitConnection.setBounds(10, 55, 614, 32);
		contentPane.add(btnInitConnection);

		JLabel lblBandwidth = new JLabel("Bandwidth:");
		lblBandwidth.setBounds(402, 185, 72, 14);
		contentPane.add(lblBandwidth);

		bw = new JComboBox();
		bw.setModel(new DefaultComboBoxModel(new String[] { "10", "20", "30",
				"40", "50", "60", "70", "80", "90", "100" }));
		bw.setBounds(476, 182, 46, 20);
		contentPane.add(bw);

		singlePath = new JRadioButton("Single Path");
		singlePath.setBounds(448, 106, 87, 23);
		contentPane.add(singlePath);

		multiPath = new JRadioButton("Multi Path");
		multiPath.setBounds(537, 106, 87, 23);
		contentPane.add(multiPath);

		ButtonGroup bg = new ButtonGroup();
		bg.add(singlePath);
		bg.add(multiPath);
		singlePath.setSelected(true);

		lblDelay = new JLabel("Delay:");
		lblDelay.setBounds(532, 185, 46, 14);
		contentPane.add(lblDelay);

		dl = new JComboBox();
		dl.setModel(new DefaultComboBoxModel(new String[] { "40", "80", "120",
				"160" }));
		dl.setBounds(578, 181, 46, 20);
		contentPane.add(dl);

		JLabel lblRequestFormat = new JLabel("Request Format: ");
		lblRequestFormat.setBounds(20, 277, 125, 14);
		contentPane.add(lblRequestFormat);

		requestFormat = new JLabel("");
		requestFormat.setHorizontalAlignment(SwingConstants.CENTER);
		requestFormat.setBounds(113, 255, 477, 74);
		contentPane.add(requestFormat);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("start")) {
			serverUp();
			startServer();
		} else if (e.getActionCommand().equals("init")) {
			connectionInited();
			initConnection();
		} else if (e.getActionCommand().equals("request")) {
			prepareRequest();
			sendRequest();
		}
	}

	private void startServer() {
		ServerLauncher.main(new String[0]);
	}

	private void serverUp() {
		startServer.setText("Server is UP!");
		startServer.setBackground(Color.GREEN);
		startServer.setForeground(Color.WHITE);
		startServer.setEnabled(false);
	}

	private void prepareRequest() {
		this.src = source.getText();
		this.bandwidth = Double.parseDouble((String) this.bw.getSelectedItem());
		this.dest = destination.getText();
		this.delay = Double.parseDouble((String) this.dl.getSelectedItem());

		sendRequest();

		if (this.singlePath.isSelected())
			GlobalCfg.singlePath = true;
		else
			GlobalCfg.singlePath = false;

		// title
		String formatString = "<html><center><table><tr><td>Source</td><td>Destination</td><td>Bandwidth</td><td>Delay</td><td>Single</td><td>Multi</td></tr>";

		// source
		formatString += "<td bgcolor=green></td>";

		// destination
		formatString += "<td bgcolor=green></td>";

		// bandwidth
		formatString += "<td bgcolor=green></td>";

		// delay
		formatString += "<td bgcolor=green></td>";

		// single or multiple path
		if (singlePath.isSelected())
			formatString += "<td bgcolor=green></td><td></td>";
		else
			formatString += "<td></td><td bgcolor=green></td>";

		formatString += "</tr></table></center></html>";

		this.requestFormat.setText(formatString);

	}

	private void sendRequest() {
		this.responseFrame = ClientTest.getPath(src, dest,
				(float) this.bandwidth, (float) this.delay);
		prepareResult();
	}

	private void connectionInited() {
		btnInitConnection.setText("Connected with server");
		btnInitConnection.setBackground(Color.GREEN);
		btnInitConnection.setForeground(Color.WHITE);
		btnInitConnection.setEnabled(false);
	}

	private void initConnection() {
		ClientTest.initClient();
	}

	private void prepareResult() {
		LinkedList<PCEPExplicitRouteObject> paths;
		ArrayList<EROSubobjects> addresses;
		if (this.responseFrame.extractNoPathObject() == null) {
			resultString = "<font color='blue' size=4>Path Exists!</font><br><br>";
			paths = responseFrame.extractExplicitRouteObjectList();
			ArrayList<EROSubobjects> tmpPath = ((PCEPGenericExplicitRouteObjectImpl) paths
					.get(0)).getTraversedVertexList();

			if (GlobalCfg.singlePath) {
				resultString += "<font color='purple'>Path is : </font>";
				for (int j = 0; j < tmpPath.size(); j++) {
					resultString += "<font color='green'>"
							+ ((PCEPAddress) tmpPath.get(j))
									.getIPv4Address(false) + "</font>";
					if (j != tmpPath.size() - 1)
						resultString += " - ";
				}
			} else {
				resultString += "<font color='purple'>Paths are : </font>";
				LinkedList<PCEPBandwidthObject> bwList = responseFrame
						.extractBandwidthObjectList();
				for (int i = 0; i < paths.size(); i++) {
					addresses = ((PCEPGenericExplicitRouteObjectImpl) paths
							.get(i)).getTraversedVertexList();
					for (int j = 0; j < addresses.size(); j++) {
						resultString += "<font color='green'>"
								+ ((PCEPAddress) addresses.get(j))
										.getIPv4Address(false) + "</font>";
						if (j != addresses.size() - 1)
							resultString += " - ";
					}
					resultString += " <br> With allocated Bandwidth = "
							+ bwList.get(i).getBandwidthFloatValue();
					if (i != paths.size() - 1)
						resultString += "<br>";
				}
			}

		} else {
			resultString = "<font color='red'>No Path between <font color='blue' size=5><b>"
					+ src
					+ "</b></font> and <font color='blue' size=5><b>"
					+ dest + "</b></font> exists!</font>";
		}

		updateResult(this.resultString);
	}

	private void updateResult(String up) {
		this.result.setText("<html>" + up + "</html>");
	}

	private void cout(String outString) {
		System.out.println(outString);
		GuiLauncher.debug(outString);
	}

	public static void debug(String debugString) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
					"./debugFile.txt"), true));
			bw.write(debugString + "\n");
			bw.flush();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
