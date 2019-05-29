import org.joml.Intersectionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;

public class CameraBoxSelectionDetector {

    private final Vector3f max;

    private final Vector3f min;

    private final Vector2f nearFar;

    private Vector3f dir;

    public CameraBoxSelectionDetector() {
        dir = new Vector3f();
        min = new Vector3f();
        max = new Vector3f();
        nearFar = new Vector2f();
    }

    public boolean selectGameItem(ArrayList<GameItem> gameItems, Camera camera) {
        dir = camera.getViewMatrix().positiveZ(dir).negate();
        return selectGameItem(gameItems, camera.getPosition(), dir);
    }
    
    protected boolean selectGameItem(ArrayList<GameItem> gameItems, Vector3f center, Vector3f dir) {
        boolean selected = false;
        GameItem selectedGameItem = null;
        float closestDistance = Float.POSITIVE_INFINITY;

        for (GameItem gameItem : gameItems) {
            min.set(gameItem.getPosition());
            max.set(gameItem.getPosition());
            min.add(-gameItem.getScale(), -gameItem.getScale(), -gameItem.getScale());
            max.add(gameItem.getScale(), gameItem.getScale(), gameItem.getScale());
            if (Intersectionf.intersectRayAab(center, dir, min, max, nearFar) && nearFar.x < closestDistance) {
                closestDistance = nearFar.x;
                selectedGameItem = gameItem;
            }
        }

        if (selectedGameItem != null) {
            selected = true;
        }
        return selected;
    }
}
