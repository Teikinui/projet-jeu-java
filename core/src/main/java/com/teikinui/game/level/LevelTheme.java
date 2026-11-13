package com.teikinui.game.level;

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
            new float[] {0.80f, 0.62f, 0.30f});

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
}