package tracing;

public class Settings {
    private TurnPolicy turnPolicy = TurnPolicy.MINORITY;
    private int turdSize =  2;
    private boolean optimizeCurve = true;
    private int alphaMax = 1;
    private double optimalityTolerance = 0.2;

    public TurnPolicy getTurnPolicy() {
        return turnPolicy;
    }

    public void setTurnPolicy(TurnPolicy turnPolicy) {
        this.turnPolicy = turnPolicy;
    }

    public int getTurdSize() {
        return turdSize;
    }

    public void setTurdSize(int turdSize) {
        this.turdSize = turdSize;
    }

    public boolean isOptimizeCurve() {
        return optimizeCurve;
    }

    public void setOptimizeCurve(boolean optimizeCurve) {
        this.optimizeCurve = optimizeCurve;
    }

    public int getAlphaMax() {
        return alphaMax;
    }

    public void setAlphaMax(int alphaMax) {
        this.alphaMax = alphaMax;
    }

    public double getOptimalityTolerance() {
        return optimalityTolerance;
    }

    public void setOptimalityTolerance(double optimalityTolerance) {
        this.optimalityTolerance = optimalityTolerance;
    }
}
