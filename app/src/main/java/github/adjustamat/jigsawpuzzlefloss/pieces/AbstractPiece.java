package github.adjustamat.jigsawpuzzlefloss.pieces;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.shapes.PathShape;

import androidx.annotation.NonNull;

import github.adjustamat.jigsawpuzzlefloss.game.Container;

/**
 * A {@link SinglePiece}, or a {@link LargerPiece} made up of two or more of the former that
 * fit together.
 */
public abstract class AbstractPiece {
    public static final float SIDE_SIZE = 120f;
    public static final float HALF_SIZE = 60f;

    /**
     * The type of jigsaw edge of a puzzle piece.
     */
    public static enum EdgeType {
        EDGE,
        IN,
        OUT
    }

    public enum Direction {
        NORTH(1, 2, 0, 0, 1, 0, 0, -1),
        SOUTH(1, 2, 0, 0, 0, 1, 0, 1),
        EAST(2, 1, 0, 1, 0, 0, 1, 0),
        WEST(2, 1, 1, 0, 0, 0, -1, 0);
        public final int startWidth;
        public final int startHeight;
        public final int startX1;
        public final int startX2;
        public final int startY1;
        public final int startY2;
        public final int directionX;
        public final int directionY;
        //public final int ;
        //public final int ;
        //public final int ;

        Direction(int startWidth, int startHeight,
                  int startX1, int startX2, int startY1, int startY2,
                  int directionX, int directionY) {
            this.startWidth = startWidth;
            this.startHeight = startHeight;
            this.startX1 = startX1;
            this.startX2 = startX2;
            this.startY1 = startY1;
            this.startY2 = startY2;
            this.directionX = directionX;
            this.directionY = directionY;
        }

        public Direction opposite() {
            switch (this) {
                case NORTH:
                    return SOUTH;
                case SOUTH:
                    return NORTH;
                case EAST:
                    return WEST;
                default:
                    return EAST;
            }
        }
    }

    Direction

    EdgeType leftEdge;
    EdgeType topEdge;
    EdgeType rightEdge;
    EdgeType bottomEdge;

    PathShape imageMask;
    SVGPath outline; // the mask of the ImagePuzzle image, and outline to draw when rotating or when drawing 3d-effect (emboss).
    RectF edgeWidths;

    Point correctPuzzlePosition;

    Group groupParent;
    Integer indexInGroup;

    @NonNull
    Container containerParent;
    public int indexInContainer;
    PointF positionInContainer; // null when container is Box. (use indexInContainer instead)
    boolean lockedInPlace; // always false when container is Box.


    protected AbstractPiece(@NonNull Container containerParent) {
        this.containerParent = containerParent;
    }

    public void setContainer(Container newParent) {
        this.containerParent = newParent;
    }

    public @NonNull Container getContainer() {
        return containerParent;
    }

    public boolean isLockedInPlace() {
        return lockedInPlace;
    }

    public void setLockedInPlace(boolean locked) {
        lockedInPlace = locked;
    }

    void removeFromGroup() {
        if (groupParent != null) {
            groupParent.removeMe(indexInGroup);
            setNullGroup();
        }
    }

    private void setNullGroup() {
        groupParent = null;
        indexInGroup = null;
    }

    void setGroup(@NonNull Group group, int index) {
        if (groupParent != null) {
            groupParent.removeMe(indexInGroup);
        }
        groupParent = group;
        indexInGroup = index;
    }

    public boolean isGrouped() {
        return groupParent != null && !groupParent.isLonelyPiece();
    }

    public boolean isLeftEdge() {
        return leftEdge == EdgeType.EDGE;
    }

    public boolean isTopEdge() {
        return topEdge == EdgeType.EDGE;
    }

    public boolean isRightEdge() {
        return rightEdge == EdgeType.EDGE;
    }

    public boolean isBottomEdge() {
        return bottomEdge == EdgeType.EDGE;
    }

    public boolean isEdgePiece() {
        return isLeftEdge() || isTopEdge() || isRightEdge() || isBottomEdge();
    }

    public boolean isCornerPiece() {
        return (isLeftEdge() || isRightEdge()) && (isTopEdge() || isBottomEdge());
    }
}
