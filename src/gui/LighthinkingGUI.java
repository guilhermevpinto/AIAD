package gui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JFrame;

import lighthinking.Config;
import lighthinking.Lighthinking;
import lighthinking.agent.Agent;
import lighthinking.agent.Agent.Type;
import trasmapi.genAPI.exceptions.TimeoutException;
import trasmapi.genAPI.exceptions.UnimplementedMethod;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.JCheckBox;

public class LighthinkingGUI {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LighthinkingGUI window = new LighthinkingGUI();
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
	public LighthinkingGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 544, 486);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		JComboBox<Type> comboBox = new JComboBox(Agent.Type.values());
		comboBox.setBounds(183, 50, 162, 20);
		frame.getContentPane().add(comboBox);
		
		JLabel lblNewLabel = new JLabel("Agent type:");
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel.setBounds(34, 53, 148, 14);
		frame.getContentPane().add(lblNewLabel);
		
		JButton btnStart = new JButton("START");
		btnStart.setBounds(212, 379, 105, 41);
		frame.getContentPane().add(btnStart);
		
		JCheckBox chckbxUseJadeGui = new JCheckBox("Use JADE gui");
		chckbxUseJadeGui.setBounds(212, 90, 225, 23);
		frame.getContentPane().add(chckbxUseJadeGui);
		
		
		
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				frame.dispose();
				
				Config conf = new Config();
				
				conf.jadeGUI = chckbxUseJadeGui.isSelected();
				conf.agentType = (Type) comboBox.getSelectedItem();
				
				try {
					Lighthinking.start(conf);
				} catch (IOException | TimeoutException | UnimplementedMethod | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
}
