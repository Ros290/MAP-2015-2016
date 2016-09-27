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

/*
 * Definisce un applet eseguibile in un browser web
 */
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
	
	/*
	 * Classe privata che estende JPanel
	 * Tale classe è inner class di RT
	 */
	private class TabbedPane extends JPanel{
		 private JPanelLearning panelDB;
		 private JPanelLearning panelFile;
		 private  JPanelPredicting panelPredict;
		
		 /*
		  * Classe privata che estende JPanel
		  * Tale classe è inner class di TabbedPane
		  */
		 private class JPanelLearning extends JPanel{
			private JTextField tableText=new JTextField(20);
			private JTextArea outputMsg=new JTextArea();
			private JButton executeButton=new JButton("LEARN");
			private JButton saveButton=new JButton("SAVE ON PDF");
			
			protected JFileChooser fileChooser = new JFileChooser();
			
			/*
			  * Inizializza il pannello ed aggiunge l'ascoltatore al bottone executeButton ed al bottone saveButton
			  */
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
		
		 /*
		  * Classe privata che estende JPanel
		  * Tale classe è inner class di TabbedPane
		  */
		private class JPanelPredicting extends JPanel{
			private JPanel queryMsg = new JPanel ();
			private JButton startButton=new JButton("START");
			private JLabel predictedClass = new JLabel("");
			// struttura quale conterrà la rappresentazione grafica dell'albero di regressione
			private JTree tree = new JTree();
			// questa flag indica se si sta effettuando la prima predizione dall'avvio del client o no.
			// per capire a cosa effettivamente serve, andare a vedere startPrediction()
			private boolean firstPred = true;
			// indica se il client sta ancora operando sulla predizione (ovvero, non ha ancora terminato).
			// all'avvio di qualsiasi predizione, viene settato a true, così da NON permettere di cambiare tabella
			// fino a quando non termina la predizione
			private boolean isPredicting = false;
			//variabili necessari per catturari i click eseguiti sui nodi dell'albero di regressione
		    private boolean singleClick  = true;
		    private int doubleClickDelay = 300;
		    private Timer timer;     
			
		    /*
			  * Inizializza il pannello ed aggiunge l'ascoltatore al bottone startButton
			  */
		    
			//notare che, come secondo parametro, c'è MouseListener, questo perchè
		    //non sarà più soltanto il bottone a definire una determinata azione, ma anche i click effettuati col mouse
		    
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
		
		/*
		  * TabbedPane inizializza i membri panelDB, panleFile e panelPredict 
		  * e li aggiunge ad un oggetto istanza della classe TabbedPane, che è poi inserito nel pannello che si sta costruendo
		  */
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
		      // per ogni click effettuato, il sistema rimanda a questa porzione di codice
		      new MouseAdapter() {
		    	  // la variabile 'e' conterrà tutte le info determinanti a stabilire quanti click sono stati compiuti
		          public void mouseClicked(MouseEvent e) {
		        	  // verifico che , prima di tutto, siano stati effettuati due click (indipendentemente se li ha fatto clickando su un
		        	  // o su un punto vuoto dello schermo
		              if (e.getClickCount() == 2) {
		            	  // catturo il nodo selezionato ( node = tree.getLastSelectedPathComponent())
		            	  // se non è stato clickato alcun nodo, allora node = null
		                  DefaultMutableTreeNode node = (DefaultMutableTreeNode) tab.panelPredict.tree.getLastSelectedPathComponent();
		                  // quindi ci assicuriamo che, prima di passare a continuePredicting, sia stato effettivamente clickato
		                  // su un nodo (if node != null) e , in tal caso , se esso sia effettivamente un nodo figlio o n
		                  // (if node.isLeaf), altrimenti non fa nulla
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
	
	/*
	  * Inizializza la componente grafica dell'applet istanziando un oggetti della classe JTabbedPane ed aggiungendo al container dell'applet
	  * Avvia la richiesta di connessione al Server ed inizializza i flussi di comunicazione
	  */
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
	
	/*
	 * Acquisisce il nome della tabella e lo trasmette al Server
	 * Trasmette al server il comando 1
	 * Infine visualizza i messaggi inviati dal server
	  */
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
	
	/*
	 * Trasmette al server il comando 2
	 * Acquisisce il nome della tabella e lo trasmette al Server
	 * Infine visualizza i messaggi inviati dal server
	  */
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
	
	/*
	 * Trasmette al server il comando 3
	 * Legge la risosta
	 * Se la risposta è QUERY allora legge la successiva risposta dal server e la visualizza, 
	 * altrimenti de la risposta è OK legge la succesiva risposta dal server e la visualizza, abilitando il pulsante START,
	 * altrimenti visulizza la risposta e abilita il pulsante START
	  */
	void startPredictingAction(){
		try{		
			tab.panelPredict.startButton.setEnabled(false);			
			writeObject(socket,3);
			System.out.println("Starting prediction phase!");
			String answer=readObject(socket).toString();			
			if(answer.equals("QUERY"))
			{
				// setto la flag a true, così da nn permettere l'acquisizione di nuove tabelle fino alla fine della predizione
				tab.panelPredict.isPredicting = true;
				answer=readObject(socket).toString();
				// da qua comincia la costruzione dell'albero di regressione, per semplicità ho denominato la radice
				// come "Root"
				DefaultMutableTreeNode root = new DefaultMutableTreeNode ("ROOT");
				// il codice che sta di seguito riportato, serve per riconoscere i nodi dalla stringa
				// ottenuta dal server (infatti il server non è stato proprio toccato)
				int startString;
				int spaceString;
				String subString = "";
				// qui saranno contenute le stringhe che rappresenteranno i nodi figli di "Root"
				List <String> listNodes = new ArrayList<String> ();
				// premessa: vi ricordo che il server non è stato cambiato, quindi la stringa che
				// rappresenta la richiesta del server è così strutturata:
				// *** 0: X = A\n ***
				// *** 1: X = B\n ***
				// eccetera... (p.s. gli asterschi non c'entrano).
				while (answer.length() > 0)
				{
					// definisco in start la posizione del primo : che trovo nella stringa
					// 0: X = A\n , quindi start = 2
					startString = answer.indexOf(':');
					// definisco in space la posizione del primo \n che trovo nella stringa
					// 0: X = A\n , quindi space = 9
					spaceString = answer.indexOf('\n');
					// definisco una sotto stringa che consista nella parte che va da dopo : fino a \n e lo aggiungo alla lista
					// quindi da essere 0: X = A\n, diventa solo X = A
					subString = answer.substring(startString+1, spaceString);
					listNodes.add(subString);
					// rimuovo la sotto stringa appena acquisita come nodo, e passo alle successive, partendo 
					// dalla posizione succesiva di quella dell'ultima \n ricavata
					// quindi passo ad analizzare 1: X = B\n e così via dicendo
					answer = answer.substring(spaceString+1);
				}
				int n = listNodes.size();
				// definisco tanti nodi quanti sono le stringhe contenute nella lista
				DefaultMutableTreeNode childs [] = new DefaultMutableTreeNode [n];
				// definisco ciascun nodo con la stringa associata nella lista, e lo definisco come figlio di Root ( root.add(childs[i]) )
				for (int i = 0; i < n ; i++)
				{
					childs [i] = new DefaultMutableTreeNode (listNodes.get(i));
					root.add(childs[i]);
				}
				tab.panelPredict.predictedClass.setText("*** Phase Prediction started ***");
				if (tab.panelPredict.firstPred)
				{
					// in pratica non faccio altro che caricare in schermata il modello attuale dell'albero, semplicemente
					DefaultTreeModel model = (DefaultTreeModel)tab.panelPredict.tree.getModel();
					model.setRoot(root);
					tab.panelPredict.queryMsg.add(tab.panelPredict.tree);
				}
				else
				{
					// premessa: appena si effettua una nuova predizione, bisogna caricare su video la nuova struttura
					// dell'albero, qualora siano state fatte altre predizioni. Il problema è che, anche se decidiamo di
					// cancellare totalmente l'albero della precidizione precedente, per far spazio ad uno nuovo, 
					// sullo schermo rimangono però " i segni" dell'albero precedente... nel senso che alcuni nodi del vecchio
					// albero rimangono visibili
					// Per ovviare a questo problema, sono stati adottati i "modelli" DefaultTreeModel, quale permettono, diciamo, azioni
					// in più rispetto ad un semplice JTree, sopratutto per quanto riguarda la rappresentazione grafica.
					// In sostanza, se volete capire coi vostri occhi del motivo di tutto ciò, sostituite DefaultTreeModel con Jtree.
					
					// questa cosa non doveva esserci , vabbè mia svista
					tab.panelPredict.predictedClass.setText("");
					// definisco il modello dell'albero della predizione PRECEDENTE
					DefaultTreeModel model = (DefaultTreeModel)tab.panelPredict.tree.getModel();
					// definisco la radice sempre dello stesso albero come NewRoot, e rimuovo tutti i suoi figli, quindi anche i figli dei figli ecc
					// così facendo riavremo nuovamente il nostro albero che ha solo un nodo radice e sarà il punto di partenza del nuovo albero
					DefaultMutableTreeNode newRoot = (DefaultMutableTreeNode) model.getRoot();
					newRoot.removeAllChildren();
					// attenzione, la funzione getChildAt di un nodo, funge come la funzione pop() di una pila,
					// ovvero rimuovendo l'elemento preso dalla struttura che lo conteneva. Quindi per ogni root.getChildAt, il numero dei
					// figli di root diminuisce
					while (root.getChildCount()!=0)
						newRoot.add((DefaultMutableTreeNode)root.getChildAt(0));
					// la comodità di definire il modello dell'albero, è che permette di ricaricare la rappresentazione grafica 
					// dell'albero, per quante aggiunte/rimozioni siano state fatte
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
			// definisco in node, il nodo (foglia) selezionato
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tab.panelPredict.tree.getLastSelectedPathComponent();
			// definisco in parent, il padre di node
			DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
			// definisco l'indice di parent
			int choice = parent.getIndex(node);
			// qui cancelliamo tutti i figli di parent, a parte node.
			// questo serve affinchè il client non possa optare per "ritornare indietro" col percorso
			// e scegliere un nodo differrente dalla quello scelto, perchè questa versione del programma
			// non consente azioni del genere
			int nChildren = parent.getChildCount() -1;
			while (nChildren >= 0 )
			{
				if (nChildren != choice)
					parent.remove(nChildren);
				nChildren --;
			}
			DefaultTreeModel model = (DefaultTreeModel)tab.panelPredict.tree.getModel();
			DefaultMutableTreeNode root = (DefaultMutableTreeNode) parent;
			// ricarico l'albero, poichè c'è stata una modifica nella struttura
			model.reload(root);
			writeObject(socket,new Integer(choice));
			System.out.println("Continuing prediction phase!");
			String answer=readObject(socket).toString();
			if(answer.equals("QUERY"))
			{
				// questa parte è praticamente identica a startPrediction, solo che i nodi ricavati
				// dal server vengono aggiunti direttamente su node (il nodo selezionato)
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
				//senza questa istruzione, vedremmo solo il nodo selezionato , per vedere i suoi figli dovremmo clickarci sopra
				// così invece vediamo subito quali siano i suoi figli
				tab.panelPredict.tree.expandPath(new TreePath(node.getPath()));
			}
			else if(answer.equals("OK"))
			{ 
				// la predizione è terminata, quindi isPred = false
				tab.panelPredict.isPredicting = false;
				answer=readObject(socket).toString();
				tab.panelPredict.predictedClass.setText("Predicted class:"+answer);
				tab.panelPredict.startButton.setEnabled(true);
				// che sia o no la prima predizione questa, firstPred viene comunque settata a false
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



