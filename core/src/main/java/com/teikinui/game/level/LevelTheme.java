package com.teikinui.game.level;

import com.teikinui.game.model.ContactBounds;

/**
 * Les pays du voyage, dans l'ordre. Chaque ligne donne le fichier du niveau,
 * le nom affiché, l'objet à attraper et les couleurs (rouge, vert, bleu entre 0 et 1).
 * Ajouter un pays = ajouter une ligne ici + un fichier .txt.
 */
public enum LevelTheme {
    TAHITI("tahiti.txt", "Tahiti", "le palmier",
            new float[] {0.10f, 0.12f, 0.16f},
            new float[] {0.55f, 0.40f, 0.25f},
            new float[] {0.20f, 0.70f, 0.30f}),
    CHINE("chine.txt", "Chine", "la porte du temple",
            new float[] {0.12f, 0.08f, 0.08f},
            new float[] {0.62f, 0.16f, 0.12f},
            new float[] {0.95f, 0.90f, 0.80f}),
    TUNISIE("tunisie.txt", "Tunisie", "le piment rouge",
            new float[] {0.07f, 0.13f, 0.28f},
            new float[] {0.82f, 0.64f, 0.36f},
            new float[] {0.85f, 0.10f, 0.10f}),
    VIETNAM("vietnam.txt", "Vietnam", "le chapeau conique",
            new float[] {0.05f, 0.12f, 0.55f},
            new float[] {0.30f, 0.55f, 0.28f},
            new float[] {0.93f, 0.82f, 0.50f},
            true),
    SRI_LANKA("srilanka.txt", "Sri Lanka", "le piment",
            new float[] {0.08f, 0.22f, 0.14f},
            new float[] {0.72f, 0.40f, 0.22f},
            new float[] {0.85f, 0.10f, 0.10f}),
    TURQUIE("turquie.txt", "Turquie", "la lampe turque",
            new float[] {0.04f, 0.28f, 0.30f},
            new float[] {0.80f, 0.55f, 0.45f},
            new float[] {0.95f, 0.75f, 0.20f}),
    FRANCE("france.txt", "France", "la tour Eiffel",
            new float[] {0.08f, 0.10f, 0.25f},
            new float[] {0.80f, 0.76f, 0.66f},
            new float[] {0.80f, 0.62f, 0.30f}),
    LAPONIE("laponie.txt", "Laponie", "le traîneau du Père Noël",
            new float[] {0.04f, 0.07f, 0.16f},
            new float[] {0.78f, 0.90f, 0.97f},
            new float[] {0.85f, 0.15f, 0.15f});

    private final String fileName;
    private final String displayName;
    private final String goalName;
    private final float[] backgroundColor;
    private final float[] wallColor;
    private final float[] goalColor;
    private final boolean invertedControls;

    LevelTheme(String fileName, String displayName, String goalName,
               float[] backgroundColor, float[] wallColor, float[] goalColor) {
        this(fileName, displayName, goalName, backgroundColor, wallColor, goalColor, false);
    }

    /** Le dernier paramètre à true échange les flèches gauche et droite dans ce pays. */
    LevelTheme(String fileName, String displayName, String goalName,
               float[] backgroundColor, float[] wallColor, float[] goalColor,
               boolean invertedControls) {
        this.invertedControls = invertedControls;
        this.fileName = fileName;
        this.displayName = displayName;
        this.goalName = goalName;
        this.backgroundColor = backgroundColor;
        this.wallColor = wallColor;
        this.goalColor = goalColor;
    }

    /** Le pays suivant, ou null si c'est le dernier. */
    public LevelTheme next() {
        LevelTheme[] all = values();
        int nextIndex = ordinal() + 1;
        return nextIndex < all.length ? all[nextIndex] : null;
    }

    public String getFileName() {
        return fileName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getGoalName() {
        return goalName;
    }

    /** Opaque PNG bounds for the country goal, in the goal's 32×32 tile. */
    public ContactBounds getGoalContactBounds() {
        return switch (this) {
            case TAHITI -> ContactBounds.fromAlphaBounds(32, 32, 8, 3, 24, 29);
            case CHINE -> ContactBounds.fromAlphaBounds(32, 32, 6, 5, 26, 28);
            case TUNISIE -> ContactBounds.fromAlphaBounds(32, 32, 4, 2, 28, 30);
            case VIETNAM -> ContactBounds.fromAlphaBounds(32, 32, 5, 7, 27, 26);
            case SRI_LANKA -> ContactBounds.fromAlphaBounds(32, 32, 11, 4, 21, 28);
            case TURQUIE -> ContactBounds.fromAlphaBounds(32, 32, 8, 6, 25, 26);
            case FRANCE -> ContactBounds.fromAlphaBounds(32, 32, 6, 9, 26, 22);
            case LAPONIE -> ContactBounds.fromAlphaBounds(32, 32, 4, 9, 28, 25);
        };
    }

    public float[] getBackgroundColor() {
        return backgroundColor.clone();
    }

    public float[] getWallColor() {
        return wallColor.clone();
    }

    public float[] getGoalColor() {
        return goalColor.clone();
    }

    public boolean hasInvertedControls() {
        return invertedControls;
    }

    /** Difficulté affichée dans le menu : de 1 à 3 bombes. */
    public int getDifficulty() {
        return switch (this) {
            case TURQUIE, FRANCE -> 1;
            case TAHITI, VIETNAM, SRI_LANKA -> 2;
            case CHINE, TUNISIE, LAPONIE -> 3;
        };
    }

    /** En Laponie, tout le sol est gelé : le joueur glisse. */
    public boolean isIcy() {
        return this == LAPONIE;
    }

    /** En Chine, la vraie sortie est cachée : elle est dessinée comme un trou à pics. */
    public boolean hidesGoal() {
        return this == CHINE;
    }
}