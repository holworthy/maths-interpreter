package holworthy.maths.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.HashMap;

import javax.swing.JPanel;
import java.awt.event.*;

import holworthy.maths.Maths;
import holworthy.maths.exceptions.MathsInterpreterException;
import holworthy.maths.nodes.BinaryNode;
import holworthy.maths.nodes.Divide;
import holworthy.maths.nodes.Negative;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.Number;
import holworthy.maths.nodes.UnaryNode;

public class Graph extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
	private HashMap<Double, Double> values = new HashMap<>();
	private String equation;
	private Dimension size = new Dimension(400, 400);

	public Graph(String equation){
		this.equation = equation;
		setPreferredSize(size);
		setMaximumSize(size);
		setMinimumSize(size);
	}

	public HashMap<Double, Double> getValues() {
		return values;
	}

	@Override
	public String toString() {
		return values.toString();
	}

	// Assumes y = equation
	public void findValues(int upperBound, int lowerBound) throws Exception{
		for (int x = lowerBound; x < upperBound + 1; x++){
			String replaced = equation.replaceAll("x", "("+Integer.toString(x)+")");
			Node answer = Maths.parseInput(replaced).simplify();
			// y = 5
			if(answer instanceof Number){
				values.put((double) x,  ((Number) answer).getValue().doubleValue());
			}
			// y = -5
			else if(answer instanceof Negative && ((UnaryNode) answer).getNode() instanceof Number){
				values.put((double) x,  -((Number) ((UnaryNode) answer).getNode()).getValue().doubleValue());
			}
			// y = a/b
			else if(answer instanceof Divide){
				// a = 1 b = 2
				if(((BinaryNode) answer).getLeft() instanceof Number && ((BinaryNode) answer).getRight() instanceof Number){
					Double value = ((Number) ((BinaryNode) answer).getLeft()).getValue().doubleValue() / ((Number) ((BinaryNode) answer).getRight()).getValue().doubleValue();
					values.put((double) x, value);
				}
				// a = -1 b = 2
				if(((BinaryNode) answer).getLeft() instanceof Negative && ((UnaryNode) ((BinaryNode) answer).getLeft()).getNode() instanceof Number && ((BinaryNode) answer).getRight() instanceof Number){
					Double value = -((Number) ((UnaryNode) ((BinaryNode) answer).getLeft()).getNode()).getValue().doubleValue() / ((Number) ((BinaryNode) answer).getRight()).getValue().doubleValue();
					values.put((double) x, value);
				}
				// a = 1 b = -2
				if(((BinaryNode) answer).getLeft() instanceof Number && ((BinaryNode) answer).getRight() instanceof Negative && ((UnaryNode) ((BinaryNode) answer).getRight()).getNode() instanceof Number){
					Double value = ((Number) ((BinaryNode) answer).getLeft()).getValue().doubleValue() / -((Number) ((UnaryNode) ((BinaryNode) answer).getRight()).getNode()).getValue().doubleValue();
					values.put((double) x, value);
				}
			}
			// y = -(a/b)
			else if(answer instanceof Negative && ((UnaryNode) answer).getNode() instanceof Divide && ((BinaryNode) ((UnaryNode) answer).getNode()).getLeft() instanceof Number && ((BinaryNode) ((UnaryNode) answer).getNode()).getRight() instanceof Number){
				Double value = -((Number) ((BinaryNode) ((UnaryNode) answer).getNode()).getLeft()).getValue().doubleValue() / ((Number) ((BinaryNode) ((UnaryNode) answer).getNode()).getRight()).getValue().doubleValue();
				values.put((double) x, value);
			}
			else{
				throw new MathsInterpreterException("Something is very wrong with this equation.");
			}
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(new Color(240, 240, 240));
		
		for(int x = 0; x < 400; x += 10)
			g.drawLine(x, 0, x, getHeight());
		for(int y = 0; y < 400; y += 10)
			g.drawLine(0, y, getWidth(), y);
		
		g.setColor(Color.BLUE);
		g.drawLine(0, 100, getWidth(), 300);
	}

	public static void main(String[] args) throws Exception {
		Graph graph = new Graph("2*x^2+x");
		graph.findValues(10, -10);
		System.out.println(graph);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		// TODO Auto-generated method stub
		
	}
}
