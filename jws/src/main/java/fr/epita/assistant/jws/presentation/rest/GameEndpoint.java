package fr.epita.assistant.jws.presentation.rest;
import fr.epita.assistant.jws.data.model.GameModel;
import fr.epita.assistant.jws.data.model.MapModel;
import fr.epita.assistant.jws.domain.entity.GameEntity;
import fr.epita.assistant.jws.domain.entity.GameState;
import fr.epita.assistant.jws.domain.entity.PlayerEntity;
import fr.epita.assistant.jws.domain.service.GameService;
import fr.epita.assistant.jws.domain.service.PlayerService;
import fr.epita.assistant.jws.domain.service.exceptions.DifferentGamesException;
import fr.epita.assistant.jws.domain.service.exceptions.NullPlayerException;
import fr.epita.assistant.jws.domain.service.exceptions.UnallowedMoveException;
import fr.epita.assistant.jws.domain.service.exceptions.UnknownGameException;
import fr.epita.assistant.jws.presentation.rest.request.CreateGameRequest;
import fr.epita.assistant.jws.presentation.rest.request.JoinGameRequest;
import fr.epita.assistant.jws.presentation.rest.request.MovePlayerRequest;
import fr.epita.assistant.jws.presentation.rest.request.PutBombRequest;
import fr.epita.assistant.jws.presentation.rest.response.GameDetailResponse;
import fr.epita.assistant.jws.presentation.rest.response.GameListReponse;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped @Path("/games")
public class GameEndpoint
{
    @Inject GameService gameService;
    @Inject PlayerService playerService;

    @ConfigProperty(name="JWS_TICK_DURATION") int JWS_TICK_DURATION;

    @GET
    public List<GameListReponse> getAllGames()
    {
        List<GameEntity> gameEntities = gameService.getAllGames();
        List<GameListReponse> gameListReponses = new ArrayList<>();
        gameEntities.forEach(gameEntity -> {
            gameListReponses.add(GameListReponse.EntityToDTO(gameEntity));
        });
        return gameListReponses;
    }

