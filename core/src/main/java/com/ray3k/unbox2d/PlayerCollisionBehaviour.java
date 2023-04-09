package com.ray3k.unbox2d;

import com.badlogic.gdx.physics.box2d.Contact;
import dev.lyze.gdxUnBox2d.Behaviour;
import dev.lyze.gdxUnBox2d.GameObject;
import dev.lyze.gdxUnBox2d.behaviours.BehaviourAdapter;

public class PlayerCollisionBehaviour extends BehaviourAdapter {

    public PlayerCollisionBehaviour(GameObject gameObject) {
        super(gameObject);
    }

    @Override
    public void onCollisionEnter(Behaviour other, Contact contact) {
        getGameObject().destroy();
    }
}
