package me.paulf.fairylights.client.model.lights;

import me.paulf.fairylights.client.model.AdvancedRendererModel;
import me.paulf.fairylights.util.Mth;

public final class OrnateLanternModel extends LightModel {
    public OrnateLanternModel() {
        this.amutachromicParts.setTextureOffset(21, 0);
        this.amutachromicParts.addBox(-1, 0.5F, -1, 2, 1, 2); // point

        this.amutachromicParts.setTextureOffset(0, 3);
        this.amutachromicParts.addBox(-1.5F, -1.5F, -1.5F, 3, 2, 3); // capUpper

        this.amutachromicParts.setTextureOffset(43, 15);
        this.amutachromicParts.addBox(-2.5F, -1.75F, -2.5F, 5, 1, 5); // ogee

        this.amutachromicParts.setTextureOffset(23, 27);
        this.amutachromicParts.addBox(-3, -2.5F, -3, 6, 1, 6); // cap

        this.amutachromicParts.setTextureOffset(43, 21);
        this.amutachromicParts.addBox(-2.5F, -8.5F, -2.5F, 5, 1, 5);

        for (int i = 0; i < 4; i++) {
            final AdvancedRendererModel frame = new AdvancedRendererModel(this, 4 * i + 47, 27);
            frame.addBox(-0.5F, 0, -0.5F, 1, 6, 1);
            frame.setRotationPoint(2.1F * ((i & 2) == 0 ? 1 : -1), -8F, 2.1F * ((i + 1 & 2) == 0 ? 1 : -1));
            frame.rotateAngleX = 5 * Mth.DEG_TO_RAD;
            frame.rotateAngleY = (90 * i + 45) * Mth.DEG_TO_RAD;
            this.amutachromicParts.addChild(frame);
        }

        this.colorableParts.setTextureOffset(48, 6);
        this.colorableParts.addBox(-2, -7.5F, -2, 4, 5, 4); // glass
        this.colorableParts.glowExpandAmount = 0.2F;
    }

    @Override
    public boolean hasRandomRotation() {
        return true;
    }
}
