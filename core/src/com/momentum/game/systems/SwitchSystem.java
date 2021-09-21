package com.momentum.game.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.momentum.game.components.Door;
import com.momentum.game.components.Switch;

public class SwitchSystem extends IteratingSystem {

    private Iterable<Entity> doors;

    public SwitchSystem() {
        super(Family.all(Switch.class).get());
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        doors = engine.getEntitiesFor(Family.all(Door.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Switch aSwitch = Switch.mapper.get(entity);
        if (aSwitch.justPressed) {
            aSwitch.pressed = true;
            aSwitch.justPressed = false;
            String id = aSwitch.name.split("_")[1];
            for (Entity doorEntity : doors) {
                Door door = Door.mapper.get(doorEntity);
                if (door.name.equals("door_" + id)) {
                    getEngine().removeEntity(doorEntity);
                }
            }
        }
    }


}
