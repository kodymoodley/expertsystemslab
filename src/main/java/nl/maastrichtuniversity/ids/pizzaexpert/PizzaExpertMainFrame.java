package nl.maastrichtuniversity.ids.pizzaexpert;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxOWLObjectRendererImpl;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.Node;

public class PizzaExpertMainFrame{
	//private static final Insets bottomInsets = new Insets(10, 10, 10, 10);
	private static final Insets normalInsets = new Insets(10, 10, 0, 10);
	// GUI components
	private JRadioButton rbSmall, rbMedium, rbLarge, rbThinCrust, rbMediumCrust, rbPan;
	private JFrame frame;
	private static OWLOntology ontology;
	private static ManchesterOWLSyntaxOWLObjectRendererImpl man = new ManchesterOWLSyntaxOWLObjectRendererImpl();
	private static String [] unsats;
	private static OWLDataFactory df;
	private static DefaultListModel<String> toppingListModel; 
	private static JList<String> list;  
	
	public static void main(String[] args) throws OWLOntologyCreationException {
		try {
			toppingListModel = new DefaultListModel<String>();
			df = OWLManager.getOWLDataFactory();
			ontology = OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(new File("resources/pizza.owl"));
			Reasoner reasoner = new Reasoner(new Configuration(), ontology);
			OWLClass pizzaTopping = df.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI().get()+"#PizzaTopping"));
			Set<OWLClass> classes_before = reasoner.getSubClasses(pizzaTopping, false).getFlattened();
			Node<OWLClass> unsatclses = reasoner.getUnsatisfiableClasses();
			Set<OWLClass> classes = new HashSet<OWLClass>();
			
			for (OWLClass item : classes_before) {
				if (!unsatclses.contains(item)){
					System.out.println(item);
					classes.add(item);
				}
			}
			
