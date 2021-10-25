package holworthy.maths.gui;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import holworthy.maths.Maths;
import holworthy.maths.nodes.Equation;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.Variable;

public class GUI {
	public GUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) {

		}

		JFrame window = new JFrame("Maths Interpreter");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(400, 400);
		window.setLayout(new BorderLayout());

		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
		JTextField inputTextField = new JTextField();
		inputPanel.add(inputTextField);
		JButton interpretButton = new JButton("Interpret");
		inputPanel.add(interpretButton);
		window.add(inputPanel, BorderLayout.PAGE_START);

		JPanel outputPanel = new JPanel();
		outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.PAGE_AXIS));
		window.add(outputPanel, BorderLayout.CENTER);

		// event listeners
		interpretButton.addActionListener(ae -> {
			try {
				String text = inputTextField.getText();
				if(text.length() > 0) {
					// TODO: do this on another thread
					Node input = Maths.parseInput(text);

					outputPanel.removeAll();
					
					// expanded
					JPanel expandedPanel = new JPanel();
					expandedPanel.setLayout(new BoxLayout(expandedPanel, BoxLayout.PAGE_AXIS));
					expandedPanel.add(new JLabel("Expanded:"));
					expandedPanel.add(new JLabel(input.expand().toString()));
					outputPanel.add(expandedPanel);

					// simplified
					JPanel simplifiedPanel = new JPanel();
					simplifiedPanel.setLayout(new BoxLayout(simplifiedPanel, BoxLayout.PAGE_AXIS));
					simplifiedPanel.add(new JLabel("Simplified:"));
					simplifiedPanel.add(new JLabel(input.simplify().toString()));
					outputPanel.add(simplifiedPanel);

					// solutions
					if(input instanceof Equation) {
						JPanel solutionsPanel = new JPanel();
						solutionsPanel.setLayout(new BoxLayout(solutionsPanel, BoxLayout.PAGE_AXIS));
						solutionsPanel.add(new JLabel("Solutions:"));
						ArrayList<Equation> solutions = ((Equation) input).solve();
						for(Equation solution : solutions)
							solutionsPanel.add(new JLabel(solution.toString()));
						outputPanel.add(solutionsPanel);
					}

					outputPanel.revalidate();
					outputPanel.repaint();

					System.out.println(input.differentiate(new Variable("x")));
				}
			} catch(Exception e) {
				e.printStackTrace();
				// TODO: remove this and handle errors
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
