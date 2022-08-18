package boid;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

class BoidFlock 
{
    //Variables
    private List<Boid> boids;
    
    //Constructor
    public BoidFlock()
    { 
        boids = new ArrayList<>();
    }           
    //Mehtods
    public void addBoid(Boid boid)
    {
        //Function Definition
        boids.add(boid);
        
    }
    public Boid removeBoid()
    {
        //Function Definition
        boids.get(0).requestStop();
        return  boids.remove(0);//Removes Boid index Zero
    }
    public synchronized List<Boid> getNeighbours(Boid boid)
    {
        //Function Definition    
        List<Boid> neighbours = new ArrayList<>();
        double distance;
        for(int i = 0; i < boids.size(); i++){
            distance = Math.sqrt((Math.pow((boid.getPostionX()- boids.get(i).getPostionX()), 2) 
                    + Math.pow((boid.getPostionY()- boids.get(i).getPostionY()), 2)));
            if(distance <= Boid.RADIUS_DETECTION && boids.get(i) != boid){
                neighbours.add(boids.get(i));
            }
        }
        return neighbours;  
    }
    public int size()
    {
        //Function Definition
        if(boids.isEmpty()){
            return 0;
        }else{
            return boids.size();
        }
    }   
    public void drawBoids(Graphics g)
    {
        //Function Definition
        for(Boid boid : boids){
            boid.draw(g);
        }   
        
    }
}
