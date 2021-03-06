package me.paulf.fairylights.server.fastener.connection.type.letter;

import com.google.common.base.MoreObjects;
import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.client.gui.EditLetteredConnectionScreen;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.connection.Catenary;
import me.paulf.fairylights.server.fastener.connection.ConnectionType;
import me.paulf.fairylights.server.fastener.connection.PlayerAction;
import me.paulf.fairylights.server.fastener.connection.Segment;
import me.paulf.fairylights.server.fastener.connection.collision.Intersection;
import me.paulf.fairylights.server.fastener.connection.type.Connection;
import me.paulf.fairylights.server.fastener.connection.type.Lettered;
import me.paulf.fairylights.server.net.clientbound.OpenEditLetteredConnectionScreenMessage;
import me.paulf.fairylights.util.styledstring.StyledString;
import me.paulf.fairylights.util.styledstring.StylingPresence;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public final class LetterBuntingConnection extends Connection implements Lettered {
    public static final SymbolSet SYMBOLS = SymbolSet.from(6, 10, "0,6,1,3,2,6,3,6,4,6,5,6,6,6,7,6,8,6,9,6,A,8,B,7,C,8,D,7,E,7,F,7,G,8,H,7,I,2,J,6,K,8,L,7,M,10,N,8,O,8,P,7,Q,8,R,7,S,7,T,8,U,7,V,8,W,10,X,8,Y,8,Z,7, ,6");

    private static final float TRACKING = 2;

    private static final StylingPresence SUPPORTED_STYLING = new StylingPresence(true, false, false, false, false, false);

    private StyledString text;

    private Letter[] letters = new Letter[0];

    private Letter[] prevLetters;

    public LetterBuntingConnection(final World world, final Fastener<?> fastener, final UUID uuid, final Fastener<?> destination, final boolean isOrigin, final CompoundNBT compound) {
        super(world, fastener, uuid, destination, isOrigin, compound);
    }

    public LetterBuntingConnection(final World world, final Fastener<?> fastener, final UUID uuid) {
        super(world, fastener, uuid);
        this.text = new StyledString();
    }

    @Override
    public float getRadius() {
        return 0.9F / 32;
    }

    public Letter[] getLetters() {
        return this.letters;
    }

    public Letter[] getPrevLetters() {
        return MoreObjects.firstNonNull(this.prevLetters, this.letters);
    }

    @Override
    public ConnectionType getType() {
        return ConnectionType.LETTER_BUNTING;
    }

    @Override
    public void processClientAction(final PlayerEntity player, final PlayerAction action, final Intersection intersection) {
        if (this.openTextGui(player, action, intersection)) {
            super.processClientAction(player, action, intersection);
        }
    }

    @Override
    public void onConnect(final World world, final PlayerEntity user, final ItemStack heldStack) {
        if (this.text.isEmpty()) {
            FairyLights.network.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) user), new OpenEditLetteredConnectionScreenMessage<>(this));
        }
    }

    @Override
    protected void onUpdateEarly() {
        this.prevLetters = this.letters;
        for (final Letter letter : this.letters) {
            letter.tick();
        }
    }

    @Override
    protected void onCalculateCatenary() {
        this.updateLetters();
    }

    private void updateLetters() {
        if (this.text.isEmpty()) {
            this.prevLetters = this.letters;
            this.letters = new Letter[0];
        } else {
            final Catenary catenary = this.getCatenary();
            float textWidth = 0;
            int textLen = 0;
            final float[] pointOffsets = new float[this.text.length()];
            final float catLength = catenary.getLength();
            for (int i = 0; i < this.text.length(); i++) {
                final float w = SYMBOLS.getWidth(this.text.charAt(i));
                pointOffsets[i] = textWidth + w / 2;
                textWidth += w + TRACKING;
                if (textWidth > catLength) {
                    break;
                }
                textLen++;
            }
            final float offset = catLength / 2 - textWidth / 2;
            for (int i = 0; i < textLen; i++) {
                pointOffsets[i] += offset;
            }
            int pointIdx = 0;
            this.prevLetters = this.letters;
            final boolean hasPrevLetters = this.prevLetters != null;
            final List<Letter> letters = new ArrayList<>(this.text.length());
            final Segment[] segments = catenary.getSegments();
            double distance = 0;
            for (int i = 0; i < segments.length; i++) {
                final Segment seg = segments[i];
                final double length = seg.getLength();
                for (int n = pointIdx; n < textLen; n++) {
                    final float pointOffset = pointOffsets[n];
                    if (pointOffset < distance + length) {
                        final double t = (pointOffset - distance) / length;
                        final Vec3d point = seg.pointAt(t);
                        final Letter letter = new Letter(pointIdx, point, seg.getRotation(), SYMBOLS, this.text.charAt(pointIdx), this.text.styleAt(pointIdx));
                        if (hasPrevLetters && pointIdx < this.prevLetters.length) {
                            letter.inherit(this.prevLetters[pointIdx]);
                        }
                        letters.add(letter);
                        pointIdx++;
                    } else {
                        break;
                    }
                }
                if (pointIdx == textLen) {
                    break;
                }
                distance += length;
            }
            this.letters = letters.toArray(new Letter[letters.size()]);
        }
    }

    @Override
    public StylingPresence getSupportedStyling() {
        return SUPPORTED_STYLING;
    }

    @Override
    public boolean isSupportedCharacter(final char chr) {
        return SYMBOLS.contains(chr);
    }

    @Override
    public boolean isSuppportedText(final StyledString text) {
        float len = 0;
        final float available = this.getCatenary().getLength();
        for (int i = 0; i < text.length(); i++) {
            final float w = SYMBOLS.getWidth(text.charAt(i));
            len += w + TRACKING;
            if (len > available) {
                return false;
            }
            if (!text.styleAt(i).isPlain()) {
                return false;
            }
        }
        return Lettered.super.isSuppportedText(text);
    }

    @Override
    public void setText(final StyledString text) {
        this.text = text;
        this.dataUpdateState = true;
    }

    @Override
    public StyledString getText() {
        return this.text;
    }

    @Override
    public Function<Character, Character> getCharInputTransformer() {
        return Character::toUpperCase;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Screen createTextGUI() {
        return new EditLetteredConnectionScreen<>(this);
    }

    @Override
    public CompoundNBT serializeLogic() {
        final CompoundNBT compound = super.serializeLogic();
        compound.put("text", StyledString.serialize(this.text));
        return compound;
    }

    @Override
    public void deserializeLogic(final CompoundNBT compound) {
        super.deserializeLogic(compound);
        this.text = StyledString.deserialize(compound.getCompound("text"));
    }
}
