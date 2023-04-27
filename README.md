# UnBox2D Example

This is an example of how to use [UnBox2D](https://github.com/lyze237/gdx-UnBox2D#gdx-unbox2d-unity-box2d), a library for libGDX that mocks the functionality of behaviours in Unity. This serves as a transcript and companion to the [video](https://youtu.be/iLce1Xs067c).

## Introduction
Hey, if you’ve been watching this channel, you already know. libGDX is not Unity. Of course not. Unity is C# and it’s backed by a multi-million dollar company. So, they get all the fun utilities and the benefit of a well-tested, modern engine. libGDX is largely a community driven effort with no fancy toys. But that doesn’t mean we can’t adopt some of Unity’s workflow.

Behaviours! Be-have eee… ours? (UK spelling) Yes, Behaviours are a simple way to add scripted actions to the objects in your game. You write code for a specific type of behavior that you want to apply to your game object. For example, you may want to move your player entity with the keyboard. Apply the behaviour to your game object. The real power of this model is that you can apply this behavior to any entity. This is a fantastic way to reduce excessively long and duplicated code.

You can think of Behaviours like an ECS or Entity Component System “but without the S”. It’s just a new way to approach your code. This is only one aspect of UnBox2D: a new library from Lyze. The goal of this lib is to mock Unity’s behaviour system, execution order, and handling of Box2D to make it easier to access and make more sense. You can add Box2D functionality to any entity by adding a new Box2DBehaviour to the game object. Then add a fixture to it. That’s it.

## Using UnBox2D

Now let’s get into how to use this lib. Follow the instructions on [Github](https://github.com/lyze237/gdx-UnBox2D/wiki/Installation) to add the dependencies to your project. We’ll do some boilerplate code in your game class. Next, you’ll have to create an instance of UnBox. You’ll need to have access to this object throughout your project.

```java
public class SampleGame extends ApplicationAdapter {
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

        new Box2dBehaviour(BodyDefType.DynamicBody, rightGo);
        new Box2dBehaviour(BodyDefType.DynamicBody, leftGo);

        // Attach a logging behaviour to both of the game objects
        new SoutBehaviour("Right GO", false, rightGo);
        new SoutBehaviour("Left GO", false, leftGo);

        new CreateBox2dBoxFixtureBehaviour(.5f, .5f, leftGo);
        new CreateBox2dBoxFixtureBehaviour(.5f, .5f, rightGo);

        // Attach a movement behaviour to both game objects
        new MoveBehaviour(true, rightGo);
        new MoveBehaviour(false, leftGo);
    }
    ...
```

Then you can create your GameObjects. A GameObject doesn’t really do anything on its own. Its physics, its logic, the way it’s drawn are all handled by behaviors. There are a number of built-in behaviors for UnBox2d. We’ll just add a Box2dBehaviour to our game objects to create some basic bodies. This SoutBehavior is a good way to demonstrate how the execution order works. It adds an entry with the libGDX logger every time an event occurs for the GameObject. Then we’ll attach a MoveBehaviour. This is actually something we need to write ourselves. Let’s save that for later.

We need to write our render method now. This will handle the logic and rendering for each frame of our game. Unbox has 3 stages that need to be called: prerender, render, and post render. This is the order you’re supposed to do it. First, clear your screen, call the prerender and feed it the delta time. The nice thing is that Unbox handles all the fixed time step business for you, so you don’t have to reread Gaffer’s article on Fix Your Timestep for the umpteenth time.

```java
...
    @Override
    public void render() {
        ScreenUtils.clear(Color.DARK_GRAY);

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
```

Next, setup your viewport and batch. Render unbox and any other systems you may be using like Scene2D. Since we are using the default debug renderer that depends on shape renderer, we’ll close the batch and call the Box2D debug render method. Finally we’ll call the post render.

The last bit of boilerplate code is to write the resize method to update our viewport.

```java
...
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}
```

This isn’t going to run yet because we have to write that move behavior class. Create a new class and call it MoveBehaviour. A behavior has several methods that need to be overridden. If you want to write all of them, make sure your class extends Behaviour. Otherwise, you can extend BehaviourAdapter and only override the methods that matter to you.

The goal of this behavior is to move the game object’s body to the left or right. Nothing too complicated That will be controlled by a boolean variable. Make a constructor that initializes this value as well as passes the game object to the super constructor.

```java
public class MoveBehaviour extends BehaviourAdapter {
    private final boolean moveToRight;

    public MoveBehaviour(boolean moveToRight, GameObject gameObject) {
        super(gameObject);

        this.moveToRight = moveToRight;
    }

    @Override
    public void fixedUpdate() {
        Vector2 position = getGameObject().getBehaviour(Box2dBehaviour.class).getBody().getPosition();
        getGameObject().getBehaviour(Box2dBehaviour.class).getBody().applyLinearImpulse(0.01f * (moveToRight ? -1 : 1), 0, position.x, position.y, true);
    }

    @Override
    public void update(float delta) {
        Vector2 position = getGameObject().getBehaviour(Box2dBehaviour.class).getBody().getPosition();
        System.out.println("Position: " + position);
    }
}
```

ApplicationAdapter
```java
    @Override
    public void create() {
        viewport = new FitViewport(30, 30);
        viewport.getCamera().translate(0, 0, 0);
        batch = new SpriteBatch();
        debugRenderer = new Box2DDebugRenderer();
        unBox = new UnBox<>(new Box2dPhysicsWorld(new World(new Vector2(0, 0), true)));

        GameObject rightGo = new GameObject(unBox);
        GameObject leftGo = new GameObject(unBox);

        new Box2dBehaviour(BodyDefType.DynamicBody, rightGo);
        new Box2dBehaviour(BodyDefType.DynamicBody, leftGo);

        new SoutBehaviour("Right GO", false, rightGo);
        new SoutBehaviour("Left GO", false, leftGo);

        // Attach a movement behaviour to both game objects
        new MoveBehaviour(true, rightGo);
        new MoveBehaviour(false, leftGo);
    }
```

Now override the fixedUpdate method. FixedUpdate is reserved for anything that involves manipulating the physics of bodies. It is not tied to the frame rate and will always happen at a constant rate. That means you can define speeds without having to multiply it by delta time. This line gets the current position, and this line applies an impulse in the appropriate direction. Just keep in mind that anything you put in here may be executed multiple times for every frame. Or possibly some frames get skipped depending on the speed of the target device. Don’t put anything expensive like drawing to a FrameBuffer in here.

The update method is called once for every frame. So, if you had to describe something that happens over time in this method, you will need to multiply it by delta time. For now, we’ll just print the current position of the game object.

You can run the game now and see what it does. From the output, you can see that one body goes to the right. And this one to the left.

We can’t actually see what’s going on without adding some fixtures to our bodies. Returning to our game class, we can use another built-in behaviour: CreateBox2DCircleFixtureBehaviour. You have to pass in the position in relation to the body and the radius of the circle. Most basic games can just use circles for collision detection. You can also use rectangles if that fits your idea of your game better. CreateBox2DBoxFixtureBehaviour. We should reposition our bodies so they don’t spawn on top of each other. You can do that by passing in a body definition to the Box2DBehaviour instead of just defining a body type. If you’re not familiar with Box2D, I suggest checking out some [tutorials](https://www.iforce2d.net/b2dtut/). Because Box2D is pretty ubiquitous and the feature set is the same all over, any tutorials you find will be relevant.

```java
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
```

Okay, so now they knock into each other. Let’s say that this left object is the player and this right object is an enemy. Let’s exert control over our player by using the keyboard to direct movement. Create a new behavior called KeyboardBehaviour. In the update method, we’ll do some basic keyboard polling to see if we should move up, down, left or right. Then, within the fixedUpdate, we can apply the velocity.

```java
public class KeyboardBehaviour extends BehaviourAdapter {
    private final Vector2 velocity = new Vector2();

    public KeyboardBehaviour(GameObject gameObject) {
        super(gameObject);
    }

    @Override
    public void fixedUpdate() {
        getGameObject().getBehaviour(Box2dBehaviour.class).getBody().setLinearVelocity(velocity);
    }

    @Override
    public void update(float delta) {
        float maxSpeed = 5f;
        velocity.set(0, 0);

        if (Gdx.input.isKeyPressed(Keys.UP)) velocity.y += maxSpeed;
        if (Gdx.input.isKeyPressed(Keys.DOWN)) velocity.y -= maxSpeed;
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) velocity.x += maxSpeed;
        if (Gdx.input.isKeyPressed(Keys.LEFT)) velocity.x -= maxSpeed;
    }
}
```
ApplicationAdapter
```java
    @Override
    public void create() {
        ...

        // Attach a movement behaviour to both game objects
        new MoveBehaviour(true, rightGo);
        new KeyboardBehaviour(leftGo);
    }
```

Well, I’m not entirely satisfied with the movement. This code actually lets the player move diagonally faster. Let’s make a change. That’s the advantage of using a behavior system instead of one big class for every entity. We just need to find the keyboard behavior and any improvements we make will be applied to anything that uses it. We’ll clamp the velocity. Done.

```java
    @Override
    public void update(float delta) {
        float maxSpeed = 5f;
        velocity.set(0, 0);

        if (Gdx.input.isKeyPressed(Keys.UP)) velocity.y += maxSpeed;
        if (Gdx.input.isKeyPressed(Keys.DOWN)) velocity.y -= maxSpeed;
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) velocity.x += maxSpeed;
        if (Gdx.input.isKeyPressed(Keys.LEFT)) velocity.x -= maxSpeed;

        velocity.setLength(MathUtils.clamp(velocity.len(), 0, maxSpeed));
        System.out.println("velocity.len() = " + velocity.len());
    }
```

Now, say I’m a fickle developer. I want the right object to be the player this time. Apply the MoveBehaviour to the left game object and apply the KeyboardBehaviour to the right game object. This way, any character in the game can be controllable by changing a single line.

ApplicationAdapter
```java
        // Attach a movement behaviour to both game objects
        new MoveBehaviour(true, leftGo);
        new KeyboardBehaviour(rightGo);
```

Let’s talk about collisions. Normally you’ll handle collisions by implementing a contact listener in libGDX. This is rather clumsy and leads to having a single monolithic class responsible for handling all sorts of collisions. Unbox2D takes [the approach](https://github.com/lyze237/gdx-UnBox2D/blob/main/src/main/java/dev/lyze/gdxUnBox2d/behaviours/box2d/IBox2dBehaviourEvents.java) that Unity and many other engines do instead. Each individual behavior has its own collision related methods and they are called when the relevant collision event occurs. And within each method, it’s no longer ambiguous as to what object is what. Behaviour other will always be the behavior associated with the other object that this object collided with.

In the context of this game, I want the player to disappear when they touch the enemy. So, new behavior. PlayerCollisionBehavior. This time we will extend Box2dBehaviourAdapter because this behaviour will have events specific to Box2D. This includes all the regular behaviour methods and methods specific to collisions. We’ll specify in onCollisionEnter that we’ll destroy the player GameObject. That’s easy enough. Notice that destroying a body during a collision would normally be a bad thing, but our game did not crash. That’s because Unbox is careful to handle those little details for us at the very end of the current frame after all the behaviours are handled.

```java
public class PlayerCollisionBehaviour extends Box2dBehaviourAdapter {

    public PlayerCollisionBehaviour(GameObject gameObject) {
        super(gameObject);
    }

    @Override
    public void onCollisionEnter(Behaviour other, Contact contact) {
        getGameObject().destroy();
    }
}
```

ApplicationAdapter
```java
    @Override
    public void create() {
        ...

        // Attach a movement behaviour to both game objects
        new MoveBehaviour(true, leftGo);
        new KeyboardBehaviour(rightGo);
        new PlayerCollisionBehaviour(rightGo);
    }
```

This particular code only works because we just have these two objects. It wouldn’t be nice for the player to just die when they touch a wall, for example. There also may be many more kinds of enemies or players in the future. We need some way to identify what team these objects belong to. So, let’s make a couple more behaviors.

TeamEnemyBehaviour.

```java
public class TeamEnemyBehaviour extends BehaviourAdapter {
    public TeamEnemyBehaviour(GameObject gameObject) {
        super(gameObject);
    }
}
```

TeamPlayerBehaviour.

```java
public class TeamPlayerBehaviour extends BehaviourAdapter {
    public TeamPlayerBehaviour(GameObject gameObject) {
        super(gameObject);
    }
}
```

ApplicationAdapter
```java
    @Override
    public void create() {
        ...

        // Attach a movement behaviour to both game objects
        new MoveBehaviour(true, leftGo);
        new KeyboardImprovedBehaviour(rightGo);
        new PlayerCollisionImprovedBehaviour(rightGo);

        new TeamEnemyBehaviour(leftGo);
        new TeamPlayerBehaviour(rightGo);
    }
```

These don’t actually have to do anything. We’ll just apply them to the corresponding game objects. In our player collision code, we can just check if the other GameObject has the TeamEnemyBehaviour, and then destroy the player object.

```java
    @Override
    public void onCollisionEnter(Behaviour other, Contact contact) {
        if (other.getGameObject().getBehaviour(TeamEnemyBehaviour.class) != null) getGameObject().destroy();
    }
```

Speaking of walls, let’s make some of those. Some liberal use of CreateBox2dBoxFixtureBehavior can be used here. Normally I would be reading the positions for these walls and entities from a map file instead of manually creating them.

ApplicationAdapter
```java
    @Override
    public void create() {
        ...
        
        // Attach a movement behaviour to both game objects
        new MoveBehaviour(true, leftGo);
        new KeyboardImprovedBehaviour(rightGo);
        new PlayerCollisionImprovedBehaviour(rightGo);

        new TeamEnemyBehaviour(leftGo);
        new TeamPlayerBehaviour(rightGo);

        GameObject wall = new GameObject(unBox);
        bodyDef = new BodyDef();
        bodyDef.type = BodyType.StaticBody;
        bodyDef.position.set(10, 0);
        new Box2dBehaviour(bodyDef, wall);
        new CreateBox2dBoxFixtureBehaviour(.5f, 10, wall);

        wall = new GameObject(unBox);
        bodyDef = new BodyDef();
        bodyDef.type = BodyType.StaticBody;
        bodyDef.position.set(-10, 0);
        new Box2dBehaviour(bodyDef, wall);
        new CreateBox2dBoxFixtureBehaviour(.5f, 10, wall);

        wall = new GameObject(unBox);
        bodyDef = new BodyDef();
        bodyDef.type = BodyType.StaticBody;
        bodyDef.position.set(0, 9.5f);
        new Box2dBehaviour(bodyDef, wall);
        new CreateBox2dBoxFixtureBehaviour(9.5f, .5f, wall);

        wall = new GameObject(unBox);
        bodyDef = new BodyDef();
        bodyDef.type = BodyType.StaticBody;
        bodyDef.position.set(0, -9.5f);
        new Box2dBehaviour(bodyDef, wall);
        new CreateBox2dBoxFixtureBehaviour(9.5f, .5f, wall);
    }
```

So far, we’ve only been able to see any of these bodies because of the Box2DDebugRenderer. We should create a behavior that can draw our objects to the screen with some Sprite representations. Download the [texture files](). Then this simple SpriteBehaviour will draw the provided image at the body position with a given offset. If there is no attached box2dbehaviour, just set the position as 0, 0.

```java
public class SpriteBehaviour extends BehaviourAdapter {
    private float offsetX;
    private float offsetY;
    private Sprite sprite;
    private final Vector2 position = new Vector2();

    public SpriteBehaviour(GameObject gameObject, float offsetX, float offsetY, Sprite sprite) {
        super(gameObject);
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.sprite = sprite;
    }

    @Override
    public void fixedUpdate() {
        if (getGameObject().getBehaviour(Box2dBehaviour.class) != null)
            position.set(getGameObject().getBehaviour(Box2dBehaviour.class).getBody().getPosition());
        else position.setZero();

        position.add(offsetX, offsetY);
        this.sprite.setPosition(position.x, position.y);
    }

    @Override
    public void render(Batch batch) {
        sprite.draw(batch);
    }
}
```

Apply it to each kind of object.

```java
    private TextureAtlas textureAtlas;
    
    ...
    @Override
    public void create() {
        textureAtlas = new TextureAtlas(Gdx.files.internal("textures.atlas"));
        ...

        Sprite sprite = new Sprite(textureAtlas.findRegion("tractor"));
        sprite.setSize(1, 1);
        sprite.setOriginCenter();
        new SpriteBehaviour(rightGo, -.5f, -.5f, sprite);

        sprite = new Sprite(textureAtlas.findRegion("spider"));
        sprite.setSize(1, 1);
        sprite.setOriginCenter();
        new SpriteBehaviour(leftGo, -.5f, -.5f, sprite);

        GameObject wall = new GameObject(unBox);
        bodyDef = new BodyDef();
        bodyDef.type = BodyType.StaticBody;
        bodyDef.position.set(10, 0);
        new Box2dBehaviour(bodyDef, wall);
        new CreateBox2dBoxFixtureBehaviour(.5f, 10, wall);
        sprite = new Sprite(textureAtlas.findRegion("wall-vertical"));
        sprite.setSize(1, 20);
        sprite.setOriginCenter();
        new SpriteBehaviour(wall, -.5f, -10, sprite);

        wall = new GameObject(unBox);
        bodyDef = new BodyDef();
        bodyDef.type = BodyType.StaticBody;
        bodyDef.position.set(-10, 0);
        new Box2dBehaviour(bodyDef, wall);
        new CreateBox2dBoxFixtureBehaviour(.5f, 10, wall);
        sprite = new Sprite(textureAtlas.findRegion("wall-vertical"));
        sprite.setSize(1, 20);
        sprite.setOriginCenter();
        new SpriteBehaviour(wall, -.5f, -10, sprite);

        wall = new GameObject(unBox);
        bodyDef = new BodyDef();
        bodyDef.type = BodyType.StaticBody;
        bodyDef.position.set(0, 9.5f);
        new Box2dBehaviour(bodyDef, wall);
        new CreateBox2dBoxFixtureBehaviour(9.5f, .5f, wall);
        sprite = new Sprite(textureAtlas.findRegion("wall-horizontal"));
        sprite.setSize(19, 1);
        sprite.setOriginCenter();
        new SpriteBehaviour(wall, -9.5f, -.5f, sprite);

        wall = new GameObject(unBox);
        bodyDef = new BodyDef();
        bodyDef.type = BodyType.StaticBody;
        bodyDef.position.set(0, -9.5f);
        new Box2dBehaviour(bodyDef, wall);
        new CreateBox2dBoxFixtureBehaviour(9.5f, .5f, wall);
        sprite = new Sprite(textureAtlas.findRegion("wall-horizontal"));
        sprite.setSize(19, 1);
        sprite.setOriginCenter();
        new SpriteBehaviour(wall, -9.5f, -.5f, sprite);
    }
```

It's time for some polish. This poor game is lacking some pop. We have this ground tile we can add. And some nasty cobwebs too. That should just be another SpriteBehavior with some positioning. Uh oh! We can’t see the player or enemy. We’ve neglected the render order of our game objects. So far, the game is rendering everything in the order that it was created in code.

```java
    @Override
    public void create() {
        ...

        GameObject ground = new GameObject(unBox);
        sprite = new Sprite(textureAtlas.findRegion("ground"));
        sprite.setSize(19, 18);
        sprite.setOriginCenter();
        new SpriteBehaviour(ground, -9.5f, -9, sprite);

        GameObject web = new GameObject(unBox);
        sprite = new Sprite(textureAtlas.findRegion("cobweb"));
        sprite.setSize(1, 1);
        sprite.setOriginCenter();
        new SpriteBehaviour(web, 8.5f, 8, sprite);

        web = new GameObject(unBox);
        sprite = new Sprite(textureAtlas.findRegion("cobweb"));
        sprite.setSize(1, 1);
        sprite.setOriginCenter();
        sprite.flip(true, false);
        new SpriteBehaviour(web, -9.5f, 8, sprite);

        web = new GameObject(unBox);
        sprite = new Sprite(textureAtlas.findRegion("cobweb"));
        sprite.setSize(1, 1);
        sprite.setOriginCenter();
        sprite.flip(false, true);
        new SpriteBehaviour(web, 8.5f, -9, sprite);

        web = new GameObject(unBox);
        sprite = new Sprite(textureAtlas.findRegion("cobweb"));
        sprite.setSize(1, 1);
        sprite.setOriginCenter();
        sprite.flip(true, true);
        new SpriteBehaviour(web, -9.5f, -9, sprite);
    }
```

That might be okay for cheap games with little complexity, but we should use setRenderOrder on the sprite behavior to specify what is supposed to be in the background and the foreground. You can type any float in here with smaller numbers rendered in the back and larger toward the front. To keep your render orders organized and easy to modify, create some final static variables in your game class to reference throughout your code. This way you can group your backgrounds, foregrounds, and everything in between. Objects sharing the same render order will be sorted by creation order, but that usually is not a problem.

```java
public SpriteImprovedBehaviour(GameObject gameObject, float offsetX, float offsetY, Sprite sprite, float renderOrder) {
        super(gameObject);
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.sprite = sprite;
        setRenderOrder(renderOrder);
    }
```

```java
    public static final float RO_BACKGROUND = -10;
    public static final float RO_CHARACTERS = 0;
    public static final float RO_FOREGROUND = 10;

    ...
    
    @Override
    public void create() {
        ...

        Sprite sprite = new Sprite(textureAtlas.findRegion("tractor"));
        sprite.setSize(1, 1);
        sprite.setOriginCenter();
        new SpriteImprovedBehaviour(rightGo, -.5f, -.5f, sprite, RO_CHARACTERS);

        sprite = new Sprite(textureAtlas.findRegion("spider"));
        sprite.setSize(1, 1);
        sprite.setOriginCenter();
        new SpriteImprovedBehaviour(leftGo, -.5f, -.5f, sprite, RO_CHARACTERS);

        GameObject wall = new GameObject(unBox);
        bodyDef = new BodyDef();
        bodyDef.type = BodyType.StaticBody;
        bodyDef.position.set(10, 0);
        new Box2dBehaviour(bodyDef, wall);
        new CreateBox2dBoxFixtureBehaviour(.5f, 10, wall);
        sprite = new Sprite(textureAtlas.findRegion("wall-vertical"));
        sprite.setSize(1, 20);
        sprite.setOriginCenter();
        new SpriteImprovedBehaviour(wall, -.5f, -10, sprite, RO_BACKGROUND);

        wall = new GameObject(unBox);
        bodyDef = new BodyDef();
        bodyDef.type = BodyType.StaticBody;
        bodyDef.position.set(-10, 0);
        new Box2dBehaviour(bodyDef, wall);
        new CreateBox2dBoxFixtureBehaviour(.5f, 10, wall);
        sprite = new Sprite(textureAtlas.findRegion("wall-vertical"));
        sprite.setSize(1, 20);
        sprite.setOriginCenter();
        new SpriteImprovedBehaviour(wall, -.5f, -10, sprite, RO_BACKGROUND);

        wall = new GameObject(unBox);
        bodyDef = new BodyDef();
        bodyDef.type = BodyType.StaticBody;
        bodyDef.position.set(0, 9.5f);
        new Box2dBehaviour(bodyDef, wall);
        new CreateBox2dBoxFixtureBehaviour(9.5f, .5f, wall);
        sprite = new Sprite(textureAtlas.findRegion("wall-horizontal"));
        sprite.setSize(19, 1);
        sprite.setOriginCenter();
        new SpriteImprovedBehaviour(wall, -9.5f, -.5f, sprite, RO_BACKGROUND);

        wall = new GameObject(unBox);
        bodyDef = new BodyDef();
        bodyDef.type = BodyType.StaticBody;
        bodyDef.position.set(0, -9.5f);
        new Box2dBehaviour(bodyDef, wall);
        new CreateBox2dBoxFixtureBehaviour(9.5f, .5f, wall);
        sprite = new Sprite(textureAtlas.findRegion("wall-horizontal"));
        sprite.setSize(19, 1);
        sprite.setOriginCenter();
        new SpriteImprovedBehaviour(wall, -9.5f, -.5f, sprite, RO_BACKGROUND);

        GameObject ground = new GameObject(unBox);
        sprite = new Sprite(textureAtlas.findRegion("ground"));
        sprite.setSize(19, 18);
        sprite.setOriginCenter();
        new SpriteImprovedBehaviour(ground, -9.5f, -9, sprite, RO_BACKGROUND);

        GameObject web = new GameObject(unBox);
        sprite = new Sprite(textureAtlas.findRegion("cobweb"));
        sprite.setSize(1, 1);
        sprite.setOriginCenter();
        new SpriteImprovedBehaviour(web, 8.5f, 8, sprite, RO_FOREGROUND);

        web = new GameObject(unBox);
        sprite = new Sprite(textureAtlas.findRegion("cobweb"));
        sprite.setSize(1, 1);
        sprite.setOriginCenter();
        sprite.flip(true, false);
        new SpriteImprovedBehaviour(web, -9.5f, 8, sprite, RO_FOREGROUND);

        web = new GameObject(unBox);
        sprite = new Sprite(textureAtlas.findRegion("cobweb"));
        sprite.setSize(1, 1);
        sprite.setOriginCenter();
        sprite.flip(false, true);
        new SpriteImprovedBehaviour(web, 8.5f, -9, sprite, RO_FOREGROUND);

        web = new GameObject(unBox);
        sprite = new Sprite(textureAtlas.findRegion("cobweb"));
        sprite.setSize(1, 1);
        sprite.setOriginCenter();
        sprite.flip(true, true);
        new SpriteImprovedBehaviour(web, -9.5f, -9, sprite, RO_FOREGROUND);
    }
```

There is also a [setExecutionOrder](https://github.com/lyze237/gdx-UnBox2D/blob/main/src/main/java/dev/lyze/gdxUnBox2d/Behaviour.java#L156) if you want some behavior code to be executed in a different order. This is the order that the behaviors are executed within a single game object. Do try to avoid depending on this too much because it makes for nasty spaghetti code with hard to track down bugs.

Now we have something almost reminiscent of an actual game. These behaviors are just puzzle pieces that we put together to make a final project. If you bring this to its logical conclusion, you might end up with a [game like this](https://raeleus.itch.io/unboxed-in-space). The the example code can be found [here](https://github.com/raeleus/unboxed-in-space).

## More About UnBox2D

Okay, you’ve conducted your first experiment in UnBox2D. Let’s briefly talk about the available methods in Behaviour and how the Execution Order works. [This is a list of each method](https://github.com/lyze237/gdx-UnBox2D/wiki/Behaviours) that you can override. Sometimes you need to set some values immediately when you add a behavior to an object. Use the method “awake” for that. However, you’ll typically want to do things when the object is actually added to the world. That would be the “start” method.

We’ve talked about “fixedUpdate” and “update” for code that happens every physics step and frame respectively, but “lateUpdate” is convenient sometimes for things like cameras that have to be positioned after all the bodies are moved and interacted with.

The collision methods of Box2dBehaviourAdapter are associated with their corresponding events in Box2D. You can always refer to the provided contact for access to the collision details. And the same Box2D rules apply, like disabling a contact is only permitted in the pre-solve method.

As demonstrated in the example, "render" is the appropriate method to place any draw calls like you would need for a Sprite or texture.

You can disable game objects or their behaviors if you don’t want them to execute. This is helpful with keeping your frame rate high by disabling enemies that are not in view, for example. The "onDisable" and "onEnable" methods are called when these events occur.

"onDestroy" is called when the object is destroyed. I usually use this as a cue to create death particle effects, increase the score, or any other related task.

[This diagram](https://github.com/lyze237/gdx-UnBox2D/wiki/executionOrder) shows the execution order of all these methods and how they flow together. That might be important to review if your physics simulation isn’t syncing up with your drawing, for example.

## Conclusion

UnBox2D addresses several shortcomings in coding with plain libGDX. I hope this video has shown you how it can help organize your code and reduce some bloat. We only went over the basic functionality with Box2D, but you can implement your own kinds of worlds if you use a different physics engine. Yes, there’s more to learn about this lib, but If you’re unsure of what something does, just check the javadocs. It’s documented rather well. Anyway, thanks to Lyze for making yet another excellent lib and thank you for watching!
