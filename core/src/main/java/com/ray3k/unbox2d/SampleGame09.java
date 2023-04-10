package com.ray3k.unbox2d;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
import dev.lyze.gdxUnBox2d.behaviours.Box2dBehaviour;
import dev.lyze.gdxUnBox2d.behaviours.SoutBehaviour;
import dev.lyze.gdxUnBox2d.behaviours.fixtures.CreateBoxFixtureBehaviour;
import dev.lyze.gdxUnBox2d.behaviours.fixtures.CreateCircleFixtureBehaviour;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class SampleGame09 extends ApplicationAdapter {
    public FitViewport viewport;
    private SpriteBatch batch;
    private UnBox<Box2dPhysicsWorld> unBox;
    private Box2DDebugRenderer debugRenderer;
    private TextureAtlas textureAtlas;

    @Override
    public void create() {
        viewport = new FitViewport(30, 30);
        viewport.getCamera().translate(0, 0, 0);
        batch = new SpriteBatch();
        debugRenderer = new Box2DDebugRenderer();
        unBox = new UnBox<>(new Box2dPhysicsWorld(new World(new Vector2(0, 0), true)));
        textureAtlas = new TextureAtlas(Gdx.files.internal("textures.atlas"));

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

        new CreateCircleFixtureBehaviour(.5f, rightGo);
        new CreateCircleFixtureBehaviour(.5f, leftGo);

        new SoutBehaviour("Right GO", false, rightGo);
        new SoutBehaviour("Left GO", false, leftGo);

        // Attach a movement behaviour to both game objects
        new MoveBehaviour(true, leftGo);
        new KeyboardImprovedBehaviour(rightGo);
        new PlayerCollisionImprovedBehaviour(rightGo);

        new TeamEnemyBehaviour(leftGo);
        new TeamPlayerBehaviour(rightGo);

        Sprite sprite = new Sprite(textureAtlas.findRegion("tractor"));
        sprite.setSize(1, 1);
        sprite.setOriginCenter();
        new SpriteBehavior(rightGo, -.5f, -.5f, sprite);

        sprite = new Sprite(textureAtlas.findRegion("spider"));
        sprite.setSize(1, 1);
        sprite.setOriginCenter();
        new SpriteBehavior(leftGo, -.5f, -.5f, sprite);

        GameObject wall = new GameObject(unBox);
        bodyDef = new BodyDef();
        bodyDef.type = BodyType.StaticBody;
        bodyDef.position.set(10, 0);
        new Box2dBehaviour(bodyDef, wall);
        new CreateBoxFixtureBehaviour(.5f, 10, wall);
        sprite = new Sprite(textureAtlas.findRegion("wall-vertical"));
        sprite.setSize(1, 20);
        sprite.setOriginCenter();
        new SpriteBehavior(wall, -.5f, -10, sprite);

        wall = new GameObject(unBox);
        bodyDef = new BodyDef();
        bodyDef.type = BodyType.StaticBody;
        bodyDef.position.set(-10, 0);
        new Box2dBehaviour(bodyDef, wall);
        new CreateBoxFixtureBehaviour(.5f, 10, wall);
        sprite = new Sprite(textureAtlas.findRegion("wall-vertical"));
        sprite.setSize(1, 20);
        sprite.setOriginCenter();
        new SpriteBehavior(wall, -.5f, -10, sprite);

        wall = new GameObject(unBox);
        bodyDef = new BodyDef();
        bodyDef.type = BodyType.StaticBody;
        bodyDef.position.set(0, 9.5f);
        new Box2dBehaviour(bodyDef, wall);
        new CreateBoxFixtureBehaviour(9.5f, .5f, wall);
        sprite = new Sprite(textureAtlas.findRegion("wall-horizontal"));
        sprite.setSize(19, 1);
        sprite.setOriginCenter();
        new SpriteBehavior(wall, -9.5f, -.5f, sprite);

        wall = new GameObject(unBox);
        bodyDef = new BodyDef();
        bodyDef.type = BodyType.StaticBody;
        bodyDef.position.set(0, -9.5f);
        new Box2dBehaviour(bodyDef, wall);
        new CreateBoxFixtureBehaviour(9.5f, .5f, wall);
        sprite = new Sprite(textureAtlas.findRegion("wall-horizontal"));
        sprite.setSize(19, 1);
        sprite.setOriginCenter();
        new SpriteBehavior(wall, -9.5f, -.5f, sprite);
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