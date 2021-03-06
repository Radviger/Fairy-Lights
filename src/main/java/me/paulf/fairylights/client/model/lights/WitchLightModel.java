package me.paulf.fairylights.client.model.lights;

import me.paulf.fairylights.client.model.AdvancedRendererModel;
import me.paulf.fairylights.util.Mth;

public final class WitchLightModel extends LightModel {
    public WitchLightModel() {
        final AdvancedRendererModel rim = new AdvancedRendererModel(this, 58, 7);
        rim.setRotationPoint(0, -1, 0);
        rim.addBox(-4.0F, 0, -4.0F, 8, 1, 8, 0);
        final AdvancedRendererModel middleBottom = new AdvancedRendererModel(this, 56, 58);
        middleBottom.setRotationPoint(0, -2, 0);
        middleBottom.addBox(-2, 0, -2, 4, 2, 4, 0);
        final AdvancedRendererModel beltPoke = new AdvancedRendererModel(this, 66, 4);
        beltPoke.setRotationPoint(0.2F, 0.5F, -0.5F);
        beltPoke.addBox(0, 0, 0, 1, 1, 1, 0);
        final AdvancedRendererModel buckle = new AdvancedRendererModel(this, 0, 27);
        buckle.setRotationPoint(0, -0.6F, -2.9F);
        buckle.addBox(0, 0, -1, 1, 2, 2, 0);
        buckle.setRotationAngles(0, -Mth.HALF_PI, 0);
        final AdvancedRendererModel belt = new AdvancedRendererModel(this, 62, 0);
        belt.setRotationPoint(0, -4.5F, 0);
        belt.addBox(-2.5F, 0, -2.5F, 5, 1, 5, 0);
        final AdvancedRendererModel middleTop = new AdvancedRendererModel(this, 52, 52);
        middleTop.setRotationPoint(0, -3, 0);
        middleTop.addBox(-1.5F, 0, -1.5F, 3, 3, 3, 0);
        final AdvancedRendererModel tip = new AdvancedRendererModel(this, 15, 54);
        tip.setRotationPoint(0, 0, 0);
        tip.addBox(-1, 0, -1, 2, 2, 2, 0);
        middleBottom.addChild(rim);
        middleTop.addChild(middleBottom);
        buckle.addChild(beltPoke);
        belt.addChild(buckle);
        tip.addChild(middleTop);
        this.colorableParts.addChild(tip);
        this.amutachromicParts.addChild(belt);
    }

    @Override
    public boolean hasRandomRotation() {
        return true;
    }
}
