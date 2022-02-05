package fr.epita.assistant.jws.domain.service;
import fr.epita.assistant.jws.data.model.GameModel;
import fr.epita.assistant.jws.data.model.PlayerModel;
import fr.epita.assistant.jws.data.repository.GameRepository;
import fr.epita.assistant.jws.domain.entity.GameEntity;
import fr.epita.assistant.jws.domain.entity.GameState;
import fr.epita.assistant.jws.domain.entity.MapEntity;
import fr.epita.assistant.jws.domain.entity.PlayerEntity;
import fr.epita.assistant.jws.domain.service.exceptions.UnknownGameException;
import fr.epita.assistant.jws.presentation.rest.GameEndpoint;
import fr.epita.assistant.jws.presentation.rest.response.GameListReponse;
import org.eclipse.microprofile.config.inject.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.PUT;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class GameService
{
    @Inject GameRepository gameRepository;

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

    public GameEntity GetGameWithId(Long id) throws UnknownGameException
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
        gameModel.state = "RUNNING";
        gameRepository.persist(gameModel);
        return GameEntity.ModelToEntity(gameModel);
    }
}
