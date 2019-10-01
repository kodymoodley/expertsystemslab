package nl.maastrichtuniversity.ids.pizzaexpert;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxOWLObjectRendererImpl;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

public class PizzaExpertResultsFrame{

	private static final Insets bottomInsets = new Insets(10, 10, 10, 10);
	private static final Insets normalInsets = new Insets(10, 10, 0, 10);

	// GUI components
	private static ManchesterOWLSyntaxOWLObjectRendererImpl man = new ManchesterOWLSyntaxOWLObjectRendererImpl();
	private static JFrame frame;
	private static String size;
	private static String base;
	private static OWLOntology ontology;
	private Set<OWLClass> superClasses;
//	private static int logicalAxiomCount;
//	private static ManchesterOWLSyntaxOWLObjectRendererImpl man = new ManchesterOWLSyntaxOWLObjectRendererImpl();
//	private static String [] unsats;
	private static OWLDataFactory df = OWLManager.getOWLDataFactory();
//	private static DefaultListModel<String> toppingListModel; 
	private static DefaultListModel<InferenceWrapper> inferenceListModel;
//	private static JList<String> list;  
	private static JList<InferenceWrapper> inferenceList;  
	private static OWLClassExpression toppingsIntersection;

	public void results_screen() throws OWLOntologyCreationException {
		try {
			frame.setVisible(true);

		} catch (Exception e) {
			e.printStackTrace();
		}

	} // End of main