			Set<OWLClass> direct_classes = reasoner.getSubClasses(pizzaTopping, true).getFlattened();
			classes.removeAll(direct_classes);
			classes.remove(df.getOWLNothing());
			classes.remove(df.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI().get()+"#NutTopping")));
			classes.remove(df.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI().get()+"#VegetableTopping")));
			classes.remove(df.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI().get()+"#SauceTopping")));
			classes.remove(df.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI().get()+"#CheeseTopping")));
			classes.remove(df.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI().get()+"#FruitTopping")));

			System.out.println("Number of pizza toppings in the ontology: " + classes.size());
			unsats = new String[classes.size()];
			int i = 0;
			for (OWLClass c: classes) {
				String newStr = man.render(c);
				newStr = newStr.replace("Topping","");
				unsats[i] = newStr;
				i++;
			}
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}

		try {
			PizzaExpertMainFrame window = new PizzaExpertMainFrame();
			window.frame.setVisible(true);

		} catch (Exception e) {
			e.printStackTrace();
		}
	} // End of main

	public PizzaExpertMainFrame() {
		initialize();

	}

	/**
	 * @wbp.parser.entryPoint
	 */
	private void initialize() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		screenSize.getWidth();
		screenSize.getHeight();

		frame = new JFrame("Luigi's Pizzeria Maastricht");
		frame.setSize(600, 400);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocationByPlatform(true);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setBackground( Color.WHITE );
		int gridy = 0;

		addComponent(mainPanel, createTitlePanel(), 0, gridy++, 2, 1,
				normalInsets, GridBagConstraints.LINE_START,
				GridBagConstraints.HORIZONTAL);

		JPanel eastPanel = new JPanel();
		eastPanel.setLayout(new BorderLayout());
		eastPanel.setBackground( Color.WHITE );
		eastPanel.add(createSizePanel(), BorderLayout.WEST);
		eastPanel.add(createTypePanel(), BorderLayout.CENTER);
		eastPanel.add(createToppingPanel(), BorderLayout.EAST);

		eastPanel.add(createButtonPanel(), BorderLayout.SOUTH);

		addComponent(mainPanel, eastPanel, 1, gridy++, 1, 1, normalInsets,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL);

		frame.getContentPane().add(mainPanel);
		frame.pack();
		frame.setVisible(true);
	}

	private JPanel createTitlePanel() {
		JPanel panel = new JPanel();
		panel.setBackground( Color.WHITE );

		ImageIcon logo = new ImageIcon("resources/logo.png");
		JLabel lblLogo = new JLabel(logo);

		panel.add(lblLogo);
		return panel;
	}

	private JPanel createToppingPanel() {
		Border redBorder = BorderFactory.createLineBorder(Color.BLACK, 3);
		Border emptyBorder = BorderFactory.createEmptyBorder(4, 10, 4, 10);

		JPanel masterPanel = new JPanel();
		masterPanel.setLayout(new BoxLayout(masterPanel, BoxLayout.Y_AXIS));
		masterPanel.setBorder(BorderFactory.createCompoundBorder(redBorder,
				emptyBorder));
		masterPanel.setBackground( Color.WHITE );	

		try {
			//create the font to use. Specify the size!
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			//register the font
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("resources/Amatic-Bold.ttf")));

		} catch (IOException e) {
			e.printStackTrace();
		} catch(FontFormatException e) {
			e.printStackTrace();
		}

		JLabel lblEachTopping = new JLabel("Selected Toppings");
		lblEachTopping.setFont(new Font("Amatic Bold", Font.TRUETYPE_FONT, 35));
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);

		panel.add(lblEachTopping);
		masterPanel.add(panel);

		JPanel otherPanel = new JPanel(new FlowLayout());
		otherPanel.setBackground(Color.WHITE);
		JComboBox<String> toppingBox = new JComboBox<String>(unsats);
		toppingBox.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					Object item = e.getItem();
					if (!toppingListModel.contains((String)item)) {
						toppingListModel.addElement((String)item);  
					}
				}
			}
		});

		toppingBox.setFont(new Font("Calibri", Font.BOLD | Font.TRUETYPE_FONT, 20));
		toppingBox.setBackground(Color.BLACK);
		toppingBox.setForeground(Color.WHITE);
		otherPanel.add(toppingBox);
		JButton addBtn = new JButton("Remove selected");
		addBtn.setBackground(Color.BLACK);
		addBtn.setFont(new Font("Calibri", Font.BOLD | Font.TRUETYPE_FONT, 20));
		addBtn.setForeground(Color.WHITE);
		addBtn.setOpaque(true);
		addBtn.addActionListener(new AddToListButtonHandler());
		JLabel addBtn2 = new JLabel("Remove selected");
		addBtn2.setBackground(Color.WHITE);
		addBtn2.setFont(new Font("Calibri", Font.BOLD | Font.TRUETYPE_FONT, 20));
		addBtn2.setForeground(Color.WHITE);
		addBtn2.setOpaque(true);
		
		list = new JList<String>(toppingListModel);
		list.setBackground(Color.BLACK);
		list.setForeground(Color.WHITE);
		list.setFont(new Font("Calibri", Font.TRUETYPE_FONT, 20));

		JScrollPane scrollPane = new JScrollPane(list);
		otherPanel.add(scrollPane);
		otherPanel.add(addBtn);

		masterPanel.add(otherPanel);
		masterPanel.add(addBtn2);
		return masterPanel;
	}

	private JPanel createSizePanel() {
		Border redBorder = BorderFactory.createLineBorder(Color.BLACK, 3);
		Border emptyBorder = BorderFactory.createEmptyBorder(4, 10, 4, 10);

		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createCompoundBorder(redBorder,
				emptyBorder));
		panel.setLayout(new GridLayout(0, 1));
		panel.setBackground( Color.WHITE );

		try {
			//create the font to use. Specify the size!
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			//register the font
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("resources/Amatic-Bold.ttf")));

		} catch (IOException e) {
			e.printStackTrace();
		} catch(FontFormatException e) {
			e.printStackTrace();
		}

		JLabel lblPizzaSize = new JLabel("Pizza Size");
		lblPizzaSize.setFont(new Font("Amatic Bold", Font.TRUETYPE_FONT, 35));
		panel.add(lblPizzaSize);

		rbSmall = new JRadioButton("Small: 20cm");
		rbSmall.setBackground(Color.WHITE);
		rbSmall.setFont(new Font("Calibri", Font.TRUETYPE_FONT, 20));
		rbMedium = new JRadioButton("Medium: 30cm");
		rbMedium.setFont(new Font("Calibri", Font.TRUETYPE_FONT, 20));
		rbMedium.setBackground(Color.WHITE);
		rbLarge = new JRadioButton("Large: 40cm");
		rbLarge.setFont(new Font("Calibri", Font.TRUETYPE_FONT, 20));
		rbLarge.setBackground(Color.WHITE);

		// Create new ButtonGroup
		ButtonGroup group = new ButtonGroup();

		group.add(rbSmall);
		group.add(rbMedium);
		group.add(rbLarge);
		panel.add(rbSmall);
		panel.add(rbMedium);
		panel.add(rbLarge);

		return panel;
	}

	private JPanel createTypePanel() {
		Border redBorder = BorderFactory.createLineBorder(Color.BLACK, 3);
		Border emptyBorder = BorderFactory.createEmptyBorder(4, 10, 4, 10);

		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createCompoundBorder(redBorder,
				emptyBorder));
		panel.setLayout(new GridLayout(0, 1));
		panel.setBackground( Color.WHITE );

		try {
			//create the font to use. Specify the size!
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			//register the font
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("resources/Amatic-Bold.ttf")));

		} catch (IOException e) {
			e.printStackTrace();
		} catch(FontFormatException e) {
			e.printStackTrace();
		}


		JLabel lblPizzaType = new JLabel("Pizza Type");
		lblPizzaType.setFont(new Font("Amatic Bold", Font.TRUETYPE_FONT, 35));
		panel.add(lblPizzaType);

		rbThinCrust = new JRadioButton("Thin Crust");
		rbThinCrust.setFont(new Font("Calibri", Font.TRUETYPE_FONT, 20));
		rbThinCrust.setBackground(Color.WHITE);
		rbMediumCrust = new JRadioButton("Medium Crust");
		rbMediumCrust.setFont(new Font("Calibri", Font.TRUETYPE_FONT, 20));
		rbMediumCrust.setBackground(Color.WHITE);
		rbPan = new JRadioButton("Thick Crust");
		rbPan.setFont(new Font("Calibri", Font.TRUETYPE_FONT, 20));
		rbPan.setBackground(Color.WHITE);

		// Create new ButtonGroup
		ButtonGroup group = new ButtonGroup();

		// add components to optionBox2 and grp1
		group.add(rbThinCrust);
		group.add(rbMediumCrust);
		group.add(rbPan);

		panel.add(rbThinCrust);
		panel.add(rbMediumCrust);
		panel.add(rbPan);

		return panel;
	}

	private JPanel createButtonPanel() {
		JPanel panel = new JPanel();

		JButton btnProcessSelection = new JButton("Order !");
		btnProcessSelection.setOpaque(true);
		btnProcessSelection.addActionListener(new CalculateButtonHandler());
		panel.add(btnProcessSelection);
		panel.setBackground( Color.WHITE );

		btnProcessSelection.setBackground(Color.BLACK);
		btnProcessSelection.setFont(new Font("Calibri", Font.BOLD | Font.TRUETYPE_FONT, 20));
		btnProcessSelection.setForeground(Color.WHITE);

		return panel;
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	private void addComponent(Container container, Component component,
			int gridx, int gridy, int gridwidth, int gridheight, Insets insets,
			int anchor, int fill) {
		GridBagConstraints gbc = new GridBagConstraints(gridx, gridy,
				gridwidth, gridheight, 1.0D, 1.0D, anchor, fill, insets, 0, 0);
		container.add(component, gbc);
	}

	private class CalculateButtonHandler implements ActionListener {

		public void actionPerformed(ActionEvent event) {

			boolean gotAllInfo = true;
			// Need all the toppings and choices for base and size
			// 1. Size
			String pizzaSize = "";
			if (rbSmall.isSelected()) {
				pizzaSize = "small";
			}
			else if (rbMedium.isSelected()) {
				pizzaSize = "medium";
			}
			else if (rbLarge.isSelected()) {
				pizzaSize = "large";
			}
			else {
				gotAllInfo = false;
				JOptionPane.showMessageDialog(frame, "You haven't selected a pizza size.");
			}
			//2. Base
			String pizzaBase = "";
			if (rbThinCrust.isSelected()) {
				pizzaBase = "thin crust";
			}
			else if (rbMediumCrust.isSelected()) {
				pizzaBase = "medium crust";
			}
			else if (rbPan.isSelected()) {
				pizzaBase = "pan crust";
			}
			else {
				gotAllInfo = false;
				JOptionPane.showMessageDialog(frame, "You haven't selected a pizza base.");
			}
			
			// 3. Pizza-top
			List<String> toppings = new ArrayList<String>();
			for(int i = 0; i< list.getModel().getSize();i++){
				toppings.add(list.getModel().getElementAt(i));
	            System.out.println(list.getModel().getElementAt(i));
	        }

			if (gotAllInfo) {
				final PizzaExpertResultsFrame f = new PizzaExpertResultsFrame(ontology, pizzaSize, pizzaBase, toppings);
				try {
					f.results_screen();
				} catch (OWLOntologyCreationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private class AddToListButtonHandler implements ActionListener {

		public void actionPerformed(ActionEvent event) {
			List<String> selectedValues = list.getSelectedValuesList();
			for (String s: selectedValues) {
				toppingListModel.removeElement(s);
			}
		}

	}
}
