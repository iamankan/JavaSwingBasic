package com.taship.swing.util;

import com.taship.swing.util.Point2D;

import java.util.ArrayList;

public class CalculateBazier {
    private ArrayList<Point2D> pixels2d = new ArrayList<Point2D>();
    private float precision=0.01f;
    private int pointCount;
    private Point2D[] p2d;
    private ArrayList<Point2D> finalPixels= new ArrayList<Point2D>();
    public ArrayList<Point2D> getFinalPixels(){
        return this.finalPixels;
    }
    public CalculateBazier(Point2D[] p2d){
        this.p2d=p2d;
        this.pointCount=this.p2d.length;
        if(this.pointCount > 1){
            finalPixels.add(new Point2D(this.p2d[0].getX(), this.p2d[0].getY()));
            float t = 0;
            while(t <= 1){
                finalPixels.add(this.besierCurvePixel(t));
                t += precision;
            }
            finalPixels.add(new Point2D(this.p2d[this.pointCount-1].getX(), this.p2d[this.pointCount-1].getY()));
        }
    }
    //Factorial
    private static int fact(int n) {
        if(n>=1){
            return n*fact(n-1);
        }else{
            return 1;
        }
    }
    //Bernstein polynomial
    private static double bernstein(float t, int n, int i){

        return (fact(n) / (fact(i) * fact(n-i))) * Math.pow(1-t, n-i) * Math.pow(t, i);
    }

    private Point2D besierCurvePixel(float t){

        double bPoly[] = new double[this.pointCount];

        for(int i = 0; i < this.pointCount; i++){
            bPoly[i] = bernstein(t, this.pointCount, i+1);
        }

        double sumX = 0;
        double sumY = 0;

        for(int i = 0; i < this.pointCount;  i++){
            sumX += bPoly[i] * this.p2d[i].getX();
            sumY += bPoly[i] * this.p2d[i].getY();
        }

        int x, y;
        x = (int) Math.round(sumX);
        y = (int) Math.round(sumY);

        Point2D tmp_p2d = new Point2D(x, y);

        return tmp_p2d;

//        g.drawLine(x + offsetX, y + offsetY, x + offsetX, y + offsetY);

    }
}
