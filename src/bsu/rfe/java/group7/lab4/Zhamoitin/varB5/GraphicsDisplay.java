package bsu.rfe.java.group7.lab4.Zhamoitin.varB5;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.*;

public class GraphicsDisplay extends JPanel {
    private Double[][] graphicsData;
    private boolean showAxis = true;
    private boolean showMarkers = true;
    private boolean showTask = true;
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    private double scale;
    private BasicStroke graphicsStroke;
    private BasicStroke axisStroke;
    private BasicStroke markerStroke;
    private Font axisFont;

    public GraphicsDisplay ()
    {
        setBackground(Color.WHITE);
        graphicsStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_ROUND, 10.0f, new float[]{16.0f, 4.0f,2.0f,4.0f}, 0.0f);
        axisStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
        markerStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
        axisFont = new Font("Serif", Font.BOLD, 36);
    }

    public void showGraphics(Double[][] graphicsData)
    {
        this.graphicsData = graphicsData;
        repaint();
    }

    public void setShowAxis(boolean showAxis) {
        this.showAxis = showAxis;
        repaint();
    }

    public void setShowMarkers(boolean showMarkers) {
        this.showMarkers = showMarkers;
        repaint();
    }

    public void setShowTask(boolean showTask)
    {
        this.showTask = showTask;
        repaint();
    }

    protected Point2D.Double xyToPoint(double x, double y)
    {
        double deltaX = x - minX;
        double deltaY = maxY - y;
        return new Point2D.Double(deltaX*scale,deltaY*scale);
    }

    protected Point2D.Double shiftPoint(Point2D.Double src, double deltaX, double deltaY)
    {
        Point2D.Double dest = new Point2D.Double();
        dest.setLocation(src.getX() + deltaX, src.getY() + deltaY);
        return dest;
    }

    protected void paintGraphics(Graphics2D canvas)
    {
        canvas.setStroke(graphicsStroke);
        canvas.setColor(Color.RED);
        GeneralPath graphics = new GeneralPath();
        for(int i =0; i<graphicsData.length; i++)
        {
            Point2D.Double point = xyToPoint(graphicsData[i][0],graphicsData[i][1]);
            if(i>0)
                graphics.lineTo(point.getX(),point.getY());
            else
                graphics.moveTo(point.getX(),point.getY());
        }
        canvas.draw(graphics);
    }

    protected void paintAxis(Graphics2D canvas)
    {
        canvas.setStroke(axisStroke);
        canvas.setColor(Color.BLACK);
        canvas.setPaint(Color.BLACK);
        canvas.setFont(axisFont);
        FontRenderContext context = canvas.getFontRenderContext();
        if(minX<=0.0 && maxX>=0.0)
        {
            canvas.draw(new Line2D.Double(xyToPoint(0, maxY), xyToPoint(0, minY)));
            GeneralPath arrow = new GeneralPath();
            Point2D.Double lineEnd = xyToPoint(0, maxY);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            arrow.lineTo(arrow.getCurrentPoint().getX()+5,
                         arrow.getCurrentPoint().getY()+20);
            arrow.lineTo(arrow.getCurrentPoint().getX()-10,
                            arrow.getCurrentPoint().getY());
            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);
            Rectangle2D bounds = axisFont.getStringBounds("y", context);
            Point2D.Double labelPos = xyToPoint(0, maxY);
            canvas.drawString("y", (float)labelPos.getX() + 10,
                                          (float)(labelPos.getY() - bounds.getY()));
        }
        if(minY<=0.0 && maxY>=0.0)
        {
            canvas.draw(new Line2D.Double(xyToPoint(minX, 0), xyToPoint(maxX, 0)));
            GeneralPath arrow = new GeneralPath();
            Point2D.Double lineEnd = xyToPoint(maxX, 0);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            arrow.lineTo(arrow.getCurrentPoint().getX()-20,
                         arrow.getCurrentPoint().getY()-5);
            arrow.lineTo(arrow.getCurrentPoint().getX(),
                         arrow.getCurrentPoint().getY()+10);
            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);
            Rectangle2D bounds = axisFont.getStringBounds("x", context);
            Point2D.Double labelPos = xyToPoint(maxX, 0);
            canvas.drawString("x", (float)(labelPos.getX()-bounds.getWidth()-10),
                                       (float)(labelPos.getY() + bounds.getY()));
        }
    }

    protected void paintMarkers(Graphics2D canvas)
    {
        canvas.setStroke(markerStroke);
        canvas.setColor(Color.RED);
        canvas.setPaint(Color.BLACK);

        for(Double[] point : graphicsData)
        {
            if(HasOnlyEvenDigits(point[1]))
            {
                Polygon marker = new Polygon();
                Point2D.Double center = xyToPoint(point[0], point[1]);
                int halfSize = 5; // Половина размера маркера (11 пикселей / 2)
                marker.addPoint((int) center.x, (int) (center.y - halfSize));
                marker.addPoint((int) (center.x + halfSize), (int) center.y);
                marker.addPoint((int) center.x, (int) (center.y + halfSize));
                marker.addPoint((int) (center.x - halfSize), (int) center.y);
                canvas.draw(marker);
                canvas.fill(marker);
            }
        }
    }

    private void paintTask(Graphics2D canvas)
    {
        canvas.setStroke(markerStroke);
        canvas.setColor(Color.BLUE);
        canvas.setPaint(Color.BLUE);

        for(int i = 1; i<graphicsData.length-1; i++)
        {
            boolean maxex = graphicsData[i][1]>graphicsData[i-1][1] && graphicsData[i][1]>graphicsData[i+1][1];
            boolean minex = graphicsData[i][1]<graphicsData[i-1][1] && graphicsData[i][1] < graphicsData[i+1][1];
            if(maxex || minex)
            {
                Double[] point = graphicsData[i];
                Polygon marker = new Polygon();
                Point2D.Double center = xyToPoint(point[0], point[1]);
                int halfSize = 5; // Половина размера маркера (11 пикселей / 2)
                marker.addPoint((int) center.x, (int) (center.y - halfSize));
                marker.addPoint((int) (center.x + halfSize), (int) center.y);
                marker.addPoint((int) center.x, (int) (center.y + halfSize));
                marker.addPoint((int) (center.x - halfSize), (int) center.y);
                canvas.draw(marker);
                canvas.fill(marker);

                String text = String.format("(%.5f, %.5f)", point[0], point[1]);
                int textX = (int) (center.x + halfSize + 5); // Смещение для текста по X
                int textY = (int) center.y; // Смещение для текста по Y
                canvas.drawString(text, textX, textY);
            }

        }
    }

    private boolean HasOnlyEvenDigits(Double point) {
        point = Math.abs(point);
        String num = String.valueOf(point.intValue());

        for(int i = 0;i<num.length();i++)
        {
            int digit = Character.getNumericValue(num.charAt(i));
            if(digit % 2 != 0)
                return false;
        }
        return true;
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if(graphicsData == null || graphicsData.length == 0) return;
        minX = graphicsData[0][0];
        maxX = graphicsData[graphicsData.length-1][0];
        minY = graphicsData[0][1];
        maxY = minY;

        for (int i = 1; i<graphicsData.length; i++) {
            if (graphicsData[i][1]<minY) {
                minY = graphicsData[i][1];
            }
            if (graphicsData[i][1]>maxY) {
                maxY = graphicsData[i][1];
            }
        }
        double scaleX = getSize().getWidth() / (maxX - minX);
        double scaleY = getSize().getHeight() / (maxY - minY);
        scale = Math.min(scaleX,scaleY);
        if(scale == scaleX)
        {
            double yIncrement = (getSize().getHeight()/scale-(maxY-minY))/2;
            maxY += yIncrement;
            minY -= yIncrement;
        }
        if(scale == scaleY)
        {
            double xIncrement = (getSize().getWidth()/scale-(maxX-minX))/2;
            maxX += xIncrement;
            minX -= xIncrement;
        }
        Graphics2D canvas = (Graphics2D) g;
        Stroke oldStroke = canvas.getStroke();
        Color oldColor = canvas.getColor();
        Paint oldPaint = canvas.getPaint();
        Font oldFont = canvas.getFont();

        if (showAxis) paintAxis(canvas);
        paintGraphics(canvas);
        if (showMarkers) paintMarkers(canvas);
        if(showTask) paintTask(canvas);
        canvas.setFont(oldFont);
        canvas.setPaint(oldPaint);
        canvas.setColor(oldColor);
        canvas.setStroke(oldStroke);

    }
}
