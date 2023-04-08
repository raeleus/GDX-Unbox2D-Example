package com.ray3k.unbox2d;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import dev.lyze.gdxUnBox2d.*;
import dev.lyze.gdxUnBox2d.behaviours.Box2dBehaviour;
import dev.lyze.gdxUnBox2d.behaviours.SoutBehaviour;
import dev.lyze.gdxUnBox2d.behaviours.fixtures.CreateBoxFixtureBehaviour;
import space.earlygrey.shapedrawer.ShapeDrawer;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Box2DSimpleExample extends ApplicationAdapter {
    public static FitViewport viewport;
    private SpriteBatch batch;
    private UnBox<Box2dPhysicsWorld> unBox;
    private Box2DDebugRenderer debugRenderer;

    @Override
    public void create() {
        viewport = new FitViewport(30, 10);
        viewport.getCamera().translate(0, 0, 0);
        batch = new SpriteBatch();

        //Create a white pixel texture. It is suggested to provide a white TextureRegion from your TextureAtlas instead...
        Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.drawPixel(0, 0);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        TextureRegion region = new TextureRegion(texture, 0, 0, 1, 1);

        //Create a ShapeDrawer and adjust its settings to match the pixels per meter of your world.
        ShapeDrawer shapeDrawer = new ShapeDrawer(batch, region);
        shapeDrawer.setPixelSize(.05f);
        shapeDrawer.setDefaultLineWidth(.05f);
        debugRenderer = new Box2DDebugRenderer();

        unBox = new UnBox<>(new Box2dPhysicsWorld(new World(new Vector2(0, 0), true)));
        GameObject rightGo = new GameObject(unBox);
        GameObject leftGo = new GameObject(unBox);

        new Box2dBehaviour(BodyDefType.DynamicBody, rightGo);
        new Box2dBehaviour(BodyDefType.DynamicBody, leftGo);

        // Attach a logging behaviour to both of the game objects
        new SoutBehaviour("Right GO", false, rightGo);
        new SoutBehaviour("Left GO", false, leftGo);

        new CreateBoxFixtureBehaviour(.5f, .5f, leftGo);
        new CreateBoxFixtureBehaviour(.5f, .5f, rightGo);

        // Attach a movement behaviour to both game objects
        new MoveBehaviour(true, rightGo);
        new MoveBehaviour(false, leftGo);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(.25f, .25f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Step through physics and update loops
        unBox.preRender(Gdx.graphics.getDeltaTime());

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        // Render the state
        batch.begin();
        unBox.render(batch);
        batch.end();

        // Debug render all box2d bodies
        debugRenderer.render(unBox.getPhysicsWorld().getWorld(), viewport.getCamera().combined);

        // Clean up render loop
        unBox.postRender();
    }

    @Override
    public void dispose() {

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}
