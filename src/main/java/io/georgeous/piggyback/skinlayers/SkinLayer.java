package io.georgeous.piggyback.skinlayers;

public enum SkinLayer {
    CAPE(1),
    JACKET(1 << 1),
    LEFT_SLEEVE(1 << 2),
    RIGHT_SLEEVE(1 << 3),
    LEFT_PANTS_LEG(1 << 4),
    RIGHT_PANTS_LEG(1 << 5),
    HAT(1 << 6),
    ALL(0x7f);

    private final byte bit;

    SkinLayer(int bit) {
        this.bit = (byte)bit;
    }

    public byte getBit() {
        return this.bit;
    }
}
