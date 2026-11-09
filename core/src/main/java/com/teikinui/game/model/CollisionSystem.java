package com.teikinui.game.model;

/** Résout les déplacements du joueur contre les murs, sans dépendance LibGDX. */
public final class CollisionSystem {
    // Petite marge pour éviter de compter une case touchée seulement par son bord.
    private static final float EPSILON = 0.0001f;

    /**
     * Déplace le joueur horizontalement, puis verticalement, en corrigeant sa position
     * lorsqu'un mur est rencontré.
     *
     * @param player joueur à déplacer
     * @param level grille contenant les murs
     * @param dt temps écoulé depuis la dernière image, en secondes
     */
    public void move(Player player, Level level, float dt) {
        moveX(player, level, dt);
        moveY(player, level, dt);
    }

    /** Vérifie toutes les colonnes traversées par le joueur sur l'axe X. */
    private void moveX(Player player, Level level, float dt) {
        float vx = player.getVx();
        if (vx == 0f) {
            return;
        }

        // Position X prévue avant correction éventuelle par un mur.
        float targetX = player.x + vx * dt;
        // Rangées occupées par la hitbox verticale du joueur.
        int firstRow = floorToTile(player.y);
        int lastRow = floorToTile(player.y + player.height - EPSILON);

        if (vx > 0f) {
            // En allant à droite, on vérifie chaque colonne touchée par le bord droit.
            int firstCol = floorToTile(player.x + player.width);
            int lastCol = floorToTile(targetX + player.width - EPSILON);
            for (int col = firstCol; col <= lastCol; col++) {
                for (int row = firstRow; row <= lastRow; row++) {
                    if (level.isWall(col, row)) {
                        player.x = col * Level.TILE - player.width;
                        player.stopHorizontalMovement();
                        return;
                    }
                }
            }
        } else {
            // En allant à gauche, on vérifie chaque colonne touchée par le bord gauche.
            int firstCol = floorToTile(player.x) - 1;
            int lastCol = floorToTile(targetX);
            for (int col = firstCol; col >= lastCol; col--) {
                for (int row = firstRow; row <= lastRow; row++) {

                    if (level.isWall(col, row)) {
                        player.x = (col + 1) * Level.TILE;
                        player.stopHorizontalMovement();
                        return;
                    }
                }
            }
        }

        player.x = targetX;
    }

    /** Résout les collisions verticales : sol en descente, plafond en montée. */
    private void moveY(Player player, Level level, float dt) {
        float vy = player.getVy();
        // Position Y prévue avant la détection du sol ou du plafond.
        float targetY = player.y + vy * dt;

        // Le joueur peut chevaucher plusieurs colonnes pendant sa chute ou son saut.
        int firstCol = floorToTile(player.x);
        int lastCol = floorToTile(player.x + player.width - EPSILON);

        if (vy < 0f) {
            // En descendant, parcourir les rangées du joueur vers sa position prévue.
            int firstRow = floorToTile(player.y - EPSILON);
            int lastRow = floorToTile(targetY - EPSILON);

            for (int row = firstRow; row >= lastRow; row--) {
                for (int col = firstCol; col <= lastCol; col++) {
                    if (level.isWall(col, row)) {
                        // Le joueur repose juste au-dessus du mur; il a atterri.
                        player.y = (row + 1) * Level.TILE;
                        player.land();
                        return;
                    }
                }
            }
            // Aucune case murale : la chute continue et le joueur quitte le sol.
            player.y = targetY;
            player.leaveGround();
        } else if (vy > 0f) {
            // En montant, parcourir les rangées atteintes par le haut de la hitbox.
            int firstRow = floorToTile(player.y + player.height);
            int lastRow = floorToTile(targetY + player.height - EPSILON);

            for (int row = firstRow; row <= lastRow; row++) {
                for (int col = firstCol; col <= lastCol; col++) {
                    if (level.isWall(col, row)) {
                        // Le joueur est replacé sous le mur; son saut s'arrête.
                        player.y = row * Level.TILE - player.height;
                        player.stopVerticalMovement();
                        return;
                    }
                }
            }
            player.y = targetY;
        }
    }

    /** Convertit une position en pixels vers l'index de case correspondant. */
    private int floorToTile(float position) {
        // floor (et pas une simple conversion int) gère aussi les coordonnées négatives.
        return (int) Math.floor(position / Level.TILE);
    }
}
