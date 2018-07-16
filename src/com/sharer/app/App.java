package com.sharer.app;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import com.sharer.entities.Asset;
import com.sharer.entities.Assets;
import com.sharer.io.FileUtils;
import com.sharer.net.ClientAPI;
public class App {
	private JFrame frame;
	private JTextField name;
	private JTextField version;
	private JTextArea data;
	private ClientAPI api;
	private Assets localAssets;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					App window = new App();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public App() {
		// Create instance to access restful api
		api = new ClientAPI();
		
		try {
			boolean updateLocalAssets = false;
			
			// Download server.xml with files length
			Assets remoteAssets = api.downloadDictionary();
			
			// Read local files directory
			localAssets = FileUtils.getLocalAssets();
			

			// Sync assets based on version and length
			for (Asset remoteAsset : remoteAssets.getAssets()) {
				if (!localAssets.getAssets().contains(remoteAsset)) {
					Asset asset = api.download(remoteAsset.getRemotePathTofile());
					FileUtils.saveFile(asset);
					updateLocalAssets = true;					
				} else {
					for (Asset localAsset : localAssets.getAssets()) {
						// Check if assets exists locally to download
						if(!localAsset.equals(remoteAsset)) {
							Asset asset = api.download(remoteAsset.getRemotePathTofile());
							FileUtils.saveFile(asset);
							updateLocalAssets = true;
						}
					}
				}
			}
			
			// Update local assets case it's something new
			if(updateLocalAssets) {
				localAssets = FileUtils.getLocalAssets();
			}
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(frame, "Error when try to update from server");
			System.out.println(e.getMessage());
		}
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setBounds(6, 6, 438, 266);
		frame.getContentPane().add(splitPane);
		
		DefaultMutableTreeNode filesTreeNode = new DefaultMutableTreeNode("repository");
		
		// Show local assets at JTree
		for (final Asset asset : localAssets.getAssets()) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(asset);
			filesTreeNode.add(node);
		}
		
		JTree tree = new JTree(filesTreeNode);
		splitPane.setLeftComponent(tree);
		
		tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				
				if (selectedNode.getUserObject() instanceof Asset) {
					// Get selected asset item from JTree
					Asset asset = (Asset)selectedNode.getUserObject();
					
					name.setText(asset.getName());
					version.setText(String.valueOf(asset.getVersion()));
					data.setText(asset.getData());
					
					// Can't change the name
					name.setEnabled(false);
				}
			}
		});
		
		JPanel panel = new JPanel();
		splitPane.setRightComponent(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblName = new JLabel("Name");
		GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.insets = new Insets(0, 0, 5, 5);
		gbc_lblName.anchor = GridBagConstraints.EAST;
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = 0;
		panel.add(lblName, gbc_lblName);
		
		name = new JTextField();
		GridBagConstraints gbc_name = new GridBagConstraints();
		gbc_name.insets = new Insets(0, 0, 5, 0);
		gbc_name.fill = GridBagConstraints.HORIZONTAL;
		gbc_name.gridx = 1;
		gbc_name.gridy = 0;
		panel.add(name, gbc_name);
		name.setColumns(10);
		
		JLabel lblVersion = new JLabel("Version");
		GridBagConstraints gbc_lblVersion = new GridBagConstraints();
		gbc_lblVersion.anchor = GridBagConstraints.EAST;
		gbc_lblVersion.insets = new Insets(0, 0, 5, 5);
		gbc_lblVersion.gridx = 0;
		gbc_lblVersion.gridy = 1;
		panel.add(lblVersion, gbc_lblVersion);
		
		version = new JTextField();
		GridBagConstraints gbc_version = new GridBagConstraints();
		gbc_version.insets = new Insets(0, 0, 5, 0);
		gbc_version.fill = GridBagConstraints.HORIZONTAL;
		gbc_version.gridx = 1;
		gbc_version.gridy = 1;
		panel.add(version, gbc_version);
		version.setColumns(10);
		
		JLabel lblData = new JLabel("Data");
		GridBagConstraints gbc_lblData = new GridBagConstraints();
		gbc_lblData.insets = new Insets(0, 0, 5, 5);
		gbc_lblData.gridx = 0;
		gbc_lblData.gridy = 2;
		panel.add(lblData, gbc_lblData);
		
		data = new JTextArea();
		GridBagConstraints gbc_data = new GridBagConstraints();
		gbc_data.insets = new Insets(0, 0, 5, 0);
		gbc_data.fill = GridBagConstraints.BOTH;
		gbc_data.gridx = 1;
		gbc_data.gridy = 2;
		panel.add(data, gbc_data);
		
		JButton btnSave = new JButton("Save");
		
		// Create click event for save button
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				// Create object to store asset data
				Asset asset = new Asset();
				asset.setName(name.getText());
				asset.setVersion(Integer.parseInt(version.getText()));
				asset.setData(data.getText());
				
				// Try persist data
				try {
					// Send data to server
					api.uploadFile(asset);
					
					// Show an ok message
					JOptionPane.showMessageDialog(frame, "Data persisted with success");
					
					// Clean text fields
					name.setText("");
					version.setText("");
					data.setText("");
					
					// Save data locally
					FileUtils.saveFile(asset);
					
					// Update client.xml
					localAssets = FileUtils.getLocalAssets();
					
					// Update JTree
					filesTreeNode.removeAllChildren();
					for (final Asset assetRefreshed : localAssets.getAssets()) {
						DefaultMutableTreeNode node = new DefaultMutableTreeNode(assetRefreshed);
						filesTreeNode.add(node);
					}
					tree.expandRow(0);
					tree.updateUI();
					
				} catch (Exception e) {
					JOptionPane.showMessageDialog(frame, "Error when try to update data");
					System.out.println(e.getMessage());
				}
			}
		});
		GridBagConstraints gbc_btnSave = new GridBagConstraints();
		gbc_btnSave.gridx = 1;
		gbc_btnSave.gridy = 4;
		panel.add(btnSave, gbc_btnSave);
		
		JButton btnNew = new JButton("New");
		btnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Clean text fields
				name.setText("");
				version.setText("");
				data.setText("");
				
				// Can't change the name
				name.setEnabled(true);
			}
		});
		GridBagConstraints gbc_btnNew = new GridBagConstraints();
		gbc_btnNew.gridx = 0;
		gbc_btnNew.gridy = 4;
		panel.add(btnNew, gbc_btnNew);
	}
}
