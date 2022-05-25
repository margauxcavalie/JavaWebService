package fr.epita.assistant.jws.domain.entity;
import fr.epita.assistant.jws.data.model.PlayerModel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.awt.*;
import java.time.LocalDateTime;

@With @NoArgsConstructor @AllArgsConstructor
public class PlayerEntity
{
    public Long id;
    public LocalDateTime lastBomb;
    public LocalDateTime lastMovement;
    public int lives;
    public String name;
    public Point coord;
    public int position;

    public static PlayerEntity ModelToEntity(PlayerModel playerModel)
    {
        Point point = new Point(playerModel.posX, playerModel.posY);
        PlayerEntity playerEntity = new PlayerEntity()
                .withId(playerModel.id)
                .withLastBomb(playerModel.lastBomb)
                .withLastMovement(playerModel.lastMovement)
                .withLives(playerModel.lives)
                .withName(playerModel.name)
                .withCoord(point)
                .withPosition(playerModel.position);

        return playerEntity;
    }
}
