package com.ray3k.unbox2d;

import com.badlogic.gdx.physics.box2d.Contact;
import dev.lyze.gdxUnBox2d.Behaviour;
import dev.lyze.gdxUnBox2d.GameObject;
import dev.lyze.gdxUnBox2d.behaviours.box2d.Box2dBehaviourAdapter;

public class PlayerCollisionImprovedBehaviour extends Box2dBehaviourAdapter {

    public PlayerCollisionImprovedBehaviour(GameObject gameObject) {
        super(gameObject);
    }

    @Override
    public void onCollisionEnter(Behaviour other, Contact contact) {
        if (other.getGameObject().getBehaviour(TeamEnemyBehaviour.class) != null) getGameObject().destroy();
    }
}
