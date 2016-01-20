package com.github.ferrosilicon.ike.world.util;

import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public final class WorldBuilder {

    public static Array<Body> buildShapes(Map map, float pixels, World world) {
        final Array<Body> bodies = new Array<Body>();

        for (final MapObject object : map.getLayers().get("Collision").getObjects()) {
            if (object instanceof TextureMapObject) {
                continue;
            }

            Shape shape;
            if (object instanceof RectangleMapObject) {
                shape = getRectangle((RectangleMapObject) object, pixels);
            } else if (object instanceof PolygonMapObject) {
                shape = getPolygon((PolygonMapObject) object, pixels);
            } else if (object instanceof PolylineMapObject) {
                shape = getPolyline((PolylineMapObject) object, pixels);
            } else if (object instanceof CircleMapObject) {
                shape = getCircle((CircleMapObject) object, pixels);
            } else {
                continue;
            }

            final BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;

            final Body body = world.createBody(bodyDef);
            body.createFixture(shape, 1);

            bodies.add(body);

            shape.dispose();
        }
        return bodies;
    }

    private static PolygonShape getRectangle(final RectangleMapObject rectangleObject,
                                             final float ppt) {
        final Rectangle rectangle = rectangleObject.getRectangle();
        final PolygonShape polygon = new PolygonShape();
        final Vector2 size = new Vector2((rectangle.x + rectangle.width * 0.5f) / ppt,
                (rectangle.y + rectangle.height * 0.5f) / ppt);
        polygon.setAsBox(rectangle.width * 0.5f / ppt,
                rectangle.height * 0.5f / ppt,
                size,
                0.0f);
        return polygon;
    }

    private static CircleShape getCircle(final CircleMapObject circleObject, final float ppt) {
        final Circle circle = circleObject.getCircle();
        final CircleShape circleShape = new CircleShape();
        circleShape.setRadius(circle.radius / ppt);
        circleShape.setPosition(new Vector2(circle.x / ppt, circle.y / ppt));
        return circleShape;
    }

    private static PolygonShape getPolygon(final PolygonMapObject polygonObject, final float ppt) {
        final PolygonShape polygon = new PolygonShape();
        final float[] vertices = polygonObject.getPolygon().getTransformedVertices();
        final float[] worldVertices = new float[vertices.length];

        for (int i = 0; i < vertices.length; ++i) {
            worldVertices[i] = vertices[i] / ppt;
        }

        polygon.set(worldVertices);
        return polygon;
    }

    private static ChainShape getPolyline(final PolylineMapObject polylineObject,
                                          final float ppt) {
        final float[] vertices = polylineObject.getPolyline().getTransformedVertices();
        final Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < vertices.length / 2; ++i) {
            worldVertices[i] = new Vector2();
            worldVertices[i].x = vertices[i * 2] / ppt;
            worldVertices[i].y = vertices[i * 2 + 1] / ppt;
        }

        final ChainShape chain = new ChainShape();
        chain.createChain(worldVertices);
        return chain;
    }
}