package map7Client;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.SQLException;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class RT extends JApplet {

	private static final long serialVersionUID = 1L;
	
	private class TabbedPane extends JPanel{
		 private JPanelLearning panelDB;
		 private JPanelLearning panelFile;
		 private  JPanelPredicting panelPredict;
		
		private class JPanelLearning extends JPanel{
			private JTextField tableText=new JTextField(20);
			private JTextArea outputMsg=new JTextArea();
			private JButton executeButton=new JButton("LEARN");;
			
			JPanelLearning( java.awt.event.ActionListener a){
				setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
				JPanel  upPanel=new JPanel();
				upPanel.setLayout(new FlowLayout());
				upPanel.add(new JLabel("Table:"));
				upPanel.add(tableText);
				executeButton.addActionListener(a);
				
				upPanel.add(executeButton);
				add(upPanel);
				
				JPanel  bottomPanel=new JPanel();
				bottomPanel.setLayout(new GridLayout(1,1));
				outputMsg.setEditable(false);
				JScrollPane scrollingArea = new JScrollPane(outputMsg);
				bottomPanel.add(scrollingArea);
				add(bottomPanel);
				
				
			}
		}
		
		private class JPanelPredicting extends JPanel{
			//TO BE DEFINED
			private JTextArea queryMsg=new JTextArea(4,50);
			private JTextField answer=new JTextField(20);
			private JButton startButton=new JButton("START");
			private JButton executeButton=new JButton("CONTINUE");
			private JLabel predictedClass = new JLabel("");
			
			JPanelPredicting( java.awt.event.ActionListener aStart, java.awt.event.ActionListener aContinue){
				// TO BE DEFINED
				setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
				JPanel  upPanel=new JPanel();
				upPanel.setLayout(new FlowLayout());
				upPanel.add(queryMsg);
				JScrollPane scrollingArea = new JScrollPane(queryMsg);
				upPanel.add(scrollingArea);
				add(upPanel);
				
				JPanel  middlePanel=new JPanel();
				middlePanel.setLayout(new FlowLayout());
				middlePanel.add(answer);
				startButton.addActionListener(aStart);
				executeButton.addActionListener(aContinue);
				middlePanel.add(startButton);
				middlePanel.add(executeButton);
				add(middlePanel);
				
				JPanel  downPanel=new JPanel();
				//downPanel.setLayout(new GridLayout(1,1));
				downPanel.setLayout(new FlowLayout());
				downPanel.add(new JLabel("predictedClass:"));
				add(downPanel);	
			}
		}
		
		
		TabbedPane() {
			super(new GridLayout(1, 1)); 
			JTabbedPane tabbedPane = new JTabbedPane();
			//copy img in src Directory and bin directory
			//java.net.URL imgURL = getClass().getResource("img/db.jpg");
			ImageIcon iconDB = new ImageIcon("DB");
			panelDB = new JPanelLearning(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					learningFromDBAction();
				}
		      });
	        tabbedPane.addTab("DB", iconDB, panelDB, "Does nothing");
	      
	        //imgURL = getClass().getResource("img/file.jpg");
	        ImageIcon iconFile = new ImageIcon("FILE");
			panelFile = new JPanelLearning(new java.awt.event.ActionListener() {
				public void actionPerformed(ActionEvent e) {
						learningFromFileAction();
				}
		      });
	        tabbedPane.addTab("FILE", iconFile, panelFile, "Does nothing");
	        
	        //imgURL = getClass().getResource("img/predict.jpg");
	        ImageIcon iconPredict = new ImageIcon("PREDICT");
			panelPredict = new JPanelPredicting(new java.awt.event.ActionListener() {
				public void actionPerformed(ActionEvent e) {
						startPredictingAction();
				}
		      },
			new java.awt.event.ActionListener() {
				public void actionPerformed(ActionEvent e) {
						//continuePredictingAction();		
				}
			});
	        tabbedPane.addTab("PREDICT", iconPredict, panelPredict, "Does nothing");
	        
	        //Add the tabbed pane to this panel.
	        add(tabbedPane);         
	        //The following line enables to use scrolling tabs.
	        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		}
	}
	
	
	private ObjectOutputStream out;
	private ObjectInputStream in ; // stream con richieste del client
	private TabbedPane tab;
	
	public void init()
	{
		final int port = 8080;
		tab=new TabbedPane();
		getContentPane().setLayout(new GridLayout(1,1));
		getContentPane().add(tab);
		
		this.setSize(1000,500);
		
		String ip=this.getParameter("ServerIP");
		
		//int port=new Integer(this.getParameter("Port")).intValue();
		System.out.println("port");
		try
		{
		InetAddress addr = InetAddress.getByName(ip); //ip
		System.out.println("addr = " + addr);
		Socket socket = new Socket(addr, port); //Port
		System.out.println(socket);
		
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream()); 
		}
		catch(IOException e){
			System.exit(0);
		}
	}
	
	void learningFromDBAction(){
		try{	
			tab.panelDB.outputMsg.setText("Working ....");
			// learning tree
			System.out.println("Starting learning phase!");
			out.writeObject(1);
			String nomeTab = tab.panelDB.tableText.getText();
			out.writeObject(nomeTab);
			String answer=in.readObject().toString();
			if(answer.equals("OK"))
			{
				tab.panelDB.outputMsg.setText((String)in.readObject());
				System.out.println("FILE SALVATO CORRETTAMENTE!!!");
				JOptionPane.showMessageDialog(this,"File salvato correttamente!!!");
				return;
			}
			else 
				System.out.println("SALVATAGGIO SU FILE NON RIUSCITO!!!");
				tab.panelDB.outputMsg.setText("Regression tree learned!");
		}
		catch(IOException | ClassNotFoundException   e){
			tab.panelDB.outputMsg.setText(e.toString());
		}
	}
	
	
	void learningFromFileAction(){
		// TO BE DEFINED
		try{	
			tab.panelFile.outputMsg.setText("Working ....");
			// store from file
			System.out.println("Starting learning phase!");
			out.writeObject(2);
			String nomeTab = tab.panelFile.tableText.getText();
			out.writeObject(nomeTab);
			String answer=in.readObject().toString();
			if(answer.equals("OK"))
			{
				tab.panelFile.outputMsg.setText((String)in.readObject());
				System.out.println("FILE CARICATO CORRETTAMENTE");
				JOptionPane.showMessageDialog(this,"File caricato correttamente!!!");
				return;
			}
			else 
				System.out.println("CARICAMENTO NON RIUSCITO!!!");
				tab.panelFile.outputMsg.setText("Regression tree learned!");
		}
		catch(IOException | ClassNotFoundException e){
			tab.panelFile.outputMsg.setText(e.toString());
		}
	}
	
	void startPredictingAction(){
		try{		
			
			
		
			tab.panelPredict.startButton.setEnabled(false);
			out.writeObject(3);
			System.out.println("Starting prediction phase!");
			String answer=in.readObject().toString();
			
			
			if(answer.equals("QUERY")){
				// Formualting query, reading answer
				answer=in.readObject().toString();
				tab.panelPredict.queryMsg.setText(answer);
				tab.panelPredict.executeButton.setEnabled(true);
			}
			else
			if(answer.equals("OK")){ 
				// Reading prediction
				answer=in.readObject().toString();
				tab.panelPredict.predictedClass.setText("Predicted class:"+answer);
				tab.panelPredict.queryMsg.setText("");
				tab.panelPredict.startButton.setEnabled(true);
				tab.panelPredict.executeButton.setEnabled(false);
			}
			else {
				//Printing error message
				tab.panelPredict.queryMsg.setText(answer);
				tab.panelPredict.startButton.setEnabled(true);
				tab.panelPredict.executeButton.setEnabled(false);
			}
		}
		catch(IOException | ClassNotFoundException e){
			tab.panelPredict.queryMsg.setText(e.toString());
		}
	}
	
	/*
	void continuePredictingAction(){
		//TO BE DEFINED
	}*/
}
