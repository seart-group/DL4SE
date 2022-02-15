package org.example;

class Circle {

    private static final double PI = 3.14159;
    private double radius;

    public Circle() {
        radius = 0.0;
    }

    public Circle(double r) {
        radius = r;
    }

    public void setRadius(double r) {
        radius = r;
    }

    public double getRadius() {
        return radius;
    }

    public double getArea() {
        return PI * radius * radius;
    }
}