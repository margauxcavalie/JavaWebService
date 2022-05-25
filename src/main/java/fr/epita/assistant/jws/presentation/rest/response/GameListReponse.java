package fr.epita.assistant.jws.presentation.rest.response;
import fr.epita.assistant.jws.domain.entity.GameEntity;
import fr.epita.assistant.jws.domain.entity.GameState;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

@With
@NoArgsConstructor
@AllArgsConstructor
public class GameListReponse
{
    public Long id;
    public int players;
    public GameState state;

    public static GameListReponse EntityToDTO (GameEntity gameEntity)
    {
        GameListReponse gameListReponse = new GameListReponse()
                .withId(gameEntity.id)
                .withPlayers(gameEntity.players.size())
                .withState(gameEntity.state);

        return  gameListReponse;
    }
}
