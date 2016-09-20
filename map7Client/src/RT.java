

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;

public class RT extends JApplet {

	private static final long serialVersionUID = 1L;
	
	protected static Object readObject(Socket socket) throws ClassNotFoundException, IOException
	{
		Object o;
		ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
		o = in.readObject();
		return o;
	}
	
	protected static void writeObject(Socket socket, Object o) throws IOException
	{
		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		out.writeObject(o);
		out.flush();
	}
	
	private class TabbedPane extends JPanel{
		 private JPanelLearning panelDB;
		 private JPanelLearning panelFile;
		 private  JPanelPredicting panelPredict;
		
		private class JPanelLearning extends JPanel{
			private JTextField tableText=new JTextField(20);
			private JTextArea outputMsg=new JTextArea();
			private JButton executeButton=new JButton("LEARN");
			private JButton saveButton=new JButton("SAVE ON PDF");
			
			JPanelLearning( java.awt.event.ActionListener aLearn, java.awt.event.ActionListener aSave){
				setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
				JPanel  upPanel=new JPanel();
				upPanel.setLayout(new FlowLayout());
				upPanel.add(new JLabel("Table:"));
				upPanel.add(tableText);
				executeButton.addActionListener(aLearn);
				saveButton.addActionListener(aSave);
				saveButton.setEnabled(false);
				
				upPanel.add(executeButton);
				upPanel.add(saveButton);
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
			//private JTextArea queryMsg=new JTextArea(4,50);
			private JPanel queryMsg = new JPanel ();
			private JTextField answer=new JTextField(20);
			private JButton startButton=new JButton("START");
			private JButton executeButton=new JButton("CONTINUE");
			private JLabel predictedClass = new JLabel("");
			private JTree tree = new JTree ();
			
			JPanelPredicting( java.awt.event.ActionListener aStart, java.awt.event.ActionListener aContinue){
				setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
				JPanel  upPanel=new JPanel();
				upPanel.setLayout(new FlowLayout());
				upPanel.add(queryMsg);
				JScrollPane scrollingArea = new JScrollPane(queryMsg);
				upPanel.add(scrollingArea);
				add(upPanel);
				
				JPanel  middlePanel=new JPanel();
				middlePanel.setLayout(new FlowLayout());
				middlePanel.add(new JLabel("Choise:"));
				middlePanel.add(answer);
				startButton.addActionListener(aStart);
				executeButton.addActionListener(aContinue);
				middlePanel.add(startButton);
				middlePanel.add(executeButton);
				add(middlePanel);
				
				JPanel  downPanel=new JPanel();
				downPanel.setLayout(new FlowLayout());
				downPanel.add(predictedClass);
				add(downPanel);	
			}
		}
		
		
		TabbedPane() {
			super(new GridLayout(1, 1)); 
			JTabbedPane tabbedPane = new JTabbedPane();
			//copy img in src Directory and bin directory
			java.net.URL imgURL = getClass().getResource("img/db.jpg");
			ImageIcon iconDB = new ImageIcon(imgURL);
			panelDB = new JPanelLearning(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					learningFromDBAction();
				}
		      },
				new java.awt.event.ActionListener() {
					public void actionPerformed(ActionEvent e) 
					{
							JFileChooser fileChooser = new JFileChooser();
							fileChooser.setMultiSelectionEnabled(false);
							fileChooser.setFileFilter(new FileNameExtensionFilter("PDF file", "pdf"));

							if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) 
							{
								File file = fileChooser.getSelectedFile();
								String fileName = file.getName();
								int i = fileName.indexOf('.');
								try 
								{
									if (i >= 0 && fileName.substring(i + 1).equalsIgnoreCase("pdf"))
										PDFcreator(file.getAbsolutePath(),tab.panelDB.outputMsg.getText());
									
									else
										PDFcreator(file.getAbsolutePath() + ".pdf","NOME TABELLA:   "+ tab.panelDB.tableText.getText() + "\n" + tab.panelDB.outputMsg.getText());
								} 
								catch (Exception e1) 
								{
									e1.printStackTrace();
								}
							}	
					}
				});
	        tabbedPane.addTab("DB", iconDB, panelDB);
	      
