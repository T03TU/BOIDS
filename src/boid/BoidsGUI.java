/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package boid;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.border.CompoundBorder;

public class BoidsGUI extends JPanel implements ActionListener
{
    private final JButton addBoidButton, addBoidsButton ,removeBoidButton, removeAllButton;
    private final mainPanel drawPanel;
    private final JPanel southPanel, northPanel;
    private final BoidFlock boids = new BoidFlock();
    private final Timer timer;
    public static JSlider COHESIONSLIDER, ALIGNMENTSLIDER,SEPARATIONSLIDER,RADIUSSLIDER;
    public BoidsGUI(){
        super(new BorderLayout());
        southPanel = new JPanel();
        northPanel = new JPanel();
//Sliders-----------------------------------------------------------------------
        COHESIONSLIDER= new JSlider(JSlider.HORIZONTAL,0,100,0);
        COHESIONSLIDER.setBorder(new CompoundBorder(BorderFactory.createTitledBorder("Cohesion: ")
                    ,COHESIONSLIDER.getBorder()));
        northPanel.add(COHESIONSLIDER);
        ALIGNMENTSLIDER= new JSlider(JSlider.HORIZONTAL,0,100,0);
        ALIGNMENTSLIDER.setBorder(new CompoundBorder(BorderFactory.createTitledBorder("Alignment: ")
                    ,ALIGNMENTSLIDER.getBorder()));
        northPanel.add(ALIGNMENTSLIDER);
        SEPARATIONSLIDER= new JSlider(JSlider.HORIZONTAL,0,100,0);
        SEPARATIONSLIDER.setBorder(new CompoundBorder(BorderFactory.createTitledBorder("Separation: ")
                    ,SEPARATIONSLIDER.getBorder()));
        northPanel.add(SEPARATIONSLIDER);
        RADIUSSLIDER = new JSlider(JSlider.HORIZONTAL,1,500,1);
        RADIUSSLIDER.setBorder(new CompoundBorder(BorderFactory.createTitledBorder("Radius: ")
                    ,RADIUSSLIDER.getBorder()));
        northPanel.add(RADIUSSLIDER);
//Buttons-----------------------------------------------------------------------
        addBoidButton = new JButton("ADD BOID");
        addBoidButton.addActionListener(this);
        
        southPanel.add(addBoidButton);
        
        addBoidsButton = new JButton("ADD 100 BOIDS");
        addBoidsButton.addActionListener(this);
        
        southPanel.add(addBoidsButton);

        removeBoidButton = new JButton("REMOVE BOID");
        removeBoidButton.addActionListener(this);
        
        southPanel.add(removeBoidButton);
        
        removeAllButton = new JButton("REMOVE ALL");
        removeAllButton.addActionListener(this);
        
        southPanel.add(removeAllButton);
        
        drawPanel = new mainPanel();
        add(northPanel, BorderLayout.NORTH);
        add(southPanel, BorderLayout.SOUTH);
        add(drawPanel, BorderLayout.CENTER);
        
        timer = new Timer(20,this);
        timer.start();
    }
    
    private class mainPanel extends JPanel
    {
        public mainPanel()
        {
            setBackground(Color.BLACK);
            setPreferredSize(new Dimension(500,500));
        }
        
        @Override
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Boid.WORLD_WIDTH = getWidth();
            Boid.WORLD_HEIGHT = getHeight();
            boids.drawBoids(g);
        }         
    }
    
     @Override
    public void actionPerformed(ActionEvent ae)
    {
       Object source  = ae.getSource();
       
       if(source == addBoidButton){
            Boid boid = new Boid(boids);  
            Thread thread= new Thread(boid);
            thread.start();
       }
       if(source == addBoidsButton){
           for(int i = 0; i < 100; i++){
                Boid boid = new Boid(boids);  
                Thread thread= new Thread(boid);
                thread.start();
           }
       }
       if(source == removeBoidButton)
       { 
           try
           {
              boids.removeBoid();
           }catch(IndexOutOfBoundsException e)
           {
               System.out.println("There are no more boids!!");
           }
       }
       if(source == removeAllButton){
           while(boids.size() != 0 ){
               boids.removeBoid();
           }
       }
       drawPanel.repaint();
    }
    
    public static void main(String[] args)
    {  JFrame frame = new JFrame("Boid Flocking");
       // kill all threads when frame closes
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.getContentPane().add(new BoidsGUI());
       frame.pack();
       // position the frame in the middle of the screen
       Toolkit tk = Toolkit.getDefaultToolkit();
       Dimension screenDimension = tk.getScreenSize();
       Dimension frameDimension = frame.getSize();
       frame.setLocation((screenDimension.width-frameDimension.width)/2,
          (screenDimension.height-frameDimension.height)/2);
       frame.setVisible(true);
       // now display something while the main thread is still alive
       for (int i=0; i<20; i++)
       {  System.out.println("Main thread counting: " + i);
          try
          {  
              Thread.sleep(500); // delay for 500ms
          }
          catch (InterruptedException e)
          {}
       }
       System.out.println("Main thread about to die");
    }
    
}
