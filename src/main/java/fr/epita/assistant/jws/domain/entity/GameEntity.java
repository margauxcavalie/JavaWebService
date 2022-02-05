package fr.epita.assistant.jws.domain.entity;
import fr.epita.assistant.jws.data.model.GameModel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import java.time.LocalDateTime;
import java.util.List;

@With @AllArgsConstructor @NoArgsConstructor
public class GameEntity
{
    public Long id;
    public LocalDateTime startTime;
    public GameState state;
    public List<PlayerEntity> players;
    public MapEntity map;

    public static GameEntity ModelToEntity(GameModel gameModel)
    {
        List<PlayerEntity> playerEntities = gameModel.players
                .stream().map(PlayerEntity::ModelToEntity).toList();
        MapEntity mapEntity = MapEntity.ModelToEntity(gameModel.map);
        GameEntity gameEntity = new GameEntity()
                .withId(gameModel.id)
                .withStartTime(gameModel.startTime)
                .withState(GameState.StringToGameState(gameModel.state))
                .withPlayers(playerEntities)
                .withMap(mapEntity);

        return gameEntity;
    }
}
