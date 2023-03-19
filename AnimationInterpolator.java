import javafx.animation.Interpolator;

public class AnimationInterpolator extends Interpolator {
    /**
     * Exponential growth ratio to allow for fast -> slow animation
     * derived by desmos graphing calculator: y = 1 - (2^(-9x))
     */
    @Override
    protected double curve(double timePassedRatio){
        return (timePassedRatio >= 0.98)? 1.0 : 1 - Math.pow(2.0, -9.8 * timePassedRatio);
    }
}