	public PizzaExpertResultsFrame(OWLOntology ontology, String size, String base, List<String> toppings) {
		inferenceListModel = new DefaultListModel<InferenceWrapper>();
		PizzaExpertResultsFrame.ontology = ontology;
		PizzaExpertResultsFrame.size = size;
		PizzaExpertResultsFrame.base = base;
		
		System.out.println("size: " + PizzaExpertResultsFrame.size);
		System.out.println("base: " + PizzaExpertResultsFrame.base);
		System.out.println();
		
		OWLObjectProperty hasBase = df.getOWLObjectProperty(IRI.create(ontology.getOntologyID().getOntologyIRI().get()+"#hasBase"));
		OWLClassExpression baseType = null;
		if (PizzaExpertResultsFrame.base == "pan crust")
			baseType = df.getOWLObjectSomeValuesFrom(hasBase, df.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI().get()+"#DeepPanBase")));
		else if (PizzaExpertResultsFrame.base == "thin crust")
			baseType = df.getOWLObjectSomeValuesFrom(hasBase, df.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI().get()+"#ThinAndCrispyBase")));
		else
			baseType = df.getOWLObjectSomeValuesFrom(hasBase, df.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI().get()+"#ShallowPanBase")));
		
		
		OWLObjectProperty hasSize = df.getOWLObjectProperty(IRI.create(ontology.getOntologyID().getOntologyIRI().get()+"#hasSize"));
		OWLClassExpression baseSize = null;
		if (PizzaExpertResultsFrame.size == "small")
			baseSize = df.getOWLObjectSomeValuesFrom(hasSize, df.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI().get()+"#SmallSize")));
		else if (PizzaExpertResultsFrame.size == "large")
			baseSize = df.getOWLObjectSomeValuesFrom(hasSize, df.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI().get()+"#LargeSize")));
		else
			baseSize = df.getOWLObjectSomeValuesFrom(hasSize, df.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI().get()+"#MediumSize")));
		
		
		for (int i = 0; i < toppings.size(); i++) {
			System.out.println("topping " + i + ": " + toppings.get(i));
		}
		System.out.println();
		
		Reasoner reasoner = new Reasoner(new Configuration(), ontology);
		
		// First create expression: 
		
		OWLClass pizza = df.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI().get()+"#Pizza"));
		OWLObjectProperty hasTopping = df.getOWLObjectProperty(IRI.create(ontology.getOntologyID().getOntologyIRI().get()+"#hasTopping"));
		Set<OWLClass> toppingClasses = new HashSet<OWLClass>();
		Set<OWLClassExpression> hasToppingClasses = new HashSet<OWLClassExpression>();
		for (int i = 0; i < toppings.size(); i++) {
			OWLClassExpression tmp = null;
			String tmpStr = toppings.get(i) + "Topping";
			OWLClass toppingClass = df.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI().get()+"#"+tmpStr));
			toppingClasses.add(toppingClass);
			tmp = df.getOWLObjectSomeValuesFrom(hasTopping, toppingClass);
			hasToppingClasses.add(tmp);
		} 
		
		hasToppingClasses.add(pizza);
		hasToppingClasses.add(baseType);
		hasToppingClasses.add(baseSize);
		
		// Now compute closure class
		OWLClassExpression closureClass = null;
		OWLClassExpression disjunctionOfToppings = df.getOWLObjectUnionOf(toppingClasses);
		closureClass = df.getOWLObjectAllValuesFrom(hasTopping, disjunctionOfToppings);
		hasToppingClasses.add(closureClass);
		
		toppingsIntersection = df.getOWLObjectIntersectionOf(hasToppingClasses);
		System.out.println("class: " + toppingsIntersection);
		superClasses = reasoner.getSuperClasses(toppingsIntersection, true).getFlattened();
		
		System.out.println("SuperClasses:");
		System.out.println("-----------");
		for (OWLClass c: superClasses) {
			System.out.println(man.render(c));
		}
		
		initialize();
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	private void initialize() {		
		frame = new JFrame("Luigi's Pizzeria Maastricht");
		frame.setSize(600, 400);
		//frame.setBounds(10, 10, 600, 1100);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocationByPlatform(true);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setBackground( Color.WHITE );
		int gridy = 0;

		addComponent(mainPanel, createTitlePanel(), 0, gridy++, 2, 1,
				normalInsets, GridBagConstraints.LINE_START,
				GridBagConstraints.HORIZONTAL);

		addComponent(mainPanel, createTextAreaPanel(), 0, gridy++, 2, 1,
				bottomInsets, GridBagConstraints.LINE_START,
				GridBagConstraints.HORIZONTAL);

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

	private JPanel createTextAreaPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		List<String> superClassesList = new ArrayList<String>();
		for (OWLClass c: superClasses) {
			superClassesList.add(man.render(c));
			inferenceListModel.addElement(new InferenceWrapper(c));
		}
		
		JLabel lblYourOrder = new JLabel("Order information:");
		lblYourOrder.setFont(new Font("Calibri", Font.BOLD | Font.TRUETYPE_FONT, 20));
		lblYourOrder.setBackground(Color.WHITE);
		panel.add(lblYourOrder, BorderLayout.NORTH);
		panel.setBackground(Color.WHITE);
		inferenceList = new JList<InferenceWrapper>(inferenceListModel);
		inferenceList.setBackground(Color.BLACK);
		inferenceList.setForeground(Color.WHITE);
		inferenceList.setFont(new Font("Calibri", Font.TRUETYPE_FONT, 18));
		JScrollPane scrollPane = new JScrollPane(inferenceList);
		
		inferenceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		panel.add(scrollPane, BorderLayout.CENTER);		
		JButton explainButton = new JButton("Explain");
		explainButton.addActionListener(new ExplanationComputerHandler());

		explainButton.setBackground(Color.BLACK);
		explainButton.setFont(new Font("Calibri", Font.BOLD | Font.TRUETYPE_FONT, 20));
		explainButton.setForeground(Color.WHITE);
		explainButton.setOpaque(true);
		panel.add(explainButton, BorderLayout.SOUTH);
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

	private class ExplanationComputerHandler implements ActionListener {

		public void actionPerformed(ActionEvent event) {
			// Need all the toppings and choices for base and size
			// 1. Size
			InferenceWrapper value = inferenceList.getSelectedValue();
			OWLSubClassOfAxiom entailment = df.getOWLSubClassOfAxiom(toppingsIntersection, 
					value.getClassExpression());
					/*df.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI().get()+"#"+value)));*/
			PizzaExpertExplanationFrame f = new PizzaExpertExplanationFrame(ontology, entailment);
			try {
				f.explanation_screen();
			} catch (OWLOntologyCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
				
		}

	}
}