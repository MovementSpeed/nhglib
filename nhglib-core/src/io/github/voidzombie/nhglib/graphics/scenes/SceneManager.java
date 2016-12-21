package io.github.voidzombie.nhglib.graphics.scenes;

import com.artemis.ComponentMapper;
import com.badlogic.gdx.graphics.g3d.Model;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.assets.Asset;
import io.github.voidzombie.nhglib.data.models.components.GraphicsComponentJson;
import io.github.voidzombie.nhglib.data.models.components.MessageComponentJson;
import io.github.voidzombie.nhglib.graphics.representations.ModelRepresentation;
import io.github.voidzombie.nhglib.runtime.ecs.components.graphics.GraphicsComponent;
import io.github.voidzombie.nhglib.runtime.messaging.Message;
import io.github.voidzombie.nhglib.utils.scenes.SceneUtils;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class SceneManager {
    private Scene currentScene;
    private ComponentMapper<GraphicsComponent> graphicsMapper;

    public SceneManager() {
        SceneUtils.getInstance().addComponentJsonMapping("graphics", GraphicsComponentJson.class);
        SceneUtils.getInstance().addComponentJsonMapping("message", MessageComponentJson.class);

        SceneUtils.getInstance().addAssetClassMapping("model", Model.class);
    }

    public void loadScene(Scene scene) {
        currentScene = scene;

        if (graphicsMapper == null) {
            graphicsMapper = NHG.entitySystem.getMapper(GraphicsComponent.class);
        }

        Observable.fromIterable(scene.sceneGraph.getEntities())
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return graphicsMapper.has(integer);
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        GraphicsComponent graphicsComponent = graphicsMapper.get(integer);

                        if (graphicsComponent.state == GraphicsComponent.State.NOT_INITIALIZED) {
                            loadGraphicsAsset(graphicsComponent);
                        }
                    }
                });
    }

    public void refresh() {
        loadScene(currentScene);
    }

    private void loadGraphicsAsset(final GraphicsComponent graphicsComponent) {
        graphicsComponent.state = GraphicsComponent.State.LOADING;

        NHG.messaging.get(NHG.strings.events.assetLoaded)
                .filter(new Predicate<Message>() {
                    @Override
                    public boolean test(Message message) throws Exception {
                        Asset asset = (Asset) message.data.get(NHG.strings.defaults.assetKey);
                        return graphicsComponent.asset.is(asset.alias);
                    }
                })
                .subscribe(new Consumer<Message>() {
                    @Override
                    public void accept(Message message) throws Exception {
                        Asset asset = (Asset) message.data.get(NHG.strings.defaults.assetKey);
                        createRepresentation(graphicsComponent, asset);
                    }
                });

        NHG.assets.queueAsset(graphicsComponent.asset);
    }

    private void createRepresentation(GraphicsComponent graphicsComponent, Asset asset) {
        if (asset.isType(Model.class)) {
            Model model = NHG.assets.get(asset);
            ModelRepresentation representation = new ModelRepresentation(model);
            graphicsComponent.setRepresentation(representation);
        }
    }
}
