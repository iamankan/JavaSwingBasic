package com.taship.swing;

import com.taship.swing.util.Config;
import com.taship.swing.util.ConfigAnnot;
import com.taship.swing.util.CalculateBazier;
import com.taship.swing.util.Point2D;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ImageProcessor implements Config {
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new ChooseImageProcessingFrame(Config.FRAME_NAME);
                ImageIcon imageIcon = new ImageIcon(Config.ICON_PATH);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(Config.VISIBILITY);
                frame.setSize(500, 500);
                frame.setResizable(Config.RESIZABLE);
                frame.getContentPane().setBackground(new Color(190, 190, 190));
                frame.setIconImage(imageIcon.getImage());
            }
        });
    }
}

class ChooseImageProcessingFrame extends JFrame implements Config{
    public ChooseImageProcessingFrame(String frameName){
        setTitle(frameName);
        setSize(Config.HEIGHT, Config.WIDTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenu chooseMenu = new JMenu("File");
        JMenuItem drawItem = new JMenuItem("Draw");

        drawItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JFrame drawFrame = new DrawFrame(frameName);
                        drawFrame.setVisible(true);
                        setVisible(false);
                    }
                });
            }
        });
        chooseMenu.add(drawItem);

        JMenuItem openImageItem = new JMenuItem("Open Images");
        openImageItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JFrame openImageFrame = new ImageProcessingFrame(frameName);
                        openImageFrame.setVisible(true);
                        setVisible(false);
                    }
                });
            }
        });
        chooseMenu.add(openImageItem);

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        chooseMenu.add(exitItem);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(chooseMenu);
        setJMenuBar(menuBar);
    }
}

class PaintPanel extends JPanel implements Config{
    public PaintPanel(){
        setPreferredSize(new Dimension(Config.WIDTH, Config.HEIGHT));
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        g.drawString("BLAH", 20, 20);
        g.drawRect(20, 60, 70, 100);
    }
}

@ConfigAnnot
class DrawPanel extends JPanel{
    public DrawPanel(){
        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        setBackground(new Color(255, 0, 100));
        setVisible(ConfigAnnot.TRUE);
    }

    public void setDim(double height, double width){
        Dimension dim = new Dimension((int)width, (int)height);
        this.setPreferredSize(dim);
    }
}

//https://stackoverflow.com/questions/6118737/how-to-draw-in-jpanel-swing-graphics-java
class DrawFrame extends JFrame implements Config{
    public int bazierControlPoints = 0;
    public int bazierControlPointsTmp=0;
    public boolean isBazier = false;

    public Point2D pixels2D[] = null;

    public void setBazierControlPoints(int bazierControlPoints) {
        this.bazierControlPoints = bazierControlPoints;
        this.bazierControlPointsTmp = bazierControlPoints;
    }

    public int getBazierControlPoints(){
        return bazierControlPoints;
    }

