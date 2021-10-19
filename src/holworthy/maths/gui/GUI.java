package holworthy.maths.gui;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import holworthy.maths.Maths;
import holworthy.maths.nodes.Node;

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
				Node input = Maths.parseInput(inputTextField.getText());
				outputPanel.removeAll();
				// TODO: do this on another thread
				Node output = input.simplify();
				outputPanel.add(new JLabel(output.toString()));
				outputPanel.revalidate();
				outputPanel.repaint();
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
