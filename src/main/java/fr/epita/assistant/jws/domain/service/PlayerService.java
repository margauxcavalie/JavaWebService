package fr.epita.assistant.jws.domain.service;
import fr.epita.assistant.jws.data.model.GameModel;
import fr.epita.assistant.jws.data.model.MapModel;
import fr.epita.assistant.jws.data.model.PlayerModel;
import fr.epita.assistant.jws.data.repository.GameRepository;
import fr.epita.assistant.jws.data.repository.MapRepository;
import fr.epita.assistant.jws.data.repository.PlayerRepository;
import fr.epita.assistant.jws.domain.entity.GameEntity;
import fr.epita.assistant.jws.domain.entity.GameState;
import fr.epita.assistant.jws.domain.entity.PlayerEntity;
import fr.epita.assistant.jws.domain.service.exceptions.DifferentGamesException;
import fr.epita.assistant.jws.domain.service.exceptions.NullPlayerException;
import fr.epita.assistant.jws.domain.service.exceptions.UnallowedMoveException;
import fr.epita.assistant.jws.domain.service.exceptions.UnknownGameException;
import fr.epita.assistant.jws.presentation.rest.request.MovePlayerRequest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.stream.Stream;

@ApplicationScoped
public class PlayerService
{
    @Inject PlayerRepository playerRepository;
    @Inject GameRepository gameRepository;


    @Transactional
    public GameEntity addPlayer(String player_name, GameEntity gameEntity)
    {
        int position = gameEntity.players.size() + 1;
        Point coords = new Point(1, 1);
        if (position == 2)
            coords.x = 15;
        else if (position == 3)
        {
            coords.x = 15;
            coords.y = 13;

        }
        else if (position == 4)
        {
            //coords.x = 15;
            coords.y = 13;
        }

        PlayerEntity playerEntity = new PlayerEntity()
                .withName(player_name)
                .withLives(3)
                .withCoord(coords)
                .withPosition(position);

        GameModel gameModel = gameRepository.findById(gameEntity.id);
        PlayerModel playerModel = PlayerModel.EntityToModel(playerEntity);

        playerModel.game = gameModel;
        gameModel.players.add(playerModel);
        playerRepository.persist(playerModel);
        return GameEntity.ModelToEntity(gameModel);
    }

    public PlayerEntity getPlayerWithId(Long id) throws NullPlayerException
    {
        PlayerModel playerModel = playerRepository.findById(id);
        if (playerModel == null)
            throw new NullPlayerException();
        return PlayerEntity.ModelToEntity(playerModel);
    }

    @Transactional
    public GameEntity movePlayer(Long playerId, Long gameId, MovePlayerRequest movePlayerRequest)
            throws UnallowedMoveException, DifferentGamesException
    {
        PlayerModel playerModel = playerRepository.findById(playerId);
        GameModel gameModel = gameRepository.findById(gameId);

        // Check state of the game
        if (gameModel.state.equals(GameState.FINISHED))
            throw new UnallowedMoveException();

        // Check Manhattan distance
        int manhattanDistance = Math.abs(movePlayerRequest.posX - playerModel.posX) +
                Math.abs(movePlayerRequest.posY - playerModel.posY);
        if (manhattanDistance > 1)
            throw new UnallowedMoveException();

        // Check which tile it is
        String decodedMap = MapModel.decodeMapRLE(gameModel.map.get(movePlayerRequest.posY).map);
        if (decodedMap.charAt(movePlayerRequest.posX) == 'W' || decodedMap.charAt(movePlayerRequest.posX) == 'B' ||
                decodedMap.charAt(movePlayerRequest.posX) == 'M')
            throw new UnallowedMoveException();

        // Change position and lastMovement
        playerModel.posX = movePlayerRequest.posX;
        playerModel.posY = movePlayerRequest.posY;
        playerModel.lastMovement = LocalDateTime.now();

        /*playersSorted = gameModel.players.stream().sorted(Comparator.comparing(PlayerModel::getId));
        gameModel.players = playersSorted.toList();*/

        return GameEntity.ModelToEntity(gameModel);
    }
}