    public DrawFrame(String frameName){
        setTitle(frameName);
        setSize(Config.HEIGHT, Config.WIDTH);
        setBackground(new Color(255, 255, 0));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JFrame parent = this;
        DrawFrame classParent = this;

        int height = this.getHeight();
        int width = this.getWidth();

        //Container
        Container drawCont = this.getContentPane();

        drawCont.setLayout(new BoxLayout(drawCont, BoxLayout.Y_AXIS));
//        drawCont.setLayout(new FlowLayout(FlowLayout.LEADING, 10, 90));


        JLabel infoLabel = new JLabel("Info");
//        infoLabel.setLayout(new BoxLayout(infoLabel, BoxLayout.X_AXIS));

        JLabel positionLabel = new JLabel("Position");
//        positionLabel.setLayout(new BoxLayout(positionLabel, BoxLayout.X_AXIS));

        this.add(infoLabel);

        this.add(positionLabel);

        DrawPanel drawPanel = new DrawPanel();
        drawPanel.setDim(height/2.0, width/2.0);

        this.add(drawPanel);

        this.pack();

        drawPanel.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                positionLabel.setText("Mouse @ ("+e.getX()+","+e.getY()+")");
            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        });

        drawPanel.addMouseListener(new MouseListener() {
            String txt=positionLabel.getText();
            @Override
            public void mouseClicked(MouseEvent e) {
                String tmpTxt = txt+"\n Clicked @ (x: "+e.getX()+",y: "+e.getY()+")\n";
                positionLabel.setText(tmpTxt);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                String tmpTxt = txt+"\n Pressed @ (x: "+e.getX()+",y: "+e.getY()+")\n";
                positionLabel.setText(tmpTxt);

                System.out.println("isBazier: "+isBazier);
                System.out.println("bCounter: "+bazierControlPoints);
                if (pixels2D!=null) {
                    System.out.println("pixel2d: " + pixels2D.length);
                }

                if(isBazier && pixels2D!=null && bazierControlPointsTmp>0){
                    Graphics g = drawPanel.getGraphics();
                    g.fillOval(e.getX(), e.getY(), 20, 20);
                    g.setColor(new Color(0,0,0));
                    int idx = bazierControlPoints-bazierControlPointsTmp;
                    System.out.println("idx: "+idx);
                    System.out.println(">>len: "+pixels2D.length);
                    System.out.println(">>> "+pixels2D[idx]);
                    pixels2D[idx] = new Point2D(e.getX(), e.getY());
                    bazierControlPointsTmp--;
                }else if(isBazier && pixels2D!=null){
                    for(Point2D pt:pixels2D){
                        System.out.println(pt.toString());
                    }
                    System.out.println("Calculating Bazier curve");
                    ArrayList<Point2D> finalPixels = new CalculateBazier(pixels2D).getFinalPixels();
                    Graphics g = drawPanel.getGraphics();
                    g.setColor(new Color(255, 255, 255));
                    int i = finalPixels.size();
                    Point2D p2dFinal[] = new Point2D[i];
                    int c=0;
                    for(Point2D finalPixel: finalPixels){
                        System.out.println("F>>>> "+finalPixel.toString());
                        p2dFinal[c++] = finalPixel;
                    }
                    for(int j=1;j<p2dFinal.length;j++){
                        g.drawLine((int)p2dFinal[j-1].getX(), (int)p2dFinal[j-1].getY(),
                                (int)p2dFinal[j].getX(), (int)p2dFinal[j].getY());
                    }
                }
                else{
                    JOptionPane.showMessageDialog(parent,
                            "No points for Bazier are chosen. First select the number of points.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                String tmpTxt = txt+"\n Released @ (x: "+e.getX()+",y: "+e.getY()+")\n";
                positionLabel.setText(tmpTxt);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                positionLabel.setText("Inside the canvas. Ready to draw. Current position: ("+e.getX()+", "+e.getY()+")");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                positionLabel.setText("Exited at: ("+e.getX()+", "+e.getY()+")");
            }
        });

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                parent.addComponentListener(new ComponentListener() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        Component newComponent = e.getComponent();
                        int newHeight = newComponent.getHeight();
                        int newWidth = newComponent.getWidth();
                        parent.setPreferredSize(new Dimension(newWidth, newHeight));
                        drawPanel.setDim(newHeight/2.0, newWidth/2.0);
                        parent.remove(drawPanel);
                        parent.add(drawPanel);
                        parent.pack();
                    }

                    @Override
                    public void componentMoved(ComponentEvent e) {

                    }

                    @Override
                    public void componentShown(ComponentEvent e) {

                    }

                    @Override
                    public void componentHidden(ComponentEvent e) {

                    }
                });
            }
        });

        JMenu menu = new JMenu("File");
        JMenuBar menuBar = new JMenuBar();
        JMenuItem homeItem = new JMenuItem("Home");
        homeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JFrame homeFrame = new ChooseImageProcessingFrame(frameName);
                        homeFrame.setVisible(true);
                        setVisible(false);
                    }
                });
            }
        });
        menu.add(homeItem);

        JMenuItem bazierItem = new JMenuItem("Bazier");
        bazierItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isBazier=false;
                pixels2D=null;
                drawPanel.repaint();
                BazierDialogue bDialogue = new BazierDialogue(classParent);
                bDialogue.addComponentListener(new ComponentListener() {
                    @Override
                    public void componentResized(ComponentEvent e) {

                    }

                    @Override
                    public void componentMoved(ComponentEvent e) {

                    }

                    @Override
                    public void componentShown(ComponentEvent e) {
                        infoLabel.setText("Info");
                    }

                    @Override
                    public void componentHidden(ComponentEvent e) {
                        System.out.println(">> "+bDialogue.getControlPoints());
                        System.out.println(">>> "+classParent.getBazierControlPoints());
                        infoLabel.setText("Bazier curve with "+classParent.getBazierControlPoints()+" control points");
                        isBazier=true;
                        pixels2D = new Point2D[classParent.getBazierControlPoints()];
                    }
                });
            }
        });
        menu.add(bazierItem);

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        menu.add(exitItem);

        menuBar.add(menu);
        setJMenuBar(menuBar);
    }
}

