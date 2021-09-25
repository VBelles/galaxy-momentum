package com.momentum.game.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.momentum.game.StageLoader;
import com.momentum.game.components.Goal;
import com.momentum.game.components.Stage;
import com.momentum.game.components.Tag;
import com.momentum.game.resources.Resources;

public class StageSystem extends IteratingSystem {

    private final Resources resources;
    private final Preferences preferences;

    private Iterable<Entity> tagEntities;
    private ImmutableArray<Entity> goalEntities;

    public StageSystem(Resources resources, Preferences preferences) {
        super(Family.all(Stage.class).get());
        this.resources = resources;
        this.preferences = preferences;
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
        if (stage.level == -1 || goalsAchieved
                || (Gdx.input.isKeyJustPressed(Input.Keys.N) && stageUnlocked(stage.level + 1))) {
            // Load next stage
            if (stage.level != -1) resources.goalSound.play();
            stage.level++;
            loadStage(stage.level - 1, stage.level);
        } else if (stage.failure || Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            // Reset stage
            loadStage(stage.level, stage.level);
            stage.failure = false;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.P) && stage.level > 0) {
            // Load previous stage
            resources.goalSound.play();
            stage.level--;
            loadStage(stage.level + 1, stage.level);
        }

    }

    private boolean stageUnlocked(int level) {
        return level <= preferences.getInteger("level", -1);
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

        // Store the furthest stage reached
        int furthestLevel = preferences.getInteger("level", -1);
        if (level > furthestLevel) {
            preferences.putInteger("level", level);
        }
    }
}
