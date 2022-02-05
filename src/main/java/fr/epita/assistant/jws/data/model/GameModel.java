package fr.epita.assistant.jws.data.model;
import fr.epita.assistant.jws.domain.entity.GameEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity @Table(name = "game")
@AllArgsConstructor @NoArgsConstructor @With
public class GameModel
{
    public @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    public @Column(name = "starttime") LocalDateTime startTime;
    public String state;
    @OneToMany(cascade = CascadeType.ALL) public List<PlayerModel> players = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL) public List<MapModel> map = new ArrayList<>();

    public void addPlayer(PlayerModel playerModel)
    {
        this.players.add(playerModel);
        playerModel.game = this;
    }

    public void addMap(MapModel mapModel)
    {
        this.map.add(mapModel);
        mapModel.game = this;
    }

    public static GameModel EntityToModel(GameEntity gameEntity)
    {
        GameModel gameModel = new GameModel()
                .withId(gameEntity.id)
                .withStartTime(gameEntity.startTime)
                .withState(gameEntity.state.toString());

        gameEntity.players.forEach(playerEntity -> {
            gameModel.addPlayer(PlayerModel.EntityToModel(playerEntity));
        });

        for (int index = 0; index < gameEntity.map.map.size(); index++)
        {
            MapModel mapModel = new MapModel()
                    .withGame(null)
                    .withMap(gameEntity.map.map.get(index))
                    .withId(gameEntity.map.ids.get(index));

            gameModel.addMap(mapModel);
        }

        return gameModel;
    }
}
