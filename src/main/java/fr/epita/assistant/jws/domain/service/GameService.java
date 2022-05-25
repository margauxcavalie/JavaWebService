package fr.epita.assistant.jws.domain.service;
import fr.epita.assistant.jws.data.model.GameModel;
import fr.epita.assistant.jws.data.model.MapModel;
import fr.epita.assistant.jws.data.model.PlayerModel;
import fr.epita.assistant.jws.data.repository.GameRepository;
import fr.epita.assistant.jws.data.repository.MapRepository;
import fr.epita.assistant.jws.data.repository.PlayerRepository;
import fr.epita.assistant.jws.domain.entity.GameEntity;
import fr.epita.assistant.jws.domain.entity.GameState;
import fr.epita.assistant.jws.domain.entity.MapEntity;
import fr.epita.assistant.jws.domain.entity.PlayerEntity;
import fr.epita.assistant.jws.domain.service.exceptions.DifferentGamesException;
import fr.epita.assistant.jws.domain.service.exceptions.UnknownGameException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@ApplicationScoped
public class GameService
{
    @Inject GameRepository gameRepository;
    @Inject MapRepository mapRepository;
    @Inject PlayerRepository playerRepository;

    @ConfigProperty(name="JWS_MAP_PATH") String JWS_MAP_PATH;

    @Transactional
    public Long createGame(String player_name)
    {
        PlayerEntity playerEntity = new PlayerEntity()
                .withName(player_name)
                .withLives(3)
                .withCoord(new Point(1, 1))
                .withPosition(1);

        MapEntity mapEntity = MapEntity.MapFromPath(JWS_MAP_PATH);
        GameEntity gameEntity = new GameEntity()
                .withMap(mapEntity)
                .withStartTime(LocalDateTime.now())
                .withState(GameState.STARTING)
                .withPlayers(List.of(playerEntity));

        GameModel gameModel = GameModel.EntityToModel(gameEntity);
        gameRepository.persist(gameModel);
        gameRepository.flush();
        return gameModel.id;
    }

    public List<GameEntity> getAllGames()
    {
        List<GameModel> gameModels = gameRepository.findAll().stream().toList();
        List<GameEntity> gameEntities = new ArrayList<>();
        gameModels.forEach(gameModel -> {
            gameEntities.add(GameEntity.ModelToEntity(gameModel));
        });

        return gameEntities;
    }

    public GameEntity getGameWithId(Long id) throws UnknownGameException
    {
        GameModel gameModel = gameRepository.findById(id);
        if (gameModel == null)
            throw new UnknownGameException();
        return GameEntity.ModelToEntity(gameModel);
    }

    @Transactional
    public GameEntity startGame(Long id)
    {
        GameModel gameModel = gameRepository.findById(id);
        if (gameModel.players.size() > 1)
            gameModel.state = "RUNNING";
        else
            gameModel.state = "FINISHED";

        return GameEntity.ModelToEntity(gameModel);
    }

    @Transactional
    public GameEntity putBomb(Long gameId, Long playerId) throws DifferentGamesException
    {
        GameModel gameModel = gameRepository.findById(gameId);
        PlayerModel playerModel = playerRepository.findById(playerId);

        // Sort
        Stream<MapModel> mapsSorted = gameModel.map.stream().sorted(Comparator.comparing(MapModel::getId));
        gameModel.map = mapsSorted.toList();

        // Replace map
        String oldMap = MapModel.decodeMapRLE(gameModel.map.get(playerModel.posY).map);
        String newMap = oldMap.substring(0, playerModel.posX) + 'B' + oldMap.substring(playerModel.posX + 1);
        gameModel.map.get(playerModel.posY).map = MapModel.encodeMapRLE(newMap);

        mapsSorted = gameModel.map.stream().sorted(Comparator.comparing(MapModel::getId));
        gameModel.map = mapsSorted.toList();

        // Change player's lastBomb
        playerModel.lastBomb = LocalDateTime.now();

        return GameEntity.ModelToEntity(gameModel);
    }
}
