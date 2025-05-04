package org.piaa;

public class Square {
    private final int x;
    private final int y;
    private final int length;

    public Square(int x, int y, int length) {
        this.x = x;
        this.y = y;
        this.length = length;
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        return ">>> Coords : (" + x + ", " + y + "), Size: " + length + "x" + length + " \n";
    }
}