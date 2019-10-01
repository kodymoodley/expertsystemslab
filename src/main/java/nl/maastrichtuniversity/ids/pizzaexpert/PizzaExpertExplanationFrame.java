package nl.maastrichtuniversity.ids.pizzaexpert;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owl.explanation.api.ExplanationGenerator;
import org.semanticweb.owl.explanation.api.ExplanationGeneratorFactory;
import org.semanticweb.owl.explanation.api.ExplanationManager;
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxOWLObjectRendererImpl;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

public class PizzaExpertExplanationFrame{

	private static final Insets bottomInsets = new Insets(10, 10, 10, 10);
	private static final Insets normalInsets = new Insets(10, 10, 0, 10);

	// GUI components
	private static ManchesterOWLSyntaxOWLObjectRendererImpl man = new ManchesterOWLSyntaxOWLObjectRendererImpl();
	private static JFrame frame;
	private static String explanation;
	
public void explanation_screen() throws OWLOntologyCreationException {
		try {
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	} // End of main

	public PizzaExpertExplanationFrame(OWLOntology ontology, OWLAxiom entailment) {
		OWLReasonerFactory rf = new ReasonerFactory();
		//Reasoner reasoner = new Reasoner(new Configuration(), ontology);
		// Create the explanation generator factory which uses reasoners provided by the specified
		// reasoner factory
		ExplanationGeneratorFactory<OWLAxiom> genFac = ExplanationManager.createExplanationGeneratorFactory(rf);

		// Now create the actual explanation generator for our ontology
		ExplanationGenerator<OWLAxiom> gen = genFac.createExplanationGenerator(ontology);
		
		// Get our explanations.  Ask for a maximum of 5.
		Set<Explanation<OWLAxiom>> expl = gen.getExplanations(entailment, 1);
		
		explanation = "";
		
		int i = 1;
		for (Explanation<OWLAxiom> ex: expl) {
			for (OWLAxiom a: ex.getAxioms()) {
				explanation += i + ". ";
				if (a.getAxiomType().equals(AxiomType.SUBCLASS_OF)) {
					OWLSubClassOfAxiom sub = (OWLSubClassOfAxiom)a;
					if (!sub.getSubClass().isAnonymous()) {
						explanation += man.render(sub.getSubClass()) + " ";
						if (!sub.getSuperClass().isAnonymous()) {
							explanation += "is a " + man.render(sub.getSuperClass()) + "\n";
							if (!explanation.contains("Range"))
								explanation=explanation.replace("hasTopping","has");
							explanation=explanation.replace("Equivalent","");
							//explanation=explanation.replace("1","");
						}
						else {
							explanation += "has this property: " + man.render(sub.getSuperClass()) + "\n";
							if (!explanation.contains("Range"))
								explanation=explanation.replace("hasTopping","has");
							explanation=explanation.replace("Equivalent","");
							//explanation=explanation.replace("1","");
						}
					}
					else {
						explanation += "If " + man.render(sub.getSubClass()) + " then " + man.render(sub.getSuperClass()) + "\n";
						explanation=explanation.replace("Equivalent","");
						//explanation=explanation.replace("1","");
					}
				}
				else if (a.getAxiomType().equals(AxiomType.EQUIVALENT_CLASSES)) {
					OWLEquivalentClassesAxiom equiv = (OWLEquivalentClassesAxiom)a;
					Object[] arr = equiv.getClassExpressionsAsList().toArray();
					explanation += man.render((OWLClassExpression)arr[0]) + " is the same as: " + man.render((OWLClassExpression)arr[1]) + "\n";
					if (!explanation.contains("Range"))
						explanation=explanation.replace("hasTopping","has");
					explanation=explanation.replace("Equivalent","");
					//explanation=explanation.replace("1","");
				}
				else if (a.getAxiomType().equals(AxiomType.DISJOINT_CLASSES)) {
					OWLDisjointClassesAxiom disjoint = (OWLDisjointClassesAxiom)a;
					Object[] arr = disjoint.getClassExpressionsAsList().toArray();
					explanation += "The following categories cannot share instances: ";
					for (Object o: arr) {
						explanation += "(" + man.render((OWLClassExpression)o) + ") ";
						if (!explanation.contains("Range"))
							explanation=explanation.replace("hasTopping","has");
						explanation=explanation.replace("Equivalent","");
						//explanation=explanation.replace("1","");
					}
					explanation += "\n";
				}
				else {
					explanation += man.render(a) + "\n";
					if (!explanation.contains("Range"))
						explanation=explanation.replace("hasTopping","has");
				}
				i++;
			}
		}
		initialize();
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	private void initialize() {
		//Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		//double width = screenSize.getWidth();
		//double height = screenSize.getHeight();
		
		//double x = 0.0;
		//double y = 0;
		
		//x = width * 0.328125;
		//y = height * 0.694444444444444444444444444444;
		
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

		/*List<String> superClassesList = new ArrayList<String>();
		for (OWLClass c: superClasses) {
			superClassesList.add(man.render(c));
			inferenceListModel.addElement(man.render(c));
		}*/
		
		JLabel lblYourOrder = new JLabel("Reason for selected inference:");
		lblYourOrder.setFont(new Font("Calibri", Font.BOLD | Font.TRUETYPE_FONT, 20));
		lblYourOrder.setBackground(Color.WHITE);
		panel.add(lblYourOrder, BorderLayout.NORTH);
		panel.setBackground(Color.WHITE);
		JTextArea explanationArea = new JTextArea(12, 12);
		explanationArea.setBackground(Color.BLACK);
		explanationArea.setForeground(Color.WHITE);
		explanationArea.setFont(new Font("Calibri", Font.TRUETYPE_FONT, 18));
		explanationArea.setText(explanation);
		JScrollPane scrollPane = new JScrollPane(explanationArea);
		panel.add(scrollPane, BorderLayout.CENTER);		
	
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
	
	
	
	
	
	
	
	
	
	
	

}
