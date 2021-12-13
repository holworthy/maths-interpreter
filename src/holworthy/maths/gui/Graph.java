package holworthy.maths.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JPanel;

import holworthy.maths.nodes.Equation;
import holworthy.maths.nodes.InputDouble;
import holworthy.maths.nodes.Node;
import holworthy.maths.nodes.Variable;

public class Graph extends JComponent {
	private Equation equation;
	private DecimalFormat decimalFormat = new DecimalFormat("0.00E0");
	
	private class GraphPanel extends JPanel {
		private double startX = -10;
		private double startY = -10;
		private double zoomX = 20;
		private double zoomY = 20;

		private boolean mouseEntered = false;
		private Point lastMousePressPoint;

		public GraphPanel() {
			MouseAdapter mouseAdapter = new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					lastMousePressPoint = e.getPoint();
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
					double dx = lastMousePressPoint.getX() - e.getX();
					double dy = lastMousePressPoint.getY() - e.getY();
					if(dx != 0)
						startX += ((dx / getWidth()) * zoomX);
					if(dy != 0)
						startY -= ((dy / getHeight()) * zoomY);

					lastMousePressPoint = e.getPoint();
					repaint();
				}

				@Override
				public void mouseWheelMoved(MouseWheelEvent e) {
					int rotation = e.getWheelRotation();
					double mx = (double) e.getX() / getWidth();
					double my = (double) e.getY() / getHeight();

					boolean shouldScrollX = ((e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) != KeyEvent.SHIFT_DOWN_MASK);
					boolean shouldScrollY = ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != KeyEvent.CTRL_DOWN_MASK);

					// zoom in
					if(rotation < 0) {
						if(shouldScrollX && zoomX > 10e-13 && zoomX < 10e150) {
							startX = startX + zoomX * mx - (zoomX / 2f) * mx;
							zoomX /= 2;
						}
						if(shouldScrollY && zoomY > 10e-13 && zoomY < 10e150) {
							startY = startY + zoomY * (1.0 - my) - (zoomY / 2f) * (1.0 - my);
							zoomY /= 2;
						}
					}
					// zoom out
					if(rotation > 0) {
						if(shouldScrollX) {
							startX = startX + zoomX * mx - (zoomX * 2f) * mx;
							zoomX *= 2;
						}
						if(shouldScrollY) {
							startY = startY + zoomY * (1.0 - my) - (zoomY * 2f) * (1.0 - my);
							zoomY *= 2;
						}
					}

					// if somehow floating point numbers have failed us reset the values
					if(Double.isNaN(startX) || Double.isNaN(startY) || Double.isNaN(zoomX) || Double.isNaN(zoomY) || Double.isInfinite(zoomX) || Double.isInfinite(zoomY)) {
						startX = startY = -10;
						zoomX = zoomY = 20;
					}