    @POST
    public Response createGame(CreateGameRequest createGameRequest)
    {
        if (createGameRequest == null || createGameRequest.name == null)
            return Response.status(Response.Status.BAD_REQUEST).build();

        Long id = gameService.createGame(createGameRequest.name);
        GameEntity gameEntity = null;
        try {
            gameEntity = gameService.getGameWithId(id);
        } catch (UnknownGameException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        GameDetailResponse gameDetailResponse = GameDetailResponse.EntityToDTO(gameEntity);
        return Response.status(Response.Status.OK).entity(gameDetailResponse).build();
    }

    @GET @Path("/{gameId}")
    public Response getGameInfo(@PathParam("gameId") Long id)
    {
        GameEntity gameEntity = null;
        try
        {
            gameEntity = gameService.getGameWithId(id);
        }
        catch (UnknownGameException e)
        {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        GameDetailResponse gameDetailResponse = GameDetailResponse.EntityToDTO(gameEntity);
        return Response.status(Response.Status.OK).entity(gameDetailResponse).build();
    }

    @POST @Path("/{gameId}")
    public Response joinGame(@PathParam("gameId")Long id, JoinGameRequest joinGameRequest)
    {
        if (joinGameRequest == null || joinGameRequest.name == null)
            return Response.status(Response.Status.BAD_REQUEST).build();

        GameEntity gameEntity = null;
        try {
            gameEntity = gameService.getGameWithId(id);
        } catch (UnknownGameException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (gameEntity.state.equals(GameState.FINISHED) ||
                gameEntity.state.equals(GameState.RUNNING) || gameEntity.players.size() >= 4)
            return Response.status(Response.Status.BAD_REQUEST).build();

        gameEntity = playerService.addPlayer(joinGameRequest.name, gameEntity);

        GameDetailResponse gameDetailResponse = GameDetailResponse.EntityToDTO(gameEntity);
        return Response.status(Response.Status.OK).entity(gameDetailResponse).build();
    }

    @PATCH @Path("/{gameId}/start")
    public Response startGame(@PathParam("gameId")Long id)
    {
        GameEntity gameEntity = null;
        try {
            gameEntity = gameService.getGameWithId(id);
        } catch (UnknownGameException e) {
            return Response.status(Response.Status.NOT_FOUND).build(); // ERROR 404
        }

        if (gameEntity.state.equals(GameState.FINISHED))
            return Response.status(Response.Status.NOT_FOUND).build(); // ERROR 404

        gameEntity = gameService.startGame(id);

        GameDetailResponse gameDetailResponse = GameDetailResponse.EntityToDTO(gameEntity);
        return Response.status(Response.Status.OK).entity(gameDetailResponse).build();
    }

    @POST @Path("/{gameId}/players/{playerId}/bomb")
    public Response putBomb(@PathParam("gameId") Long gameId,
                            @PathParam("playerId") Long playerId, PutBombRequest putBombRequest)
    {
        PlayerEntity playerEntity = null;
        try {
            playerEntity = playerService.getPlayerWithId(playerId);
        } catch (NullPlayerException e) {
            return Response.status(Response.Status.NOT_FOUND).build(); //ERROR 404
        }

        if (putBombRequest == null || playerEntity.name == null)
            return Response.status(Response.Status.BAD_REQUEST).build(); // ERROR 400

        GameEntity gameEntity = null;
        try
        {
            gameEntity = gameService.getGameWithId(gameId);
        }
        catch (UnknownGameException e)
        {
            return Response.status(Response.Status.NOT_FOUND).build(); // ERROR 404
        }

        if (gameEntity.state.equals(GameState.FINISHED) ||
                gameEntity.players.size() > 4 ||
                    playerEntity.coord.x != putBombRequest.posX ||
                        playerEntity.coord.y != putBombRequest.posY)
            return Response.status(Response.Status.BAD_REQUEST).build(); // ERROR 400

        if (playerEntity.lastBomb != null)
            if (LocalDateTime.now().minusSeconds(JWS_TICK_DURATION / 1000L).isBefore(playerEntity.lastBomb))
                return Response.status(Response.Status.TOO_MANY_REQUESTS).build(); // ERROR 429

        try {
            gameEntity = gameService.putBomb(gameId, playerId);
        } catch (DifferentGamesException e) {
            return Response.status(Response.Status.BAD_REQUEST).build(); // ERROR 400
        }

        GameDetailResponse gameDetailResponse = GameDetailResponse.EntityToDTO(gameEntity);
        return Response.status(Response.Status.OK).entity(gameDetailResponse).build();
    }

    @POST @Path("/{gameId}/players/{playerId}/move")
    public Response movePlayer(@PathParam("gameId") Long gameId,
                               @PathParam("playerId") Long playerId, MovePlayerRequest movePlayerRequest)
    {
        if (movePlayerRequest == null || movePlayerRequest.posX < 0 || movePlayerRequest.posY < 0)
            return Response.status(Response.Status.BAD_REQUEST).build(); // ERROR 400


        PlayerEntity playerEntity = null;
        try {
            playerEntity = playerService.getPlayerWithId(playerId);
        } catch (NullPlayerException e) {
            return Response.status(Response.Status.NOT_FOUND).build(); //ERROR 404
        }
        GameEntity gameEntity = null;
        try {
            gameEntity = gameService.getGameWithId(gameId);
        } catch (UnknownGameException e) {
            return Response.status(Response.Status.NOT_FOUND).build(); // ERROR 404
        }

        // Check for errors
        if (!gameEntity.state.equals(GameState.RUNNING) || playerEntity.lives <= 0)
            return Response.status(Response.Status.BAD_REQUEST).build(); // ERROR 400

        if (playerEntity.lastMovement != null)
            if (LocalDateTime.now().minusSeconds(JWS_TICK_DURATION / 1000L).isBefore(playerEntity.lastMovement))
                return Response.status(Response.Status.TOO_MANY_REQUESTS).build(); // ERROR 429

        try {
            gameEntity = playerService.movePlayer(playerId, gameId, movePlayerRequest);
        } catch (UnallowedMoveException | DifferentGamesException e) {
            return Response.status(Response.Status.BAD_REQUEST).build(); // ERROR 400
        }
        GameDetailResponse gameDetailResponse = GameDetailResponse.EntityToDTO(gameEntity);
        return Response.status(Response.Status.OK).entity(gameDetailResponse).build();
    }
}
