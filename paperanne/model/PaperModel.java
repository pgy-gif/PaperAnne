package com.paperanne.model;

import javafx.beans.property.*;

public class PaperModel {
    private final DoubleProperty x = new SimpleDoubleProperty();
    private final DoubleProperty y = new SimpleDoubleProperty();
    private final DoubleProperty width = new SimpleDoubleProperty();
    private final DoubleProperty height = new SimpleDoubleProperty();
    private final DoubleProperty rotation = new SimpleDoubleProperty();
    private final StringProperty type = new SimpleStringProperty("normal");
    private final BooleanProperty isDragging = new SimpleBooleanProperty();

    public PaperModel(double x, double y, double w, double h) {
        setX(x);
        setY(y);
        setWidth(w);
        setHeight(h);
    }

    public PaperModel(double x, double y, double width, double height,double r, String type) {
        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);
        setType(type);
        setRotation(r);
    }

    // Getter 和 Setter 方法
    public double getX() {
        return x.get();
    }

    public void setX(double x) {
        this.x.set(x);
    }

    public DoubleProperty xProperty() {
        return x;
    }

    public double getY() {
        return y.get();
    }

    public void setY(double y) {
        this.y.set(y);
    }

    public DoubleProperty yProperty() {
        return y;
    }

    public double getWidth() {
        return width.get();
    }

    public void setWidth(double width) {
        this.width.set(width);
    }

    public DoubleProperty widthProperty() {
        return width;
    }

    public double getHeight() {
        return height.get();
    }

    public void setHeight(double height) {
        this.height.set(height);
    }

    public DoubleProperty heightProperty() {
        return height;
    }

    public double getRotation() {
        return rotation.get();
    }

    public void setRotation(double rotation) {
        this.rotation.set(rotation);
    }

    public DoubleProperty rotationProperty() {
        return rotation;
    }

    public String getType() {
        return type.get();
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public StringProperty typeProperty() {
        return type;
    }

    public boolean isDragging() {
        return isDragging.get();
    }

    public void setDragging(boolean dragging) {
        this.isDragging.set(dragging);
    }

    public BooleanProperty draggingProperty() {
        return isDragging;
    }
}