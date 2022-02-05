package fr.epita.assistant.jws.data.model;
import fr.epita.assistant.jws.domain.entity.PlayerEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "player")
@AllArgsConstructor @NoArgsConstructor @With
public class PlayerModel
{
    public @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    public @Column(name = "lastbomb") LocalDateTime lastBomb;
    public @Column(name = "lastmovement") LocalDateTime lastMovement;
    public int lives;
    public String name;
    public @Column(name = "posx") int posX;
    public @Column(name = "posy") int posY;
    public int position;
    @ManyToOne public GameModel game;

    public static PlayerModel EntityToModel(PlayerEntity playerEntity)
    {
        PlayerModel playerModel = new PlayerModel()
                .withId(playerEntity.id)
                .withLastBomb(playerEntity.lastBomb)
                .withLastMovement(playerEntity.lastMovement)
                .withLives(playerEntity.lives)
                .withName(playerEntity.name)
                .withPosX(playerEntity.coord.x)
                .withPosY(playerEntity.coord.y)
                .withPosition(playerEntity.position)
                .withGame(null);

        return playerModel;
    }
}
