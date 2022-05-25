package fr.epita.assistant.jws.presentation.rest.response;
import fr.epita.assistant.jws.domain.entity.GameEntity;
import fr.epita.assistant.jws.domain.entity.GameState;
import fr.epita.assistant.jws.domain.entity.PlayerEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@With
@NoArgsConstructor
@AllArgsConstructor
public class GameDetailResponse
{
    public LocalDateTime startTime;
    public GameState state;
    public List<PlayerResponse> players;
    public List<String> map;
    public Long id;

    public static GameDetailResponse EntityToDTO(GameEntity gameEntity)
    {
        List<PlayerResponse> playerResponses = new ArrayList<>();
        gameEntity.players.forEach(playerEntity -> {
            playerResponses.add(PlayerResponse.EntityToDTO(playerEntity));
        });

        GameDetailResponse gameDetailResponse = new GameDetailResponse()
               .withStartTime(gameEntity.startTime)
               .withState(gameEntity.state)
               .withPlayers(playerResponses)
               .withMap(gameEntity.map.map)
               .withId(gameEntity.id);

       return gameDetailResponse;
    }
}
