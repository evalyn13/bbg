package com.example.bbg1;

public class Velocity1 {
    private float x, y; // Change to float

    // Default constructor
    public Velocity1() {
        this.x = 0;
        this.y = 0;
    }

    // Parameterized constructor
    public Velocity1(float x, float y) { // Change to float
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) { // Change to float
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) { // Change to float
        this.y = y;
    }

    public void increaseX(float amount) { // Change to float
        this.x += amount;
    }

    public void increaseY(float amount) { // Change to float
        this.y += amount;
    }

    public void decreaseX(float amount) { // Change to float
        this.x -= amount;
    }

    public void decreaseY(float amount) { // Change to float
        this.y -= amount;
    }

    @Override
    public String toString() {
        return "Velocity1{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
