package holworthy.maths.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashMap;

import javax.swing.JPanel;

import holworthy.maths.Maths;
import holworthy.maths.exceptions.MathsInterpreterException;
import holworthy.maths.nodes.BinaryNode;
import holworthy.maths.nodes.Divide;
import holworthy.maths.nodes.Equation;
import holworthy.maths.nodes.Negative;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.Number;
import holworthy.maths.nodes.UnaryNode;
import holworthy.maths.nodes.Variable;

public class Graph extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
	private HashMap<Double, Double> values = new HashMap<>();
	private Equation equation;
	private Dimension size = new Dimension(400, 400);
	private boolean mouseEntered = false;
	private Point mousePoint;

	public Graph(Equation equation){
		this.equation = equation;
		setPreferredSize(size);
		setMaximumSize(size);
		setMinimumSize(size);

		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
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
			String replaced = ""; //equation.replaceAll("x", "("+Integer.toString(x)+")");
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
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(new Color(240, 240, 240));
		
		for(int x = 0; x < 400; x += 10)
			g.drawLine(x, 0, x, getHeight());
		for(int y = 0; y < 400; y += 10)
			g.drawLine(0, y, getWidth(), y);
		
		g.setColor(Color.BLUE);
		double lastX = 0;
		double lastY = 0;
		
		// TODO: move somewhere else
		try {
			HashMap<Variable, Node> values = new HashMap<>();
			for(double x = 0; x < 100; x += 1) {
				values.put(new Variable("x"), Maths.parseInput("" + x));
				double y = equation.evaluate(values);

				g.drawLine((int) lastX, (int) (getHeight() - lastY), (int) x, (int) (getHeight() - y));
				lastX = x;
				lastY = y;
			}
		} catch(Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(mouseEntered) {
			g.setColor(new Color(160, 160, 160));
			g.drawLine(0, (int) mousePoint.getY(), getWidth(), (int) mousePoint.getY());
			g.drawLine((int) mousePoint.getX(), 0, (int) mousePoint.getX(), getHeight());
		}
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
		mouseEntered = true;
		repaint();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		mouseEntered = false;
		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mousePoint = e.getPoint();
		repaint();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		// TODO Auto-generated method stub
		
	}
}
