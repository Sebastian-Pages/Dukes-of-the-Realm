package game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public abstract class Sprite {

    protected ImageView imageView;

    protected Pane layer;

    protected double x;
    protected double y;

    private boolean removable = false;

    protected double w;
    protected double h;

    public Sprite(Pane layer, Image image, double x, double y) {

        this.layer = layer;
        this.x = x;
        this.y = y;

        this.imageView = new ImageView(image);
        this.imageView.relocate(x, y);

        this.w = image.getWidth(); 
        this.h = image.getHeight(); 

        addToLayer();

    }

    public void addToLayer() {
        this.layer.getChildren().add(this.imageView);
    }

    public void removeFromLayer() {
        this.layer.getChildren().remove(this.imageView);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public boolean isRemovable() {
        return removable;
    }

    protected ImageView getView() {
        return imageView;
    }
    
    protected void setView(Image image) {
    	this.removeFromLayer();
        this.imageView=new ImageView(image);
        imageView.relocate(this.x, this.y);
        this.addToLayer();
    }
    protected void setView(Image image,double angle) {
    	this.removeFromLayer();
        this.imageView=new ImageView(image);
        imageView.relocate(this.x, this.y);
        this.imageView.setRotate(angle);
        this.addToLayer();
    }
    
    public void updateUI() {
        imageView.relocate(x, y);
    }

    public double getWidth() {
        return w;
    }

    public double getHeight() {
        return h;
    }

    public double getCenterX() {
        return x + w * 0.5;
    }

    public double getCenterY() {
        return y + h * 0.5;
    }

    // TODO: per-pixel-collision
    public boolean collidesWith(Sprite sprite) {
    	return getView().getBoundsInParent().intersects(sprite.getView().getBoundsInParent());
    }



    public void remove() {
        this.removable = true;
    }

    public abstract void checkRemovability();

}
