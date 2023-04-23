package com.ray3k.unbox2d;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import dev.lyze.gdxUnBox2d.BodyDefType;
import dev.lyze.gdxUnBox2d.Box2dPhysicsWorld;
import dev.lyze.gdxUnBox2d.GameObject;
import dev.lyze.gdxUnBox2d.UnBox;
import dev.lyze.gdxUnBox2d.behaviours.box2d.Box2dBehaviour;
import dev.lyze.gdxUnBox2d.behaviours.SoutBehaviour;
import dev.lyze.gdxUnBox2d.behaviours.box2d.fixtures.CreateBox2dBoxFixtureBehaviour;
import dev.lyze.gdxUnBox2d.behaviours.box2d.fixtures.CreateBox2dCircleFixtureBehaviour;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class SampleGame03 extends ApplicationAdapter {
    public FitViewport viewport;
    private SpriteBatch batch;
    private UnBox<Box2dPhysicsWorld> unBox;
    private Box2DDebugRenderer debugRenderer;

    @Override
    public void create() {
        viewport = new FitViewport(30, 30);
        viewport.getCamera().translate(0, 0, 0);
        batch = new SpriteBatch();
        debugRenderer = new Box2DDebugRenderer();
        unBox = new UnBox<>(new Box2dPhysicsWorld(new World(new Vector2(0, 0), true)));

        GameObject rightGo = new GameObject(unBox);
        GameObject leftGo = new GameObject(unBox);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.position.set(5f, 0);
        new Box2dBehaviour(bodyDef, rightGo);

        bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.position.set(-5f, 0);
        new Box2dBehaviour(bodyDef, leftGo);

        new CreateBox2dCircleFixtureBehaviour(.5f, rightGo);
        new CreateBox2dCircleFixtureBehaviour(.5f, leftGo);

        //new CreateBox2dBoxFixtureBehaviour(.5f, .5f, rightGo);
        //new CreateBox2dBoxFixtureBehaviour(.5f, .5f, leftGo);

        new SoutBehaviour("Right GO", false, rightGo);
        new SoutBehaviour("Left GO", false, leftGo);

        // Attach a movement behaviour to both game objects
        new MoveBehaviour(true, rightGo);
        new MoveBehaviour(false, leftGo);
    }

    @Override
    public void render() {
        ScreenUtils.clear(Color.GRAY);

        // Step through physics and update loops
        unBox.preRender(Gdx.graphics.getDeltaTime());

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        // Render the state
        batch.begin();
        unBox.render(batch);
        //ex. stage.draw();
        batch.end();

        // Debug render all box2d bodies
        debugRenderer.render(unBox.getPhysicsWorld().getWorld(), viewport.getCamera().combined);

        // Clean up render loop
        unBox.postRender();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}
