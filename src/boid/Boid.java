package boid;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;
import java.util.Random;

public class Boid implements Runnable
{
//Boid Variables----------------------------------------------------------------
    private double x, y, dx, dy;
    private boolean isAlive;
    private final Color[] color;
    private BoidFlock flock;
    public static int WORLD_WIDTH, WORLD_HEIGHT;
    public final int BOID_SIZE;
    public static float COHESION_WEIGHT, SEPARATION_WEIGHT, 
            ALIGNMENT_WEIGHT, RADIUS_DETECTION;
    public final float MAX_SPEED;
    private Random rand = new Random();
    private int c1, c2, c3;
    private Vector2D V;
//Constructor-------------------------------------------------------------------
    Boid(BoidFlock flock){
        flock.addBoid(this);
        this.flock = flock;
        x = WORLD_WIDTH/2;
        y = WORLD_HEIGHT/2;
        MAX_SPEED = 10;
        dy = rand.nextInt((int)MAX_SPEED) + 1;
        dx = rand.nextInt((int)MAX_SPEED) + 1; 
        switch(rand.nextInt(4)){
            case 0:
                break;
            case 1:
                dx *= -1;
                break;
            case 2:
                dx *= -1;
                dy *= -1;
                break;
            case 3:
                dy *= -1;
                break;
            default:
                break;
        }
        V = new Vector2D(dx,dy);
        color = new Color[4];
        for(int i = 0; i < 4; i++){
            c1 = rand.nextInt(255)  + 1;
            c2 = rand.nextInt(255)  + 1;
            c3 = rand.nextInt(255)  + 1;
            color[i] = new Color(c1, c2, c3);  
        }
        BOID_SIZE = 10;  
        RADIUS_DETECTION = 0;
        COHESION_WEIGHT = 0;
        ALIGNMENT_WEIGHT = 0;
        SEPARATION_WEIGHT =0;           
    }    
//Methods-----------------------------------------------------------------------
    public void requestStop(){
        isAlive = false;
    }   
    @Override
    public void run() {
        Vector2D coherence,separation, alignment;
        isAlive = true;
        while(isAlive) {
            V = new Vector2D(dx,dy);
            COHESION_WEIGHT = BoidsGUI.COHESIONSLIDER.getValue();
            ALIGNMENT_WEIGHT = BoidsGUI.ALIGNMENTSLIDER.getValue();
            SEPARATION_WEIGHT = BoidsGUI.SEPARATIONSLIDER.getValue();
            RADIUS_DETECTION = BoidsGUI.RADIUSSLIDER.getValue();
            coherence = cohere();
            separation = separate(); 
            alignment = align();
            Vector2D velocity = add(add(add(V, coherence), separation), alignment);
            if(velocity.x > MAX_SPEED || velocity.y > MAX_SPEED 
                    || velocity.x < -1*MAX_SPEED || velocity.y < -1*MAX_SPEED){
                velocity = new Vector2D((velocity.x)/(Math.sqrt(Math.pow(velocity.x, 2) + Math.pow(velocity.y, 2)))*MAX_SPEED
                        ,(velocity.y)/(Math.sqrt(Math.pow(velocity.x, 2) + Math.pow(velocity.y, 2))) * MAX_SPEED);
                dx = velocity.x;
                dy = velocity.y;
            }else{
                dx = velocity.x;
                dy = velocity.y;
            }
            try{
                x += dx;
                y += dy;
                if(x > WORLD_WIDTH || x < 0){
                    dx *= -1;
                }
                if(y > WORLD_HEIGHT || y< 0){
                    dy *= -1;
                }
                Thread.sleep(100);
            } catch (InterruptedException ex)  {
            }
        }
    }
    public double getPostionX(){
        return x;
    }
    public double getPostionY(){
        return y;
    }
    public double getMovementX(){
        return dx;
    }
    public double getMovementY(){
        return dx;
    }
    public void setPostionX(double x){
        this.x = x;
    }
    public void setPostionY(double y){
        this.y = y;
    }
    public void setMovementX(double dx){
        this.dx = dx;
    }
    public void setMovementY(double dy){
        this.dy = dy;
    }
    public void draw(Graphics g){
        double speed, velX, velY;
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(6));
        speed = Math.sqrt(dx*dx + dy*dy);
        velX = (BOID_SIZE * dx)/(2*speed);
        velY = (BOID_SIZE * dy)/(2*speed);
        //g2.setColor(Color.WHITE);
        g2.setColor(color[0]);
        g2.drawLine((int)(x - velX + velY),(int)(y - velX - velY),(int)x, (int)y);
        g2.setColor(color[1]);
        g2.drawLine((int)(x - velX - velY),(int)(y + velX - velY),(int)x, (int)y);
        g2.setColor(color[2]);
        g2.drawLine((int)(x - 2*velX),(int)(y - 2*velY),(int)x, (int)y);
        g2.setStroke(new BasicStroke(1));
        g2.setColor(color[3]);
        g2.drawLine((int)(x - velX + velY),(int)(y - velX - velY),(int)x, (int)y);
        g2.drawLine((int)(x - velX - velY),(int)(y + velX - velY),(int)x, (int)y);
        g2.drawLine((int)(x - 2*velX),(int)(y - 2*velY),(int)x, (int)y);
    } 
