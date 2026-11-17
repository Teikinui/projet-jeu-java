package com.teikinui.game.view;

import com.badlogic.gdx.graphics.Color;
import com.teikinui.game.level.LevelTheme;

/** Couleurs de niveau et texte de moquerie propres à un pays. */
public record ThemeStyle(Color hudBar, Color accent, Color soft, Color halo, Color sky, String taunt) {
    public ThemeStyle {
        if (hudBar == null || accent == null || soft == null || halo == null || sky == null
                || taunt == null || taunt.isBlank()) {
            throw new IllegalArgumentException("Un style de pays doit définir ses couleurs et sa phrase.");
        }
    }

    public static ThemeStyle of(LevelTheme theme) {
        if (theme == null) {
            throw new IllegalArgumentException("Le thème ne peut pas être nul.");
        }
        return switch (theme) {
            case TAHITI -> make("#0E3B3C", "#FF8A7E", "#CFEDEB", "#FFD166", "#8FDCDD",
                    "Ce palmier-là ? T'es sûr ?");
            case CHINE -> make("#2A0E0E", "#F2C14E", "#F3D9C4", "#F2C14E", "#7A1616",
                    "Il sent bon, ce ramen, hein ?");
            case TUNISIE -> make("#3B1D12", "#8EC1EE", "#F3E0C8", "#D7261E", "#F7E3B8",
                    "Il pique pas, celui-là. Promis.");
            case VIETNAM -> make("#1A2A0E", "#F2C14E", "#CFE3D6", "#C8322A", "#CFE3D6",
                    "Gauche, c'est à droite. Ou l'inverse ?");
            case SRI_LANKA -> make("#2A1408", "#F2992E", "#F6C27A", "#D7261E", "#F6C27A",
                    "Le singe vise bien, hein ?");
            case TURQUIE -> make("#2A1A1A", "#3CB7B0", "#E8CDBB", "#E0B04A", "#F7D9B5",
                    "Regarde la mosaïque, pas tes pieds.");
            case FRANCE -> make("#2B2B33", "#E0A04A", "#EFE4C6", "#E0A04A", "#A9CBEA",
                    "Un croissant, ça donne des ailes ?");
            case LAPONIE -> make("#1C2A4A", "#59E3A0", "#CBFAFC", "#F2C14E", "#2E4470",
                    "Ça glisse un peu, non ?");
        };
    }

    private static ThemeStyle make(String bar, String accent, String soft, String halo,
                                   String sky, String taunt) {
        return new ThemeStyle(Color.valueOf(bar), Color.valueOf(accent), Color.valueOf(soft),
                Color.valueOf(halo), Color.valueOf(sky), taunt);
    }
}
