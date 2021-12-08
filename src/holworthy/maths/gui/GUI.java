package holworthy.maths.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import holworthy.maths.Maths;
import holworthy.maths.exceptions.MathsInterpreterException;
import holworthy.maths.nodes.Equation;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.Variable;

public class GUI {
	public GUI() {
		try {
			// set the UI back to the windows look rather than the Java look
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) {

		}

		// scale the fonts up to be legible
		UIDefaults defaults = UIManager.getDefaults();
		Enumeration<Object> keys = defaults.keys();
		while(keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = defaults.get(key);
			if(value instanceof Font)
				defaults.put(key, ((Font) value).deriveFont(((Font) value).getSize() * 1.5f));
		}

		JFrame window = new JFrame("Maths Interpreter");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(600, 600);
		
		JPanel wrapperPanel = new JPanel(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(wrapperPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		window.add(scrollPane);

		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
		JTextField inputTextField = new JTextField();
		inputPanel.add(inputTextField);
		JButton interpretButton = new JButton("Interpret");
		inputPanel.add(interpretButton);
		wrapperPanel.add(inputPanel, BorderLayout.PAGE_START);

		JPanel outputPanel = new JPanel();
		outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.PAGE_AXIS));
		wrapperPanel.add(outputPanel, BorderLayout.CENTER);

		// event listeners
		interpretButton.addActionListener(ae -> {
			String text = inputTextField.getText();
			if(text.length() > 0) {
				Node input;
				try {
					input = Maths.parseInput(text);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(window, "Make sure you are using the correct syntax", "Uh oh", JOptionPane.ERROR_MESSAGE);
					return;
				}
				Node simplified;
				try {
					simplified = input.simplify();
				} catch (MathsInterpreterException e1) {
					JOptionPane.showMessageDialog(window, "Not able to simplify that", "Uh oh", JOptionPane.ERROR_MESSAGE);
					return;
				}

				outputPanel.removeAll();
				
				// expanded
				Node expanded;
				try {
					expanded = input.expand();
					JPanel expandedPanel = new JPanel();
					expandedPanel.setLayout(new BoxLayout(expandedPanel, BoxLayout.PAGE_AXIS));
					expandedPanel.add(new JLabel("Expanded:"));
					expandedPanel.add(new JLabel(expanded.toString()));
					outputPanel.add(expandedPanel);
				} catch(MathsInterpreterException e) {
					
				}
				
				// simplified
				JPanel simplifiedPanel = new JPanel();
				simplifiedPanel.setLayout(new BoxLayout(simplifiedPanel, BoxLayout.PAGE_AXIS));
				simplifiedPanel.add(new JLabel("Simplified:"));
				simplifiedPanel.add(new JLabel(simplified.toString()));
				outputPanel.add(simplifiedPanel);

				if(simplified instanceof Equation) {
					// solutions
					try {
						ArrayList<Equation> solutions = ((Equation) simplified).solve();
						JPanel solutionsPanel = new JPanel();
						solutionsPanel.setLayout(new BoxLayout(solutionsPanel, BoxLayout.PAGE_AXIS));
						solutionsPanel.add(new JLabel("Solutions:"));
						if(solutions.size() > 0)
							for(Equation solution : solutions)
								solutionsPanel.add(new JLabel(solution.toString()));
						else
							solutionsPanel.add(new JLabel("No Solutions"));
						outputPanel.add(solutionsPanel);

						for(Equation solution : solutions) {
							if(solution.numVariables() == 2) {
							
								// graphs
								JPanel graphPanel = new JPanel();
								graphPanel.setLayout(new BoxLayout(graphPanel, BoxLayout.PAGE_AXIS));
								graphPanel.add(new JLabel("Graph of " + solution + ":"));
								Graph graph = new Graph((Equation) solution);
	
								// TODO: specify which variable is on which axis
								// graph.setXAxis();
								// graph.setYAxis();
	
								graphPanel.add(graph);
								outputPanel.add(graphPanel);
							}
						}
					} catch (MathsInterpreterException e) {
						
					}	
				}

				// derivative
				try {
					for(Variable wrt : simplified.getVariables()) {
						JPanel derivativePanel = new JPanel();
						derivativePanel.setLayout(new BoxLayout(derivativePanel, BoxLayout.PAGE_AXIS));
						derivativePanel.add(new JLabel("Derivative with respect to " + wrt + ":"));
						derivativePanel.add(new JLabel(simplified.differentiate(wrt).toString()));
						outputPanel.add(derivativePanel);
					}
				} catch(MathsInterpreterException e) {

				}

				outputPanel.revalidate();
				outputPanel.repaint();
			}
		});
		inputTextField.addActionListener(e -> interpretButton.doClick());

		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}

	public static void main(String[] args) {
		new GUI();
	}
}