@ConfigAnnot
class BazierDialogue extends JFrame{
    private int numOfControlPoints=4;
    private int controlPoints=0;
    public BazierDialogue(DrawFrame grandParent){
        setPreferredSize(new Dimension(300, 300));
        setBackground(new Color(255, 230, 210));
        setVisible(ConfigAnnot.TRUE);
        setResizable(ConfigAnnot.FALSE);

        JFrame parent = this;

        Container bCont = parent.getContentPane();
        bCont.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 90));

        JLabel controlPointsLabel = new JLabel("Number of Control Points:");
        parent.add(controlPointsLabel);

        JComboBox<Integer> controlPointsCombo = new JComboBox<Integer>();
        for(int cp=1; cp<numOfControlPoints; cp++){
            controlPointsCombo.addItem(cp+1);
        }
        parent.add(controlPointsCombo);

        JButton okBtn = new JButton("OK");
        okBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(ConfigAnnot.FALSE);
            }
        });
        parent.add(okBtn);

        parent.pack();

        controlPointsCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setControlPoints((int)controlPointsCombo.getSelectedItem());
                grandParent.setBazierControlPoints(getControlPoints());
            }
        });

    }

    private void setControlPoints(int x){
        controlPoints = x;
    }

    public int getControlPoints(){
        return controlPoints;
    }
}

class ImageProcessingFrame extends JFrame implements Config{
    private BufferedImage image;

    public ImageProcessingFrame(String frameName){
        setTitle(frameName);
        setSize(Config.HEIGHT, Config.WIDTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        add(new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                if(image!=null){
                    g.drawImage(image, 0, 0, null);
                }
            }
        });

        JMenu fileMenu = new JMenu("File");
        JMenuItem homeItem = new JMenuItem("Home");
        homeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JFrame homeFrame = new ChooseImageProcessingFrame(frameName);
                        homeFrame.setVisible(true);
                        setVisible(false);
                    }
                });
            }
        });
        fileMenu.add(homeItem);

        JMenuItem openItem = new JMenuItem("Open");
        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });
        fileMenu.add(openItem);

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(exitItem);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    public void openFile(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        String[] extensions = ImageIO.getReaderFileSuffixes();
        for(String extension: extensions){
            System.out.println("Ext: "+extension);
        }
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", extensions));
        int r = fileChooser.showOpenDialog(this);
        if ( r != JFileChooser.APPROVE_OPTION){
            return;
        }

        try{
            Image img = ImageIO.read(fileChooser.getSelectedFile());
            image = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);

            image.getGraphics().drawImage(img, 0, 0, null);
        }catch(IOException e){
            JOptionPane.showMessageDialog(this, e);
        }
        repaint();
    }
}