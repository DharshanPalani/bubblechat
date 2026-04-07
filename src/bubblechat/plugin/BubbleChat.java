package bubblechat.plugin;

import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

public class BubbleChat {

    private final TextDisplay text;

    private double yAxisOffset;
    private double targetYAxisOffset;
    
    private Vector3f scale;
    private Vector3f targetScale;
    
    BubbleChat(TextDisplay text, double initialOffset) {
        this.text = text;
        this.yAxisOffset = initialOffset;
        this.targetYAxisOffset = initialOffset;
        
        this.targetScale = text.getTransformation().getScale();
        this.scale = new Vector3f(targetScale);
        
    }

    public void tick() {

        this.scale.x += (targetScale.x - scale.x) * 0.2f;
        this.scale.y += (targetScale.y - scale.y) * 0.2f;
        this.scale.z += (targetScale.z - scale.z) * 0.2f;

        this.yAxisOffset += (targetYAxisOffset - yAxisOffset) * 0.2;

        Transformation old = text.getTransformation();

        Transformation updated = new Transformation(
            old.getTranslation(),
            old.getLeftRotation(),
            scale,
            old.getRightRotation()
        );

        text.setTransformation(updated);
    }
    
    public void updateTargetTextScale(float x, float y) {
    	targetScale.x = x;
    	targetScale.y = y;
    }
    
    public void decreaseTargetTextScale(float percentage) {
        float factor = 1 - (percentage / 100f);

        targetScale.x *= factor;
        targetScale.y *= factor;
    }

    public TextDisplay getTextDisplay() {
        return text;
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