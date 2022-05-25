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

        /*for (int i = 0; i < playerEntities.size() - 1; i++)
        {
            if (playerEntities.get(i).id > playerEntities.get(i + 1).id)
            {
                int j = 0;
                while (playerEntities.get(j).id < playerEntities.get(i + 1).id)
                {
                    j++;
                }

                PlayerEntity playerTemp = playerEntities.get(j);
                playerEntities.remove(j);
                playerEntities.add(j, playerEntities.get(i +1));
                playerEntities.remove(i + 1);
                playerEntities.add(i + 1, playerTemp);
            }
        }*/

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
