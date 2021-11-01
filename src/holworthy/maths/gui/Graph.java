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

import holworthy.maths.nodes.Equation;
import holworthy.maths.nodes.InputDouble;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.Variable;

public class Graph extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
	private HashMap<InputDouble, InputDouble> values = new HashMap<>();
	private Equation equation;
	private Dimension size = new Dimension(400, 400);
	private boolean mouseEntered = false;
	private Point mousePoint;

	private double startX = -10;
	private double endX = 10;
	private double startY = -10;
	private double endY = 10;

	public Graph(Equation equation){
		this.equation = equation;
		setPreferredSize(size);
		setMaximumSize(size);
		setMinimumSize(size);

		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
	}

	public HashMap<InputDouble, InputDouble> getValues() {
		return values;
	}

	@Override
	public String toString() {
		return values.toString();
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
		double lastX = Double.NEGATIVE_INFINITY;
		double lastY = 0;
		
		// TODO: move somewhere else
		try {
			HashMap<Variable, Node> values = new HashMap<>();
			for(double x = startX; x <= endX; x += (endX - startX) / getWidth()) {
				values.put(new Variable("x"), new InputDouble(x));
				double y = equation.evaluate(values);

				g.drawLine(
					(int) (((lastX - startX) / (endX - startX)) * getWidth()),
					(int) ((1.0 - (lastY - startY) / (endY - startY)) * getHeight()),
					(int) (((x - startX) / (endX - startX)) * getWidth()),
					(int) ((1.0 - (y - startY) / (endY - startY)) * getHeight())
				);
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
