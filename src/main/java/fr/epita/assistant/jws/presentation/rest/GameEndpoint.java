package fr.epita.assistant.jws.presentation.rest;
import fr.epita.assistant.jws.domain.entity.GameEntity;
import fr.epita.assistant.jws.domain.entity.GameState;
import fr.epita.assistant.jws.domain.entity.PlayerEntity;
import fr.epita.assistant.jws.domain.service.GameService;
import fr.epita.assistant.jws.domain.service.PlayerService;
import fr.epita.assistant.jws.domain.service.exceptions.UnknownGameException;
import fr.epita.assistant.jws.presentation.rest.request.CreateGameRequest;
import fr.epita.assistant.jws.presentation.rest.request.JoinGameRequest;
import fr.epita.assistant.jws.presentation.rest.response.GameDetailResponse;
import fr.epita.assistant.jws.presentation.rest.response.GameListReponse;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped @Path("/games")
public class GameEndpoint
{
    @Inject GameService gameService;
    @Inject PlayerService playerService;

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
            gameEntity = gameService.GetGameWithId(id);
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
            gameEntity = gameService.GetGameWithId(id);
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
        try
        {
            gameEntity = gameService.GetGameWithId(id);
        }
        catch (UnknownGameException e)
        {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (gameEntity.state.equals(GameState.FINISHED) ||
                gameEntity.state.equals(GameState.RUNNING) ||
                    gameEntity.players.size() >= 4)
            return Response.status(Response.Status.BAD_REQUEST).build();

        gameEntity = playerService.addPlayer(joinGameRequest.name, gameEntity);

        GameDetailResponse gameDetailResponse = GameDetailResponse.EntityToDTO(gameEntity);
        return Response.status(Response.Status.OK).entity(gameDetailResponse).build();
    }

    @PATCH @Path("/{gameId}/start")
    public Response startGame(@PathParam("gameId")Long id)
    {
        GameEntity gameEntity = null;
        try
        {
            gameEntity = gameService.GetGameWithId(id);
        }
        catch (UnknownGameException e)
        {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        gameEntity = gameService.startGame(id);

        GameDetailResponse gameDetailResponse = GameDetailResponse.EntityToDTO(gameEntity);
        return Response.status(Response.Status.OK).entity(gameDetailResponse).build();
    }

}
