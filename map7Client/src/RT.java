

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.management.timer.Timer;
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
import javax.swing.JTree;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

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
			
			protected JFileChooser fileChooser = new JFileChooser();
			
			
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
			private JPanel queryMsg = new JPanel ();
			private JButton startButton=new JButton("START");
			private JLabel predictedClass = new JLabel("");
			private JTree tree = new JTree();
			private boolean firstPred = true;
			private boolean isPredicting = false;
		    private boolean singleClick  = true;
		    private int doubleClickDelay = 300;
		    private Timer timer;    
			
			
			JPanelPredicting( java.awt.event.ActionListener aStart, MouseListener aContinue){
				setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
				JPanel  upPanel=new JPanel();
				queryMsg.setPreferredSize(new Dimension (512,256));
				upPanel.setLayout(new FlowLayout());
				upPanel.add(queryMsg);
				JScrollPane scrollingArea = new JScrollPane(queryMsg);
				upPanel.add(scrollingArea);
				add(upPanel);
				
				JPanel  middlePanel=new JPanel();
				middlePanel.setLayout(new FlowLayout());
				startButton.setEnabled(false);
				startButton.addActionListener(aStart);
				tree.addMouseListener(aContinue);
				middlePanel.add(startButton);
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
				
						
							((JFileChooser) tab.panelDB.fileChooser).setFileSelectionMode(JFileChooser.FILES_ONLY);

							int choice = tab.panelDB.fileChooser.showSaveDialog(getParent());

							if (choice != JFileChooser.APPROVE_OPTION) return;

							String dest=tab.panelDB.fileChooser.getSelectedFile().getAbsolutePath()+".pdf";
							File file = new File(dest);

							file.getParentFile().mkdirs();
	
							Document document = new Document();

						
							try {
								PdfWriter.getInstance(document, new FileOutputStream(dest));
							} catch (FileNotFoundException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (DocumentException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							

							com.itextpdf.text.Rectangle two = new com.itextpdf.text.Rectangle(700,400);

							document.open();

							Paragraph p = new Paragraph("Tabella: "+tab.panelDB.tableText.getText());
							p.add(new Paragraph(" "));
							try {
								document.add(p);
							} catch (DocumentException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							
							Paragraph p1 = new Paragraph("REGRESSION TREE E REGOLE GENERATE  \n \n"+tab.panelDB.outputMsg.getText());
							try {
								document.add(p1);
							} catch (DocumentException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							
							document.setPageSize(two);
  							document.close();
  							
  							System.out.println("Pdf salvato correttamente....");
							JOptionPane.showMessageDialog(tab.panelDB, "Pdf salvato correttamente!!!");	
							
					    
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
							
							((JFileChooser) tab.panelFile.fileChooser).setFileSelectionMode(JFileChooser.FILES_ONLY);

							int choice = tab.panelFile.fileChooser.showSaveDialog(getParent());

							if (choice != JFileChooser.APPROVE_OPTION) return;

							String dest=tab.panelFile.fileChooser.getSelectedFile().getAbsolutePath()+".pdf";
							File file = new File(dest);

							file.getParentFile().mkdirs();
	
							Document document = new Document();

						
							try {
								PdfWriter.getInstance(document, new FileOutputStream(dest));
							} catch (FileNotFoundException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (DocumentException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							

							com.itextpdf.text.Rectangle two = new com.itextpdf.text.Rectangle(700,400);

							document.open();

							Paragraph p = new Paragraph("Tabella: "+tab.panelFile.tableText.getText());
							p.add(new Paragraph(" "));
							try {
								document.add(p);
							} catch (DocumentException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							
							Paragraph p1 = new Paragraph("REGRESSION TREE E REGOLE GENERATE  \n \n"+tab.panelFile.outputMsg.getText());
							try {
								document.add(p1);
							} catch (DocumentException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							
							document.setPageSize(two);
  							document.close();
  							
  							System.out.println("Pdf salvato correttamente....");
							JOptionPane.showMessageDialog(tab.panelFile, "Pdf salvato correttamente!!!");	
							
					    
					
						
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
		      new MouseAdapter() {
		          public void mouseClicked(MouseEvent e) {
		              if (e.getClickCount() == 2) {
		                  DefaultMutableTreeNode node = (DefaultMutableTreeNode) tab.panelPredict.tree.getLastSelectedPathComponent();
		                  if ((node != null) && (node.isLeaf()) && (tab.panelPredict.isPredicting)) continuePredictingAction();
		              }
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
			
			System.out.println("Server non avviato!!!");
			JOptionPane.showMessageDialog(this,e.toString()+ " SERVER OFFLINE!!!");
			System.exit(0);
		}
	}
	
	void learningFromDBAction(){
		try
		{
			if (!tab.panelPredict.isPredicting)
			{
				tab.panelDB.outputMsg.setText("Working ....");
				tab.panelDB.saveButton.setEnabled(false);
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
					tab.panelPredict.startButton.setEnabled(true);
					return;
				}
				else
				{
					JOptionPane.showMessageDialog(this,(String)readObject(socket));
					tab.panelDB.saveButton.setEnabled(false);
				}
			}
			else
				JOptionPane.showMessageDialog(this,"PREDIZIONE IN CORSO!\n"+"Impossibile impostare una nuova tabella fino al termine della operazione");
		}
		catch(IOException | ClassNotFoundException   e){
			JOptionPane.showMessageDialog(this,e.toString());
		}
		
		
		
	}
	
	
	void learningFromFileAction(){
		try{
			if (!tab.panelPredict.isPredicting)
			{
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
				tab.panelPredict.startButton.setEnabled(true);
				return;
			}
			else 
			{
				System.out.println("CARICAMENTO DA FILE NON RIUSCITO!!!");
				JOptionPane.showMessageDialog(this,(String)readObject(socket));
				tab.panelFile.saveButton.setEnabled(false);
			}
			}
			else
				JOptionPane.showMessageDialog(this,"PREDIZIONE IN CORSO!\n"+"Impossibile impostare una nuova tabella fino al termine della operazione");
		}
		catch(IOException | ClassNotFoundException e){
			tab.panelFile.outputMsg.setText(e.toString());
			JOptionPane.showMessageDialog(this,e.toString());
		}
	}
	
	
	void startPredictingAction(){
		try{		
			tab.panelPredict.startButton.setEnabled(false);			
			writeObject(socket,3);
			System.out.println("Starting prediction phase!");
			String answer=readObject(socket).toString();			
			if(answer.equals("QUERY"))
			{
				tab.panelPredict.isPredicting = true;
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
				tab.panelPredict.predictedClass.setText("*** Phase Prediction started ***");
				if (tab.panelPredict.firstPred)
				{
					DefaultTreeModel model = (DefaultTreeModel)tab.panelPredict.tree.getModel();
					model.setRoot(root);
					tab.panelPredict.queryMsg.add(tab.panelPredict.tree);
				}
				else
				{
					tab.panelPredict.predictedClass.setText("");
					DefaultTreeModel model = (DefaultTreeModel)tab.panelPredict.tree.getModel();
					DefaultMutableTreeNode newRoot = (DefaultMutableTreeNode) model.getRoot();
					newRoot.removeAllChildren();
					while (root.getChildCount()!=0)
						newRoot.add((DefaultMutableTreeNode)root.getChildAt(0));
					model.reload(newRoot);
				}
			}
			else if(answer.equals("OK"))
			{ 
				tab.panelPredict.isPredicting = false;
				answer=readObject(socket).toString();
				tab.panelPredict.predictedClass.setText("Predicted class:"+answer);
				tab.panelPredict.startButton.setEnabled(true);
			}
			else {
				//Printing error message
				JOptionPane.showMessageDialog(this,answer);
				tab.panelPredict.startButton.setEnabled(true);
			}
		}
		catch(IOException | ClassNotFoundException e){
			
			JOptionPane.showMessageDialog(this,e.toString());
			tab.panelPredict.startButton.setEnabled(true);
		}
	}
	
	void continuePredictingAction()
	{
		try
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tab.panelPredict.tree.getLastSelectedPathComponent();
			DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
			int choice = parent.getIndex(node);
			int nChildren = parent.getChildCount() -1;
			while (nChildren >= 0 )
			{
				if (nChildren != choice)
					parent.remove(nChildren);
				nChildren --;
			}
			DefaultTreeModel model = (DefaultTreeModel)tab.panelPredict.tree.getModel();
			DefaultMutableTreeNode root = (DefaultMutableTreeNode) parent;
			model.reload(root);
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
				model.reload(root);
				tab.panelPredict.tree.expandPath(new TreePath(node.getPath()));
			}
			else if(answer.equals("OK"))
			{ 
				tab.panelPredict.isPredicting = false;
				answer=readObject(socket).toString();
				tab.panelPredict.predictedClass.setText("Predicted class:"+answer);
				tab.panelPredict.startButton.setEnabled(true);
				tab.panelPredict.firstPred = false;
			}
			else {
				//Printing error message
				JOptionPane.showMessageDialog(this,answer);
				tab.panelPredict.startButton.setEnabled(true);
			}	
		}
		catch(IOException | ClassNotFoundException e){
			JOptionPane.showMessageDialog(this,e.toString());
		}
	}
	
	
	
	
	public void mouseClicked(MouseEvent e) 
	{ 
	    if (e.getClickCount() == 1) {
	    	tab.panelPredict.singleClick = true;
	    	tab.panelPredict.timer.start();
	    } else {
	    	tab.panelPredict.singleClick = false;
	    }
	}
}