					repaint();
				}
			};

			addMouseListener(mouseAdapter);
			addMouseMotionListener(mouseAdapter);
			addMouseWheelListener(mouseAdapter);
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			// clear background
			g2d.setColor(Color.WHITE);
			g2d.fillRect(0, 0, getWidth(), getHeight());
			
			// grid lines
			double lineGapX = 1;
			int expX = 1;
			while(lineGapX * 10 < zoomX)
				lineGapX = Math.pow(10, ++expX);
			while(lineGapX * 2 > zoomX)
				lineGapX = Math.pow(10, --expX);
			double lineStartX = Math.round(startX / lineGapX) * lineGapX;
			double lineGapY = 1;
			int expY = 1;
			while(lineGapY * 10 < zoomY)
				lineGapY = Math.pow(10, ++expY);
			while(lineGapY * 2 > zoomY)
				lineGapY = Math.pow(10, --expY);
			double lineStartY = Math.round(startY / lineGapY) * lineGapY;

			for(double x = lineStartX; x < startX + zoomX; x += lineGapX) {
				g2d.setColor(new Color(240, 240, 240));
				g2d.drawLine(
					(int) (((x - startX) / zoomX) * getWidth()),
					0,
					(int) (((x - startX) / zoomX) * getWidth()),
					getHeight()
				);
			}
			for(double y = lineStartY; y < startY + zoomY; y += lineGapY) {
				g2d.setColor(new Color(240, 240, 240));
				g2d.drawLine(
					0,
					(int) ((1.0 - ((y - startY) / zoomY)) * getHeight()),
					getWidth(),
					(int) ((1.0 - ((y - startY) / zoomY)) * getHeight())
				);
			}

			// x and y axis
			g2d.setColor(new Color(255 - 16 * 4, 255 - 16 * 4, 255 - 16 * 4));
			if(0 > startX && 0 < startX + zoomX)
				g2d.drawLine((int) ((-startX / zoomX) * getWidth()), 0, (int) ((-startX / zoomX) * getWidth()), getHeight());
			if(0 > startY && 0 < startY + zoomY)
				g2d.drawLine(0, (int) ((1.0 - (-startY / zoomY)) * getHeight()), getWidth(), (int) ((1.0 - (-startY / zoomY)) * getHeight()));


			// labels
			for(double x = lineStartX; x < startX + zoomX; x += lineGapX) {
				g2d.setColor(Color.BLACK);
				int tx = (int) (((x - startX) / zoomX) * getWidth());
				int ty = 0;
				g2d.translate(tx, ty);
				g2d.rotate(Math.PI / 2);
				g2d.drawString(decimalFormat.format(x), 0, 0);
				g2d.rotate(-Math.PI / 2);
				g2d.translate(-tx, -ty);
			}
			for(double y = lineStartY; y < startY + zoomY; y += lineGapY) {
				g2d.setColor(Color.BLACK);
				g2d.drawString(decimalFormat.format(y), 0, (int) ((1.0 - ((y - startY) / zoomY)) * getHeight()));
			}

			// graph
			g2d.setColor(Color.BLUE);
			double lastX = Double.NaN;
			double lastY = Double.NaN;
			
			try {
				// get variables for each axis
				Variable yVar = (Variable) equation.getLeft();
				ArrayList<Variable> variables = equation.getVariables();
				variables.remove(yVar);
				Variable xVar = variables.get(0);

				HashMap<Variable, Node> values = new HashMap<>();
				double endX = startX + zoomX;
				double endY = startY + zoomY;
				for(double x = startX; x <= startX + zoomX; x += (endX - startX) / getWidth()) {
					values.put(xVar, new InputDouble(x));
					double y = equation.evaluate(values);

					if(!Double.isNaN(lastX) && !Double.isNaN(lastY) && !Double.isNaN(x) && !Double.isNaN(y))
						g2d.drawLine(
							(int) (((lastX - startX) / (endX - startX)) * getWidth()),
							(int) ((1.0 - (lastY - startY) / (endY - startY)) * getHeight()),
							(int) (((x - startX) / (endX - startX)) * getWidth()),
							(int) ((1.0 - (y - startY) / (endY - startY)) * getHeight())
						);
					lastX = x;
					lastY = y;
				}
			} catch(Exception e) {
				
			}

			g2d.setColor(Color.RED);
			// HashMap<Variable, Node> values = new HashMap<>();
			// g2d.drawLine(equation.evaluate(0f), 0, equation.evaluate(0), getHeight());

			if(mouseEntered) {
				// g2d.setColor(new Color(160, 160, 160));
				// g2d.drawLine(0, (int) mousePoint.getY(), getWidth(), (int) mousePoint.getY());
				// g2d.drawLine((int) mousePoint.getX(), 0, (int) mousePoint.getX(), getHeight());
			}
		}
	}

	public Graph(Equation equation){
		decimalFormat.setMaximumFractionDigits(340);
		this.equation = equation;

		setPreferredSize(new Dimension(0, 600));
		setMinimumSize(new Dimension(0, 600));

		setLayout(new BorderLayout());
		GraphPanel graphPanel = new GraphPanel();
		add(graphPanel, BorderLayout.CENTER);
	}
}
