import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.geom.Point2D;

public class GraphicsDisplay extends JPanel{
    private Double[][] graphicsData;
    // Флаговые переменные, задающие правила отображения графика
    private boolean showAxis = true;
    private boolean showMarkers = true;
    // Границы диапазона пространства, подлежащего отображению
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    // Используемый масштаб отображения
    private double scale;
    // Различные стили черчения линий
    private BasicStroke graphicsStroke;
    private BasicStroke axisStroke;
    private BasicStroke markerStroke;
    // Различные шрифты отображения надписей
    private Font axisFont;
    public GraphicsDisplay() {
        // Цвет заднего фона области отображения - белый
        setBackground(Color.lightGray);
        // Сконструировать необходимые объекты, используемые в рисовании
        // Перо для рисования графика
        graphicsStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f, new float[] { 4, 1, 3, 1, 1}, 0.0f);
        // Перо для рисования осей координат
        axisStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
        // Перо для рисования контуров маркеров
        markerStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
        // Шрифт для подписей осей координат
        axisFont = new Font("Serif", Font.BOLD, 16);
    }
    public void showGraphics(Double[][] graphicsData) {
        // Сохранить массив точек во внутреннем поле класса
        this.graphicsData = graphicsData;
        // Запросить перерисовку компонента, т.е. неявно вызвать paintComponent();
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
    // Метод отображения всего компонента, содержащего график
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (graphicsData == null || graphicsData.length == 0) return;
        minX = graphicsData[0][0];
        maxX = graphicsData[graphicsData.length - 1][0];
        minY = graphicsData[0][1];
        maxY = minY;
        for (int i = 1; i < graphicsData.length; i++) {
            if (graphicsData[i][1] < minY) {
                minY = graphicsData[i][1];
            }
            if (graphicsData[i][1] > maxY) {
                maxY = graphicsData[i][1];
            }
        }
        double scaleX = getSize().getWidth() / (maxX - minX);
        double scaleY = getSize().getHeight() / (maxY - minY);
        scale = Math.min(scaleX, scaleY);
        if (scale == scaleX) {
            double yIncrement = (getSize().getHeight() / scale - (maxY - minY)) / 2;
            maxY += yIncrement;
            minY -= yIncrement;
        }
        if (scale == scaleY) {
            double xIncrement = (getSize().getWidth() / scale - (maxX -
                    minX)) / 2;
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
        canvas.setFont(oldFont);
        canvas.setPaint(oldPaint);
        canvas.setColor(oldColor);
        canvas.setStroke(oldStroke);
    }
    protected void paintGraphics(Graphics2D canvas) {
        canvas.setStroke(graphicsStroke);
        canvas.setColor(Color.ORANGE);
        GeneralPath graphics = new GeneralPath();
        for (int i=0; i<graphicsData.length; i++) {
            Point2D.Double point = xyToPoint(graphicsData[i][0],
                    graphicsData[i][1]);
            if (i>0) {
                graphics.lineTo(point.getX(), point.getY());
            } else {
                graphics.moveTo(point.getX(), point.getY());
            }
        }
        canvas.draw(graphics);
    }
    protected void paintMarkers(Graphics2D canvas) {
        int k = 0, sum = 0;
        for (Double[] point : graphicsData) {
            int number = (int) (point[1]/1);
            int num1 = number;
            int num2 = number;
            while(((int)(num1/ 10)) != 0){
                num1 = (int)(num1 /10);
                k++;
            }
            for(int i=0; i <= k; i++){
                   sum += num2 % 10;
                   num2 = (int)(num2 / 10);
               }
            if (sum < 10 || (int)(point[1]/1) < 10) {
                // Выбрать красный цвета для контуров маркеров
                canvas.setColor(Color.RED);
                // Выбрать красный цвет для закрашивания маркеров внутри
                canvas.setPaint(Color.RED);
            } else {
                // Выбрать красный цвета для контуров маркеров
                canvas.setColor(Color.YELLOW);
                // Выбрать красный цвет для закрашивания маркеров внутри
                canvas.setPaint(Color.YELLOW);
            }sum =  0;
            canvas.setStroke(markerStroke);
            GeneralPath path = new GeneralPath();
            Point2D.Double center = xyToPoint(point[0], point[1]);
            canvas.draw(new Line2D.Double(shiftPoint(center, -8, -8), shiftPoint(center, 8, -8)));
            canvas.draw(new Line2D.Double(shiftPoint(center, 0, -1), shiftPoint(center, 8, -8)));
            canvas.draw(new Line2D.Double(shiftPoint(center, 0, -1), shiftPoint(center, -8, -8)));
            Point2D.Double corner = shiftPoint(center, 3, 3);
        }
    }
    protected void paintAxis(Graphics2D canvas) {
// Установить особое начертание для осей
        canvas.setStroke(axisStroke);
// Оси рисуются чѐрным цветом
        canvas.setColor(Color.BLACK);
// Стрелки заливаются чѐрным цветом
        canvas.setPaint(Color.BLACK);
// Подписи к координатным осям делаются специальным шрифтом
        canvas.setFont(axisFont);
// Создать объект контекста отображения текста - для получения характеристик устройства (экрана)
                FontRenderContext context = canvas.getFontRenderContext();
        if (minX<=0.0 && maxX>=0.0) {
            canvas.draw(new Line2D.Double(xyToPoint(0, maxY),
                    xyToPoint(0, minY)));
// Стрелка оси Y
            GeneralPath arrow = new GeneralPath();
// Установить начальную точку ломаной точно на верхний конец оси Y
            Point2D.Double lineEnd = xyToPoint(0, maxY);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
// Вести левый "скат" стрелки в точку с относительными координатами (5,20)
            arrow.lineTo(arrow.getCurrentPoint().getX()+5,
                    arrow.getCurrentPoint().getY()+20);
// Вести нижнюю часть стрелки в точку с относительными координатами (-10, 0)
            arrow.lineTo(arrow.getCurrentPoint().getX()-10,
                    arrow.getCurrentPoint().getY());
            arrow.closePath();
            canvas.draw(arrow); // Нарисовать стрелку
            canvas.fill(arrow);
            Rectangle2D bounds = axisFont.getStringBounds("y", context);
            Point2D.Double labelPos = xyToPoint(0, maxY);
            canvas.drawString("y", (float)labelPos.getX() + 10,
                    (float)(labelPos.getY() - bounds.getY()));
        }
        if (minY<=0.0 && maxY>=0.0) {
                    canvas.draw(new Line2D.Double(xyToPoint(minX, 0), xyToPoint(maxX, 0)));
// Стрелка оси X
            GeneralPath arrow = new GeneralPath();
// Установить начальную точку ломаной точно на правый конец оси X
            Point2D.Double lineEnd = xyToPoint(maxX, 0);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
// Вести верхний "скат" стрелки в точку с относительными координатами (-20,-5)
            arrow.lineTo(arrow.getCurrentPoint().getX()-20,
                    arrow.getCurrentPoint().getY()-5);
// Вести левую часть стрелки в точку с относительными координатами (0, 10)
            arrow.lineTo(arrow.getCurrentPoint().getX(),
                    arrow.getCurrentPoint().getY()+10);
// Замкнуть треугольник стрелки
            arrow.closePath();
            canvas.draw(arrow); // Нарисовать стрелку
            canvas.fill(arrow);
            // Закрасить стрелку
// Нарисовать подпись к оси X
// Определить, сколько места понадобится для надписи "x"
            Rectangle2D bounds = axisFont.getStringBounds("x", context);
            Rectangle2D centerBounds = axisFont.getStringBounds("1", context);
            Point2D.Double centerLabelPos = xyToPoint(0,1);
            Point2D.Double labelPos = xyToPoint(maxX, 0);
// Вывести надпись в точке с вычисленными координатами
            canvas.drawString("x", (float)(labelPos.getX() -
                    bounds.getWidth() - 10), (float)(labelPos.getY() + bounds.getY()));
            canvas.drawString("1", (float)centerLabelPos.getX() + 10,
                    (float)(centerLabelPos.getY() - centerBounds.getY()));
            canvas.draw(new Line2D.Double(xyToPoint(-0.05, 1), xyToPoint(0.05, 1)));
        }
    }
    /* Метод-помощник, осуществляющий преобразование координат.
     * Оно необходимо, т.к. верхнему левому углу холста с координатами
     * (0.0, 0.0) соответствует точка графика с координатами (minX, maxY), где
     * minX - это самое "левое" значение X, а
     * maxY - самое "верхнее" значение Y.
     */
    protected Point2D.Double xyToPoint(double x, double y) {
// Вычисляем смещение X от самой левой точки (minX)
        double deltaX = x - minX;
// Вычисляем смещение Y от точки верхней точки (maxY)
        double deltaY = maxY - y;
        return new Point2D.Double(deltaX*scale, deltaY*scale);
    }
    /* Метод-помощник, возвращающий экземпляр класса Point2D.Double
     * смещѐнный по отношению к исходному на deltaX, deltaY
     * К сожалению, стандартного метода, выполняющего такую задачу, нет.
     */
    protected Point2D.Double shiftPoint(Point2D.Double src, double deltaX,
                                        double deltaY) {
// Инициализировать новый экземпляр точки
        Point2D.Double dest = new Point2D.Double();
// Задать еѐ координаты как координаты существующей точки + заданные смещения
        dest.setLocation(src.getX() + deltaX, src.getY() + deltaY);
        return dest;
    }













}