//Boid Behaviour----------------------------------------------------------------
    Vector2D separate(){
        List<Boid> neighbours = flock.getNeighbours(this);
        if(neighbours.isEmpty())
            return new Vector2D(0,0);
        double sumX = 0, sumY = 0;
        double dV;
        Vector2D separationVector;
        for(Boid i : neighbours){
            dV = Math.sqrt(Math.pow((this.getPostionX() - i.getPostionX()), 2) 
                    + Math.pow(this.getPostionY() - i.getPostionY(), 2));
            if(dV != 0 ){
                sumX += (this.getPostionX() - i.getPostionX())/dV;
                sumY += (this.getPostionY() - i.getPostionY())/dV;
            }
        }
        separationVector = new Vector2D((SEPARATION_WEIGHT/100)*sumX, (SEPARATION_WEIGHT/100)*sumY);
        
        return separationVector;
    }
    Vector2D cohere(){
        //Center
        List<Boid> neighbours = flock.getNeighbours(this);
        if(neighbours.isEmpty())
            return new Vector2D(0,0);
        double sumX = 0;
        double sumY = 0;
        Vector2D cohesionVector;
         //Summation of vectors 
         for(Boid i : neighbours){
             //x 
             sumX += i.getPostionX();
             //y
             sumY += i.getPostionY();
         }
         cohesionVector = new Vector2D((COHESION_WEIGHT/100)*(sumX/neighbours.size() - this.getPostionX())
                 ,(COHESION_WEIGHT/100)*(sumY/neighbours.size() - this.getPostionY()));
         //Cohesion Vector
         return cohesionVector;
    }
    Vector2D align(){
        double sumVX = 0;
        double sumVY = 0;
        Vector2D alignmentVector;
        List<Boid> neighbours = flock.getNeighbours(this);
        if(neighbours.isEmpty())
            return new Vector2D(0,0);
        for(Boid i : neighbours){
            sumVX += i.getMovementX();
            sumVY += i.getMovementY();
        }
        alignmentVector = new Vector2D((ALIGNMENT_WEIGHT/100)*(sumVX/neighbours.size() - this.getMovementX())
                 ,(ALIGNMENT_WEIGHT/100)*(sumVY/neighbours.size() - this.getMovementY()));
        
        return alignmentVector;
    }
    
    public Vector2D add(Vector2D v1, Vector2D v2){
        return new Vector2D(v1.x + v2.x, v1.y + v2.y);
    }
    //2D Vector Class
    protected class Vector2D{
        double x, y;
        public Vector2D(double x, double y){
            this.x = x; 
            this.y = y;
        }
    }
}
