package bubblechat.plugin;

import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

public class BubbleChat {

    private final TextDisplay textDisplay;

    private double yAxisOffset;
    private double targetYAxisOffset;
    
    private Vector3f scale;
    private Vector3f targetScale;

    BubbleChat(TextDisplay textDisplay, double initialOffset) {
        this.textDisplay = textDisplay;
        this.yAxisOffset = initialOffset;
        this.targetYAxisOffset = initialOffset;
        
        this.targetScale = textDisplay.getTransformation().getScale();
        this.scale = new Vector3f(targetScale);
        
    }

    public void tick() {

        this.scale.x += (targetScale.x - scale.x) * 0.2f;
        this.scale.y += (targetScale.y - scale.y) * 0.2f;
        this.scale.z += (targetScale.z - scale.z) * 0.2f;

        this.yAxisOffset += (targetYAxisOffset - yAxisOffset) * 0.2;

        Transformation old = textDisplay.getTransformation();

        Transformation updated = new Transformation(
            old.getTranslation(),
            old.getLeftRotation(),
            scale,
            old.getRightRotation()
        );

        textDisplay.setTransformation(updated);
    }

    public void updateTargetTextScale(float x, float y) {
    	targetScale.x = x;
    	targetScale.y = y;
    }
    
    public void updateTargetTextColor() {
    }
    
    public void decreaseTargetTextScale(float percentage) {
        float factor = 1 - (percentage / 100f);

        targetScale.x *= factor;
        targetScale.y *= factor;
    }

    public TextDisplay getTextDisplay() {
        return textDisplay;
    }

    public double getYAxisOffset() {
        return yAxisOffset;
    }

    public void setTargetYAxisOffset(double offset) {
        this.targetYAxisOffset = offset;
    }
    
    public void updateTargetYAxisOffset(double offset) {
    	this.targetYAxisOffset += offset;
    }
}