package fr.epita.assistant.jws.domain.service;
import fr.epita.assistant.jws.data.model.GameModel;
import fr.epita.assistant.jws.data.model.PlayerModel;
import fr.epita.assistant.jws.data.repository.GameRepository;
import fr.epita.assistant.jws.data.repository.PlayerRepository;
import fr.epita.assistant.jws.domain.entity.GameEntity;
import fr.epita.assistant.jws.domain.entity.PlayerEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.awt.*;

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
        if (position == 3)
            coords.y = 13;
        if (position == 4) {
            coords.x = 15;
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
        gameRepository.persist(gameModel);
        return GameEntity.ModelToEntity(gameModel);
    }
}
