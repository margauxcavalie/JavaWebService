package fr.epita.assistant.jws.presentation.rest.response;
import fr.epita.assistant.jws.data.repository.PlayerRepository;
import fr.epita.assistant.jws.domain.entity.PlayerEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

@With
@NoArgsConstructor
@AllArgsConstructor
public class PlayerResponse
{
    public Long id;
    public String name;
    public int lives;
    public int posX;
    public int posY;

    public static PlayerResponse EntityToDTO(PlayerEntity playerEntity)
    {
        PlayerResponse playerResponse = new PlayerResponse()
                .withId(playerEntity.id)
                .withName(playerEntity.name)
                .withLives(playerEntity.lives)
                .withPosX(playerEntity.coord.x)
                .withPosY(playerEntity.coord.y);

        return playerResponse;
    }
}
