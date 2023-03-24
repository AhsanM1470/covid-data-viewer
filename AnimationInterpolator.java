import javafx.animation.Interpolator;

public class AnimationInterpolator extends Interpolator {
    /**
     * Exponential growth ratio to allow for fast -> slow animation
     * derived by desmos graphing calculator: y = 1 - (2.6^(-6.9420x))
     * takes in a value between 0 and 1, returns a value between 0 and 1 determining
     * accelration of movement during transition
     */
    // make a app
    @Override
    protected double curve(double timePassedRatio) {
        return (timePassedRatio >= 1.0) ? 1.0 : 1 - Math.pow(2, -6.9_420 * timePassedRatio);
    }
}