	        imgURL = getClass().getResource("img/file.jpg");
	        ImageIcon iconFile = new ImageIcon(imgURL);
			panelFile = new JPanelLearning(new java.awt.event.ActionListener() {
				public void actionPerformed(ActionEvent e) {
						learningFromFileAction();
				}
		      },
				new java.awt.event.ActionListener() {
					public void actionPerformed(ActionEvent e) 
					{
							JFileChooser fileChooser = new JFileChooser();
							fileChooser.setMultiSelectionEnabled(false);
							fileChooser.setFileFilter(new FileNameExtensionFilter("PDF file", "pdf"));

							if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) 
							{
								File file = fileChooser.getSelectedFile();
								String fileName = file.getName();
								int i = fileName.indexOf('.');
								try 
								{
									if (i >= 0 && fileName.substring(i + 1).equalsIgnoreCase("pdf"))
										PDFcreator(file.getAbsolutePath(), tab.panelFile.outputMsg.getText());
									
									else
										PDFcreator(file.getAbsolutePath() + ".pdf", "NOME TABELLA:   "+ tab.panelFile.tableText.getText() + "\n" + tab.panelFile.outputMsg.getText());
								} 
								catch (Exception e1) 
								{
									e1.printStackTrace();
								}
								
							}
						
					}
					
				});
			
	        tabbedPane.addTab("FILE", iconFile, panelFile, "Does nothing");
	        
	        imgURL = getClass().getResource("img/predizione.jpg");
	        ImageIcon iconPredict = new ImageIcon(imgURL);
			panelPredict = new JPanelPredicting(new java.awt.event.ActionListener() {
				public void actionPerformed(ActionEvent e) {
						startPredictingAction();
				}
		      },
			new java.awt.event.ActionListener() {
				public void actionPerformed(ActionEvent e) {
						continuePredictingAction();		
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
	private Socket socket;
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
		socket = new Socket(addr, port); //Port
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
			tab.panelDB.saveButton.setEnabled(true);
			// learning tree		
			System.out.println("Starting learning phase!");
			writeObject(socket,1);
			String nomeTab = tab.panelDB.tableText.getText();
			writeObject(socket,nomeTab);
			String answer=readObject(socket).toString();
			if(answer.equals("OK"))
			{
				tab.panelDB.outputMsg.setText((String)readObject(socket));
				System.out.println("Caricamento da DB effettuato correttamente...");
				JOptionPane.showMessageDialog(this,"Caricamento da DB e salvataggio su file .dmp effettuati correttamente!!!");
				tab.panelDB.saveButton.setEnabled(true);
				return;
			}
			else 
			{
				System.out.println("SALVATAGGIO SU FILE NON RIUSCITO!!!");
				//tab.panelDB.outputMsg.setText("Regression tree learned!");
				tab.panelDB.outputMsg.setText("Errore! - Database non trovato!\n Reinserire il nome della tabella da apprendere");
				JOptionPane.showMessageDialog(this,"TABELLA INESISTENTE!\n"+"Inserire nuovamente il nome della tabella");
			}
		}
		catch(IOException | ClassNotFoundException   e){
			tab.panelDB.outputMsg.setText(e.toString());
		}
		
		
		
	}
	
	
	void learningFromFileAction(){
		try{	
			tab.panelFile.outputMsg.setText("Working ....");
			// store from file
			System.out.println("Starting learning phase!");
			writeObject(socket,2);
			String nomeTab = tab.panelFile.tableText.getText();
			writeObject(socket,nomeTab);
			String answer=readObject(socket).toString();
			if(answer.equals("OK"))
			{
				tab.panelFile.outputMsg.setText((String)readObject(socket));
				System.out.println("Caricamneto da File riuscito correttamente...");
				JOptionPane.showMessageDialog(this,"File caricato correttamente!!!");
				tab.panelFile.saveButton.setEnabled(true);
				return;
			}
			else 
			{
				System.out.println("CARICAMENTO DA FILE NON RIUSCITO!!!");
			   	//tab.panelFile.outputMsg.setText("Regression tree learned!");
				tab.panelFile.outputMsg.setText("Errore! - File inesistente!\n Reinseirire il nome del file da caricare");
				JOptionPane.showMessageDialog(this,"FILE INESISTENTE!\n"+"Inserire nuovamente il nome del file");
			}
		}
		catch(IOException | ClassNotFoundException e){
			tab.panelFile.outputMsg.setText(e.toString());
		}
	}
	
	
	void startPredictingAction(){
		try{		
			tab.panelPredict.startButton.setEnabled(false);			
			writeObject(socket,3);
			System.out.println("Starting prediction phase!");
			String answer=readObject(socket).toString();
			
			/*
			if(answer.equals("QUERY")){
				// Formualting query, reading answer
				answer=readObject(socket).toString();
				tab.panelPredict.queryMsg.setText(answer);
				tab.panelPredict.executeButton.setEnabled(true);
			}
			else
			if(answer.equals("OK")){ 
				// Reading prediction
				answer=readObject(socket).toString();
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
			*/
			
			if(answer.equals("QUERY"))
			{
				answer=readObject(socket).toString();
				DefaultMutableTreeNode root = new DefaultMutableTreeNode ("ROOT");
				int startString;
				int spaceString;
				String subString = "";
				
				List <String> listNodes = new ArrayList<String> ();
				while (answer.length() > 0)
				{
					startString = answer.indexOf(':');
					spaceString = answer.indexOf('\n');
					subString = answer.substring(startString+1, spaceString);
					listNodes.add(subString);
					answer = answer.substring(spaceString+1);
				}
				
				int n = listNodes.size();
				DefaultMutableTreeNode childs [] = new DefaultMutableTreeNode [n];
				
				for (int i = 0; i < n ; i++)
				{
					childs [i] = new DefaultMutableTreeNode (listNodes.get(i));
					root.add(childs[i]);
				}
				
				tab.panelPredict.tree = new JTree(root);
				tab.panelPredict.queryMsg.add(tab.panelPredict.tree);
			}
		}
		catch(IOException | ClassNotFoundException e){
			//tab.panelPredict.queryMsg.setText(e.toString());
			JOptionPane.showMessageDialog(this,e.toString());
			tab.panelPredict.startButton.setEnabled(true);
			tab.panelPredict.executeButton.setEnabled(false);
		}
	}
	
	/*
	void continuePredictingAction(){
		//TO BE DEFINED	
		
		try{		
			writeObject(socket,new Integer(tab.panelPredict.answer.getText()));
			System.out.println("Continuing prediction phase!");
			String answer=readObject(socket).toString();
			
			if(answer.equals("QUERY")){
				answer=readObject(socket).toString();
				tab.panelPredict.queryMsg.setText(answer);
				tab.panelPredict.executeButton.setEnabled(true);
				tab.panelPredict.startButton.setEnabled(false);
			}
			else
			if(answer.equals("OK")){ 
				answer=readObject(socket).toString();
				tab.panelPredict.predictedClass.setText("Predicted class:"+answer);
				tab.panelPredict.queryMsg.setText("");
				tab.panelPredict.startButton.setEnabled(true);
				tab.panelPredict.executeButton.setEnabled(false);
			}
			else {
				//Printing error message
				tab.panelPredict.queryMsg.setText(answer);
				tab.panelPredict.startButton.setEnabled(false);
				tab.panelPredict.executeButton.setEnabled(true);
			}
		}
		catch(IOException | ClassNotFoundException e){
			tab.panelPredict.queryMsg.setText(e.toString());
		}
		*/
	
	/*
	 *  ****LAVORI IN CORSO****
	 */
	void continuePredictingAction()
	{
		//TO BE DEFINED	
		
		try
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tab.panelPredict.tree.getLastSelectedPathComponent();
			DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
			int choice = parent.getIndex(node);
			
			writeObject(socket,new Integer(choice));
			System.out.println("Continuing prediction phase!");
			String answer=readObject(socket).toString();
			
			if(answer.equals("QUERY"))
			{
				answer=readObject(socket).toString();
				int startString;
				int spaceString;
				String subString = "";
				
				List <String> listNodes = new ArrayList<String> ();
				while (answer.length() > 0)
				{
					startString = answer.indexOf(':');
					spaceString = answer.indexOf('\n');
					subString = answer.substring(startString+1, spaceString);
					listNodes.add(subString);
					answer = answer.substring(spaceString+1);
				}
				
				int n = listNodes.size();
				DefaultMutableTreeNode childs [] = new DefaultMutableTreeNode [n];
				
				for (int i = 0; i < n ; i++)
				{
					childs [i] = new DefaultMutableTreeNode (listNodes.get(i));
					node.add(childs[i]);
				}
				

			}
			/*
			else
			if(answer.equals("OK")){ 
				answer=readObject(socket).toString();
				tab.panelPredict.predictedClass.setText("Predicted class:"+answer);
				tab.panelPredict.queryMsg.setText("");
				tab.panelPredict.startButton.setEnabled(true);
				tab.panelPredict.executeButton.setEnabled(false);
			}
			*/
			else {
				//Printing error message
				JOptionPane.showMessageDialog(this,answer);
				tab.panelPredict.startButton.setEnabled(false);
				tab.panelPredict.executeButton.setEnabled(true);
			}
			
		}
		catch(IOException | ClassNotFoundException e){
			JOptionPane.showMessageDialog(this,e.toString());
		}
		
		
	}
	
	
	void PDFcreator (String title, String text)
	{
		try
		{
			
	        PDDocument doc = new PDDocument();
	        PDPage page = new PDPage();
	        doc.addPage(page);
	        PDPageContentStream contentStream = new PDPageContentStream(doc, page);

	        PDFont pdfFont = PDType1Font.HELVETICA;
	        float fontSize = 12;
	        float leading = 1.5f * fontSize;

	        PDRectangle mediabox = page.findMediaBox();
	        float margin = 72;
	        float startX = mediabox.getLowerLeftX() + margin/2;
	        float startY = mediabox.getUpperRightY() - margin;
			float center = mediabox.getWidth() /5.0f;
	      	
	        List<String> lines = new ArrayList<String>();
	               	     
	        int lastSpace = -1;
	                	
	        while (text.length() > 0)
	        {
	            int spaceIndex = text.indexOf('\n');
	                              	
	            if (spaceIndex < 0)
	            {   
	            	       	
	                lines.add(text);
	                text = "";
	            }
	            else
	            {
	                String subString = text.substring(0, spaceIndex);
					if (lastSpace < 0)
						lastSpace = spaceIndex;
					else
						lastSpace = spaceIndex;
					lines.add(subString);
					text = text.substring(lastSpace).trim();
					lastSpace = -1;
	            }
	        }

	        contentStream.beginText();
	        contentStream.setFont(pdfFont, fontSize);
	        contentStream.moveTextPositionByAmount(startX, startY + margin - center);
	        
	        for (String line: lines)
	        {
	            contentStream.drawString(line);
	            contentStream.moveTextPositionByAmount(0, -leading);
	        }
	        contentStream.endText(); 

			/*
			try 
			{
				File f = new File (img);
				BufferedImage awtImage = ImageIO.read(f);
				PDXObjectImage ximage = new PDPixelMap(doc, awtImage);
				float scale = 0.5f; // alter this value to set the image size
	        	contentStream.drawXObject(ximage, 100, 400, ximage.getWidth()*scale, ximage.getHeight()*scale);
			} 
	    
			catch (FileNotFoundException fnfex) 
			{
				System.out.println("No image for you");
			}
			*/
			contentStream.close();
			doc.save(title);
			doc.close();
			System.out.println("File PDF salvato correttamente...");
			JOptionPane.showMessageDialog(this,"File PDF salvato correttamente!!!");
			
			
			}
		catch (Exception e) {}
	}
}
