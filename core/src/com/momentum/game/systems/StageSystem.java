package com.momentum.game.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.momentum.game.StageLoader;
import com.momentum.game.components.Goal;
import com.momentum.game.components.Stage;
import com.momentum.game.components.Tag;
import com.momentum.game.resources.Resources;

public class StageSystem extends IteratingSystem {

    private final Resources resources;

    private Iterable<Entity> tagEntities;
    private ImmutableArray<Entity> goalEntities;

    public StageSystem(Resources resources) {
        super(Family.all(Stage.class).get());
        this.resources = resources;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        tagEntities = engine.getEntitiesFor(Family.all(Tag.class).get());
        goalEntities = engine.getEntitiesFor(Family.all(Goal.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Stage stage = Stage.mapper.get(entity);
        boolean goalsAchieved = areGoalsAchieved();
        if ((stage.level == -1 || goalsAchieved || stage.next) && resources.stages.size() > stage.level + 1) {
            // Load next stage
            if (!stage.next && stage.level != -1) {
                resources.goalSound.play();
            }
            stage.level++;
            stage.next = false;
            loadStage(stage.level - 1, stage.level);
        } else if (stage.failure) {
            // Reset stage
            loadStage(stage.level, stage.level);
        } else if (stage.previous && stage.level > 0) {
            // Load previous stage
            stage.level--;
            loadStage(stage.level + 1, stage.level);
        }

        stage.failure = false;
        stage.next = false;
        stage.previous = false;

    }

    private boolean areGoalsAchieved() {
        if (goalEntities.size() == 0) return false;
        boolean goalsAchieved = true;
        for (Entity goalEntity : goalEntities) {
            Goal goal = Goal.mapper.get(goalEntity);
            goalsAchieved = goalsAchieved && goal.achieved;
        }
        return goalsAchieved;
    }

    private void loadStage(int previousLevel, int level) {
        // Clear entities from previous level
        for (Entity tagEntity : tagEntities) {
            Tag tag = Tag.mapper.get(tagEntity);
            if (tag.hasTag(previousLevel)) {
                getEngine().removeEntity(tagEntity);
            }
        }
        // Load new level
        StageLoader.load(getEngine(), resources, level);
    }
}
