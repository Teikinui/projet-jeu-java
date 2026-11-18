package com.teikinui.game.model;

/** Contact rectangle normalized to an entity's movement/draw rectangle. */
public record ContactBounds(float left, float bottom, float right, float top) {
    public static final ContactBounds FULL = new ContactBounds(0f, 0f, 1f, 1f);

    public ContactBounds {
        if (!Float.isFinite(left) || !Float.isFinite(bottom)
                || !Float.isFinite(right) || !Float.isFinite(top)
                || right <= left || top <= bottom) {
            throw new IllegalArgumentException("Contact bounds must be finite and non-empty");
        }
    }

    /**
     * Converts an opaque PNG pixel rectangle (right and bottom exclusive) to game-space fractions.
     * PNG coordinates start at the top; game coordinates start at the bottom.
     */
    public static ContactBounds fromAlphaBounds(
            int imageWidth, int imageHeight, int left, int top, int right, int bottom) {
        if (imageWidth <= 0 || imageHeight <= 0 || left < 0 || top < 0
                || right > imageWidth || bottom > imageHeight
                || right <= left || bottom <= top) {
            throw new IllegalArgumentException("Invalid image alpha bounds");
        }
        return new ContactBounds(
                left / (float) imageWidth,
                (imageHeight - bottom) / (float) imageHeight,
                right / (float) imageWidth,
                (imageHeight - top) / (float) imageHeight);
    }
}
