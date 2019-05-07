/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bacterion;

import java.awt.Graphics;
import java.util.LinkedList;

/**
 *
 * @author Diego
 */
public class Player extends Item{
    
    // El estres se mide en porcentaje, 0 -  100
    private int estres;
    private int estresStun;
    private boolean alive;
    private LinkedList<Antibiotico> antibioticos;
    private int[] cargas;
    private int[] cargLimits;
    private boolean[] bactTarget;
    
    private Animation animBact;
    private Animation animBactCarg[];
    
    /**
     * Player constructor
     * @param x
     * @param y
     * @param width
     * @param height
     * @param game 
     * @param speed
     */
    public Player(Game game, int x, int y) {
        super(game, x, y, Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT, Constants.PLAYER_SPEED);
        alive = true;
        estres = Constants.ESTES_INICIAL;
        estresStun = Constants.ESTRES_STUN;
        antibioticos = new LinkedList<>();
        cargas = new int[5];
        cargLimits = new int[5];
        cargLimits[0] = 200;
        cargLimits[1] = 100;
        cargLimits[2] = 150;
        cargLimits[3] = 300;
        cargLimits[4] = 250;
        
        // Con base en el nivel son los sprites de nuestra bacteria
        animBactCarg = new Animation[5];
        switch(game.getLevel()){
            case 1:
                animBact = new Animation(Assets.bacteria0, height);
                animBactCarg[0] = new Animation(Assets.bacteria0Cargada0, height);
                animBactCarg[1] = new Animation(Assets.bacteria0Cargada1, height);
                animBactCarg[2] = new Animation(Assets.bacteria0Cargada2, height);
                animBactCarg[3] = new Animation(Assets.bacteria0Cargada3, height);
                animBactCarg[4] = new Animation(Assets.bacteria0Cargada4, height);
                break;
            case 2:
                animBact = new Animation(Assets.bacteria1, height);
                animBactCarg[0] = new Animation(Assets.bacteria1Cargada0, height);
                animBactCarg[1] = new Animation(Assets.bacteria1Cargada1, height);
                animBactCarg[2] = new Animation(Assets.bacteria1Cargada2, height);
                animBactCarg[3] = new Animation(Assets.bacteria1Cargada3, height);
                animBactCarg[4] = new Animation(Assets.bacteria1Cargada4, height);
                break;
            case 3:
                animBact = new Animation(Assets.bacteria2, height);
                animBactCarg[0] = new Animation(Assets.bacteria2Cargada0, height);
                animBactCarg[1] = new Animation(Assets.bacteria2Cargada1, height);
                animBactCarg[2] = new Animation(Assets.bacteria2Cargada2, height);
                animBactCarg[3] = new Animation(Assets.bacteria2Cargada3, height);
                animBactCarg[4] = new Animation(Assets.bacteria2Cargada4, height);
                break;
            default: break;
        }       
         
        // Este array nos indica los sensores target de nuestra bacteria
        this.bactTarget = Constants.BACT0_TARGET;
    }
    
    public boolean isAlive(){
        return this.alive;
    }
    
    public int getEstres(){
        return estres;
    }
    public void setEstres(int estres){
        this.estres = estres;
    }
    public void estresar(){
        estres+= Constants.ESTRES_INCREASE;
    }
    
    public int getAntibioticosSize(){
        return antibioticos.size();
    }
    
    public Antibiotico getAntibiotico(){
        return antibioticos.poll();
    }
    public boolean hasAntibiotico(){
        return antibioticos.peek()!=null;
    }
    
    public boolean cargaEsto(int index){
        return bactTarget[index];
    }
    
    /**
     * Control del movimiento del player y su 
     */
    @Override
    public void tick() {
        this.animBact.tick();
        for(Animation anim : animBactCarg){
            anim.tick();
        }
        
        // moving player depending on flags
        if (game.getKeyManager().left && getX()>=Constants.MOV_OFFSET) {
           setX(getX() - speed);
        } else if (game.getKeyManager().right && getX()+getWidth()<=game.getWidth()-Constants.MOV_OFFSET-Constants.MOV_OFFSET/2) {
           setX(getX() + speed);
        }
        if (game.getKeyManager().up && getY()>=Constants.MOV_OFFSET) {
           setY(getY() - speed);
        } else if (game.getKeyManager().down && getY()+getHeight()<=game.getHeight()-Constants.MOV_OFFSET-Constants.MOV_OFFSET) {
           setY(getY() + speed);
        }
        
        if(estresStun>0){
            estresStun--;
        } else {
            estres--;
            estresStun = Constants.ESTRES_STUN;
        }
        
        if(estres>=Constants.ESTRES_BAJO && estres<=Constants.ESTRES_ALTO){
            for(int i=0; i<bactTarget.length; i++){
                if(bactTarget[i]){
                    cargas[i]++;
                }
            }
        }
        
        if(estres>=Constants.ESTRES_MORTAL){
            alive = false;
            game.endGame();
        }
        
        for(int i=0; i<cargas.length; i++){
            if(cargas[i]>=cargLimits[i]){
                switch(i){
                    case 0:
                        antibioticos.push(
                                new Antibiotico(game,x,y,Constants.ANTIB_WIDTH,Constants.ANTIB_HEIGHT,AntiType.E_COLI));
                        break;
                    case 1:
                        antibioticos.push(
                                new Antibiotico(game,x,y,Constants.ANTIB_WIDTH,Constants.ANTIB_HEIGHT,AntiType.B_SUBTILIS));
                        break;
                    case 2:
                        antibioticos.push(
                                new Antibiotico(game,x,y,Constants.ANTIB_WIDTH,Constants.ANTIB_HEIGHT,AntiType.P_AERUGINOSA));
                        break;
                    case 3:
                        antibioticos.push(
                                new Antibiotico(game,x,y,Constants.ANTIB_WIDTH,Constants.ANTIB_HEIGHT,AntiType.S_PNEUMONIAE));
                    case 4:
                        antibioticos.push(
                                new Antibiotico(game,x,y,Constants.ANTIB_WIDTH,Constants.ANTIB_HEIGHT,AntiType.S_DYSENTERIAE));
                        break;
                        
                }
                cargas[i] = 0;
                Assets.added.play();
            }
        }
    }
    
    // Guarda la información del objeto en un string
    public String toString() {
        return (x+" "+y);
    }
    
    // Se encarga de guardar en un archivo toda la informacion de nuestra partida
    public void loadFromString(String[] datos) {
        this.x = Integer.parseInt(datos[0]);
        this.y = Integer.parseInt(datos[1]);
    }

    /**
     * render the image of the player 
     * @param g 
     */
    @Override
    public void render(Graphics g) { 
        if(antibioticos.size()>0){
            g.drawImage(animBactCarg[antibioticos.peek().getTipoInt()].getCurrentFrame(), getX(), getY(), getWidth(), getHeight(), null);
        } else {
            g.drawImage(animBact.getCurrentFrame(), getX(), getY(), getWidth(), getHeight(), null);
        }
    }
}
